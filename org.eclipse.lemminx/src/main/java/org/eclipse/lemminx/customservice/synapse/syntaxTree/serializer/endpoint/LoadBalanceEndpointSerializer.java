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

package org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.endpoint;

import org.apache.axiom.om.OMElement;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.endpoint.NamedEndpoint;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.endpoint.loadbalance.EndpointLoadbalance;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.endpoint.loadbalance.EndpointLoadbalanceEndpoint;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.endpoint.loadbalance.EndpointLoadbalanceMember;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.endpoint.loadbalance.EndpointOrMember;

public class LoadBalanceEndpointSerializer extends EndpointSerializer {

    @Override
    protected OMElement serializeSpecificEndpoint(NamedEndpoint endpoint) {

        EndpointLoadbalance loadBalanceEndpoint = endpoint.getLoadbalance();
        if (loadBalanceEndpoint == null) {
            handleException("Could not find the load balance endpoint");
        }

        OMElement loadBalanceElement = serializeLoadBalanceEndpoint(loadBalanceEndpoint);

        return loadBalanceElement;
    }

    protected OMElement serializeLoadBalanceEndpoint(EndpointLoadbalance loadBalanceEndpoint) {

        OMElement loadBalanceElement = fac.createOMElement("loadbalance", synNS);
        serializeAttributes(loadBalanceElement, loadBalanceEndpoint);
        serializeChildren(loadBalanceElement, loadBalanceEndpoint);
        return loadBalanceElement;
    }

    private void serializeAttributes(OMElement loadBalanceElement, EndpointLoadbalance loadBalanceEndpoint) {

        if (loadBalanceEndpoint.getAlgorithm() != null) {
            loadBalanceElement.addAttribute("algorithm", loadBalanceEndpoint.getAlgorithm(), nullNS);
        }
        if (!loadBalanceEndpoint.isFailover()) {
            loadBalanceElement.addAttribute("failover", "false", nullNS);
        }
        if (loadBalanceEndpoint.getPolicy() != null) {
            loadBalanceElement.addAttribute("policy", loadBalanceEndpoint.getPolicy(), nullNS);
        }
        if (loadBalanceEndpoint.isBuildMessage()) {
            loadBalanceElement.addAttribute("buildMessage", String.valueOf(loadBalanceEndpoint.isBuildMessage()),
                    nullNS);
        }
    }

    private void serializeChildren(OMElement loadBalanceElement, EndpointLoadbalance loadBalanceEndpoint) {

        if (loadBalanceEndpoint.getEndpointOrMember() != null) {
            for (EndpointOrMember endpointOrMember : loadBalanceEndpoint.getEndpointOrMember()) {
                if (endpointOrMember.isEndpoint()) {
                    serializeChildEndpoint(loadBalanceElement, endpointOrMember.getEndpoint().get());
                } else if (endpointOrMember.isMember()) {
                    serializeChildMember(loadBalanceElement, endpointOrMember.getMember().get());
                }
            }
        }
    }

    private void serializeChildEndpoint(OMElement loadBalanceElement, EndpointLoadbalanceEndpoint endpoint) {

        OMElement endpointElement = fac.createOMElement("endpoint", synNS);
        if (endpoint.getKey() != null) {
            endpointElement.addAttribute("key", endpoint.getKey(), nullNS);
        } else {
            if (endpoint.getName() != null) {
                endpointElement.addAttribute("name", endpoint.getName(), nullNS);
            } else {
                handleException("Endpoint name is required.");
            }
            if (endpoint.get_default() != null) {
                DefaultEndpointSerializer defaultEndpointSerializer = new DefaultEndpointSerializer();
                OMElement defaultElt = defaultEndpointSerializer.serializeDefaultEndpoint(endpoint.get_default());
                endpointElement.addChild(defaultElt);
            } else if (endpoint.getHttp() != null) {
                HTTPEndpointSerializer httpEndpointSerializer = new HTTPEndpointSerializer();
                OMElement httpElt = httpEndpointSerializer.serializeHttpEndpoint(endpoint.getHttp());
                endpointElement.addChild(httpElt);
            } else if (endpoint.getAddress() != null) {
                AddressEndpointSerializer addressEndpointSerializer = new AddressEndpointSerializer();
                OMElement addressElt = addressEndpointSerializer.serializeAddressEndpoint(endpoint.getAddress());
                endpointElement.addChild(addressElt);
            } else if (endpoint.getWsdl() != null) {
                WSDLEndpointSerializer wsdlEndpointSerializer = new WSDLEndpointSerializer();
                OMElement wsdlElt = wsdlEndpointSerializer.serializeWSDLEndpoint(endpoint.getWsdl());
                endpointElement.addChild(wsdlElt);
            } else if (endpoint.getLoadbalance() != null) {
                LoadBalanceEndpointSerializer loadbalanceEndpointSerializer = new LoadBalanceEndpointSerializer();
                OMElement loadbalanceElt =
                        loadbalanceEndpointSerializer.serializeLoadBalanceEndpoint(endpoint.getLoadbalance());
                endpointElement.addChild(loadbalanceElt);
            } else if (endpoint.getSession() != null) {
                OMElement sessionElt = serializeSession(endpoint.getSession());
                endpointElement.addChild(sessionElt);
            }
        }
        loadBalanceElement.addChild(endpointElement);
    }

    private void serializeChildMember(OMElement loadBalanceElement, EndpointLoadbalanceMember member) {

        OMElement memberElement = fac.createOMElement("member", synNS);
        if (member.getHostName() != null) {
            memberElement.addAttribute("hostName", member.getHostName(), nullNS);
        }
        if (member.getHttpPort() != null) {
            memberElement.addAttribute("httpPort", member.getHttpPort(), nullNS);
        }
        if (member.getHttpsPort() != null) {
            memberElement.addAttribute("httpsPort", member.getHttpsPort(), nullNS);
        }
        loadBalanceElement.addChild(memberElement);
    }
}
