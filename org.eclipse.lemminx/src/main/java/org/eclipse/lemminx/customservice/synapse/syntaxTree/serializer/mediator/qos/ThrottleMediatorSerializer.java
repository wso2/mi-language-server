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

package org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.mediator.qos;

import org.apache.axiom.om.OMElement;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.Mediator;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.filter.throttle.Throttle;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.filter.throttle.ThrottlePolicy;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.AnonymousSequenceSerializer;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.mediator.AbstractMediatorSerializer;

public class ThrottleMediatorSerializer extends AbstractMediatorSerializer {

    @Override
    protected OMElement serializeSpecificMediator(Mediator m) {

        Throttle throttle = (Throttle) m;
        OMElement throttleElement = fac.createOMElement("throttle", synNS);

        serializeAttributes(throttleElement, throttle);

        if (throttle.getPolicy() != null) {
            serializePolicy(throttleElement, throttle.getPolicy());
        }

        serializeSequences(throttleElement, throttle);

        return throttleElement;
    }

    private void serializeAttributes(OMElement throttleElement, Throttle throttle) {

        if (throttle.getId() != null) {
            throttleElement.addAttribute("id", throttle.getId(), null);
        }

        if (throttle.getDescription() != null) {
            throttleElement.addAttribute("description", throttle.getDescription(), null);
        }

    }

    private void serializePolicy(OMElement throttleElement, ThrottlePolicy policy) {

    }

    private void serializeSequences(OMElement throttleElement, Throttle throttle) {

        if (throttle.getOnAcceptAttribute() != null) {
            throttleElement.addAttribute("onAccept", throttle.getOnAcceptAttribute(), null);
        } else {
            OMElement onAcceptElement;
            if (throttle.getOnAccept() != null) {
                onAcceptElement = AnonymousSequenceSerializer.serializeAnonymousSequence(throttle.getOnAccept());
                onAcceptElement.setLocalName("onAccept");
            } else {
                onAcceptElement = fac.createOMElement("onAccept", synNS);
            }
            throttleElement.addChild(onAcceptElement);
        }

        if (throttle.getOnRejectAttribute() != null) {
            throttleElement.addAttribute("onReject", throttle.getOnRejectAttribute(), null);
        } else {
            OMElement onRejectElement;
            if (throttle.getOnReject() != null) {
                onRejectElement = AnonymousSequenceSerializer.serializeAnonymousSequence(throttle.getOnReject());
                onRejectElement.setLocalName("onReject");
            } else {
                onRejectElement = fac.createOMElement("onReject", synNS);
            }
            throttleElement.addChild(onRejectElement);
        }
    }

    @Override
    public String getMediatorClassName() {

        return Throttle.class.getName();
    }
}
