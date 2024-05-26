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

public class TemplateEndpointSerializer extends EndpointSerializer {

    @Override
    protected OMElement serializeSpecificEndpoint(NamedEndpoint endpoint) {

        OMElement templateEndpointElement = fac.createOMElement("endpoint", synNS);

        serializeAttributes(templateEndpointElement, endpoint);
        serializeChildren(templateEndpointElement, endpoint);
        return templateEndpointElement;
    }

    private void serializeAttributes(OMElement endpointElt, NamedEndpoint endpoint) {

        if (endpoint.getName() != null) {
            endpointElt.addAttribute("name", endpoint.getName(), nullNS);
        } else {
            handleException("Endpoint name is required.");
        }
        if (endpoint.getTemplate() != null) {
            endpointElt.addAttribute("template", endpoint.getTemplate(), nullNS);
        } else {
            handleException("Template endpoint template is required.");
        }
        if (endpoint.getUri() != null) {
            endpointElt.addAttribute("uri", endpoint.getUri(), nullNS);
        }

    }

    private void serializeChildren(OMElement endpointElt, NamedEndpoint endpoint) {

        serializeEndpointProperties(endpointElt, endpoint.getProperty());
        serializeEndpointParameters(endpointElt, endpoint.getParameter());
        if (endpoint.getDescription() != null) {
            OMElement descriptionElement = fac.createOMElement("description", synNS);
            descriptionElement.setText(endpoint.getDescription());
            endpointElt.addChild(descriptionElement);
        }
    }
}
