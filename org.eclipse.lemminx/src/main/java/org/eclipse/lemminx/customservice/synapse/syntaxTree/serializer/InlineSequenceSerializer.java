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

package org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.Mediator;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.misc.common.Sequence;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.mediator.AbstractMediatorSerializer;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.mediator.MediatorSerializerFinder;
import org.eclipse.lemminx.customservice.synapse.utils.Constant;

import java.util.List;

public class InlineSequenceSerializer {

    private static final OMFactory fac = OMAbstractFactory.getOMFactory();

    public static OMElement serializeAnonymousSequence(Sequence sequence) {

        List<Mediator> mediatorList = null;
        if (sequence != null) {
            mediatorList = sequence.getMediatorList();
        }
        OMElement sequenceElt = serializeAnonymousSequence(mediatorList);
        return sequenceElt;
    }

    public static OMElement serializeAnonymousSequence(List<Mediator> mediatorList) {

        OMElement sequenceElt = fac.createOMElement("sequence", Constant.SYNAPSE_OMNAMESPACE);

        serializeAnonymousSequence(mediatorList, sequenceElt);
        return sequenceElt;
    }

    public static OMElement serializeAnonymousSequence(List<Mediator> mediatorList, OMElement parentElt) {

        if (mediatorList != null) {
            for (Mediator mediator : mediatorList) {
                AbstractMediatorSerializer serializer = MediatorSerializerFinder.getInstance().getSerializer(mediator);
                if (serializer != null) {
                    serializer.serializeMediator(parentElt, mediator);
                }
            }
        }
        return parentElt;
    }

}
