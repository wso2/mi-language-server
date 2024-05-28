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
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.endpoint.recipientList.EndpointRecipientlist;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.endpoint.recipientList.EndpointRecipientlistEndpoint;

public class RecipientListEndpointSerializer extends EndpointSerializer {

    @Override
    protected OMElement serializeSpecificEndpoint(NamedEndpoint endpoint) {

        EndpointRecipientlist recipientListEndpoint = endpoint.getRecipientlist();
        if (recipientListEndpoint == null) {
            handleException("Could not find the RecipientList endpoint.");
        }

        OMElement recipientListEndpointElement = serializeRecipientListEndpoint(recipientListEndpoint);

        return recipientListEndpointElement;
    }

    private OMElement serializeRecipientListEndpoint(EndpointRecipientlist recipientListEndpoint) {

        OMElement recipientListElement = fac.createOMElement("recipientlist", synNS);
        serializeChildren(recipientListElement, recipientListEndpoint);
        return recipientListElement;
    }

    private void serializeChildren(OMElement recipientListElement, EndpointRecipientlist recipientListEndpoint) {

        if (recipientListEndpoint.getEndpoint() != null) {
            for (EndpointRecipientlistEndpoint endpoint : recipientListEndpoint.getEndpoint()) {
                OMElement childEp = serializeChildEndpoint(endpoint);
                recipientListElement.addChild(childEp);
            }
        }

    }

    private OMElement serializeChildEndpoint(EndpointRecipientlistEndpoint endpoint) {

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
        return endpointElement;
    }
}
