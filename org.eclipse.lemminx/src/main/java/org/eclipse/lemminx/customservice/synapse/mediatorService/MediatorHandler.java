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
import org.eclipse.lemminx.commons.BadLocationException;
import org.eclipse.lemminx.customservice.synapse.mediatorService.pojo.SynapseConfigResponse;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.MediatorFactoryFinder;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.STNode;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.InvalidMediator;
import org.eclipse.lemminx.customservice.synapse.utils.Utils;
import org.eclipse.lemminx.dom.DOMDocument;
import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.TextDocumentIdentifier;
import org.eclipse.lsp4j.TextEdit;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

import java.io.File;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MediatorHandler {

    private static final Logger logger = Logger.getLogger(MediatorHandler.class.getName());
    private JsonObject mediatorList;
    private Map<String, JsonObject> uiSchemaMap;
    private Map<String, Mustache> templateMap;

    public void init(String projectServerVersion) {

        this.mediatorList = Utils.getMediatorList(projectServerVersion);
        this.templateMap = Utils.getTemplateMap("org/eclipse/lemminx/mediators/"
                + projectServerVersion.replace(".", "") + "/templates");
        this.uiSchemaMap = Utils.getUISchemaMap("org/eclipse/lemminx/mediators/"
                + projectServerVersion.replace(".", "") + "/ui-schemas");
    }

    public JsonObject getSupportedMediators(TextDocumentIdentifier documentIdentifier, Position position) {
        try {
            DOMDocument document = Utils.getDOMDocument(new File(documentIdentifier.getUri().replaceFirst("file://", "")));
            List<String> lastMediators = Arrays.asList("send", "drop", "loopback", "respond");
            List<String> iterateMediators = Arrays.asList("iterate", "foreach");
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

    public SynapseConfigResponse generateSynapseConfig(String documentUri, Range range, String mediator,
                                                       Map<String, Object> data, List<String> dirtyFields) {
        try {
            boolean isUpdate = !(range.getEnd().getCharacter() - range.getStart().getCharacter() == 0);
            STNode node = getMediatorNodeAtPosition(Utils.getDOMDocument(new File(documentUri)), range.getStart(),isUpdate);
            for (Map.Entry<String, JsonElement> entry : mediatorList.entrySet()) {
                JsonArray mediatorsArray = entry.getValue().getAsJsonArray();
                for (JsonElement mediatorElement : mediatorsArray) {
                    JsonObject mediatorObject = mediatorElement.getAsJsonObject();
                    if (mediator.equals(mediatorObject.get("tag").getAsString())) {
                        String mediatorClass = mediatorObject.get("mediatorClass").getAsString();
                        String processingClass = mediatorObject.get("processingClass").getAsString();
                        String processingMethod = mediatorObject.get("storeMethod").getAsString();
                        Class<?> mediatorProcessor = Class.forName(processingClass);
                        Object processorInstance = mediatorProcessor.getDeclaredConstructor().newInstance();
                        Method processorMethod = mediatorProcessor.getMethod(processingMethod, Map.class, Class.forName(mediatorClass), List.class);

                        Either<Map<String, Object>, Map<Range, Map<String, Object>>>
                                processedData =
                                (Either<Map<String, Object>, Map<Range, Map<String, Object>>>) processorMethod.invoke(processorInstance, data, node, dirtyFields);
                        if (processedData.isLeft()) {
                            StringWriter writer = new StringWriter();
                            String edit = templateMap.get(mediator).execute(writer, processedData.getLeft()).toString();
                            TextEdit textEdit = new TextEdit(range, edit);
                            return new SynapseConfigResponse(textEdit);
                        } else {
                            Map<Range, Map<String, Object>> editsData = processedData.getRight();
                            SynapseConfigResponse edits = new SynapseConfigResponse();
                            for (Map.Entry<Range, Map<String, Object>> entry1 : editsData.entrySet()) {
                                StringWriter writer = new StringWriter();
                                String edit = templateMap.get(mediator).execute(writer, entry1.getValue()).toString();
                                TextEdit textEdit = new TextEdit(entry1.getKey(), edit);
                                edits.addTextEdit(textEdit);
                            }
                            return edits;
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error occurred while generating Synapse configuration.", e);
        }
        return null;
    }

    public JsonObject getSchemaWithValues(TextDocumentIdentifier documentIdentifier, Position position) {
        try {
            DOMDocument document = Utils.getDOMDocument(new File(documentIdentifier.getUri().replaceFirst("file://", "")));
            STNode node = getMediatorNodeAtPosition(document, position, Boolean.TRUE);
            String mediatorName = node.getTag();
            JsonObject uiSchema = uiSchemaMap.get(mediatorName).deepCopy();
            for (Map.Entry<String, JsonElement> entry : mediatorList.entrySet()) {
                JsonArray mediatorsArray = entry.getValue().getAsJsonArray();
                for (JsonElement mediatorElement : mediatorsArray) {
                    JsonObject mediator = mediatorElement.getAsJsonObject();
                    if (mediatorName.equals(mediator.get("tag").getAsString())) {
                        String mediatorClass = mediator.get("mediatorClass").getAsString();
                        String processingClass = mediator.get("processingClass").getAsString();
                        String processingMethod = mediator.get("retrieveMethod").getAsString();
                        Class<?> mediatorProcessor = Class.forName(processingClass);
                        Object processorInstance = mediatorProcessor.getDeclaredConstructor().newInstance();
                        Method processorMethod = mediatorProcessor.getMethod(processingMethod, Class.forName(mediatorClass));
                        Object data = processorMethod.invoke(processorInstance, node);

                        return UISchemaMapper.mapInputToUISchema(new Gson().toJsonTree(data).getAsJsonObject(), uiSchema);
                    }
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error occurred while updating UI schema with existing values.", e);
        }
        return null;
    }

    private STNode getMediatorNodeAtPosition(DOMDocument document, Position position, Boolean isUpdate) throws BadLocationException {

        position = new Position(position.getLine(), position.getCharacter() + (isUpdate ? 1 : 0));
        int offset = document.offsetAt(position);
        DOMNode node = document.findNodeAt(offset);
        if (node != null) {
            STNode mediator = MediatorFactoryFinder.getInstance().getMediator(node);
            if (mediator != null && !(mediator instanceof InvalidMediator)) {
                return mediator;
            }
        }
        return null;
    }

    private JsonObject removeMediators(JsonObject jsonObject, List<String> mediatorsToRemove) {
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

    public JsonObject getUiSchema(String mediatorName) {

        return uiSchemaMap.get(mediatorName);
    }
}
