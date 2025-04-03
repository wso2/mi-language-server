/*
 * Copyright (c) 2025, WSO2 LLC. (http://www.wso2.com).
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

package org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.connector.ai;

import org.eclipse.lemminx.customservice.synapse.connectors.entity.Connection;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.connector.Connector;

import java.util.HashMap;
import java.util.Map;

public abstract class AIConnector extends Connector {

    private Map<String, Connection> connections;

    public AIConnector() {

        connections = new HashMap<>();
    }

    public Map<String, Connection> getConnections() {

        return connections;
    }

    public void addConnection(String name, Connection connection) {

        this.connections.put(name, connection);
    }
}
