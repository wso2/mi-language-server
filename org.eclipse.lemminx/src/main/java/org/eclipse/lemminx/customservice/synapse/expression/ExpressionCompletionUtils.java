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
import org.eclipse.lemminx.customservice.synapse.utils.Utils;
import org.eclipse.lemminx.extensions.contentmodel.participants.completion.AttributeValueCompletionResolver;
import org.eclipse.lemminx.services.data.DataEntryField;
import org.eclipse.lemminx.services.extensions.completion.ICompletionRequest;
import org.eclipse.lemminx.services.extensions.completion.ICompletionResponse;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionItemKind;
import org.eclipse.lsp4j.InsertTextFormat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ExpressionCompletionUtils {

    private static final Logger LOGGER = Logger.getLogger(ExpressionCompletionUtils.class.getName());
    private static List<CompletionItem> functionCompletions;

    static {
        try {
            functionCompletions = new ArrayList<>();
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
                LOGGER.log(Level.SEVERE, "Failed to load functions.json");
            }

            String jsonContent = new BufferedReader(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8)
            ).lines().collect(Collectors.joining("\n"));
            JsonArray jsonArray = Utils.getJsonArray(jsonContent);
            for (JsonElement element : jsonArray) {
                JsonObject jsonObject = element.getAsJsonObject();
                String label = jsonObject.get("label").getAsString();
                String insertText = jsonObject.get("insertText").getAsString();
                String detail = jsonObject.get("details").getAsString();
                int order = jsonObject.get("category").getAsInt();
                functionCompletions.add(
                        createCompletionItem(label, insertText, detail, CompletionItemKind.Function, order, true));
            }
        }
    }

    public static void addRootLevelCompletions(ICompletionRequest request, ICompletionResponse response,
                                               String filterText) {

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

        items.addAll(Collections.unmodifiableCollection(functionCompletions));
    }

    public static void addOperatorCompletions(ICompletionRequest request, ICompletionResponse response) {

        addCompletionItem(request, response, "+", "Addition", CompletionItemKind.Operator, 0, false);
        addCompletionItem(request, response, "-", "Subtraction", CompletionItemKind.Operator, 0, false);
        addCompletionItem(request, response, "*", "Multiplication", CompletionItemKind.Operator, 0, false);
        addCompletionItem(request, response, "/", "Division", CompletionItemKind.Operator, 0, false);
        addCompletionItem(request, response, "? ${1} : ${2}", "Ternary operator", CompletionItemKind.Operator, 0, true);
    }

    public static CompletionItem addCompletionItem(ICompletionRequest request, ICompletionResponse response,
                                                   String completion, String detail, CompletionItemKind kind, int order,
                                                   boolean isSnippet) {

        return addCompletionItem(request, response, completion, completion, detail, kind, order, isSnippet);
    }

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

    public static void addAttributeSecondLevelCompletions(ICompletionRequest request, ICompletionResponse response,
                                                          String filterText) {

        ExpressionConstants.ATTRIBUTES_SECOND_LEVEL.forEach((value) -> {
            if (value.startsWith(filterText)) {
                addCompletionItem(request, response, value, value, "Attribute", CompletionItemKind.Keyword, 0, false);
            }
        });
    }

    private ExpressionCompletionUtils() {

    }
}
