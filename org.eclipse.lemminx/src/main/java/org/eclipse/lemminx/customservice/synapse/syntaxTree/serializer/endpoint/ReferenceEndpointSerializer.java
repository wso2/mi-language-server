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
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.SerializerUtils;

public class ReferenceEndpointSerializer extends EndpointSerializer {

    @Override
    protected OMElement serializeSpecificEndpoint(NamedEndpoint endpoint) {

        OMElement endpointElt = fac.createOMElement("endpoint", synNS);
        if (endpoint.getKey() != null) {
            endpointElt.addAttribute("key", endpoint.getKey(), nullNS);
        } else if (endpoint.getKeyExpression() != null) {
            SerializerUtils.serializeExpression(endpoint.getKeyExpression(), endpointElt, "key", endpoint);
        } else {
            handleException("Invalid endpoint. " +
                    "Should have a 'key' or 'key expression' ");
        }
        return endpointElt;
    }
}
