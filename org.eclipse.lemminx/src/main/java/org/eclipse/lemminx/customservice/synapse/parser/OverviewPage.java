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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.lemminx.customservice.synapse.dependency.tree.DependencyScanner;
import org.eclipse.lemminx.customservice.synapse.dependency.tree.pojo.ConnectorDependency;
import org.eclipse.lemminx.customservice.synapse.dependency.tree.pojo.Dependency;
import org.eclipse.lemminx.customservice.synapse.dependency.tree.pojo.DependencyTree;
import org.eclipse.lemminx.customservice.synapse.directoryTree.DirectoryMapResponse;
import org.eclipse.lemminx.customservice.synapse.directoryTree.DirectoryTreeBuilder;
import org.eclipse.lemminx.customservice.synapse.parser.config.ConfigParser;
import org.eclipse.lemminx.customservice.synapse.utils.Constant;
import org.eclipse.lemminx.customservice.synapse.utils.Utils;
import org.eclipse.lsp4j.WorkspaceFolder;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.eclipse.lemminx.customservice.synapse.parser.pom.PomParser.getPomDetails;

public class OverviewPage {

    private static final Logger LOGGER = Logger.getLogger(OverviewPage.class.getName());
    private static final List<String> AI_AGENT_SUPPORTED_ARTIFACTS = List.of(Constant.API_ARTIFACTS, Constant.SEQUENCE);

    public static OverviewPageDetailsResponse getDetails(String projectUri) {
        OverviewPageDetailsResponse pomDetailsResponse = new OverviewPageDetailsResponse();
        getPomDetails(projectUri, pomDetailsResponse);
        pomDetailsResponse.setConfigurables(ConfigParser.getConfigDetails(projectUri));
        return pomDetailsResponse;
    }

    public static List<String> getProjectIntegrationType(WorkspaceFolder projectFolder) {
        List<String> integrationTypes = new ArrayList<>();
        DirectoryMapResponse directoryMap = DirectoryTreeBuilder.getProjectExplorerModel(projectFolder);
        if (directoryMap != null) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(directoryMap.getDirectoryMap().getAsJsonObject().toString());
                JsonNode artifacts = root.path(Constant.SRC).path(Constant.MAIN).path(Constant.WSO2MI).path(Constant.ARTIFACTS);

                if (artifacts.has(Constant.API_ARTIFACTS) && !artifacts.path(Constant.API_ARTIFACTS).isEmpty()) {
                    integrationTypes.add(Constant.DEVANT_API);
                }

                if (artifacts.has(Constant.EVENT_INTEGRATIONS) && !artifacts.path(Constant.EVENT_INTEGRATIONS).isEmpty()) {
                    integrationTypes.add(Constant.DEVANT_EVENT);
                }

                if (artifacts.has(Constant.OTHER_ARTIFACTS) && artifacts.path(Constant.OTHER_ARTIFACTS).has(Constant.SEQUENCE_ARTIFACTS)) {
                    JsonNode sequences = artifacts.path(Constant.OTHER_ARTIFACTS).path(Constant.SEQUENCE_ARTIFACTS);
                    if (sequences.isArray()) {
                        for (JsonNode sequence : sequences) {
                            if (sequence.has(Constant.IS_MAIN_SEQUENCE) && sequence.path(Constant.IS_MAIN_SEQUENCE).asBoolean()) {
                                integrationTypes.add(Constant.DEVANT_AUTOMATION);
                                break;
                            }
                        }
                    }
                }

                if (hasAIAgent(projectFolder, artifacts)) {
                    integrationTypes.add(Constant.AI_AGENT);
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error occurred while checking project integration type.", e);
            }
        }
        return integrationTypes;
    }

    private static boolean hasAIAgent(WorkspaceFolder projectFolder, JsonNode artifacts) {

        String projectPath = Utils.getAbsolutePath(projectFolder.getUri());
        if (StringUtils.isEmpty(projectPath)) {
            return false;
        }
        DependencyScanner dependencyScanner = new DependencyScanner(projectPath);

        for (String artifactType : AI_AGENT_SUPPORTED_ARTIFACTS) {
            if (hasAIAgent(dependencyScanner, artifacts, artifactType)) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasAIAgent(DependencyScanner dependencyScanner, JsonNode artifacts, String artifactType) {

        if (!artifacts.has(artifactType) || artifacts.path(artifactType).isEmpty()) {
            return false;
        }
        JsonNode artifactList = artifacts.path(artifactType);
        for (JsonNode apiArtifact : artifactList) {
            String path = apiArtifact.path(Constant.PATH).asText();
            DependencyTree dependencyTree = dependencyScanner.analyzeArtifact(path);
            boolean result = dependencyTree.getDependencyList()
                    .stream()
                    .anyMatch(OverviewPage::isAIAgent);
            if (result) {
                return true;
            }
        }
        return false;
    }

    private static boolean isAIAgent(Dependency dependency) {

        return dependency instanceof ConnectorDependency &&
                Constant.AI.equalsIgnoreCase(dependency.getName()) &&
                Constant.AGENT.equalsIgnoreCase(((ConnectorDependency) dependency).getOperationName());
    }
}
