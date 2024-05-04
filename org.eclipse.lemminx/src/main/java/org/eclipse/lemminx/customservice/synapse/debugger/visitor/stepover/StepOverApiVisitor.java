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

package org.eclipse.lemminx.customservice.synapse.debugger.visitor.stepover;

import org.eclipse.lemminx.customservice.synapse.debugger.entity.Breakpoint;
import org.eclipse.lemminx.customservice.synapse.debugger.entity.StepOverInfo;
import org.eclipse.lemminx.customservice.synapse.debugger.visitor.Visitor;
import org.eclipse.lemminx.customservice.synapse.debugger.visitor.VisitorUtils;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.api.API;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.api.APIResource;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.misc.common.Sequence;

public class StepOverApiVisitor implements Visitor {

    API syntaxTree;
    Breakpoint breakpoint;
    StepOverInfo stepOverInfo;

    public StepOverApiVisitor(API syntaxTree, Breakpoint breakpoint, StepOverInfo stepOverInfo) {

        this.syntaxTree = syntaxTree;
        this.breakpoint = breakpoint;
        this.stepOverInfo = stepOverInfo;
    }

    @Override
    public void startVisit() {

        traverseNode(syntaxTree, breakpoint);
    }

    private void traverseNode(API node, Breakpoint breakpoint) {

        if (node == null) {
            return;
        }
        if (VisitorUtils.checkNodeInRange(node, breakpoint)) {
            APIResource[] resources = node.getResource();
            for (APIResource resource : resources) {
                if (VisitorUtils.checkNodeInRange(resource, breakpoint)) {
                    visitResource(resource, breakpoint);
                    return;
                }
            }
        }
    }

    private void visitResource(APIResource resource, Breakpoint breakpoint) {

        if (VisitorUtils.checkNodeInRange(resource.getInSequence(), breakpoint)) {
            visitMediationSequence(resource.getInSequence());
        } else if (VisitorUtils.checkNodeInRange(resource.getOutSequence(), breakpoint)) {
            visitMediationSequence(resource.getOutSequence());
        } else if (VisitorUtils.checkNodeInRange(resource.getFaultSequence(), breakpoint)) {
            visitMediationSequence(resource.getFaultSequence());
        }
    }

    private void visitMediationSequence(Sequence sequence) {

        StepOverMediatorVisitor mediatorVisitor = new StepOverMediatorVisitor(breakpoint, stepOverInfo);
        VisitorUtils.visitMediators(sequence.getMediatorList(), mediatorVisitor);
    }
}
