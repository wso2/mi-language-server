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

package org.eclipse.lemminx.customservice.synapse.directoryTree;

import org.eclipse.lemminx.customservice.synapse.directoryTree.node.APINode;
import org.eclipse.lemminx.customservice.synapse.directoryTree.node.APIResource;
import org.eclipse.lemminx.customservice.synapse.directoryTree.node.AdvancedNode;
import org.eclipse.lemminx.customservice.synapse.directoryTree.node.FileNode;
import org.eclipse.lemminx.customservice.synapse.directoryTree.node.FolderNode;
import org.eclipse.lemminx.customservice.synapse.directoryTree.node.Node;
import org.eclipse.lemminx.customservice.synapse.directoryTree.node.TestFolder;
import org.eclipse.lemminx.customservice.synapse.directoryTree.legacyBuilder.LegacyDirectoryTreeBuilder;
import org.eclipse.lemminx.customservice.synapse.directoryTree.utils.DirectoryTreeUtils;
import org.eclipse.lemminx.customservice.synapse.utils.Constant;
import org.eclipse.lemminx.customservice.synapse.utils.Utils;
import org.eclipse.lemminx.dom.DOMDocument;
import org.eclipse.lemminx.dom.DOMElement;
import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lsp4j.WorkspaceFolder;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DirectoryTreeBuilder {

    private static final Logger LOGGER = Logger.getLogger(DirectoryTreeBuilder.class.getName());
    private static final String MAIN = "main";
    private static final String WSO2MI = "wso2mi";
    private static final String RESOURCES = "resources";
    private static final String JAVA = "java";
    private static String projectPath;

    public static DirectoryMapResponse buildDirectoryTree(WorkspaceFolder projectFolder) {

        //Support old project structure
        if (DirectoryTreeUtils.isLegacyProject(projectFolder)) {
            return LegacyDirectoryTreeBuilder.buildDirectoryTree(projectFolder);
        }
        try {
            String encodedPath = projectFolder.getUri().substring(7);
            projectPath = URLDecoder.decode(encodedPath, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOGGER.log(Level.SEVERE, "Could not decode the file path.", e);
        }
        Tree directoryTree = null;
        if (projectPath != null) {
            String projectType = DirectoryTreeUtils.getProjectType(projectPath);
            if (Constant.INTEGRATION_PROJECT.equalsIgnoreCase(projectType)) {
                directoryTree = new IntegrationDirectoryTree(projectPath, projectType);
                analyzeIntegrationProject((IntegrationDirectoryTree) directoryTree);
            } else if (Constant.DOCKER_PROJECT.equalsIgnoreCase(projectType) || Constant.KUBERNETES_PROJECT.
                    equalsIgnoreCase(projectType)) {
                directoryTree = new DistributionDirectoryTree(projectPath, projectType);
                analyzeDistributionProject((DistributionDirectoryTree) directoryTree);
            }
        }

        DirectoryMapResponse directoryMapResponse = new DirectoryMapResponse(directoryTree);
        return directoryMapResponse;
    }

    private static void analyzeIntegrationProject(IntegrationDirectoryTree directoryTree) {

        analyzeArtifacts(directoryTree);
        analyzeResources(directoryTree);
        analyzeJavaProjects(directoryTree);
        analyzeTestsFolder(directoryTree);
    }

    private static void analyzeDistributionProject(DistributionDirectoryTree directoryTree) {

        File folder = new File(projectPath);
        if (folder != null && folder.exists() && !folder.isHidden()) {
            String folderName = folder.getName();
            FolderNode folderNode = new FolderNode(folderName, projectPath);
            traverseFolder(folderNode);
            directoryTree.setFolders(folderNode.getFolders());
            directoryTree.setFiles(folderNode.getFiles());
        }
    }

    private static void analyzeArtifacts(IntegrationDirectoryTree directoryTree) {

        String artifactsPath = projectPath + File.separator + Constant.SRC + File.separator + MAIN
                + File.separator + WSO2MI + File.separator + "artifacts";
        File folder = new File(artifactsPath);
        File[] listOfFiles = folder.listFiles(File::isDirectory);
        if (listOfFiles != null) {
            for (File subFolder : listOfFiles) {
                try {
                    if (subFolder.isDirectory()) {
                        String type = getType(subFolder.getName());
                        analyzeByType(directoryTree, subFolder, type);
                    }
                } catch (SecurityException e) {
                    LOGGER.log(Level.WARNING, "No read access to the file.", e);
                }
            }
        }
    }

    private static String getType(String name) {

        String name1 = Utils.removeHyphen(name);
        name1 = Utils.pluralToSingular(name1);
        name1 = name1.substring(0, 1).toUpperCase() + name1.substring(1);
        return name1;
    }

    private static void analyzeByType(IntegrationDirectoryTree directoryTree, File folder, String type) {

        try {
            File[] listOfFiles = folder.listFiles();
            if (listOfFiles != null) {
                for (File file : listOfFiles) {
                    if (file.isFile() && !file.isHidden()) {
                        String name = file.getName();
                        String path = file.getAbsolutePath();
                        Node advancedComponent = createEsbComponent(type, name, path);
                        try {
                            String methodName = "add" + type;
                            Method method = directoryTree.getClass().getMethod(methodName, Node.class);
                            method.invoke(directoryTree, advancedComponent);
                        } catch (NoSuchMethodException | IllegalArgumentException | IllegalAccessException |
                                 InvocationTargetException e) {
                            LOGGER.log(Level.WARNING, "Error while trying to execute method.", e);
                        }
                    }
                }
            }
        } catch (SecurityException e) {
            LOGGER.log(Level.WARNING, "No read access to the file.", e);
        }
    }

    private static void analyzeResources(IntegrationDirectoryTree directoryTree) {

        analyzeRegistryResources(directoryTree);
        analyzeConnectorResources(directoryTree);
        analyzeMetadataResources(directoryTree);
    }

    private static void analyzeRegistryResources(IntegrationDirectoryTree directoryTree) {

        analyzeRegistryByType(directoryTree, Constant.GOV);
        analyzeRegistryByType(directoryTree, Constant.CONF);
    }

    private static void analyzeRegistryByType(IntegrationDirectoryTree directoryTree, String type) {

        String registryPath = projectPath + File.separator + Constant.SRC + File.separator +
                MAIN + File.separator + WSO2MI + File.separator + RESOURCES +
                File.separator + Constant.REGISTRY + File.separator + type;
        File folder = new File(registryPath);
        if (folder != null && folder.exists()) {
            if (!folder.isHidden()) {
                String folderName = folder.getName();
                FolderNode registryFolderNode = new FolderNode(folderName, registryPath);
                traverseFolder(registryFolderNode);
                if (Constant.GOV.equalsIgnoreCase(type)) {
                    directoryTree.getResources().getRegistry().setGov(registryFolderNode);
                } else if (Constant.CONF.equalsIgnoreCase(type)) {
                    directoryTree.getResources().getRegistry().setConf(registryFolderNode);
                }
            }
        }
    }

    private static void analyzeConnectorResources(IntegrationDirectoryTree directoryTree) {

        String connectorPath = projectPath + File.separator + Constant.SRC + File.separator + MAIN
                + File.separator + WSO2MI + File.separator + RESOURCES + File.separator + "connectors";
        File folder = new File(connectorPath);
        File[] listOfFiles = folder.listFiles();
        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (Utils.isZipFile(file) && !file.isHidden()) {
                    String name = file.getName();
                    String path = file.getAbsolutePath();
                    Node resource = new Node("connector", name, path);
                    directoryTree.getResources().addConnector(resource);
                }
            }
        }
    }

    private static void analyzeMetadataResources(IntegrationDirectoryTree directoryTree) {

        String metadataPath = projectPath + File.separator + Constant.SRC + File.separator + MAIN +
                File.separator + WSO2MI + File.separator + RESOURCES +
                File.separator + "metadata";
        File folder = new File(metadataPath);
        File[] listOfFiles = folder.listFiles();
        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile() && !file.isHidden()) {
                    String name = file.getName();
                    String path = file.getAbsolutePath();
                    Node resource = new Node("metadata", name, path);
                    directoryTree.getResources().addMetadata(resource);
                }
            }
        }
    }

    private static void analyzeJavaProjects(IntegrationDirectoryTree directoryTree) {

        String javaPath =
                projectPath + File.separator + Constant.SRC + File.separator + MAIN +
                        File.separator + JAVA;
        File folder = new File(javaPath);
        if (folder != null && folder.exists()) {
            if (!folder.isHidden()) {
                String folderName = folder.getName();
                FolderNode javaFolderNode = new FolderNode(folderName, javaPath);
                traverseFolder(javaFolderNode);
                directoryTree.setJava(javaFolderNode);
            }
        }
    }

    private static void analyzeTestsFolder(IntegrationDirectoryTree directoryTree) {

        TestFolder testFolder = new TestFolder();
        String testsPath = projectPath + File.separator + Constant.SRC + File.separator + "tests";
        analyzeSubTestFolder(testsPath, WSO2MI, testFolder::setWso2mi);
        analyzeSubTestFolder(testsPath, JAVA, testFolder::setJava);
        directoryTree.setTests(testFolder);
    }

    private static void analyzeSubTestFolder(String testPath, String testName, Consumer<FolderNode> setter) {

        File subFolder = new File(testPath + File.separator + testName);
        if (subFolder != null && subFolder.exists() && !subFolder.isHidden()) {
            String folderName = subFolder.getName();
            FolderNode testsFolderNode = new FolderNode(folderName, testPath);
            traverseFolder(testsFolderNode);
            setter.accept(testsFolderNode);
        }
    }

    private static void traverseFolder(FolderNode folderNode) {

        File[] listOfFiles = folderNode.listFiles();
        for (File file : listOfFiles) {
            if (file.isFile() && !file.isHidden()) {
                String name = file.getName();
                String filePath = file.getAbsolutePath();
                FileNode fileNodeComponent = new FileNode(name, filePath);
                folderNode.addFile(fileNodeComponent);
            } else if (file.isDirectory() && !file.isHidden()) {
                String name = file.getName();
                String folderPath = file.getAbsolutePath();
                FolderNode subFolderNode = new FolderNode(name, folderPath);
                folderNode.addFolder(subFolderNode);
                traverseFolder(subFolderNode);
            }
        }
    }

    private static Node createEsbComponent(String type, String name, String path) {

        Node component = new Node(Utils.addUnderscoreBetweenWords(type).toUpperCase(), name, path);
        if (Constant.API.equalsIgnoreCase(type) || Constant.SEQUENCES.equalsIgnoreCase(type) ||
                Constant.PROXY_SERVICES.equalsIgnoreCase(type) || Constant.INBOUND_ENDPOINTS.equalsIgnoreCase(type)) {
            AdvancedNode advancedNode;
            if (Constant.API.equalsIgnoreCase(type)) {
                advancedNode = new APINode(component);
            } else {
                advancedNode = new AdvancedNode(component);
            }
            File file = new File(path);
            if (file.isFile() && !file.isHidden()) {
                DOMDocument domDocument = null;
                try {
                    domDocument = Utils.getDOMDocument(file);
                } catch (IOException e) {
                    LOGGER.log(Level.WARNING, "Error while reading file content", e);
                }
                DOMElement rootElement = Utils.getRootElementFromConfigXml(domDocument);
                if (Constant.API.equalsIgnoreCase(type)) {
                    addResources(rootElement, advancedNode);
                }
            }
            return advancedNode;
        }
        return component;
    }

    private static void addResources(DOMElement rootElement, AdvancedNode advancedNode) {

        List<DOMNode> apiChildren = rootElement.getChildren();
        for (DOMNode child : apiChildren) {
            String name = child.getNodeName();
            if (Constant.RESOURCE.equalsIgnoreCase(name)) {
                String methods = child.getAttribute(Constant.METHODS);
                String uriTemplate = child.getAttribute(Constant.URI_TEMPLATE);
                String urlMapping = child.getAttribute(Constant.URL_MAPPING);
                APIResource resource = new APIResource(methods, uriTemplate, urlMapping);
                ((APINode) advancedNode).addResource(resource);
            }
        }
    }
}
