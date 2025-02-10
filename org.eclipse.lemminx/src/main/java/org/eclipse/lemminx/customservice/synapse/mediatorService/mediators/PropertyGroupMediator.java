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
import org.eclipse.lemminx.customservice.synapse.mediatorService.pojo.Namespace;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.Property;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.PropertyGroup;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PropertyGroupMediator {
    public static Either<Map<String, Object>, Map<Range, Map<String, Object>>> processData430(Map<String, Object> data,
                                                                                              PropertyGroup propertyGroup,
                                                                                              List<String> dirtyFields) {
        List<Object> propertiesList = data.get("properties") instanceof List<?> ?
                (List<Object>) data.get("properties") : new ArrayList<>();
        List<Map<String, Object>> properties = new ArrayList<>();

        for (Object propertyObj : propertiesList) {
            if (propertyObj instanceof List<?>) {
                List<Object> property = (List<Object>) propertyObj;

                String name = property.get(0) instanceof Map<?, ?> &&
                        Boolean.TRUE.equals(((Map<?, ?>) property.get(0)).get("isExpression"))
                        ? "{" + ((Map<?, ?>) property.get(0)).get("value") + "}"
                        : (String) ((Map<?, ?>) property.get(0)).get("value");

                List<Map<String, Object>> namespaces = new ArrayList<>();
                if (property.get(0) instanceof Map<?, ?> &&
                        ((Map<?, ?>) property.get(0)).get("namespaces") instanceof List<?>) {
                    namespaces.addAll((List<Map<String, Object>>) ((Map<?, ?>) property.get(0)).get("namespaces"));
                }

                Object value = null, expression = null, omValue = null, isInlineOMValue = null;
                if ("OM".equals(property.get(2))) {
                    if (property.get(4) instanceof Map<?, ?> &&
                            Boolean.TRUE.equals(((Map<?, ?>) property.get(4)).get("isExpression"))) {
                        expression = ((Map<?, ?>) property.get(4)).get("value");
                        if (((Map<?, ?>) property.get(4)).get("namespaces") instanceof List<?>) {
                            namespaces.addAll((List<Map<String, Object>>) ((Map<?, ?>) property.get(4)).get("namespaces"));
                        }
                    } else {
                        isInlineOMValue = true;
                        omValue = ((Map<?, ?>) property.get(4)).get("value");
                    }
                } else {
                    if (property.get(3) instanceof Map<?, ?> &&
                            Boolean.TRUE.equals(((Map<?, ?>) property.get(3)).get("isExpression"))) {
                        expression = ((Map<?, ?>) property.get(3)).get("value");
                        if (((Map<?, ?>) property.get(3)).get("namespaces") instanceof List<?>) {
                            namespaces.addAll((List<Map<String, Object>>) ((Map<?, ?>) property.get(3)).get("namespaces"));
                        }
                    } else {
                        value = ((Map<?, ?>) property.get(3)).get("value");
                    }
                }

                List<Map<String, Object>> filteredNamespaces = MediatorUtils.filterNamespaces(namespaces);

                boolean hasStringPattern = property.size() > 6 &&
                        property.get(6) != null && !((String) property.get(6)).isEmpty();

                Map<String, Object> propertyMap = new HashMap<>();
                propertyMap.put("newPropertyName", name);
                propertyMap.put("propertyAction", property.get(1));
                propertyMap.put("propertyDataType", property.get(2));
                propertyMap.put("value", value);
                propertyMap.put("expression", expression);
                propertyMap.put("namespaces", filteredNamespaces);
                propertyMap.put("OMValue", omValue);
                propertyMap.put("propertyScope", property.size() > 5 && property.get(5) != null ?
                        ((String) property.get(5)).toLowerCase() : null);
                propertyMap.put("valueStringPattern", hasStringPattern ? property.get(6) : null);
                propertyMap.put("valueStringCapturingGroup", hasStringPattern ? property.get(7) : null);
                propertyMap.put("description", property.size() > 8 ? property.get(8) : null);
                propertyMap.put("isInlineOMValue", isInlineOMValue);

                properties.add(propertyMap);
            }
        }

        data.put("properties", properties);
        return Either.forLeft(data);

    }

    public static Map<String, Object> getDataFromST430(PropertyGroup node) {

        Map<String, Object> data = new HashMap<>();
        data.put("description", node.getDescription());
        List<List<Object>> properties = new ArrayList<>();
        for (Property property : node.getProperty()) {
            boolean isNameExpression = property.getName() != null &&
                    property.getName().startsWith("{") && property.getName().endsWith("}");
            String name = isNameExpression
                    ? property.getName().substring(1, property.getName().length() - 1)
                    : property.getName();

            boolean isValueExpression = property.getValue() == null;

            List<Namespace> namespaces = MediatorUtils.transformNamespaces(property.getNamespaces());

            Object omValue = property.getAny();
            boolean isOmExpression = false;
            if ("OM".equals(property.getType()) && property.getExpression() != null) {
                isOmExpression = true;
                omValue = property.getExpression();
            }

            String expression = property.getExpression() != null ? property.getExpression() : "";
            String value = property.getValue() != null ? property.getValue() : "";
            properties.add(List.of(
                    Map.of(
                            "isExpression", isNameExpression,
                            "value", name,
                            "namespaces", namespaces
                    ),
                    property.getAction() != null ? property.getAction() : "",
                    property.getType() != null ? property.getType().toString() : "",
                    Map.of(
                            "isExpression", isValueExpression,
                            "value", isValueExpression ? expression : value,
                            "namespaces", namespaces
                    ),
                    Map.of(
                            "isExpression", isOmExpression,
                            "value", omValue != null ? omValue : "",
                            "namespaces", namespaces
                    ),
                    property.getScope() != null ? property.getScope().getValue().toUpperCase() : "",
                    property.getPattern() != null ? property.getPattern() : "",
                    property.getGroup() != null ? property.getGroup() : "",
                    property.getDescription() != null ? property.getDescription() : ""
            ));
        }
        data.put("properties", properties);
        return data;
    }
}
