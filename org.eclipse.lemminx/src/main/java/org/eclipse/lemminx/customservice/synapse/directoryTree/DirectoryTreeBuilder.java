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
import org.eclipse.lemminx.customservice.synapse.directoryTree.node.ConnectionNode;
import org.eclipse.lemminx.customservice.synapse.directoryTree.node.FileNode;
import org.eclipse.lemminx.customservice.synapse.directoryTree.node.FolderNode;
import org.eclipse.lemminx.customservice.synapse.directoryTree.node.Node;
import org.eclipse.lemminx.customservice.synapse.directoryTree.node.RegistryNode;
import org.eclipse.lemminx.customservice.synapse.directoryTree.node.TestFolder;
import org.eclipse.lemminx.customservice.synapse.directoryTree.legacyBuilder.LegacyDirectoryTreeBuilder;
import org.eclipse.lemminx.customservice.synapse.directoryTree.utils.DirectoryTreeUtils;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.TemplateFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.endpoint.EndpointFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.endpoint.NamedEndpoint;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.template.Template;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            String encodedPath = projectFolder.getUri();
            if (encodedPath.startsWith(Constant.FILE_PREFIX)) {
                encodedPath = encodedPath.substring(7);
            }
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
                ((IntegrationDirectoryTree) directoryTree).sort();
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
            traverseFolder(folderNode, null);
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
                        if (advancedComponent != null) {
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
                traverseFolder(registryFolderNode, directoryTree);
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
                traverseFolder(javaFolderNode, null);
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
            traverseFolder(testsFolderNode, null);
            setter.accept(testsFolderNode);
        }
    }

    private static void traverseFolder(FolderNode folderNode, IntegrationDirectoryTree directoryTree) {

        File[] listOfFiles = folderNode.listFiles();
        for (File file : listOfFiles) {
            if (file.isFile() && !file.isHidden()) {
                String name = file.getName();
                String filePath = file.getAbsolutePath();
                FileNode fileNodeComponent = new FileNode(name, filePath);
                folderNode.addFile(fileNodeComponent);
                if (directoryTree != null) {
                    addResourceToIntegrationTree(directoryTree, filePath);
                }
            } else if (file.isDirectory() && !file.isHidden()) {
                String name = file.getName();
                String folderPath = file.getAbsolutePath();
                FolderNode subFolderNode = new FolderNode(name, folderPath);
                folderNode.addFolder(subFolderNode);
                traverseFolder(subFolderNode, directoryTree);
            }
        }
    }

    private static void addResourceToIntegrationTree(IntegrationDirectoryTree directoryTree, String path) {

        if (path.endsWith(".xml")) {
            try {
                File file = new File(path);
                DOMDocument domDocument = Utils.getDOMDocument(file);
                DOMElement rootElement = Utils.getRootElementFromConfigXml(domDocument);

                if (rootElement != null) {
                    String type = rootElement.getNodeName();
                    String name = file.getName();
                    type = getType(type);
                    Node regNode = createRegistryNode(name, type, path);
                    String methodName = "add" + type;
                    Method method = directoryTree.getClass().getMethod
                            (methodName, Node.class);
                    method.invoke(directoryTree, regNode);
                }
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Error while reading file content", e);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                LOGGER.log(Level.WARNING, "Error while trying to execute method.", e);
            }
        }
    }

    private static Node createRegistryNode(String name, String type, String path) {

        StringBuilder key = new StringBuilder();
        if (path.contains(Constant.GOV)) {
            key.append(Constant.GOV);
        } else {
            key.append(Constant.CONF);
        }
        String key1 = path.substring(path.indexOf(key.toString()) + key.length() + 1,
                path.lastIndexOf(File.separator) + 1);
        key.append(":");
        key.append(key1);
        Node node = createEsbComponent(type, name, path);
        Node registry = new RegistryNode(node, key.toString());
        return registry;
    }

    private static Node createEsbComponent(String type, String name, String path) {

        String artifactName;
        String nodeType = Utils.addUnderscoreBetweenWords(type).toUpperCase();
        try {
            artifactName = getArtifactName(type, path);
        } catch (IOException e) {
            //Could not read artifact name. Ignoring the file as it is invalid.
            Node invalidNode = new Node(nodeType, name, path);
            invalidNode.setFaulty(Boolean.TRUE);
            return invalidNode;
        }
        if (artifactName == null) artifactName = name;
        Node component = new Node(nodeType, artifactName, path);
        if (Constant.API.equalsIgnoreCase(type) || Constant.SEQUENCE.equalsIgnoreCase(type) ||
                Constant.PROXY_SERVICE.equalsIgnoreCase(type) || Constant.INBOUND_ENDPOINT.equalsIgnoreCase(type)) {
            AdvancedNode advancedNode = createAdvancedEsbComponent(component, type, path);
            return advancedNode;
        } else if (Constant.LOCAL_ENTRY.equalsIgnoreCase(type)) {
            Node localEntry = createLocalEntry(component, path);
            return localEntry;
        }

        if (Constant.ENDPOINT.equalsIgnoreCase(type)) {
            String endpointType = getEndpointType(path);
            component.setSubType(endpointType);
        } else if (Constant.TEMPLATE.equalsIgnoreCase(type)) {
            String templateType = getTemplateType(path);
            component.setSubType(templateType);
        }
        return component;
    }

    private static AdvancedNode createAdvancedEsbComponent(Node component, String type, String path) {

        AdvancedNode advancedNode;
        if (Constant.API.equalsIgnoreCase(type)) {
            String context = getApiContext(path);
            advancedNode = new APINode(component);
            ((APINode) advancedNode).setContext(context);
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

    private static Node createLocalEntry(Node component, String path) {

        File file = new File(path);
        try {
            DOMDocument domDocument = Utils.getDOMDocument(file);
            if (domDocument != null) {
                DOMElement rootElement = domDocument.getDocumentElement();
                String key = rootElement.getAttribute(Constant.KEY);
                DOMElement childElement = Utils.getFirstElement(rootElement);
                if (childElement != null) {
                    String entryTag = childElement.getNodeName();
                    Pattern pattern = Pattern.compile("(.*)\\.init");
                    Matcher matcher = pattern.matcher(entryTag);
                    if (matcher.find()) {
                        String connectorName = matcher.group(1);
                        String connectionType = getConnectionType(childElement);
                        ConnectionNode connectionNode = new ConnectionNode(key, path, connectorName, connectionType);
                        return connectionNode;
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error while reading file content", e);
        }
        return component;
    }

    private static String getConnectionType(DOMElement element) {

        List<DOMNode> children = element.getChildren();
        if (children != null) {
            for (DOMNode child : children) {
                String nodeName = child.getNodeName();
                if ("connectionType".equals(nodeName)) {
                    String connectionType = Utils.getInlineString(child.getFirstChild());
                    return connectionType;
                }
            }
        }
        return null;
    }

    private static String getTemplateType(String path) {

        File file = new File(path);
        DOMDocument domDocument = null;
        try {
            domDocument = Utils.getDOMDocument(file);
            TemplateFactory factory = new TemplateFactory();
            Template template = (Template) factory.create(domDocument.getDocumentElement());
            if (template.getEndpoint() != null) {
                return template.getEndpoint().getType().toString();
            } else if (template.getSequence() != null) {
                return Constant.SEQUENCE.toUpperCase();
            }
        } catch (IOException e) {
            //ignore
        }
        return null;
    }

    private static String getApiContext(String path) {

        File file = new File(path);
        DOMDocument domDocument = null;
        try {
            domDocument = Utils.getDOMDocument(file);
            DOMNode node = Utils.getChildNodeByName(domDocument, Constant.API);
            if (node != null) {
                String context = node.getAttribute(Constant.CONTEXT);
                return context;
            }
        } catch (IOException e) {
            //ignore
        }
        return null;
    }

    private static String getArtifactName(String type, String path) throws IOException {

        File file = new File(path);
        DOMDocument domDocument = Utils.getDOMDocument(file);

        if ("ProxyService".equalsIgnoreCase(type)) type = Constant.PROXY;
        if ("DataService".equalsIgnoreCase(type)) type = Constant.DATA;

        DOMNode node = Utils.getChildNodeByName(domDocument, type);
        if (node != null) {
            String name = node.getAttribute(Constant.NAME);
            if (name == null) {
                name = node.getAttribute(Constant.KEY);
            }
            return name;
        } else {
            throw new IOException("Invalid artifact in the artifact folder: " + type);
        }
    }

    private static String getEndpointType(String path) {

        try {
            File file = new File(path);
            DOMDocument domDocument = Utils.getDOMDocument(file);
            EndpointFactory factory = new EndpointFactory();
            NamedEndpoint endpoint = (NamedEndpoint) factory.create(domDocument.getDocumentElement());
            String type = endpoint.getType().name();
            return type;
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Could not read the type from endpoint");
        }
        return "";
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
