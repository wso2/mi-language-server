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

package org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.mediator.core;

import org.apache.axiom.om.OMElement;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.Mediator;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.Property;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.SerializerUtils;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.mediator.AbstractMediatorSerializer;
import org.eclipse.lemminx.customservice.synapse.utils.Utils;

public class PropertyMediatorSerializer extends AbstractMediatorSerializer {

    @Override
    protected OMElement serializeSpecificMediator(Mediator m) {

        Property property = (Property) m;
        OMElement propertyElt = fac.createOMElement("property", synNS);

        if (property.getName() != null) {
            propertyElt.addAttribute("name", property.getName(), nullNS);
        } else {
            handleException("Invalid property mediator. Name is required");
        }

        if (property.getValue() != null) {
            propertyElt.addAttribute("value", property.getValue(), nullNS);
        } else if (property.getAny() != null) {
            OMElement inline = SerializerUtils.stringToOM(property.getAny().toString());
            if (inline != null) {
                propertyElt.addChild(inline);
            }
        } else if (property.getExpression() != null) {
            SerializerUtils.serializeExpression(property.getExpression(), propertyElt, "expression", property);
        } else if ("set".equals(property.getAction())) {
            handleException("Invalid property mediator. Value or expression is required");
        }

        if (property.getScope() != null) {
            propertyElt.addAttribute("scope", property.getScope().getValue(), nullNS);
        }

        if ("remove".equals(property.getAction())) {
            propertyElt.addAttribute("action", property.getAction(), nullNS);
        } else if (property.getType() != null) {
            propertyElt.addAttribute("type", property.getType().name(), nullNS);
        }

        if (property.getPattern() != null) {
            propertyElt.addAttribute("pattern", property.getPattern(), nullNS);
            int group = Utils.parseInt(property.getGroup());
            if (group >= 0) {
                propertyElt.addAttribute("group", property.getGroup(), nullNS);
            }
        }

        if (property.getDescription() != null) {
            propertyElt.addAttribute("description", property.getDescription(), nullNS);
        }

        return propertyElt;
    }

    @Override
    public String getMediatorClassName() {

        return Property.class.getName();
    }
}
