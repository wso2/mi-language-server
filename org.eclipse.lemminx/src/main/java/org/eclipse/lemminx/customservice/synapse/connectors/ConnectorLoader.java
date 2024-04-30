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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.io.FileUtils;
import org.eclipse.lemminx.customservice.SynapseLanguageClientAPI;
import org.eclipse.lemminx.customservice.synapse.ConnectorStatusNotification;
import org.eclipse.lemminx.customservice.synapse.utils.Constant;
import org.eclipse.lemminx.customservice.synapse.utils.Utils;
import org.eclipse.lemminx.dom.DOMDocument;
import org.eclipse.lemminx.dom.DOMNode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ConnectorLoader {

    private static final Logger log = Logger.getLogger(ConnectorLoader.class.getName());
    private SynapseLanguageClientAPI languageClient;
    private ConnectorHolder connectorHolder;
    private String connectorsFolderPath;
    private Boolean legacyMode;
    String projectId;

    public ConnectorLoader(SynapseLanguageClientAPI languageClient, ConnectorHolder connectorHolder) {

        this.languageClient = languageClient;
        this.connectorHolder = connectorHolder;
    }

    public void loadConnector() {

        if (legacyMode) {
            loadConnectorInLegacyMode();
        }
        loadConnectorInNonLegacyMode();

    }

    private void loadConnectorInNonLegacyMode() {

        projectId = Utils.getHash(connectorsFolderPath);
        String tempFolderPath =
                System.getProperty("user.home") + File.separator + ".wso2-mi" + File.separator + "connectors"
                        + File.separator + projectId;
        File tempFolder = new File(tempFolderPath);

        if (!tempFolder.exists()) {
            tempFolder.mkdirs();
        }
        List<File> connectorZips = new ArrayList<>();
        File folder = new File(connectorsFolderPath);
        if (folder.exists()) {
            File[] files = folder.listFiles();
            for (File f : files) {
                if (Utils.isZipFile(f)) {
                    connectorZips.add(f);
                }
            }
        }
        removeOldConnectors(tempFolder, connectorZips);
        extractZips(connectorZips, tempFolder);
        readTempFolder(tempFolder);
    }

    private void removeOldConnectors(File tempFolder, List<File> connectorZips) {

        File[] tempFiles = tempFolder.listFiles();
        List<String> tempConnectorNames =
                Arrays.stream(tempFiles).filter(File::isDirectory).map(File::getName).collect(Collectors.toList());
        for (String connectorName : tempConnectorNames) {
            boolean isConnectorAvailable =
                    connectorZips.stream().anyMatch(file -> file.getName().contains(connectorName));
            if (!isConnectorAvailable) {
                File connectorFolder = new File(tempFolder.getAbsolutePath() + File.separator + connectorName);
                connectorHolder.removeConnector(getConnectorName(connectorFolder));
                try {
                    FileUtils.deleteDirectory(connectorFolder);
                    notifyRemoveConnector(connectorName, true, "Connector deleted successfully");
                } catch (IOException e) {
                    log.log(Level.WARNING, "Failed to delete connector folder:" + connectorName, e);
                }
            }
        }
    }

    private String getConnectorName(File connectorFolder) {

        String connectorName = null;
        File connectorFile = new File(connectorFolder.getAbsolutePath() + File.separator + "connector.xml");
        if (connectorFile.exists()) {
            try {
                DOMDocument connectorDocument = Utils.getDOMDocument(connectorFile);
                DOMNode connectorElement = Utils.getChildNodeByName(connectorDocument, "connector");
                DOMNode componentElement = Utils.getChildNodeByName(connectorElement, "component");
                connectorName = componentElement.getAttribute(Constant.NAME);
            } catch (Exception e) {
                log.log(Level.WARNING, "Error reading connector file", e);
            }
        }
        return connectorName;
    }

    public void loadConnectorInLegacyMode() {

        ConnectorHolder holder = new ConnectorHolder();
        File connectorFolder = new File(connectorsFolderPath);
        if (connectorsFolderPath != null && connectorFolder.exists()) {
            File[] connectors = connectorFolder.listFiles(File::isDirectory);
            for (File connectorFile : connectors) {
                Connector connector = readConnector(connectorFile.getPath());
                if (connector != null) {
                    holder.addConnector(connector);
                }
            }
        }
        connectorHolder = holder;
    }

    private void extractZips(List<File> connectorZips, File tempFolder) {

        File[] tempFiles = tempFolder.listFiles();
        List<String> tempConnectorNames =
                Arrays.stream(tempFiles).filter(File::isDirectory).map(File::getName).collect(Collectors.toList());
        for (File zip : connectorZips) {
            String zipName = zip.getName();
            zipName = zipName.substring(0, zipName.lastIndexOf(Constant.DOT));
            if (!tempConnectorNames.contains(zipName)) {
                String extractTo = tempFolder.getAbsolutePath() + File.separator + zipName;
                File extractToFolder = new File(extractTo);
                try {
                    Utils.extractZip(zip, extractToFolder);
                } catch (IOException e) {
                    notifyAddConnector(zipName, false, "Failed to extract connector zip");
                    log.log(Level.WARNING, "Failed to extract connector zip:" + zipName, e);
                }
            }
        }
    }

    private void readTempFolder(File tempFolder) {

        File[] files = tempFolder.listFiles(File::isDirectory);
        for (File f : files) {
            String connectorName = getConnectorName(f);
            if (!connectorHolder.isAlreadyExist(connectorName)) {
                Connector connector = readConnector(f.getAbsolutePath());
                if (connector != null) {
                    connectorHolder.addConnector(connector);
                    notifyAddConnector(connector.getName(), true, "Connector added successfully");
                    continue;
                }
                notifyAddConnector(connectorName, false, "Failed to add connector. " +
                        "Corrupted connector file");
            }
        }
    }

    private Connector readConnector(String connectorPath) {

        Connector connector = null;
        if (connectorPath != null) {
            File connectorFile = new File(connectorPath + File.separator + "connector.xml");
            if (connectorFile.exists()) {
                try {
                    DOMDocument connectorDocument = Utils.getDOMDocument(connectorFile);
                    DOMNode connectorElement = Utils.getChildNodeByName(connectorDocument, "connector");
                    DOMNode componentElement = Utils.getChildNodeByName(connectorElement, "component");
                    String name = componentElement.getAttribute(Constant.NAME);
                    connector = new Connector();
                    connector.setName(name);
                    connector.setPath(connectorPath);
                    connector.setVersion(getConnectorVersion(connectorPath));
                    connector.setIconPath(connectorPath + File.separator + "icon");
                    connector.setUiSchemaPath(connectorPath + File.separator + "uischema");
                    populateConnectorActions(connector, componentElement);
                    populateConnectionUiSchema(connector);
                } catch (Exception e) {
                    log.log(Level.SEVERE, "Error reading connector file", e);
                }
            }
        }
        return connector;
    }

    private String getConnectorVersion(String connectorPath) {

        String connectorName = connectorPath.substring(connectorPath.lastIndexOf(File.separator) + 1);
        int versionStartIndex = connectorName.lastIndexOf("-");
        if (versionStartIndex == -1) {
            return "";
        }
        return connectorName.substring(versionStartIndex + 1);
    }

    private void populateConnectorActions(Connector connector, DOMNode componentElement) {

        List<String> dependencies = getDependencies(componentElement);
        readDependencies(connector, dependencies);
    }

    private void populateConnectionUiSchema(Connector connector) {

        File uiSchemaFolder = new File(connector.getUiSchemaPath());
        if (uiSchemaFolder.exists()) {
            File[] files = uiSchemaFolder.listFiles();
            if (files != null) {
                for (File file : files) {
                    String connectionName = getConnectionSchemaName(file);
                    if (connectionName != null) {
                        connector.addConnectionUiSchema(connectionName, file.getAbsolutePath());
                    }
                }
            }
        }
    }

    private String getConnectionSchemaName(File file) {

        String connectionName = null;
        try {
            String schema = Utils.readFile(file);
            JsonObject uiJson = Utils.getJsonObject(schema);
            if (uiJson != null) {
                JsonElement connectionNameEle = uiJson.get("connectionName");
                if (connectionNameEle != null) {
                    connectionName = connectionNameEle.getAsString();
                }
            }
        } catch (IOException e) {
            log.log(Level.WARNING, "Error while reading connection ui schema file", e);
        }
        return connectionName;
    }

    private List<String> getDependencies(DOMNode connectorElement) {

        List<String> dependencies = new ArrayList<>();
        List<DOMNode> children = connectorElement.getChildren();
        for (DOMNode child : children) {
            if (child.getNodeName().equals(Constant.DEPENDENCY)) {
                String dependency = child.getAttribute(Constant.COMPONENT);
                dependencies.add(dependency);
            }
        }
        return dependencies;
    }

    private void readDependencies(Connector connector, List<String> dependencies) {

        for (String dependency : dependencies) {
            File dependencyFile = new File(connector.getPath() + File.separator + dependency + File.separator +
                    "component.xml");
            if (dependencyFile.exists()) {
                readSubComponents(connector, dependencyFile);
            }
        }
    }

    private void readSubComponents(Connector connector, File dependencyFile) {

        try {
            DOMDocument dependencyDocument = Utils.getDOMDocument(dependencyFile);
            DOMNode componentElement = Utils.getChildNodeByName(dependencyDocument, "component");
            DOMNode subComponents = Utils.getChildNodeByName(componentElement, "subComponents");
            List<DOMNode> children = subComponents.getChildren();
            for (DOMNode child : children) {
                if (child.getNodeName().equals(Constant.COMPONENT)) {
                    ConnectorAction action = new ConnectorAction();
                    String name = child.getAttribute(Constant.NAME);
                    action.setName(name);
                    String tag = connector.getName() + Constant.DOT + name;
                    action.setTag(tag);
                    DOMNode descriptionNode = Utils.getChildNodeByName(child, Constant.DESCRIPTION);
                    if (descriptionNode != null) {
                        String description = Utils.getInlineString(descriptionNode.getFirstChild());
                        action.setDescription(description);
                    }
                    DOMNode fileNode = Utils.getChildNodeByName(child, "file");
                    if (fileNode != null) {
                        String fileName = Utils.getInlineString(fileNode.getFirstChild());
                        String actionPath = dependencyFile.getParent() + File.separator + fileName;
                        populateParameters(action, actionPath);
                    }
                    DOMNode hiddenNode = Utils.getChildNodeByName(child, "hidden");
                    if (hiddenNode != null) {
                        String isHidden = Utils.getInlineString(hiddenNode.getFirstChild());
                        action.setHidden(Boolean.parseBoolean(isHidden));
                    } else {
                        action.setHidden(Boolean.FALSE);
                    }
                    connector.addAction(action);
                }
            }
        } catch (IOException e) {
            log.log(Level.WARNING, "Error while reading " + connector.getName() + " connector", e);
        }
    }

    private void populateParameters(ConnectorAction action, String actionPath) {

        File actionFile = getActionPath(actionPath);
        if (actionFile != null) {
            try {
                DOMDocument actionDom = Utils.getDOMDocument(actionFile);
                DOMNode templateNode = Utils.getChildNodeByName(actionDom, "template");
                if (templateNode != null) {
                    List<DOMNode> children = templateNode.getChildren();
                    for (DOMNode child : children) {
                        if ("parameter".equalsIgnoreCase(child.getNodeName())) {
                            String name = child.getAttribute("name");
                            action.addParameter(name);
                        }
                    }
                }
            } catch (IOException e) {
                log.log(Level.WARNING, "Error while reading " + action.getName() + " connector action", e);
            }
        }
    }

    private File getActionPath(String actionPath) {

        String dependencyPath = actionPath.substring(0, actionPath.lastIndexOf(File.separator));
        File dependencyFolder = new File(dependencyPath);
        File[] files = dependencyFolder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (actionPath.equalsIgnoreCase(file.getAbsolutePath())) {
                    return file;
                }
            }
        }
        return null;
    }

    public void updateConnectorLoader(String projectPath) {

        setLegacyMode(projectPath);
        setConnectorsFolderPath(projectPath);
    }

    private void setLegacyMode(String projectPath) {

        if (Utils.isLegacyProject(projectPath)) {
            legacyMode = Boolean.TRUE;
        } else {
            legacyMode = Boolean.FALSE;
        }
    }

    private void setConnectorsFolderPath(String projectRoot) {

        if (projectRoot != null) {
            if (legacyMode) {
                File projectFile = new File(projectRoot);
                File parentFolder = projectFile.getParentFile();
                String workspacePath = parentFolder.getAbsolutePath();
                connectorsFolderPath = Path.of(workspacePath, ".metadata", ".Connectors").toString();
            } else {
                connectorsFolderPath =
                        Path.of(projectRoot, "src", "main", "wso2mi", "resources", "connectors").toString();
            }
        } else {
            // To avoid null pointer exception.
            connectorsFolderPath = "";
        }
    }

    private void notifyAddConnector(String connector, boolean isSuccessful, String message) {

        ConnectorStatusNotification status = new ConnectorStatusNotification(connector, isSuccessful, message);
        languageClient.addConnectorStatus(status);
    }

    private void notifyRemoveConnector(String connector, boolean isSuccessful, String message) {

        ConnectorStatusNotification status = new ConnectorStatusNotification(connector, isSuccessful, message);
        languageClient.removeConnectorStatus(status);
    }
}
