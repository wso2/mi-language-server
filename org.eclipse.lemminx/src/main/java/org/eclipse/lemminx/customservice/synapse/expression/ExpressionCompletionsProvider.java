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

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.eclipse.lemminx.commons.BadLocationException;
import org.eclipse.lemminx.customservice.synapse.expression.pojo.ExpressionCompletionContext;
import org.eclipse.lemminx.customservice.synapse.expression.pojo.ExpressionCompletionParam;
import org.eclipse.lemminx.customservice.synapse.expression.pojo.ExpressionCompletionRequest;
import org.eclipse.lemminx.customservice.synapse.expression.pojo.ExpressionCompletionResponse;
import org.eclipse.lemminx.customservice.synapse.expression.pojo.ExpressionCompletionType;
import org.eclipse.lemminx.customservice.synapse.mediator.schema.generate.ServerLessTryoutHandler;
import org.eclipse.lemminx.customservice.synapse.mediator.tryout.pojo.MediatorInfo;
import org.eclipse.lemminx.customservice.synapse.mediator.tryout.pojo.MediatorTryoutInfo;
import org.eclipse.lemminx.customservice.synapse.mediator.tryout.pojo.MediatorTryoutRequest;
import org.eclipse.lemminx.customservice.synapse.mediator.tryout.pojo.Properties;
import org.eclipse.lemminx.customservice.synapse.mediator.tryout.pojo.Property;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.MediatorFactoryFinder;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.InvalidMediator;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.Mediator;
import org.eclipse.lemminx.customservice.synapse.utils.Utils;
import org.eclipse.lemminx.dom.DOMDocument;
import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.services.extensions.completion.ICompletionRequest;
import org.eclipse.lemminx.services.extensions.completion.ICompletionResponse;
import org.eclipse.lsp4j.CompletionItemKind;
import org.eclipse.lsp4j.Position;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExpressionCompletionsProvider {

    private static final Logger LOGGER = Logger.getLogger(ExpressionCompletionsProvider.class.getName());
    private static final String EXPRESSION_PREFIX = "${";
    private static final List<String> startingTokens = List.of("var", "attributes", "headers", "payload");

    public static ICompletionResponse getCompletions(ExpressionCompletionParam param) {

        try {
            DOMDocument document = Utils.getDOMDocument(new File(param.getDocumentUri()));
            String expression = EXPRESSION_PREFIX + param.getExpression();
            return getCompletions(document, param.getPosition(), expression, param.getOffset() + 2);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error while getting the DOM document", e);
        }
        return null;
    }

    public static ICompletionResponse getCompletions(DOMDocument document, Position position, String valuePrefix,
                                                     int cursorOffset) {

        try {
            Position mediatorPosition = getMediatorPosition(document, position);
            if (mediatorPosition == null) {
                return null;
            }
            ICompletionRequest request = new ExpressionCompletionRequest(document, mediatorPosition);
            ICompletionResponse response = new ExpressionCompletionResponse();
            doComplete(valuePrefix, request, response, cursorOffset, mediatorPosition != position);
            return response;
        } catch (BadLocationException e) {
            LOGGER.log(Level.SEVERE, "Error while getting the mediator position", e);
        }
        return null;
    }

    private static Position getMediatorPosition(DOMDocument document, Position position) throws BadLocationException {

        int offset = document.offsetAt(position);
        DOMNode node = document.findNodeAt(offset);
        Mediator mediator = MediatorFactoryFinder.getInstance().getMediator(node);
        if (mediator != null && !(mediator instanceof InvalidMediator)) {
            return position;
        }
        if (offset > node.getStart() && offset < node.getEnd()) {
            List<DOMNode> children = node.getChildren();
            DOMNode currentNode = null;
            for (DOMNode child : children) {
                if (offset <= child.getEnd()) {
                    break;
                }
                currentNode = child;
            }
            if (currentNode != null) {
                return document.positionAt(currentNode.getStart());
            }
        }
        return null;
    }

    public static ICompletionResponse doComplete(String valuePrefix, ICompletionRequest request,
                                                 ICompletionResponse response, int offset, boolean isNewMediator)
            throws BadLocationException {

        fillAttributeValueWithExpression(valuePrefix, request, response, offset, isNewMediator);
        return response;
    }

    public static ICompletionResponse doComplete(String valuePrefix, ICompletionRequest request,
                                                 ICompletionResponse response, boolean isNewMediator) {

        try {
            fillAttributeValueWithExpression(valuePrefix, request, response, 0, isNewMediator);
        } catch (BadLocationException e) {
            LOGGER.log(Level.SEVERE, "Error while getting the completion items", e);
        }
        return response;
    }

    private static MediatorTryoutInfo getMediatorProperties(ICompletionRequest request) {

        Pattern pattern = Pattern.compile("file://(.+?)/src/main/wso2mi/.*");
        Matcher matcher = pattern.matcher(request.getXMLDocument().getDocumentURI());
        if (!matcher.matches()) {
            return null;
        }
        ServerLessTryoutHandler serverLessTryoutHandler = new ServerLessTryoutHandler(matcher.group(1));
        MediatorTryoutRequest
                propertyRequest = new MediatorTryoutRequest(request.getXMLDocument().getDocumentURI().substring(7),
                request.getPosition().getLine(), request.getPosition().getCharacter(), "", null);
        return serverLessTryoutHandler.handle(propertyRequest);
    }

    public static void fillAttributeValueWithExpression(String valuePrefix, ICompletionRequest request,
                                                        ICompletionResponse response, int offset, boolean isNewMediator)
            throws BadLocationException {

        if (offset > 0) {
            valuePrefix = valuePrefix.substring(0, offset);
        } else {
            Position startPosition = request.getReplaceRange().getStart();
            Position endPosition = request.getPosition();
            int startOffset = request.getXMLDocument().offsetAt(startPosition);
            int endOffset = request.getXMLDocument().offsetAt(endPosition);
            int offsetDiff = endOffset - startOffset;
            if (offsetDiff > 0) {
                valuePrefix = valuePrefix.substring(0, offsetDiff);
            }
        }
        Pattern pattern = Pattern.compile("\\$\\{([^}]*)}?$");
        Matcher matcher = pattern.matcher(valuePrefix);
        if (matcher.matches()) {
            String expression = matcher.group(1);
            if (expression.isEmpty()) {
                ExpressionCompletionUtils.addRootLevelCompletions(request, response, "");
            } else {
                ExpressionCompletionContext segment = parseExpression(expression, request);
                if (segment == null) {
                    return;
                }
                if (ExpressionCompletionType.ROOT_LEVEL.equals(segment.getType())) {
                    String filterText = segment.getSegment().size() == 0 ? "" :
                            segment.getSegment().get(segment.getSegment().size() - 1);
                    ExpressionCompletionUtils.addRootLevelCompletions(request, response, filterText);
                } else if (ExpressionCompletionType.OBJECT_TRAVERSAL.equals(segment.getType())) {
                    if (segment.getSegment().size() > 0) {
                        List<String> segments = segment.getSegment();
                        if (startingTokens.contains(segments.get(0))) {
                            MediatorTryoutInfo info = getMediatorProperties(request);
                            if (info != null) {
                                getCompletionItems(request, response, info, segment, isNewMediator);
                            }
                        }
                    }
                } else if (ExpressionCompletionType.OPERATOR.equals(segment.getType())) {
                    ExpressionCompletionUtils.addOperatorCompletions(request, response);
                }
            }
        }
    }

    private static ExpressionCompletionContext parseExpression(String expression, ICompletionRequest request) {

        ExpressionCompletionContext currentSegment = new ExpressionCompletionContext();
        StringBuilder currentSegmentValue = new StringBuilder();
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            if (c == '.' || c == '[' || c == ',') {
                currentSegment.addSegment(currentSegmentValue.toString());
                if (c != ',') {
                    currentSegment.setType(ExpressionCompletionType.OBJECT_TRAVERSAL);
                    currentSegment.setNeedNext(Boolean.TRUE);
                } else {
                    currentSegment = new ExpressionCompletionContext(currentSegment);
                    currentSegment.setType(ExpressionCompletionType.ROOT_LEVEL);
                }
                currentSegmentValue = new StringBuilder();
            } else if (c == '(' || ExpressionConstants.OPERATORS.contains(String.valueOf(c))) {
                currentSegment = new ExpressionCompletionContext(currentSegment);
                currentSegmentValue = new StringBuilder();
                currentSegmentValue.append(c);
                currentSegment.addSegment(currentSegmentValue.toString());

                currentSegment = new ExpressionCompletionContext(currentSegment);
                currentSegment.setType(ExpressionCompletionType.ROOT_LEVEL);
                currentSegmentValue = new StringBuilder();
            } else if (c == ' ') {
                if (currentSegment.getType().equals(ExpressionCompletionType.ROOT_LEVEL) ||
                        (currentSegment.getParent() != null && ExpressionConstants.OPERATORS.contains(
                                currentSegment.getParent().getSegment().get(0)))) {
                    currentSegment.addSegment(currentSegmentValue.toString());
                    currentSegment = new ExpressionCompletionContext(currentSegment);
                    currentSegment.setType(ExpressionCompletionType.ROOT_LEVEL);
                    currentSegmentValue = new StringBuilder();
                    continue;
                }
                currentSegment.setType(ExpressionCompletionType.OPERATOR);
                currentSegmentValue.append(c);
            } else {
                if (c == '\'' || c == '\"' || c == ']') {
                    continue;
                } else if (c == ')') {
                    currentSegment.setType(ExpressionCompletionType.OPERATOR);
                    continue;
                }
                currentSegmentValue.append(c);
                if (i == expression.length() - 1) {
                    currentSegment.addSegment(currentSegmentValue.toString());
                    currentSegment.setNeedNext(false);
                }
            }
        }
        return currentSegment;
    }

    private static void getCompletionItems(ICompletionRequest request, ICompletionResponse response,
                                           MediatorTryoutInfo info, ExpressionCompletionContext context,
                                           boolean isNewMediator) {

        List<String> expressionSegments = context.getSegment();
        MediatorInfo mediatorInfo = isNewMediator ? info.getOutput() : info.getInput();
        String firstSegment = expressionSegments.get(0);
        List<Property> properties = null;
        JsonPrimitive payload = null;
        List<String> itemValues;
        switch (firstSegment) {
            case "var":
                properties = mediatorInfo.getVariables();
                expressionSegments = expressionSegments.subList(1, expressionSegments.size());
                break;
            case "attributes":
                Properties attributes = mediatorInfo.getAttributes();
                if (expressionSegments.size() == 2 && !context.isNeedNext()) {
                    ExpressionCompletionUtils.addAttributeSecondLevelCompletions(request, response,
                            expressionSegments.get(1));
                    return;
                } else if (expressionSegments.size() > 1) {
                    switch (expressionSegments.get(1)) {
                        case "axis2":
                            properties = attributes.getAxis2();
                            break;
                        case "axis2Client":
                            properties = attributes.getAxis2Client();
                            break;
                        case "axis2Transport":
                            properties = attributes.getAxis2Transport();
                            break;
                        case "axis2Operation":
                            properties = attributes.getAxis2Operation();
                            break;
                        case "synapse":
                            properties = attributes.getSynapse();
                            break;
                        default:
                            return;
                    }
                    expressionSegments = expressionSegments.subList(2, expressionSegments.size());
                } else {
                    ExpressionCompletionUtils.addAttributeSecondLevelCompletions(request, response, "");
                    return;
                }
                break;
            case "headers":
                properties = mediatorInfo.getHeaders();
                expressionSegments = expressionSegments.subList(1, expressionSegments.size());
                break;
            case "payload":
                payload = mediatorInfo.getPayload();
                break;
        }
        if (properties != null) {
            itemValues = findItemValues(expressionSegments, properties, context.isNeedNext());
            if (itemValues != null) {
                for (String item : itemValues) {
                    ExpressionCompletionUtils.addCompletionItem(request, response, item, "Object",
                            CompletionItemKind.Value, 0, false);
                }
            }
        } else if (payload != null) {
            // TODO: Implement the logic to traverse the payload values
        }
    }

    private static List<String> findItemValues(List<String> expressionSegments, List<Property> properties,
                                               boolean needNext) {

        Gson gson = new Gson();
        Property property = null;
        List<String> items = new ArrayList<>();
        for (Property p : properties) {
            items.add(p.getKey());
        }
        if (expressionSegments.isEmpty()) {
            return items;
        } else if (expressionSegments.size() == 1 && !needNext) {
            return filterCompletionItems(items, expressionSegments.get(0));
        }
        for (Property p : properties) {
            if (p.getKey().equals(expressionSegments.get(0))) {
                property = p;
                break;
            }
        }
        if (property != null) {
            String value = property.getValue();
            if (value != null) {
                JsonObject jsonObject = gson.fromJson(value, JsonObject.class);
                JsonObject currentObject = jsonObject;
                if (expressionSegments.size() > 1) {
                    for (int i = 1; i < expressionSegments.size(); i++) {
                        String segment = expressionSegments.get(i);
                        if (i == expressionSegments.size() - 1 && !needNext) {
                            break;
                        }
                        if (segment.isEmpty()) {
                            break;
                        } else if (currentObject.has(segment)) {
                            if (currentObject.get(segment).isJsonObject()) {
                                currentObject = currentObject.getAsJsonObject(segment);
                                continue;
                            }
                        }
                        return Collections.emptyList();
                    }
                }
                if (currentObject.isJsonObject()) {
                    items = new ArrayList<>(currentObject.keySet());
                    String lastSegment =
                            expressionSegments.size() > 1 ? expressionSegments.get(expressionSegments.size() - 1) : "";
                    return filterCompletionItems(items, lastSegment);
                }
            }
        }
        return Collections.emptyList();
    }

    private static List<String> filterCompletionItems(List<String> items, String filter) {

        if (filter.isEmpty()) {
            return items;
        } else {
            List<String> filteredItems = new ArrayList<>();
            for (String item : items) {
                if (item.startsWith(filter)) {
                    filteredItems.add(item);
                }
            }
            return filteredItems;
        }
    }
}
