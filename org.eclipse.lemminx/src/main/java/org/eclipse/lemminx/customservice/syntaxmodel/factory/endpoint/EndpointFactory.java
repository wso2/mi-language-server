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

package org.eclipse.lemminx.customservice.syntaxmodel.factory.endpoint;

import org.eclipse.lemminx.customservice.syntaxmodel.factory.AbstractFactory;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.STNode;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.endpoint.DefaultEndpoint;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.endpoint.EndpointAddress;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.endpoint.EndpointSession;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.endpoint.EndpointType;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.endpoint.NamedEndpoint;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.endpoint.common.EndpointParameter;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.endpoint.common.EndpointProperty;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.endpoint.failover.EndpointFailover;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.endpoint.http.EndpointHttp;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.endpoint.loadbalance.EndpointLoadbalance;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.endpoint.recipientList.EndpointRecipientlist;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.endpoint.wsdl.WSDLEndpoint;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.mediator.core.MediatorProperty;
import org.eclipse.lemminx.customservice.syntaxmodel.utils.Constant;
import org.eclipse.lemminx.customservice.syntaxmodel.utils.Utils;
import org.eclipse.lemminx.dom.DOMElement;
import org.eclipse.lemminx.dom.DOMNode;

import java.util.ArrayList;
import java.util.List;

public class EndpointFactory extends AbstractFactory {

    @Override
    public STNode create(DOMElement element) {

        NamedEndpoint endpoint = new NamedEndpoint();
        endpoint.elementNode(element);
        endpoint.setType(EndpointType.NAMED_ENDPOINT);
        populateAttributes(endpoint, element);
        List<DOMNode> children = element.getChildren();
        List<EndpointProperty> properties = new ArrayList<>();
        List<EndpointParameter> parameters = new ArrayList<>();
        if (children != null && !children.isEmpty()) {
            for (DOMNode node : children) {
                String name = node.getNodeName();
                if (name.equalsIgnoreCase(Constant.ADDRESS)) {
                    AddressEndpointFactory factory = new AddressEndpointFactory();
                    EndpointAddress address = (EndpointAddress) factory.create((DOMElement) node);
                    endpoint.setAddress(address);
                    endpoint.setType(EndpointType.ADDRESS_ENDPOINT);
                } else if (name.equalsIgnoreCase(Constant.DEFAULT)) {
                    DefaultEndpointFactory factory = new DefaultEndpointFactory();
                    DefaultEndpoint defaultEndpoint = (DefaultEndpoint) factory.create((DOMElement) node);
                    endpoint.set_default(defaultEndpoint);
                    endpoint.setType(EndpointType.DEFAULT_ENDPOINT);
                } else if (name.equalsIgnoreCase(Constant.FAIL_OVER)) {
                    FailoverEndpointFactory factory = new FailoverEndpointFactory();
                    EndpointFailover failover = (EndpointFailover) factory.create((DOMElement) node);
                    endpoint.setFailover(failover);
                    endpoint.setType(EndpointType.FAIL_OVER_ENDPOINT);
                } else if (name.equalsIgnoreCase(Constant.HTTP)) {
                    HttpEndpointFactory factory = new HttpEndpointFactory();
                    EndpointHttp http = (EndpointHttp) factory.create((DOMElement) node);
                    endpoint.setHttp(http);
                    endpoint.setType(EndpointType.HTTP_ENDPOINT);
                } else if (name.equalsIgnoreCase(Constant.LOADBALANCE)) {
                    LoadbalanceEndpointFactory factory = new LoadbalanceEndpointFactory();
                    EndpointLoadbalance loadbalance = (EndpointLoadbalance) factory.create((DOMElement) node);
                    endpoint.setLoadbalance(loadbalance);
                    endpoint.setType(EndpointType.LOAD_BALANCE_ENDPOINT);
                } else if (name.equalsIgnoreCase(Constant.RECIPIENT_LIST)) {
                    RecipientlistEndpointFactory factory = new RecipientlistEndpointFactory();
                    EndpointRecipientlist recipientlist = (EndpointRecipientlist) factory.create((DOMElement) node);
                    endpoint.setRecipientlist(recipientlist);
                    endpoint.setType(EndpointType.RECIPIENT_LIST_ENDPOINT);
                } else if (name.equalsIgnoreCase(Constant.WSDL)) {
                    WSDLEndpointFactory factory = new WSDLEndpointFactory();
                    WSDLEndpoint wsdl = (WSDLEndpoint) factory.create((DOMElement) node);
                    endpoint.setWsdl(wsdl);
                    endpoint.setType(EndpointType.WSDL_ENDPOINT);
                } else if (name.equalsIgnoreCase(Constant.SESSION)) {
                    SessionFactory factory = new SessionFactory();
                    EndpointSession session = (EndpointSession) factory.create((DOMElement) node);
                    endpoint.setSession(session);
                } else if (name.equalsIgnoreCase(Constant.PROPERTY)) {
                    EndpointProperty property = createEndpointProperty(node);
                    properties.add(property);
                } else if (name.equalsIgnoreCase(Constant.PARAMETER)) {
                    EndpointParameter parameter = createEndpointParameter(node);
                    parameters.add(parameter);
                } else if (name.equalsIgnoreCase(Constant.DESCRIPTION)) {
                    String description = Utils.getInlineString(node.getFirstChild());
                    endpoint.setDescription(description);
                }
            }
            endpoint.setProperty(properties.toArray(new EndpointProperty[properties.size()]));
            endpoint.setParameter(parameters.toArray(new EndpointParameter[parameters.size()]));
        }
        return endpoint;
    }

    private EndpointProperty createEndpointProperty(DOMNode node) {

        MediatorProperty property = Utils.createMediatorProperty(node);
        EndpointProperty endpointProperty = new EndpointProperty(property);
        endpointProperty.elementNode((DOMElement) node);
        String scope = node.getAttribute(Constant.SCOPE);
        if (scope != null && !scope.isEmpty()) {
            endpointProperty.setScope(scope);
        }
        return endpointProperty;
    }

    private EndpointParameter createEndpointParameter(DOMNode node) {

        EndpointParameter parameter = new EndpointParameter();
        parameter.elementNode((DOMElement) node);
        String name = node.getAttribute(Constant.NAME);
        if (name != null && !name.isEmpty()) {
            parameter.setName(name);
        }
        String value = node.getAttribute(Constant.VALUE);
        if (value != null && !value.isEmpty()) {
            parameter.setValue(value);
        }
        return parameter;
    }

    @Override
    public void populateAttributes(STNode node, DOMElement element) {

        String name = element.getAttribute(Constant.NAME);
        if (name != null && !name.isEmpty()) {
            ((NamedEndpoint) node).setName(name);
        }
        String key = element.getAttribute(Constant.KEY);
        if (key != null && !key.isEmpty()) {
            ((NamedEndpoint) node).setKey(key);
        }
        String template = element.getAttribute(Constant.TEMPLATE);
        if (template != null && !template.isEmpty()) {
            ((NamedEndpoint) node).setTemplate(template);
        }
        String uri = element.getAttribute(Constant.URI);
        if (uri != null && !uri.isEmpty()) {
            ((NamedEndpoint) node).setUri(uri);
        }
    }
}
