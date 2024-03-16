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

import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.endpoint.EndpointFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.AbstractMediatorFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.STNode;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.endpoint.NamedEndpoint;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.Mediator;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.call.Call;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.call.CallSource;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.call.CallTarget;
import org.eclipse.lemminx.customservice.synapse.utils.Constant;
import org.eclipse.lemminx.dom.DOMElement;
import org.eclipse.lemminx.dom.DOMNode;

import java.util.List;

public class CallFactory extends AbstractMediatorFactory {

    private static final String CALL = "call";

    @Override
    public Mediator createSpecificMediator(DOMElement element) {

        Call call = new Call();
        call.elementNode(element);
        populateAttributes(call, element);
        List<DOMNode> children = element.getChildren();
        if (children != null && !children.isEmpty()) {
            for (DOMNode node : children) {
                if (node.getNodeName().equalsIgnoreCase(Constant.SOURCE)) {
                    CallSource callSource = new CallSource();
                    callSource.elementNode((DOMElement) node);
                    populateSourceAttributes(callSource, (DOMElement) node);
                    call.setSource(callSource);
                } else if (node.getNodeName().equalsIgnoreCase(Constant.TARGET)) {
                    CallTarget callTarget = new CallTarget();
                    callTarget.elementNode((DOMElement) node);
                    populateTargetAttributes(callTarget, (DOMElement) node);
                    call.setTarget(callTarget);
                } else if (node.getNodeName().equalsIgnoreCase(Constant.ENDPOINT)) {
                    EndpointFactory endpointFactory = new EndpointFactory();
                    NamedEndpoint namedEndpoint = (NamedEndpoint) endpointFactory.create((DOMElement) node);
                    call.setEndpoint(namedEndpoint);
                }
            }
        }
        return call;
    }

    @Override
    public void populateAttributes(STNode node, DOMElement element) {

        String blocking = element.getAttribute(Constant.BLOCKING);
        if (blocking != null) {
            ((Call) node).setBlocking(Boolean.parseBoolean(blocking));
        }
        String initAxis2ClientOptions = element.getAttribute(Constant.INIT_AXIS2_CLIENT_OPTIONS);
        if (initAxis2ClientOptions != null) {
            ((Call) node).setInitAxis2ClientOptions(Boolean.parseBoolean(initAxis2ClientOptions));
        } else {
            ((Call) node).setInitAxis2ClientOptions(Boolean.TRUE);
        }
        String description = element.getAttribute(Constant.DESCRIPTION);
        if (description != null) {
            ((Call) node).setDescription(description);
        }
    }

    private void populateSourceAttributes(CallSource callSource, DOMElement element) {

        String contentType = element.getAttribute(Constant.CONTENT_TYPE);
        if (contentType != null) {
            callSource.setContentType(contentType);
        }
        String type = element.getAttribute(Constant.TYPE);
        if (type != null) {
            callSource.setType(type);
        }
    }

    private void populateTargetAttributes(CallTarget callTarget, DOMElement element) {

        String type = element.getAttribute(Constant.TYPE);
        if (type != null) {
            callTarget.setType(type);
        }
    }

    @Override
    public String getTagName() {

        return CALL;
    }

}
