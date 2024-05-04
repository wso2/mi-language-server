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

package org.eclipse.lemminx.customservice.synapse.debugger.visitor.breakpoint;

import org.eclipse.lemminx.customservice.synapse.debugger.entity.Breakpoint;
import org.eclipse.lemminx.customservice.synapse.debugger.entity.debuginfo.IDebugInfo;
import org.eclipse.lemminx.customservice.synapse.debugger.entity.debuginfo.TemplateDebugInfo;
import org.eclipse.lemminx.customservice.synapse.debugger.visitor.Visitor;
import org.eclipse.lemminx.customservice.synapse.debugger.visitor.VisitorUtils;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.template.Template;

import java.util.HashMap;
import java.util.List;

public class TemplateVisitor implements Visitor {

    Template syntaxTree;
    List<Breakpoint> breakpoints;
    HashMap<Breakpoint, IDebugInfo> breakpointInfoMap;
    TemplateDebugInfo templateDebugInfo;

    public TemplateVisitor(Template syntaxTree, List<Breakpoint> breakpoints,
                           HashMap<Breakpoint, IDebugInfo> breakpointInfoMap) {

        this.syntaxTree = syntaxTree;
        this.breakpoints = breakpoints;
        this.breakpointInfoMap = breakpointInfoMap;
    }

    @Override
    public void startVisit() {

        while (breakpoints.size() > 0) {
            Breakpoint breakpoint = breakpoints.get(0);
            templateDebugInfo = new TemplateDebugInfo();
            traverseNode(syntaxTree, breakpoint);
        }
    }

    private void traverseNode(Template syntaxTree, Breakpoint breakpoint) {

        if (syntaxTree == null) {
            return;
        }
        if (VisitorUtils.checkNodeInRange(syntaxTree, breakpoint)) {
            templateDebugInfo.setTemplateKey(syntaxTree.getName());
            if (syntaxTree.getSequence() != null) {
                BreakpointMediatorVisitor mediatorVisitor = new BreakpointMediatorVisitor(breakpoints,
                        templateDebugInfo);
                VisitorUtils.visitMediators(syntaxTree.getSequence().getMediatorList(), mediatorVisitor,
                        breakpointInfoMap);
                if (!mediatorVisitor.isDone()) {
                    markAsInvalid(mediatorVisitor.breakpoint, "Invalid breakpoint in Template");
                }
            }
        } else {
            markAsInvalid(breakpoint, "Breakpoint is not in the range of the template");
        }
    }

    private void markAsInvalid(Breakpoint breakpoint, String error) {

        VisitorUtils.markAsInvalid(breakpoint, error, templateDebugInfo, breakpointInfoMap, breakpoints);
    }
}
