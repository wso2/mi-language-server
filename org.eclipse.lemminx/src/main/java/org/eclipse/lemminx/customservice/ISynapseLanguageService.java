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

package org.eclipse.lemminx.customservice;

import org.eclipse.lemminx.customservice.synapse.resourceFinder.ResourceParam;
import org.eclipse.lemminx.customservice.synapse.resourceFinder.ResourceResponse;
import org.eclipse.lemminx.customservice.synapse.connectors.ConnectorHolder;
import org.eclipse.lemminx.customservice.synapse.directoryTree.DirectoryMapResponse;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.SyntaxTreeResponse;
import org.eclipse.lsp4j.DefinitionParams;
import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.TextDocumentIdentifier;
import org.eclipse.lsp4j.WorkspaceFolder;
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;
import org.eclipse.lsp4j.jsonrpc.services.JsonSegment;

import java.util.concurrent.CompletableFuture;

@JsonSegment("synapse")
public interface ISynapseLanguageService {

    @JsonRequest
    CompletableFuture<SyntaxTreeResponse> syntaxTree(TextDocumentIdentifier param);

    @JsonRequest
    CompletableFuture<PublishDiagnosticsParams> diagnostic(TextDocumentIdentifier param);

    @JsonRequest
    CompletableFuture<DirectoryMapResponse> directoryTree(WorkspaceFolder param);

    @JsonRequest
    CompletableFuture<Location> definition(DefinitionParams params);

    @JsonRequest
    CompletableFuture<ResourceResponse> availableResources(ResourceParam param);

    @JsonRequest
    CompletableFuture<ConnectorHolder> availableConnectors(TextDocumentIdentifier param);
}
