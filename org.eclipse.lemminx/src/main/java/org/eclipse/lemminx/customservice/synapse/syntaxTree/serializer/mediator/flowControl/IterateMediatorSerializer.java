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
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.eip.Iterate;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.misc.targets.Target;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.AnonymousSequenceSerializer;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.SerializerUtils;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.mediator.AbstractMediatorSerializer;

public class IterateMediatorSerializer extends AbstractMediatorSerializer {

    @Override
    protected OMElement serializeSpecificMediator(Mediator m) {

        Iterate iterateMediator = (Iterate) m;
        OMElement iterateElt = fac.createOMElement("iterate", synNS);

        if (iterateMediator.getId() != null) {
            iterateElt.addAttribute("id", iterateMediator.getId(), null);
        }
        if (iterateMediator.getExpression() != null) {
            SerializerUtils.serializeExpression(iterateMediator.getExpression(), iterateElt, "expression",
                    iterateMediator);
        } else {
            handleException("Invalid Iterate mediator. " +
                    "Should have an 'expression' ");
        }
        if (iterateMediator.getAttachPath() != null) {
            SerializerUtils.serializeExpression(iterateMediator.getAttachPath(), iterateElt, "attachPath",
                    iterateMediator);
        }
        if (iterateMediator.isPreservePayload()) {
            iterateElt.addAttribute("preservePayload", "true", null);
        }
        if (iterateMediator.isContinueParent()) {
            iterateElt.addAttribute("continueParent", "true", null);
        }
        if (iterateMediator.isSequential()) {
            iterateElt.addAttribute("sequential", "true", null);
        }
        if (iterateMediator.getDescription() != null) {
            iterateElt.addAttribute("description", iterateMediator.getDescription(), null);
        }

        serializeTarget(iterateElt, iterateMediator.getTarget());

        return iterateElt;
    }

    private void serializeTarget(OMElement iterateElt, Target target) {

        if (target != null) {
            OMElement targetEle = fac.createOMElement("target", synNS);
            if (target.getSequenceAttribute() != null) {
                targetEle.addAttribute("sequence", target.getSequenceAttribute(), null);
            } else if (target.getSequence() != null) {
                OMElement sequenceElement = AnonymousSequenceSerializer.serializeAnonymousSequence(target.getSequence());
                targetEle.addChild(sequenceElement);
            } else {
                handleException("Invalid target mediator. " +
                        "Should have a 'sequence' ");
            }
            iterateElt.addChild(targetEle);
        } else {
            handleException("Invalid Iterate mediator. " +
                    "Should have a 'target' ");
        }
    }

    @Override
    public String getMediatorClassName() {

        return Iterate.class.getName();
    }
}
