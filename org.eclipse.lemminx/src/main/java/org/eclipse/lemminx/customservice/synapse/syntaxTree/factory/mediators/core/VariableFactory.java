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

package org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.core;

import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.AbstractMediatorFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.STNode;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.Mediator;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.PropertyMediatorType;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.Variable;
import org.eclipse.lemminx.customservice.synapse.utils.Constant;
import org.eclipse.lemminx.customservice.synapse.utils.Utils;
import org.eclipse.lemminx.dom.DOMElement;

public class VariableFactory extends AbstractMediatorFactory {

    private static final String VARIABLE = "variable";

    @Override
    protected Mediator createSpecificMediator(DOMElement element) {

        Variable variable = new Variable();
        variable.elementNode(element);
        populateAttributes(variable, element);
        return variable;
    }

    @Override
    public void populateAttributes(STNode node, DOMElement element) {

        Variable variable = (Variable) node;
        String name = element.getAttribute(Constant.NAME);
        if (name != null) {
            variable.setName(name);
        }
        String value = element.getAttribute(Constant.VALUE);
        if (value != null) {
            variable.setValue(value);
        }
        String expression = element.getAttribute(Constant.EXPRESSION);
        if (expression != null) {
            variable.setExpression(expression);
        }
        String type = element.getAttribute(Constant.TYPE);
        PropertyMediatorType typeEnum = Utils.getEnumFromValue(type, PropertyMediatorType.class);
        if (typeEnum != null) {
            variable.setType(typeEnum);
        }
        String action = element.getAttribute(Constant.ACTION);
        if (action != null) {
            variable.setAction(action);
        }
        String description = element.getAttribute(Constant.DESCRIPTION);
        if (description != null) {
            variable.setDescription(description);
        }
    }

    @Override
    public String getTagName() {

        return VARIABLE;
    }
}
