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

package org.eclipse.lemminx.customservice.synapse.mediatorService;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.connector.Connector;
import org.eclipse.lemminx.customservice.synapse.utils.Constant;

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
        connector.getParameters().forEach(parameter -> data.addProperty(parameter.getName(),
                parameter.getIsExpression() ? parameter.getExpression() : parameter.getValue()));
        data.addProperty(Constant.CONFIG_REF, connector.getConfigKey());
        return mapInputToUISchema(data, uiSchema);
    }
}
