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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.eclipse.lemminx.customservice.synapse.connectors.entity.Connector;
import org.eclipse.lemminx.customservice.synapse.connectors.entity.ConnectorAction;
import org.eclipse.lemminx.customservice.synapse.utils.Constant;
import org.eclipse.lemminx.customservice.synapse.utils.Utils;
import org.eclipse.lemminx.dom.DOMDocument;
import org.eclipse.lemminx.dom.DOMNode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectorReader {

    private static final Logger log = Logger.getLogger(ConnectorReader.class.getName());
    private HashMap<String, List<String>> allowedConnectionTypesMap = new HashMap<>();

    public Connector readConnector(String connectorPath) {

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
                    populateAllowedConnectionTypesMap(connector);
                    populateConnectorActions(connector, componentElement);
                    populateConnectionUiSchema(connector);
                } catch (Exception e) {
                    log.log(Level.SEVERE, "Error reading connector file", e);
                }
            }
        }
        return connector;
    }

    private void populateAllowedConnectionTypesMap(Connector connector) {

        String uiSchemaPath = connector.getUiSchemaPath();
        File uiSchemaFolder = new File(uiSchemaPath);
        if (uiSchemaFolder.exists()) {
            File[] files = uiSchemaFolder.listFiles();
            if (files != null) {
                for (File file : files) {
                    try {
                        String schema = Utils.readFile(file);
                        JsonObject uiJson = Utils.getJsonObject(schema);
                        JsonElement operation = uiJson.get("operationName");
                        if (operation != null) {
                            String operationName = operation.getAsString();
                            getAllowedConnectionTypes(uiJson, operationName);
                        }
                    } catch (IOException e) {
                        log.log(Level.SEVERE, "Error while reading connector ui schema file.", e);
                    }
                }
            }
        }
    }

    private void getAllowedConnectionTypes(JsonObject uiJson, String operationName) {

        JsonArray elements = getElements(uiJson);
        if (elements != null) {
            for (JsonElement element : elements) {
                if (!allowedConnectionTypesMap.containsKey(operationName)) {
                    String type = element.getAsJsonObject().get(Constant.TYPE).getAsString();
                    if (Constant.ATTRIBUTE.equalsIgnoreCase(type)) {
                        JsonElement value = element.getAsJsonObject().get(Constant.VALUE);
                        JsonElement allowedConnectionType = value.getAsJsonObject().get("allowedConnectionTypes");
                        if (allowedConnectionType != null) {
                            JsonArray allowedConnectionTypes = allowedConnectionType.getAsJsonArray();
                            List<String> allowedConnectionTypeList = new ArrayList<>();
                            for (JsonElement connectionType : allowedConnectionTypes) {
                                allowedConnectionTypeList.add(connectionType.getAsString());
                            }
                            allowedConnectionTypesMap.put(operationName, allowedConnectionTypeList);
                            break;
                        }
                    } else if ("attributeGroup".equalsIgnoreCase(type)) {
                        getAllowedConnectionTypes(element.getAsJsonObject(), operationName);
                    }
                }
            }
        }
    }

    private JsonArray getElements(JsonObject uiJson) {

        JsonObject temp = uiJson;
        JsonElement value = uiJson.get(Constant.VALUE);
        if (value != null) {
            temp = value.getAsJsonObject();
        }
        JsonElement elements = temp.get(Constant.ELEMENTS);
        if (elements != null) {
            return elements.getAsJsonArray();
        }
        return null;
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
        readUISchema(connector);
    }

    private void readUISchema(Connector connector) {

        String uiSchemaPath = connector.getUiSchemaPath();
        File uiSchemaFolder = new File(uiSchemaPath);
        if (uiSchemaFolder.exists()) {
            File[] files = uiSchemaFolder.listFiles();
            if (files != null) {
                for (File file : files) {
                    try {
                        String schema = Utils.readFile(file);
                        JsonObject uiJson = Utils.getJsonObject(schema);
                        JsonElement operation = uiJson.get(Constant.OPERATION_NAME);
                        if (operation != null) {
                            String operationName = operation.getAsString();
                            connector.addOperationUiSchema(operationName, file.getAbsolutePath());
                        }
                    } catch (IOException e) {
                        log.log(Level.SEVERE, "Error while reading connector ui schema file", e);
                    }
                }
            }
        }
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
                    if (allowedConnectionTypesMap.containsKey(action.getName())) {
                        action.setAllowedConnectionTypes(allowedConnectionTypesMap.get(action.getName()));
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

    public String getConnectorName(File connectorFolder) {

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
}
