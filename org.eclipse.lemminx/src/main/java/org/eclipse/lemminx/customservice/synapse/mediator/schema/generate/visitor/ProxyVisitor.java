/*
 *   Copyright (c) 2025, WSO2 LLC. (http://www.wso2.com).
 *
 *   WSO2 LLC. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 */

package org.eclipse.lemminx.customservice.synapse.mediator.schema.generate.visitor;

import org.eclipse.lemminx.customservice.synapse.mediator.tryout.pojo.MediatorTryoutInfo;
import org.eclipse.lemminx.customservice.synapse.mediator.tryout.pojo.MediatorTryoutRequest;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.STNode;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.proxy.Proxy;
import org.eclipse.lsp4j.Position;

public class ProxyVisitor implements SchemaVisitor {

    private String projectPath;

    public ProxyVisitor(String projectPath) {

        this.projectPath = projectPath;
    }

    @Override
    public void visit(STNode node, MediatorTryoutInfo info, MediatorTryoutRequest request) {

        Proxy proxy = (Proxy) node;
        Position position = new Position(request.getLine(), request.getColumn());
        if (Utils.checkNodeInRange(proxy.getTarget().getInSequence(), position) ||
                Utils.checkNodeInRange(proxy.getTarget().getOutSequence(), position)) {
            Utils.visitSequence(projectPath, proxy.getTarget().getInSequence(), info, position);
            Utils.visitSequence(projectPath, proxy.getTarget().getOutSequence(), info, position);
        } else if (Utils.checkNodeInRange(proxy.getTarget().getFaultSequence(), position)) {
            Utils.visitSequence(projectPath, proxy.getTarget().getFaultSequence(), info, position);
        }
    }
}
