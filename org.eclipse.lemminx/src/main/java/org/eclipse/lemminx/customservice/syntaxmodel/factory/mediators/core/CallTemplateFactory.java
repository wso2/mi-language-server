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

package org.eclipse.lemminx.customservice.syntaxmodel.factory.mediators.core;

import org.eclipse.lemminx.customservice.syntaxmodel.factory.mediators.AbstractMediatorFactory;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.STNode;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.mediator.Mediator;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.mediator.core.CallTemplate;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.mediator.core.WithParam;
import org.eclipse.lemminx.customservice.syntaxmodel.utils.Constant;
import org.eclipse.lemminx.dom.DOMElement;
import org.eclipse.lemminx.dom.DOMNode;

import java.util.ArrayList;
import java.util.List;

public class CallTemplateFactory extends AbstractMediatorFactory {

    private static final String CALL_TEMPLATE = "call-template";

    @Override
    public Mediator createSpecificMediator(DOMElement element) {

        CallTemplate callTemplate = new CallTemplate();
        callTemplate.elementNode(element);
        populateAttributes(callTemplate, element);
        List<DOMNode> children = element.getChildren();
        List<WithParam> withParams = new ArrayList<>();
        if (children != null && !children.isEmpty()) {
            for (DOMNode node : children) {
                if (node.getNodeName().equalsIgnoreCase(Constant.WITH_PARAM)) {
                    WithParam withParam = createWithParam(node);
                    withParams.add(withParam);
                }
            }
        }
        callTemplate.setWithParam(withParams.toArray(new WithParam[withParams.size()]));
        return callTemplate;
    }

    @Override
    public void populateAttributes(STNode node, DOMElement element) {

        String target = element.getAttribute(Constant.TARGET);
        if (target != null && !target.isEmpty()) {
            ((CallTemplate) node).setTarget(target);
        }
        String onError = element.getAttribute(Constant.ON_ERROR);
        if (onError != null && !onError.isEmpty()) {
            ((CallTemplate) node).setOnError(onError);
        }
        String description = element.getAttribute(Constant.DESCRIPTION);
        if (description != null && !description.isEmpty()) {
            ((CallTemplate) node).setDescription(description);
        }
    }

    private WithParam createWithParam(DOMNode node) {

        WithParam withParam = new WithParam();
        DOMElement element = (DOMElement) node;
        withParam.elementNode(element);
        populateWithParamAttributes(withParam, element);
        return withParam;
    }

    private void populateWithParamAttributes(WithParam withParam, DOMElement element) {

        String name = element.getAttribute(Constant.NAME);
        if (name != null && !name.isEmpty()) {
            withParam.setName(name);
        }
        String value = element.getAttribute(Constant.VALUE);
        if (value != null && !value.isEmpty()) {
            withParam.setValue(value);
        }
        String description = element.getAttribute(Constant.DESCRIPTION);
        if (description != null && !description.isEmpty()) {
            withParam.setDescription(description);
        }
    }

    @Override
    public String getTagName() {

        return CALL_TEMPLATE;
    }

}
