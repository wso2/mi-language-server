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

import com.google.gson.internal.LinkedTreeMap;
import org.eclipse.lemminx.customservice.synapse.mediatorService.pojo.ExpressionFieldValue;
import org.eclipse.lemminx.customservice.synapse.mediatorService.pojo.Namespace;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.Log;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LogMediator {

    public static Map<String, Object> processData(Map<String, Object> data) {
        String level = (String) data.get("level");
        if (level != null) {
            data.put("level", level.toLowerCase());
        }
        List<Map<String, Object>> properties = new ArrayList<>();
        Object propertyObj = data.get("properties");
        if (propertyObj instanceof List<?>) {
            for (Object propertyElement : (List<?>) propertyObj) {
                if (propertyElement instanceof LinkedTreeMap) {
                    LinkedTreeMap property = (LinkedTreeMap) propertyElement;
                    String propertyName = property.get("propertyName").toString();
                    ExpressionFieldValue value;
                    if (property.containsKey("expression")) {
                        if (property.get("namespaces") instanceof List<?> && !((List<?>) property.get("namespaces")).isEmpty()) {
                            List<Namespace> namespaces = new ArrayList<>();
                            for (Object namespace : (List<?>) property.get("namespaces")) {
                                if (namespace instanceof LinkedTreeMap) {
                                    LinkedTreeMap ns = (LinkedTreeMap) namespace;
                                    namespaces.add(new Namespace(ns.get("prefix").toString(), ns.get("uri").toString()));
                                }
                            }
                            value = new ExpressionFieldValue(property.get("expression").toString(), true, namespaces.toArray(new Namespace[0]));
                        } else {
                            value = new ExpressionFieldValue(property.get("expression").toString(), true, null);
                        }
                    } else {
                        value = new ExpressionFieldValue(property.get("value").toString(), false, null);
                    }
                    boolean isExpressionValue = value.isExpression();
                    Object namespaces = value.getNamespaces();
                    Map<String, Object> propertyData = new HashMap<>();
                    propertyData.put("propertyName", propertyName);
                    if (!isExpressionValue) {
                        propertyData.put("value", value.getValue());
                    } else {
                        propertyData.put("expression", value.getValue());
                    }
                    propertyData.put("namespaces", namespaces);
                    properties.add(propertyData);
                }
            }
        }
        if (properties.isEmpty()) {
            data.put("selfClosed", true);
        }
        data.put("properties", properties);
        return data;
    }

    public static Map<String, Object> getDataFromST(Log node) {
        Map<String, Object> data = new HashMap<>();
        if (node.getCategory() != null) {
            data.put("category", node.getCategory());
        }
        if (node.getLevel() != null) {
            data.put("level", node.getLevel().toUpperCase());
        }
        data.put("description", node.getDescription());
        data.put("separator", node.getSeparator());
        if (node.getProperty() != null) {
            List<Object> properties = Arrays.asList(node.getProperty()).stream().map(property -> {
                Map<String, Object> propertyData = new HashMap<>();
                propertyData.put("value", property.getValue() != null ? property.getValue() : property.getExpression());
                propertyData.put("isExpression", property.getExpression() != null);
                propertyData.put("namespaces", transformNamespaces(property.getNamespaces()));
                return new Object[] { property.getName(), propertyData };
            }).collect(Collectors.toList());
            data.put("properties", properties);
        }
        return data;
    }

    private static List<Namespace> transformNamespaces(Map<String, String> namespaces) {
        List<Namespace> transformedNamespaces = new ArrayList<>();
        if (namespaces != null && !namespaces.isEmpty()) {
            for (Map.Entry<String, String> entry : namespaces.entrySet()) {
                String key = entry.getKey();
                String uri = entry.getValue();
                String[] parts = key.split(":");
                String prefix = parts.length > 1 ? parts[1] : "";
                transformedNamespaces.add(new Namespace(prefix, uri));
            }
        }
        return transformedNamespaces;
    }
}
