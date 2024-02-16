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

package org.eclipse.lemminx.customservice.synapse.utils;

import org.eclipse.lemminx.customservice.synapse.directoryTree.legacyBuilder.utils.ProjectType;
import org.eclipse.lemminx.dom.DOMDocument;
import org.eclipse.lemminx.dom.DOMElement;
import org.eclipse.lemminx.dom.DOMNode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConfigFinder {

    public static String findEsbComponentPath(String key, String type, String projectPath) throws IOException {

        if (key != null && !key.isEmpty()) {
            String resourceFrom = ProjectType.ESB_CONFIGS.value;
            if (key.contains(Constant.GOV_REGISTRY_PREFIX) || key.contains(Constant.CONF_REGISTRY_PREFIX)) {
                resourceFrom = ProjectType.REGISTRY_RESOURCE.value;
                key = key.substring(key.indexOf(':') + 1);
            }
            List<String> configPaths = getConfigPaths(projectPath, resourceFrom);
            String path = null;
            for (String configPath : configPaths) {
                String configPath1 = configPath;
                if (ProjectType.ESB_CONFIGS.value.equalsIgnoreCase(resourceFrom)) {
                    configPath1 = configPath + Constant.SYNAPSE_CONFIG_PATH + Constant.FILE_SEPARATOR + type;
                }
                String foundPath = searchInConfigs(configPath1, key);
                if (foundPath != null) {
                    path = foundPath;
                    break;
                }
                String localEntryPath =
                        configPath + Constant.SYNAPSE_CONFIG_PATH + Constant.FILE_SEPARATOR + Constant.LOCAL_ENTRIES;
                foundPath = searchInConfigs(localEntryPath, key);
                if (foundPath != null) {
                    path = foundPath;
                    break;
                }
            }
            return path;
        }
        return null;
    }

    private static List<String> getConfigPaths(String projectPath, String configType) throws IOException {

        File file = new File(projectPath);
        File[] listOfFiles = file.listFiles(File::isDirectory);
        List<String> configPaths = new ArrayList<>();
        if (listOfFiles != null) {
            for (File subProject : listOfFiles) {
                String projectFilePath = subProject.getAbsolutePath() + Constant.FILE_SEPARATOR + Constant.DOT_PROJECT;
                File projectFile = new File(projectFilePath);
                if (projectFile.exists()) {
                    DOMDocument projectDOM = Utils.getDOMDocument(projectFile);
                    DOMNode descriptionNode = Utils.findDescriptionNode(projectDOM);
                    if (descriptionNode != null) {
                        DOMNode naturesNode = Utils.findNaturesNode(descriptionNode);
                        if (naturesNode != null) {
                            List<DOMNode> children = naturesNode.getChildren();
                            for (DOMNode child : children) {
                                String nature = Utils.getInlineString(child.getFirstChild());
                                if (configType.equalsIgnoreCase(nature)) {
                                    configPaths.add(subProject.getAbsolutePath());
                                }
                            }
                        }
                    }
                }
            }
        }
        return configPaths;
    }

    private static String searchInConfigs(String configPath, String key) throws IOException {

        File folder = new File(configPath);
        File[] listOfFiles = folder.listFiles();
        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile() && isXml(file)) {
                    DOMDocument domDocument = Utils.getDOMDocument(file);
                    if (domDocument != null) {
                        DOMElement rootElement = Utils.getRootElementFromConfigXml(domDocument);
                        if (rootElement != null) {
                            String rootElementName = rootElement.getAttribute(Constant.NAME);
                            if (key.equalsIgnoreCase(rootElementName)) {
                                return file.getAbsolutePath();
                            }
                            String rootElementKey = rootElement.getAttribute(Constant.KEY);
                            if (key.equalsIgnoreCase(rootElementKey)) {
                                return file.getAbsolutePath();
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    private static boolean isXml(File file) {

        String filePath = file.getName();
        int dotIndex = filePath.lastIndexOf(Constant.DOT);
        if (dotIndex != -1 && dotIndex < filePath.length() - 1) {
            String extension = filePath.substring(dotIndex + 1);
            return Constant.XML.equalsIgnoreCase(extension);
        }
        return false;
    }
}
