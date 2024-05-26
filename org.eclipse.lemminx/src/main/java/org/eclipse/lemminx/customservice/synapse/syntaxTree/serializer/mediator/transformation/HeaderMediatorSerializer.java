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

package org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.mediator.transformation;

import org.apache.axiom.om.OMElement;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.Mediator;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.Header;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.SerializerUtils;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.mediator.AbstractMediatorSerializer;

public class HeaderMediatorSerializer extends AbstractMediatorSerializer {

    @Override
    protected OMElement serializeSpecificMediator(Mediator m) {

        Header headerMediator = (Header) m;
        OMElement headerElt = fac.createOMElement("header", synNS);

        if (headerMediator.getName() != null) {
            SerializerUtils.serializeExpression(headerMediator.getName(), headerElt, "name", headerMediator);
        }
        if (headerMediator.getAction() != null) {
            headerElt.addAttribute("action", headerMediator.getAction(), null);
        }
        if (headerMediator.getScope() != null) {
            headerElt.addAttribute("scope", headerMediator.getScope().getValue(), null);
        }
        if (headerMediator.getAction() != null) {
            String action = headerMediator.getAction();
            if ("set".equals(action)) {
                if (headerMediator.getValue() != null) {
                    headerElt.addAttribute("value", headerMediator.getValue(), null);
                } else if (headerMediator.getAny() != null) {
                    String inline = headerMediator.getAny().toString();
                    OMElement inlineElt = SerializerUtils.stringToOM(inline);
                    headerElt.addChild(inlineElt);
                } else if (headerMediator.getExpression() != null) {
                    SerializerUtils.serializeExpression(headerMediator.getExpression(), headerElt,
                            "expression", headerMediator);
                }
            } else if (!"remove".equals(action)) {
                handleException("Invalid action for header mediator. Action should be either 'set' or 'remove'");
            }
        }
        if (headerMediator.getDescription() != null) {
            headerElt.addAttribute("description", headerMediator.getDescription(), null);
        }
        return headerElt;
    }

    @Override
    public String getMediatorClassName() {

        return Header.class.getName();
    }
}
