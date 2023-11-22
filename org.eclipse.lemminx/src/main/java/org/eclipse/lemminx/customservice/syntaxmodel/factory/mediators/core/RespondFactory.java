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

package org.eclipse.lemminx.customservice.syntaxmodel.factory.mediators.core;

import org.eclipse.lemminx.customservice.syntaxmodel.factory.mediators.AbstractMediatorFactory;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.STNode;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.mediator.Mediator;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.mediator.core.Respond;
import org.eclipse.lemminx.customservice.syntaxmodel.utils.Constant;
import org.eclipse.lemminx.dom.DOMElement;

public class RespondFactory extends AbstractMediatorFactory {

    private static final String RESPOND = "respond";

    @Override
    public Mediator createSpecificMediator(DOMElement element) {

        Respond respond = new Respond();
        respond.elementNode(element);
        populateAttributes(respond, element);
        return respond;
    }

    @Override
    public void populateAttributes(STNode node, DOMElement element) {

        Respond respond = (Respond) node;
        String description = element.getAttribute(Constant.DESCRIPTION);
        if (description != null && !description.isEmpty()) {
            respond.setDescription(description);
        }
    }

    @Override
    public String getTagName() {

        return RESPOND;
    }

}
