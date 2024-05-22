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
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.transformation.xquery.Xquery;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.transformation.xquery.XqueryVariable;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.transformation.xquery.XqueryVariableType;
import org.eclipse.lemminx.customservice.synapse.utils.Constant;
import org.eclipse.lemminx.customservice.synapse.utils.Utils;
import org.eclipse.lemminx.dom.DOMElement;
import org.eclipse.lemminx.dom.DOMNode;

import java.util.ArrayList;
import java.util.List;

public class XqueryFactory extends AbstractMediatorFactory {

    private static final String XQUERY = "xquery";

    @Override
    protected Mediator createSpecificMediator(DOMElement element) {

        Xquery xquery = new Xquery();
        xquery.elementNode(element);
        populateAttributes(xquery, element);
        List<DOMNode> childNodes = element.getChildren();
        List<XqueryVariable> xqueryVariables = new ArrayList<>();
        if (childNodes != null && !childNodes.isEmpty()) {
            for (DOMNode childNode : childNodes) {
                if (childNode.getNodeName().equalsIgnoreCase(Constant.VARIABLE)) {
                    XqueryVariable xqueryVariable = createXqueryVariable(childNode);
                    xqueryVariables.add(xqueryVariable);
                }
            }
            xquery.setVariable(xqueryVariables.toArray(new XqueryVariable[xqueryVariables.size()]));
        }
        return xquery;
    }

    @Override
    public void populateAttributes(STNode node, DOMElement element) {

        String key = element.getAttribute(Constant.KEY);
        if (key != null) {
            ((Xquery) node).setKey(key);
        }
        String target = element.getAttribute(Constant.TARGET);
        if (target != null) {
            ((Xquery) node).setTarget(target);
        }
        String description = element.getAttribute(Constant.DESCRIPTION);
        if (description != null) {
            ((Xquery) node).setDescription(description);
        }
    }

    private XqueryVariable createXqueryVariable(DOMNode element) {

        XqueryVariable xqueryVariable = new XqueryVariable();
        xqueryVariable.elementNode((DOMElement) element);
        String name = element.getAttribute(Constant.NAME);
        if (name != null) {
            xqueryVariable.setName(name);
        }
        String type = element.getAttribute(Constant.TYPE);
        XqueryVariableType typeEnum = Utils.getEnumFromValue(type, XqueryVariableType.class);
        if (typeEnum != null) {
            xqueryVariable.setType(typeEnum);
        }
        String expression = element.getAttribute(Constant.EXPRESSION);
        if (expression != null) {
            xqueryVariable.setExpression(expression);
        }
        String value = element.getAttribute(Constant.VALUE);
        if (value != null) {
            xqueryVariable.setValue(value);
        }
        String key = element.getAttribute(Constant.KEY);
        if (key != null) {
            xqueryVariable.setKey(key);
        }
        return xqueryVariable;
    }

    @Override
    public String getTagName() {

        return XQUERY;
    }
}
