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

import org.eclipse.lemminx.customservice.synapse.connectors.entity.Connector;
import org.eclipse.lemminx.customservice.synapse.connectors.entity.ConnectorAction;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConnectorHolder {

    private List<Connector> connectors;
    private List<File> connectorZips;

    public ConnectorHolder() {

        this.connectors = new ArrayList<>();
    }

    public void addConnector(Connector connector) {

        connectors.add(connector);
    }

    private Boolean exists(Connector connector) {

        for (Connector con : connectors) {
            if (con.getName().equals(connector.getName())) {
                return true;
            }
        }
        return false;
    }

    public boolean exists(String connectorName) {

        for (Connector connector : connectors) {
            if (connector.getName().equalsIgnoreCase(connectorName)) {
                return true;
            }
        }
        return false;
    }

    public List<Connector> getConnectors() {

        return Collections.unmodifiableList(connectors);
    }

    public Connector getConnector(String name) {

        for (Connector connector : connectors) {
            if (connector.getName().equalsIgnoreCase(name)) {
                return connector;
            }
        }
        return null;
    }

    public Boolean isValidConnector(String name) {

        String connectorName = name.split("\\.")[0];
        for (Connector connector : connectors) {
            if (connector.getName().equalsIgnoreCase(connectorName)) {
                for (ConnectorAction action : connector.getActions()) {
                    String tag = action.getTag();
                    if (tag != null && tag.equalsIgnoreCase(name)) {
                        return Boolean.TRUE;
                    }
                }
            }
        }
        return Boolean.FALSE;
    }

    public void removeConnector(String connectorName) {

        if (connectorName != null) {
            for (Connector connector : connectors) {
                if (connector.getName().equalsIgnoreCase(connectorName)) {
                    connectors.remove(connector);
                    break;
                }
            }
        }
    }

    public List<File> getConnectorZips() {

        return connectorZips;
    }

    public void setConnectorZips(List<File> connectorZips) {

        this.connectorZips = connectorZips;
    }

    public void clearConnectors() {

        connectors.clear();
    }
}
