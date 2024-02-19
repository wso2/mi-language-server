/*
 * Copyright (c) 2023, WSO2 LLC. (http://www.wso2.com).
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

package org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.eip;

import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.AbstractMediatorFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.STNode;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.Mediator;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.eip.Iterate;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.misc.targets.Target;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.utils.SyntaxTreeUtils;
import org.eclipse.lemminx.customservice.synapse.utils.Constant;
import org.eclipse.lemminx.dom.DOMElement;
import org.eclipse.lemminx.dom.DOMNode;

import java.util.ArrayList;
import java.util.List;

public class IterateFactory extends AbstractMediatorFactory {

    private static final String ITERATE = "iterate";

    @Override
    public Mediator createSpecificMediator(DOMElement element) {

        Iterate iterate = new Iterate();
        iterate.elementNode(element);
        populateAttributes(iterate, element);
        List<DOMNode> children = element.getChildren();
        List<Target> targets = new ArrayList<>();
        if (children != null && !children.isEmpty()) {
            for (DOMNode node : children) {
                if (node.getNodeName().equals(Constant.TARGET)) {
                    Target target = SyntaxTreeUtils.createTarget(node);
                    targets.add(target);
                }
            }
            iterate.setTarget(targets.toArray(new Target[targets.size()]));
        }
        return iterate;
    }

    @Override
    public void populateAttributes(STNode node, DOMElement element) {

        String sequential = element.getAttribute(Constant.SEQUENTIAL);
        if (sequential != null && !sequential.isEmpty()) {
            ((Iterate) node).setSequential(Boolean.parseBoolean(sequential));
        }
        String continueParent = element.getAttribute(Constant.CONTINUE_PARENT);
        if (continueParent != null && !continueParent.isEmpty()) {
            ((Iterate) node).setContinueParent(Boolean.parseBoolean(continueParent));
        }
        String preservePayload = element.getAttribute(Constant.PRESERVE_PAYLOAD);
        if (preservePayload != null && !preservePayload.isEmpty()) {
            ((Iterate) node).setPreservePayload(Boolean.parseBoolean(preservePayload));
        }
        String expression = element.getAttribute(Constant.EXPRESSION);
        if (expression != null && !expression.isEmpty()) {
            ((Iterate) node).setExpression(expression);
        }
        String attachPath = element.getAttribute(Constant.ATTACH_PATH);
        if (attachPath != null && !attachPath.isEmpty()) {
            ((Iterate) node).setAttachPath(attachPath);
        }
        String id = element.getAttribute(Constant.ID);
        if (id != null && !id.isEmpty()) {
            ((Iterate) node).setId(id);
        }
        String description = element.getAttribute(Constant.DESCRIPTION);
        if (description != null && !description.isEmpty()) {
            ((Iterate) node).setDescription(description);
        }
    }

    @Override
    public String getTagName() {

        return ITERATE;
    }
}
