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

package org.eclipse.lemminx.customservice.syntaxmodel.factory.mediators.advanced;

import org.eclipse.lemminx.customservice.syntaxmodel.factory.mediators.AbstractMediatorFactory;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.STNode;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.mediator.Mediator;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.mediator.advanced.Event;
import org.eclipse.lemminx.customservice.syntaxmodel.utils.Constant;
import org.eclipse.lemminx.dom.DOMElement;

public class EventFactory extends AbstractMediatorFactory {

    private static final String EVENT = "event";

    @Override
    public Mediator createSpecificMediator(DOMElement element) {

        Event event = new Event();
        event.elementNode(element);
        populateAttributes(event, element);
        return event;
    }

    @Override
    public void populateAttributes(STNode node, DOMElement element) {

        Event event = (Event) node;
        String topic = element.getAttribute(Constant.TOPIC);
        if (topic != null) {
            event.setTopic(topic);
        }
        String expression = element.getAttribute(Constant.EXPRESSION);
        if (expression != null) {
            event.setExpression(expression);
        }
        String description = element.getAttribute(Constant.DESCRIPTION);
        if (description != null) {
            event.setDescription(description);
        }
    }

    @Override
    public String getTagName() {

        return EVENT;
    }
}
