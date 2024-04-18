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
import org.eclipse.lemminx.customservice.synapse.utils.Constant;
import org.eclipse.lemminx.customservice.synapse.utils.LegacyConfigFinder;
import org.eclipse.lemminx.customservice.synapse.utils.Utils;
import org.eclipse.lemminx.dom.DOMDocument;
import org.eclipse.lemminx.dom.DOMElement;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResourceFinder {

    private static final Logger LOGGER = Logger.getLogger(ResourceFinder.class.getName());
    private static final String ARTIFACTS = "ARTIFACTS";
    private static final String REGISTRY = "REGISTRY";
    private static final List<String> resourceFromRegistryOnly = List.of("dataMapper", "js", "json", "smooksConfig",
            "wsdl", "ws_policy", "xsd", "xsl", "xslt", "yaml");

    // This has the extension mapping for the non-xml types in the registry
    private static final Map<String, String> nonXmlTypeToExtensionMap = new HashMap<>();

    // This has the xml tag mapping for each artifact type
    private static final Map<String, String> typeToXmlTagMap = new HashMap<>();

    static {
        // Populate the type to extension map
        nonXmlTypeToExtensionMap.put("dataMapper", ".dmc");
        nonXmlTypeToExtensionMap.put("js", ".js");
        nonXmlTypeToExtensionMap.put("json", ".json");
        nonXmlTypeToExtensionMap.put("wsdl", ".wsdl");
        nonXmlTypeToExtensionMap.put("xsd", ".xsd");
        nonXmlTypeToExtensionMap.put("xsl", ".xsl");
        nonXmlTypeToExtensionMap.put("xslt", ".xslt");
        nonXmlTypeToExtensionMap.put("yaml", ".yaml");

        // Populate the type to xml tag map
        typeToXmlTagMap.put("api", "api");
        typeToXmlTagMap.put("endpoint", "endpoint");
        typeToXmlTagMap.put("sequence", "sequence");
        typeToXmlTagMap.put("messageStore", "messageStore");
        typeToXmlTagMap.put("messageProcessor", "messageProcessor");
        typeToXmlTagMap.put("endpointTemplate", "template");
        typeToXmlTagMap.put("sequenceTemplate", "template");
        typeToXmlTagMap.put("task", "task");
        typeToXmlTagMap.put("localEntry", "localEntry");
        typeToXmlTagMap.put("dataService", "data");
        typeToXmlTagMap.put("dataSource", "dataSource");
        typeToXmlTagMap.put("ws_policy", "wsp:Policy");
        typeToXmlTagMap.put("smooksConfig", "smooks-resource-list");
        typeToXmlTagMap.put("proxyService", "proxy");
    }

    public static ResourceResponse getAvailableResources(String uri, String resourceType) {

        ResourceResponse response = null;
        Boolean isLegacyProject = Utils.isLegacyProject(uri);
        try {
            String projectRootPath = Utils.findProjectRootPath(uri, isLegacyProject);
            if (projectRootPath != null) {
                response = findResources(projectRootPath, resourceType, isLegacyProject);
            }
        } catch (IOException e) {
            LOGGER.warning("Error while finding project root path");
        }
        return response;
    }

    private static ResourceResponse findResources(String projectPath, String type, Boolean isLegacyProject) {

        if (isLegacyProject) {
            return findResourcesInLegacyProject(projectPath, type);
        }
        return findResources(projectPath, type);
    }

    private static ResourceResponse findResources(String projectPath, String type) {

        ResourceResponse response = new ResourceResponse();
        if (!resourceFromRegistryOnly.contains(type)) {
            Path artifactsPath = Path.of(projectPath, "src", "main", "wso2mi", "artifacts");
            List<Resource> resourcesInArtifacts = findResourceInArtifacts(artifactsPath, type);
            response.setResources(resourcesInArtifacts);
        }
        Path registryPath = Path.of(projectPath, "src", "main", "wso2mi", "resources", "registry");
        List<Resource> resourcesInRegistry = findResourceInRegistry(registryPath, type);
        response.setRegistryResources(resourcesInRegistry);

        return response;
    }

    private static ResourceResponse findResourcesInLegacyProject(String projectPath, String type) {

        ResourceResponse response = new ResourceResponse();
        List<Resource> resourcesInArtifacts = new ArrayList<>();
        List<Resource> resourcesInRegistry = new ArrayList<>();
        try {
            List<String> esbConfigPaths = LegacyConfigFinder.getConfigPaths(projectPath, ProjectType.ESB_CONFIGS.value);
            for (String esbConfigPath : esbConfigPaths) {
                Path artifactPath = Path.of(esbConfigPath, "src", "main", "synapse-config");
                List<Resource> resourceInArtifacts = findResourceInArtifacts(artifactPath, type);
                resourcesInArtifacts.addAll(resourceInArtifacts);
            }
            List<String> registryConfigPaths = LegacyConfigFinder.getConfigPaths(projectPath,
                    ProjectType.REGISTRY_RESOURCE.value);
            for (String registryConfigPath : registryConfigPaths) {
                Path registryPath = Path.of(registryConfigPath);
                List<Resource> resourceInRegistry = findResourceInRegistry(registryPath, type);
                resourcesInRegistry.addAll(resourceInRegistry);
            }
        } catch (IOException e) {
            LOGGER.warning("Error while finding resources in legacy project");
        }
        response.setResources(resourcesInArtifacts);
        response.setRegistryResources(resourcesInRegistry);
        return response;
    }

    private static List<Resource> findResourceInArtifacts(Path artifactsPath, String type) {

        List<Resource> resources = new ArrayList<>();
        String resourceTypeFolder = getArtifactFolder(type);
        if (resourceTypeFolder != null) {
            Path resourceFolderPath = Path.of(artifactsPath.toString(), resourceTypeFolder);
            File folder = new File(resourceFolderPath.toString());
            File[] listOfFiles = folder.listFiles();
            if (listOfFiles != null) {
                List<Resource> resources1 = createResources(List.of(listOfFiles), type, ARTIFACTS);
                resources.addAll(resources1);
            }
        }
        return resources;
    }

    private static List<Resource> findResourceInRegistry(Path registryPath, String type) {

        List<Resource> resources = new ArrayList<>();
        File folder = registryPath.toFile();
        traverseFolder(folder, type, resources);
        return resources;
    }

    private static void traverseFolder(File folder, String type, List<Resource> resources) {

        File[] listOfFiles = folder.listFiles();
        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isDirectory()) {
                    traverseFolder(file, type, resources);
                } else if (file.isFile()) {
                    if (nonXmlTypeToExtensionMap.containsKey(type)) {
                        String extension = nonXmlTypeToExtensionMap.get(type);
                        if (file.getName().endsWith(extension)) {
                            Resource resource = createNonXmlResource(file, type, REGISTRY);
                            if (resource != null) {
                                resources.add(resource);
                            }
                        }
                    } else if (Utils.isXml(file)) {
                        Resource resource = createResource(file, type, REGISTRY);
                        if (resource != null) {
                            resources.add(resource);
                        }
                    }
                }

            }
        }
    }

    private static String getArtifactFolder(String type) {

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
        } else if (Constant.DATA_SERVICE.equalsIgnoreCase(type)) {
            return "data-services";
        } else if (Constant.DATA_SOURCE.equalsIgnoreCase(type)) {
            return "data-sources";
        } else if (Constant.PROXY_SERVICE.equalsIgnoreCase(type)) {
            return "proxy-services";
        }
        return null;
    }

    private static List<Resource> createResources(List<File> files, String type, String from) {

        List<Resource> resources = new ArrayList<>();
        for (File file : files) {
            Resource resource = createResource(file, type, from);
            if (resource != null) {
                resources.add(resource);
            }
        }
        return resources;
    }

    private static Resource createNonXmlResource(File file, String type, String registry) {

        Resource resource = new RegistryResource();
        resource.setName(file.getName());
        resource.setType(type.toUpperCase());
        resource.setFrom(registry);
        ((RegistryResource) resource).setRegistryPath(file.getAbsolutePath());
        ((RegistryResource) resource).setRegistryKey(getRegistryKey(file));
        return resource;
    }

    private static Resource createResource(File file, String type, String from) {

        try {
            DOMDocument document = Utils.getDOMDocument(file);
            DOMElement rootElement;
            String nodeName = typeToXmlTagMap.get(type);
            rootElement = (DOMElement) Utils.getChildNodeByName(document, nodeName);
            if (rootElement != null && checkValid(rootElement, type)) {
                Resource resource = null;
                if (ARTIFACTS.equals(from)) {
                    resource = createArtifactResource(file, rootElement, type);
                } else if (REGISTRY.equals(from)) {
                    resource = createRegistryResource(file, rootElement, type);
                }
                return resource;
            }
        } catch (IOException e) {
            LOGGER.warning("Error while reading file: " + file.getName() + " to create resource object");
        }
        return null;
    }

    private static boolean checkValid(DOMElement rootElement, String type) {

        String nodeName = rootElement.getNodeName();
        if (Constant.TEMPLATE.equals(nodeName)) {
            if ("sequenceTemplate".equals(type)) {
                DOMElement sequenceElement = (DOMElement) Utils.getChildNodeByName(rootElement, Constant.SEQUENCE);
                if (sequenceElement != null) {
                    return true;
                }
            } else if ("endpointTemplate".equals(type)) {
                DOMElement endpointElement = (DOMElement) Utils.getChildNodeByName(rootElement, Constant.ENDPOINT);
                if (endpointElement != null) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    private static Resource createArtifactResource(File file, DOMElement rootElement, String type) {

        Resource artifact = new ArtifactsResource();
        String name = getArtifactName(rootElement);
        if (name != null) {
            artifact.setName(name);
            artifact.setType(Utils.addUnderscoreBetweenWords(type).toUpperCase());
            artifact.setFrom(ARTIFACTS);
            ((ArtifactsResource) artifact).setArtifactPath(file.getName());
            return artifact;
        }
        return null;
    }

    private static Resource createRegistryResource(File file, DOMElement rootElement, String type) {

        Resource registry = new RegistryResource();
        String name = getArtifactName(rootElement);
        if (name != null) {
            registry.setName(name);
            registry.setType(Utils.addUnderscoreBetweenWords(type).toUpperCase());
            registry.setFrom(REGISTRY);
            ((RegistryResource) registry).setRegistryPath(file.getAbsolutePath());
            ((RegistryResource) registry).setRegistryKey(getRegistryKey(file));
            return registry;
        }
        return null;
    }

    private static String getArtifactName(DOMElement rootElement) {

        if (rootElement.hasAttribute(Constant.NAME)) {
            return rootElement.getAttribute(Constant.NAME);
        } else if (rootElement.hasAttribute(Constant.KEY)) {
            return rootElement.getAttribute(Constant.KEY);
        }
        return null;
    }

    private static String getRegistryKey(File file) {

        String pattern = "(.*)(\\b(gov|conf)\\b)(.*)";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(file.getAbsolutePath());

        if (m.find()) {
            String type = m.group(3);
            String path = m.group(4).replaceAll("^/+", "");
            return type + ":" + path;
        } else {
            return null;
        }
    }
}
