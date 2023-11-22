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

package org.eclipse.lemminx.customservice.syntaxmodel.factory.mediators.transformation;

import org.eclipse.lemminx.customservice.syntaxmodel.factory.mediators.AbstractMediatorFactory;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.STNode;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.mediator.Mediator;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.mediator.core.MediatorProperty;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.mediator.transformation.Jsontransform;
import org.eclipse.lemminx.customservice.syntaxmodel.utils.Constant;
import org.eclipse.lemminx.customservice.syntaxmodel.utils.Utils;
import org.eclipse.lemminx.dom.DOMElement;
import org.eclipse.lemminx.dom.DOMNode;

import java.util.ArrayList;
import java.util.List;

public class JsonTransformFactory extends AbstractMediatorFactory {

    private static final String JSON_TRANSFORM = "jsontransform";

    @Override
    public Mediator createSpecificMediator(DOMElement element) {

        Jsontransform jsontransform = new Jsontransform();
        jsontransform.elementNode(element);
        populateAttributes(jsontransform, element);
        List<DOMNode> childNodes = element.getChildren();
        List<MediatorProperty> properties = new ArrayList<>();
        if (childNodes != null && !childNodes.isEmpty()) {
            for (DOMNode child : childNodes) {
                if (child.getNodeName().equalsIgnoreCase(Constant.PROPERTY)) {
                    MediatorProperty property = Utils.createMediatorProperty(child);
                    properties.add(property);
                }
            }
            jsontransform.setProperty(properties.toArray(new MediatorProperty[properties.size()]));
        }
        return jsontransform;
    }

    @Override
    public void populateAttributes(STNode node, DOMElement element) {

        String schema = element.getAttribute(Constant.SCHEMA);
        if (schema != null && !schema.isEmpty()) {
            ((Jsontransform) node).setSchema(schema);
        }
    }

    @Override
    public String getTagName() {

        return JSON_TRANSFORM;
    }
}
