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

package org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.mediator.flowControl;

import org.apache.axiom.om.OMElement;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.Mediator;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.eip.Foreach;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.InlineSequenceSerializer;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.SerializerUtils;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.mediator.AbstractMediatorSerializer;

public class ForeachMediatorSerializer extends AbstractMediatorSerializer {

    @Override
    protected OMElement serializeSpecificMediator(Mediator m) {

        Foreach foreachMediator = (Foreach) m;
        OMElement foreachElt = fac.createOMElement("foreach", synNS);

        if (foreachMediator.getId() != null) {
            foreachElt.addAttribute("id", foreachMediator.getId(), null);
        }
        if (foreachMediator.getExpression() != null) {
            SerializerUtils.serializeExpression(foreachMediator.getExpression(), foreachElt, "expression",
                    foreachMediator);
        } else {
            handleException("Invalid Foreach mediator. " +
                    "Should have an 'expression' ");
        }

        if (foreachMediator.getSequenceAttribute() != null) {
            foreachElt.addAttribute("sequence", foreachMediator.getSequenceAttribute(), null);
        } else if (foreachMediator.getSequence() != null) {
            OMElement sequenceElt = InlineSequenceSerializer.serializeAnonymousSequence(foreachMediator.getSequence());
            foreachElt.addChild(sequenceElt);
        } else {
            handleException("Invalid Foreach mediator. " +
                    "Should have a 'sequence' ");
        }

        if (foreachMediator.getDescription() != null) {
            foreachElt.addAttribute("description", foreachMediator.getDescription(), null);
        }

        return foreachElt;
    }

    @Override
    public String getMediatorClassName() {

        return Foreach.class.getName();
    }
}
