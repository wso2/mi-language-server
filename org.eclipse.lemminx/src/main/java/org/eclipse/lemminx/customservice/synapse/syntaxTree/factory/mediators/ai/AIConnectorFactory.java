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

package org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.ai;

import org.eclipse.lemminx.customservice.synapse.connectors.ConnectionFinder;
import org.eclipse.lemminx.customservice.synapse.connectors.ConnectorHolder;
import org.eclipse.lemminx.customservice.synapse.connectors.entity.Connection;
import org.eclipse.lemminx.customservice.synapse.connectors.entity.Connections;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.ConnectorFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.connector.ai.AIConnector;
import org.eclipse.lemminx.customservice.synapse.utils.Constant;
import org.eclipse.lemminx.customservice.synapse.utils.Utils;
import org.eclipse.lemminx.dom.DOMElement;
import org.eclipse.lemminx.dom.DOMNode;

import java.util.List;

public abstract class AIConnectorFactory extends ConnectorFactory {

    private static final String AI = "ai";

    protected void populateConnectorConfigs(AIConnector aiConnector, DOMElement element) {

        aiConnector.elementNode(element);
        String elementName = element.getNodeName();
        String connectorName = elementName.substring(0, elementName.indexOf(Constant.DOT));
        aiConnector.setConnectorName(connectorName);
        aiConnector.setMethod(elementName.substring(elementName.indexOf(Constant.DOT) + 1));
        populateAttributes(aiConnector, element);
        aiConnector.removeParameter("connections");
    }

    protected void populateConnections(AIConnector aiConnector, DOMElement element,
                                       List<String> allowedConnectionTags) {

        DOMNode connectionsElement = Utils.getChildNodeByName(element, Constant.CONNECTIONS);
        if (connectionsElement != null) {
            Connections connections = ConnectionFinder.findConnections(getProjectPath(), AI,
                    ConnectorHolder.getInstance(), false).getLeft();

            List<DOMNode> connectionElements = connectionsElement.getChildren();
            for (DOMNode connectionElement : connectionElements) {
                String connectionType = connectionElement.getNodeName();
                String connectionName = Utils.getInlineString(connectionElement.getFirstChild());
                if (allowedConnectionTags.contains(connectionType)) {
                    String connectionDisplayName = Constant.AI_CONNECTION_TO_DISPLAY_NAME_MAP.get(connectionType);
                    aiConnector.addConnection(connectionDisplayName,
                            findConnection(connectionName, connectionDisplayName, connections));
                }

            }
        }
    }

    private Connection findConnection(String connectionName, String connectionType, Connections connections) {

        if (connections != null) {
            List<Connection> connectionList = connections.getConnections();
            for (Connection connection : connectionList) {
                if (connection.getName().equals(connectionName)) {
                    return connection;
                }
            }
        }
        return new Connection(AI, connectionName, connectionType, null, null);
    }
}
