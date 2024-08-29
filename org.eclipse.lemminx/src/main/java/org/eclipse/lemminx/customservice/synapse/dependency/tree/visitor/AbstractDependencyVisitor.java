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
import org.eclipse.lemminx.customservice.synapse.dependency.tree.pojo.Dependency;
import org.eclipse.lemminx.customservice.synapse.dependency.tree.pojo.DependencyTree;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.SyntaxTreeGenerator;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.STNode;
import org.eclipse.lemminx.customservice.synapse.utils.Utils;
import org.eclipse.lemminx.dom.DOMDocument;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractDependencyVisitor {

    private static final Logger LOGGER = Logger.getLogger(AbstractDependencyVisitor.class.getName());
    DependencyLookUp dependencyLookUp;
    private DependencyTree dependencyTree;
    protected String projectPath;

    public AbstractDependencyVisitor(DependencyTree dependencyTree, String projectPath,
                                     DependencyLookUp dependencyLookUp) {

        this.dependencyTree = dependencyTree;
        this.projectPath = projectPath;
        this.dependencyLookUp = dependencyLookUp;
    }

    /**
     * Visit the artifact and build the dependency tree
     *
     * @param artifactPath
     */
    public final void visit(String artifactPath) {

        DOMDocument document = null;
        try {
            document = Utils.getDOMDocument(new File(artifactPath));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error while reading the artifact file", e);
        }
        if (document != null) {
            STNode node = SyntaxTreeGenerator.buildTree(document.getDocumentElement());
            visit(node);
        }
    }

    public abstract void visit(STNode node);

    protected void addDependency(Dependency dependency) {

        if (dependency != null) {
            if (dependencyLookUp.getDependency(dependency.getPath()) == null) {
                dependencyLookUp.addDependency(dependency.getPath(), dependency);
            }
            dependencyTree.addDependency(dependency);
        }
    }

    protected void addDependencies(List<Dependency> dependencies) {

        for (Dependency dependency : dependencies) {
            if (dependencyLookUp.getDependency(dependency.getPath()) == null) {
                dependencyLookUp.addDependency(dependency.getPath(), dependency);
            }
        }
        dependencyTree.addDependencyList(dependencies);
    }

    public DependencyTree getDependencyTree() {

        return dependencyTree;
    }
}
