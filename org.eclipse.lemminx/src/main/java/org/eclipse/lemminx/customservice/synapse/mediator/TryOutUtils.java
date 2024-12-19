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

package org.eclipse.lemminx.customservice.synapse.mediator;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.lemminx.commons.BadLocationException;
import org.eclipse.lemminx.customservice.synapse.debugger.entity.Breakpoint;
import org.eclipse.lemminx.customservice.synapse.debugger.entity.debuginfo.IDebugInfo;
import org.eclipse.lemminx.customservice.synapse.debugger.visitor.VisitorUtils;
import org.eclipse.lemminx.customservice.synapse.mediator.tryout.pojo.Edit;
import org.eclipse.lemminx.customservice.synapse.mediator.tryout.pojo.InvalidConfigurationException;
import org.eclipse.lemminx.customservice.synapse.mediator.tryout.pojo.MediatorInfo;
import org.eclipse.lemminx.customservice.synapse.mediator.tryout.pojo.MediatorTryoutRequest;
import org.eclipse.lemminx.customservice.synapse.mediator.tryout.pojo.Property;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.SyntaxTreeGenerator;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.NamedSequence;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.STNode;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.api.API;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.api.APIResource;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.Mediator;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.Respond;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.misc.common.Sequence;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.api.APISerializer;
import org.eclipse.lemminx.customservice.synapse.utils.Constant;
import org.eclipse.lemminx.customservice.synapse.utils.Utils;
import org.eclipse.lemminx.dom.DOMDocument;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class TryOutUtils {

    private TryOutUtils() {

    }

    /**
     * This method is used to get the offset of the given position in the content.
     *
     * @param content
     * @param position
     * @return
     */
    public static int getOffset(String content, Position position) {

        String[] lines = content.split("\n", -1);
        int offset = 0;
        for (int i = 0; i < position.getLine(); i++) {
            offset += lines[i].length() + 1;
        }
        return offset + position.getCharacter();
    }

    /**
     * This method is used to check whether the actual breakpoint is the expected breakpoint.
     *
     * @param actual
     * @param expected
     * @return
     */
    public static boolean isExpectedBreakpoint(JsonObject actual, IDebugInfo expected) {

        JsonElement actualSequenceData = actual.get(Constant.SEQUENCE);

        JsonObject breakpointJson = expected.toJson().getAsJsonObject();
        JsonElement expectedSequenceData = breakpointJson.get(Constant.SEQUENCE);

        return expectedSequenceData != null
                && expectedSequenceData.equals(actualSequenceData);
    }

    /**
     * Relativizes a target path against a given source path and then resolves it
     * against another source path to produce a new path.
     * <p>
     * This method first computes the relative path from {@code relativizeSourcePath}
     * to {@code relativizeTargetPath}. It then resolves this relative path
     * against {@code resolveSourcePath}.
     *
     * @param relativizeSourcePath the base path against which {@code relativizeTargetPath} will be relativized
     * @param relativizeTargetPath the target path to be relativized against {@code relativizeSourcePath}
     * @param resolveSourcePath    the path against which the computed relative path will be resolved
     * @return the resulting path obtained by resolving the relative path between
     * {@code relativizeSourcePath} and {@code relativizeTargetPath} against {@code resolveSourcePath}
     * @throws IllegalArgumentException if {@code relativizeSourcePath} and {@code relativizeTargetPath} are not of the same type (both relative or both absolute)
     */
    public static Path relativizeAndResolvePath(Path relativizeSourcePath, Path relativizeTargetPath,
                                                Path resolveSourcePath) {

        Path relativeFilePath = relativizeSourcePath.relativize(relativizeTargetPath);
        return resolveSourcePath.resolve(relativeFilePath);
    }

    /**
     * This method is used to clone and preprocess the project.
     *
     * @param projectUri the project URI
     * @param request   the tryout request
     * @param tempFolder the temporary folder to clone the project
     * @return the path of the file in which the edits are applied
     * @throws IOException
     */
    public static Path cloneAndPreprocessProject(String projectUri, MediatorTryoutRequest request, Path tempFolder)
            throws IOException {

        Path projectPath = Path.of(projectUri);
        Utils.copyFolder(projectPath, tempFolder, null);

        Path editFilePath = TryOutUtils.relativizeAndResolvePath(projectPath, Path.of(request.getFile()), tempFolder);
        removeBelowMediators(editFilePath, new Position(request.getLine(), request.getColumn()));
        doEdits(request.getEdits(), editFilePath);         // Apply the edits from user
        return editFilePath;
    }

    private static void removeBelowMediators(Path editFilePath, Position position) throws IOException {

        DOMDocument document = Utils.getDOMDocument(editFilePath.toFile());
        if (document == null) {
            return;
        }
        STNode root = SyntaxTreeGenerator.buildTree(document.getDocumentElement());
        if (root instanceof NamedSequence) {
            removeBelowMediators((NamedSequence) root, position, editFilePath);
        } else if (root instanceof API) {
            removeBelowMediators((API) root, position, editFilePath);
        }
    }

    private static void removeBelowMediators(NamedSequence root, Position position, Path editFilePath)
            throws IOException {

        Mediator currentMediator = findCurrentMediator(root.getMediatorList(), position);
        if (currentMediator != null) {
            Position sequenceEndTagStart = root.getRange().getEndTagRange().getStart();
            removeContentAfterMediator(currentMediator, sequenceEndTagStart, editFilePath);
        }
    }

    private static void removeBelowMediators(API root, Position position, Path editFilePath) throws IOException {

        if (root == null) {
            return;
        }
        APIResource[] resources = root.getResource();
        if (resources == null) {
            return;
        }
        Mediator mediator = null;
        Position end = null;
        for (APIResource resource : resources) {
            if (isNodeInRange(resource, position)) {
                if (isNodeInRange(resource.getInSequence(), position)) {
                    mediator = findCurrentMediator(resource.getInSequence().getMediatorList(), position);
                    end = resource.getInSequence().getRange().getEndTagRange().getStart();
                } else if (isNodeInRange(resource.getOutSequence(), position)) {
                    mediator = findCurrentMediator(resource.getOutSequence().getMediatorList(), position);
                    end = resource.getOutSequence().getRange().getEndTagRange().getStart();
                } else if (isNodeInRange(resource.getFaultSequence(), position)) {
                    mediator = findCurrentMediator(resource.getFaultSequence().getMediatorList(), position);
                    end = resource.getFaultSequence().getRange().getEndTagRange().getStart();
                }
            }
        }
        removeContentAfterMediator(mediator, end, editFilePath);
    }

    private static void removeContentAfterMediator(Mediator mediator, Position end, Path editFilePath)
            throws IOException {

        if (mediator == null || end == null) {
            return;
        }
        Position mediatorEnd =
                mediator.getRange().getEndTagRange() != null ? mediator.getRange().getEndTagRange().getEnd() :
                        mediator.getRange().getStartTagRange().getEnd();
        mediatorEnd.setCharacter(mediatorEnd.getCharacter() + 1);
        end.setCharacter(end.getCharacter() - 1);
        doEdit(new Edit(StringUtils.EMPTY, new Range(mediatorEnd, end)), editFilePath);
    }

    private static boolean isNodeInRange(STNode node, Position position) {

        return VisitorUtils.checkNodeInRange(node, new Breakpoint(position.getLine(), position.getCharacter()));
    }

    private static Mediator findCurrentMediator(List<Mediator> mediatorList, Position position) {

        if (mediatorList == null) {
            return null;
        }
        for (Mediator mediator : mediatorList) {
            if (isNodeInRange(mediator, position)) {
                return mediator;
            }
        }
        return null;
    }

    /**
     * This method is used to apply the edits to the file.
     *
     * @param edits        the edits to be applied
     * @param editFilePath the file in which the edits are applied
     * @throws IOException
     */
    public static void doEdits(Edit[] edits, Path editFilePath) throws IOException {

        if (edits != null && edits.length > 0) {
            for (Edit edit : edits) {
                doEdit(edit, editFilePath);
            }
        }
    }

    /**
     * This method is used to apply the edit to the file.
     *
     * @param edit         the edit to be applied
     * @param editFilePath the file in which the edit is applied
     * @throws IOException
     */
    public static void doEdit(Edit edit, Path editFilePath) throws IOException {

        String editContent = edit.getText();
        Range editRange = edit.getRange();

        String fileContent = Files.readString(editFilePath);

        int startOffset = TryOutUtils.getOffset(fileContent, editRange.getStart());
        int endOffset = TryOutUtils.getOffset(fileContent, editRange.getEnd());

        String newContent = fileContent.substring(0, startOffset) + editContent + fileContent.substring(endOffset);

        Files.writeString(editFilePath, newContent);
    }

    /**
     * This method is used to add a new log mediator to the sequence.
     *
     * @param document     the document
     * @param insertOffset the offset to insert the log mediator
     * @param editFilePath the file in which the log mediator is added
     * @throws BadLocationException
     * @throws IOException
     */
    public static void addNewLogMediator(DOMDocument document, int insertOffset, Path editFilePath)
            throws BadLocationException, IOException {

        String xml = "<log category=\"INFO\" level=\"full\"><property name=\"body\" expression=\"$body//\" /></log>";
        Edit edit = new Edit(xml, new Range(document.positionAt(insertOffset), document.positionAt(insertOffset)));
        TryOutUtils.doEdit(edit, editFilePath);
    }

    /**
     * This method is used to get the service url of the API.
     *
     * @param activeBreakpoints the active breakpoints
     * @param host              the host
     * @param port              the port
     * @return the service url
     */
    public static String getServiceUrl(Path apiPath, List<JsonObject> activeBreakpoints, String host, int port)
            throws IOException {

        DOMDocument document = Utils.getDOMDocument(apiPath.toFile());
        if (document == null) {
            return null;
        }
        API api = (API) SyntaxTreeGenerator.buildTree(document.getDocumentElement());
        String apiKey = api.getContext();
        JsonObject apiObj =
                activeBreakpoints.get(0).get(Constant.SEQUENCE).getAsJsonObject().get(Constant.API).getAsJsonObject();
        JsonObject resourceObj = apiObj.get(Constant.RESOURCE).getAsJsonObject();
        JsonElement urlMapping = resourceObj.get(Constant.URL_MAPPING);
        JsonElement uriMapping = resourceObj.get(TryOutConstants.URI_MAPPING);
        StringBuilder url = new StringBuilder();
        url.append(TryOutConstants.HTTP_PREFIX).append(host).append(":").append(port).append(apiKey);
        if (urlMapping != null && !urlMapping.isJsonNull()) {
            url.append(urlMapping.getAsString());
        } else if (uriMapping != null && !uriMapping.isJsonNull()) {
            url.append(uriMapping.getAsString());
        } else {
            url.append(TryOutConstants.SLASH);
        }
        return url.toString();
    }

    /**
     * This method is used to get the service method of the API.
     *
     * @param activeBreakpoints the active breakpoints
     * @return the service method
     */
    public static String getServiceMethod(List<JsonObject> activeBreakpoints) {

        return activeBreakpoints.get(0).get(Constant.SEQUENCE).getAsJsonObject().get(Constant.API).getAsJsonObject()
                .get(Constant.RESOURCE).getAsJsonObject().get(Constant.METHOD).getAsString();
    }

    /**
     * Check whether the given file is an API.
     *
     * @param projectUri
     * @param file
     * @return
     */
    public static boolean isApi(String projectUri, String file) {

        Path filePath = Path.of(file);
        Path relativePath = Path.of(projectUri).relativize(filePath);
        return relativePath.startsWith(TryOutConstants.API_RELATIVE_PATH);
    }

    /**
     * Create mediator info for the given property JSON String from the debugger.
     *
     * @param properties
     * @return
     */
    public static MediatorInfo createMediatorInfo(List<String> properties) {

        List<JsonObject> parsedProperties = parseProperties(properties);
        MediatorInfo mediatorInfo = new MediatorInfo();
        for (JsonObject property : parsedProperties) {
            if (property.has(TryOutConstants.SYNAPSE_PROPERTIES)) {
                mediatorInfo.addSynapseProperties(
                        parseProperties(property.getAsJsonObject(TryOutConstants.SYNAPSE_PROPERTIES)));
            } else if (property.has(TryOutConstants.AXIS2_PROPERTIES)) {
                mediatorInfo.addAxis2Properties(
                        parseProperties(property.getAsJsonObject(TryOutConstants.AXIS2_PROPERTIES)));
                mediatorInfo.setPayload(
                        property.getAsJsonObject(TryOutConstants.AXIS2_PROPERTIES).get(TryOutConstants.ENVELOPE)
                                .getAsJsonPrimitive());
            } else if (property.has(TryOutConstants.AXIS2_CLIENT_PROPERTIES)) {
                mediatorInfo.addAxis2ClientProperties(
                        parseProperties(property.getAsJsonObject(TryOutConstants.AXIS2_CLIENT_PROPERTIES)));
            } else if (property.has(TryOutConstants.AXIS2_TRANSPORT_PROPERTIES)) {
                mediatorInfo.addAxis2TransportProperties(
                        parseProperties(property.getAsJsonObject(TryOutConstants.AXIS2_TRANSPORT_PROPERTIES)));
            } else if (property.has(TryOutConstants.AXIS2_OPERATION_PROPERTIES)) {
                mediatorInfo.addAxis2OperationProperties(
                        parseProperties(property.getAsJsonObject(TryOutConstants.AXIS2_OPERATION_PROPERTIES)));
            } else if (property.has(TryOutConstants.MESSAGE_VARIABLES)) {
                mediatorInfo.addVariables(parseProperties(property.getAsJsonObject(TryOutConstants.MESSAGE_VARIABLES)));
            }
        }
        return mediatorInfo;
    }

    private static List<Property> parseProperties(JsonObject propertiesJson) {

        if (propertiesJson == null) {
            return Collections.emptyList();
        }
        List<Property> properties = new ArrayList<>();
        for (String key : propertiesJson.keySet()) {
            Property property;
            if (propertiesJson.get(key).isJsonPrimitive()) {
                property = new Property(key, propertiesJson.get(key).getAsString());
            } else if (propertiesJson.get(key).isJsonArray()) {
                property = new Property(key, propertiesJson.getAsJsonArray(key).toString());
            } else if (propertiesJson.get(key).isJsonObject()) {
                property = new Property(key, parseProperties(propertiesJson.getAsJsonObject(key)));
            } else {
                property = new Property(key, propertiesJson.get(key).toString());
            }
            properties.add(property);
        }
        return properties;
    }

    private static List<JsonObject> parseProperties(List<String> properties) {

        List<JsonObject> parsedProperties = new ArrayList<>();
        for (String response : properties) {
            Gson gson = new Gson();
            JsonObject eventJson = gson.fromJson(response, JsonObject.class);
            parsedProperties.add(eventJson);
        }
        return parsedProperties;
    }

    /**
     * Creates an API for executing the given mediator.
     *
     * @param mediator
     * @param tempPath
     * @return the path of the created API
     * @throws InvalidConfigurationException
     */
    public static String createAPI(Mediator mediator, String tempPath) throws InvalidConfigurationException {

        try {
            if (mediator != null) {
                String apiName = mediator.getTag() + "_tryout_" + UUID.randomUUID();
                API api = new API();
                api.setName(apiName);
                api.setContext(TryOutConstants.SLASH + apiName);
                APIResource resource = new APIResource();
                resource.setMethods(new String[]{"POST"});
                resource.setUrlMapping(TryOutConstants.SLASH);
                api.setResource(new APIResource[]{resource});
                Sequence sequence = new Sequence();
                resource.setInSequence(sequence);
                sequence.addToMediatorList(mediator);
                sequence.addToMediatorList(new Respond());
                String apiContent = APISerializer.serializeAPI(api);
                Path apiPath = Path.of(tempPath).resolve(TryOutConstants.API_RELATIVE_PATH)
                        .resolve(apiName + ".xml");
                if (!apiPath.toFile().exists()) {
                    apiPath.toFile().getParentFile().mkdirs();
                }
                Utils.writeToFile(apiPath.toString(), apiContent);
                return apiPath.toString();
            }
        } catch (IOException e) {
            throw new InvalidConfigurationException("Error while creating the API for the mediator", e);
        }
        return null;
    }

    /**
     * Returns the position of the mediator in the given resourceIndex and mediatorIndex.
     *
     * @param apiPath
     * @param resourceIndex
     * @param mediatorIndex
     * @return the position of the mediator
     * @throws IOException if an error occurs while getting the mediator position or if the api is not found
     */
    public static Position getMediatorPosition(String apiPath, int resourceIndex, int mediatorIndex)
            throws IOException {

        DOMDocument dom = Utils.getDOMDocument(new File(apiPath));
        if (dom != null) {
            API api = (API) SyntaxTreeGenerator.buildTree(dom.getDocumentElement());
            if (api != null) {
                APIResource resource = api.getResource()[resourceIndex];
                if (resource != null) {
                    Sequence sequence = resource.getInSequence();
                    if (sequence != null) {
                        List<Mediator> mediators = sequence.getMediatorList();
                        if (mediators != null && mediators.size() > mediatorIndex) {
                            return mediators.get(mediatorIndex).getRange().getStartTagRange().getStart();
                        }
                    }
                }
            }
            return null;
        }
        throw new IOException("Error while getting the mediator position");
    }
}
