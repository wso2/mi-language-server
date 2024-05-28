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
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.Header;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.HeaderScope;
import org.eclipse.lemminx.customservice.synapse.utils.Constant;
import org.eclipse.lemminx.customservice.synapse.utils.Utils;
import org.eclipse.lemminx.dom.DOMElement;
import org.eclipse.lemminx.dom.DOMNode;

public class HeaderFactory extends AbstractMediatorFactory {

    private static final String HEADER = "header";

    @Override
    protected Mediator createSpecificMediator(DOMElement element) {

        Header header = new Header();
        header.elementNode(element);
        populateAttributes(header, element);
        DOMNode inline = element.getFirstChild();
        if (inline != null) {
            String inlineString = Utils.getInlineString(inline);
            header.setAny(inlineString);
        }
        return header;
    }

    @Override
    public void populateAttributes(STNode node, DOMElement element) {

        String name = element.getAttribute(Constant.NAME);
        if (name != null) {
            ((Header) node).setName(name);
        }
        String value = element.getAttribute(Constant.VALUE);
        if (value != null) {
            ((Header) node).setValue(value);
        }
        String expression = element.getAttribute(Constant.EXPRESSION);
        if (expression != null) {
            ((Header) node).setExpression(expression);
        }
        String action = element.getAttribute(Constant.ACTION);
        if (action != null) {
            ((Header) node).setAction(action);
        }
        String scope = element.getAttribute(Constant.SCOPE);
        HeaderScope headerScope = HeaderScope.DEFAULT;
        if (headerScope != null) {
            ((Header) node).setScope(headerScope);
        }
        String description = element.getAttribute(Constant.DESCRIPTION);
        if (description != null) {
            ((Header) node).setDescription(description);
        }
    }

    @Override
    public String getTagName() {

        return HEADER;
    }

}
