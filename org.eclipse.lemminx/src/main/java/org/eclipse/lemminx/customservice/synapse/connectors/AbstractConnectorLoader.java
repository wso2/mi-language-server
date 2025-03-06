/*
 * Copyright (c) 2024, WSO2 LLC. (http://www.wso2.com).
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.eclipse.lemminx.customservice.synapse.connectors;

import org.eclipse.lemminx.customservice.SynapseLanguageClientAPI;
import org.eclipse.lemminx.customservice.synapse.ConnectorStatusNotification;
import org.eclipse.lemminx.customservice.synapse.connectors.entity.Connector;
import org.eclipse.lemminx.customservice.synapse.utils.Constant;
import org.eclipse.lemminx.customservice.synapse.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Abstract class to load connectors.
 */
public abstract class AbstractConnectorLoader {

    private static final Logger log = Logger.getLogger(AbstractConnectorLoader.class.getName());
    private SynapseLanguageClientAPI languageClient;
    protected ConnectorHolder connectorHolder;
    private ConnectorReader connectorReader;
    protected List<String> connectorsZipFolderPath = new ArrayList<>();
    private File connectorExtractFolder;
    protected String projectUri;

    public AbstractConnectorLoader(SynapseLanguageClientAPI languageClient, ConnectorHolder connectorHolder) {

        this.languageClient = languageClient;
        this.connectorHolder = connectorHolder;
        this.connectorReader = new ConnectorReader();
    }

    public void init(String projectRoot) {

        setConnectorsZipFolderPath(projectRoot);
        setProjectUri(projectRoot);
        connectorExtractFolder = getConnectorExtractFolder();
    }

    protected abstract void setConnectorsZipFolderPath(String projectRoot);

    public void loadConnector() {

        if (canContinue(connectorExtractFolder)) {
            List<File> connectorZips = getConnectorZips();
            connectorHolder.setConnectorZips(Collections.unmodifiableList(connectorZips));
            cleanOldConnectors(connectorExtractFolder, connectorZips);
            copyToProjectIfNeeded(connectorZips);
            extractZips(connectorZips, connectorExtractFolder);
            readConnectors(connectorExtractFolder, projectUri);
        }
    }

    protected abstract void copyToProjectIfNeeded(List<File> connectorZips);

    protected abstract File getConnectorExtractFolder();

    protected abstract boolean canContinue(File connectorExtractFolder);

    private List<File> getConnectorZips() {

        List<File> connectorZips = new ArrayList<>();
        for (String folderPath : connectorsZipFolderPath) {
            File folder = new File(folderPath);
            if (folder.exists() && folder.isDirectory()) {
                File[] files = folder.listFiles();
                if (files != null) {
                    for (File f : files) {
                        if (Utils.isZipFile(f)) {
                            connectorZips.add(f);
                        }
                    }
                }
            }
        }
        return connectorZips;
    }

    protected abstract void cleanOldConnectors(File connectorExtractFolder, List<File> connectorZips);

    protected String getConnectorName(File connectorFolder) {

        return connectorReader.getConnectorName(connectorFolder);

    }

    private void extractZips(List<File> connectorZips, File extractFolder) {

        File[] tempFiles = extractFolder.listFiles();
        List<String> tempConnectorNames =
                Arrays.stream(tempFiles).filter(File::isDirectory).map(File::getName).collect(Collectors.toList());
        for (File zip : connectorZips) {
            String zipName = zip.getName();
            zipName = zipName.substring(0, zipName.lastIndexOf(Constant.DOT));
            if (!tempConnectorNames.contains(zipName)) {
                String extractTo = extractFolder.getAbsolutePath() + File.separator + zipName;
                File extractToFolder = new File(extractTo);
                try {
                    Utils.extractZip(zip, extractToFolder);
                } catch (IOException e) {
                    log.log(Level.WARNING, "Failed to extract connector zip:" + zipName, e);
                }
            }
        }
    }

    private void readConnectors(File connectorFolder, String projectUri) {

        File[] files = connectorFolder.listFiles(File::isDirectory);
        for (File f : files) {
            String connectorName = getConnectorName(f);
            if (!connectorHolder.exists(connectorName)) {
                Connector connector = connectorReader.readConnector(f.getAbsolutePath(), projectUri);
                if (connector != null) {
                    connector.setConnectorZipPath(
                            getConnectorZip(connectorHolder.getConnectorZips(), connector.getExtractedConnectorPath()));
                    connectorHolder.addConnector(connector);
                    notifyAddConnector(connector.getName(), true, "Connector added successfully");
                    continue;
                }
                notifyAddConnector(connectorName, false, "Failed to add connector. " +
                        "Corrupted connector file.");
            }
        }
    }

    private String getConnectorZip(List<File> connectorZips, String extractedConnectorPath) {

        String extractedConnectorName =
                extractedConnectorPath.substring(extractedConnectorPath.lastIndexOf(File.separator) + 1);
        for (File zip : connectorZips) {
            if (!zip.getAbsolutePath().contains(projectUri)) {
                continue;
            }
            String zipName = zip.getName();
            zipName = zipName.substring(0, zipName.lastIndexOf("."));
            if (extractedConnectorName.equals(zipName)) {
                return zip.getAbsolutePath();
            }
        }
        return null;
    }

    protected void notifyAddConnector(String connector, boolean isSuccessful, String message) {

        ConnectorStatusNotification status = new ConnectorStatusNotification(connector, isSuccessful, message);
        languageClient.addConnectorStatus(status);
    }

    protected void notifyRemoveConnector(String connector, boolean isSuccessful, String message) {

        ConnectorStatusNotification status = new ConnectorStatusNotification(connector, isSuccessful, message);
        languageClient.removeConnectorStatus(status);
    }

    protected String getProjectUri() {
        return projectUri;
    }

    protected void setProjectUri(String projectUri) {
        this.projectUri = projectUri;
    }
}
