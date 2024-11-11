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
import org.eclipse.lemminx.customservice.synapse.mediatorService.MediatorUtils;
import org.eclipse.lemminx.customservice.synapse.mediatorService.pojo.ExpressionFieldValue;
import org.eclipse.lemminx.customservice.synapse.mediatorService.pojo.Namespace;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.Log;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LogMediator {

    public static Either<Map<String, Object>, Map<Range, Map<String, Object>>> processData(Map<String, Object> data,
                                                                                           Log log,
                                                                                           List<String> dirtyFields) {

        String level = (String) data.get("level");
        if (level != null) {
            data.put("level", level.toLowerCase());
        }
        List<Map<String, Object>> properties = new ArrayList<>();
        Object propertyObj = data.get("properties");
        if (propertyObj instanceof List<?>) {
            for (Object property : (List<?>) propertyObj) {
                if (property instanceof List) {
                    String propertyName = (String) ((List<?>) property).get(0);
                    Object propertyElementObj = ((List<?>) property).get(1);
                    if (propertyElementObj instanceof LinkedTreeMap) {
                        ExpressionFieldValue value;
                        LinkedTreeMap propertyElement = (LinkedTreeMap) propertyElementObj;
                        Boolean isExpression = (Boolean) propertyElement.get("isExpression");
                        if (isExpression) {
                            if (propertyElement.get("namespaces") instanceof List<?> &&
                                    !((List<?>) propertyElement.get("namespaces")).isEmpty()) {
                                List<Namespace> namespaces = new ArrayList<>();
                                for (Object namespace : (List<?>) propertyElement.get("namespaces")) {
                                    if (namespace instanceof LinkedTreeMap) {
                                        LinkedTreeMap ns = (LinkedTreeMap) namespace;
                                        namespaces.add(
                                                new Namespace(ns.get("prefix").toString(), ns.get("uri").toString()));
                                    }
                                }
                                value = new ExpressionFieldValue(propertyElement.get("expression").toString(), true,
                                        namespaces.toArray(new Namespace[0]));
                            } else {
                                value = new ExpressionFieldValue(propertyElement.get("value").toString(), true, null);
                            }
                        } else {
                            value = new ExpressionFieldValue(propertyElement.get("value").toString(), false, null);
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
        }
        if (properties.isEmpty()) {
            data.put("selfClosed", true);
        }
        data.put("properties", properties);
        return Either.forLeft(data);
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
                propertyData.put("namespaces", MediatorUtils.transformNamespaces(property.getNamespaces()));
                return new Object[]{property.getName(), propertyData};
            }).collect(Collectors.toList());
            data.put("properties", properties);
        }
        return data;
    }
}
