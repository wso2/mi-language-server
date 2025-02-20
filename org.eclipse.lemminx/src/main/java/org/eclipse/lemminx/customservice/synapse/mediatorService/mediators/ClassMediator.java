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
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.MediatorProperty;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.extension.Class;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassMediator {
    public static Either<Map<String, Object>, Map<Range, Map<String, Object>>> processData430(Map<String, Object> data,
                                                                                              Class aClass,
                                                                                              List<String> dirtyFields) {
        List<Map<String, Object>> properties = new ArrayList<>();
        List<Object> propertiesData = data.get("properties") instanceof List ?
                (List<Object>) data.get("properties") : new ArrayList<>();
        for (Object propertyObj : propertiesData) {
            if (propertyObj instanceof List<?>) {
                List<Object> property = (List<Object>) propertyObj;
                if (property.size() >= 2) {
                    Map<String, Object> propertyData = property.get(1) instanceof Map ?
                            (Map<String, Object>) property.get(1) : null;
                    if (propertyData != null) {
                        String propertyName = property.get(0) instanceof String ? (String) property.get(0) : "";
                        List<String> namespaces = propertyData.get("namespaces") instanceof List ?
                                (List<String>) propertyData.get("namespaces") : null;
                        Map<String, Object> propertyValue = new HashMap<>(Map.of(
                                "propertyName", propertyName,
                                "value", propertyData.get("value") != null ? propertyData.get("value") : "",
                                "isExpression", propertyData.get("isExpression") != null ?
                                        propertyData.get("isExpression") : false
                        ));
                        if (namespaces != null) {
                            propertyValue.put("namespaces", namespaces);
                        }
                        propertyValue.put("propertyName", propertyName);
                        properties.add(propertyValue);
                    }
                }
            }
        }

        data.put("properties", properties);
        return Either.forLeft(data);

    }

    public static Map<String, Object> getDataFromST430(Class node) {
        Map<String, Object> data = new HashMap<>();
        data.put("description", node.getDescription());
        data.put("className", node.getName());
        if (node.getProperty() != null) {
            List<List<Object>> properties = new ArrayList<>();

            for (MediatorProperty property : node.getProperty()) {
                boolean isExpression = property.getValue() == null;
                Map<String, Object> propertyDetails = new HashMap<>();
                propertyDetails.put("isExpression", isExpression);
                propertyDetails.put("value", isExpression ? property.getExpression() : property.getValue());

                if (isExpression && property.getNamespaces() != null) {
                    propertyDetails.put("namespaces", MediatorUtils.transformNamespaces(property.getNamespaces()));
                }

                properties.add(List.of(property.getName() != null ? property.getName() : "",
                        propertyDetails));
            }

            data.put("properties", properties);
        }
        return data;
    }

}
