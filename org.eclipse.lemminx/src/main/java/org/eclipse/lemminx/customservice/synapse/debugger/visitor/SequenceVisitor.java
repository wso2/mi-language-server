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

import org.eclipse.lemminx.customservice.synapse.debugger.debuginfo.SequenceDebugInfo;
import org.eclipse.lemminx.customservice.synapse.debugger.entity.Breakpoint;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.NamedSequence;

public class SequenceVisitor implements Visitor {

    NamedSequence syntaxTree;
    Breakpoint breakpoint;
    SequenceDebugInfo debugInfo;

    public SequenceVisitor(NamedSequence syntaxTree, Breakpoint breakpoint, SequenceDebugInfo debugInfo) {

        this.syntaxTree = syntaxTree;
        this.breakpoint = breakpoint;
        this.debugInfo = debugInfo;
    }

    @Override
    public void startVisit() {

        traverseNode(syntaxTree);
    }

    private void traverseNode(NamedSequence syntaxTree) {

        if (syntaxTree == null) {
            return;
        }
        if (VisitorUtils.checkNodeInRange(syntaxTree, breakpoint)) {
            debugInfo.setSequenceKey(syntaxTree.getName());
            MediatorVisitor mediatorVisitor = new MediatorVisitor(breakpoint, debugInfo);
            VisitorUtils.visitMediators(syntaxTree.getMediatorList(), mediatorVisitor);
        } else {
            debugInfo.setError("Breakpoint is not in the range of the sequence");
            debugInfo.setValid(false);
        }
    }
}
