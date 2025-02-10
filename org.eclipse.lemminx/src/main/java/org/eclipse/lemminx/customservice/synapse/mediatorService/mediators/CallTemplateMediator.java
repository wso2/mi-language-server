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
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.CallTemplate;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.WithParam;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CallTemplateMediator {
    public static Either<Map<String, Object>, Map<Range, Map<String, Object>>> processData430(Map<String, Object> data,
                                                                                              CallTemplate callTemplate,
                                                                                              List<String> dirtyFields) {
        List<Object> parameterNameTable = data.get("parameterNameTable") instanceof List<?> ?
                (List<Object>) data.get("parameterNameTable") : new ArrayList<>();
        List<Map<String, Object>> parameterName = new ArrayList<>();
        for (Object propertyObj : parameterNameTable) {
            if (propertyObj instanceof List<?>) {
                List<Object> property = (List<Object>) propertyObj;
                Map<String, Object> value = property.get(1) instanceof Map<?, ?> ?
                        (Map<String, Object>) property.get(1) : null;
                if (value != null) {
                    boolean isExpressionValue = (boolean) value.get("isExpression");
                    List<Object> namespaces = isExpressionValue && value.get("namespaces") instanceof List<?> ?
                            (List<Object>) value.get("namespaces") : null;
                    Map<String, Object> propertyValue = new HashMap<>();
                    propertyValue.put("parameterName", property.get(0));
                    propertyValue.put("parameterValue", isExpressionValue ? "{" +
                            value.get("value") + "}" : value.get("value"));
                    if (namespaces != null) {
                        propertyValue.put("namespaces", namespaces);
                    }
                    parameterName.add(propertyValue);
                }
            }
        }

        data.put("parameterName", parameterName);

        return Either.forLeft(data);
    }


    public static Map<String, Object> getDataFromST430(CallTemplate node) {
        Map<String, Object> data = new HashMap<>();
        data.put("targetTemplate", node.getTarget());
        data.put("description", node.getDescription());
        data.put("onError", node.getOnError());
        if (node.getWithParam() != null) {
            List<List<Object>> parameterNameTable = new ArrayList<>();

            for (WithParam property : node.getWithParam()) {
                String propertyValue = property.getValue() != null ? property.getValue() : "";
                boolean isExpression = propertyValue.startsWith("{");
                String value = isExpression
                        ? propertyValue.replaceAll("\\{([^}]*)\\}", "$1")
                        : propertyValue;

                parameterNameTable.add(List.of(
                        property.getName() != null ? property.getName() : "",
                        Map.of(
                                "value", value,
                                "isExpression", isExpression,
                                "namespaces", MediatorUtils.transformNamespaces(property.getNamespaces())
                        )
                ));
            }

            data.put("parameterNameTable", parameterNameTable);
        }
        return data;
    }
}
