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

package org.eclipse.lemminx.customservice.synapse.dependency.tree.visitor;

import org.eclipse.lemminx.customservice.synapse.dependency.tree.DependencyLookUp;
import org.eclipse.lemminx.customservice.synapse.dependency.tree.pojo.DependencyTree;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.SyntaxTreeGenerator;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.LocalEntry;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.STNode;
import org.eclipse.lemminx.customservice.synapse.utils.Utils;
import org.eclipse.lemminx.dom.DOMDocument;

import java.io.File;
import java.io.IOException;

public class LocalEntryVisitor extends AbstractDependencyVisitor {

    public LocalEntryVisitor(DependencyTree dependencyTree, String projectPath) {

        super(dependencyTree, projectPath, new DependencyLookUp());

    }

    public LocalEntryVisitor(DependencyTree dependencyTree, String projectPath, DependencyLookUp dependencyLookUp) {

        super(dependencyTree, projectPath, dependencyLookUp);
    }

    @Override
    public void visit(STNode node) {

        LocalEntry localEntry = (LocalEntry) node;
        try {
            DOMDocument document = getDomDocument(localEntry);
            if (document != null && document.getDocumentElement() != null) {
                STNode childNode = SyntaxTreeGenerator.buildTree(document.getDocumentElement());
                DependencyVisitorFactory dependencyVisitorFactory = new DependencyVisitorFactory(projectPath);
                AbstractDependencyVisitor visitor =
                        dependencyVisitorFactory.createVisitor(childNode.getTag(), getDependencyTree());
                if (visitor != null) {
                    visitor.visit(childNode);
                }
            }
        } catch (IOException e) {
        }

    }

    private DOMDocument getDomDocument(LocalEntry localEntry) throws IOException {

        String content = localEntry.getContent();
        if (content != null) {
            return Utils.getDOMDocument(content);
        }
        String path = localEntry.getSrc();
        if (path != null && path.endsWith(".xml")) {
            File file = new File(path);
            if (file.exists()) {
                return Utils.getDOMDocument(file);
            }
        }
        return null;
    }
}
