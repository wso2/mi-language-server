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

package org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.mediator.core;

import org.apache.axiom.om.OMElement;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.endpoint.NamedEndpoint;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.Mediator;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.Send;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.SerializerUtils;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.endpoint.EndpointSerializer;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.mediator.AbstractMediatorSerializer;

public class SendMediatorSerializer extends AbstractMediatorSerializer {

    @Override
    protected OMElement serializeSpecificMediator(Mediator m) {

        Send sendMediator = (Send) m;
        OMElement sendElt = fac.createOMElement("send", synNS);

        if (sendMediator.getReceive() != null) {
            SerializerUtils.serializeExpression(sendMediator.getReceive(), sendElt, "receive", sendMediator);
        }
        if (sendMediator.isBuildmessage()) {
            sendElt.addAttribute("buildmessage", "true", null);
        }
        if (sendMediator.getDescription() != null) {
            sendElt.addAttribute("description", sendMediator.getDescription(), null);
        }

        serializeEndpoint(sendMediator.getEndpoint(), sendElt);
        return sendElt;
    }

    private void serializeEndpoint(NamedEndpoint endpoint, OMElement sendElt) {

        if (endpoint != null) {
            OMElement endpointElt;
            if (endpoint.getKey() != null) {
                endpointElt = fac.createOMElement("endpoint", synNS);
                endpointElt.addAttribute("key", endpoint.getKey(), nullNS);
            } else {
                endpointElt = EndpointSerializer.serializeEndpoint(endpoint);
            }
            sendElt.addChild(endpointElt);
        } else {
            handleException("Endpoint is required for the Call mediator");
        }
    }

    @Override
    public String getMediatorClassName() {

        return Send.class.getName();
    }
}
