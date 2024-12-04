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

package org.eclipse.lemminx.customservice.synapse.directoryTree.node;

import java.util.ArrayList;
import java.util.List;

public class Resource {

    private RegistryResource registry;
    private List<Node> connectors;
    private List<Node> metadata;
    private FolderNode newResources;

    public Resource() {

        registry = new RegistryResource();
        connectors = new ArrayList<>();
        metadata = new ArrayList<>();
    }

    public FolderNode getNewResources() {

        return newResources;
    }

    public void setNewResources(FolderNode newResources) {

        this.newResources = newResources;
    }

    public void addConnector(Node connector) {

        connectors.add(connector);
    }

    public void addMetadata(Node meta) {

        metadata.add(meta);
    }

    public RegistryResource getRegistry() {

        return registry;
    }
}
