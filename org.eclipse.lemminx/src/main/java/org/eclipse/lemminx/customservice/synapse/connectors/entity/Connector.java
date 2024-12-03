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

package org.eclipse.lemminx.customservice.synapse.connectors.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Connector {

    private String name;
    private String path;
    private String version;
    private List<ConnectorAction> actions;
    private Map<String, String> connectionUiSchema;
    private String iconPath;
    private String uiSchemaPath;

    public Connector() {

        this.actions = new ArrayList<>();
        this.connectionUiSchema = new HashMap<>();
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public String getPath() {

        return path;
    }

    public void setPath(String path) {

        this.path = path;
    }

    public void addAction(ConnectorAction action) {

        actions.add(action);
    }

    public List<ConnectorAction> getActions() {

        return actions;
    }

    public ConnectorAction getAction(String actionName) {

        for (ConnectorAction action : actions) {
            if (action.getName().equals(actionName)) {
                return action;
            }
        }
        return null;
    }

    public void setActions(List<ConnectorAction> actions) {

        this.actions = actions;
    }

    public void addConnectionUiSchema(String key, String value) {

        connectionUiSchema.put(key, value);
    }

    public Map<String, String> getConnectionUiSchema() {

        return connectionUiSchema;
    }

    public void setConnectionUiSchema(Map<String, String> connectionUiSchema) {

        this.connectionUiSchema = connectionUiSchema;
    }

    public String getVersion() {

        return version;
    }

    public void setVersion(String version) {

        this.version = version;
    }

    public String getIconPath() {

        return iconPath;
    }

    public void setIconPath(String iconPath) {

        this.iconPath = iconPath;
    }

    public String getUiSchemaPath() {

        return uiSchemaPath;
    }

    public void setUiSchemaPath(String uiSchemaPath) {

        this.uiSchemaPath = uiSchemaPath;
    }

    public void addOperationUiSchema(String operationName, String absolutePath) {

        if (operationName == null) {
            return;
        }
        for (ConnectorAction action : actions) {
            if (action.getName().equals(operationName)) {
                action.setUiSchemaPath(absolutePath);
            }
        }
    }
}
