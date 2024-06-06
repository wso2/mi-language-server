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

package org.eclipse.lemminx.customservice.synapse.resourceFinder;

import org.eclipse.lemminx.customservice.synapse.directoryTree.legacyBuilder.utils.ProjectType;
import org.eclipse.lemminx.customservice.synapse.resourceFinder.pojo.RequestedResource;
import org.eclipse.lemminx.customservice.synapse.resourceFinder.pojo.Resource;
import org.eclipse.lemminx.customservice.synapse.resourceFinder.pojo.ResourceResponse;
import org.eclipse.lemminx.customservice.synapse.utils.Constant;
import org.eclipse.lemminx.customservice.synapse.utils.LegacyConfigFinder;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class OldProjectResourceFinder extends AbstractResourceFinder {

    private static final Logger LOGGER = Logger.getLogger(OldProjectResourceFinder.class.getName());

    @Override
    protected ResourceResponse findResources(String projectPath, List<RequestedResource> types) {

        ResourceResponse response = new ResourceResponse();
        List<Resource> resourcesInArtifacts = new ArrayList<>();
        List<Resource> resourcesInRegistry = new ArrayList<>();
        try {
            List<String> esbConfigPaths = LegacyConfigFinder.getConfigPaths(projectPath, ProjectType.ESB_CONFIGS.value);
            for (String esbConfigPath : esbConfigPaths) {
                Path artifactPath = Path.of(esbConfigPath, "src", "main", "synapse-config");
                List<Resource> resourceInArtifacts = findResourceInArtifacts(artifactPath, types);
                resourcesInArtifacts.addAll(resourceInArtifacts);
            }
            List<String> registryConfigPaths = LegacyConfigFinder.getConfigPaths(projectPath,
                    ProjectType.REGISTRY_RESOURCE.value);
            for (String registryConfigPath : registryConfigPaths) {
                Path registryPath = Path.of(registryConfigPath);
                List<Resource> resourceInRegistry = findResourceInRegistry(registryPath, types);
                resourcesInRegistry.addAll(resourceInRegistry);
            }
        } catch (IOException e) {
            LOGGER.warning("Error while finding resources in legacy project");
        }
        response.setResources(resourcesInArtifacts);
        response.setRegistryResources(resourcesInRegistry);
        return response;
    }

    @Override
    protected String getArtifactFolder(String type) {

        if (Constant.ENDPOINT.equalsIgnoreCase(type)) {
            return "endpoints";
        } else if (Constant.SEQUENCE.equalsIgnoreCase(type)) {
            return "sequences";
        } else if (Constant.MESSAGE_STORE.equalsIgnoreCase(type)) {
            return "message-stores";
        } else if (Constant.MESSAGE_PROCESSOR.equalsIgnoreCase(type)) {
            return "message-processors";
        } else if ("endpointTemplate".equalsIgnoreCase(type)) {
            return "templates";
        } else if ("sequenceTemplate".equalsIgnoreCase(type)) {
            return "templates";
        } else if (Constant.TASK.equalsIgnoreCase(type)) {
            return "tasks";
        } else if (Constant.LOCAL_ENTRY.equalsIgnoreCase(type)) {
            return "local-entries";
        } else if (Constant.PROXY_SERVICE.equalsIgnoreCase(type)) {
            return "proxy-services";
        }
        return null;
    }
}
