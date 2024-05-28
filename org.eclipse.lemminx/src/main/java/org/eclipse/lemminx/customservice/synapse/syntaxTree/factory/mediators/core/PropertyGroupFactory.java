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
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.PropertyGroup;
import org.eclipse.lemminx.customservice.synapse.utils.Constant;
import org.eclipse.lemminx.dom.DOMElement;
import org.eclipse.lemminx.dom.DOMNode;

import java.util.ArrayList;
import java.util.List;

public class PropertyGroupFactory extends AbstractMediatorFactory {

    private static final String PROPERTY_GROUP = "propertygroup";

    @Override
    protected Mediator createSpecificMediator(DOMElement element) {

        PropertyGroup propertyGroup = new PropertyGroup();
        propertyGroup.elementNode(element);
        populateAttributes(propertyGroup, element);
        List<DOMNode> children = element.getChildren();
        List<Property> properties = new ArrayList<>();
        for (DOMNode child : children) {
            if (child instanceof DOMElement) {
                DOMElement childElement = (DOMElement) child;
                String tagName = childElement.getTagName();
                if (tagName.equals(Constant.PROPERTY)) {
                    PropertyFactory propertyFactory = new PropertyFactory();
                    Property property = (Property) propertyFactory.create(childElement);
                    properties.add(property);
                }
            }
        }
        propertyGroup.setProperty(properties.toArray(new Property[0]));
        return propertyGroup;
    }

    @Override
    public void populateAttributes(STNode node, DOMElement element) {

        PropertyGroup propertyGroup = (PropertyGroup) node;
        String description = element.getAttribute(Constant.DESCRIPTION);
        if (description != null) {
            propertyGroup.setDescription(description);
        }
    }

    @Override
    public String getTagName() {

        return PROPERTY_GROUP;
    }
}
