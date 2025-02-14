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

import org.eclipse.lemminx.customservice.synapse.dependency.tree.pojo.DependencyTree;
import org.eclipse.lemminx.customservice.synapse.utils.Constant;

public class DependencyVisitorFactory {

    private final String projectPath;

    public DependencyVisitorFactory(String projectPath) {

        this.projectPath = projectPath;
    }

    /**
     * Create a visitor based on the tag.
     *
     * @param tag            tag of the visitor
     * @param dependencyTree dependency tree
     * @return visitor
     */
    public AbstractDependencyVisitor createVisitor(String tag, DependencyTree dependencyTree) {

        switch (tag) {
            case Constant.API:
                return new APIVisitor(dependencyTree, projectPath);
            case Constant.ENDPOINT:
                return new EndpointVisitor(dependencyTree, projectPath);
            case Constant.SEQUENCE:
                return new SequenceVisitor(dependencyTree, projectPath);
            case Constant.PROXY:
                return new ProxyServiceVisitor(dependencyTree, projectPath);
            case Constant.INBOUND_ENDPOINT:
                return new InboundEndpointVisitor(dependencyTree, projectPath);
            case Constant.MESSAGE_STORE:
                return new MessageStoreVisitor(dependencyTree, projectPath);
            case Constant.MESSAGE_PROCESSOR:
                return new MessageProcessorVisitor(dependencyTree, projectPath);
            case Constant.TASK:
                return new TaskVisitor(dependencyTree, projectPath);
            case Constant.LOCAL_ENTRY:
                return new LocalEntryVisitor(dependencyTree, projectPath);
            case Constant.TEMPLATE:
                return new TemplateVisitor(dependencyTree, projectPath);
            case Constant.DATA:
                return new DataServiceVisitor(dependencyTree, projectPath);
            case Constant.DATA_SOURCE:
                return new DataSourceVisitor(dependencyTree, projectPath);
            default:
                throw new IllegalStateException("Invalid tag: " + tag);
        }
    }
}
