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

package org.eclipse.lemminx.customservice.synapse.dependency.tree;

import org.eclipse.lemminx.customservice.synapse.dependency.tree.pojo.Connection;
import org.eclipse.lemminx.customservice.synapse.dependency.tree.pojo.Dependency;
import org.eclipse.lemminx.customservice.synapse.dependency.tree.pojo.DependencyTree;
import org.eclipse.lemminx.customservice.synapse.dependency.tree.pojo.Entrypoint;
import org.eclipse.lemminx.customservice.synapse.dependency.tree.pojo.OverviewModel;
import org.eclipse.lemminx.customservice.synapse.resourceFinder.NewProjectResourceFinder;
import org.eclipse.lemminx.customservice.synapse.resourceFinder.pojo.ArtifactResource;
import org.eclipse.lemminx.customservice.synapse.resourceFinder.pojo.RequestedResource;
import org.eclipse.lemminx.customservice.synapse.resourceFinder.pojo.Resource;
import org.eclipse.lemminx.customservice.synapse.resourceFinder.pojo.ResourceResponse;
import org.eclipse.lemminx.customservice.synapse.utils.Constant;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OverviewModelGenerator {

    private static int connectionId = 1;

    /**
     * Generate the overview model for the project
     *
     * @param projectPath absolute path of the project
     *
     * @return overview model for the project
     */
    public static OverviewModel getOverviewModel(String projectPath) {
        List<DependencyTree> dependencyTreeList = new ArrayList<>();
        List<String> types = List.of(Constant.API, Constant.TASK, Constant.INBOUND_DASH_ENDPOINT);
        List<RequestedResource> requiredResources = new ArrayList<>();
        for (String type : types) {
            requiredResources.add(new RequestedResource(type, false));
        }
        NewProjectResourceFinder newProjectResourceFinder = new NewProjectResourceFinder();
        ResourceResponse response = newProjectResourceFinder.getAvailableResources(projectPath, Either.forRight(requiredResources));
        for (Resource resource : response.getResources()) {
            DependencyScanner dependencyScanner = new DependencyScanner(projectPath);
            DependencyTree dependencyTree = dependencyScanner.analyzeArtifact(((ArtifactResource) resource).getAbsolutePath());
            dependencyTreeList.add(dependencyTree);
        }
        return convertDataToOverviewModel(Paths.get(projectPath).getFileName().toString(), dependencyTreeList);
    }

    /**
     * Convert dependency trees of artifacts to the overview model
     *
     * @param projectName name of the project
     * @param dependencyTreeList dependency trees of artifacts in the project
     *
     * @return overview model for the project
     */
    private static OverviewModel convertDataToOverviewModel(String projectName, List<DependencyTree> dependencyTreeList) {
        List<Entrypoint> entrypoints = new ArrayList<>();
        List<Connection> connections = new ArrayList<>();
        Map<String, String> connectionMap = new HashMap<>();
        int entrypointId = 1;

        for (DependencyTree dependencyTree : dependencyTreeList) {
            String entrypointType = getEntrypointType(dependencyTree.getType());
            if (entrypointType.equals(Constant.OTHER)) {
                continue;
            }

            List<String> connectionIds = new ArrayList<>();
            extractConnections(dependencyTree.getDependencyList(), connections, connectionMap, connectionIds);
            List<Integer> sortedConnectionIds = connectionIds.stream().map(Integer::parseInt).sorted().collect(Collectors.toList());

            entrypoints.add(new Entrypoint(String.valueOf(entrypointId++), dependencyTree.getName(), entrypointType, dependencyTree.getPath(),
                    new ArrayList<>(), sortedConnectionIds.stream().map(String::valueOf).collect(Collectors.toList())));
        }
        connectionId = 1;
        return new OverviewModel(projectName, entrypoints, connections);
    }

    /**
     * Extract the connections used in a given artifact
     *
     * @param dependencyList dependencies of the considered artifact
     * @param connections connections in the project
     * @param connectionMap map of connections used in the considered artifact
     * @param connectionIds ID list of the connections used in the considered artifact
     */
    private static void extractConnections(List<Dependency> dependencyList, List<Connection> connections,
                                           Map<String, String> connectionMap, List<String> connectionIds) {
        for (Dependency dependency : dependencyList) {
            if (dependency.getType().name().equals(Constant.CONNECTION_UPPERCASE)) {
                if (!connectionMap.containsKey(dependency.getName())) {
                    String newConnectionId = String.valueOf(connectionId++);
                    connections.add(new Connection(newConnectionId, dependency.getName(), dependency.getPath()));
                    connectionMap.put(dependency.getName(), newConnectionId);
                }
                String currentConnectionId = connectionMap.get(dependency.getName());
                if (!connectionIds.contains(currentConnectionId)) {
                    connectionIds.add(currentConnectionId);
                }
            }
            extractConnections(dependency.getDependencyList(), connections, connectionMap, connectionIds);
        }
    }

    /**
     * Get the entrypoint type of a given artifact
     *
     * @param type type of the considered artifact
     *
     * @return entrypoint type of the considered artifact
     */
    private static String getEntrypointType(String type) {
        switch (type) {
            case Constant.API_UPPERCASE:
                return Constant.SERVICE;
            case Constant.TASK_UPPERCASE:
                return Constant.SCHEDULED_TASK;
            case Constant.INBOUND_UPPERCASE:
                return Constant.TRIGGER;
            default:
                return Constant.OTHER;
        }
    }
}
