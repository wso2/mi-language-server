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

import org.eclipse.lemminx.customservice.syntaxmodel.utils.ConfigFinder;
import org.eclipse.lemminx.customservice.syntaxmodel.utils.Constant;
import org.eclipse.lemminx.customservice.syntaxmodel.utils.Utils;
import org.eclipse.lemminx.dom.DOMDocument;
import org.eclipse.lemminx.dom.DOMElement;
import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lsp4j.WorkspaceFolder;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DirectoryTreeBuilder {

    private static final Logger LOGGER = Logger.getLogger(DirectoryTreeBuilder.class.getName());
    private static String projectPath;

    public static DirectoryMapResponse buildDirectoryTree(WorkspaceFolder workspaceFolder) {

        String currentPath = workspaceFolder.getUri();
        DirectoryMap directoryMap = new DirectoryMap();

        String rootPath = null;
        try {
            rootPath = Utils.findRootPath(currentPath);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error while reading file content", e);
        }
        projectPath = rootPath;
        if (projectPath != null) {
            analyze(directoryMap);
        }
        DirectoryMapResponse directoryMapResponse = new DirectoryMapResponse(directoryMap);
        return directoryMapResponse;
    }

    private static void analyze(DirectoryMap directoryMap) {

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
        if (projectFile == null || !projectFile.exists()) {
            return null;
        }
        DOMDocument projectDOM = null;
        try {
            projectDOM = Utils.getDOMDocument(projectFile);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error while reading file content", e);
        }
        DOMNode descriptionNode = Utils.findDescriptionNode(projectDOM);
        if (descriptionNode != null) {
            DOMNode naturesNode = Utils.findNaturesNode(descriptionNode);
            if (naturesNode != null) {
                List<DOMNode> children = naturesNode.getChildren();
                for (DOMNode child : children) {
                    String nature = Utils.getInlineString(child.getFirstChild());
                    if (ProjectType.DATA_SERVICE_CONFIGS.value.equalsIgnoreCase(nature)) {
                        directoryMap.addDataServiceConfig(ProjectType.DATA_SERVICE_CONFIGS.name(),
                                subProject.getName(), subProject.getAbsolutePath());
                        return ProjectType.DATA_SERVICE_CONFIGS;
                    } else if (ProjectType.ESB_CONFIGS.value.equalsIgnoreCase(nature)) {
                        ESBComponent esbComponent = new ESBComponent(ProjectType.ESB_CONFIGS.name(),
                                subProject.getName(), subProject.getAbsolutePath());
                        analyzeEsbConfigs(subProject.getAbsolutePath() +
                                Constant.SYNAPSE_CONFIG_PATH, esbComponent);
                        directoryMap.addEsbComponent(esbComponent);
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

    private static void analyzeEsbConfigs(String configPath, ESBComponent esbComponent) {

        File folder = new File(configPath);
        File[] listOfFiles = folder.listFiles(File::isDirectory);
        if (listOfFiles != null) {
            for (File subFolder : listOfFiles) {
                try {
                    if (subFolder.isDirectory()) {
                        String type = subFolder.getName();
                        analyzeByType(esbComponent, subFolder, type);
                    }
                } catch (SecurityException e) {
                    LOGGER.log(Level.WARNING, "No read access to the file.", e);
                }
            }
        }
    }

    private static void analyzeByType(ESBComponent esbComponent, File folder, String type) {

        try {
            File[] listOfFiles = folder.listFiles();
            if (listOfFiles != null) {
                for (File file : listOfFiles) {
                    if (file.isFile()) {
                        String name = file.getName();
                        String path = file.getAbsolutePath();
                        SimpleComponent advancedComponent = createEsbComponent(type, name, path);
                        esbComponent.addEsbConfig(type, advancedComponent);
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
                DOMDocument domDocument = null;
                try {
                    domDocument = Utils.getDOMDocument(file);
                } catch (IOException e) {
                    LOGGER.log(Level.WARNING, "Error while reading file content", e);
                }
                DOMElement rootElement = Utils.getRootElementFromConfigXml(domDocument);
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
                String epPath = null;
                try {
                    epPath = ConfigFinder.findEsbComponentPath(endpointName, Constant.ENDPOINTS, projectPath);
                } catch (IOException e) {
                    LOGGER.log(Level.WARNING, "Error while reading file content", e);
                }
                if (epPath != null) {
                    SimpleComponent endpoint = new SimpleComponent(Constant.ENDPOINT, endpointName, epPath);
                    advancedComponent.addEndpoint(endpoint);
                }
            } else if (Constant.SEQUENCE.equalsIgnoreCase(child.getNodeName())) {
                String sequenceName = child.getAttribute(Constant.KEY);
                String seqPath = null;
                try {
                    seqPath = ConfigFinder.findEsbComponentPath(sequenceName, Constant.SEQUENCES, projectPath);
                } catch (IOException e) {
                    LOGGER.log(Level.WARNING, "Error while reading file content", e);
                }
                if (seqPath != null) {
                    SimpleComponent sequence = new SimpleComponent(Constant.SEQUENCE, sequenceName, seqPath);
                    advancedComponent.addSequence(sequence);
                }
            } else if (child.hasChildNodes()) {
                traverseAndFind((DOMElement) child, advancedComponent);
            }
        });
    }
}
