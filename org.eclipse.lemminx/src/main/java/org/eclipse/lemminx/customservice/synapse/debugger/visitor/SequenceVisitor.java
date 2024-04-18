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
import org.eclipse.lemminx.customservice.synapse.debugger.debuginfo.SequenceDebugInfo;
import org.eclipse.lemminx.customservice.synapse.debugger.entity.Breakpoint;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.NamedSequence;

import java.util.HashMap;
import java.util.List;

public class SequenceVisitor implements Visitor {

    NamedSequence syntaxTree;
    List<Breakpoint> breakpoints;
    HashMap<Breakpoint, IDebugInfo> breakpointInfoMap;
    SequenceDebugInfo sequenceDebugInfo;

    public SequenceVisitor(NamedSequence syntaxTree, List<Breakpoint> breakpoints,
                           HashMap<Breakpoint, IDebugInfo> breakpointInfoMap) {

        this.syntaxTree = syntaxTree;
        this.breakpoints = breakpoints;
        this.breakpointInfoMap = breakpointInfoMap;
    }

    @Override
    public void startVisit() {

        while (breakpoints.size() > 0) {
            Breakpoint breakpoint = breakpoints.get(0);
            sequenceDebugInfo = new SequenceDebugInfo();
            traverseNode(syntaxTree, breakpoint);
        }
    }

    private void traverseNode(NamedSequence syntaxTree, Breakpoint breakpoint) {

        if (syntaxTree == null) {
            return;
        }
        if (VisitorUtils.checkNodeInRange(syntaxTree, breakpoint)) {
            sequenceDebugInfo.setSequenceKey(syntaxTree.getName());
            MediatorVisitor mediatorVisitor = new MediatorVisitor(breakpoints, sequenceDebugInfo);
            VisitorUtils.visitMediators(syntaxTree.getMediatorList(), mediatorVisitor, breakpointInfoMap);
            if (!mediatorVisitor.isDone()) {
                breakpoints.remove(mediatorVisitor.breakpoint);
                sequenceDebugInfo.setValid(false);
                sequenceDebugInfo.setError("Invalid breakpoint in Sequence");
                breakpointInfoMap.put(mediatorVisitor.breakpoint, sequenceDebugInfo);
            }
        } else {
            markAsInvalid(breakpoint, "Breakpoint is not in the range of the sequence");
        }
    }

    private void markAsInvalid(Breakpoint breakpoint, String error) {

        VisitorUtils.markAsInvalid(breakpoint, error, sequenceDebugInfo, breakpointInfoMap, breakpoints);
    }
}
