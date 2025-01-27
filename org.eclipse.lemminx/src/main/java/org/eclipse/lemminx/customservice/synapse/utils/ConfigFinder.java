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

import org.eclipse.lemminx.dom.DOMDocument;
import org.eclipse.lemminx.dom.DOMElement;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class ConfigFinder {

    public static String findEsbComponentPath(String key, String type, String projectPath) throws IOException {

        String foundPath = null;
        if (key != null && !key.isEmpty()) {
            String resourceFrom;
            Path configPath;
            if (key.contains(Constant.GOV_REGISTRY_PREFIX) || key.contains(Constant.CONF_REGISTRY_PREFIX)) {
                resourceFrom = "resources" + File.separator + "registry" + File.separator + key.split(":")[0];
                key = key.substring(key.indexOf(':') + 1);
                Path possiblePath = Path.of(projectPath, "src", "main", "wso2mi", resourceFrom, key);
                if (Utils.isFileExists(possiblePath.toString())) {
                    foundPath = possiblePath.toString();
                }
            } else if (key.contains(Constant.RESOURCES)) {
                key = key.substring(key.indexOf(':') + 1);
                Path possiblePath = Path.of(projectPath, "src", "main", "wso2mi", "resources", key);
                if (Utils.isFileExists(possiblePath.toString())) {
                    foundPath = possiblePath.toString();
                }
            } else {
                resourceFrom = "artifacts" + File.separator + type;
                configPath = Path.of(projectPath, "src", "main", "wso2mi", resourceFrom);
                foundPath = searchInConfigs(configPath.toString(), key);
            }
            Path localEntryPath = Path.of(projectPath, "src", "main", "wso2mi", "artifacts", "local-entries");
            if (foundPath == null) {
                foundPath = searchInConfigs(localEntryPath.toString(), key);
            }
        }
        return foundPath;
    }

    private static String searchInConfigs(String configPath, String key) throws IOException {

        File folder = new File(configPath);
        File[] listOfFiles = folder.listFiles();
        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile() && Utils.isXml(file)) {
                    DOMDocument domDocument = Utils.getDOMDocument(file);
                    if (domDocument != null) {
                        DOMElement rootElement = Utils.getRootElementFromConfigXml(domDocument);
                        if (rootElement != null) {
                            String rootElementName = rootElement.getAttribute(Constant.NAME);
                            if (key.equals(rootElementName)) {
                                return file.getAbsolutePath();
                            }
                            String rootElementKey = rootElement.getAttribute(Constant.KEY);
                            if (key.equals(rootElementKey)) {
                                return file.getAbsolutePath();
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
}
