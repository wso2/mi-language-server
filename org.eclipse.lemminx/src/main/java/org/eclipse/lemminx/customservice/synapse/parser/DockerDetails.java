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

public class DockerDetails {

    private Node dockerFileBaseImage;
    private Node dockerName;
    private Node cipherToolEnable;
    private Node keyStoreName;
    private Node keyStorePassword;
    private Node keyStoreAlias;
    private Node keyStoreType;
    private String projectRuntimeVersion;
    private String projectArtifactId;
    private String projectVersion;
    private boolean updateDockerBaseImage = false;
    private boolean updateDockerName = false;

    DockerDetails() {}

    public void setDockerFileBaseImage(Node dockerFileBaseImage) {
        this.dockerFileBaseImage = dockerFileBaseImage;
        updateDockerFileBaseImage();
    }

    public void setDockerName(Node dockerName) {
        this.dockerName = dockerName;
        updateDockerName();
    }

    public void setCipherToolEnable(Node cipherToolEnable) {
        this.cipherToolEnable = cipherToolEnable;
    }

    public void setKeyStoreName(Node keyStoreName) {
        this.keyStoreName = keyStoreName;
    }

    public void setKeyStorePassword(Node keyStorePassword) {
        this.keyStorePassword = keyStorePassword;
    }

    public void setKeyStoreAlias(Node keyStoreAlias) {
        this.keyStoreAlias = keyStoreAlias;
    }

    public void setKeyStoreType(Node keyStoreType) {
        this.keyStoreType = keyStoreType;
    }
    public void setProjectRuntimeVersion(String projectRuntimeVersion) {
        this.projectRuntimeVersion = projectRuntimeVersion;
        updateDockerFileBaseImage();
    }

    public void setProjectArtifactId(String projectArtifactId) {
        this.projectArtifactId = projectArtifactId;
        updateDockerName();
    }

    public void setProjectVersion(String projectVersion) {
        this.projectVersion = projectVersion;
        updateDockerName();
    }

    private void updateDockerFileBaseImage() {
        if (!updateDockerBaseImage && this.projectRuntimeVersion != null && this.dockerFileBaseImage != null) {
            String[] values = this.dockerFileBaseImage.getValue().split(Constants.COLON);
            if (values.length == 2 && values[1].trim().equals(Constants.PROJECT_RUNTIME_VERSION_CONSTANT)) {
                this.dockerFileBaseImage.setDisplayValue(values[0] + Constants.COLON + this.projectRuntimeVersion);
                updateDockerBaseImage = true;
            }
        }
    }

    private void updateDockerName() {
        if (!updateDockerName && this.projectVersion != null && this.dockerName != null &&
                this.projectArtifactId != null) {
            String[] values = this.dockerName.getValue().split(Constants.COLON);
            if (values.length == 2) {
                String version = values[1];
                String artifactId = values[0];
                if (version.trim().equals(Constants.PROJECT_VERSION_CONSTANT)) {
                    version = this.projectVersion;
                }
                if (artifactId.trim().equals(Constants.PROJECT_ARTIFACT_ID_CONSTANT)) {
                    artifactId = this.projectArtifactId;
                }
                this.dockerName.setDisplayValue(artifactId + Constants.COLON + version);
                updateDockerName = true;
            }
        }
    }
}
