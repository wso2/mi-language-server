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

package org.eclipse.lemminx.customservice.synapse.mediator.schema.generate.visitor;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.eclipse.lemminx.customservice.synapse.AbstractMediatorVisitor;
import org.eclipse.lemminx.customservice.synapse.mediator.tryout.pojo.MediatorTryoutInfo;
import org.eclipse.lemminx.customservice.synapse.mediator.tryout.pojo.Property;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.SyntaxTreeGenerator;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.NamedSequence;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.STNode;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.Mediator;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.misc.common.Sequence;
import org.eclipse.lemminx.customservice.synapse.utils.ConfigFinder;
import org.eclipse.lemminx.dom.DOMDocument;
import org.eclipse.lsp4j.Position;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Utils {

    private static final Logger LOGGER = Logger.getLogger(Utils.class.getName());

    public static void visitSequence(String projectPath, Sequence seq, MediatorTryoutInfo info, Position position) {

        visitSequence(projectPath, seq, info, position, false);
    }

    public static void visitSequence(String projectPath, Sequence seq, MediatorTryoutInfo info, Position position,
                                     boolean isSplitAndAggregate) {

        if (seq != null && needToVisit(seq, position)) {
            boolean isSplit = splitPayloadIfRequired(info, isSplitAndAggregate);
            List<Mediator> mediatorList = seq.getMediatorList();
            if (mediatorList != null) {
                visitMediators(projectPath, mediatorList, info, position);
            }
            if (isAggregateNeeded(isSplit, seq, position)) {
                aggregatePayload(info);
            }
        }
    }

    private static boolean needToVisit(STNode node, Position position) {

        int line = node.getRange().getStartTagRange().getStart().getLine();
        int column = node.getRange().getStartTagRange().getStart().getCharacter();
        if (line > position.getLine() || (line == position.getLine() && column > position.getCharacter())) {
            return false;
        }
        return true;
    }

    private static boolean splitPayloadIfRequired(MediatorTryoutInfo info, boolean isSplitAndAggregate) {

        if (isSplitAndAggregate) {
            JsonPrimitive input = info.getOutput().getPayload();
            if (input != null) {
                String payload = input.getAsString();
                JsonArray jsonArray = org.eclipse.lemminx.customservice.synapse.utils.Utils.getJsonArray(payload);
                if (jsonArray != null) {
                    JsonPrimitive payloadElement = new JsonPrimitive(jsonArray.get(0).toString());
                    info.setInputPayload(payloadElement);
                    info.setOutputPayload(payloadElement);
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean isSplitNeeded(boolean isSplit, Sequence sequence, Position position) {

        if (!isSplit) {
            return false;
        }
        return checkNodeInRange(sequence, position);
    }

    private static boolean isAggregateNeeded(boolean isSplit, Sequence sequence, Position position) {

        if (!isSplit) {
            return false;
        }
        return isOutOfSequence(sequence, position) || !checkNodeInRange(sequence, position);
    }

    private static void aggregatePayload(MediatorTryoutInfo info) {

        JsonPrimitive output = info.getOutput().getPayload();
        if (output != null) {
            String payload = output.getAsString();
            JsonElement outputElement = org.eclipse.lemminx.customservice.synapse.utils.Utils.getJsonElement(payload);
            JsonArray jsonArray = new JsonArray();
            jsonArray.add(outputElement);
            info.setOutputPayload(new JsonPrimitive(jsonArray.toString()));
        }
    }

    public static void visitMediators(String projectPath, List<Mediator> mediatorList, MediatorTryoutInfo info,
                                      Position position) {

        visitMediators(projectPath, mediatorList, info, position, true);
    }

    public static void visitMediators(String projectPath, List<Mediator> mediatorList, MediatorTryoutInfo info,
                                      Position position, boolean needRangeCheck) {

        if (mediatorList == null || mediatorList.isEmpty() || !needToVisit(mediatorList.get(0), position)) {
            return;
        }
        MediatorSchemaVisitor mediatorVisitor = new MediatorSchemaVisitor(projectPath, info, position);
        for (Mediator mediator : mediatorList) {
            visitMediator(mediator, mediatorVisitor);
            if (needRangeCheck && checkNodeInRange(mediator, position)) {
                break;
            }
            info.replaceInputWithOutput();
        }
    }

    public static void visitMediator(Mediator node, AbstractMediatorVisitor visitor) {

        String tag = node.getTag();
        tag = sanitizeTag(tag);

        String visitFn;
        visitFn = "visit" + tag.substring(0, 1).toUpperCase() + tag.substring(1);
        try {
            Method method = AbstractMediatorVisitor.class.getDeclaredMethod(visitFn, node.getClass());
            method.setAccessible(true);
            method.invoke(visitor, node);
        } catch (NoSuchMethodException e) {
            LOGGER.log(Level.SEVERE, "No visit method found for mediator: " + tag, e);
        } catch (InvocationTargetException e) {
            LOGGER.log(Level.SEVERE, "Error while invoking visit method for mediator: " + tag, e);
        } catch (IllegalAccessException e) {
            LOGGER.log(Level.SEVERE, "Error while accessing visit method for mediator: " + tag, e);
        }
    }

    private static String sanitizeTag(String tag) {

        String sanitizedTag = tag;
        if (tag.contains("-")) {
            String[] split = tag.split("-");
            sanitizedTag = split[0] + split[1].substring(0, 1).toUpperCase() + split[1].substring(1);
        } else if (tag.contains(":")) {
            String[] split = tag.split(":");
            sanitizedTag = split[1];
        } else if (tag.contains(".")) {
            sanitizedTag = "connector";
        }
        return sanitizedTag;

    }

    public static boolean checkNodeInRange(STNode node, Position position) {

        int line = position.getLine();
        int column = position.getCharacter();
        if (node == null) {
            return false;
        }
        int startLine = node.getRange().getStartTagRange().getStart().getLine();
        int startColumn = node.getRange().getStartTagRange().getStart().getCharacter();
        int endLine;
        int endColumn;
        if (node.getRange().getEndTagRange() == null) {
            endLine = node.getRange().getStartTagRange().getEnd().getLine();
            endColumn = node.getRange().getStartTagRange().getEnd().getCharacter();
        } else {
            endLine = node.getRange().getEndTagRange().getEnd().getLine();
            endColumn = node.getRange().getEndTagRange().getEnd().getCharacter();
        }
        if (line < startLine || (startLine < line && line < endLine)) {
            return true;
        } else if (startLine == line && endLine == line) {
            return (startColumn <= column && column < endColumn);
        } else if (startLine == line) {
            return startColumn <= column;
        } else if (endLine == line) {
            return column < endColumn;
        } else {
            return false;
        }
    }

    public static boolean isOutOfSequence(Sequence node, Position position) {

        int line = position.getLine();
        int column = position.getCharacter();
        if (node == null) {
            return false;
        }
        int startLine = node.getRange().getStartTagRange().getStart().getLine();
        int startColumn = node.getRange().getStartTagRange().getStart().getCharacter();
        int endLine;
        int endColumn;
        if (node.getRange().getEndTagRange() == null) {
            endLine = node.getRange().getStartTagRange().getEnd().getLine();
            endColumn = node.getRange().getStartTagRange().getEnd().getCharacter();
        } else {
            endLine = node.getRange().getEndTagRange().getEnd().getLine();
            endColumn = node.getRange().getEndTagRange().getEnd().getCharacter();
        }
        return startLine > line || (line == startLine && column < startColumn) || line > endLine ||
                (line == endLine && column > endColumn);
    }

    public static void convertToJsonObject(Property property, JsonObject jsonObject) {

        if (property.getProperties() != null) {
            for (org.eclipse.lemminx.customservice.synapse.mediator.tryout.pojo.Property prop :
                    property.getProperties()) {
                if (prop.getProperties() != null) {
                    JsonObject obj = new JsonObject();
                    convertToJsonObject(prop, obj);
                    jsonObject.add(prop.getKey(), obj);
                } else {
                    jsonObject.add(prop.getKey(), new JsonPrimitive(prop.getValue()));
                }
            }
        }
    }

    public static void visitNamedSequence(String projectPath, String key, MediatorTryoutInfo info, Position position) {

        try {
            String sequencePath = getSequencePath(key, projectPath);
            if (sequencePath != null) {
                DOMDocument domDocument =
                        org.eclipse.lemminx.customservice.synapse.utils.Utils.getDOMDocument(new File(sequencePath));
                NamedSequence sequence =
                        (NamedSequence) SyntaxTreeGenerator.buildTree(domDocument.getDocumentElement());
                if (sequence != null) {
                    visitMediators(projectPath, sequence.getMediatorList(), info, position, false);
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error while visiting named sequence: " + key, e);
        }
    }

    private static String getSequencePath(String key, String projectPath) {

        try {
            return ConfigFinder.findEsbComponentPath(key, "sequences", projectPath);
        } catch (IOException e) {
            return null;
        }
    }

    private Utils() {

    }
}
