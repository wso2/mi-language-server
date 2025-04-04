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

public class PrimaryDetails {

    private Node runtimeVersion;
    private Node projectName;
    private Node projectVersion;
    private Node projectDescription;
    private Node projectPackaging;

    PrimaryDetails() {}

    public void setRuntimeVersion(Node runtimeVersion) {
        this.runtimeVersion = runtimeVersion;
    }

    public void setProjectVersion(Node projectVersion) {
        this.projectVersion = projectVersion;
    }

    public void setProjectDescription(Node projectDescription) {
        this.projectDescription = projectDescription;
    }

    public void setProjectName(Node projectName) {
        this.projectName = projectName;
    }

    public void setProjectPackaging(Node projectPackaging) {
        this.projectPackaging = projectPackaging;
    }

    public Node getRuntimeVersion() {
        return this.runtimeVersion;
    }

    public Node getProjectVersion() {
        return this.projectVersion;
    }

    public Node getProjectDescription() {
        return this.projectDescription;
    }

    public Node getProjectName() {
        return this.projectName;
    }

    public Node getProjectPackaging() {
        return this.projectPackaging;
    }
}
