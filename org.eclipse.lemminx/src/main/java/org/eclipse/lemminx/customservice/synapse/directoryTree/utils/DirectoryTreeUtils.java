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

package org.eclipse.lemminx.customservice.synapse.directoryTree.utils;

import org.eclipse.lemminx.customservice.synapse.utils.Constant;
import org.eclipse.lemminx.customservice.synapse.utils.Utils;
import org.eclipse.lemminx.dom.DOMDocument;
import org.eclipse.lemminx.dom.DOMElement;
import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lsp4j.WorkspaceFolder;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class DirectoryTreeUtils {

    public static boolean isLegacyProject(WorkspaceFolder workspaceFolder) {

        String projectFilePath = workspaceFolder.getUri() + File.separator + Constant.DOT_PROJECT;
        return Utils.isFileExists(projectFilePath);
    }

    public static String getProjectType(String projectPath) {

        String pomFilePath = projectPath + File.separator + Constant.POM;
        File pomFile = new File(pomFilePath);
        if (pomFile.exists()) {
            try {
                DOMDocument domDocument = Utils.getDOMDocument(pomFile);
                DOMElement projectElement = getProjectElement(domDocument);
                if (projectElement != null) {
                    DOMNode propertiesNode = Utils.getChildNodeByName(projectElement, Constant.PROPERTIES);
                    DOMNode projectType = Utils.getChildNodeByName(propertiesNode, Constant.PROJECT_TYPE);
                    if (projectType != null) {
                        return Utils.getInlineString(projectType.getFirstChild());
                    }
                }
            } catch (IOException e) {
                return null;
            }
        }
        return null;
    }

    private static DOMElement getProjectElement(DOMDocument domDocument) {

        List<DOMNode> children = domDocument.getChildren();
        for (DOMNode child : children) {
            if (child instanceof DOMElement) {
                DOMElement element = (DOMElement) child;
                if (Constant.PROJECT.equalsIgnoreCase(element.getNodeName())) {
                    return element;
                }
            }
        }
        return null;
    }
}
