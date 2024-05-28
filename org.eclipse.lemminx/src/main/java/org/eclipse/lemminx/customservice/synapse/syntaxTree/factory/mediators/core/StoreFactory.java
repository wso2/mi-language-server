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
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.Store;
import org.eclipse.lemminx.customservice.synapse.utils.Constant;
import org.eclipse.lemminx.dom.DOMElement;

public class StoreFactory extends AbstractMediatorFactory {

    private static final String STORE = "store";

    @Override
    protected Mediator createSpecificMediator(DOMElement element) {

        Store store = new Store();
        store.elementNode(element);
        populateAttributes(store, element);
        return store;
    }

    @Override
    public void populateAttributes(STNode node, DOMElement element) {

        Store store = (Store) node;
        String messageStore = element.getAttribute(Constant.MESSAGE_STORE);
        if (messageStore != null) {
            store.setMessageStore(messageStore);
        }
        String sequence = element.getAttribute(Constant.SEQUENCE);
        if (sequence != null) {
            store.setSequence(sequence);
        }
        String description = element.getAttribute(Constant.DESCRIPTION);
        if (description != null) {
            store.setDescription(description);
        }
    }

    @Override
    public String getTagName() {

        return STORE;
    }
}
