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
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.PropertyGroup;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.mediator.AbstractMediatorSerializer;

public class PropertyGroupMediatorSerializer extends AbstractMediatorSerializer {

    @Override
    protected OMElement serializeSpecificMediator(Mediator m) {

        PropertyGroup propertyGroup = (PropertyGroup) m;
        OMElement propertyGroupElt = fac.createOMElement("propertyGroup", synNS);

        Property[] properties = propertyGroup.getProperty();
        if (properties != null) {
            PropertyMediatorSerializer propertyMediatorSerializer = new PropertyMediatorSerializer();
            for (Property property : properties) {
                OMElement propertyElt = propertyMediatorSerializer.serializeSpecificMediator(property);
                propertyGroupElt.addChild(propertyElt);
            }
        }

        if (propertyGroup.getDescription() != null) {
            propertyGroupElt.addAttribute("description", propertyGroup.getDescription(), nullNS);
        }
        return propertyGroupElt;
    }

    @Override
    public String getMediatorClassName() {

        return PropertyGroup.class.getName();
    }
}
