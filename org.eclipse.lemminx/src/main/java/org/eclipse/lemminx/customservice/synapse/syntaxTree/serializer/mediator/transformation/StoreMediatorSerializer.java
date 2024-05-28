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
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.Store;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.mediator.AbstractMediatorSerializer;

public class StoreMediatorSerializer extends AbstractMediatorSerializer {

    @Override
    protected OMElement serializeSpecificMediator(Mediator m) {

        Store storeMediator = (Store) m;
        OMElement storeElt = fac.createOMElement("store", synNS);

        if (storeMediator.getMessageStore() != null) {
            storeElt.addAttribute("messageStore", storeMediator.getMessageStore(), null);
        }
        if (storeMediator.getSequence() != null) {
            storeElt.addAttribute("sequence", storeMediator.getSequence(), null);
        }
        if (storeMediator.getDescription() != null) {
            storeElt.addAttribute("description", storeMediator.getDescription(), null);
        }
        return storeElt;
    }

    @Override
    public String getMediatorClassName() {

        return Store.class.getName();
    }
}
