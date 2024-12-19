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

package org.eclipse.lemminx.customservice.synapse.mediator.schema.generate;

import com.google.gson.JsonPrimitive;
import org.eclipse.lemminx.customservice.synapse.mediator.TryOutConstants;
import org.eclipse.lemminx.customservice.synapse.mediator.schema.generate.visitor.APIVisitor;
import org.eclipse.lemminx.customservice.synapse.mediator.TryOutUtils;
import org.eclipse.lemminx.customservice.synapse.mediator.tryout.pojo.MediatorInfo;
import org.eclipse.lemminx.customservice.synapse.mediator.tryout.pojo.MediatorTryoutRequest;
import org.eclipse.lemminx.customservice.synapse.mediator.tryout.pojo.MediatorTryoutInfo;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.SyntaxTreeGenerator;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.STNode;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.api.API;
import org.eclipse.lemminx.customservice.synapse.utils.Constant;
import org.eclipse.lemminx.customservice.synapse.utils.Utils;
import org.eclipse.lemminx.dom.DOMDocument;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class ServerLessTryoutHandler {

    private final String projectUri;

    public ServerLessTryoutHandler(String projectUri) {

        this.projectUri = projectUri;
    }

    public MediatorTryoutInfo handle(MediatorTryoutRequest request) {

        try {
            String filePath = request.getFile();
            DOMDocument domDocument = Utils.getDOMDocument(new File(filePath));
            STNode node = SyntaxTreeGenerator.buildTree(domDocument.getDocumentElement());
            MediatorTryoutInfo mediatorTryoutInfo = createInitialMediatorTryoutInfo(request);
            if (node != null) {
                visitNode(node, request, mediatorTryoutInfo);
            }
            return mediatorTryoutInfo;
        } catch (IOException e) {
            return new MediatorTryoutInfo(e.getMessage());
        }
    }

    private MediatorTryoutInfo createInitialMediatorTryoutInfo(MediatorTryoutRequest request) {

        MediatorInfo mediatorInfo = new MediatorInfo();
        JsonPrimitive payload = null;
        if (request.getInputPayload() != null) {
            payload = new JsonPrimitive(request.getInputPayload());
        }
        mediatorInfo.setPayload(payload);
        //TODO: Add params to the mediatorInfo
        return new MediatorTryoutInfo(mediatorInfo, mediatorInfo);
    }

    private void visitNode(STNode node, MediatorTryoutRequest request, MediatorTryoutInfo mediatorTryoutInfo) {

        String nodeType = node.getTag();
        if (Constant.API.equals(nodeType)) {
            APIVisitor apiVisitor = new APIVisitor();
            apiVisitor.visit((API) node, mediatorTryoutInfo, request);
        }
    }
}
