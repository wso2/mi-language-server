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

import com.github.mustachejava.Mustache;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.eclipse.lemminx.dom.DOMDocument;
import org.eclipse.lemminx.dom.DOMElement;
import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lsp4j.Position;

import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MediatorHandler {

    private static final Logger logger = Logger.getLogger(MediatorHandler.class.getName());

    public static JsonObject getSupportedMediators(DOMDocument document, Position position,
                                                   JsonObject mediatorList) {
        try {
            position = new Position(position.getLine() - 1, position.getCharacter() - 1);
            List<String> lastMediators = Arrays.asList("send","drop","loopback", "respond");
            List<String> iterateMediators = Arrays.asList("iterate","foreach");
            int offset = document.offsetAt(position);
            DOMNode currentNode = document.findNodeAt(offset);
            DOMNode nextMediator = currentNode.getNextSibling();
            DOMNode parentMediator = currentNode.getParentNode();
            if (lastMediators.contains(currentNode.getNodeName())) {
                return new JsonObject();
            } else {
                if (nextMediator != null ||
                        (parentMediator != null && iterateMediators.contains(parentMediator.getNodeName()))) {
                    return removeMediators(mediatorList, lastMediators);
                }
                return mediatorList;
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error occurred while retrieving supported mediators.", e);
        }
        return null;
    }

    public static String generateSynapseConfig(String mediator, Map<String, Object> data, Map<String,
                                               Mustache> templateMap, JsonObject mediatorList) {
        try {
            for (Map.Entry<String, JsonElement> entry : mediatorList.entrySet()) {
                JsonArray mediatorsArray = entry.getValue().getAsJsonArray();
                for (JsonElement mediatorElement : mediatorsArray) {
                    JsonObject mediatorObject = mediatorElement.getAsJsonObject();
                    if (mediator.equals(mediatorObject.get("tag").getAsString())) {
                        String processingClass = mediatorObject.get("processingClass").getAsString();
                        String processingMethod = mediatorObject.get("storeMethod").getAsString();
                        Class<?> mediatorProcessor = Class.forName(processingClass);
                        Object processorInstance = mediatorProcessor.getDeclaredConstructor().newInstance();
                        Method processorMethod = mediatorProcessor.getMethod(processingMethod, Map.class);
                        Object processedData = processorMethod.invoke(processorInstance, data);
                        StringWriter writer = new StringWriter();
                        return templateMap.get(mediator).execute(writer, processedData).toString();
                    }
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error occurred while generating Synapse configuration.", e);
        }
        return null;
    }

    public static JsonObject getSchemaWithValues(DOMDocument document, Position position,
                                                 Map<String, JsonObject> uiSchemaMap, JsonObject mediatorList) {
        try {
            position = new Position(position.getLine() - 1, position.getCharacter() - 1);
            int offset = document.offsetAt(position);
            DOMNode node = document.findNodeAt(offset);
            String mediatorName = node.getNodeName();
            JsonObject uiSchema = uiSchemaMap.get(mediatorName);
            for (Map.Entry<String, JsonElement> entry : mediatorList.entrySet()) {
                JsonArray mediatorsArray = entry.getValue().getAsJsonArray();
                for (JsonElement mediatorElement : mediatorsArray) {
                    JsonObject mediator = mediatorElement.getAsJsonObject();
                    if (mediatorName.equals(mediator.get("tag").getAsString())) {
                        String mediatorClass = mediator.get("mediatorClass").getAsString();
                        String factoryClass = mediator.get("factoryClass").getAsString();
                        String processingClass = mediator.get("processingClass").getAsString();
                        String processingMethod = mediator.get("retrieveMethod").getAsString();
                        Class<?> mediatorFactory = Class.forName(factoryClass);
                        Method buildMethod = mediatorFactory.getMethod("create", DOMElement.class);
                        Object factoryInstance = mediatorFactory.getDeclaredConstructor().newInstance();
                        Object mediatorObject = buildMethod.invoke(factoryInstance, node);
                        Class<?> mediatorProcessor = Class.forName(processingClass);
                        Object processorInstance = mediatorProcessor.getDeclaredConstructor().newInstance();
                        Method processorMethod = mediatorProcessor.getMethod(processingMethod, Class.forName(mediatorClass));
                        Object data = processorMethod.invoke(processorInstance, mediatorObject);
                        return UISchemaMapper.mapInputToUISchema(new Gson().toJsonTree(data).getAsJsonObject(), uiSchema);
                    }
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error occurred while updating UI schema with existing values.", e);
        }
        return null;
    }

    private static JsonObject removeMediators(JsonObject jsonObject, List<String> mediatorsToRemove) {
        JsonObject filteredMediators = new JsonObject();
        for (String key : jsonObject.keySet()) {
            JsonArray mediatorsArray = jsonObject.getAsJsonArray(key);
            JsonArray filteredArray = new JsonArray();
            for (JsonElement element : mediatorsArray) {
                JsonObject mediator = element.getAsJsonObject();
                String tag = mediator.get("tag").getAsString();
                if (!mediatorsToRemove.contains(tag)) {
                    filteredArray.add(mediator);
                }
            }
            filteredMediators.add(key, filteredArray);
        }
        return filteredMediators;
    }
}
