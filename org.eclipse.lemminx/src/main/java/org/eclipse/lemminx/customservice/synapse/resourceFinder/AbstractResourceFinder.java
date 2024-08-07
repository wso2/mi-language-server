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

import org.eclipse.lemminx.customservice.synapse.resourceFinder.pojo.ArtifactResource;
import org.eclipse.lemminx.customservice.synapse.resourceFinder.pojo.RegistryResource;
import org.eclipse.lemminx.customservice.synapse.resourceFinder.pojo.RequestedResource;
import org.eclipse.lemminx.customservice.synapse.resourceFinder.pojo.Resource;
import org.eclipse.lemminx.customservice.synapse.resourceFinder.pojo.ResourceResponse;
import org.eclipse.lemminx.customservice.synapse.resourceFinder.registryHander.NonXMLRegistryHandler;
import org.eclipse.lemminx.customservice.synapse.resourceFinder.registryHander.SchemaResourceHandler;
import org.eclipse.lemminx.customservice.synapse.resourceFinder.registryHander.SimpleResourceHandler;
import org.eclipse.lemminx.customservice.synapse.resourceFinder.registryHander.SwaggerResourceHandler;
import org.eclipse.lemminx.customservice.synapse.utils.Constant;
import org.eclipse.lemminx.customservice.synapse.utils.Utils;
import org.eclipse.lemminx.dom.DOMDocument;
import org.eclipse.lemminx.dom.DOMElement;
import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

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

public abstract class AbstractResourceFinder {

    private static final Logger LOGGER = Logger.getLogger(AbstractResourceFinder.class.getName());
    private static final String ARTIFACTS = "ARTIFACTS";
    private static final String REGISTRY = "REGISTRY";
    private static final String LOCAL_ENTRY = "LOCAL_ENTRY";
    protected static final List<String> resourceFromRegistryOnly = List.of("dataMapper", "js", "json", "smooksConfig",
            "wsdl", "ws_policy", "xsd", "xsl", "xslt", "yaml", "registry", "schema", "swagger");

    // This has the xml tag mapping for each artifact type
    private static final Map<String, String> typeToXmlTagMap = new HashMap<>();

    static {

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
        typeToXmlTagMap.put("xsl", "xsl:stylesheet");
        typeToXmlTagMap.put("xslt", "xsl:stylesheet");
        typeToXmlTagMap.put("xsd", "xs:schema");
        typeToXmlTagMap.put("wsdl", "wsdl:definitions");
        typeToXmlTagMap.put("ws_policy", "wsp:Policy");
    }

    public ResourceResponse getAvailableResources(String uri, Either<String, List<RequestedResource>> resourceTypes) {

        ResourceResponse response = null;
        if (uri != null) {
            if (resourceTypes.isLeft()) {
                response = findResources(uri, resourceTypes.getLeft());
            } else {
                response = findResources(uri, resourceTypes.getRight());
            }
        }
        return response;
    }

    private ResourceResponse findResources(String projectPath, String type) {

        RequestedResource requestedResource = new RequestedResource();
        requestedResource.type = type;
        requestedResource.needRegistry = true;
        return findResources(projectPath, List.of(requestedResource));
    }

    protected abstract ResourceResponse findResources(String projectPath, List<RequestedResource> type);

    protected List<Resource> findResourceInArtifacts(Path artifactsPath, List<RequestedResource> types) {

        List<Resource> resources = new ArrayList<>();
        for (RequestedResource requestedResource : types) {
            if (!resourceFromRegistryOnly.contains(requestedResource.type)) {
                String type = requestedResource.type;
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
            }
        }
        return resources;
    }

    protected List<Resource> findResourceInLocalEntry(Path localEntryPath, List<RequestedResource> types) {

        List<Resource> resources = new ArrayList<>();
        File folder = localEntryPath.toFile();

        if (folder.exists()) {
            for (RequestedResource requestedResource : types) {
                File[] listOfFiles = folder.listFiles();
                if (listOfFiles != null) {
                    List<Resource> resources1 = createResources(List.of(listOfFiles), requestedResource.type,
                            LOCAL_ENTRY);
                    resources.addAll(resources1);
                }

            }
        }
        return resources;
    }

    protected abstract String getArtifactFolder(String type);

    protected List<Resource> findResourceInRegistry(Path registryPath, List<RequestedResource> requestedResources) {

        List<Resource> resources = new ArrayList<>();
        File folder = registryPath.toFile();
        boolean isRegistryTypeRequested =
                requestedResources.stream().anyMatch(requestedResource -> "registry".equals(requestedResource.type));
        if (isRegistryTypeRequested) {
            traverseFolder(folder, null, null, resources);
        } else {
            HashMap<String, String> requestedTypeToXmlTagMap = getRequestedTypeToXmlTagMap(requestedResources);
            NonXMLRegistryHandler nonXMLRegistryHandler = getNonXMLRegistryHandler(requestedResources, resources);
            traverseFolder(folder, requestedTypeToXmlTagMap, nonXMLRegistryHandler, resources);
        }
        return resources;
    }

    private NonXMLRegistryHandler getNonXMLRegistryHandler(List<RequestedResource> requestedResources,
                                                           List<Resource> resources) {

        NonXMLRegistryHandler handler = null;
        if (hasRequestedResourceOfType(requestedResources, "swagger")) {
            handler = new SwaggerResourceHandler(resources);
        }
        if (hasRequestedResourceOfType(requestedResources, "schema")) {
            if (handler == null) {
                handler = new SchemaResourceHandler(resources);
            } else {
                handler.setNextHandler(new SchemaResourceHandler(resources));
            }
        }
        for (RequestedResource requestedResource : requestedResources) {
            if (requestedResource.type.equals("schema") || requestedResource.type.equals("swagger")) {
                continue;
            }
            if (handler == null) {
                handler = new SimpleResourceHandler(requestedResources, resources);
            } else {
                handler.setNextHandler(new SimpleResourceHandler(requestedResources, resources));
            }
            break;
        }
        return handler;
    }

    private boolean hasRequestedResourceOfType(List<RequestedResource> requestedResources, String type) {

        return requestedResources.stream()
                .anyMatch(requestedResource -> type.equals(requestedResource.type) && requestedResource.needRegistry);
    }

    private HashMap<String, String> getRequestedTypeToXmlTagMap(List<RequestedResource> requestedResources) {

        HashMap<String, String> requestedTypeToXmlTagMap = new HashMap<>();
        requestedResources.forEach(requestedResource -> {
            String type = requestedResource.type;
            String xmlTag = typeToXmlTagMap.get(type);
            if (xmlTag != null && requestedResource.needRegistry) {
                requestedTypeToXmlTagMap.put(type, xmlTag);
            }
        });
        return requestedTypeToXmlTagMap;
    }

    private void traverseFolder(File folder, HashMap<String, String> requestedTypeToXmlTagMap,
                                NonXMLRegistryHandler handler, List<Resource> resources) {

        File[] listOfFiles = folder.listFiles();
        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isDirectory()) {
                    if (!".meta".equals(file.getName())) {
                        traverseFolder(file, requestedTypeToXmlTagMap, handler, resources);
                    }
                } else if (file.isFile() && isFileInRegistry(file)) {

                    if (handler == null && requestedTypeToXmlTagMap == null) {
                        Resource resource = createNonXmlResource(file, Constant.REGISTRY, REGISTRY);
                        if (resource != null) {
                            resources.add(resource);
                        }
                        continue;
                    }
                    Pattern pattern = Pattern.compile(".*\\.(.*)$");
                    Matcher matcher = pattern.matcher(file.getName());
                    if (matcher.find()) {
                        String fileExtension = matcher.group(1);
                        if (Constant.XML.equals(fileExtension)) {
                            Resource resource = createResource(file, requestedTypeToXmlTagMap, REGISTRY);
                            if (resource != null) {
                                resources.add(resource);
                            }
                        } else {
                            handler.handleFile(file);
                        }
                    }
                }
            }
        }
    }

    private boolean isFileInRegistry(File file) {

        return file.getAbsolutePath().contains(Constant.GOV) || file.getAbsolutePath().contains(Constant.CONF);
    }

    private Resource createResource(File file, HashMap<String, String> requestedTypeToXmlTagMap, String from) {

        try {
            DOMDocument document = Utils.getDOMDocument(file);
            if (document != null && document.getDocumentElement() != null) {
                DOMElement rootElement = Utils.getRootElement(document);
                if (rootElement != null) {
                    String type = rootElement.getNodeName();
                    if (type != null && requestedTypeToXmlTagMap.containsValue(type)) {
                        Resource resource = null;
                        if (ARTIFACTS.equals(from)) {
                            resource = createArtifactResource(file, rootElement, type, Boolean.FALSE);
                        } else if (REGISTRY.equals(from)) {
                            resource = createRegistryResource(file, rootElement, type);
                        }
                        return resource;
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.warning("Error while reading file: " + file.getName() + " to create resource object");
        }
        return null;
    }

    private List<Resource> createResources(List<File> files, String type, String from) {

        List<Resource> resources = new ArrayList<>();
        for (File file : files) {
            Resource resource = createResource(file, type, from);
            if (resource != null) {
                resources.add(resource);
            }
        }
        return resources;
    }

    private Resource createResource(File file, String type, String from) {

        try {
            DOMDocument document = Utils.getDOMDocument(file);
            DOMElement rootElement;
            String nodeName;
            if (LOCAL_ENTRY.equals(from)) {
                nodeName = Constant.LOCAL_ENTRY;
            } else {
                nodeName = typeToXmlTagMap.get(type);
            }
            rootElement = (DOMElement) Utils.getChildNodeByName(document, nodeName);
            if (rootElement != null && checkValid(rootElement, type, from)) {
                Resource resource = null;
                if (ARTIFACTS.equals(from)) {
                    resource = createArtifactResource(file, rootElement, type, Boolean.FALSE);
                } else if (REGISTRY.equals(from)) {
                    resource = createRegistryResource(file, rootElement, type);
                } else if (LOCAL_ENTRY.equals(from)) {
                    resource = createArtifactResource(file, rootElement, type, Boolean.TRUE);
                }
                return resource;
            }
        } catch (IOException e) {
            LOGGER.warning("Error while reading file: " + file.getName() + " to create resource object");
        }
        return null;
    }

    private Resource createNonXmlResource(File file, String type, String registry) {

        Resource resource = new RegistryResource();
        resource.setName(file.getName());
        resource.setType(type.toUpperCase());
        resource.setFrom(registry);
        ((RegistryResource) resource).setRegistryPath(file.getAbsolutePath());
        ((RegistryResource) resource).setRegistryKey(getRegistryKey(file));
        return resource;
    }

    private boolean checkValid(DOMElement rootElement, String type, String from) {

        String nodeName = rootElement.getNodeName();
        if (LOCAL_ENTRY.equals(from)) {
            String xmlTag = typeToXmlTagMap.containsKey(type) ? typeToXmlTagMap.get(type) : type;
            DOMElement artifactElt = Utils.getFirstElement(rootElement);
            if (artifactElt != null) {
                String artifactType = artifactElt.getNodeName();
                return xmlTag.equals(artifactType);
            }
            return false;
        } else if (Constant.TEMPLATE.equals(nodeName)) {
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

    private Resource createArtifactResource(File file, DOMElement rootElement, String type, boolean isLocalEntry) {

        Resource artifact = new ArtifactResource();
        String name = getArtifactName(rootElement);
        if (name != null) {
            artifact.setName(name);
            artifact.setType(Utils.addUnderscoreBetweenWords(type).toUpperCase());
            artifact.setFrom(ARTIFACTS);
            ((ArtifactResource) artifact).setLocalEntry(isLocalEntry);
            ((ArtifactResource) artifact).setArtifactPath(file.getName());
            ((ArtifactResource) artifact).setAbsolutePath(file.getAbsolutePath());
            return artifact;
        }
        return null;
    }

    private Resource createRegistryResource(File file, DOMElement rootElement, String type) {

        Resource registry = new RegistryResource();
        String name = getArtifactName(rootElement);
        if (name == null) {
            name = file.getName();
        }
        registry.setName(name);
        type = type.replace(":", "");
        registry.setType(Utils.addUnderscoreBetweenWords(type).toUpperCase());
        registry.setFrom(REGISTRY);
        ((RegistryResource) registry).setRegistryPath(file.getAbsolutePath());
        ((RegistryResource) registry).setRegistryKey(getRegistryKey(file));
        return registry;
    }

    private String getArtifactName(DOMElement rootElement) {

        if (isApiArtifact(rootElement)) {
            return getApiArtifactName(rootElement);
        } else {
            return getNonApiArtifactName(rootElement);
        }
    }

    private boolean isApiArtifact(DOMElement rootElement) {

        return Constant.API.equalsIgnoreCase(rootElement.getNodeName());
    }

    private String getApiArtifactName(DOMElement rootElement) {

        StringBuilder name = new StringBuilder();
        name.append(rootElement.getAttribute(Constant.NAME));
        if (rootElement.hasAttribute(Constant.VERSION)) {
            name.append(":v").append(rootElement.getAttribute(Constant.VERSION));
        }
        return name.toString();
    }

    private String getNonApiArtifactName(DOMElement rootElement) {

        if (rootElement.hasAttribute(Constant.NAME)) {
            return rootElement.getAttribute(Constant.NAME);
        } else if (rootElement.hasAttribute(Constant.KEY)) {
            return rootElement.getAttribute(Constant.KEY);
        } else {
            DOMNode nameNode = Utils.getChildNodeByName(rootElement, Constant.NAME);
            if (nameNode != null) {
                return Utils.getInlineString(nameNode.getFirstChild());
            }
            return null;
        }
    }

    private String getRegistryKey(File file) {

        String pattern = "(.*)(\\b(gov|conf)\\b)(.*)";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(file.getAbsolutePath());

        if (m.find()) {
            String type = m.group(3);
            String path = m.group(4).replaceAll("\\\\", "/");
            path = path.replaceAll("^/+", "");
            return type + ":" + path;
        } else {
            return null;
        }
    }
}
