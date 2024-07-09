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
import org.eclipse.lemminx.customservice.synapse.dependency.tree.DependencyVisitorUtils;
import org.eclipse.lemminx.customservice.synapse.dependency.tree.pojo.Dependency;
import org.eclipse.lemminx.customservice.synapse.dependency.tree.pojo.DependencyTree;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.SyntaxTreeGenerator;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.LocalEntry;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.NamedSequence;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.STNode;
import org.eclipse.lemminx.customservice.synapse.utils.Utils;
import org.eclipse.lemminx.dom.DOMDocument;

import java.util.List;

public class SequenceVisitor extends AbstractDependencyVisitor {

    public SequenceVisitor(DependencyTree dependencyTree, String projectPath) {

        super(dependencyTree, projectPath, new DependencyLookUp());
    }

    public SequenceVisitor(String projectPath, DependencyLookUp dependencyLookUp) {

        super(new DependencyTree(), projectPath, dependencyLookUp);
    }

    @Override
    public void visit(STNode node) {

        if (node instanceof NamedSequence) {
            visitSequence(node);
        } else if (node instanceof LocalEntry) {
            visitLocalEntry(node);
        }
    }

    private void visitSequence(STNode node) {

        NamedSequence sequence = (NamedSequence) node;
        if (sequence.getOnError() != null) {
            Dependency dependency = DependencyVisitorUtils.visitSequence(projectPath, sequence.getOnError(),
                    dependencyLookUp);
            addDependency(dependency);
        }
        if (sequence.getMediatorList() != null) {
            List<Dependency> dependencyList = DependencyVisitorUtils.visitMediators(sequence.getMediatorList(),
                    projectPath, dependencyLookUp);
            addDependencies(dependencyList);
        }
    }

    private void visitLocalEntry(STNode node) {

        LocalEntry localEntry = (LocalEntry) node;
        String content = localEntry.getContent();
        if (content != null) {
            DOMDocument document = Utils.getDOMDocument(content);
            if (document != null) {
                STNode sequenceNode = SyntaxTreeGenerator.buildTree(document.getDocumentElement());
                visit(sequenceNode);
            }
        }
    }
}
