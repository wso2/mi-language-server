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
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.transformation.fault.Makefault;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.transformation.fault.MakefaultCode;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.transformation.fault.MakefaultDetail;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.transformation.fault.MakefaultReason;
import org.eclipse.lemminx.customservice.synapse.utils.Constant;
import org.eclipse.lemminx.dom.DOMElement;
import org.eclipse.lemminx.dom.DOMNode;

import java.util.List;

public class FaultFactory extends AbstractMediatorFactory {

    private static final String FAULT = "makefault";

    @Override
    public Mediator createSpecificMediator(DOMElement element) {

        Makefault makefault = new Makefault();
        makefault.elementNode(element);
        populateAttributes(makefault, element);
        List<DOMNode> childNodes = element.getChildren();
        if (childNodes != null && !childNodes.isEmpty()) {
            for (DOMNode child : childNodes) {
                if (child.getNodeName().equalsIgnoreCase(Constant.CODE)) {
                    MakefaultCode code = createMakefaultCode(child);
                    makefault.setCode(code);
                } else if (child.getNodeName().equalsIgnoreCase(Constant.REASON)) {
                    MakefaultReason reason = createMakefaultReason(child);
                    makefault.setReason(reason);
                } else if (child.getNodeName().equalsIgnoreCase(Constant.ROLE)) {
                    // TODO: check if this is correct
                    STNode role = new STNode();
                    role.elementNode((DOMElement) child);
                    makefault.setRole(role);
                } else if (child.getNodeName().equalsIgnoreCase(Constant.NODE)) {
                    // TODO: check if this is correct
                    STNode node = new STNode();
                    node.elementNode((DOMElement) child);
                    makefault.setNode(node);
                } else if (child.getNodeName().equalsIgnoreCase(Constant.DETAIL)) {
                    MakefaultDetail detail = new MakefaultDetail();
                    detail.elementNode((DOMElement) child);
                    String expression = child.getAttribute(Constant.EXPRESSION);
                    if (expression != null && !expression.isEmpty()) {
                        detail.setExpression(expression);
                    }
                    makefault.setDetail(detail);
                }
            }
        }
        return makefault;
    }

    @Override
    public void populateAttributes(STNode node, DOMElement element) {

        String description = element.getAttribute(Constant.DESCRIPTION);
        if (description != null && !description.isEmpty()) {
            ((Makefault) node).setDescription(description);
        }
        String version = element.getAttribute(Constant.VERSION);
        if (version != null && !version.isEmpty()) {
            ((Makefault) node).setVersion(version);
        }
        String response = element.getAttribute(Constant.RESPONSE);
        if (response != null && !response.isEmpty()) {
            ((Makefault) node).setResponse(Boolean.parseBoolean(response));
        }
    }

    private MakefaultCode createMakefaultCode(DOMNode element) {

        MakefaultCode code = new MakefaultCode();
        code.elementNode((DOMElement) element);
        String value = element.getAttribute(Constant.VALUE);
        if (value != null && !value.isEmpty()) {
            code.setValue(value);
        }
        String expression = element.getAttribute(Constant.EXPRESSION);
        if (expression != null && !expression.isEmpty()) {
            code.setExpression(expression);
        }
        return code;
    }

    private MakefaultReason createMakefaultReason(DOMNode element) {

        MakefaultReason reason = new MakefaultReason();
        reason.elementNode((DOMElement) element);
        String value = element.getAttribute(Constant.VALUE);
        if (value != null && !value.isEmpty()) {
            reason.setValue(value);
        }
        String expression = element.getAttribute(Constant.EXPRESSION);
        if (expression != null && !expression.isEmpty()) {
            reason.setExpression(expression);
        }
        return reason;
    }

    @Override
    public String getTagName() {

        return FAULT;
    }
}
