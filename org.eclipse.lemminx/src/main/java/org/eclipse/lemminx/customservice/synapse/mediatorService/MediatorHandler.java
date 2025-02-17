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
import com.google.gson.internal.LinkedTreeMap;
import org.eclipse.lemminx.commons.BadLocationException;
import org.eclipse.lemminx.customservice.synapse.connectors.ConnectorHolder;
import org.eclipse.lemminx.customservice.synapse.connectors.entity.ConnectorAction;
import org.eclipse.lemminx.customservice.synapse.connectors.entity.OperationParameter;
import org.eclipse.lemminx.customservice.synapse.mediatorService.pojo.SynapseConfigResponse;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.MediatorFactoryFinder;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.STNode;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.connector.Connector;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.InvalidMediator;
import org.eclipse.lemminx.customservice.synapse.utils.Constant;
import org.eclipse.lemminx.customservice.synapse.utils.UISchemaMapper;
import org.eclipse.lemminx.customservice.synapse.utils.Utils;
import org.eclipse.lemminx.dom.DOMDocument;
import org.eclipse.lemminx.dom.DOMElement;
import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.TextDocumentIdentifier;
import org.eclipse.lsp4j.TextEdit;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.eclipse.lemminx.customservice.synapse.mediatorService.MediatorUtils.generateResponseVariableDefaultValue;
import static org.eclipse.lemminx.customservice.synapse.utils.UISchemaMapper.mapInputToUISchema;

public class MediatorHandler {

    private static final Logger logger = Logger.getLogger(MediatorHandler.class.getName());
    private JsonObject mediatorList;
    private Map<String, JsonObject> uiSchemaMap;
    private Map<String, Mustache> templateMap;
    private ConnectorHolder connectorHolder;
    private boolean isInitialized;
    private Gson gson;

    public void init(String projectServerVersion, ConnectorHolder connectorHolder) {

        try {
            this.connectorHolder = connectorHolder;
            this.mediatorList = Utils.getMediatorList(projectServerVersion, connectorHolder);
            gson = new Gson();
        } catch (IOException e) {
            logger.log(Level.SEVERE,
                    String.format("Failed to load mediators for the MI server version: %s", projectServerVersion), e);
            logger.warning(String.format("Falling back to default mediators (MI %s).", Constant.DEFAULT_MI_VERSION));
            try {
                this.mediatorList = Utils.getMediatorList(Constant.DEFAULT_MI_VERSION, connectorHolder);
            } catch (IOException ex) {
                // This should not happen
            }
        }
        this.templateMap = Utils.getTemplateMap("org/eclipse/lemminx/mediators/"
                + projectServerVersion.replace(".", "") + "/templates");
        this.uiSchemaMap = Utils.getUISchemaMap("org/eclipse/lemminx/mediators/"
                + projectServerVersion.replace(".", "") + "/ui-schemas");
        this.isInitialized = true;
    }

    public JsonObject getSupportedMediators(TextDocumentIdentifier documentIdentifier, Position position) {

        try {
            DOMDocument document = Utils.getDOMDocument(new File(new URI(documentIdentifier.getUri())));
            List<String> lastMediators = Arrays.asList("send", "drop", "loopback", "respond");
            List<String> iterateMediators = Arrays.asList("iterate", "foreach");
            int offset = document.offsetAt(position);
            DOMNode currentNode = document.findNodeBefore(offset);
            DOMNode nextMediator = currentNode.getNextSibling();
            if (lastMediators.contains(currentNode.getNodeName())) {
                return new JsonObject();
            } else {
                if (isAddedAbove(currentNode, nextMediator, offset) || isIterateMediator(currentNode, iterateMediators)) {
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
            boolean isUpdate = !range.getEnd().equals(range.getStart());
            STNode node =
                    getMediatorNodeAtPosition(Utils.getDOMDocument(new File(documentUri)), range.getStart(), isUpdate);
            if (isConnector(node, mediator)) {
                return generateConnectorSynapseConfig(node, mediator, data, range);
            } else {
                return generateMediatorSynapseConfig(node, mediator, data, dirtyFields, range);
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error occurred while generating Synapse configuration.", e);
        }
        return null;
    }

    private boolean isConnector(STNode node, String mediator) {

        return node instanceof Connector || mediator.contains(".");
    }

    private SynapseConfigResponse generateConnectorSynapseConfig(STNode node, String mediator, Map<String, Object> data,
                                                                 Range range) {

        ConnectorAction operation = getConnectorOperation(node, mediator);
        if (operation != null) {
            List<OperationParameter> parameters = operation.getParameters();
            Map<String, Object> connectorData = new HashMap<>();
            connectorData.put(Constant.TAG, operation.getTag());
            connectorData.put(Constant.CONFIG_KEY, data.get(Constant.CONFIG_KEY));
            List<Object> parameterData = new ArrayList<>();
            for (OperationParameter parameter : parameters) {
                if (data.containsKey(parameter.getName())) {
                    Map<String, Object> dataValue = processConnectorParameter(data.get(parameter.getName()));
                    parameterData.add(Map.of(Constant.NAME, parameter.getName(), Constant.VALUE, dataValue));
                }
            }
            connectorData.put(Constant.PARAMETERS, parameterData);
            StringWriter writer = new StringWriter();
            String edit = templateMap.get(Constant.CONNECTOR).execute(writer, connectorData).toString();
            TextEdit textEdit = new TextEdit(range, edit);
            return new SynapseConfigResponse(textEdit);
        }
        return null;
    }

    private Map<String, Object> processConnectorParameter(Object data) {

        Map<String, Object> dataValue = new HashMap<>();
        if (data instanceof String) {
            dataValue.put(Constant.VALUE, String.format("%s", data));
        } else if (data instanceof Boolean) {
            dataValue.put(Constant.VALUE, data);
        } else if (data instanceof Map) {
            dataValue = (Map) data;
            Object isExpressionObj = dataValue.get(Constant.IS_EXPRESSION);
            boolean isExpression = isExpressionObj == null ? false : (boolean) isExpressionObj;
            if (isExpression) {
                dataValue.put(Constant.VALUE, String.format("{%s}", dataValue.get(Constant.VALUE)));
            } else {
                if (dataValue.get(Constant.VALUE).toString().startsWith("{") &&
                        dataValue.get(Constant.VALUE).toString().endsWith("}")) {
                    dataValue.put(Constant.VALUE, String.format("'%s'", dataValue.get(Constant.VALUE)));
                }
            }
        } else if (data instanceof List) {
            List<LinkedTreeMap> dataValueList = (List) data;
            StringBuilder dataValueStr = new StringBuilder("[");
            for (LinkedTreeMap dataValueItem : dataValueList) {
                if (dataValueItem.get(Constant.PROPERTY_NAME) != null && dataValueItem.get(Constant.PROPERTY_VALUE) != null) {
                    Object propertyValue;
                    if (dataValueItem.get(Constant.PROPERTY_VALUE) instanceof LinkedTreeMap) {
                        LinkedTreeMap dataValueLinkedTree =  (LinkedTreeMap) dataValueItem.get(Constant.PROPERTY_VALUE);
                        propertyValue = dataValueLinkedTree.get(Constant.VALUE);
                    } else {
                        propertyValue = dataValueItem.get(Constant.PROPERTY_VALUE);
                    }
                    dataValueStr.append(String.format("[\"%s\",\"%s\"],",
                            dataValueItem.get(Constant.PROPERTY_NAME), propertyValue));
                }
            }
            dataValueStr.append(']');
            dataValue.put(Constant.VALUE, String.format("%s", dataValueStr));
        }
        if (dataValue.get(Constant.VALUE) != null && dataValue.get(Constant.VALUE).toString().startsWith("<![CDATA[")) {
            dataValue.put("isCDATA", true);
            String value = dataValue.get(Constant.VALUE).toString().substring(9); // Remove <![CDATA[
            if (value.endsWith("]]>")) {
                value = value.substring(0, value.length() - 3); // Remove ]]>
            }
            dataValue.put(Constant.VALUE, value);
        }
        return dataValue;
    }

    private ConnectorAction getConnectorOperation(STNode node, String mediator) {

        String connectorName;
        String operation;
        if (node instanceof Connector) {
            connectorName = ((Connector) node).getConnectorName();
            operation = ((Connector) node).getMethod();
        } else {
            connectorName = mediator.split("\\.")[0];
            operation = mediator.split("\\.")[1];
        }
        if (!connectorHolder.exists(connectorName)) {
            return null;
        }
        org.eclipse.lemminx.customservice.synapse.connectors.entity.Connector connectorMetadata =
                connectorHolder.getConnector(connectorName);
        return connectorMetadata.getAction(operation);
    }

    private SynapseConfigResponse generateMediatorSynapseConfig(STNode node, String mediator, Map<String, Object> data,
                                                                List<String> dirtyFields,
                                                                Range range)
            throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException,
            IllegalAccessException {

        for (Map.Entry<String, JsonElement> entry : mediatorList.entrySet()) {
            JsonArray mediatorsArray = entry.getValue().getAsJsonObject().getAsJsonArray(Constant.ITEMS);
            for (JsonElement mediatorElement : mediatorsArray) {
                JsonObject mediatorObject = mediatorElement.getAsJsonObject();
                if (mediator.equals(mediatorObject.get(Constant.TAG).getAsString())) {
                    String mediatorClass = mediatorObject.get(Constant.MEDIATOR_CLASS).getAsString();
                    String processingClass = mediatorObject.get(Constant.PROCESSING_CLASS).getAsString();
                    String processingMethod = mediatorObject.get(Constant.STORE_METHOD).getAsString();
                    Class<?> mediatorProcessor = Class.forName(processingClass);
                    Object processorInstance = mediatorProcessor.getDeclaredConstructor().newInstance();
                    Method processorMethod =
                            mediatorProcessor.getMethod(processingMethod, Map.class, Class.forName(mediatorClass),
                                    List.class);
                    if (!Class.forName(mediatorClass).isInstance(node)) {
                        node = null;
                    }
                    Either<Map<String, Object>, Map<Range, Map<String, Object>>>
                            processedData =
                            (Either<Map<String, Object>, Map<Range, Map<String, Object>>>) processorMethod.invoke(
                                    processorInstance, data, node, dirtyFields);
                    if (processedData.isLeft()) {
                        StringWriter writer = new StringWriter();
                        String edit =
                                templateMap.get(mediator).execute(writer, processedData.getLeft()).toString().trim();
                        TextEdit textEdit = new TextEdit(range, edit);
                        return new SynapseConfigResponse(textEdit);
                    } else {
                        Map<Range, Map<String, Object>> editsData = processedData.getRight();
                        SynapseConfigResponse edits = new SynapseConfigResponse();
                        for (Map.Entry<Range, Map<String, Object>> entry1 : editsData.entrySet()) {
                            StringWriter writer = new StringWriter();
                            String edit =
                                    templateMap.get(mediator).execute(writer, entry1.getValue()).toString().trim();
                            TextEdit textEdit = new TextEdit(entry1.getKey(), edit);
                            edits.addTextEdit(textEdit);
                        }
                        return edits;
                    }
                }
            }
        }
        return null;
    }

    public JsonObject getSchemaWithValues(TextDocumentIdentifier documentIdentifier, Position position) {

        try {
            DOMDocument document = Utils.getDOMDocument(new File(new URI(documentIdentifier.getUri())));
            STNode node = getMediatorNodeAtPosition(document, position, Boolean.TRUE);
            if (node != null) {
                if (node instanceof Connector) {
                    return getSchemaWithValuesForConnector(node);
                }
                return getSchemaWithValuesForMediator(node);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error occurred while updating UI schema with existing values.", e);
        }
        return null;
    }

    private JsonObject getSchemaWithValuesForConnector(STNode node) {

        Connector connector = (Connector) node;
        JsonObject uiSchema = getConnectorUiSchema(connector.getTag(), null, null);
        return UISchemaMapper.mapInputToUISchemaForConnector(connector, uiSchema);
    }

    private JsonObject getSchemaWithValuesForMediator(STNode node)
            throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, ClassNotFoundException,
            InstantiationException {

        String mediatorName = node.getTag();
        for (Map.Entry<String, JsonElement> entry : mediatorList.entrySet()) {
            JsonArray mediatorsArray = entry.getValue().getAsJsonObject().getAsJsonArray(Constant.ITEMS);
            for (JsonElement mediatorElement : mediatorsArray) {
                JsonObject mediator = mediatorElement.getAsJsonObject();
                if (mediatorName.equals(mediator.get(Constant.TAG).getAsString())) {
                    String mediatorClass = mediator.get(Constant.MEDIATOR_CLASS).getAsString();
                    String processingClass = mediator.get(Constant.PROCESSING_CLASS).getAsString();
                    String processingMethod = mediator.get(Constant.RETRIEVE_METHOD).getAsString();
                    Class<?> mediatorProcessor = Class.forName(processingClass);
                    Object processorInstance = mediatorProcessor.getDeclaredConstructor().newInstance();
                    Method processorMethod =
                            mediatorProcessor.getMethod(processingMethod, Class.forName(mediatorClass));
                    Object data = processorMethod.invoke(processorInstance, node);
                    JsonObject dataJson = gson.toJsonTree(data).getAsJsonObject();
                    return UISchemaMapper.mapInputToUISchema(dataJson,
                            findUISchema(mediatorName, dataJson.get(Constant.UI_SCHEMA_NAME)));
                }
            }
        }
        return uiSchemaMap.get(mediatorName);
    }

    private JsonObject findUISchema(String mediatorName, JsonElement uiSchemaName) {

        if (uiSchemaName != null) {

            // Replace _ with : in the UI schema name to match the UI schema map key
            String uiSchemaNameValue = uiSchemaName.getAsString().replace("_", ":");
            return uiSchemaMap.get(uiSchemaNameValue).deepCopy();
        }
        return uiSchemaMap.get(mediatorName).deepCopy();
    }

    private STNode getMediatorNodeAtPosition(DOMDocument document, Position position, Boolean isUpdate)
            throws BadLocationException {

        position = new Position(position.getLine(), position.getCharacter() + (isUpdate ? 1 : 0));
        int offset = document.offsetAt(position);
        DOMNode node = document.findNodeAt(offset);
        if (node == null || (node instanceof DOMElement && ((DOMElement) node).getEndTagOpenOffset() == offset)) {
            return null;
        }

        STNode mediator = MediatorFactoryFinder.getInstance().getMediator(node);
        if (mediator != null && !(mediator instanceof InvalidMediator)) {
            return mediator;
        }

        return null;
    }

    private JsonObject removeMediators(JsonObject mediatorList, List<String> mediatorsToRemove) {

        JsonObject filteredMediators = new JsonObject();
        for (String key : mediatorList.keySet()) {
            JsonObject mediatorObject = mediatorList.getAsJsonObject(key);
            if (mediatorObject.has(Constant.IS_CONNECTOR) && mediatorObject.get(Constant.IS_CONNECTOR).getAsBoolean()) {
                filteredMediators.add(key, mediatorObject);
            } else {
                JsonArray mediatorsArray = mediatorObject.getAsJsonArray(Constant.ITEMS);
                JsonArray filteredArray = new JsonArray();
                for (JsonElement element : mediatorsArray) {
                    JsonObject mediator = element.getAsJsonObject();
                    String tag = mediator.get(Constant.TAG).getAsString();
                    if (!mediatorsToRemove.contains(tag)) {
                        filteredArray.add(mediator);
                    }
                }
                JsonObject items = new JsonObject();
                items.add(Constant.ITEMS, filteredArray);
                filteredMediators.add(key, items);
            }
        }
        return filteredMediators;
    }

    private boolean isIterateMediator(DOMNode currentNode, List<String> iterateMediators) {

        if (currentNode != null && iterateMediators != null) {
            DOMNode node = currentNode.getNodeName().equals("#text") ? currentNode.getParentNode() : currentNode;
            if (node != null) {
                DOMNode parentNode = node.getParentNode();
                if (parentNode != null && iterateMediators.contains(parentNode.getNodeName())) {
                    return true;
                } else {
                    DOMNode grandParentNode = parentNode != null ? parentNode.getParentNode() : null;
                    if (grandParentNode != null && iterateMediators.contains(grandParentNode.getNodeName())) {
                        return true;
                    } else {
                        DOMNode greatGrandParentNode = grandParentNode != null ? grandParentNode.getParentNode() : null;
                        return greatGrandParentNode != null && iterateMediators.contains(greatGrandParentNode.getNodeName());
                    }

                }
            }
        }
        return false;
    }

    private boolean isAddedAbove(DOMNode currentNode, DOMNode nextNode, int offset) {
        if (currentNode instanceof DOMElement) {
            DOMElement tagElement = (DOMElement) currentNode;
            if ((!tagElement.isSelfClosed()) && tagElement.isInInsideStartEndTag(offset)) {
                return currentNode.hasChildNodes();
            } else {
                return nextNode != null;
            }
        }
        return false;
    }

    public JsonObject getUiSchema(String mediatorName, TextDocumentIdentifier documentIdentifier, Position position) {

        if (uiSchemaMap.containsKey(mediatorName)) {
            return uiSchemaMap.get(mediatorName);
        }
        if (mediatorName.contains(".")) {
            return getConnectorUiSchema(mediatorName, documentIdentifier, position);
        }
        return null;
    }

    private JsonObject getConnectorUiSchema(String mediatorName, TextDocumentIdentifier documentIdentifier, Position position) {

        String connectorName = mediatorName.split("\\.")[0];
        String operationName = mediatorName.split("\\.")[1];
        if (connectorHolder.exists(connectorName)) {
            org.eclipse.lemminx.customservice.synapse.connectors.entity.Connector
                    connector = connectorHolder.getConnector(connectorName);
            ConnectorAction operation = connector.getAction(operationName);
            if (operation != null) {
                String uiSchemaPath = operation.getUiSchemaPath();
                try {
                    JsonObject uiSchemaObject = Utils.getJsonObject(Utils.readFile(new File(uiSchemaPath)));
                    JsonObject resultObject = new JsonObject();
                    if (documentIdentifier != null && position != null) {
                        resultObject.addProperty(Constant.RESPONSE_VARIABLE, generateResponseVariableDefaultValue(documentIdentifier, position, connectorName, operationName));
                    }
                    return mapInputToUISchema(resultObject, uiSchemaObject);
                } catch (IOException e) {
                    logger.log(Level.SEVERE, "Error occurred while retrieving UI schema for connector operation.", e);
                }
            }

        }
        return null;
    }

    public boolean isInitialized() {

        return isInitialized;
    }

    public void reloadMediatorList(String projectServerVersion) {

        try {
            this.mediatorList = Utils.getMediatorList(projectServerVersion, connectorHolder);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to reload mediators.", e);
        }
    }
}
