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

package org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.core;

import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.AbstractMediatorFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.STNode;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.Mediator;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.Property;
import org.eclipse.lemminx.customservice.synapse.utils.Constant;
import org.eclipse.lemminx.customservice.synapse.utils.Utils;
import org.eclipse.lemminx.dom.DOMElement;
import org.eclipse.lemminx.dom.DOMNode;

public class PropertyFactory extends AbstractMediatorFactory {

    private static final String PROPERTY = "property";

    @Override
    public Mediator createSpecificMediator(DOMElement element) {

        Property property = new Property();
        property.elementNode(element);
        populateAttributes(property, element);
        DOMNode inline = element.getFirstChild();
        if (inline != null) {
            String inlineString = Utils.getInlineString(inline);
            property.setAny(inlineString);
        }
        return property;
    }

    @Override
    public void populateAttributes(STNode node, DOMElement element) {

        Property property = (Property) node;
        String name = element.getAttribute(Constant.NAME);
        if (name != null) {
            property.setName(name);
        }
        String value = element.getAttribute(Constant.VALUE);
        if (value != null) {
            property.setValue(value);
        }
        String expression = element.getAttribute(Constant.EXPRESSION);
        if (expression != null) {
            property.setExpression(expression);
        }
        String scope = element.getAttribute(Constant.SCOPE);
        if (scope != null) {
            property.setScope(scope);
        }
        String type = element.getAttribute(Constant.TYPE);
        if (type != null) {
            property.setType(type);
        }
        String pattern = element.getAttribute(Constant.PATTERN);
        if (pattern != null) {
            property.setPattern(pattern);
        }
        String action = element.getAttribute(Constant.ACTION);
        if (action != null) {
            property.setAction(action);
        }
        String group = element.getAttribute(Constant.GROUP);
        if (group != null) {
            property.setGroup(group);
        }
        String description = element.getAttribute(Constant.DESCRIPTION);
        if (description != null) {
            property.setDescription(description);
        }
    }

    @Override
    public String getTagName() {

        return PROPERTY;
    }
}
