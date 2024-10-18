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

package org.eclipse.lemminx.customservice.synapse.dependency.tree.pojo;

import java.util.List;
import java.util.stream.Collectors;

public class Entrypoint {
    private String id;
    private String name;
    private String type;
    private String path;
    private List<String> dependencies;
    private List<String> connections;

    public Entrypoint(String id, String name, String type, String path, List<String> dependencies, List<String> connections) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.path = path;
        this.dependencies = dependencies;
        this.connections = connections;
    }

    @Override
    public String toString() {
        String formattedConnections = connections.stream().map(s -> "\"" + s + "\"").collect(Collectors.joining(", ", "[", "]"));
        return String.format("{ \"id\": \"%s\", \"name\": \"%s\", \"type\": \"%s\", \"path\": \"%s\", \"dependencies\": %s, \"connections\": %s }",
                id, name, type, path, dependencies.toString(), formattedConnections);
    }
}
