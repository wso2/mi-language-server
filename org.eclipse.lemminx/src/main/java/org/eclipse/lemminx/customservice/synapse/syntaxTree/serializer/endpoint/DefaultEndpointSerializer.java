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
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.endpoint.DefaultEndpoint;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.endpoint.NamedEndpoint;

public class DefaultEndpointSerializer extends EndpointSerializer {

    @Override
    protected OMElement serializeSpecificEndpoint(NamedEndpoint endpoint) {

        DefaultEndpoint defaultEndpoint = endpoint.get_default();
        if (defaultEndpoint == null) {
            handleException("Could not find the default endpoint.");
        }

        OMElement defaultElement = serializeDefaultEndpoint(defaultEndpoint);

        return defaultElement;
    }

    protected OMElement serializeDefaultEndpoint(DefaultEndpoint defaultEndpoint) {

        OMElement defaultElement = fac.createOMElement("default", synNS);
        serializeEndpointConfigurations(defaultElement, defaultEndpoint);
        return defaultElement;
    }

    protected void serializeEndpointConfigurations(OMElement defaultElement, DefaultEndpoint defaultEndpoint) {

        serializeQOSProperties(defaultElement, defaultEndpoint);
        serializeCommonEndpointProperties(defaultElement, defaultEndpoint);
        serializeAttributes(defaultElement, defaultEndpoint);

    }

    private void serializeQOSProperties(OMElement defaultElement, DefaultEndpoint defaultEndpoint) {

        if (defaultEndpoint.getEnableSec() != null) {
            OMElement enableSecElt = serializeEnableSec(defaultEndpoint.getEnableSec());
            defaultElement.addChild(enableSecElt);
        }
        if (defaultEndpoint.getEnableRM() != null) {
            OMElement enableRMElt = serializeEnableRM(defaultEndpoint.getEnableRM());
            defaultElement.addChild(enableRMElt);
        }
        if (defaultEndpoint.getEnableAddressing() != null) {
            OMElement enableAddressingElt = serializeEnableAddressing(defaultEndpoint.getEnableAddressing());
            defaultElement.addChild(enableAddressingElt);
        }
    }

    private void serializeCommonEndpointProperties(OMElement defaultElement, DefaultEndpoint defaultEndpoint) {

        if (defaultEndpoint.getTimeout() != null) {
            OMElement timeoutElt = serializeTimeout(defaultEndpoint.getTimeout());
            defaultElement.addChild(timeoutElt);
        }
        if (defaultEndpoint.getSuspendOnFailure() != null) {
            OMElement suspendOnFailureElt = serializeSuspendOnFailure(defaultEndpoint.getSuspendOnFailure());
            defaultElement.addChild(suspendOnFailureElt);
        }
        if (defaultEndpoint.getMarkForSuspension() != null) {
            OMElement markForSuspensionElt = serializeMarkForSuspension(defaultEndpoint.getMarkForSuspension());
            defaultElement.addChild(markForSuspensionElt);
        }
    }

    private void serializeAttributes(OMElement defaultElement, DefaultEndpoint defaultEndpoint) {

        if (defaultEndpoint.getEncoding() != null) {
            defaultElement.addAttribute("encoding", defaultEndpoint.getEncoding(), nullNS);
        }
        if (defaultEndpoint.getFormat() != null) {
            defaultElement.addAttribute("format", defaultEndpoint.getFormat().name(), nullNS);
        }
        if (defaultEndpoint.getOptimize() != null) {
            defaultElement.addAttribute("optimize", defaultEndpoint.getOptimize().name(), nullNS);
        }
        if (defaultEndpoint.getStatistics() != null) {
            defaultElement.addAttribute("statistics", defaultEndpoint.getStatistics(), nullNS);
        }
        if (defaultEndpoint.getTrace() != null) {
            defaultElement.addAttribute("trace", defaultEndpoint.getTrace(), nullNS);
        }
    }
}
