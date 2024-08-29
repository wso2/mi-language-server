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

package org.eclipse.lemminx.customservice.synapse.dependency.tree;

import org.eclipse.lemminx.customservice.synapse.dependency.tree.pojo.DependencyTree;
import org.eclipse.lemminx.customservice.synapse.dependency.tree.visitor.AbstractDependencyVisitor;
import org.eclipse.lemminx.customservice.synapse.dependency.tree.visitor.DependencyVisitorFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.SyntaxTreeGenerator;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.STNode;
import org.eclipse.lemminx.customservice.synapse.utils.Constant;
import org.eclipse.lemminx.customservice.synapse.utils.Utils;
import org.eclipse.lemminx.dom.DOMDocument;
import org.eclipse.lemminx.dom.DOMElement;
import org.eclipse.lemminx.dom.DOMNode;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DependencyScanner {

    private static final Logger LOGGER = Logger.getLogger(DependencyScanner.class.getName());
    private String projectPath;

    public DependencyScanner(String projectPath) {

        this.projectPath = projectPath;
    }

    public DependencyTree analyzeArtifact(String artifactPath) {

        DependencyTree dependencyTree = new DependencyTree();
        artifactPath = Utils.removeFilePrefix(artifactPath);
        dependencyTree.setPath(artifactPath);
        try {
            DOMDocument document = Utils.getDOMDocument(new File(artifactPath));
            if (document != null) {
                String artifactName = getArtifactName(document);
                if (artifactName != null) {
                    dependencyTree.setName(artifactName);
                }
                STNode node = SyntaxTreeGenerator.buildTree(document.getDocumentElement());
                DependencyVisitorFactory dependencyVisitorFactory = new DependencyVisitorFactory(projectPath);
                AbstractDependencyVisitor visitor = dependencyVisitorFactory.createVisitor(node.getTag(),
                        dependencyTree);
                if (visitor != null) {
                    visitor.visit(node);
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error occurred while analyzing the artifact: " + artifactPath, e);
        }
        return dependencyTree;
    }

    private String getArtifactName(DOMDocument document) {

        DOMElement rootElement = document.getDocumentElement();
        if (rootElement != null) {
            if (rootElement.hasAttribute(Constant.NAME)) {
                return rootElement.getAttribute(Constant.NAME);
            } else if (rootElement.hasAttribute(Constant.KEY)) {
                return rootElement.getAttribute(Constant.KEY);
            } else {
                DOMNode childElement = Utils.getChildNodeByName(rootElement, Constant.NAME);
                if (childElement != null) {
                    return childElement.getTextContent();
                }
            }
        }
        return null;
    }
}
