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

import org.eclipse.lemminx.customservice.synapse.debugger.visitor.AbstractMediatorVisitor;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.STNode;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.Mediator;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.misc.common.Sequence;
import org.eclipse.lemminx.customservice.synapse.mediator.tryout.pojo.MediatorTryoutInfo;
import org.eclipse.lsp4j.Position;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Utils {

    private static final Logger LOGGER = Logger.getLogger(Utils.class.getName());
    public static void visitSequence(Sequence seq, MediatorTryoutInfo info, Position position) {

        if (seq != null && checkNodeInRange(seq, position)) {
            List<Mediator> mediatorList = seq.getMediatorList();
            if (mediatorList != null) {
                visitMediators(mediatorList, info, position);
            }
        }
    }

    private static void visitMediators(List<Mediator> mediatorList, MediatorTryoutInfo info, Position position) {

        MediatorSchemaGenerateVisitor mediatorVisitor = new MediatorSchemaGenerateVisitor(info, position);
        for (Mediator mediator : mediatorList) {
            visitMediator(mediator, mediatorVisitor);
            if (checkNodeInRange(mediator, position)) {
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
        if (node.isSelfClosed()) {
            endLine = node.getRange().getStartTagRange().getEnd().getLine();
            endColumn = node.getRange().getStartTagRange().getEnd().getCharacter();
        } else {
            endLine = node.getRange().getEndTagRange().getEnd().getLine();
            endColumn = node.getRange().getEndTagRange().getEnd().getCharacter();
        }
        if (startLine < line && line < endLine) {
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

}
