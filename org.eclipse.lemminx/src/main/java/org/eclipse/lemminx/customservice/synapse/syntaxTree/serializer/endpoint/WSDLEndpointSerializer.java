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
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.endpoint.wsdl.WSDLEndpoint;

public class WSDLEndpointSerializer extends EndpointSerializer {

    @Override
    protected OMElement serializeSpecificEndpoint(NamedEndpoint endpoint) {

        WSDLEndpoint wsdlEndpoint = endpoint.getWsdl();
        if (wsdlEndpoint == null) {
            handleException("Could not find the WSDL endpoint");
        }

        OMElement wsdlEndpointElement = serializeWSDLEndpoint(wsdlEndpoint);
        return wsdlEndpointElement;
    }

    protected OMElement serializeWSDLEndpoint(WSDLEndpoint wsdlEndpoint) {

        OMElement wsdlEndpointElement = fac.createOMElement("wsdl", synNS);

        serializeAttributes(wsdlEndpointElement, wsdlEndpoint);
        serializeQOSProperties(wsdlEndpointElement, wsdlEndpoint);
        serializeCommonEndpointProperties(wsdlEndpointElement, wsdlEndpoint);

        return wsdlEndpointElement;
    }

    private void serializeAttributes(OMElement wsdlEndpointElement, WSDLEndpoint wsdlEndpoint) {

        if (wsdlEndpoint.getUri() != null) {
            wsdlEndpointElement.addAttribute("uri", wsdlEndpoint.getUri(), nullNS);
        } else {
            handleException("WSDL endpoint URI is required.");
        }
        if (wsdlEndpoint.getService() != null) {
            wsdlEndpointElement.addAttribute("service", wsdlEndpoint.getService(), nullNS);
        } else {
            handleException("WSDL endpoint service is required.");
        }
        if (wsdlEndpoint.getPort() != null) {
            wsdlEndpointElement.addAttribute("port", wsdlEndpoint.getPort(), nullNS);
        } else {
            handleException("WSDL endpoint port is required.");
        }

        if (wsdlEndpoint.getFormat() != null) {
            wsdlEndpointElement.addAttribute("format", wsdlEndpoint.getFormat().name(), nullNS);
        }
        if (wsdlEndpoint.getOptimize() != null) {
            wsdlEndpointElement.addAttribute("optimize", wsdlEndpoint.getOptimize().name(), nullNS);
        }
        if (wsdlEndpoint.getEncoding() != null) {
            wsdlEndpointElement.addAttribute("encoding", wsdlEndpoint.getEncoding(), nullNS);
        }
        if (wsdlEndpoint.getStatistics() != null) {
            wsdlEndpointElement.addAttribute("statistics", wsdlEndpoint.getStatistics().name(), nullNS);
        }
        if (wsdlEndpoint.getTrace() != null) {
            wsdlEndpointElement.addAttribute("trace", wsdlEndpoint.getTrace().name(), nullNS);
        }
    }

    private void serializeQOSProperties(OMElement wsdlEndpointElement, WSDLEndpoint wsdlEndpoint) {

        if (wsdlEndpoint.getEnableSec() != null) {
            OMElement enableSec = serializeEnableSec(wsdlEndpoint.getEnableSec());
            wsdlEndpointElement.addChild(enableSec);
        }

        if (wsdlEndpoint.getEnableRM() != null) {
            OMElement enableRM = serializeEnableRM(wsdlEndpoint.getEnableRM());
            wsdlEndpointElement.addChild(enableRM);
        }

        if (wsdlEndpoint.getEnableAddressing() != null) {
            OMElement enableAddressing = serializeEnableAddressing(wsdlEndpoint.getEnableAddressing());
            wsdlEndpointElement.addChild(enableAddressing);
        }
    }

    private void serializeCommonEndpointProperties(OMElement wsdlEndpointElement, WSDLEndpoint wsdlEndpoint) {

        if (wsdlEndpoint.getTimeout() != null) {
            OMElement timeout = serializeTimeout(wsdlEndpoint.getTimeout());
            wsdlEndpointElement.addChild(timeout);
        }

        if (wsdlEndpoint.getSuspendOnFailure() != null) {
            OMElement suspendOnFailure = serializeSuspendOnFailure(wsdlEndpoint.getSuspendOnFailure());
            wsdlEndpointElement.addChild(suspendOnFailure);
        }

        if (wsdlEndpoint.getMarkForSuspension() != null) {
            OMElement markForSuspension = serializeMarkForSuspension(wsdlEndpoint.getMarkForSuspension());
            wsdlEndpointElement.addChild(markForSuspension);
        }
    }
}
