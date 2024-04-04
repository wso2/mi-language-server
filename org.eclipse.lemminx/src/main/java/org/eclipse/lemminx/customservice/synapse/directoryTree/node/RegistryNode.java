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

public class RegistryNode extends Node {

    final boolean isRegistryResource = Boolean.TRUE;
    String registryPath;

    public RegistryNode(Node node, String registryPath) {

        super(node.getType(), node.getName(), node.getPath());
        this.subType = node.getSubType();
        this.registryPath = registryPath;
    }

    public String getRegistryPath() {

        return registryPath;
    }

    public void setRegistryPath(String registryPath) {

        this.registryPath = registryPath;
    }
}
