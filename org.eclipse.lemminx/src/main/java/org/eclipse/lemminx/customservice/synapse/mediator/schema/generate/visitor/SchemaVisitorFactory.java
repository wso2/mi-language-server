/*
 *   Copyright (c) 2025, WSO2 LLC. (http://www.wso2.com).
 *
 *   WSO2 LLC. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 */

package org.eclipse.lemminx.customservice.synapse.mediator.schema.generate.visitor;

import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.STNode;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.template.Template;
import org.eclipse.lemminx.customservice.synapse.utils.Constant;

import java.util.logging.Logger;

public class SchemaVisitorFactory {

    private static final Logger LOGGER = Logger.getLogger(SchemaVisitorFactory.class.getName());

    public static SchemaVisitor getSchemaVisitor(STNode node, String projectPath) {

        String nodeType = node.getTag();
        SchemaVisitor visitor = null;
        if (Constant.API.equals(nodeType)) {
            visitor = new APIVisitor(projectPath);
        } else if (Constant.SEQUENCE.equals(nodeType)) {
            visitor = new SequenceVisitor(projectPath);
        } else if (Constant.TEMPLATE.equals(nodeType) && ((Template) node).getSequence() != null) {
            visitor = new SequenceTemplateVisitor(projectPath);

        } else {
            LOGGER.warning("No visitor found for the node type: " + nodeType);
        }
        return visitor;
    }
}
