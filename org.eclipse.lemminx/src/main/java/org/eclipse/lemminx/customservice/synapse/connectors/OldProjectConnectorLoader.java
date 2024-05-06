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
import org.eclipse.lemminx.customservice.synapse.directoryTree.legacyBuilder.utils.ProjectType;
import org.eclipse.lemminx.customservice.synapse.utils.LegacyConfigFinder;
import org.eclipse.lemminx.customservice.synapse.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class to load connectors for old projects.
 */
public class OldProjectConnectorLoader extends AbstractConnectorLoader {

    private static final Logger log = Logger.getLogger(OldProjectConnectorLoader.class.getName());

    public OldProjectConnectorLoader(SynapseLanguageClientAPI languageClient, ConnectorHolder connectorHolder) {

        super(languageClient, connectorHolder);
    }

    @Override
    protected File getConnectorExtractFolder() {

        File connectorsFolderPath = new File(this.connectorsZipFolderPath);
        return connectorsFolderPath;
    }

    @Override
    protected boolean canContinue(File connectorExtractFolder) {

        if (connectorExtractFolder.exists()) {
            return true;
        }
        return false;
    }

    @Override
    protected void cleanOldConnectors(File connectorExtractFolder, List<File> connectorZips) {

    }

    @Override
    protected List<File> getConnectorZips() {

        List<File> connectorZips = super.getConnectorZips();
        List<File> connectorsFromExporters = getConnectorsFromExporters();
        connectorZips.addAll(connectorsFromExporters);
        return connectorZips;
    }

    private List<File> getConnectorsFromExporters() {

        List<File> connectorZips = new ArrayList<>();
        try {
            List<String> connectorExporters = LegacyConfigFinder.getConfigPaths(projectUri,
                    ProjectType.CONNECTOR_EXPORTER.value);
            for (String connectorExporter : connectorExporters) {
                File connectorExporterFolder = new File(connectorExporter);
                File[] files = connectorExporterFolder.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (Utils.isZipFile(file)) {
                            connectorZips.add(file);
                        }
                    }
                }
            }
        } catch (IOException e) {
            log.log(Level.SEVERE, "Error while reading connectors in the connector exporter projects.", e);
        }
        return connectorZips;
    }

    @Override
    protected void setConnectorsZipFolderPath(String projectRoot) {

        File projectFile = new File(projectRoot);
        File parentFolder = projectFile.getParentFile();
        String workspacePath = parentFolder.getAbsolutePath();
        connectorsZipFolderPath = Path.of(workspacePath, ".metadata", ".Connectors").toString();
    }
}
