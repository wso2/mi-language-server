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

public class OverviewPageDetailsResponse {

    private PrimaryDetails primaryDetails;
    private BuildDetails buildDetails;
    private DependenciesDetails dependencies;
    private UnitTestDetails unitTest;
    private List<Node> configurables;

    public OverviewPageDetailsResponse() {
        primaryDetails = new PrimaryDetails();
        buildDetails = new BuildDetails();
        dependencies = new DependenciesDetails();
        unitTest = new UnitTestDetails();
        configurables = new ArrayList<>();
    }

    public void setRuntimeVersion(Node runtimeVersion) {
        this.primaryDetails.setRuntimeVersion(runtimeVersion);
    }

    public void setProjectGroupId(Node projectGroupId) {
        this.buildDetails.advanceDetails.setProjectGroupId(projectGroupId);
    }

    public void setProjectName(Node projectName) {
        this.primaryDetails.setProjectName(projectName);
    }

    public void setProjectArtifactId(Node projectArtifactId) {
        this.buildDetails.advanceDetails.setProjectArtifactId(projectArtifactId);
    }

    public void setProjectVersion(Node projectVersion) {
        this.primaryDetails.setProjectVersion(projectVersion);
    }

    public void setProjectDescription(Node projectDescription) {
        this.primaryDetails.setProjectDescription(projectDescription);
    }

    public void setProjectBuildPluginVersion(String pluginVersion, Range range) {
        this.buildDetails.advanceDetails.getPluginDetails().setProjectBuildPluginVersion(pluginVersion, range);
    }

    public void setConnectorDependencies(DependencyDetails connectorDependencies) {
        this.dependencies.addConnectorDependencies(connectorDependencies);
    }

    public void setOtherDependencies(DependencyDetails otherDependencies) {
        this.dependencies.addOtherDependencies(otherDependencies);
    }

    public void setMiContainerPluginVersion(Node miContainerPluginVersion) {
        this.buildDetails.advanceDetails.getPluginDetails().setMiContainerPluginVersion(miContainerPluginVersion);
    }

    public void setUnitTestPluginVersion(Node unitTestPluginVersion) {
        this.buildDetails.advanceDetails.getPluginDetails().setUnitTestPluginVersion(unitTestPluginVersion);
    }

    public void setKeyStoreType(Node keyStoreType) {
        this.buildDetails.dockerDetails.setKeyStoreType(keyStoreType);
    }

    public void setKeyStoreName(Node keyStoreName) {
        this.buildDetails.dockerDetails.setKeyStoreName(keyStoreName);
    }

    public void setKeyStorePassword(Node keyStorePassword) {
        this.buildDetails.dockerDetails.setKeyStorePassword(keyStorePassword);
    }

    public void setKeyStoreAlias(Node keyStoreAlias) {
        this.buildDetails.dockerDetails.setKeyStoreAlias(keyStoreAlias);
    }

    public void setCipherToolEnable(Node cipherToolEnable) {
        this.buildDetails.dockerDetails.setCipherToolEnable(cipherToolEnable);
    }

    public void setDockerFileBaseImage(Node dockerFileBaseImage) {
        this.buildDetails.dockerDetails.setDockerFileBaseImage(dockerFileBaseImage);
    }

    public void setDockerName(Node dockerName) {
        this.buildDetails.dockerDetails.setDockerName(dockerName);
    }

    public void setSkipTest(Node skipTest) {
        this.unitTest.setSkipTest(skipTest);
    }

    public void setServerType(Node serverType) {
        this.unitTest.setServerType(serverType);
    }

    public void setServerHost(Node serverHost) {
        this.unitTest.setServerHost(serverHost);
    }

    public void setServerPort(Node serverPort) {
        this.unitTest.setServerPort(serverPort);
    }

    public void setServerPath(Node serverPath) {
        this.unitTest.setServerPath(serverPath);
    }

    public void setServerVersion(Node serverVersion) {
        this.unitTest.setServerVersion(serverVersion);
    }

    public void setServerDownloadLink(Node serverDownloadLink) {
        this.unitTest.setServerDownloadLink(serverDownloadLink);
    }

    public void setConfig(Node config) {
        this.configurables.add(config);
    }
}
