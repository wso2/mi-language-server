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

import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.connector.ConnectorParameter;

import java.util.List;

public class Connection {

    String connectorName;
    String name;
    String connectionType;
    String path;
    List<ConnectionParameter> parameters;

    public Connection(String connectorName, String name, String connectionType, List<ConnectionParameter> parameters, String path) {

        this.connectorName = connectorName;
        this.name = name;
        this.connectionType = connectionType;
        this.path = path;
        this.parameters = parameters;
    }

    public String getConnectorName() {

        return connectorName;
    }

    public String getName() {

        return name;
    }

    public String getConnectionType() {

        return connectionType;
    }

    public List<ConnectionParameter> getParameters() {

        return parameters;
    }

    public String getPath() {

        return path;
    }
}
