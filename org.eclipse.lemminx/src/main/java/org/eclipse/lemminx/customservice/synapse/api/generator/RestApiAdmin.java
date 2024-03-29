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

package org.eclipse.lemminx.customservice.synapse.api.generator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import org.apache.commons.io.FilenameUtils;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

// Source: https://github.com/wso2/integration-studio/blob/v8.2.0/components/esb-tools/plugins/org.wso2.integrationstudio.artifact.synapse.api/src/org/wso2/integrationstudio/artifact/synapse/api/ui/wizard/SynapseAPICreationWizard.java

public class RestApiAdmin {

    private static final Logger LOGGER = Logger.getLogger(RestApiAdmin.class.getName());
    private static final String CREATE_FROM_SWAGGER = "create.api.from.swagger";
    private static final String CREATE_FROM_WSDL = "create.api.from.wsdl";
    private static final String TITLE = "title";

    public String createAPI(String apiName, String swaggerPath, String mode) {

        if (CREATE_FROM_SWAGGER.equalsIgnoreCase(mode)) {
            try {
                return createAPIFromSwagger(apiName, swaggerPath);
            } catch (JsonProcessingException e) {
                LOGGER.log(Level.SEVERE, "Exception occurred while creating API from Swagger", e);
                return null;
            }
        } else if (CREATE_FROM_WSDL.equalsIgnoreCase(mode)) {
            // TODO: Implement wsdl to API creation
        }
        return null;
    }

    private String createAPIFromSwagger(String apiName, String swaggerPath) throws JsonProcessingException {

        File swaggerFile = new File(swaggerPath);
        String swaggerYaml = getSwaggerFileAsYAML(swaggerFile, apiName);
        String swaggerString = GenericApiObjectDefinition.convertYamlToJson(swaggerYaml);
        JsonParser jsonParser = new JsonParser();
        JsonElement swaggerJson = jsonParser.parse(swaggerString);
        APIGenerator apiGenerator = new APIGenerator(swaggerJson.getAsJsonObject());
        String apiXml = apiGenerator.generateSynapseAPIXml();
        return apiXml;
    }

    private String getSwaggerFileAsYAML(File swaggerFile, String apiName) {

        String swaggerContent = "";
        try {
            swaggerContent = new String(Files.readAllBytes(Paths.get(swaggerFile.getAbsolutePath())));
            if (FilenameUtils.getExtension(swaggerFile.getAbsolutePath()).equals("json")) {
                swaggerContent = convertJSONtoYaml(swaggerContent);
            }
            swaggerContent = updateNameInSwagger(apiName, swaggerContent);

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Exception occurred while reading swagger file", e);
        } catch (APIException e) {
            LOGGER.log(Level.SEVERE, "Exception occurred while updating swagger name", e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Exception occurred while converting swagger JSON to YAML", e);
        }
        return swaggerContent;
    }

    public String updateNameInSwagger(String newName, String swagger) throws APIException {

        JsonParser parser = new JsonParser();
        JsonElement jsonElement;
        try {
            jsonElement = parser.parse(swagger);
            Boolean openApi = jsonElement.getAsJsonObject().has("openapi");
            if (!openApi) {
                LOGGER.log(Level.SEVERE, "Provided swagger is not OpenApi 3.0");
            } else {
                JsonObject infoObject = jsonElement.getAsJsonObject().get("info").getAsJsonObject();
                infoObject.remove(TITLE);
                infoObject.add(TITLE, new JsonPrimitive(newName));
                return jsonElement.toString();
            }
        } catch (JsonSyntaxException ex) {
            // neglect the error - treat as YAML
            Yaml yaml = new Yaml();
            Map<String, Object> obj = yaml.load(swagger);
            Map<String, Object> infoMap = (Map<String, Object>) obj.get("info");
            infoMap.remove(TITLE);
            infoMap.put(TITLE, newName);

            DumperOptions options = new DumperOptions();
            options.setIndent(2);
            options.setPrettyFlow(true);
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            Yaml output = new Yaml(options);
            return output.dump(obj);
        }
        return null;
    }

    public static String convertJSONtoYaml(String jsonSource) throws Exception {

        try {
            Yaml yaml = new Yaml();
            Map<String, Object> obj = yaml.load(jsonSource);
            DumperOptions options = new DumperOptions();
            options.setIndent(2);
            options.setPrettyFlow(true);
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            Yaml output = new Yaml(options);
            return output.dump(obj);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Exception while converting json to yaml", e);
            throw new Exception(e);
        }
    }
}
