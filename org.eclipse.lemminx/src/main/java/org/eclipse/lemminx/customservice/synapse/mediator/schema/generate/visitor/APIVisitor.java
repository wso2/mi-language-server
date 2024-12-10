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

package org.eclipse.lemminx.customservice.synapse.mediator.schema.generate.visitor;

import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.api.API;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.api.APIResource;
import org.eclipse.lemminx.customservice.synapse.mediator.tryout.pojo.MediatorTryoutRequest;
import org.eclipse.lemminx.customservice.synapse.mediator.tryout.pojo.MediatorTryoutInfo;
import org.eclipse.lsp4j.Position;

public class APIVisitor {

    public void visit(API api, MediatorTryoutInfo info, MediatorTryoutRequest request) {

        int line = request.getLine();
        int column = request.getColumn();
        Position position = new Position(line, column);
        if (Utils.checkNodeInRange(api, position)) {
            APIResource[] resources = api.getResource();
            if (resources != null) {
                for (APIResource resource : resources) {
                    if (Utils.checkNodeInRange(resource, position)) {
                        visitResource(resource, info, position);
                    }
                }
            }
        }
    }

    private void visitResource(APIResource resource, MediatorTryoutInfo info, Position position) {

        Utils.visitSequence(resource.getInSequence(), info, position);
        Utils.visitSequence(resource.getOutSequence(), info, position);
        Utils.visitSequence(resource.getFaultSequence(), info, position);
    }
}
