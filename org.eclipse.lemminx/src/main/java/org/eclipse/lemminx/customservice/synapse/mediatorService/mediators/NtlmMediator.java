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

import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.other.ntlm.Ntlm;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NtlmMediator {
    public static Either<Map<String, Object>, Map<Range, Map<String, Object>>> processData430(Map<String, Object> data,
                                                                                              Ntlm ntlm,
                                                                                              List<String> dirtyFields) {
        // Process username
        if (data.containsKey("username") && data.get("username") instanceof Map<?, ?>) {
            Map<String, Object> usernameMap = (Map<String, Object>) data.get("username");
            data.put("username", Boolean.TRUE.equals(usernameMap.get("isExpression"))
                    ? "{" + usernameMap.get("value") + "}"
                    : usernameMap.get("value"));
        }

        // Process password
        if (data.containsKey("password") && data.get("password") instanceof Map<?, ?>) {
            Map<String, Object> passwordMap = (Map<String, Object>) data.get("password");
            data.put("password", Boolean.TRUE.equals(passwordMap.get("isExpression"))
                    ? "{" + passwordMap.get("value") + "}"
                    : passwordMap.get("value"));
        }

        // Process host
        if (data.containsKey("host") && data.get("host") instanceof Map<?, ?>) {
            Map<String, Object> hostMap = (Map<String, Object>) data.get("host");
            data.put("host", Boolean.TRUE.equals(hostMap.get("isExpression"))
                    ? "{" + hostMap.get("value") + "}"
                    : hostMap.get("value"));
        }

        // Process domain
        if (data.containsKey("domain") && data.get("domain") instanceof Map<?, ?>) {
            Map<String, Object> domainMap = (Map<String, Object>) data.get("domain");
            data.put("domain", Boolean.TRUE.equals(domainMap.get("isExpression"))
                    ? "{" + domainMap.get("value") + "}"
                    : domainMap.get("value"));
        }

        // Process NTLM version
        if (data.containsKey("ntlmVersion") && data.get("ntlmVersion") instanceof Map<?, ?>) {
            Map<String, Object> ntlmVersionMap = (Map<String, Object>) data.get("ntlmVersion");
            data.put("ntlmVersion", Boolean.TRUE.equals(ntlmVersionMap.get("isExpression"))
                    ? "{" + ntlmVersionMap.get("value") + "}"
                    : ntlmVersionMap.get("value"));
        }
        return Either.forLeft(data);

    }

    public static Map<String, Object> getDataFromST430(Ntlm node) {

        Map<String, Object> data = new HashMap<>();
        data.put("description", node.getDescription());
        if (node.getUsername().contains("{")) {
            String username = node.getUsername().substring(1, node.getUsername().length() - 1);
            data.put("username", Map.of("isExpression", true, "value", username));
        } else {
            data.put("username", Map.of("isExpression", false,
                    "value", node.getUsername() != null ? node.getUsername() : ""
            ));
        }

        // Process password
        if (node.getPassword().contains("{")) {
            String password = node.getPassword().substring(1, node.getPassword().length() - 1);
            data.put("password", Map.of("isExpression", true, "value", password));
        } else {
            data.put("password", Map.of("isExpression", false,
                    "value", node.getPassword() != null ? node.getPassword() : ""
            ));
        }

        // Process host
        if (node.getHost().contains("{")) {
            String host = node.getHost().substring(1, node.getHost().length() - 1);
            data.put("host", Map.of("isExpression", true, "value", host));
        } else {
            data.put("host", Map.of("isExpression", false,
                    "value", node.getHost() != null ? node.getHost() : ""
            ));
        }

        // Process domain
        if (node.getDomain().contains("{")) {
            String domain = node.getDomain().substring(1, node.getDomain().length() - 1);
            data.put("domain", Map.of("isExpression", true, "value", domain));
        } else {
            data.put("domain", Map.of("isExpression", false,
                    "value", node.getDomain() != null ? node.getDomain() : ""
            ));
        }

        // Process NTLM version
        if (node.getNtlmVersion().contains("{")) {
            String ntlmVersion = node.getNtlmVersion().substring(1, node.getNtlmVersion().length() - 1);
            data.put("ntlmVersion", Map.of("isExpression", true, "value", ntlmVersion));
        } else {
            data.put("ntlmVersion", Map.of("isExpression", false,
                    "value", node.getNtlmVersion() != null ? node.getNtlmVersion() : ""
            ));
        }
        return data;
    }
}
