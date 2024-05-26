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
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.advanced.Clone.Clone;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.advanced.Clone.CloneTarget;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.InlineSequenceSerializer;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.endpoint.EndpointSerializer;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.mediator.AbstractMediatorSerializer;

public class CloneMediatorSerializer extends AbstractMediatorSerializer {

    @Override
    protected OMElement serializeSpecificMediator(Mediator m) {

        Clone cloneMediator = (Clone) m;
        OMElement cloneElt = fac.createOMElement("clone", synNS);

        if (cloneMediator.getId() != null) {
            cloneElt.addAttribute("id", cloneMediator.getId(), null);
        }
        if (cloneMediator.isContinueParent()) {
            cloneElt.addAttribute("continueParent", "true", null);
        }
        if (cloneMediator.isSequential()) {
            cloneElt.addAttribute("sequential", "true", null);
        }
        if (cloneMediator.getDescription() != null) {
            cloneElt.addAttribute("description", cloneMediator.getDescription(), null);
        }

        serializeTargets(cloneElt, cloneMediator);

        return cloneElt;
    }

    private void serializeTargets(OMElement cloneElt, Clone cloneMediator) {

        CloneTarget[] targets = cloneMediator.getTarget();
        if (targets != null) {
            for (CloneTarget target : targets) {
                OMElement targetEle = serializeTarget(target);
                cloneElt.addChild(targetEle);
            }
        }
    }

    private OMElement serializeTarget(CloneTarget target) {

        OMElement targetElt = null;
        if (target != null) {
            targetElt = fac.createOMElement("target", synNS);
            if (target.getTo() != null) {
                targetElt.addAttribute("to", target.getTo(), null);
            }
            if (target.getSoapAction() != null) {
                targetElt.addAttribute("soapAction", target.getSoapAction(), null);
            }

            if (target.getSequenceAttribute() != null) {
                targetElt.addAttribute("sequence", target.getSequenceAttribute(), null);
            } else if (target.getSequence() != null) {
                OMElement sequenceElt = InlineSequenceSerializer.serializeAnonymousSequence(target.getSequence());
                targetElt.addChild(sequenceElt);
            }

            if (target.getEndpointAttribute() != null) {
                targetElt.addAttribute("endpoint", target.getEndpointAttribute(), null);
            } else if (target.getEndpoint() != null) {
                OMElement endpointElt = EndpointSerializer.serializeEndpoint(target.getEndpoint());
                targetElt.addChild(endpointElt);
            }
        }
        return targetElt;
    }

    @Override
    public String getMediatorClassName() {

        return Clone.class.getName();
    }
}
