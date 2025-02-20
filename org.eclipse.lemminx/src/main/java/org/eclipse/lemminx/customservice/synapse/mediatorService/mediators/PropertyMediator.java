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
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.Property;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.PropertyMediatorType;
import org.eclipse.lemminx.utils.StringUtils;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PropertyMediator {

    public static Either<Map<String, Object>, Map<Range, Map<String, Object>>> processData430(Map<String, Object> data,
                                                                                              Property mediatorname,
                                                                                              List<String> dirtyFields) {

        List<Map<String, Object>> namespaces = new ArrayList<>();

        Map<String, Object> propertyName = data.get("propertyName") instanceof Map<?, ?> ?
                (Map<String, Object>) data.get("propertyName") : null;
        if (propertyName != null && Boolean.TRUE.equals(propertyName.get("isExpression"))) {
            namespaces.addAll((List<Map<String, Object>>) propertyName.getOrDefault("namespaces", new ArrayList<>()));
            data.put("propertyName", "{" + propertyName.get("value") + "}");
        } else if (propertyName != null) {
            data.put("propertyName", propertyName.get("value"));
        }

        if ("OM".equals(data.get("propertyDataType"))) {
            data.remove("value");
            Map<String, Object> omValue = data.get("OMValue") instanceof Map<?, ?> ?
                    (Map<String, Object>) data.get("OMValue") : null;
            if (omValue != null && Boolean.TRUE.equals(omValue.get("isExpression"))) {
                namespaces.addAll((List<Map<String, Object>>) omValue.getOrDefault("namespaces", new ArrayList<>()));
                data.put("expression", omValue.get("value"));
            } else {
                data.remove("expression");
                data.put("isInlineOM", true);
                data.put("OMValue", omValue != null ? omValue.get("value") : null);
            }
        }

        if ("STRING".equals(data.get("propertyDataType"))) {
            if (StringUtils.isEmpty((String) data.get("valueStringPattern"))) {
                data.remove("valueStringCapturingGroup");
            }
        } else {
            data.remove("valueStringPattern");
            data.remove("valueStringCapturingGroup");
        }

        if ("set".equals(data.get("propertyAction"))) {
            data.remove("propertyAction");
        } else {
            data.remove("value");
            data.remove("propertyDataType");
            data.remove("valueStringPattern");
            data.remove("valueStringCapturingGroup");
        }

        Map<String, Object> value = data.get("value") instanceof Map<?, ?> ?
                (Map<String, Object>) data.get("value") : null;
        if (value != null && Boolean.TRUE.equals(value.get("isExpression"))) {
            namespaces.addAll((List<Map<String, Object>>) value.getOrDefault("namespaces", new ArrayList<>()));
            data.put("expression", value.get("value"));
            data.put("namespaces", value.get("namespaces"));
            data.remove("value");
        } else if (value != null) {
            data.put("value", value.get("value"));
        }

        data.put("namespaces", MediatorUtils.filterNamespaces(namespaces));
        data.put("propertyScope", ((String) data.get("propertyScope")).toLowerCase());

        return Either.forLeft(data);
    }

    public static Map<String, Object> getDataFromST430(Property node) {

        Map<String, Object> data = new HashMap<>();
        data.put("description", node.getDescription());

        Map<String, Object> propertyName = new HashMap<>();
        if (node.getName() != null && node.getName().startsWith("{") && node.getName().endsWith("}")) {
            propertyName.put("isExpression", true);
            propertyName.put("value", node.getName().substring(1, node.getName().length() - 1));
            propertyName.put("namespaces", MediatorUtils.transformNamespaces(node.getNamespaces()));
        } else {
            propertyName.put("isExpression", false);
            propertyName.put("value", node.getName());
        }
        data.put("propertyName", propertyName);

        if (node.getType() != null) {
            data.put("propertyDataType", node.getType());
        }

        if (PropertyMediatorType.OM.equals(node.getType())) {
            Map<String, Object> omValue = new HashMap<>();
            if (node.getExpression() != null) {
                omValue.put("isExpression", true);
                omValue.put("value", node.getExpression());
                omValue.put("namespaces", MediatorUtils.transformNamespaces(node.getNamespaces()));
            } else {
                omValue.put("isExpression", false);
                omValue.put("value", node.getAny());
            }
            data.put("OMValue", omValue);
        }

        if (node.getAction() != null) {
            data.put("propertyAction", node.getAction());
        }

        if (node.getScope() != null) {
            data.put("propertyScope", node.getScope().getValue().toUpperCase());
        }

        if (node.getPattern() != null) {
            data.put("valueStringPattern", node.getPattern());
        }

        if (node.getGroup() != null) {
            data.put("valueStringCapturingGroup", node.getGroup());
        }

        Map<String, Object> value = new HashMap<>();
        value.put("isExpression", false);
        value.put("value", "");
        if (node.getValue() != null) {
            value.put("isExpression", false);
            value.put("value", node.getValue());
        } else if (node.getExpression() != null) {
            value.put("isExpression", true);
            value.put("value", node.getExpression());
            value.put("namespaces", MediatorUtils.transformNamespaces(node.getNamespaces()));
        }
        data.put("value", value);

        return data;
    }

}
