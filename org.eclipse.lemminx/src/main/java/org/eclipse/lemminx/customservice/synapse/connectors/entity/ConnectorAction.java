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

package org.eclipse.lemminx.customservice.synapse.connectors.entity;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.lemminx.customservice.synapse.mediator.tryout.pojo.Property;
import org.eclipse.lemminx.customservice.synapse.utils.Constant;
import org.eclipse.lemminx.customservice.synapse.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ConnectorAction {

    private String name;
    private String tag;
    private List<String> parameters;
    private List<String> allowedConnectionTypes;
    private String description;
    private Boolean isHidden;
    private String uiSchemaPath;
    private String outputSchemaPath;
    private Property outputSchema;

    public ConnectorAction() {

        parameters = new ArrayList<>();
        allowedConnectionTypes = new ArrayList<>();
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public String getDescription() {

        return description;
    }

    public void setDescription(String description) {

        this.description = description;
    }

    public String getTag() {

        return tag;
    }

    public void setTag(String tag) {

        this.tag = tag;
    }

    public void addParameter(String parameter) {

        parameters.add(parameter);
    }

    public List<String> getParameters() {

        return Collections.unmodifiableList(parameters);
    }

    public void setParameters(List<String> parameters) {

        this.parameters = parameters;
    }

    public Boolean getHidden() {

        return isHidden;
    }

    public void setHidden(Boolean hidden) {

        isHidden = hidden;
    }

    public List<String> getAllowedConnectionTypes() {

        return Collections.unmodifiableList(allowedConnectionTypes);
    }

    public void setAllowedConnectionTypes(List<String> allowedConnectionTypes) {

        this.allowedConnectionTypes = allowedConnectionTypes;
    }

    public String getUiSchemaPath() {

        return uiSchemaPath;
    }

    public void setUiSchemaPath(String uiSchemaPath) {

        this.uiSchemaPath = uiSchemaPath;
    }

    public void setOutputSchemaPath(String outputSchemaPath) {

        this.outputSchemaPath = outputSchemaPath;
    }

    private void loadOutputSchema() throws IOException {

        if (outputSchemaPath == null) {
            return;
        }
        String outputSchemaString = Utils.readFile(new File(outputSchemaPath));
        JsonObject outputSchemaJson = Utils.getJsonObject(outputSchemaString);
        if (outputSchemaJson != null) {
            outputSchema = createSchemaObject(outputSchemaJson);
        }
    }

    private Property createSchemaObject(JsonObject outputSchemaJson) {

        JsonObject properties = outputSchemaJson.getAsJsonObject(Constant.PROPERTIES);
        if (properties == null) {
            return null;
        }
        Property outputSchemaObject = new Property("root", StringUtils.EMPTY);
        List<Property> propertiesList = extractProperties(properties);
        outputSchemaObject.setProperties(propertiesList);
        return outputSchemaObject;
    }

    private List<Property> extractProperties(JsonObject propertiesObject) {

        List<Property> propertiesList = new ArrayList<>();
        for (Map.Entry<String, JsonElement> entry : propertiesObject.entrySet()) {
            String key = entry.getKey();
            JsonElement value = entry.getValue();
            if (value.isJsonObject()) {
                JsonObject propertyObject = value.getAsJsonObject();
                String propDescription = propertyObject.get(Constant.DESCRIPTION).getAsString();
                Property property = new Property(key, StringUtils.EMPTY, propDescription);
                if (propertyObject.has(Constant.PROPERTIES)) {
                    List<Property> properties = extractProperties(propertyObject.getAsJsonObject(Constant.PROPERTIES));
                    property.setProperties(properties);
                }
                propertiesList.add(property);
            }
        }
        return propertiesList;
    }

    public Property getOutputSchema() {

        if (outputSchema == null) {
            try {
                loadOutputSchema();
            } catch (IOException e) {
                //Do nothing
            }
        }
        return outputSchema != null ? outputSchema.deepCopy() : null;
    }
}
