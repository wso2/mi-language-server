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

package org.eclipse.lemminx.customservice.syntaxmodel.factory.mediators.eip;

import org.eclipse.lemminx.customservice.syntaxmodel.factory.mediators.AbstractMediatorFactory;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.STNode;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.mediator.Mediator;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.mediator.eip.Foreach;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.misc.common.Sequence;
import org.eclipse.lemminx.customservice.syntaxmodel.utils.Constant;
import org.eclipse.lemminx.customservice.syntaxmodel.utils.Utils;
import org.eclipse.lemminx.dom.DOMElement;
import org.eclipse.lemminx.dom.DOMNode;

import java.util.List;

public class ForeachFactory extends AbstractMediatorFactory {

    private static final String FOR_EACH = "foreach";

    @Override
    public Mediator createSpecificMediator(DOMElement element) {

        Foreach foreach = new Foreach();
        foreach.elementNode(element);
        populateAttributes(foreach, element);
        List<DOMNode> children = element.getChildren();
        if (children != null && !children.isEmpty()) {
            for (DOMNode node : children) {
                Sequence sequence = Utils.createSequence(node);
                foreach.setSequence(sequence);
            }
        }
        return foreach;
    }

    @Override
    public void populateAttributes(STNode node, DOMElement element) {

        String expression = element.getAttribute(Constant.EXPRESSION);
        if (expression != null && !expression.isEmpty()) {
            ((Foreach) node).setExpression(expression);
        }
        String sequenceAttribute = element.getAttribute(Constant.SEQUENCE);
        if (sequenceAttribute != null && !sequenceAttribute.isEmpty()) {
            ((Foreach) node).setSequenceAttribute(sequenceAttribute);
        }
        String id = element.getAttribute(Constant.ID);
        if (id != null && !id.isEmpty()) {
            ((Foreach) node).setId(id);
        }
        String description = element.getAttribute(Constant.DESCRIPTION);
        if (description != null && !description.isEmpty()) {
            ((Foreach) node).setDescription(description);
        }
    }

    @Override
    public String getTagName() {

        return FOR_EACH;
    }

}
