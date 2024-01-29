/*
 * Copyright (c) 2023, WSO2 LLC. (http://www.wso2.com).
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

package org.eclipse.lemminx.customservice.syntaxmodel.directoryTree;

import org.eclipse.lemminx.commons.TextDocument;
import org.eclipse.lemminx.customservice.syntaxmodel.utils.Constant;
import org.eclipse.lemminx.customservice.syntaxmodel.utils.Utils;
import org.eclipse.lemminx.dom.DOMDocument;
import org.eclipse.lemminx.dom.DOMElement;
import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.dom.DOMParser;
import org.eclipse.lsp4j.WorkspaceFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DirectoryTreeBuilder {

    private static final Logger LOGGER = Logger.getLogger(DirectoryTreeBuilder.class.getName());
    private static String projectPath;

    public static DirectoryMapResponse buildDirectoryTree(WorkspaceFolder workspaceFolder) {

        String currentPath = workspaceFolder.getUri();
        currentPath = currentPath.substring(7);
        DirectoryMap directoryMap = new DirectoryMap();

        String rootPath = findRootPath(currentPath);
        DirectoryTreeBuilder.projectPath = rootPath;
        analyze(projectPath, directoryMap);
        DirectoryMapResponse directoryMapResponse = new DirectoryMapResponse(directoryMap);
        return directoryMapResponse;
    }

    private static String findRootPath(String currentPath) {

        String prevFolderPath = currentPath.substring(0, currentPath.lastIndexOf(File.separator));
        String dotProjectPath = currentPath + Constant.FILE_SEPARATOR + Constant.DOT_PROJECT;
        File dotProjectFile = new File(dotProjectPath);
        if (dotProjectFile != null && dotProjectFile.exists()) {
            DOMDocument projectDOM = getDOMDocument(dotProjectFile);
            DOMNode descriptionNode = findDescriptionNode(projectDOM);
            if (descriptionNode != null) {
                DOMNode naturesNode = findNaturesNode(descriptionNode);
                if (naturesNode != null) {
                    List<DOMNode> children = naturesNode.getChildren();
                    for (DOMNode child : children) {
                        String nature = Utils.getInlineString(child.getFirstChild());
                        if (Constant.MAVEN_MULTI_MODULE_PROJECT.equalsIgnoreCase(nature)) {
                            return currentPath;
                        }
                    }
                    return findRootPath(prevFolderPath);
                }
            }
        } else {
            return findRootPath(prevFolderPath);
        }
        return null;
    }

    private static DOMNode findDescriptionNode(DOMDocument projectDOM) {

        DOMNode descriptionNode = null;
        for (int i = 0; i < projectDOM.getChildren().size(); i++) {
            String elementName = projectDOM.getChild(i).getNodeName();
            if (Constant.PROJECT_DESCRIPTION.equalsIgnoreCase(elementName)) {
                descriptionNode = projectDOM.getChild(i);
                break;
            }
        }
        return descriptionNode;
    }

    private static DOMNode findNaturesNode(DOMNode descriptionNode) {

        DOMNode naturesNode = null;
        for (int i = 0; i < descriptionNode.getChildren().size(); i++) {
            String elementName = descriptionNode.getChild(i).getNodeName();
            if (Constant.NATURES.equalsIgnoreCase(elementName)) {
                naturesNode = descriptionNode.getChild(i);
                break;
            }
        }
        return naturesNode;
    }

    private static void analyze(String projectPath, DirectoryMap directoryMap) {

        File folder = new File(projectPath);
        File[] listOfFiles = folder.listFiles(File::isDirectory);
        if (listOfFiles != null) {
            for (File subProject : listOfFiles) {
                analyzeByProjectType(subProject, directoryMap);
            }
        }
    }

    private static ProjectType analyzeByProjectType(File subProject, DirectoryMap directoryMap) {

        String projectFilePath = subProject.getAbsolutePath() + Constant.FILE_SEPARATOR + Constant.DOT_PROJECT;
        File projectFile = new File(projectFilePath);
        DOMDocument projectDOM = getDOMDocument(projectFile);
        DOMNode descriptionNode = findDescriptionNode(projectDOM);
        if (descriptionNode != null) {
            DOMNode naturesNode = findNaturesNode(descriptionNode);
            if (naturesNode != null) {
                List<DOMNode> children = naturesNode.getChildren();
                for (DOMNode child : children) {
                    String nature = Utils.getInlineString(child.getFirstChild());
                    if (ProjectType.DATA_SERVICE_CONFIGS.value.equalsIgnoreCase(nature)) {
                        directoryMap.addDataServiceConfig(ProjectType.DATA_SERVICE_CONFIGS.name(),
                                subProject.getName(), subProject.getAbsolutePath());
                        return ProjectType.DATA_SERVICE_CONFIGS;
                    } else if (ProjectType.ESB_CONFIGS.value.equalsIgnoreCase(nature)) {
                        analyzeEsbConfigs(subProject.getAbsolutePath() +
                                Constant.SYNAPSE_CONFIG_PATH, directoryMap);
                        return ProjectType.ESB_CONFIGS;
                    } else if (ProjectType.COMPOSITE_EXPORTER.value.equalsIgnoreCase(nature)) {
                        directoryMap.addCompositeExporter(ProjectType.COMPOSITE_EXPORTER.name(), subProject.getName()
                                , subProject.getAbsolutePath());
                        return ProjectType.COMPOSITE_EXPORTER;
                    } else if (ProjectType.CONNECTOR_EXPORTER.value.equalsIgnoreCase(nature)) {
                        directoryMap.addConnectorExporter(ProjectType.CONNECTOR_EXPORTER.name(), subProject.getName()
                                , subProject.getAbsolutePath());
                        return ProjectType.CONNECTOR_EXPORTER;
                    } else if (ProjectType.DATA_SOURCE_CONFIGS.value.equalsIgnoreCase(nature)) {
                        directoryMap.addDataSourceConfig(ProjectType.DATA_SOURCE_CONFIGS.name(), subProject.getName()
                                , subProject.getAbsolutePath());
                        return ProjectType.DATA_SOURCE_CONFIGS;
                    } else if (ProjectType.MEDIATOR_PROJECT.value.equalsIgnoreCase(nature)) {
                        directoryMap.addMediatorProject(ProjectType.MEDIATOR_PROJECT.name(), subProject.getName(),
                                subProject.getAbsolutePath());
                        return ProjectType.MEDIATOR_PROJECT;
                    } else if (ProjectType.REGISTRY_RESOURCE.value.equalsIgnoreCase(nature)) {
                        directoryMap.addRegistryResource(ProjectType.REGISTRY_RESOURCE.name(), subProject.getName(),
                                subProject.getAbsolutePath());
                        return ProjectType.REGISTRY_RESOURCE;
                    } else if (ProjectType.DOCKER_EXPORTER.value.equalsIgnoreCase(nature)) {
                        directoryMap.addDockerExporter(ProjectType.DOCKER_EXPORTER.name(), subProject.getName(),
                                subProject.getAbsolutePath());
                        return ProjectType.DOCKER_EXPORTER;
                    } else if (ProjectType.KUBERNETES_EXPORTER.value.equalsIgnoreCase(nature)) {
                        directoryMap.addKubernetesExporter(ProjectType.KUBERNETES_EXPORTER.name(),
                                subProject.getName(), subProject.getAbsolutePath());
                        return ProjectType.KUBERNETES_EXPORTER;
                    } else if (ProjectType.JAVA_LIBRARY_PROJECT.value.equalsIgnoreCase(nature)) {
                        directoryMap.addJavaLibraryProject(ProjectType.JAVA_LIBRARY_PROJECT.name(),
                                subProject.getName(), subProject.getAbsolutePath());
                        return ProjectType.JAVA_LIBRARY_PROJECT;
                    }
                }
            }
        }
        return null;
    }

    private static void analyzeEsbConfigs(String configPath, DirectoryMap directoryMap) {

        File folder = new File(configPath);
        File[] listOfFiles = folder.listFiles(File::isDirectory);
        if (listOfFiles != null) {
            for (File subFolder : listOfFiles) {
                try {
                    if (subFolder.isDirectory()) {
                        String type = subFolder.getName();
                        analyzeByType(directoryMap, subFolder, type);
                    }
                } catch (SecurityException e) {
                    LOGGER.log(Level.WARNING, "No read access to the file.", e);
                }
            }
        }
    }

    private static void analyzeByType(DirectoryMap directoryMap, File folder, String type) {

        try {
            File[] listOfFiles = folder.listFiles();
            if (listOfFiles != null) {
                for (File file : listOfFiles) {
                    if (file.isFile()) {
                        String name = file.getName();
                        String path = file.getAbsolutePath();
                        SimpleComponent advancedComponent = createEsbComponent(type, name, path);
                        directoryMap.addEsbComponent(type, advancedComponent);
                    }
                }
            }
        } catch (SecurityException e) {
            LOGGER.log(Level.WARNING, "No read access to the file.", e);
        }
    }

    private static SimpleComponent createEsbComponent(String type, String name, String path) {

        SimpleComponent component = new SimpleComponent(type, name, path);
        if (Constant.API.equalsIgnoreCase(type) || Constant.SEQUENCES.equalsIgnoreCase(type) ||
                Constant.PROXY_SERVICES.equalsIgnoreCase(type) || Constant.INBOUND_ENDPOINTS.equalsIgnoreCase(type)) {
            AdvancedComponent advancedComponent;
            if (Constant.API.equalsIgnoreCase(type)) {
                advancedComponent = new APIComponent(component);
            } else {
                advancedComponent = new AdvancedComponent(component);
            }
            File file = new File(path);
            if (file.isFile()) {
                DOMDocument domDocument = getDOMDocument(file);
                DOMElement rootElement = getRootElement(domDocument);
                if (Constant.API.equalsIgnoreCase(type)) {
                    addResources(rootElement, advancedComponent);
                }
                traverseAndFind(rootElement, advancedComponent);
            }
            return advancedComponent;
        }
        return component;
    }

    private static void addResources(DOMElement rootElement, AdvancedComponent advancedComponent) {

        List<DOMNode> apiChildren = rootElement.getChildren();
        for (DOMNode child : apiChildren) {
            String name = child.getNodeName();
            if (Constant.RESOURCE.equalsIgnoreCase(name)) {
                String methods = child.getAttribute(Constant.METHODS);
                String uriTemplate = child.getAttribute(Constant.URI_TEMPLATE);
                APIResource resource = new APIResource(methods, uriTemplate);
                ((APIComponent) advancedComponent).addResource(resource);
            }
        }
    }

    private static void traverseAndFind(DOMElement rootElement, AdvancedComponent advancedComponent) {

        rootElement.getChildren().forEach(child -> {
            if (Constant.ENDPOINT.equalsIgnoreCase(child.getNodeName())) {
                String endpointName = child.getAttribute(Constant.KEY);
                String epPath = findEsbComponentPath(endpointName, Constant.ENDPOINTS);
                if (epPath != null) {
                    SimpleComponent endpoint = new SimpleComponent(Constant.ENDPOINT, endpointName, epPath);
                    advancedComponent.addEndpoint(endpoint);
                }
            } else if (Constant.SEQUENCE.equalsIgnoreCase(child.getNodeName())) {
                String sequenceName = child.getAttribute(Constant.KEY);
                String seqPath = findEsbComponentPath(sequenceName, Constant.SEQUENCES);
                if (seqPath != null) {
                    SimpleComponent sequence = new SimpleComponent(Constant.SEQUENCE, sequenceName, seqPath);
                    advancedComponent.addSequence(sequence);
                }
            } else if (child.hasChildNodes()) {
                traverseAndFind((DOMElement) child, advancedComponent);
            }
        });
    }

    private static DOMDocument getDOMDocument(File file) {

        Path path = file.toPath();
        String text = "";
        try {
            text = Files.readString(path);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error while reading file content", e);
        }
        TextDocument document = new TextDocument(text, file.getName());
        DOMDocument domDocument = DOMParser.getInstance().parse(document, null);
        return domDocument;
    }

    private static String findEsbComponentPath(String name, String type) {

        List<String> esbConfigPaths = getEsbConfigPaths();
        String path = null;
        for (String esbConfigPath : esbConfigPaths) {
            String foundPath = searchInEsbConfig(esbConfigPath, name, type);
            if (foundPath != null) {
                path = foundPath;
                break;
            }
        }
        return path;
    }

    private static String searchInEsbConfig(String esbConfigPath, String name, String type) {

        File folder = new File(esbConfigPath + Constant.FILE_SEPARATOR + type);
        File[] listOfFiles = folder.listFiles();
        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    DOMDocument domDocument = getDOMDocument(file);
                    if (domDocument != null) {
                        DOMElement rootElement = getRootElement(domDocument);
                        if (rootElement != null) {
                            String rootElementName = rootElement.getAttribute(Constant.NAME);
                            if (rootElementName.equalsIgnoreCase(name)) {
                                return file.getAbsolutePath();
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    private static List<String> getEsbConfigPaths() {

        File file = new File(projectPath);
        File[] listOfFiles = file.listFiles(File::isDirectory);
        List<String> esbConfigPaths = new ArrayList<>();
        if (listOfFiles != null) {
            for (File subProject : listOfFiles) {
                String projectFilePath = subProject.getAbsolutePath() + Constant.FILE_SEPARATOR + Constant.DOT_PROJECT;
                File projectFile = new File(projectFilePath);
                DOMDocument projectDOM = getDOMDocument(projectFile);
                DOMNode descriptionNode = findDescriptionNode(projectDOM);
                if (descriptionNode != null) {
                    DOMNode naturesNode = findNaturesNode(descriptionNode);
                    if (naturesNode != null) {
                        List<DOMNode> children = naturesNode.getChildren();
                        for (DOMNode child : children) {
                            String nature = Utils.getInlineString(child.getFirstChild());
                            if (ProjectType.ESB_CONFIGS.value.equalsIgnoreCase(nature)) {
                                esbConfigPaths.add(subProject.getAbsolutePath() + Constant.SYNAPSE_CONFIG_PATH);
                            }
                        }
                    }
                }
            }
        }
        return esbConfigPaths;
    }

    private static DOMElement getRootElement(DOMDocument document) {

        DOMElement rootElement = null;
        for (int i = 0; i < document.getChildren().size(); i++) {
            String elementName = document.getChild(i).getNodeName();
            if (containsIgnoreCase(Constant.SYNAPSE_CONFIG_ELEMENTS, elementName)) {
                rootElement = (DOMElement) document.getChild(i);
                break;
            }
        }
        return rootElement;
    }

    private static boolean containsIgnoreCase(List<String> list, String elementName) {

        return list.stream().anyMatch(s -> s.equalsIgnoreCase(elementName));
    }

}
