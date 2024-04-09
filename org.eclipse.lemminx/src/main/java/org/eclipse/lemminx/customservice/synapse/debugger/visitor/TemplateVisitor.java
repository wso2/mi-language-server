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

import org.eclipse.lemminx.customservice.synapse.debugger.BreakPoint;
import org.eclipse.lemminx.customservice.synapse.debugger.debuginfo.TemplateDebugInfo;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.template.Template;

public class TemplateVisitor implements Visitor {

    Template syntaxTree;
    BreakPoint breakpoint;
    TemplateDebugInfo templateDebugInfo;

    public TemplateVisitor(Template syntaxTree, BreakPoint breakpoint, TemplateDebugInfo debugInfo) {

        this.syntaxTree = syntaxTree;
        this.breakpoint = breakpoint;
        this.templateDebugInfo = debugInfo;
    }

    @Override
    public void startVisit() {

        traverseNode(syntaxTree);
    }

    private void traverseNode(Template syntaxTree) {

        if (syntaxTree == null) {
            return;
        }
        if (VisitorUtils.checkNodeInRange(syntaxTree, breakpoint)) {
            templateDebugInfo.setTemplateKey(syntaxTree.getName());
            if (syntaxTree.getSequence() != null) {
                MediatorVisitor mediatorVisitor = new MediatorVisitor(breakpoint, templateDebugInfo);
                VisitorUtils.visitMediators(syntaxTree.getSequence().getMediatorList(), mediatorVisitor);
            }
        } else {
            templateDebugInfo.setError("Breakpoint is not in the range of the template");
            templateDebugInfo.setValid(false);
        }
    }
}
