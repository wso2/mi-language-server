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
import org.eclipse.lemminx.customservice.synapse.mediator.TryOutUtils;
import org.eclipse.lemminx.customservice.synapse.mediator.schema.generate.visitor.SchemaVisitor;
import org.eclipse.lemminx.customservice.synapse.mediator.schema.generate.visitor.SchemaVisitorFactory;
import org.eclipse.lemminx.customservice.synapse.mediator.tryout.pojo.MediatorInfo;
import org.eclipse.lemminx.customservice.synapse.mediator.tryout.pojo.MediatorTryoutInfo;
import org.eclipse.lemminx.customservice.synapse.mediator.tryout.pojo.MediatorTryoutRequest;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.SyntaxTreeGenerator;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.STNode;
import org.eclipse.lemminx.customservice.synapse.utils.Utils;
import org.eclipse.lemminx.dom.DOMDocument;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class ServerLessTryoutHandler {

    Path TEMP_FOLDER = Path.of(System.getProperty("user.home"), ".wso2-mi", "expression-temp");
    private static final String TEMP_FILE_NAME = "temp.xml";
    private final String projectUri;

    public ServerLessTryoutHandler(String projectUri) {

        this.projectUri = projectUri;
    }

    public MediatorTryoutInfo handle(MediatorTryoutRequest request) {

        try {
            String filePath = request.getFile();
            if (request.getEdits() != null) {
                String documentUri = request.getFile();
                Utils.copyFile(documentUri, TEMP_FOLDER.toString(), TEMP_FILE_NAME);
                filePath = TEMP_FOLDER.resolve(TEMP_FILE_NAME).toString();
                TryOutUtils.doEdits(request.getEdits(), Path.of(filePath));
                request = new MediatorTryoutRequest(filePath, request.getLine(), request.getColumn() + 1,
                        request.getInputPayload(), null);
            }
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
        return new MediatorTryoutInfo(mediatorInfo, mediatorInfo.deepCopy());
    }

    private void visitNode(STNode node, MediatorTryoutRequest request, MediatorTryoutInfo mediatorTryoutInfo) {

        SchemaVisitor visitor = SchemaVisitorFactory.getSchemaVisitor(node, projectUri);
        if (visitor != null) {
            visitor.visit(node, mediatorTryoutInfo, request);
        }
    }
}
