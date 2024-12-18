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

package org.eclipse.lemminx.customservice.synapse.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.eclipse.lemminx.customservice.synapse.mediatorService.MediatorUtils;
import org.eclipse.lemminx.customservice.synapse.mediatorService.pojo.Namespace;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.connector.Connector;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.connector.ConnectorParameter;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.inbound.InboundEndpoint;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.inbound.InboundEndpointParameters;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.misc.common.Parameter;

import java.util.List;

public class UISchemaMapper {

    public static JsonObject mapInputToUISchema(JsonObject data, JsonObject uiSchema) {
        JsonArray elements = uiSchema.getAsJsonArray("elements");
        processElements(data, elements);
        return uiSchema;
    }

    private static void processElements(JsonObject data, JsonArray elements) {
        for (JsonElement element : elements) {
            JsonObject elementObj = element.getAsJsonObject();
            String elementType = elementObj.get("type").getAsString();
            if (elementType.equals("attributeGroup")) {
                JsonObject groupValue = elementObj.getAsJsonObject("value");
                JsonArray groupElements = groupValue.getAsJsonArray("elements");
                processElements(data, groupElements);
            } else {
                processElement(data, elementObj);
            }
        }
    }

    private static void processElement(JsonObject elementData, JsonObject elementObj) {
        JsonObject value = elementObj.getAsJsonObject("value");
        if (elementObj.get("type").getAsString().equals("table")) {
            String tableName = value.get("name").getAsString();
            if (elementData.has(tableName)) {
                JsonArray tableData = elementData.getAsJsonArray(tableName);
                value.add("currentValue", tableData);
            }
        } else {
            if (value.has("name")) {
                String attributeName = value.get("name").getAsString();
                if (elementData.has(attributeName)) {
                    JsonElement currentValue = elementData.get(attributeName);
                    value.add("currentValue", currentValue);
                }
            }
        }
    }

    public static JsonObject mapInputToUISchemaForConnector(Connector connector, JsonObject uiSchema) {

        JsonObject data = new JsonObject();
        connector.getParameters().forEach(parameter -> {
            String name = parameter.getName();
            if (parameter.getIsExpression()) {
                JsonObject expression = getConnectorExpressionParam(parameter);
                data.add(name, expression);
            } else {
                data.addProperty(name, parameter.getValue());
            }
        });
        data.addProperty(Constant.CONFIG_REF, connector.getConfigKey());
        return mapInputToUISchema(data, uiSchema);
    }

    private static JsonObject getConnectorExpressionParam(ConnectorParameter parameter) {

        List<Namespace> namespaces = MediatorUtils.transformNamespaces(parameter.getNamespaces());
        JsonArray namespacesJson = new Gson().toJsonTree(namespaces).getAsJsonArray();
        JsonObject expression = new JsonObject();
        expression.addProperty(Constant.VALUE, extractConnectorExpressionValue(parameter.getValue()));
        expression.add(Constant.NAMESPACES, namespacesJson);
        expression.addProperty(Constant.IS_EXPRESSION, true);
        return expression;
    }

    private static String extractConnectorExpressionValue(String value) {

        if (value == null) {
            return null;
        }
        if (value.startsWith("${") && value.endsWith("}")) {
            return value.substring(2, value.length() - 1);
        } else if (value.startsWith("{") && value.endsWith("}")) {
            return value.substring(1, value.length() - 1);
        }
        return value;
    }

    public static JsonObject mapInputToUISchemaForInboundEndpoint(InboundEndpoint ib, JsonObject uiSchema) {

        JsonObject data = new JsonObject();
        data.addProperty(Constant.NAME, ib.getName());
        data.addProperty(Constant.SEQUENCE, ib.getSequence());
        data.addProperty(Constant.PROTOCOL, ib.getProtocol());
        data.addProperty(Constant.CLASS, ib.getClazz());
        data.addProperty(Constant.ON_ERROR, ib.getOnError());
        data.addProperty(Constant.SUSPEND, ib.isSuspend());
        data.addProperty(Constant.STATISTICS, ib.getStatistics() != null ? ib.getStatistics().toString() : null);
        data.addProperty(Constant.TRACE, ib.getTrace() != null ? ib.getTrace().toString() : null);
        InboundEndpointParameters[] parametersList = ib.getParameters();
        if (parametersList != null) {
            InboundEndpointParameters parameters = parametersList[0];
            if (parameters != null) {
                Parameter[] parametersArray = parameters.getParameter();
                if (parametersArray != null) {
                    for (int i = 0; i < parametersArray.length; i++) {
                        Parameter parameter = parametersArray[i];
                        data.addProperty(parameter.getName(), parameter.getContent());
                    }
                }
            }
        }
        return mapInputToUISchema(data, uiSchema);
    }
}
