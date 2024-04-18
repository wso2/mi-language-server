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

import org.eclipse.lemminx.customservice.synapse.resourceFinder.ArtifactsResource;
import org.eclipse.lemminx.customservice.synapse.resourceFinder.Resource;
import org.eclipse.lemminx.customservice.synapse.resourceFinder.ResourceFinder;
import org.eclipse.lemminx.customservice.synapse.resourceFinder.ResourceResponse;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.MediatorFactoryFinder;
import org.eclipse.lemminx.customservice.synapse.utils.Constant;
import org.eclipse.lemminx.customservice.synapse.utils.Utils;
import org.eclipse.lemminx.dom.DOMDocument;
import org.eclipse.lemminx.dom.DOMElement;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectionFinder {

    private static final Logger log = Logger.getLogger(ConnectionFinder.class.getName());

    public static Either<List<Connection>, Map<String, List<Connection>>> findConnections(String uri, String name) {

        if (uri != null) {
            ResourceResponse response = ResourceFinder.getAvailableResources(uri, Constant.LOCAL_ENTRY);
            if (response != null) {
                List<Resource> resources = response.getResources();
                if (name != null) {
                    List<Connection> connections = getConnections(resources, name);
                    return Either.forLeft(connections);
                } else {
                    Map<String, List<Connection>> connections = new HashMap<>();
                    populateConnectors(connections);
                    getConnections(connections, resources);
                    return Either.forRight(connections);
                }
            }
        }
        return null;
    }

    private static void populateConnectors(Map<String, List<Connection>> connections) {

        ConnectorHolder connectors = MediatorFactoryFinder.getInstance().getConnectorHolder();
        if (connectors != null) {
            List<Connector> connectorList = connectors.getConnectors();
            for (Connector connector : connectorList) {
                String connectorName = connector.getName();
                connections.put(connectorName, new ArrayList<>());
            }
        }
    }

    private static List<Connection> getConnections(List<Resource> resources, String name) {

        HashMap<String, List<Connection>> connections = new HashMap<>();
        connections.put(name, new ArrayList<>());
        getConnections(connections, resources);
        return connections.get(name);
    }

    private static void getConnections(Map<String, List<Connection>> connections, List<Resource> resources) {

        for (Resource resource : resources) {
            try {
                String localEntryName = resource.getName();
                String filePath = ((ArtifactsResource) resource).getAbsolutePath();
                File file = new File(filePath);
                DOMDocument document = Utils.getDOMDocument(file);
                DOMElement childElement = Utils.getFirstElement(document.getDocumentElement());
                if (childElement != null) {
                    String nodeName = childElement.getNodeName();
                    String connectorName = getConnectorName(nodeName);
                    if (connectorName != null) {
                        addToConnections(connections, connectorName, localEntryName, filePath);
                    }
                }
            } catch (IOException e) {
                log.log(Level.SEVERE, "Error while reading local entry file", e);
            }
        }
    }

    private static String getConnectorName(String nodeName) {

        if (nodeName.contains(".")) {
            String[] split = nodeName.split("\\.");
            return split[0];
        }
        return null;
    }

    private static void addToConnections(Map<String, List<Connection>> connections, String connectorName, String name,
                                         String path) {

        if (connections.containsKey(connectorName)) {
            Connection connection = new Connection(name, path);
            connections.get(connectorName).add(connection);
        }
    }
}
