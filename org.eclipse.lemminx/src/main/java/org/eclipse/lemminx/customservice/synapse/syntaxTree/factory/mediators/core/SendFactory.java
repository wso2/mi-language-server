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
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.Send;
import org.eclipse.lemminx.customservice.synapse.utils.Constant;
import org.eclipse.lemminx.customservice.synapse.utils.Utils;
import org.eclipse.lemminx.dom.DOMDocument;
import org.eclipse.lemminx.dom.DOMElement;
import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lsp4j.Position;

import java.util.ArrayList;
import java.util.List;

public class SendFactory extends AbstractMediatorFactory {

    private static final String SEND = "send";

    @Override
    protected Mediator createSpecificMediator(DOMElement element) {

        Send send = new Send();
        send.elementNode(element);
        populateAttributes(send, element);
        List<DOMNode> children = element.getChildren();
        List<NamedEndpoint> endpoints = new ArrayList<>();
        DOMNode endpointNode = Utils.getChildNodeByName(element, Constant.ENDPOINT);
        if (endpointNode != null) {
            EndpointFactory endpointFactory = new EndpointFactory();
            NamedEndpoint namedEndpoint = (NamedEndpoint) endpointFactory.create((DOMElement) endpointNode);
            send.setEndpoint(namedEndpoint);
            try {
                if (namedEndpoint.getKey() == null && namedEndpoint.getKeyExpression() == null) {
                    DOMDocument document = element.getOwnerDocument();
                    int startOffset = document.offsetAt(namedEndpoint.getRange().getStartTagRange().getStart());
                    int endOffset = document.offsetAt(namedEndpoint.getRange().getEndTagRange().getEnd());
                    String endpointXml = document.getText().substring(startOffset, endOffset);
                    send.setInlineEndpointXml(endpointXml);
                }
            } catch (Exception ignored) {}
        }
        return send;
    }

    @Override
    public void populateAttributes(STNode node, DOMElement element) {

        Send send = (Send) node;
        String receive = element.getAttribute(Constant.RECEIVE);
        if (receive != null) {
            send.setReceive(receive);
        }
        String buildmessage = element.getAttribute(Constant.BUILDMESSAGE);
        if (buildmessage != null) {
            send.setBuildmessage(Boolean.parseBoolean(buildmessage));
        }
        String description = element.getAttribute(Constant.DESCRIPTION);
        if (description != null) {
            send.setDescription(description);
        }
    }

    @Override
    public String getTagName() {

        return SEND;
    }

}
