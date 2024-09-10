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
import org.eclipse.lemminx.customservice.synapse.dependency.tree.pojo.DependencyTree;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.MessageStore;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.STNode;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.misc.common.Parameter;

public class MessageStoreVisitor extends AbstractDependencyVisitor {

    private static final String FAIL_OVER_MESSAGE_PARAMETER = "store.failover.message.store.name";

    public MessageStoreVisitor(DependencyTree dependencyTree, String projectPath) {

        super(dependencyTree, projectPath, new DependencyLookUp());
    }

    public MessageStoreVisitor(String projectPath, DependencyLookUp dependencyLookUp) {

        super(new DependencyTree(), projectPath, dependencyLookUp);
    }

    @Override
    public void visit(STNode node) {

        MessageStore messageStore = (MessageStore) node;
        Parameter[] parameters = messageStore.getParameter();
        if (parameters != null) {
            for (Parameter parameter : parameters) {
                if (FAIL_OVER_MESSAGE_PARAMETER.equalsIgnoreCase(parameter.getName())) {
                    addDependency(DependencyVisitorUtils.visitMessageStore(parameter.getContent(), projectPath,
                            dependencyLookUp));
                }
            }
        }
    }
}
