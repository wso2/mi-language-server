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

import org.eclipse.lemminx.customservice.synapse.debugger.debuginfo.ProxyDebugInfo;
import org.eclipse.lemminx.customservice.synapse.debugger.entity.Breakpoint;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.misc.common.Sequence;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.proxy.Proxy;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.proxy.ProxyTarget;

public class ProxyVisitor implements Visitor {

    Proxy syntaxTree;
    Breakpoint breakpoint;
    ProxyDebugInfo proxyDebugInfo;

    public ProxyVisitor(Proxy syntaxTree, Breakpoint breakpoint, ProxyDebugInfo proxyDebugInfo) {

        this.syntaxTree = syntaxTree;
        this.breakpoint = breakpoint;
        this.proxyDebugInfo = proxyDebugInfo;
    }

    @Override
    public void startVisit() {

        traverseNode(syntaxTree);
    }

    private void traverseNode(Proxy node) {

        if (node == null) {
            return;
        }
        if (VisitorUtils.checkNodeInRange(node, breakpoint)) {
            proxyDebugInfo.setProxyKey(node.getName());
            visitTargetSequence(node.getTarget());
        } else {
            proxyDebugInfo.setError("Breakpoint is not in the range of the proxy");
            proxyDebugInfo.setValid(false);
        }
    }

    private void visitTargetSequence(ProxyTarget target) {

        if (target == null) {
            return;
        }
        if (VisitorUtils.checkNodeInRange(target.getInSequence(), breakpoint)) {
            visitMediationSequence(target.getInSequence());
        } else if (VisitorUtils.checkNodeInRange(target.getOutSequence(), breakpoint)) {
            visitMediationSequence(target.getOutSequence());
        } else if (VisitorUtils.checkNodeInRange(target.getFaultSequence(), breakpoint)) {
            visitMediationSequence(target.getFaultSequence());
        } else {
            proxyDebugInfo.setError("Breakpoint is not in the range of the proxy target sequence");
            proxyDebugInfo.setValid(false);
        }
    }

    private void visitMediationSequence(Sequence sequence) {

        proxyDebugInfo.setSequenceType("proxy_" + sequence.getTag().substring(0, sequence.getTag().length() - 4).toLowerCase());
        MediatorVisitor mediatorVisitor = new MediatorVisitor(breakpoint, proxyDebugInfo);
        VisitorUtils.visitMediators(sequence.getMediatorList(), mediatorVisitor);
    }
}
