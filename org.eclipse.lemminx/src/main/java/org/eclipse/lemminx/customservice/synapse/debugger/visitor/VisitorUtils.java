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

package org.eclipse.lemminx.customservice.synapse.debugger.visitor;

import org.eclipse.lemminx.customservice.synapse.debugger.debuginfo.IDebugInfo;
import org.eclipse.lemminx.customservice.synapse.debugger.entity.Breakpoint;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.STNode;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.Mediator;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VisitorUtils {

    private static final Logger LOGGER = Logger.getLogger(VisitorUtils.class.getName());

    /**
     * Check whether the breakpoint is within the range of the node.
     *
     * @param node       The node to check.
     * @param breakpoint The breakpoint to check.
     * @return True if the breakpoint is within the range of the node, false otherwise.
     */
    public static boolean checkNodeInRange(STNode node, Breakpoint breakpoint) {

        if (node == null) {
            return false;
        }
        int startLine = node.getRange().getStartTagRange().getStart().getLine();
        int endLine;
        if (node.isSelfClosed()) {
            endLine = node.getRange().getStartTagRange().getEnd().getLine();
        } else {
            endLine = node.getRange().getEndTagRange().getEnd().getLine();
        }
        if (startLine <= breakpoint.getLine() && breakpoint.getLine() <= endLine) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Check whether the breakpoint is in the start tag range of the mediator node.
     *
     * @param node       The node(mediator) to check.
     * @param breakpoint The breakpoint to check.
     * @return True if the breakpoint is within the start tag range of the node, false otherwise.
     */
    public static boolean checkValidBreakpoint(STNode node, Breakpoint breakpoint) {

        if (node == null) {
            return false;
        }
        int startLine = node.getRange().getStartTagRange().getStart().getLine();
        int endLine = node.getRange().getStartTagRange().getEnd().getLine();
        if (startLine <= breakpoint.getLine() && breakpoint.getLine() <= endLine) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Visit the mediators in the list and get the debug info.
     *
     * @param mediators The list of mediators to visit.
     * @param visitor   The visitor to visit the mediators.
     */
    public static void visitMediators(List<Mediator> mediators, BreakpointMediatorVisitor visitor) {

        visitMediators(mediators, visitor, new HashMap<>());
    }

    /**
     * Visit the mediators in the list and generate debug info for the current breakpoint.
     * If the visitor is done, then the breakpoint will be removed from the list and the debug info will be stored in
     * the map. Then the visitor will continue from the next breakpoint.
     *
     * @param mediators  The list of mediators to visit.
     * @param visitor    The visitor to visit the mediators.
     * @param debugInfos The map to store the debug info of the mediators.
     */
    public static void visitMediators(List<Mediator> mediators, BreakpointMediatorVisitor visitor, HashMap<Breakpoint,
            IDebugInfo> debugInfos) {

        if (mediators != null && mediators.size() > 0) {
            for (int i = 0; i < mediators.size(); i++) {

                visitMediator(mediators.get(i), visitor);
                if (visitor.isDone()) {
                    IDebugInfo debugInfo = null;
                    try {
                        debugInfo = visitor.debugInfo.clone();
                    } catch (CloneNotSupportedException e) {
                        LOGGER.log(Level.SEVERE, "Error while cloning debug info", e);
                    }
                    debugInfos.put(visitor.breakpoint, debugInfo);
                    if (visitor.breakpoints != null && visitor.breakpoints.size() > 0) {
                        visitor.nextBreakpoint();
                        i--;
                    }
                }
            }
        }
    }

    /**
     * Visit the mediators in the list and get the next step over breakpoints.
     *
     * @param mediators The list of mediators to visit.
     * @param visitor   The visitor to visit the mediators.
     */
    public static void visitMediators(List<Mediator> mediators, StepOverMediatorVisitor visitor) {

        if (mediators != null && mediators.size() > 0) {
            for (int i = 0; i < mediators.size(); i++) {
                visitMediator(mediators.get(i), visitor);
                if (visitor.isDone()) {
                    break;
                }
            }
        }
    }

    /**
     * Visit the mediator node.
     *
     * @param node    The mediator node to visit.
     * @param visitor The visitor to visit the mediator.
     */
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

    /**
     * Mark the breakpoint as invalid and store the error message in the debug info.
     *
     * @param breakpoint        The breakpoint to mark as invalid.
     * @param error             The error message to store in the debug info.
     * @param debugInfo         The debug info to store the error message.
     * @param breakpointInfoMap The map to store the debug info of the breakpoints.
     * @param breakpoints       The list of breakpoints.
     */
    public static void markAsInvalid(Breakpoint breakpoint, String error, IDebugInfo debugInfo, HashMap<Breakpoint,
            IDebugInfo> breakpointInfoMap, List<Breakpoint> breakpoints) {

        breakpoints.remove(breakpoint);
        debugInfo.setValid(false);
        debugInfo.setError(error);
        breakpointInfoMap.put(breakpoint, debugInfo);
    }
}
