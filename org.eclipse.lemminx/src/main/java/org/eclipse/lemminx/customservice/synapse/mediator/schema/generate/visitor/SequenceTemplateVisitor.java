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

import org.eclipse.lemminx.customservice.synapse.mediator.tryout.pojo.MediatorTryoutInfo;
import org.eclipse.lemminx.customservice.synapse.mediator.tryout.pojo.MediatorTryoutRequest;
import org.eclipse.lemminx.customservice.synapse.mediator.tryout.pojo.Params;
import org.eclipse.lemminx.customservice.synapse.mediator.tryout.pojo.Property;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.NamedSequence;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.STNode;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.template.Template;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.template.TemplateParameter;

public class SequenceTemplateVisitor implements SchemaVisitor {

    private String projectPath;

    public SequenceTemplateVisitor(String projectPath) {

        this.projectPath = projectPath;
    }

    @Override
    public void visit(STNode node, MediatorTryoutInfo info, MediatorTryoutRequest request) {

        if (node instanceof Template) {
            visit((Template) node, info, request);
        }
    }

    private void visit(Template node, MediatorTryoutInfo info, MediatorTryoutRequest request) {

        populateFunctionParams(node, info);
        NamedSequence sequence = node.getSequence();
        if (sequence != null) {
            SchemaVisitor visitor = SchemaVisitorFactory.getSchemaVisitor(sequence, projectPath);
            if (visitor != null) {
                visitor.visit(sequence, info, request);
            }
        }
    }

    private void populateFunctionParams(Template node, MediatorTryoutInfo info) {

        TemplateParameter[] parameters = node.getParameter();
        if (parameters != null) {
            Params params = new Params();
            for (TemplateParameter parameter : parameters) {
                String name = parameter.getName();
                String value = parameter.getDefaultValue();
                params.addFunctionParam(new Property(name, value));
            }
            info.setInputParams(params);
            info.setOutputParams(params.deepCopy());
        }
    }
}
