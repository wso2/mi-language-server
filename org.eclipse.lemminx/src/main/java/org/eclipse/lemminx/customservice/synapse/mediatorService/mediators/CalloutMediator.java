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

package org.eclipse.lemminx.customservice.synapse.mediatorService.mediators;

import org.eclipse.lemminx.customservice.synapse.mediatorService.MediatorUtils;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.callout.*;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.eclipse.lemminx.customservice.synapse.mediatorService.MediatorUtils.transformNamespaces;

public class CalloutMediator {
    public static Either<Map<String, Object>, Map<Range, Map<String, Object>>> processData430(Map<String, Object> data,
                                                                                              Callout callout,
                                                                                              List<String> dirtyFields) {
        boolean xpathPayload = "XPATH".equals(data.get("payloadType"));
        boolean propertyPayload = "PROPERTY".equals(data.get("payloadType"));
        boolean envelopePayload = "ENVELOPE".equals(data.get("payloadType"));
        boolean xpathTarget = "XPATH".equals(data.get("resultType"));
        boolean propertyTarget = "PROPERTY".equals(data.get("resultType"));

        String targetProperty = data.get("resultContextProperty") != null ? (String) data.get("resultContextProperty") : "";
        boolean securityEnabled = "TRUE".equals(data.get("securityType"));
        boolean configurationEnabled = data.get("pathToAxis2Repository") != null || data.get("pathToAxis2Xml") != null;
        boolean policies = "TRUE".equals(data.get("policies"));

        if (xpathTarget) {
            data.put("targetMessageXPath", data.get("resultMessageXPath"));
        }

        data.putAll(Map.of(
                "xpathPayload", xpathPayload,
                "propertyPayload", propertyPayload,
                "envelopePayload", envelopePayload,
                "xpathTarget", xpathTarget,
                "propertyTarget", propertyTarget,
                "securityEnabled", securityEnabled,
                "configurationEnabled", configurationEnabled,
                "targetProperty", targetProperty,
                "policies", policies
        ));

        return Either.forLeft(data);
    }


    public static Map<String, Object> getDataFromST430(Callout node) {
        Map<String, Object> data = new HashMap<>();
        data.put("description", node.getDescription());
        if (node.getServiceURL() != null) {
            data.put("endpointType", "URL");
            data.put("serviceURL", node.getServiceURL());
        }
        if (node.getEndpointKey() != null) {
            data.put("endpointType", "AddressEndpoint");
            data.put("addressEndpoint", node.getEndpointKey());
        }
        if (node.getAction() != null) {
            data.put("soapAction", node.getAction());
        }

        SourceOrTargetOrConfiguration sourceOrTargetOrConfiguration = node.getSourceOrTargetOrConfiguration();
        if (sourceOrTargetOrConfiguration != null) {

            if (sourceOrTargetOrConfiguration.getConfiguration().isPresent()) {
                CalloutConfiguration calloutConfiguration = sourceOrTargetOrConfiguration.getConfiguration().get();
                if (calloutConfiguration.getRepository() != null) {
                    data.put("pathToAxis2Repository", calloutConfiguration.getRepository());
                }
                if (calloutConfiguration.getAxis2Xml() != null) {
                    data.put("pathToAxis2xml", calloutConfiguration.getAxis2Xml());
                }
            }

            if (sourceOrTargetOrConfiguration.getSource().isPresent()) {
                CalloutSource source = sourceOrTargetOrConfiguration.getSource().get();
                if (source.getKey() != null) {
                    data.put("payloadType", "PROPERTY");
                    data.put("payloadProperty", source.getKey());
                } else if (source.getXpath() != null) {
                    data.put("payloadType", "XPATH");
                    data.put("payloadMessageXPath", Map.of(
                            "isExpression", true,
                            "value", source.getXpath(),
                            "namespaces", MediatorUtils.transformNamespaces(source.getNamespaces())
                    ));
                } else {
                    data.put("payloadType", "ENVELOPE");
                }
            }

            if (sourceOrTargetOrConfiguration.getTarget().isPresent()) {
                CalloutTarget target = sourceOrTargetOrConfiguration.getTarget().get();
                if (target.getKey() != null) {
                    data.put("resultType", "PROPERTY");
                    data.put("resultContextProperty", target.getKey());
                } else if (target.getXpath() != null) {
                    data.put("resultType", "XPATH");
                    data.put("resultMessageXPath", Map.of(
                            "isExpression", true,
                            "value", target.getXpath(),
                            "namespaces", transformNamespaces(target.getNamespaces())
                    ));
                }
            }

            data.put("securityType", "FALSE");
            if (sourceOrTargetOrConfiguration.getEnableSec().isPresent()) {
                CalloutEnableSec enableSec = sourceOrTargetOrConfiguration.getEnableSec().get();
                data.put("securityType", "TRUE");
                if (enableSec.getPolicy() != null) {
                    data.put("policies", "FALSE");
                    data.put("policyKey", enableSec.getPolicy());
                } else {
                    data.put("policies", "TRUE");
                    if (enableSec.getInboundPolicy() != null) {
                        data.put("inboundPolicyKey", enableSec.getInboundPolicy());
                    }
                    if (enableSec.getOutboundPolicy() != null) {
                        data.put("outboundPolicyKey", enableSec.getOutboundPolicy());
                    }
                }
            }
        }

        return data;
    }
}
