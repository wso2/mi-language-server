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

package org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.transformation;

import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.AbstractMediatorFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.STNode;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.Mediator;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.transformation.smooks.Smooks;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.transformation.smooks.SmooksInput;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.transformation.smooks.SmooksOutput;
import org.eclipse.lemminx.customservice.synapse.utils.Constant;
import org.eclipse.lemminx.dom.DOMElement;
import org.eclipse.lemminx.dom.DOMNode;

import java.util.List;

public class SmooksFactory extends AbstractMediatorFactory {

    private static final String SMOOKS = "smooks";

    @Override
    public Mediator createSpecificMediator(DOMElement element) {

        Smooks smooks = new Smooks();
        smooks.elementNode(element);
        populateAttributes(smooks, element);
        List<DOMNode> childNodes = element.getChildren();
        if (childNodes != null && !childNodes.isEmpty()) {
            for (DOMNode childNode : childNodes) {
                if (childNode.getNodeName().equalsIgnoreCase(Constant.INPUT)) {
                    SmooksInput smooksInput = createSmooksInput(childNode);
                    smooks.setInput(smooksInput);
                } else if (childNode.getNodeName().equalsIgnoreCase(Constant.OUTPUT)) {
                    SmooksOutput smooksOutput = createSmooksOutput(childNode);
                    smooks.setOutput(smooksOutput);
                }
            }
        }
        return smooks;
    }

    @Override
    public void populateAttributes(STNode node, DOMElement element) {

        String configKey = element.getAttribute(Constant.CONFIG_KEY);
        if (configKey != null) {
            ((Smooks) node).setConfigKey(configKey);
        }
        String description = element.getAttribute(Constant.DESCRIPTION);
        if (description != null) {
            ((Smooks) node).setDescription(description);
        }
    }

    private SmooksInput createSmooksInput(DOMNode childNode) {

        SmooksInput smooksInput = new SmooksInput();
        smooksInput.elementNode((DOMElement) childNode);
        String type = childNode.getAttribute(Constant.TYPE);
        if (type != null) {
            smooksInput.setType(type);
        }
        String expression = childNode.getAttribute(Constant.EXPRESSION);
        if (expression != null) {
            smooksInput.setExpression(expression);
        }
        return smooksInput;
    }

    private SmooksOutput createSmooksOutput(DOMNode element) {

        SmooksOutput smooksOutput = new SmooksOutput();
        smooksOutput.elementNode((DOMElement) element);
        String type = element.getAttribute(Constant.TYPE);
        if (type != null) {
            smooksOutput.setType(type);
        }
        String property = element.getAttribute(Constant.PROPERTY);
        if (property != null) {
            smooksOutput.setProperty(property);
        }
        String action = element.getAttribute(Constant.ACTION);
        if (action != null) {
            smooksOutput.setAction(action);
        }
        String expression = element.getAttribute(Constant.EXPRESSION);
        if (expression != null) {
            smooksOutput.setExpression(expression);
        }
        return smooksOutput;
    }

    @Override
    public String getTagName() {

        return SMOOKS;
    }
}
