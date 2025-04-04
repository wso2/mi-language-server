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

package org.eclipse.lemminx.customservice.synapse.resourceFinder;

import org.eclipse.lemminx.customservice.synapse.connectors.ConnectionFinder;
import org.eclipse.lemminx.customservice.synapse.connectors.ConnectorHolder;
import org.eclipse.lemminx.customservice.synapse.connectors.entity.Connection;
import org.eclipse.lemminx.customservice.synapse.connectors.entity.ConnectionParameter;
import org.eclipse.lemminx.customservice.synapse.connectors.entity.Connections;
import org.eclipse.lemminx.customservice.synapse.dependency.tree.DependencyScanner;
import org.eclipse.lemminx.customservice.synapse.dependency.tree.pojo.Dependency;
import org.eclipse.lemminx.customservice.synapse.dependency.tree.pojo.DependencyTree;
import org.eclipse.lemminx.customservice.synapse.directoryTree.DirectoryTreeBuilder;
import org.eclipse.lemminx.customservice.synapse.utils.Utils;
import org.eclipse.lsp4j.WorkspaceFolder;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ResourceUsageFinder {

    public static List<String> findResourceUsages(String projectUri, String resourceFilePath,
                                                  ConnectorHolder connectorHolder, boolean isLegacyProject) {

        String derivedKey = Utils.deriveResourceKeyFromFilePath(resourceFilePath);
        List<String> resourceUsagesList = new ArrayList<>();
        List<String> artifactFilePaths = ArtifactFileScanner.scanArtifactFiles(projectUri, true);

        DependencyScanner dependencyScanner = new DependencyScanner(projectUri);

        for (String artifactFilePath : artifactFilePaths) {
            DependencyTree artifactDependencyTree = dependencyScanner.analyzeArtifact(artifactFilePath);
            List<Dependency> dependencyList = artifactDependencyTree.getDependencyList();
            for (Dependency dependency : dependencyList) {
                if (dependency.getName().equals(derivedKey)) {
                    resourceUsagesList.add(artifactFilePath);
                }
            }
        }

        Either<Connections, Map<String, Connections>> connectionsResult =
                ConnectionFinder.findConnections(projectUri, null, connectorHolder, isLegacyProject);
        if (connectionsResult.isRight()) {
            Map<String, Connections> connectionsMap = connectionsResult.getRight();
            for (Map.Entry<String, Connections> entry : connectionsMap.entrySet()) {
                Connections connections = entry.getValue();
                for (Connection connection : connections.getConnections()) {
                    List<ConnectionParameter> parameters = connection.getParameters();
                    for (ConnectionParameter parameter : parameters) {
                        if (parameter.getValue() != null && parameter.getValue().equals(derivedKey)) {
                            resourceUsagesList.add(connection.getPath());
                        }
                    }
                }
            }
        }

        return resourceUsagesList;
    }

    public static List<String> findResourceUsagesProjectIdentifiers(String projectUri, String resourceFilePath,
                                                                    ConnectorHolder connectorHolder, boolean isLegacyProject) {

        List<String> resourceUsagesList = findResourceUsages(projectUri, resourceFilePath, connectorHolder, isLegacyProject);
        return DirectoryTreeBuilder.getProjectIdentifiers(new WorkspaceFolder(projectUri),
                resourceUsagesList);
    }
}
