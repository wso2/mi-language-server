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

import org.eclipse.lemminx.customservice.synapse.debugger.debuginfo.ApiDebugInfo;
import org.eclipse.lemminx.customservice.synapse.debugger.entity.Breakpoint;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.api.API;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.api.APIResource;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.misc.common.Sequence;

public class ApiVisitor implements Visitor {

    API syntaxTree;
    Breakpoint breakpoint;
    ApiDebugInfo apiDebugInfo;

    public ApiVisitor(API syntaxTree, Breakpoint breakpoint, ApiDebugInfo apiDebugInfo) {

        this.syntaxTree = syntaxTree;
        this.breakpoint = breakpoint;
        this.apiDebugInfo = apiDebugInfo;
    }

    @Override
    public void startVisit() {

        traverseNode(syntaxTree);
    }

    private void traverseNode(API node) {

        if (node == null) {
            return;
        }
        if (VisitorUtils.checkNodeInRange(node, breakpoint)) {
            apiDebugInfo.setApiKey(node.getContext());
            APIResource[] resources = node.getResource();
            for (APIResource resource : resources) {
                if (VisitorUtils.checkNodeInRange(resource, breakpoint)) {
                    apiDebugInfo.setMethod(resource.getMethods()[0]);
                    apiDebugInfo.setUriTemplate(resource.getUriTemplate());
                    apiDebugInfo.setUrlMapping(resource.getUrlMapping());
                    visitResource(resource);
                    return;
                }
            }
        } else {
            apiDebugInfo.setValid(false);
            apiDebugInfo.setError("Breakpoint is not in the range of the API");
        }
    }

    private void visitResource(APIResource resource) {

        if (VisitorUtils.checkNodeInRange(resource.getInSequence(), breakpoint)) {
            visitMediationSequence(resource.getInSequence());
        } else if (VisitorUtils.checkNodeInRange(resource.getOutSequence(), breakpoint)) {
            visitMediationSequence(resource.getOutSequence());
        } else if (VisitorUtils.checkNodeInRange(resource.getFaultSequence(), breakpoint)) {
            visitMediationSequence(resource.getFaultSequence());
        }
    }

    private void visitMediationSequence(Sequence sequence) {

        apiDebugInfo.setSequenceType("api_" + sequence.getTag().substring(0, sequence.getTag().length() - 5).toLowerCase());
        MediatorVisitor mediatorVisitor = new MediatorVisitor(breakpoint, apiDebugInfo);
        VisitorUtils.visitMediators(sequence.getMediatorList(), mediatorVisitor);

    }
}
