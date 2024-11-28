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

import org.eclipse.lsp4j.Range;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OverviewPageDetailsResponse {

    Node runtimeVersion;
    Node projectGroupId;
    Node projectArtifactId;
    Node projectVersion;
    Node projectDescription;
    Node projectName;
    Node projectBuildPluginVersion;
    Node keyStoreType;
    List<Map<String, Node>> connectorDependencies;
    List<Map<String, Node>> otherDependencies;
    Node miContainerPluginVersion;
    Node unitTestPluginVersion;
    Node keyStoreName;
    Node keyStorePassword;
    Node keyStoreAlias;
    Node cipherToolEnable;
    Node dockerFileBaseImage;
    Node dockerName;
    Node serverDownloadLink;
    Node serverVersion;
    Node serverPath;
    Node serverPort;
    Node serverHost;
    Node serverType;
    Node skipTest;

    List<Range> ranges;

    List<Node> configs;
    Range lastDependencyEndTagRange;

    public OverviewPageDetailsResponse() {
        connectorDependencies = new ArrayList<>();
        otherDependencies = new ArrayList<>();
        ranges = new ArrayList<>();
        configs = new ArrayList<>();
    }


    public void setRuntimeVersion(Node runtimeVersion) {
        this.runtimeVersion = runtimeVersion;
    }

    public void setProjectGroupId(Node projectGroupId) {
        this.projectGroupId = projectGroupId;
    }

    public void setProjectName(Node projectName) {
        this.projectName = projectName;
    }

    public void setProjectArtifactId(Node projectArtifactId) {
        this.projectArtifactId = projectArtifactId;
    }

    public void setProjectVersion(Node projectVersion) {
        this.projectVersion = projectVersion;
    }

    public void setProjectDescription(Node projectDescription) {
        this.projectDescription = projectDescription;
    }

    public void setProjectBuildPluginVersion(String pluginVersion, Range range) {
        ranges.add(range);
        this.projectBuildPluginVersion = new Node(pluginVersion, ranges);
    }

    public void setConnectorDependencies(Map<String, Node> connectorDependencies) {
        this.connectorDependencies.add(connectorDependencies);
    }

    public void setOtherDependencies(Map<String, Node> otherDependencies) {
        this.otherDependencies.add(otherDependencies);
    }

    public void setMiContainerPluginVersion(Node miContainerPluginVersion) {
        this.miContainerPluginVersion = miContainerPluginVersion;
    }

    public void setUnitTestPluginVersion(Node unitTestPluginVersion) {
        this.unitTestPluginVersion = unitTestPluginVersion;
    }

    public void setKeyStoreType(Node keyStoreType) {
        this.keyStoreType = keyStoreType;
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

    public void setCipherToolEnable(Node cipherToolEnable) {
        this.cipherToolEnable = cipherToolEnable;
    }

    public void setDockerFileBaseImage(Node dockerFileBaseImage) {
        this.dockerFileBaseImage = dockerFileBaseImage;
    }

    public void setDockerName(Node dockerName) {
        this.dockerName = dockerName;
    }

    public void setSkipTest(Node skipTest) {
        this.skipTest = skipTest;
    }

    public void setServerType(Node serverType) {
        this.serverType = serverType;
    }

    public void setServerHost(Node serverHost) {
        this.serverHost = serverHost;
    }

    public void setServerPort(Node serverPort) {
        this.serverPort = serverPort;
    }

    public void setServerPath(Node serverPath) {
        this.serverPath = serverPath;
    }

    public void setServerVersion(Node serverVersion) {
        this.serverVersion = serverVersion;
    }

    public void setServerDownloadLink(Node serverDownloadLink) {
        this.serverDownloadLink = serverDownloadLink;
    }

    public void setLastDependencyEndTagRange(Range lastDependencyEndTagRange) {
        this.lastDependencyEndTagRange = lastDependencyEndTagRange;
    }

    public void setConfig(Node config) {
        this.configs.add(config);
    }
}
