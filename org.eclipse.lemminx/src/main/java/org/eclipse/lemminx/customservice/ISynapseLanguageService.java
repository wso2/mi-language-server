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

import org.eclipse.lemminx.customservice.synapse.api.generator.pojo.GenerateAPIResponse;
import org.eclipse.lemminx.customservice.synapse.api.generator.pojo.GenerateSwaggerParam;
import org.eclipse.lemminx.customservice.synapse.api.generator.pojo.GenerateSwaggerResponse;
import org.eclipse.lemminx.customservice.synapse.connectors.entity.Connections;
import org.eclipse.lemminx.customservice.synapse.connectors.entity.ConnectorParam;
import org.eclipse.lemminx.customservice.synapse.connectors.entity.Connector;
import org.eclipse.lemminx.customservice.synapse.api.generator.pojo.GenerateAPIParam;
import org.eclipse.lemminx.customservice.synapse.connectors.ConnectorHolder;
import org.eclipse.lemminx.customservice.synapse.dataService.AddDriverRequestParams;
import org.eclipse.lemminx.customservice.synapse.dataService.CheckDBDriverRequestParams;
import org.eclipse.lemminx.customservice.synapse.dataService.QueryGenRequestParams;
import org.eclipse.lemminx.customservice.synapse.db.DBConnectionTestParams;
import org.eclipse.lemminx.customservice.synapse.db.DBConnectionTestResponse;
import org.eclipse.lemminx.customservice.synapse.debugger.entity.BreakpointInfoResponse;
import org.eclipse.lemminx.customservice.synapse.debugger.entity.BreakpointsRequest;
import org.eclipse.lemminx.customservice.synapse.debugger.entity.StepOverInfo;
import org.eclipse.lemminx.customservice.synapse.debugger.entity.ValidationResponse;
import org.eclipse.lemminx.customservice.synapse.directoryTree.DirectoryMapResponse;
import org.eclipse.lemminx.customservice.synapse.inbound.conector.InboundConnectorResponse;
import org.eclipse.lemminx.customservice.synapse.inbound.conector.InboundConnectorParam;
import org.eclipse.lemminx.customservice.synapse.resourceFinder.pojo.ResourceParam;
import org.eclipse.lemminx.customservice.synapse.resourceFinder.pojo.ResourceResponse;
import org.eclipse.lemminx.customservice.synapse.schemagen.util.SchemaGenFromContentRequest;
import org.eclipse.lemminx.customservice.synapse.schemagen.util.SchemaGenRequest;
import org.eclipse.lemminx.customservice.synapse.schemagen.util.SchemaGenResponse;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.SyntaxTreeResponse;
import org.eclipse.lsp4j.DefinitionParams;
import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.TextDocumentIdentifier;
import org.eclipse.lsp4j.WorkspaceFolder;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.jsonrpc.messages.Either3;
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;
import org.eclipse.lsp4j.jsonrpc.services.JsonSegment;

import java.util.List;
import java.util.Map;
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
    CompletableFuture<Either3<ConnectorHolder, Connector, Boolean>> availableConnectors(ConnectorParam param);

    @JsonRequest
    CompletableFuture<List<String>> getRegistryFiles(TextDocumentIdentifier param);

    @JsonRequest
    CompletableFuture<List<String>> getArtifactFiles(TextDocumentIdentifier param);

    @JsonRequest
    CompletableFuture<BreakpointInfoResponse> getBreakpointInfo(BreakpointsRequest breakPointRequest);

    @JsonRequest
    CompletableFuture<ValidationResponse> validateBreakpoints(BreakpointsRequest breakPointRequest);

    @JsonRequest
    CompletableFuture<StepOverInfo> stepOverBreakpoint(BreakpointsRequest breakPointRequest);

    @JsonRequest
    CompletableFuture<Either<Connections, Map<String, Connections>>> connectorConnections(ConnectorParam param);

    @JsonRequest
    CompletableFuture<SchemaGenResponse> generateSchema(SchemaGenRequest schemaGenRequest);

    @JsonRequest
    CompletableFuture<SchemaGenResponse> generateSchemaFromContent(SchemaGenFromContentRequest schemaGenRequest);

    @JsonRequest
    CompletableFuture<GenerateAPIResponse> generateAPI(GenerateAPIParam param);

    @JsonRequest
    CompletableFuture<GenerateSwaggerResponse> swaggerFromAPI(GenerateSwaggerParam param);

    @JsonRequest
    CompletableFuture<DBConnectionTestResponse> testDBConnection(DBConnectionTestParams dbConnectionTestParams);

    @JsonRequest
    CompletableFuture<Boolean> saveInboundConnectorSchema(InboundConnectorParam param);

    @JsonRequest
    CompletableFuture<InboundConnectorResponse> getInboundConnectorSchema(InboundConnectorParam param);

    @JsonRequest
    CompletableFuture<Boolean> checkDBDriver(CheckDBDriverRequestParams requestParams);

    @JsonRequest
    CompletableFuture<Boolean> addDBDriver(AddDriverRequestParams requestParams);

    @JsonRequest
    CompletableFuture<String> generateQueries(QueryGenRequestParams requestParams);

    @JsonRequest
    CompletableFuture<Map<String, List<Boolean>>> fetchTables(QueryGenRequestParams requestParams);


}
