/*
 * Copyright (c) 2025, WSO2 LLC. (http://www.wso2.com).
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
import com.google.gson.JsonPrimitive;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.lemminx.commons.BadLocationException;
import org.eclipse.lemminx.customservice.synapse.connectors.ConnectorHolder;
import org.eclipse.lemminx.customservice.synapse.connectors.entity.ConnectorAction;
import org.eclipse.lemminx.customservice.synapse.connectors.entity.OperationParameter;
import org.eclipse.lemminx.customservice.synapse.mediatorService.pojo.DocumentTextEdit;
import org.eclipse.lemminx.customservice.synapse.mediatorService.pojo.SynapseConfigResponse;
import org.eclipse.lemminx.customservice.synapse.resourceFinder.NewProjectResourceFinder;
import org.eclipse.lemminx.customservice.synapse.resourceFinder.pojo.Resource;
import org.eclipse.lemminx.customservice.synapse.resourceFinder.pojo.ResourceResponse;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.SyntaxTreeGenerator;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.STNode;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.connector.Connector;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.connector.ai.AIAgent;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.connector.ai.AgentTool;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.Mediator;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.template.Template;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.template.TemplateParameter;
import org.eclipse.lemminx.customservice.synapse.utils.ConfigFinder;
import org.eclipse.lemminx.customservice.synapse.utils.Constant;
import org.eclipse.lemminx.customservice.synapse.utils.Utils;
import org.eclipse.lemminx.dom.DOMDocument;
import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.TextEdit;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class AIConnectorHandler {

    private static final Logger LOGGER = Logger.getLogger(AIConnectorHandler.class.getName());
    private static final String AGENT_TOOL_TEMPLATE = "agentToolTemplate";
    private static final String TOOL_NAME = "toolName";
    private static final String TOOL_DESCRIPTION = "toolDescription";
    private static final String TOOL_RESULT_EXPRESSION = "toolResultExpression";
    private static final String RESULT_EXPRESSION = "resultExpression";
    private static final String SUPPORTS_AI_VALUES = "supportsAIValues";
    private static final String FUNCTION_PARAM_PREFIX = "${params.functionParams.";
    private static final String FROM_AI = "fromAI";
    private static final String AI_CONNECTOR_MUSTACHE_TEMPLATE_NAME = "AIConnector";
    private static final Path TEMPLATE_FOLDER_PATH = Path.of("src", "main", "wso2mi", "artifacts", "templates");
    Set<String> TOOL_EDIT_FIELDS = Set.of(TOOL_NAME, TOOL_DESCRIPTION, TOOL_RESULT_EXPRESSION);
    private final MediatorHandler mediatorHandler;
    private final String projectUri;
    private final Random randomGenerator = new Random();
    private static final List<String>
            CONNECTION_TYPES =
            List.of(Constant.LLM_CONFIG_KEY, Constant.MEMORY_CONFIG_KEY, Constant.EMBEDDING_CONFIG_KEY,
                    Constant.VECTOR_STORE_CONFIG_KEY);

    public AIConnectorHandler(MediatorHandler mediatorHandler, String projectUri) {

        this.mediatorHandler = mediatorHandler;
        this.projectUri = projectUri;
    }

    /**
     * Generates the AI agent operation configuration.
     *
     * @param node     syntax tree node for the current position if it is an edit operation, null otherwise
     * @param mediator mediator name
     * @param data     agent operation form data
     * @param range    range where the agent operation need to be added or updated
     * @return the generated configuration response
     */
    public SynapseConfigResponse generateAIConnectorConfig(STNode node, String mediator, Map<String, Object> data,
                                                           Range range) {

        ConnectorAction operation = mediatorHandler.getConnectorOperation(node, mediator);
        if (operation != null) {
            List<OperationParameter> parameters = operation.getParameters();
            Map<String, Object> connectorData = new HashMap<>();
            connectorData.put(Constant.TAG, operation.getTag());
            List<Map<String, String>> connections = addConnectionsDataForXML(data);
            connectorData.put(Constant.CONNECTIONS, connections);
            connectorData.put("supportTools", node instanceof AIAgent || Constant.AI_AGENT_TAG.equals(mediator));
            List<Object> parameterData = new ArrayList<>();
            for (OperationParameter parameter : parameters) {
                if (data.containsKey(parameter.getName())) {
                    Map<String, Object> dataValue =
                            mediatorHandler.processConnectorParameter(data.get(parameter.getName()));
                    if (dataValue != null && dataValue.containsKey(Constant.VALUE) &&
                            StringUtils.isNotEmpty(dataValue.get(Constant.VALUE).toString())) {
                        parameterData.add(Map.of(Constant.NAME, parameter.getName(), Constant.VALUE, dataValue));
                    }
                }
            }
            connectorData.put(Constant.PARAMETERS, parameterData);
            if (node instanceof AIAgent && ((AIAgent) node).getTools() != null) {
                List<Map<String, String>> tools = new ArrayList<>();
                for (AgentTool tool : ((AIAgent) node).getTools().getTools()) {
                    Map<String, String> toolData = new HashMap<>();
                    toolData.put(Constant.NAME, tool.getName());
                    toolData.put(Constant.TEMPLATE, tool.getTemplate());
                    toolData.put(Constant.DESCRIPTION, tool.getDescription());
                    tools.add(toolData);
                }
                connectorData.put(Constant.TOOLS, tools);
            }
            StringWriter writer = new StringWriter();
            String edit = mediatorHandler.getMustacheTemplate(AI_CONNECTOR_MUSTACHE_TEMPLATE_NAME)
                    .execute(writer, connectorData).toString();
            TextEdit textEdit = new TextEdit(range, edit);
            return new SynapseConfigResponse(textEdit);
        }
        return null;
    }

    private List<Map<String, String>> addConnectionsDataForXML(Map<String, Object> data) {

        List<Map<String, String>> connections = new ArrayList<>();
        for (String connectionType : CONNECTION_TYPES) {
            if (data.containsKey(connectionType)) {
                connections.add(
                        Map.of(Constant.NAME, connectionType, Constant.VALUE, data.get(connectionType).toString()));
            }
        }
        return connections;
    }

    /**
     * Generates the AI agent tool configuration.
     *
     * @param documentUri current document URI
     * @param range       range where the tool need to be added or updated
     * @param mediator    mediator name
     * @param data        the tool form data
     * @param dirtyFields list of fields that have been modified
     * @param isUpdate    true if it is an update operation, false otherwise
     * @return the generated configuration response
     * @throws IOException          if an error occurs while reading the current document/sequence template
     * @throws BadLocationException if the given range is invalid/ out of bounds
     */
    public SynapseConfigResponse generateAgentToolConfig(String documentUri, Range range, String mediator,
                                                         Map<String, Object> data, List<String> dirtyFields,
                                                         boolean isUpdate)
            throws IOException, BadLocationException {

        if (isUpdate) {
            return modifyAgentTool(documentUri, range, mediator, data, dirtyFields);
        } else {
            return addAIAgentTool(documentUri, range, mediator, data, dirtyFields);
        }
    }

    private SynapseConfigResponse addAIAgentTool(String documentUri, Range range, String mediator,
                                                 Map<String, Object> data, List<String> dirtyFields) {

        if (StringUtils.isEmpty(mediator)) {
            return null;
        }
        SynapseConfigResponse agentEditResponse = new SynapseConfigResponse();

        String sequenceTemplateName = getSequenceTemplateName(mediator);

        String sequenceTemplatePath =
                Path.of(projectUri).resolve(TEMPLATE_FOLDER_PATH).resolve(sequenceTemplateName + ".xml").toString();
        Map<String, String> templateParameters = new HashMap<>();

        processAIValues(data, templateParameters);

        // Remove overwrite body as we need the mediator response in the variable. TODO: Remove this field from tool UI
        data.remove(Constant.OVERWRITE_BODY);

        // Generate mediator/connector (tool) xml
        SynapseConfigResponse mediatorEdits =
                mediatorHandler.generateSynapseConfig(sequenceTemplatePath, range, mediator, data, dirtyFields);
        if (mediatorEdits == null || mediatorEdits.getTextEdits() == null || mediatorEdits.getTextEdits().isEmpty()) {
            LOGGER.log(Level.SEVERE, "Error while generating mediator edits for the tool, {0}", mediator);
            return null;
        }

        // Generate sequence template xml
        String templateXml = generateSequenceTemplate(mediatorEdits, templateParameters, sequenceTemplateName, data);
        DocumentTextEdit sequenceTemplateEdit = new DocumentTextEdit(range, templateXml, sequenceTemplatePath);
        sequenceTemplateEdit.setCreateNewFile(true);
        agentEditResponse.addTextEdit(sequenceTemplateEdit);

        String toolXml = generateToolXml(data, sequenceTemplateName);
        TextEdit toolsEditTextEdit = new DocumentTextEdit(range, toolXml, documentUri);
        agentEditResponse.addTextEdit(toolsEditTextEdit);

        return agentEditResponse;
    }

    /**
     * Processes the AI values in the data and updates the template parameters.
     * <p>
     * The field that expects value from AI will be replaced with an functional params, and corresponding template
     * parameter will be marked to be added to the sequence template.
     * </p>
     */
    private void processAIValues(Map<String, Object> data, Map<String, String> templateParameters) {

        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (isExpectingAIValue(entry.getValue())) {
                String parameterName = entry.getKey();
                String parameterDescription = extractParameterDescriptionFromFormValues(entry.getValue());
                templateParameters.put(parameterName, parameterDescription);

                Map<String, Object> expression = new HashMap<>();
                expression.put(Constant.VALUE, String.format("${params.functionParams.%s}", parameterName));
                expression.put(Constant.IS_EXPRESSION, true);
                data.put(parameterName, expression);
            }
        }
    }

    /**
     * Extracts the parameter description from the form values.
     *
     * @param data form value for the parameter
     * @return the parameter description
     */
    private String extractParameterDescriptionFromFormValues(Object data) {

        if (!(data instanceof Map)) {
            return StringUtils.EMPTY;
        }
        Map<?, ?> map = (Map<?, ?>) data;
        if (!map.containsKey(Constant.DESCRIPTION) || !(map.get(Constant.DESCRIPTION) instanceof Map<?, ?>)) {
            return StringUtils.EMPTY;
        }
        Object description = ((Map<?, ?>) map.get(Constant.DESCRIPTION)).get(Constant.CURRENT_VALUE);
        return description instanceof String ? description.toString() : StringUtils.EMPTY;
    }

    /**
     * Generates an XML representation for the specified tool.
     * <p>
     * The generated XML follows the format:
     * {@code <tool name="toolName" template="template" resultExpression="resultExpression"/>}
     * </p>
     */
    private String generateToolXml(Map<String, Object> data, String sequenceTemplateName) {

        Map<String, String> toolData = processToolData(data, sequenceTemplateName);
        StringWriter writer = new StringWriter();
        return mediatorHandler.getMustacheTemplate(Constant.TOOL).execute(writer, toolData).toString();
    }

    /**
     * Checks whether the field is expecting a value from AI.
     */
    private boolean isExpectingAIValue(Object value) {

        if (!(value instanceof Map)) {
            return false;
        }
        Map<?, ?> map = (Map<?, ?>) value;
        return map.containsKey(FROM_AI) && Boolean.TRUE.equals(map.get(FROM_AI));
    }

    /**
     * Processes the data for generating the {@code <tool/>} element.
     */
    private Map<String, String> processToolData(Map<String, Object> data, String sequenceTemplateName) {

        Map<String, String> toolData = new HashMap<>();
        toolData.put(Constant.NAME, data.get(TOOL_NAME).toString());
        toolData.put(Constant.TEMPLATE, sequenceTemplateName);
        if (data.containsKey(TOOL_RESULT_EXPRESSION) && data.get(TOOL_RESULT_EXPRESSION) instanceof Map<?, ?>) {
            Map<?, ?> expression = (Map<?, ?>) data.get(TOOL_RESULT_EXPRESSION);
            Object value = expression.get(Constant.VALUE);
            if (value instanceof String && StringUtils.isNotEmpty(value.toString())) {
                toolData.put(RESULT_EXPRESSION, value.toString());
            }
        } else if (data.containsKey(Constant.RESPONSE_VARIABLE)) {
            String resultExpression = String.format("${vars.%s}", data.get(Constant.RESPONSE_VARIABLE).toString());
            toolData.put(RESULT_EXPRESSION, resultExpression);
        }
        toolData.put(Constant.DESCRIPTION, data.get(TOOL_DESCRIPTION).toString());
        return toolData;
    }

    /**
     * Generates a unique sequence template name for the newly added tool.
     */
    private String getSequenceTemplateName(String mediator) {

        String sequenceTemplateName = String.format("%s_%s_", mediator.replace(".", "_"), "tool");
        NewProjectResourceFinder resourceFinder = new NewProjectResourceFinder();
        ResourceResponse response =
                resourceFinder.getAvailableResources(projectUri, Either.forLeft("sequenceTemplate"));
        if (response == null || response.getResources() == null) {
            return sequenceTemplateName + randomGenerator.nextInt(1000);
        }

        int i = 0;
        Set<String> existingNames =
                response.getResources().stream().map(Resource::getName).collect(Collectors.toSet());
        String newName;
        do {
            newName = sequenceTemplateName + i;
            i++;
        } while (existingNames.contains(newName));
        return newName;
    }

    /**
     * Generates the sequence template for the tool by adding the new mediator as a child in the sequence.
     */
    private String generateSequenceTemplate(SynapseConfigResponse mediatorEdits, Map<String, String> templateParameters,
                                            String sequenceTemplateName, Map<String, Object> data) {

        // There will be only one edit for a new mediator/connector. So, get the first edit from the list.
        TextEdit edit = mediatorEdits.getTextEdits().get(0);
        String mediatorXml = edit.getNewText();
        Map<String, Object> templateData = new HashMap<>();
        templateData.put(Constant.NAME, sequenceTemplateName);
        templateData.put(Constant.DESCRIPTION, data.get(TOOL_DESCRIPTION));
        List<Map<String, String>> parameters = new ArrayList<>();
        for (Map.Entry<String, String> entry : templateParameters.entrySet()) {
            Map<String, String> parameter = new HashMap<>();
            parameter.put(Constant.NAME, entry.getKey());
            parameter.put(Constant.IS_MANDATORY, "true");
            parameter.put(Constant.DESCRIPTION, entry.getValue());
            parameters.add(parameter);
        }
        templateData.put(Constant.PARAMETERS, parameters);
        templateData.put("mediatorXml", mediatorXml);
        StringWriter writer = new StringWriter();
        return mediatorHandler.getMustacheTemplate(AGENT_TOOL_TEMPLATE).execute(writer, templateData).toString();
    }

    /**
     * Retrieves the schema for the specified mediator to use as a tool.
     *
     * @param mediatorName the name of the mediator
     * @return the schema as a JsonObject
     */
    public JsonObject getToolSchema(String mediatorName) {

        boolean isConnector = mediatorName != null && mediatorName.contains(".");
        JsonObject schema = mediatorHandler.getUiSchema(mediatorName, null, null);
        JsonObject toolSchema = mediatorHandler.getUiSchema(Constant.TOOL, null, null).deepCopy();
        JsonObject mediatorSchema = null;
        if (schema != null) {
            mediatorSchema = schema.deepCopy();
            JsonArray elements = mediatorSchema.getAsJsonArray(Constant.ELEMENTS);
            JsonArray newElements = new JsonArray();

            // Remove the resultExpression field as it is not needed for connectors with response model
            if (isResponseModelAvailable(mediatorName)) {
                removeResultExpressionField(toolSchema);
            }

            if (isConnector) {
                ConnectorAction operation = ConnectorHolder.getInstance().getConnectorAction(mediatorName);
                String operationName = operation.getName();
                String operationDescription = operation.getDescription();

                // Update the tool name and description fields in schema with the connector operation description
                toolSchema.getAsJsonObject(Constant.VALUE).getAsJsonArray(Constant.ELEMENTS).get(0).getAsJsonObject()
                        .getAsJsonObject(Constant.VALUE).addProperty(Constant.CURRENT_VALUE, operationName);
                toolSchema.getAsJsonObject(Constant.VALUE).getAsJsonArray(Constant.ELEMENTS).get(1).getAsJsonObject()
                        .getAsJsonObject(Constant.VALUE).addProperty(Constant.CURRENT_VALUE, operationDescription);
            }
            newElements.add(toolSchema);

            markAIValueSupportedFields(elements, null);

            // Wrap the mediator/connector configuration fields with an attributeGroup to separate from tool fields
            JsonObject wrappedMediatorSchema = wrapMediatorSchema(elements, isConnector);
            newElements.add(wrappedMediatorSchema);
            mediatorSchema.add(Constant.ELEMENTS, newElements); // Replace the elements with the aggregated elements
        }
        return mediatorSchema;
    }

    private void removeResultExpressionField(JsonObject toolSchema) {

        toolSchema.get(Constant.VALUE).getAsJsonObject().getAsJsonArray(Constant.ELEMENTS)
                .remove(2); // index 2 is the resultExpression field. @{link ui-schemas/tool.json}
    }

    /**
     * Wraps the mediator/connector configuration elements into a new attribute group.
     */
    private JsonObject wrapMediatorSchema(JsonArray elements, boolean isConnector) {

        JsonObject wrapMediatorSchema = new JsonObject();
        new JsonObject();
        wrapMediatorSchema.addProperty("type", "attributeGroup");
        JsonObject wrapMediatorValueObj = new JsonObject();
        wrapMediatorValueObj.addProperty("groupName",
                String.format("%s Configuration", isConnector ? "Operation" : "Connector"));
        wrapMediatorValueObj.add(Constant.ELEMENTS, elements); // Add the existing elements of the mediator
        wrapMediatorSchema.add(Constant.VALUE, wrapMediatorValueObj);
        return wrapMediatorSchema;
    }

    private boolean isResponseModelAvailable(String connectorTag) {

        if (StringUtils.isEmpty(connectorTag) || !connectorTag.contains(".")) {
            return false;
        }
        ConnectorAction operation = mediatorHandler.getConnectorOperation(null, connectorTag);
        if (operation != null) {
            return operation.isSupportsResponseModel();
        }
        return false;
    }

    /**
     * Enhances the schema by adding the `supportsAIValues` field.
     *
     * @param elements - The schema elements to modify.
     */
    private void markAIValueSupportedFields(JsonArray elements, Template template) {

        JsonArray descriptionElements = new JsonArray();
        for (JsonElement element : elements) {
            JsonObject elementObj = getElementObject(element);
            if (elementObj == null) {
                continue;
            }
            if (Constant.ATTRIBUTE.equals(elementObj.get(Constant.TYPE).getAsString())) {
                JsonObject valueObj = elementObj.getAsJsonObject(Constant.VALUE);
                if (valueObj.get(Constant.INPUT_TYPE).getAsString().matches("(?i).*expression")) {
                    valueObj.addProperty(SUPPORTS_AI_VALUES, true);
                    addParameterDescriptionField(valueObj, template);
                }

                // Hide the overwrite body field as it is not needed for AI tools
                JsonElement value = valueObj.get(Constant.NAME);
                if (value instanceof JsonPrimitive && Constant.OVERWRITE_BODY.equals(value.getAsString())) {
                    valueObj.addProperty("hidden", true);
                }
            } else if (Constant.ATTRIBUTE_GROUP.equals(elementObj.get(Constant.TYPE).getAsString())) {
                markAIValueSupportedFields(
                        elementObj.getAsJsonObject(Constant.VALUE).getAsJsonArray(Constant.ELEMENTS), template);
            }
        }
        elements.addAll(descriptionElements);
    }

    private JsonObject getElementObject(JsonElement element) {

        if (!element.isJsonObject()) {
            return null;
        }
        JsonObject elementObj = element.getAsJsonObject();
        if (!elementObj.has(Constant.TYPE) || !elementObj.has(Constant.VALUE)) {
            return null;
        }
        return elementObj;
    }

    private JsonObject getParameterDescriptionObject(Template template, String parameterName, String helpTip) {

        JsonObject description = new JsonObject();
        description.addProperty(Constant.DEFAULT_VALUE, helpTip);
        if (template != null && template.hasParameter(parameterName)) {
            TemplateParameter parameter = template.getParameter(parameterName);
            description.addProperty(Constant.CURRENT_VALUE, parameter.getDescription());
        } else {
            description.addProperty(Constant.CURRENT_VALUE, helpTip);
        }
        return description;
    }

    private void addParameterDescriptionField(JsonObject valueObj, Template template) {

        String parameterName = valueObj.get(Constant.NAME).getAsString();
        String helpTip =
                valueObj.has(Constant.HELP_TIP) ? valueObj.get(Constant.HELP_TIP).getAsString() :
                        parameterName;
        JsonObject descriptionElement = getParameterDescriptionObject(template, parameterName, helpTip);

        JsonElement currentValue = valueObj.get(Constant.CURRENT_VALUE);
        if (currentValue instanceof JsonObject) {
            ((JsonObject) currentValue).add(Constant.DESCRIPTION, descriptionElement);
        } else if (!valueObj.get(Constant.INPUT_TYPE).getAsString().contains("boolean")) {
            valueObj.add(Constant.CURRENT_VALUE,
                    createNewValueObjectForToolField(currentValue, valueObj, descriptionElement));
        }
    }

    private JsonElement createNewValueObjectForToolField(JsonElement currentValue, JsonObject valueObj,
                                                         JsonObject descriptionElement) {

        String inputType = valueObj.get(Constant.INPUT_TYPE).getAsString();
        JsonPrimitive value = getCurrentValueForToolField(currentValue, valueObj);
        JsonObject currentValueObj = new JsonObject();
        currentValueObj.add(Constant.VALUE, value);
        currentValueObj.addProperty(Constant.IS_EXPRESSION, Constant.EXPRESSION.equals(inputType));
        currentValueObj.add(Constant.DESCRIPTION, descriptionElement);
        return currentValueObj;
    }

    private JsonPrimitive getCurrentValueForToolField(JsonElement currentValue, JsonObject valueObj) {

        JsonPrimitive value;
        JsonElement defaultValue = valueObj.get(Constant.DEFAULT_VALUE);
        if (currentValue != null) {
            value = currentValue.getAsJsonPrimitive();
        } else if (defaultValue != null) {
            value = defaultValue.getAsJsonPrimitive();
        } else {
            value = new JsonPrimitive(StringUtils.EMPTY);
        }
        return value;
    }

    /**
     * Retrieves the ui schema with current values for the agent tool at the given position.
     *
     * @param document current document
     * @param position position of the tool
     * @return the schema with current values
     */
    public JsonObject getToolSchemaWithValues(DOMDocument document, Position position) {

        try {
            Position toolPosition = new Position(position.getLine(), position.getCharacter() + 1);
            int offset = document.offsetAt(toolPosition);
            DOMNode node = document.findNodeAt(offset);
            if (node == null || !Constant.TOOL.equals(node.getNodeName())) {
                return null;
            }
            Template template = getSequenceTemplateForTool(node);
            if (isValidTool(template)) {
                Mediator mediator = template.getSequence().getMediatorList().get(0);
                JsonObject schema = mediatorHandler.getUISchemaForSTNode(mediator);
                markValueExpectedFromAI(schema, template);
                markAIValueSupportedFields(schema.getAsJsonArray(Constant.ELEMENTS), template);
                addToolConfigurations(schema, node, mediator);
                return schema;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error while getting tool schema with values", e);
        }
        return null;
    }

    /**
     * Adds tool configuration with the current values to the mediator/connector schema.
     */
    private void addToolConfigurations(JsonObject schema, DOMNode node, Mediator mediator) {

        JsonObject toolConfigSchema = mediatorHandler.getUiSchema(Constant.TOOL).deepCopy();
        Map<String, Object> toolData = new HashMap<>();
        toolData.put(TOOL_NAME, node.getAttribute(Constant.NAME));

        JsonObject expression = new JsonObject();
        expression.addProperty(Constant.IS_EXPRESSION, true);
        expression.addProperty(Constant.VALUE, node.getAttribute(RESULT_EXPRESSION));
        toolData.put(TOOL_RESULT_EXPRESSION, expression);

        toolData.put(TOOL_DESCRIPTION, node.getAttribute(Constant.DESCRIPTION));
        JsonArray elements = toolConfigSchema.getAsJsonObject(Constant.VALUE).getAsJsonArray(Constant.ELEMENTS);

        // Add the current values to the tool config schema
        for (JsonElement element : elements) {
            JsonObject valueObj = element.getAsJsonObject().getAsJsonObject(Constant.VALUE);
            Object currentValue = toolData.get(valueObj.get(Constant.NAME).getAsString());
            if (currentValue instanceof JsonObject) {
                valueObj.add(Constant.CURRENT_VALUE, (JsonObject) currentValue);
            } else {
                valueObj.addProperty(Constant.CURRENT_VALUE, currentValue.toString());
            }
        }

        JsonArray processedElements = new JsonArray();
        JsonArray mediatorSchemaElements = schema.getAsJsonArray(Constant.ELEMENTS);

        boolean isConnector = mediator instanceof Connector;

        // Remove the resultExpression field if the connector supports response model
        if (isResponseModelAvailable(mediator.getTag())) {
            removeResultExpressionField(toolConfigSchema);
        }
        processedElements.add(toolConfigSchema);

        JsonObject mediatorConfigurationSchema = wrapMediatorSchema(mediatorSchemaElements, isConnector);
        processedElements.add(mediatorConfigurationSchema);
        schema.add(Constant.ELEMENTS, processedElements);
    }

    /**
     * Processes the mediator schema to add the values expected from AI.
     * <p>
     * The existing mediator configurations has the fields that expects values from AI as a functional param. This will
     * be marked as a value expected from AI and added to the parameter description elements for those fields.
     * </p>
     */
    private void markValueExpectedFromAI(JsonObject schema, Template template) {

        JsonElement elements = schema.get(Constant.ELEMENTS);
        if (elements != null && elements.isJsonArray()) {
            JsonArray elementsArray = elements.getAsJsonArray();
            JsonArray parameterDescriptionElements = new JsonArray();
            for (JsonElement childElement : elementsArray) {
                if (Constant.ATTRIBUTE_GROUP.equals(childElement.getAsJsonObject().get(Constant.TYPE).getAsString())) {
                    markValueExpectedFromAI(
                            childElement.getAsJsonObject().getAsJsonObject(Constant.VALUE).getAsJsonObject(), template);
                } else if (Constant.ATTRIBUTE.equals(childElement.getAsJsonObject().get(Constant.TYPE).getAsString())) {
                    markAIValueForAttributeElement(childElement);
                }
            }
            elementsArray.addAll(parameterDescriptionElements);
        }
    }

    private void markAIValueForAttributeElement(JsonElement childElement) {

        JsonObject valueObj = childElement.getAsJsonObject().getAsJsonObject(Constant.VALUE);

        // Mark functional params as values expected from AI and add to the parameter description elements
        // for those fields.
        if (valueObj.get(Constant.CURRENT_VALUE) instanceof JsonObject &&
                valueObj.getAsJsonObject(Constant.CURRENT_VALUE).get(Constant.VALUE).getAsString()
                        .startsWith(FUNCTION_PARAM_PREFIX)) {

            JsonObject currentValue = new JsonObject();
            currentValue.addProperty(Constant.VALUE, FROM_AI);
            currentValue.addProperty(FROM_AI, true);
            currentValue.addProperty(Constant.IS_EXPRESSION, false);
            valueObj.add(Constant.CURRENT_VALUE, currentValue);
        }
    }

    /**
     * Checks whether the tool is valid.
     * <p>
     * The sequence template should have a sequence with at least one mediator to be eligible as a tool.
     * </p>
     */
    private boolean isValidTool(STNode stnode) {

        if (!(stnode instanceof Template)) {
            return false;
        }
        Template template = (Template) stnode;
        return template.getSequence() != null && template.getSequence().getMediatorList() != null &&
                !template.getSequence().getMediatorList().isEmpty();
    }

    /**
     * Returns the sequence template referenced in the tool definition.
     *
     * <p>
     * This method retrieves the {@code Template} defined in the {@code <tool template="key"/>}.
     * </p>
     *
     * @param node the tool node
     * @return the sequence template
     * @throws IOException if an error occurs while reading the template file
     */
    private Template getSequenceTemplateForTool(DOMNode node) throws IOException {

        String templateName = node.getAttribute(Constant.TEMPLATE);
        if (StringUtils.isNotEmpty(templateName)) {
            String templatePath = ConfigFinder.getTemplatePath(templateName, projectUri);
            if (StringUtils.isNotEmpty(templatePath)) {
                DOMDocument document = Utils.getDOMDocumentFromPath(templatePath);
                if (document != null) {
                    return (Template) SyntaxTreeGenerator.buildTree(document.getDocumentElement());
                }
            }
        }
        return null;
    }

    private SynapseConfigResponse modifyAgentTool(String documentUri, Range range, String mediator,
                                                  Map<String, Object> data, List<String> dirtyFields)
            throws IOException, BadLocationException {

        SynapseConfigResponse agentEditResponse = new SynapseConfigResponse();

        DOMDocument document = Utils.getDOMDocument(new File(documentUri));

        // Increment the character position by 1 to get the tool tag
        Position position = new Position(range.getStart().getLine(), range.getStart().getCharacter() + 1);
        DOMNode node = document.findNodeAt(document.offsetAt(position));
        if (node == null || !Constant.TOOL.equals(node.getNodeName())) {
            return null;
        }
        String templateName = node.getAttribute(Constant.TEMPLATE);
        if (StringUtils.isEmpty(templateName)) {
            return null;
        }


        // Add tool tag edit
        boolean needToolEdit = dirtyFields.stream().anyMatch(TOOL_EDIT_FIELDS::contains) ||
                data.containsKey(Constant.RESPONSE_VARIABLE);
        if (needToolEdit) {
            Map<String, String> toolData = processToolData(data, templateName);
            StringWriter writer = new StringWriter();
            String toolsEdit = mediatorHandler.getMustacheTemplate(Constant.TOOL).execute(writer, toolData).toString();
            TextEdit toolsEditTextEdit = new DocumentTextEdit(range, toolsEdit, documentUri);
            agentEditResponse.addTextEdit(toolsEditTextEdit);
        }

        boolean needTemplateEdit = dirtyFields.stream().anyMatch(field -> !TOOL_EDIT_FIELDS.contains(field));
        boolean isToolDescriptionChanged = dirtyFields.contains(TOOL_DESCRIPTION);

        if (!needTemplateEdit && !isToolDescriptionChanged) {
            return agentEditResponse;
        }

        // Generate mediator/connector (tool) edit for the sequence template
        String sequenceTemplatePath = ConfigFinder.getTemplatePath(templateName, projectUri);
        if (StringUtils.isEmpty(sequenceTemplatePath) && Files.notExists(Path.of(sequenceTemplatePath))) {
            return agentEditResponse;
        }
        DOMDocument sequenceTemplateDocument = Utils.getDOMDocumentFromPath(sequenceTemplatePath);
        STNode stNode = SyntaxTreeGenerator.buildTree(sequenceTemplateDocument.getDocumentElement());
        if (needTemplateEdit) {
            modifySequenceTemplate(stNode, data, dirtyFields, mediator, sequenceTemplatePath, agentEditResponse);
        }
        if (isToolDescriptionChanged) {
            modifySequenceTemplateDescription(stNode, sequenceTemplatePath, data.get(TOOL_DESCRIPTION).toString(),
                    agentEditResponse);
        }
        return agentEditResponse;
    }

    private void modifySequenceTemplateDescription(STNode stNode, String sequenceTemplatePath, String description,
                                                   SynapseConfigResponse agentEditResponse) {

        if (!isValidTool(stNode)) {
            return;
        }
        Template template = (Template) stNode;
        Range editRange;
        STNode descriptionNode = template.getDescription();
        if (descriptionNode != null) {
            editRange = getSTNodeRange(descriptionNode);
        } else {
            Position templateOpenTagEndPosition = template.getRange().getStartTagRange().getEnd();
            editRange = new Range(templateOpenTagEndPosition, templateOpenTagEndPosition);
        }
        String descriptionXml = String.format("<description>%s</description>", description);
        agentEditResponse.addTextEdit(new DocumentTextEdit(editRange, descriptionXml, sequenceTemplatePath));
    }

    private void modifySequenceTemplate(STNode stNode, Map<String, Object> data, List<String> dirtyFields,
                                        String mediator, String sequenceTemplatePath,
                                        SynapseConfigResponse agentEditResponse) {

        if (!isValidTool(stNode)) {
            return;
        }
        Template template = (Template) stNode;
        Mediator toolMediator = template.getSequence().getMediatorList().get(0);
        Range toolMediatorRange = getSTNodeRange(toolMediator);

        // Remove overwrite body as we need the mediator response in the variable. TODO: Remove this field from tool UI
        data.remove(Constant.OVERWRITE_BODY);

        Map<String, String> templateParameters = new HashMap<>();
        processAIValues(data, templateParameters);
        SynapseConfigResponse mediatorEdits =
                mediatorHandler.generateSynapseConfig(sequenceTemplatePath, toolMediatorRange, mediator, data,
                        dirtyFields);

        for (TextEdit edit : mediatorEdits.getTextEdits()) {
            agentEditResponse.addTextEdit(
                    new DocumentTextEdit(edit.getRange(), edit.getNewText(), sequenceTemplatePath));
        }

        // Generate template parameter xml
        Range templateParameterRange = getTemplateParameterRange(template);
        StringBuilder parameterXmlBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : templateParameters.entrySet()) {
            String newParameter = String.format("<parameter name=\"%s\" isMandatory=\"true\" description=\"%s\"/>%n",
                    entry.getKey(), entry.getValue());
            parameterXmlBuilder.append(newParameter);
        }
        TextEdit parameterEdit =
                new DocumentTextEdit(templateParameterRange, parameterXmlBuilder.toString(), sequenceTemplatePath);
        agentEditResponse.addTextEdit(parameterEdit);
    }

    private Range getTemplateParameterRange(Template template) {

        TemplateParameter[] parameters = template.getParameter();
        if (parameters == null || parameters.length == 0) {
            Position sequenceStart = template.getSequence().getRange().getStartTagRange().getStart();
            return new Range(sequenceStart, sequenceStart);
        }
        Position startPosition = parameters[0].getRange().getStartTagRange().getStart();
        Position endPosition = template.getSequence().getRange().getStartTagRange().getStart();
        return new Range(startPosition, endPosition);
    }

    private Range getSTNodeRange(STNode stNode) {

        Range startTagRange = stNode.getRange().getStartTagRange();
        Range endTagRange = stNode.getRange().getEndTagRange();
        Position start = startTagRange.getStart();
        Position end = endTagRange != null ? endTagRange.getEnd() : startTagRange.getEnd();
        return new Range(start, end);
    }
}
