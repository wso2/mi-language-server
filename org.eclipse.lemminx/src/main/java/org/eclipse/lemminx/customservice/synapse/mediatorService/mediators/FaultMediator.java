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
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.transformation.fault.Makefault;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FaultMediator {
    public static Either<Map<String, Object>, Map<Range, Map<String, Object>>> processData430(Map<String, Object> data,
                                                                                              Makefault makefault,
                                                                                              List<String> dirtyFields) {
        if (data.containsKey("detail") && data.get("detail") instanceof Map<?, ?>) {
            Map<String, Object> detail = (Map<String, Object>) data.get("detail");
            if (Boolean.TRUE.equals(detail.get("isExpression"))) {
                data.put("detailExpression", detail);
            } else {
                data.put("detailValue", detail.get("value"));
            }
        }

        // Process reason
        if (data.containsKey("reason") && data.get("reason") instanceof Map<?, ?>) {
            Map<String, Object> reason = (Map<String, Object>) data.get("reason");
            if (Boolean.TRUE.equals(reason.get("isExpression"))) {
                data.put("reasonExpression", reason);
            } else {
                data.put("reasonValue", reason.get("value"));
            }
        }
        data.put("hasReason", data.containsKey("reasonValue") || data.containsKey("reasonExpression"));

        // Process SOAP version
        if (data.containsKey("soapVersion") && data.get("soapVersion") instanceof String) {
            String soapVersion = ((String) data.get("soapVersion")).toLowerCase();
            data.put("soapVersion", soapVersion);

            switch (soapVersion) {
                case "soap11":
                    data.put("soapUri", "http://schemas.xmlsoap.org/soap/envelope/");
                    data.put("code", data.get("soap11"));
                    data.remove("Role");
                    data.remove("node");
                    break;
                case "soap12":
                    data.put("soapUri", "http://www.w3.org/2003/05/soap-envelope");
                    data.put("code", data.get("soap12"));
                    data.remove("actor");
                    break;
                case "pox":
                    data.remove("actor");
                    data.remove("node");
                    data.remove("Role");
                    data.put("isPox", true);
                    break;
                default:
                    break;
            }
        }

        if (Boolean.TRUE.equals(data.get("serializeResponse")) && !Boolean.TRUE.equals(data.get("markAsResponse"))) {
            data.put("markAsResponse", false);
        } else if (Boolean.FALSE.equals(data.get("serializeResponse"))) {
            data.remove("markAsResponse");
        }
        return Either.forLeft(data);

    }

    public static Map<String, Object> getDataFromST430(Makefault node) {

        Map<String, Object> data = new HashMap<>();
        if (node.getDetail() != null && node.getDetail().getExpression() != null) {
            data.put("detail", Map.of(
                    "isExpression", true,
                    "value", node.getDetail().getExpression() != null ? node.getDetail().getExpression() : "",
                    "namespaces", MediatorUtils.transformNamespaces(node.getDetail().getNamespaces())
            ));
        } else {
            data.put("detail", Map.of(
                    "isExpression", false,
                    "value", node.getDetail() != null ? node.getDetail().getTextNode() : ""
            ));
        }

        // Process reason
        if (node.getReason() != null && node.getReason().getExpression() != null) {
            data.put("reason", Map.of(
                    "isExpression", true,
                    "value", node.getReason().getExpression() != null ? node.getReason().getExpression() : "",
                    "namespaces", MediatorUtils.transformNamespaces(node.getReason().getNamespaces())
            ));
        } else {
            data.put("reason", Map.of(
                    "isExpression", false,
                    "value", node.getReason() != null ? node.getReason().getValue() : ""
            ));
        }

        // Process soapVersion
        data.put("soapVersion", node.getVersion().toString());

        // Process description
        data.put("description", node.getDescription());

        // Process node, Role, and actor
        data.put("node", node.getNode() != null ? node.getNode().getTextNode() : null);
        data.put("Role", node.getRole() != null ? node.getRole().getTextNode() : null);
        data.put("actor", node.getRole() != null ? node.getRole().getTextNode() : null);

        // Process code
        if (node.getCode() != null && node.getCode().getValue() != null) {
            String[] codeValue = node.getCode().getValue().split("Env:");
            if ("soap11".equals(data.get("soapVersion"))) {
                data.put("soap11", codeValue.length > 1 ? codeValue[1] : null);
            } else {
                data.put("soap12", codeValue.length > 1 ? codeValue[1] : null);
            }
        }

        // Process serializeResponse
        data.put("serializeResponse", false);
        if (node.isResponse() != null) {
            data.put("serializeResponse", true);
            data.put("markAsResponse", node.isResponse());
        }
        return data;
    }
}
