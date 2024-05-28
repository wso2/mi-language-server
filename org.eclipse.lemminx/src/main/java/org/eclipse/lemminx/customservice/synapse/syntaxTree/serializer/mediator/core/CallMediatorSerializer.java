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
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.call.Call;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.call.CallSource;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.call.CallTarget;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.SerializerUtils;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.endpoint.EndpointSerializer;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.mediator.AbstractMediatorSerializer;

public class CallMediatorSerializer extends AbstractMediatorSerializer {

    @Override
    protected OMElement serializeSpecificMediator(Mediator m) {

        Call call = (Call) m;
        OMElement callElt = fac.createOMElement("call", synNS);

        serializeEndpoint(call.getEndpoint(), callElt);
        serializeSource(call.getSource(), callElt);
        serializeTarget(call.getTarget(), callElt);

        if (call.isBlocking()) {
            callElt.addAttribute("blocking", "true", nullNS);
            if (!call.getInitAxis2ClientOptions()) {
                callElt.addAttribute("initAxis2ClientOptions", "false", nullNS);
            }
        }

        if (call.getDescription() != null) {
            callElt.addAttribute("description", call.getDescription(), nullNS);
        }

        return callElt;
    }

    private void serializeEndpoint(NamedEndpoint endpoint, OMElement callElt) {

        if (endpoint != null) {
            OMElement endpointElt;
            if (endpoint.getKey() != null) {
                endpointElt = fac.createOMElement("endpoint", synNS);
                endpointElt.addAttribute("key", endpoint.getKey(), nullNS);
            } else {
                endpointElt = EndpointSerializer.serializeEndpoint(endpoint);
            }
            callElt.addChild(endpointElt);
        } else {
            handleException("Endpoint is required for the Call mediator");
        }
    }

    private void serializeSource(CallSource source, OMElement callElt) {

        if (source != null) {
            OMElement sourceElt = fac.createOMElement("source", synNS);
            String sourceType = source.getType().name();
            switch (sourceType) {
                case "body":
                    sourceElt.addAttribute("type", "body", nullNS);
                    break;
                case "property":
                    sourceElt.addAttribute("type", "property", nullNS);
                    sourceElt.addAttribute("contentType", source.getContentType(), nullNS);
                    String property = source.getTextNode();
                    sourceElt.setText(property);
                    break;
                case "inline":
                    sourceElt.addAttribute("type", "inline", nullNS);
                    sourceElt.addAttribute("contentType", source.getContentType(), nullNS);
                    if (source.getContent() != null) {
                        OMElement contentElt = SerializerUtils.stringToOM(source.getContent().toString());
                        if (contentElt != null) {
                            sourceElt.addChild(contentElt);
                        }
                    }
                    break;
                case "custom":
                    sourceElt.addAttribute("type", "custom", nullNS);
                    sourceElt.addAttribute("contentType", source.getContentType(), nullNS);
                    String xpath = source.getTextNode();
                    sourceElt.setText(xpath);
                    SerializerUtils.serializeNamespaces(source, sourceElt);
                    break;
            }
            callElt.addChild(sourceElt);
        }
    }

    private void serializeTarget(CallTarget target, OMElement callElt) {

        if (target != null) {
            OMElement targetElt = fac.createOMElement("target", synNS);
            String targetType = target.getType().name();
            switch (targetType) {
                case "body":
                    targetElt.addAttribute("type", "body", nullNS);
                    break;
                case "property":
                    targetElt.addAttribute("type", "property", nullNS);
                    String property = target.getTextNode();
                    targetElt.setText(property);
                    break;
            }
            callElt.addChild(targetElt);
        }
    }

    @Override
    public String getMediatorClassName() {

        return Call.class.getName();
    }
}
