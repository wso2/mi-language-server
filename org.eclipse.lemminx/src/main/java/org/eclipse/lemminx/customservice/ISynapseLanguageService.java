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

import com.google.gson.JsonObject;
import org.eclipse.lemminx.customservice.synapse.api.generator.pojo.GenerateAPIResponse;
import org.eclipse.lemminx.customservice.synapse.api.generator.pojo.GenerateSwaggerParam;
import org.eclipse.lemminx.customservice.synapse.api.generator.pojo.GenerateSwaggerResponse;

import org.eclipse.lemminx.customservice.synapse.connectors.entity.ConnectionUIParam;
import org.eclipse.lemminx.customservice.synapse.connectors.entity.Connections;
import org.eclipse.lemminx.customservice.synapse.connectors.entity.ConnectorParam;
import org.eclipse.lemminx.customservice.synapse.connectors.entity.Connector;
import org.eclipse.lemminx.customservice.synapse.api.generator.pojo.GenerateAPIParam;
import org.eclipse.lemminx.customservice.synapse.connectors.entity.ConnectorResponse;
import org.eclipse.lemminx.customservice.synapse.connectors.entity.TestConnectionRequest;
import org.eclipse.lemminx.customservice.synapse.connectors.entity.TestConnectionResponse;
import org.eclipse.lemminx.customservice.synapse.connectors.generate.ConnectorGenerateRequest;
import org.eclipse.lemminx.customservice.synapse.connectors.generate.ConnectorGeneratorResponse;
import org.eclipse.lemminx.customservice.synapse.dataService.AddDriverRequestParams;
import org.eclipse.lemminx.customservice.synapse.dataService.CheckDBDriverRequestParams;
import org.eclipse.lemminx.customservice.synapse.dataService.QueryGenRequestParams;
import org.eclipse.lemminx.customservice.synapse.db.DBConnectionTestParams;
import org.eclipse.lemminx.customservice.synapse.db.DBConnectionTestResponse;
import org.eclipse.lemminx.customservice.synapse.debugger.entity.BreakpointInfoResponse;
import org.eclipse.lemminx.customservice.synapse.debugger.entity.BreakpointsRequest;
import org.eclipse.lemminx.customservice.synapse.debugger.entity.StepOverInfo;
import org.eclipse.lemminx.customservice.synapse.debugger.entity.ValidationResponse;
import org.eclipse.lemminx.customservice.synapse.dependency.tree.pojo.DependencyTree;
import org.eclipse.lemminx.customservice.synapse.dependency.tree.pojo.OverviewModel;
import org.eclipse.lemminx.customservice.synapse.directoryTree.DirectoryMapResponse;
import org.eclipse.lemminx.customservice.synapse.expression.pojo.ExpressionParam;
import org.eclipse.lemminx.customservice.synapse.expression.pojo.HelperPanelData;
import org.eclipse.lemminx.customservice.synapse.inbound.conector.InboundConnectorResponse;
import org.eclipse.lemminx.customservice.synapse.inbound.conector.InboundConnectorParam;
import org.eclipse.lemminx.customservice.synapse.mediator.tryout.pojo.MediatorTryoutRequest;
import org.eclipse.lemminx.customservice.synapse.mediatorService.pojo.MediatorRequest;
import org.eclipse.lemminx.customservice.synapse.mediatorService.pojo.SynapseConfigRequest;
import org.eclipse.lemminx.customservice.synapse.mediatorService.pojo.SynapseConfigResponse;
import org.eclipse.lemminx.customservice.synapse.mediatorService.pojo.UISchemaRequest;
import org.eclipse.lemminx.customservice.synapse.parser.ConfigDetails;
import org.eclipse.lemminx.customservice.synapse.parser.DependencyDetails;
import org.eclipse.lemminx.customservice.synapse.parser.OverviewPageDetailsResponse;
import org.eclipse.lemminx.customservice.synapse.parser.UpdateConfigRequest;
import org.eclipse.lemminx.customservice.synapse.parser.UpdateDependencyRequest;
import org.eclipse.lemminx.customservice.synapse.parser.UpdateResponse;
import org.eclipse.lemminx.customservice.synapse.parser.config.ConfigurableEntry;
import org.eclipse.lemminx.customservice.synapse.resourceFinder.ResourceUsagesRequest;
import org.eclipse.lemminx.customservice.synapse.resourceFinder.pojo.ResourceParam;
import org.eclipse.lemminx.customservice.synapse.resourceFinder.pojo.ResourceResponse;
import org.eclipse.lemminx.customservice.synapse.schemagen.util.SchemaGenFromContentRequest;
import org.eclipse.lemminx.customservice.synapse.schemagen.util.SchemaGenRequest;
import org.eclipse.lemminx.customservice.synapse.schemagen.util.SchemaGenResponse;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.SyntaxTreeResponse;
import org.eclipse.lemminx.customservice.synapse.mediator.tryout.pojo.MediatorTryoutInfo;
import org.eclipse.lemminx.services.extensions.completion.ICompletionResponse;
import org.eclipse.lsp4j.DefinitionParams;
import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.SignatureHelp;
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
    CompletableFuture<Either3<ConnectorResponse, Connector, Boolean>> availableConnectors(ConnectorParam param);

    @JsonRequest
    CompletableFuture<List<String>> getRegistryFiles(TextDocumentIdentifier param);

    @JsonRequest
    CompletableFuture<List<String>> getResourceFiles();

    @JsonRequest
    CompletableFuture<List<ConfigurableEntry>> getConfigurableEntries();

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
    public CompletableFuture<List<String>> getResourceUsages(ResourceUsagesRequest resourceUsagesRequest);

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
    CompletableFuture<JsonObject> getConnectionUISchema(ConnectionUIParam param);

    @JsonRequest
    CompletableFuture<DependencyTree> dependencyTree(TextDocumentIdentifier param);

    @JsonRequest
    CompletableFuture<OverviewModel> getOverviewModel();

    @JsonRequest
    CompletableFuture<Boolean> checkDBDriver(CheckDBDriverRequestParams requestParams);

    @JsonRequest
    CompletableFuture<Boolean> addDBDriver(AddDriverRequestParams requestParams);

    @JsonRequest
    CompletableFuture<String> generateQueries(QueryGenRequestParams requestParams);

    @JsonRequest
    CompletableFuture<Map<String, List<Boolean>>> fetchTables(QueryGenRequestParams requestParams);

    @JsonRequest
    CompletableFuture<DirectoryMapResponse> getProjectExplorerModel(WorkspaceFolder param);

    @JsonRequest
    CompletableFuture<JsonObject> getMediators(MediatorRequest mediatorRequest);

    @JsonRequest
    CompletableFuture<JsonObject> getMediatorUISchema(UISchemaRequest uiSchemaRequest);

    @JsonRequest
    CompletableFuture<SynapseConfigResponse> generateSynapseConfig(SynapseConfigRequest synapseConfigRequest);

    @JsonRequest
    CompletableFuture<JsonObject> getMediatorUISchemaWithValues(MediatorRequest mediatorRequest);

    @JsonRequest
    CompletableFuture<MediatorTryoutInfo> tryOutMediator(MediatorTryoutRequest request);

    @JsonRequest
    CompletableFuture<MediatorTryoutInfo> mediatorInputOutputSchema(MediatorTryoutRequest request);

    @JsonRequest
    CompletableFuture<OverviewPageDetailsResponse> getOverviewPageDetails();

    @JsonRequest
    CompletableFuture<UpdateResponse> updateDependency(UpdateDependencyRequest request);

    @JsonRequest
    CompletableFuture<UpdateResponse> updateConfigFile(UpdateConfigRequest request);

    @JsonRequest
    CompletableFuture<String> updateConnectorDependencies();

    @JsonRequest
    CompletableFuture<TestConnectionResponse> testConnectorConnection(TestConnectionRequest request);

    @JsonRequest
    CompletableFuture<ICompletionResponse> expressionCompletion(ExpressionParam param);

    @JsonRequest
    CompletableFuture<SignatureHelp> signatureHelp(ExpressionParam params);

    @JsonRequest
    CompletableFuture<HelperPanelData> expressionHelperData(ExpressionParam param);

    @JsonRequest
    CompletableFuture<ConnectorGeneratorResponse> generateConnector(ConnectorGenerateRequest schemaGenRequest);
}
