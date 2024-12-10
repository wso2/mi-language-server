/*
 *   Copyright (c) 2024, WSO2 LLC. (http://www.wso2.com).
 *
 *   WSO2 LLC. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 */

package org.eclipse.lemminx.customservice.synapse.connectors.entity;

public class ConnectionUIParam {

    private String connectorName;
    private String connectionType;
    private String documentUri;

    public ConnectionUIParam(String connectorName, String connectionType) {

        this.connectorName = connectorName;
        this.connectionType = connectionType;
    }

    public ConnectionUIParam(String documentUri) {

        this.documentUri = documentUri;
    }

    public String getConnectorName() {

        return connectorName;
    }

    public void setConnectorName(String connectorName) {

        this.connectorName = connectorName;
    }

    public String getConnectionType() {

        return connectionType;
    }

    public void setConnectionType(String connectionType) {

        this.connectionType = connectionType;
    }

    public String getDocumentUri() {

        return documentUri;
    }

    public void setDocumentUri(String documentUri) {

        this.documentUri = documentUri;
    }
}
