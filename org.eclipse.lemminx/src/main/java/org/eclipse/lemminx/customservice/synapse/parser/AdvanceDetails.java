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
package org.eclipse.lemminx.customservice.synapse.parser;

public class AdvanceDetails {

    private Node projectGroupId;
    private Node projectArtifactId;
    private PluginDetails pluginDetails;

    AdvanceDetails() {
        pluginDetails = new PluginDetails();
    }

    public void setProjectGroupId(Node projectGroupId) {
        this.projectGroupId = projectGroupId;
    }

    public void setProjectArtifactId(Node projectArtifactId) {
        this.projectArtifactId = projectArtifactId;
    }

    public PluginDetails getPluginDetails() {
        return this.pluginDetails;
    }

    public Node getProjectGroupId() {
        return this.projectGroupId;
    }

    public Node getProjectArtifactId() {
        return this.projectArtifactId;
    }
}
