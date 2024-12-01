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

package org.eclipse.lemminx.customservice.synapse.expression;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.eclipse.lemminx.customservice.synapse.expression.pojo.FunctionCompletionItem;
import org.eclipse.lemminx.customservice.synapse.utils.Utils;
import org.eclipse.lemminx.extensions.contentmodel.participants.completion.AttributeValueCompletionResolver;
import org.eclipse.lemminx.services.data.DataEntryField;
import org.eclipse.lemminx.services.extensions.completion.ICompletionRequest;
import org.eclipse.lemminx.services.extensions.completion.ICompletionResponse;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionItemKind;
import org.eclipse.lsp4j.InsertTextFormat;
import org.eclipse.lsp4j.ParameterInformation;
import org.eclipse.lsp4j.SignatureInformation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ExpressionCompletionUtils {

    private static final Logger LOGGER = Logger.getLogger(ExpressionCompletionUtils.class.getName());
    private static Map<String, List<CompletionItem>> functions;

    static {
        try {
            functions = new HashMap<>();
            loadFunctionCompletions();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load function completions", e);
        }
    }

    private static void loadFunctionCompletions() throws IOException {

        try (InputStream inputStream = ExpressionCompletionUtils.class
                .getClassLoader()
                .getResourceAsStream("org/eclipse/lemminx/expression/functions.json")) {

            if (inputStream == null) {
                LOGGER.log(Level.SEVERE, "Failed to load synapse expression functions");
            }
            String jsonContent;
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                jsonContent = reader.lines().collect(Collectors.joining("\n"));
            }
            JsonObject functionsObject = Utils.getJsonObject(jsonContent);
            if (functionsObject != null) {
                functionsObject.keySet().forEach(key -> {
                    List<CompletionItem> functionList = new ArrayList<>();
                    JsonArray jsonArray = functionsObject.getAsJsonArray(key);
                    for (JsonElement element : jsonArray) {
                        JsonObject jsonObject = element.getAsJsonObject();
                        String label = jsonObject.get(ExpressionConstants.LABEL).getAsString();
                        String insertText = jsonObject.get(ExpressionConstants.INSERT_TEXT).getAsString();
                        String detail = jsonObject.get(ExpressionConstants.DETAIL).getAsString();
                        int order = jsonObject.get(ExpressionConstants.CATEGORY).getAsInt();
                        JsonObject signature = jsonObject.getAsJsonObject(ExpressionConstants.SIGNATURE);
                        functionList.add(
                                createFunctionCompletionItem(label, insertText, signature, detail,
                                        CompletionItemKind.Function, order));
                    }
                    functions.put(key, functionList);
                });
            }
        }
    }

    /**
     * Get all the synapse expression functions.
     *
     * @return functions
     */
    public static Map<String, List<CompletionItem>> getFunctions() {

        return Collections.unmodifiableMap(functions);
    }

    /**
     * Add root level completions.
     *
     * @param response   completion response
     * @param filterText filter text
     */
    public static void addRootLevelCompletions(ICompletionResponse response, String filterText) {

        List<CompletionItem> completionItems = new ArrayList<>();
        addRootLevelObjectCompletions(completionItems);
        addFunctionCompletions(completionItems);
        for (CompletionItem item : completionItems) {
            if (item.getLabel().startsWith(filterText)) {
                response.addCompletionItem(item);
            }
        }
    }

    private static void addRootLevelObjectCompletions(List<CompletionItem> items) {

        items.add(createCompletionItem("var", "var", "Access defined variables", CompletionItemKind.Keyword, 0, false));
        items.add(createCompletionItem("attributes", "attributes", "Access defined attributes",
                CompletionItemKind.Keyword, 0,
                false));
        items.add(createCompletionItem("headers", "headers", "Access defined headers", CompletionItemKind.Keyword, 0,
                false));
        items.add(createCompletionItem("payload", "payload", "Access defined payload", CompletionItemKind.Keyword, 0,
                false));
    }

    private static void addFunctionCompletions(List<CompletionItem> items) {

        List<CompletionItem> functionCompletions = new ArrayList<>();
        functions.values().forEach(functionCompletions::addAll);
        items.addAll(Collections.unmodifiableCollection(functionCompletions));
    }

    /**
     * Add attribute completions.
     *
     * @param request  completion request
     * @param response completion response
     */
    public static void addOperatorCompletions(ICompletionRequest request, ICompletionResponse response) {

        addCompletionItem(request, response, "+", "Addition", CompletionItemKind.Operator, 0, false);
        addCompletionItem(request, response, "-", "Subtraction", CompletionItemKind.Operator, 0, false);
        addCompletionItem(request, response, "*", "Multiplication", CompletionItemKind.Operator, 0, false);
        addCompletionItem(request, response, "/", "Division", CompletionItemKind.Operator, 0, false);
        addCompletionItem(request, response, "? ${1} : ${2}", "Ternary operator", CompletionItemKind.Operator, 0, true);
    }

    /**
     * Add completion item.
     *
     * @param request    completion request
     * @param response   completion response
     * @param completion completion text
     * @param detail     completion detail
     * @param kind       completion kind
     * @param order      completion category
     * @param isSnippet  is snippet completion
     * @return
     */
    public static CompletionItem addCompletionItem(ICompletionRequest request, ICompletionResponse response,
                                                   String completion, String detail, CompletionItemKind kind, int order,
                                                   boolean isSnippet) {

        return addCompletionItem(request, response, completion, completion, detail, kind, order, isSnippet);
    }

    /**
     * Create function completion item.
     *
     * @param label      completion label
     * @param insertText insert text
     * @param signature  function signature
     * @param detail     completion detail
     * @param kind       completion kind
     * @param order      completion category
     * @return
     */
    public static CompletionItem createFunctionCompletionItem(String label, String insertText, JsonObject signature,
                                                              String detail,
                                                              CompletionItemKind kind, int order) {

        CompletionItem item = new FunctionCompletionItem();
        item.setLabel(label);
        item.setKind(kind);
        item.setDetail(detail);
        item.setInsertText(insertText);
        item.setSortText(order + "_" + insertText);
        item.setInsertTextFormat(InsertTextFormat.Snippet);

        if (signature != null) {
            SignatureInformation signatureInformation = new SignatureInformation();
            signatureInformation.setLabel(label);
            List<ParameterInformation> parameterInformation = createParameterInformation(signature);
            signatureInformation.setParameters(parameterInformation);
            ((FunctionCompletionItem) item).setSignature(signatureInformation);
        }
        return item;
    }

    private static List<ParameterInformation> createParameterInformation(JsonObject signature) {

        List<ParameterInformation> parameterInformation = new ArrayList<>();
        for (Map.Entry<String, JsonElement> entry : signature.entrySet()) {
            ParameterInformation parameter = new ParameterInformation();
            parameter.setLabel(entry.getKey());
            parameter.setDocumentation(entry.getValue().getAsString());
            parameterInformation.add(parameter);
        }
        return parameterInformation;
    }

    /**
     * Clone completion item.
     *
     * @param item completion item
     * @return
     */
    public static CompletionItem cloneCompletionItem(CompletionItem item) {

        CompletionItem newItem = new CompletionItem();
        newItem.setLabel(item.getLabel());
        newItem.setKind(item.getKind());
        newItem.setDetail(item.getDetail());
        newItem.setInsertText(item.getInsertText());
        newItem.setSortText(item.getSortText());
        newItem.setInsertTextFormat(item.getInsertTextFormat());
        return newItem;
    }

    /**
     * Create completion item.
     *
     * @param label      completion label
     * @param insertText insert text
     * @param detail     completion detail
     * @param kind       completion kind
     * @param order      completion category
     * @param isSnippet  is snippet completion
     * @return
     */
    public static CompletionItem createCompletionItem(String label, String insertText, String detail,
                                                      CompletionItemKind kind, int order, boolean isSnippet) {

        CompletionItem item = new CompletionItem();
        item.setLabel(label);
        item.setKind(kind);
        item.setDetail(detail);
        item.setInsertText(insertText);
        item.setSortText(order + "_" + insertText);
        item.setInsertTextFormat(isSnippet ? InsertTextFormat.Snippet : InsertTextFormat.PlainText);
        return item;
    }

    /**
     * Add completion item.
     *
     * @param request    completion request
     * @param response   completion response
     * @param label      completion label
     * @param insertText insert text
     * @param detail     completion detail
     * @param kind       completion kind
     * @param order      completion category
     * @param isSnippet  is snippet completion
     * @return
     */
    public static CompletionItem addCompletionItem(ICompletionRequest request, ICompletionResponse response,
                                                   String label, String insertText, String detail,
                                                   CompletionItemKind kind, int order,
                                                   boolean isSnippet) {

        CompletionItem item = new CompletionItem();
        item.setLabel(label);
        item.setKind(kind);
        item.setDetail(detail);
        item.setInsertText(insertText);
        item.setSortText(order + "_" + insertText);
        item.setInsertTextFormat(isSnippet ? InsertTextFormat.Snippet : InsertTextFormat.PlainText);
        if (request.isResolveDocumentationSupported()) {
            addResolveData(request, item, AttributeValueCompletionResolver.PARTICIPANT_ID);
        }
        response.addCompletionItem(item);
        return item;
    }

    private static void addResolveData(ICompletionRequest request, CompletionItem item, String participantId) {

        JsonObject data = DataEntryField.createCompletionData(request, participantId);
        item.setData(data);
    }

    /**
     * Add attribute second level completions.
     *
     * @param request    completion request
     * @param response   completion response
     * @param filterText filter text
     */
    public static void addAttributeSecondLevelCompletions(ICompletionRequest request, ICompletionResponse response,
                                                          String filterText) {

        ExpressionConstants.ATTRIBUTES_SECOND_LEVEL.forEach(value -> {
            if (value.startsWith(filterText)) {
                addCompletionItem(request, response, value, value, "Attribute", CompletionItemKind.Keyword, 0, false);
            }
        });
    }

    private ExpressionCompletionUtils() {

    }
}
