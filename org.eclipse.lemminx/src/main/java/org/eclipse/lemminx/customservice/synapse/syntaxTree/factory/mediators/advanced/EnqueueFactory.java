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

package org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.advanced;

import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.AbstractMediatorFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.STNode;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.Mediator;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.advanced.Enqueue;
import org.eclipse.lemminx.customservice.synapse.utils.Constant;
import org.eclipse.lemminx.customservice.synapse.utils.Utils;
import org.eclipse.lemminx.dom.DOMElement;

public class EnqueueFactory extends AbstractMediatorFactory {

    private static final String ENQUEUE = "enqueue";

    @Override
    public Mediator createSpecificMediator(DOMElement element) {

        Enqueue enqueue = new Enqueue();
        enqueue.elementNode(element);
        populateAttributes(enqueue, element);
        return enqueue;
    }

    @Override
    public void populateAttributes(STNode node, DOMElement element) {

        Enqueue enqueue = (Enqueue) node;
        String priority = element.getAttribute(Constant.PRIORITY);
        if (priority != null) {
            enqueue.setPriority(Utils.parseInt(priority));
        }
        String sequence = element.getAttribute(Constant.SEQUENCE);
        if (sequence != null) {
            enqueue.setSequence(sequence);
        }
        String executor = element.getAttribute(Constant.EXECUTOR);
        if (executor != null) {
            enqueue.setExecutor(executor);
        }
        String description = element.getAttribute(Constant.DESCRIPTION);
        if (description != null) {
            enqueue.setDescription(description);
        }
    }

    @Override
    public String getTagName() {

        return ENQUEUE;
    }
}
