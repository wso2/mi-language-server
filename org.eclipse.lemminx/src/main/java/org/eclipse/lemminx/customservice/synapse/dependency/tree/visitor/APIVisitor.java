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

import org.eclipse.lemminx.customservice.synapse.dependency.tree.ArtifactType;
import org.eclipse.lemminx.customservice.synapse.dependency.tree.DependencyLookUp;
import org.eclipse.lemminx.customservice.synapse.dependency.tree.DependencyVisitorUtils;
import org.eclipse.lemminx.customservice.synapse.dependency.tree.pojo.Dependency;
import org.eclipse.lemminx.customservice.synapse.dependency.tree.pojo.DependencyTree;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.STNode;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.api.API;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.api.APIResource;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.misc.common.Sequence;

import java.util.List;

public class APIVisitor extends AbstractDependencyVisitor {

    public APIVisitor(String projectPath, DependencyLookUp dependencyLookUp) {

        super(new DependencyTree(), projectPath, dependencyLookUp);
    }

    public APIVisitor(DependencyTree dependencyTree, String projectPath) {

        super(dependencyTree, projectPath, new DependencyLookUp());
    }

    @Override
    public void visit(STNode node) {

        API api = (API) node;

        APIResource[] resources = api.getResource();
        for (APIResource resource : resources) {
            if (resource.getInSequenceAttribute() != null) {
                Dependency dependency = DependencyVisitorUtils.visitSequence(projectPath,
                        resource.getInSequenceAttribute(), dependencyLookUp);
                addDependency(dependency);
            } else {
                Sequence inSequence = resource.getInSequence();
                List<Dependency> dependencies = DependencyVisitorUtils.visitAnonymousSequence(inSequence, projectPath
                        , dependencyLookUp);
                addDependencies(dependencies);
            }
            if (resource.getOutSequenceAttribute() != null) {
                Dependency dependency = DependencyVisitorUtils.visitSequence(projectPath,
                        resource.getOutSequenceAttribute(), dependencyLookUp);
                addDependency(dependency);
            } else {
                Sequence outSequence = resource.getOutSequence();
                List<Dependency> dependencies = DependencyVisitorUtils.visitAnonymousSequence(outSequence,
                        projectPath, dependencyLookUp);
                addDependencies(dependencies);
            }
            if (resource.getFaultSequenceAttribute() != null) {
                Dependency dependency = DependencyVisitorUtils.visitSequence(projectPath,
                        resource.getFaultSequenceAttribute(), dependencyLookUp);
                addDependency(dependency);
            } else {
                Sequence faultSequence = resource.getFaultSequence();
                List<Dependency> dependencies = DependencyVisitorUtils.visitAnonymousSequence(faultSequence,
                        projectPath, dependencyLookUp);
                addDependencies(dependencies);
            }
        }

        String publishSwagger = api.getPublishSwagger();
        if (publishSwagger != null) {
            String path = DependencyVisitorUtils.getDependencyPath(publishSwagger, "swagger", projectPath);
            Dependency dependency = new Dependency(publishSwagger, ArtifactType.SWAGGER, path);
            addDependency(dependency);
        }
    }
}
