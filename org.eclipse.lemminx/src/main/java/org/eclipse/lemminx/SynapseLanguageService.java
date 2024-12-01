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

package org.eclipse.lemminx;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.eclipse.lemminx.customservice.ISynapseLanguageService;
import org.eclipse.lemminx.customservice.SynapseLanguageClientAPI;
import org.eclipse.lemminx.customservice.synapse.api.generator.pojo.GenerateAPIResponse;
import org.eclipse.lemminx.customservice.synapse.api.generator.pojo.GenerateSwaggerParam;
import org.eclipse.lemminx.customservice.synapse.api.generator.pojo.GenerateSwaggerResponse;
import org.eclipse.lemminx.customservice.synapse.configurable.ConfigurableEntry;
import org.eclipse.lemminx.customservice.synapse.configurable.ConfigurableEntryScanner;
import org.eclipse.lemminx.customservice.synapse.connectors.ConnectionHandler;
import org.eclipse.lemminx.customservice.synapse.connectors.NewProjectConnectorLoader;
import org.eclipse.lemminx.customservice.synapse.connectors.OldProjectConnectorLoader;
import org.eclipse.lemminx.customservice.synapse.connectors.entity.Connection;
import org.eclipse.lemminx.customservice.synapse.connectors.entity.ConnectionParameter;
import org.eclipse.lemminx.customservice.synapse.connectors.entity.ConnectionUIParam;
import org.eclipse.lemminx.customservice.synapse.connectors.entity.Connections;
import org.eclipse.lemminx.customservice.synapse.connectors.entity.ConnectorParam;
import org.eclipse.lemminx.customservice.synapse.connectors.ConnectionFinder;
import org.eclipse.lemminx.customservice.synapse.connectors.entity.Connector;
import org.eclipse.lemminx.customservice.synapse.dataService.DynamicClassLoader;
import org.eclipse.lemminx.customservice.synapse.dataService.QueryGenerator;
import org.eclipse.lemminx.customservice.synapse.dataService.CheckDBDriverRequestParams;
import org.eclipse.lemminx.customservice.synapse.dataService.AddDriverRequestParams;
import org.eclipse.lemminx.customservice.synapse.dataService.QueryGenRequestParams;
import org.eclipse.lemminx.customservice.synapse.db.DBConnectionTestParams;
import org.eclipse.lemminx.customservice.synapse.db.DBConnectionTestResponse;
import org.eclipse.lemminx.customservice.synapse.db.DBConnectionTester;
import org.eclipse.lemminx.customservice.synapse.debugger.entity.StepOverInfo;
import org.eclipse.lemminx.customservice.synapse.dependency.tree.OverviewModelGenerator;
import org.eclipse.lemminx.customservice.synapse.dependency.tree.pojo.Dependency;
import org.eclipse.lemminx.customservice.synapse.dependency.tree.pojo.OverviewModel;
import org.eclipse.lemminx.customservice.synapse.expression.ExpressionHelperProvider;
import org.eclipse.lemminx.customservice.synapse.expression.ExpressionSignatureProvider;
import org.eclipse.lemminx.customservice.synapse.expression.pojo.ExpressionParam;
import org.eclipse.lemminx.customservice.synapse.expression.ExpressionCompletionsProvider;
import org.eclipse.lemminx.customservice.synapse.expression.pojo.HelperPanelData;
import org.eclipse.lemminx.customservice.synapse.inbound.conector.InboundConnectorResponse;
import org.eclipse.lemminx.customservice.synapse.inbound.conector.InboundConnectorHolder;
import org.eclipse.lemminx.customservice.synapse.inbound.conector.InboundConnectorParam;
import org.eclipse.lemminx.customservice.synapse.dependency.tree.DependencyScanner;
import org.eclipse.lemminx.customservice.synapse.dependency.tree.pojo.DependencyTree;
import org.eclipse.lemminx.customservice.synapse.mediator.tryout.pojo.MediatorTryoutRequest;
import org.eclipse.lemminx.customservice.synapse.mediatorService.MediatorHandler;
import org.eclipse.lemminx.customservice.synapse.mediatorService.pojo.MediatorRequest;
import org.eclipse.lemminx.customservice.synapse.mediatorService.pojo.SynapseConfigRequest;
import org.eclipse.lemminx.customservice.synapse.mediatorService.pojo.SynapseConfigResponse;
import org.eclipse.lemminx.customservice.synapse.mediatorService.pojo.UISchemaRequest;
import org.eclipse.lemminx.customservice.synapse.mediator.schema.generate.ServerLessTryoutHandler;
import org.eclipse.lemminx.customservice.synapse.parser.ConfigDetails;
import org.eclipse.lemminx.customservice.synapse.parser.DependencyDetails;
import org.eclipse.lemminx.customservice.synapse.parser.OverviewPage;
import org.eclipse.lemminx.customservice.synapse.parser.OverviewPageDetailsResponse;
import org.eclipse.lemminx.customservice.synapse.parser.UpdateConfigRequest;
import org.eclipse.lemminx.customservice.synapse.parser.UpdateDependencyRequest;
import org.eclipse.lemminx.customservice.synapse.parser.UpdateResponse;
import org.eclipse.lemminx.customservice.synapse.parser.config.ConfigParser;
import org.eclipse.lemminx.customservice.synapse.parser.pom.PomParser;
import org.eclipse.lemminx.customservice.synapse.parser.ConnectorDownloadManager;
import org.eclipse.lemminx.customservice.synapse.resourceFinder.AbstractResourceFinder;
import org.eclipse.lemminx.customservice.synapse.resourceFinder.ArtifactFileScanner;
import org.eclipse.lemminx.customservice.synapse.resourceFinder.RegistryFileScanner;
import org.eclipse.lemminx.customservice.synapse.debugger.entity.BreakpointInfoResponse;
import org.eclipse.lemminx.customservice.synapse.debugger.entity.BreakpointsRequest;
import org.eclipse.lemminx.customservice.synapse.debugger.entity.BreakpointValidity;
import org.eclipse.lemminx.customservice.synapse.debugger.DebuggerHelper;
import org.eclipse.lemminx.customservice.synapse.debugger.entity.ValidationResponse;
import org.eclipse.lemminx.customservice.synapse.api.generator.pojo.GenerateAPIParam;
import org.eclipse.lemminx.customservice.synapse.api.generator.RestApiAdmin;
import org.eclipse.lemminx.customservice.synapse.resourceFinder.ResourceFileScanner;
import org.eclipse.lemminx.customservice.synapse.resourceFinder.ResourceFinderFactory;
import org.eclipse.lemminx.customservice.synapse.resourceFinder.ResourceUsageFinder;
import org.eclipse.lemminx.customservice.synapse.resourceFinder.ResourceUsagesRequest;
import org.eclipse.lemminx.customservice.synapse.resourceFinder.pojo.ResourceParam;
import org.eclipse.lemminx.customservice.synapse.resourceFinder.pojo.ResourceResponse;
import org.eclipse.lemminx.customservice.synapse.connectors.ConnectorHolder;
import org.eclipse.lemminx.customservice.synapse.connectors.AbstractConnectorLoader;
import org.eclipse.lemminx.customservice.synapse.connectors.SchemaGenerate;
import org.eclipse.lemminx.customservice.synapse.definition.SynapseDefinitionProvider;
import org.eclipse.lemminx.customservice.synapse.directoryTree.DirectoryMapResponse;
import org.eclipse.lemminx.customservice.synapse.directoryTree.DirectoryTreeBuilder;
import org.eclipse.lemminx.customservice.synapse.schemagen.util.FileType;
import org.eclipse.lemminx.customservice.synapse.schemagen.util.SchemaGenFromContentRequest;
import org.eclipse.lemminx.customservice.synapse.schemagen.util.SchemaGenRequest;
import org.eclipse.lemminx.customservice.synapse.schemagen.util.SchemaGenResponse;
import org.eclipse.lemminx.customservice.synapse.schemagen.util.SchemaGeneratorHelper;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.SyntaxTreeGenerator;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.SyntaxTreeResponse;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.MediatorFactoryFinder;
import org.eclipse.lemminx.customservice.synapse.utils.Constant;
import org.eclipse.lemminx.customservice.synapse.mediator.tryout.pojo.MediatorTryoutInfo;
import org.eclipse.lemminx.customservice.synapse.mediator.tryout.TryOutHandler;
import org.eclipse.lemminx.customservice.synapse.utils.Utils;
import org.eclipse.lemminx.extensions.contentmodel.settings.XMLValidationSettings;
import org.eclipse.lemminx.services.extensions.completion.ICompletionResponse;
import org.eclipse.lemminx.settings.SharedSettings;
import org.eclipse.lsp4j.DefinitionParams;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.SignatureHelp;
import org.eclipse.lsp4j.SignatureHelpParams;
import org.eclipse.lsp4j.TextDocumentIdentifier;
import org.eclipse.lsp4j.WorkspaceFolder;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.jsonrpc.messages.Either3;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SynapseLanguageService implements ISynapseLanguageService {
    private static final Logger log = Logger.getLogger(SynapseLanguageService.class.getName());
    private XMLTextDocumentService xmlTextDocumentService;
    private XMLLanguageServer xmlLanguageServer;
    private SynapseLanguageClientAPI languageClient;
    private AbstractConnectorLoader connectorLoader;
    private static String extensionPath;
    private String projectUri;
    private boolean isLegacyProject;
    private String projectServerVersion;
    private MediatorHandler mediatorHandler;
    private final ConnectorHolder connectorHolder;
    private AbstractResourceFinder resourceFinder;
    private final InboundConnectorHolder inboundConnectorHolder;
    private final ConnectionHandler connectionHandler;
    private Path synapseXSDPath;
    private TryOutHandler tryOutHandler;
    private ServerLessTryoutHandler serverLessTryoutHandler;
    private String miServerPath;
    private ExpressionHelperProvider expressionHelperProvider;

    public SynapseLanguageService(XMLTextDocumentService xmlTextDocumentService, XMLLanguageServer xmlLanguageServer) {

        this.xmlTextDocumentService = xmlTextDocumentService;
        this.xmlLanguageServer = xmlLanguageServer;
        this.connectorHolder = new ConnectorHolder();
        this.inboundConnectorHolder = new InboundConnectorHolder();
        mediatorHandler = new MediatorHandler();
        connectionHandler = new ConnectionHandler();
    }

    public void init(String projectUri, Object settings, SynapseLanguageClientAPI languageClient) {

        this.languageClient = languageClient;
        if (settings != null) {
            extensionPath = ((JsonObject) settings).get("extensionPath").getAsString();
            // TODO: remove this after fixing the issue with the miServerPath
            miServerPath = ((JsonObject) settings).get("miServerPath").getAsString();
        }
        if (projectUri != null) {
            this.projectUri = projectUri;
            this.isLegacyProject = Utils.isLegacyProject(projectUri);
            this.projectServerVersion = Utils.getServerVersion(projectUri, Constant.DEFAULT_MI_VERSION);
            initializeConnectorLoader();
            mediatorHandler.init(projectServerVersion, connectorHolder);
            connectionHandler.init(connectorHolder);
            MediatorFactoryFinder.getInstance().setConnectorHolder(connectorHolder);
            try {
                DynamicClassLoader.updateClassLoader(Path.of(projectUri, "deployment", "libs").toFile());
                this.tryOutHandler = new TryOutHandler(projectUri);
                tryOutHandler.initAsync(miServerPath);
                this.serverLessTryoutHandler = new ServerLessTryoutHandler(projectUri);
            } catch (Exception e) {
                log.log(Level.SEVERE, "Error while updating class loader for DB drivers.", e);
            }
            this.expressionHelperProvider = new ExpressionHelperProvider(projectUri);
        } else {
            log.log(Level.SEVERE, "Project path is null. Language server initialization failed.");
        }
        resourceFinder = ResourceFinderFactory.getResourceFinder(isLegacyProject);
        inboundConnectorHolder.init(projectUri);
    }

    private void initializeConnectorLoader() {
        if (isLegacyProject) {
            connectorLoader = new OldProjectConnectorLoader(languageClient, connectorHolder);
        } else {
            connectorLoader = new NewProjectConnectorLoader(languageClient, connectorHolder);
        }
        connectorLoader.init(projectUri);
        updateConnectors();
    }

    @Override
    public CompletableFuture<SyntaxTreeResponse> syntaxTree(TextDocumentIdentifier param) {

        return xmlTextDocumentService.computeDOMAsync(param, (xmlDocument, cancelChecker) -> {
            SyntaxTreeGenerator generator = new SyntaxTreeGenerator();
            generator.setProjectPath(projectUri);
            return generator.getSyntaxTree(xmlDocument);
        });
    }

    @Override
    public CompletableFuture<DBConnectionTestResponse> testDBConnection(DBConnectionTestParams dbConnectionTestParams) {

        DBConnectionTester dbConnectionTester = new DBConnectionTester();
        boolean connectionStatus = dbConnectionTester.testDBConnection(dbConnectionTestParams.dbType,
                dbConnectionTestParams.username, dbConnectionTestParams.password,
                dbConnectionTestParams.host, dbConnectionTestParams.port, dbConnectionTestParams.dbName,
                dbConnectionTestParams.url, dbConnectionTestParams.className);
        DBConnectionTestResponse response = new DBConnectionTestResponse(connectionStatus);
        return CompletableFuture.supplyAsync(() -> response);
    }

    @Override
    public CompletableFuture<PublishDiagnosticsParams> diagnostic(TextDocumentIdentifier param) {

        return xmlTextDocumentService.computeDOMAsync(param, (xmlDocument, cancelChecker) -> {
            cancelChecker.checkCanceled();
            SharedSettings sharedSettings = xmlTextDocumentService.getSharedSettings();
            XMLValidationSettings validationSettingsForUri = sharedSettings != null
                    ? sharedSettings.getValidationSettings().getValidationSettings(xmlDocument.getDocumentURI())
                    : null;
            List<Diagnostic> diagnostics = xmlLanguageServer.getXMLLanguageService().doDiagnostics(xmlDocument,
                    validationSettingsForUri,
                    Collections.emptyMap(), cancelChecker);
            return new PublishDiagnosticsParams(xmlDocument.getDocumentURI(), diagnostics);
        });
    }

    @Override
    public CompletableFuture<DirectoryMapResponse> directoryTree(WorkspaceFolder param) {

        DirectoryMapResponse response = DirectoryTreeBuilder.buildDirectoryTree(param);
        return CompletableFuture.supplyAsync(() -> response);
    }

    @Override
    public CompletableFuture<Location> definition(
            DefinitionParams params) {

        return xmlTextDocumentService.computeDOMAsync(params.getTextDocument(), (xmlDocument, cancelChecker) -> {
            Location location = SynapseDefinitionProvider.definition(xmlDocument, params.getPosition(), projectUri,
                    cancelChecker);
            return location;
        });
    }

    @Override
    public CompletableFuture<ResourceResponse> availableResources(ResourceParam param) {

        ResourceResponse response = resourceFinder.getAvailableResources(projectUri, param.resourceType);
        return CompletableFuture.supplyAsync(() -> response);
    }

    @Override
    public CompletableFuture<Either3<ConnectorHolder, Connector, Boolean>> availableConnectors(ConnectorParam param) {

        return CompletableFuture.supplyAsync(() -> {
            if (param.connectorName != null && !param.connectorName.isEmpty()) {
                Connector connector = connectorHolder.getConnector(param.connectorName);
                if (connector == null) {
                    return Either3.forThird(Boolean.FALSE);
                }
                return Either3.forSecond(connector);
            }
            return Either3.forFirst(connectorHolder);
        });
    }

    public void updateConnectors() {

        connectorLoader.loadConnector();
        if (mediatorHandler.isInitialized()) {
            mediatorHandler.reloadMediatorList(projectServerVersion);
        }
        //Generate xsd schema for the available connectors and write it to the schema file.
        String connectorPath = synapseXSDPath.resolve("mediators").resolve("connectors.xsd").toString();
        SchemaGenerate.generate(connectorHolder, connectorPath);
    }

    @Override
    public CompletableFuture<List<String>> getRegistryFiles(TextDocumentIdentifier param) {

        List<String> registryFiles = RegistryFileScanner.scanRegistryFiles(projectUri);
        return CompletableFuture.supplyAsync(() -> registryFiles);
    }

    @Override
    public CompletableFuture<List<String>> getResourceFiles() {

        List<String> resourceFiles = ResourceFileScanner.scanResourceFiles(projectUri);
        return CompletableFuture.supplyAsync(() -> resourceFiles);
    }

    @Override
    public CompletableFuture<List<ConfigurableEntry>> getConfigurableEntries() {

        try {
            List<ConfigurableEntry> configurableEntries = ConfigurableEntryScanner.scanConfigurableEntries(projectUri);
            return CompletableFuture.supplyAsync(() -> configurableEntries);
        } catch (IOException e) {
            log.log(Level.SEVERE, "Error while scanning configurable entries.", e);
            return CompletableFuture.supplyAsync(() -> new ArrayList<>());
        }
    }

    @Override
    public CompletableFuture<List<String>> getResourceUsages(ResourceUsagesRequest resourceUsagesRequest) {

        List<String> resourceUsagesProjectIdentifiers =
                ResourceUsageFinder.findResourceUsagesProjectIdentifiers(projectUri,
                        resourceUsagesRequest.resourceFilePath, connectorHolder, isLegacyProject);
        return CompletableFuture.supplyAsync(() -> resourceUsagesProjectIdentifiers);
    }

    @Override
    public CompletableFuture<List<String>> getArtifactFiles(TextDocumentIdentifier param) {

        List<String> artifactFiles = ArtifactFileScanner.scanArtifactFiles(projectUri);
        return CompletableFuture.supplyAsync(() -> artifactFiles);
    }

    @Override
    public CompletableFuture<BreakpointInfoResponse> getBreakpointInfo(BreakpointsRequest breakPointRequest) {

        DebuggerHelper debuggerHelper = new DebuggerHelper(breakPointRequest.filePath);
        List<JsonElement> debugInfoJsonList = debuggerHelper.generateDebugInfoJson(breakPointRequest.breakpoints);
        BreakpointInfoResponse breakpointInfoResponse = new BreakpointInfoResponse(debugInfoJsonList);
        return CompletableFuture.supplyAsync(() -> breakpointInfoResponse);
    }

    @Override
    public CompletableFuture<ValidationResponse> validateBreakpoints(BreakpointsRequest breakPointRequest) {

        DebuggerHelper debuggerHelper = new DebuggerHelper(breakPointRequest.filePath);
        List<BreakpointValidity> validityList = debuggerHelper.validateBreakpoints(breakPointRequest.breakpoints);
        ValidationResponse validationResponse = new ValidationResponse(validityList);
        return CompletableFuture.supplyAsync(() -> validationResponse);
    }

    @Override
    public CompletableFuture<StepOverInfo> stepOverBreakpoint(BreakpointsRequest breakPointRequest) {

        DebuggerHelper debuggerHelper = new DebuggerHelper(breakPointRequest.filePath);
        StepOverInfo stepOverInfo = debuggerHelper.getStepOverBreakpoints(breakPointRequest.breakpoint);
        return CompletableFuture.supplyAsync(() -> stepOverInfo);
    }

    @Override
    public CompletableFuture<Either<Connections, Map<String, Connections>>> connectorConnections(ConnectorParam param) {

        Either<Connections, Map<String, Connections>> connections =
                ConnectionFinder.findConnections(projectUri, param.connectorName, connectorHolder, isLegacyProject);
        return CompletableFuture.supplyAsync(() -> connections);
    }

    @Override
    public CompletableFuture<SchemaGenResponse> generateSchema(SchemaGenRequest schemaGenRequest) {

        SchemaGeneratorHelper schemaGenerate = new SchemaGeneratorHelper();
        FileType fileType = FileType.valueOf(schemaGenRequest.type);
        String schema = schemaGenerate.getSchemaContent(fileType, schemaGenRequest.filePath, schemaGenRequest.delimiter);
        SchemaGenResponse schemaGenResponse = new SchemaGenResponse(schema);
        return CompletableFuture.supplyAsync(() -> schemaGenResponse);
    }

    @Override
    public CompletableFuture<SchemaGenResponse> generateSchemaFromContent(SchemaGenFromContentRequest schemaGenRequest) {

        SchemaGeneratorHelper schemaGenerate = new SchemaGeneratorHelper();
        FileType fileType = FileType.valueOf(schemaGenRequest.type);
        String schema = schemaGenerate.getSchemaFromContent(fileType,
                schemaGenRequest.fileContent, schemaGenRequest.delimiter);
        SchemaGenResponse schemaGenResponse = new SchemaGenResponse(schema);
        return CompletableFuture.supplyAsync(() -> schemaGenResponse);
    }

    @Override
    public CompletableFuture<GenerateAPIResponse> generateAPI(GenerateAPIParam param) {

        RestApiAdmin generator = new RestApiAdmin();
        GenerateAPIResponse apiXml = generator.createAPI(param);
        return CompletableFuture.supplyAsync(() -> apiXml);
    }

    @Override
    public CompletableFuture<GenerateSwaggerResponse> swaggerFromAPI(GenerateSwaggerParam param) {

        RestApiAdmin generator = new RestApiAdmin();
        return CompletableFuture.supplyAsync(() -> generator.generateSwaggerFromAPI(param));
    }

    @Override
    public CompletableFuture<Boolean> saveInboundConnectorSchema(InboundConnectorParam param) {

        return CompletableFuture.supplyAsync(() -> inboundConnectorHolder.saveInboundConnector(param.connectorName,
                param.uiSchema));
    }

    @Override
    public CompletableFuture<InboundConnectorResponse> getInboundConnectorSchema(InboundConnectorParam param) {

        return CompletableFuture.supplyAsync(() -> {
            if (param.connectorName != null) {
                return inboundConnectorHolder.getInboundConnectorSchema(param.connectorName);
            } else {
                return inboundConnectorHolder.getInboundConnectorSchema(new File(param.documentPath));
            }
        });
    }

    @Override
    public CompletableFuture<JsonObject> getConnectionUISchema(ConnectionUIParam param) {

        return CompletableFuture.supplyAsync(() -> connectionHandler.getConnectionUISchema(param));
    }

    @Override
    public CompletableFuture<DependencyTree> dependencyTree(TextDocumentIdentifier param) {

        DependencyScanner dependencyScanner = new DependencyScanner(projectUri);
        DependencyTree dependencyTree = dependencyScanner.analyzeArtifact(param.getUri());
        return CompletableFuture.supplyAsync(() -> dependencyTree);
    }

    @Override
    public CompletableFuture<OverviewModel> getOverviewModel() {
        OverviewModel overviewModel = OverviewModelGenerator.getOverviewModel(projectUri);
        return CompletableFuture.supplyAsync(() -> overviewModel);
    }

    @Override
    public CompletableFuture<Boolean> checkDBDriver(CheckDBDriverRequestParams requestParams) {
        boolean isDriverAvailable = QueryGenerator.isDriverAvailableInClassPath(requestParams.className);
        return CompletableFuture.supplyAsync(() -> isDriverAvailable);
    }

    @Override
    public CompletableFuture<Boolean> addDBDriver(AddDriverRequestParams requestParams) {
        boolean isSuccess = QueryGenerator.addDriverToClassPath(requestParams.driverPath, requestParams.className);
        return CompletableFuture.supplyAsync(() -> isSuccess);
    }

    @Override
    public CompletableFuture<String> generateQueries(QueryGenRequestParams requestParams) {
        String xmlContent = QueryGenerator.generateDSSQueries(requestParams);
        return CompletableFuture.supplyAsync(() -> xmlContent);
    }

    @Override
    public CompletableFuture<Map<String, List<Boolean>>> fetchTables(QueryGenRequestParams requestParams) {
        Map<String, List<Boolean>> tableList = QueryGenerator.getTableList(requestParams);
        return CompletableFuture.supplyAsync(() -> tableList);
    }

    @Override
    public CompletableFuture<DirectoryMapResponse> getProjectExplorerModel(WorkspaceFolder param) {

        DirectoryMapResponse response = DirectoryTreeBuilder.getProjectExplorerModel(param);
        return CompletableFuture.supplyAsync(() -> response);
    }

    @Override
    public CompletableFuture<JsonObject> getMediators(MediatorRequest mediatorRequest) {

        return CompletableFuture.supplyAsync(() -> mediatorHandler.getSupportedMediators(mediatorRequest.documentIdentifier, mediatorRequest.position));
    }

    @Override
    public CompletableFuture<JsonObject> getMediatorUISchema(UISchemaRequest uiSchemaRequest) {

        return CompletableFuture.supplyAsync(() -> mediatorHandler.getUiSchema(uiSchemaRequest.mediatorType));
    }

    @Override
    public CompletableFuture<SynapseConfigResponse> generateSynapseConfig(SynapseConfigRequest synapseConfigRequest) {

        return CompletableFuture.supplyAsync(
                () -> mediatorHandler.generateSynapseConfig(synapseConfigRequest.documentUri,
                        synapseConfigRequest.range, synapseConfigRequest.mediatorType, synapseConfigRequest.values,
                        synapseConfigRequest.dirtyFields));
    }

    @Override
    public CompletableFuture<JsonObject> getMediatorUISchemaWithValues(MediatorRequest mediatorRequest) {

        return CompletableFuture.supplyAsync(
                () -> mediatorHandler.getSchemaWithValues(mediatorRequest.documentIdentifier,
                        mediatorRequest.position));
    }

    @Override
    public CompletableFuture<MediatorTryoutInfo> tryOutMediator(MediatorTryoutRequest request) {

        return CompletableFuture.supplyAsync(() -> {
            try {
                return tryOutHandler.handle(request);
            } catch (InterruptedException e) {
                log.log(Level.SEVERE, "Error while trying out the mediator.", e);
                return null;
            }
        });
    }

    @Override
    public CompletableFuture<MediatorTryoutInfo> mediatorInputOutputSchema(MediatorTryoutRequest request) {

        return CompletableFuture.supplyAsync(() -> serverLessTryoutHandler.handle(request));
    }

    public CompletableFuture<OverviewPageDetailsResponse> getOverviewPageDetails() {
        OverviewPageDetailsResponse response = OverviewPage.getDetails(projectUri);
        return CompletableFuture.supplyAsync(() -> response);
    }

    @Override
    public CompletableFuture<ICompletionResponse> expressionCompletion(ExpressionParam param) {

        return CompletableFuture.supplyAsync(() -> ExpressionCompletionsProvider.getCompletions(param));
    }

    @Override
    public CompletableFuture<SignatureHelp> signatureHelp(ExpressionParam params) {

        return CompletableFuture.supplyAsync(() -> ExpressionSignatureProvider.getFunctionSignatures(params));
    }

    @Override
    public CompletableFuture<UpdateResponse> updateDependency(UpdateDependencyRequest request) {
        UpdateResponse response = PomParser.updateDependency(projectUri, request);
        return CompletableFuture.supplyAsync(() -> response);
    }

    @Override
    public CompletableFuture<HelperPanelData> expressionHelperData(ExpressionParam param) {

        return CompletableFuture.supplyAsync(() -> expressionHelperProvider.getExpressionHelperData(param));
    }

    @Override
    public CompletableFuture<UpdateResponse> updateConfigFile(UpdateConfigRequest request) {
        UpdateResponse response = ConfigParser.updateConfigFile(projectUri, request);
        return CompletableFuture.supplyAsync(() -> response);
    }

    @Override
    public CompletableFuture<String> updateConnectorDependencies() {
        String statusMessage = ConnectorDownloadManager.updateConnectors(projectUri, connectorLoader);
        return CompletableFuture.supplyAsync(() -> statusMessage);
    }

    public String getProjectUri() {
        return projectUri;
    }

    public ConnectorHolder getConnectorHolder() {

        return connectorHolder;
    }

    public static String getExtensionPath() {

        return extensionPath;
    }

    public Path getSynapseXSDPath() {

        return synapseXSDPath;
    }

    public void setSynapseXSDPath(Path synapseXSDPath) {

        this.synapseXSDPath = synapseXSDPath;
    }
}
