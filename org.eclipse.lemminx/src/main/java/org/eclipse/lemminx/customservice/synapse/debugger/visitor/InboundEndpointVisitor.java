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

import org.eclipse.lemminx.customservice.synapse.debugger.debuginfo.InboundDebugInfo;
import org.eclipse.lemminx.customservice.synapse.debugger.entity.Breakpoint;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.inbound.InboundEndpoint;

public class InboundEndpointVisitor implements Visitor {

    InboundEndpoint syntaxTree;
    Breakpoint breakpoint;
    InboundDebugInfo inboundDebugInfo;

    public InboundEndpointVisitor(InboundEndpoint syntaxTree, Breakpoint breakpoint,
                                  InboundDebugInfo inboundDebugInfo) {

        this.syntaxTree = syntaxTree;
        this.breakpoint = breakpoint;
        this.inboundDebugInfo = inboundDebugInfo;
    }

    @Override
    public void startVisit() {

        traverseNode(syntaxTree);
    }

    private void traverseNode(InboundEndpoint syntaxTree) {

        if (syntaxTree == null) {
            return;
        }

        inboundDebugInfo.setValid(false);
        inboundDebugInfo.setError("Breakpoint is not supported in inbound endpoint. Please add the breakpoint " +
                "inside the respective sequence");
    }
}
