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
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.Log;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.MediatorProperty;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogMediator {

    public static Either<Map<String, Object>, Map<Range, Map<String, Object>>> processData430(Map<String, Object> data,
                                                                                           Log log,
                                                                                           List<String> dirtyFields) {

        if (data.containsKey("level") && data.get("level") instanceof String) {
            data.put("level", ((String) data.get("level")).toLowerCase());
        }

        List<Object> propertiesList = data.get("properties") instanceof List<?> ? (List<Object>) data.get("properties") : new ArrayList<>();
        List<Map<String, Object>> processedProperties = new ArrayList<>();
        if (data.containsKey("properties")) {
            for (Object propertyObj : propertiesList) {
                if (propertyObj instanceof List<?>) {
                    List<Object> property = (List<Object>) propertyObj;

                    if (property.size() > 1 && property.get(1) instanceof Map<?, ?>) {
                        Map<String, Object> valueMap = (Map<String, Object>) property.get(1);
                        boolean isExpressionValue = Boolean.TRUE.equals(valueMap.get("isExpression"));
                        Map<String, Object> processedProperty = new HashMap<>();
                        processedProperty.put("propertyName", property.get(0));
                        if (!isExpressionValue) {
                            processedProperty.put("value", valueMap.get("value"));
                        } else {
                            processedProperty.put("expression", valueMap.get("value"));
                        }
                        processedProperty.put("namespaces", valueMap.get("namespaces"));
                        processedProperties.add(processedProperty);
                    }
                }
            }

            data.put("properties", processedProperties);
        }

        if (processedProperties.isEmpty() || propertiesList.isEmpty()) {
            data.put("selfClosed", true);
        }

        return Either.forLeft(data);
    }

    public static Map<String, Object> getDataFromST430(Log node) {

        Map<String, Object> data = new HashMap<>();

        if (node.getCategory() != null) {
            data.put("category", node.getCategory().toString());
        }
        if (node.getLevel() != null) {
            data.put("level", node.getLevel().toUpperCase());
        }
        data.put("description", node.getDescription());
        data.put("separator", node.getSeparator());
        if (node.getProperty() != null) {
            List<List<Object>> properties = new ArrayList<>();
            for (MediatorProperty property : node.getProperty()) {
                Map<String, Object> valueMap = new HashMap<>();
                valueMap.put("value", property.getValue() != null ? property.getValue() : property.getExpression());
                valueMap.put("isExpression", property.getExpression() != null);
                valueMap.put("namespaces", MediatorUtils.transformNamespaces(property.getNamespaces()));
                properties.add(List.of(property.getName() != null ? property.getName() : "", valueMap,
                        valueMap));
            }
            data.put("properties", properties);
        }
        return data;
    }

    public static Map<String, Object> getDataFromST440(Log node) {

        Map<String, Object> data = new HashMap<>();

        if (node.getCategory() != null) {
            data.put("category", node.getCategory().toString());
        }
        if (node.getLevel() != null) {
            data.put("level", node.getLevel().toUpperCase());
        }
        data.put("message", node.getMessage());
        data.put("description", node.getDescription());
        data.put("separator", node.getSeparator());
        if (node.getProperty() != null) {
            List<List<Object>> properties = new ArrayList<>();
            for (MediatorProperty property : node.getProperty()) {
                Map<String, Object> valueMap = new HashMap<>();
                valueMap.put("value", property.getValue() != null ? property.getValue() : property.getExpression());
                valueMap.put("isExpression", property.getExpression() != null);
                valueMap.put("namespaces", MediatorUtils.transformNamespaces(property.getNamespaces()));
                properties.add(List.of(property.getName() != null ? property.getName() : "",
                        valueMap));
            }
            data.put("properties", properties);
        }
        return data;
    }
}
