/*
 * Copyright (c) 2025, WSO2 LLC. (http://www.wso2.com).
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     WSO2 LLC - support for WSO2 Micro Integrator Configuration
 */

package org.eclipse.lemminx;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.eclipse.lemminx.customservice.ISynapseLanguageService;
import org.eclipse.lemminx.customservice.SynapseLanguageClientAPI;
import org.eclipse.lemminx.customservice.synapse.CodeDiagnosticRequest;
import org.eclipse.lemminx.customservice.synapse.api.generator.pojo.IsEqualSwaggersParam;
import org.eclipse.lemminx.customservice.synapse.api.generator.pojo.GenerateAPIResponse;
import org.eclipse.lemminx.customservice.synapse.api.generator.pojo.GenerateSwaggerParam;
import org.eclipse.lemminx.customservice.synapse.api.generator.pojo.GenerateSwaggerResponse;
import org.eclipse.lemminx.customservice.synapse.connectors.ConnectionHandler;
import org.eclipse.lemminx.customservice.synapse.connectors.NewProjectConnectorLoader;
import org.eclipse.lemminx.customservice.synapse.connectors.OldProjectConnectorLoader;
import org.eclipse.lemminx.customservice.synapse.connectors.entity.ConnectionUIParam;
import org.eclipse.lemminx.customservice.synapse.connectors.entity.Connections;
import org.eclipse.lemminx.customservice.synapse.connectors.entity.ConnectorParam;
import org.eclipse.lemminx.customservice.synapse.connectors.ConnectionFinder;
import org.eclipse.lemminx.customservice.synapse.connectors.entity.Connector;
import org.eclipse.lemminx.customservice.synapse.connectors.entity.ConnectorResponse;
import org.eclipse.lemminx.customservice.synapse.connectors.generate.ConnectorGenerateRequest;
import org.eclipse.lemminx.customservice.synapse.connectors.generate.ConnectorGeneratorResponse;
import org.eclipse.lemminx.customservice.synapse.connectors.entity.TestConnectionRequest;
import org.eclipse.lemminx.customservice.synapse.connectors.entity.TestConnectionResponse;
import org.eclipse.lemminx.customservice.synapse.dataService.DynamicClassLoader;
import org.eclipse.lemminx.customservice.synapse.dataService.QueryGenerator;
import org.eclipse.lemminx.customservice.synapse.dataService.CheckDBDriverRequestParams;
import org.eclipse.lemminx.customservice.synapse.dataService.CheckDBDriverResponseParams;
import org.eclipse.lemminx.customservice.synapse.dataService.ModifyDriverRequestParams;
import org.eclipse.lemminx.customservice.synapse.dataService.QueryGenRequestParams;
import org.eclipse.lemminx.customservice.synapse.db.DBConnectionTestParams;
import org.eclipse.lemminx.customservice.synapse.db.DBConnectionTestResponse;
import org.eclipse.lemminx.customservice.synapse.db.DBConnectionTester;
import org.eclipse.lemminx.customservice.synapse.debugger.entity.StepOverInfo;
import org.eclipse.lemminx.customservice.synapse.dependency.tree.OverviewModelGenerator;
import org.eclipse.lemminx.customservice.synapse.dependency.tree.pojo.OverviewModel;
import org.eclipse.lemminx.customservice.synapse.expression.pojo.ExpressionError;
import org.eclipse.lemminx.customservice.synapse.expression.ExpressionHelperProvider;
import org.eclipse.lemminx.customservice.synapse.expression.ExpressionSignatureProvider;
import org.eclipse.lemminx.customservice.synapse.expression.ExpressionValidator;
import org.eclipse.lemminx.customservice.synapse.expression.pojo.ExpressionParam;
import org.eclipse.lemminx.customservice.synapse.expression.ExpressionCompletionsProvider;
import org.eclipse.lemminx.customservice.synapse.expression.pojo.ExpressionValidationResponse;
import org.eclipse.lemminx.customservice.synapse.expression.pojo.HelperPanelData;
import org.eclipse.lemminx.customservice.synapse.inbound.conector.InboundConnectorResponse;
import org.eclipse.lemminx.customservice.synapse.inbound.conector.InboundConnectorHolder;
import org.eclipse.lemminx.customservice.synapse.inbound.conector.InboundConnectorParam;
import org.eclipse.lemminx.customservice.synapse.dependency.tree.DependencyScanner;
import org.eclipse.lemminx.customservice.synapse.dependency.tree.pojo.DependencyTree;
import org.eclipse.lemminx.customservice.synapse.mediator.tryout.TryOutManager;
import org.eclipse.lemminx.customservice.synapse.InvalidConfigurationException;
import org.eclipse.lemminx.customservice.synapse.mediator.tryout.pojo.MediatorTryoutRequest;
import org.eclipse.lemminx.customservice.synapse.mediatorService.MediatorHandler;
import org.eclipse.lemminx.customservice.synapse.mediatorService.pojo.MediatorRequest;
import org.eclipse.lemminx.customservice.synapse.mediatorService.pojo.SynapseConfigRequest;
import org.eclipse.lemminx.customservice.synapse.mediatorService.pojo.SynapseConfigResponse;
import org.eclipse.lemminx.customservice.synapse.mediatorService.pojo.UISchemaRequest;
import org.eclipse.lemminx.customservice.synapse.parser.ConfigDetails;
import org.eclipse.lemminx.customservice.synapse.parser.Constants;
import org.eclipse.lemminx.customservice.synapse.parser.DependencyStatusResponse;
import org.eclipse.lemminx.customservice.synapse.parser.DeployPluginDetails;
import org.eclipse.lemminx.customservice.synapse.parser.DependencyDownloadManager;
import org.eclipse.lemminx.customservice.synapse.parser.OverviewPage;
import org.eclipse.lemminx.customservice.synapse.parser.OverviewPageDetailsResponse;
import org.eclipse.lemminx.customservice.synapse.parser.UpdateConfigRequest;
import org.eclipse.lemminx.customservice.synapse.parser.UpdateDependencyRequest;
import org.eclipse.lemminx.customservice.synapse.parser.UpdatePropertyRequest;
import org.eclipse.lemminx.customservice.synapse.parser.UpdateResponse;
import org.eclipse.lemminx.customservice.synapse.parser.config.ConfigParser;
import org.eclipse.lemminx.customservice.synapse.parser.config.ConfigurableEntry;
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
import org.eclipse.lemminx.customservice.synapse.resourceFinder.pojo.LoadDependentResourcesResponse;
import org.eclipse.lemminx.customservice.synapse.resourceFinder.pojo.ResourceResponse;
import org.eclipse.lemminx.customservice.synapse.connectors.ConnectorHolder;
import org.eclipse.lemminx.customservice.synapse.connectors.AbstractConnectorLoader;
import org.eclipse.lemminx.customservice.synapse.connectors.SchemaGenerate;
import org.eclipse.lemminx.customservice.synapse.definition.SynapseDefinitionProvider;
import org.eclipse.lemminx.customservice.synapse.directoryTree.DirectoryMapResponse;
import org.eclipse.lemminx.customservice.synapse.directoryTree.DirectoryTreeBuilder;
import org.eclipse.lemminx.customservice.synapse.driver.DriverDownloadRequest;
import org.eclipse.lemminx.customservice.synapse.driver.DriverLoader;
import org.eclipse.lemminx.customservice.synapse.dynamic.db.DynamicField;
import org.eclipse.lemminx.customservice.synapse.dynamic.db.DynamicFieldsHandler;
import org.eclipse.lemminx.customservice.synapse.dynamic.db.GetDynamicFieldsRequest;
import org.eclipse.lemminx.customservice.synapse.schemagen.util.FileType;
import org.eclipse.lemminx.customservice.synapse.schemagen.util.SchemaGenFromContentRequest;
import org.eclipse.lemminx.customservice.synapse.schemagen.util.SchemaGenRequest;
import org.eclipse.lemminx.customservice.synapse.schemagen.util.SchemaGenResponse;
import org.eclipse.lemminx.customservice.synapse.schemagen.util.SchemaGeneratorHelper;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.SyntaxTreeGenerator;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.SyntaxTreeResponse;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.MediatorFactoryFinder;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.ArtifactTypeResponse;
import org.eclipse.lemminx.customservice.synapse.utils.Constant;
import org.eclipse.lemminx.customservice.synapse.mediator.tryout.pojo.MediatorTryoutInfo;
import org.eclipse.lemminx.customservice.synapse.utils.ExtendedLocation;
import org.eclipse.lemminx.customservice.synapse.utils.Utils;
import org.eclipse.lemminx.customservice.synapse.idp.PdfToImagesRequest;
import org.eclipse.lemminx.dom.DOMDocument;
import org.eclipse.lemminx.extensions.contentmodel.settings.XMLValidationSettings;
import org.eclipse.lemminx.services.extensions.completion.ICompletionResponse;
import org.eclipse.lemminx.settings.SharedSettings;
import org.eclipse.lemminx.uriresolver.URIResolverExtensionManager;
import org.eclipse.lsp4j.DefinitionParams;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.SignatureHelp;
import org.eclipse.lsp4j.TextDocumentIdentifier;
import org.eclipse.lsp4j.TextEdit;
import org.eclipse.lsp4j.WorkspaceFolder;
import org.eclipse.lsp4j.jsonrpc.CancelChecker;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.jsonrpc.messages.Either3;
import org.wso2.mi.tool.connector.tools.generator.grpc.GRPCConnectorGenerator;
import org.wso2.mi.tool.connector.tools.generator.openapi.ConnectorGenerator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SynapseLanguageService implements ISynapseLanguageService {

    private static final Logger log = Logger.getLogger(SynapseLanguageService.class.getName());
    private static final CancelChecker NULL_CANCEL_CHECKER = new CancelChecker() {
        @Override
        public void checkCanceled() {
            // Do nothing.
        }
    };

    private XMLTextDocumentService xmlTextDocumentService;
    private XMLLanguageServer xmlLanguageServer;
    private SynapseLanguageClientAPI languageClient;
    private AbstractConnectorLoader connectorLoader;
    private String extensionPath;
    private String projectUri;
    private boolean isLegacyProject;
    private String projectServerVersion;
    private MediatorHandler mediatorHandler;
    private final ConnectorHolder connectorHolder;
    private AbstractResourceFinder resourceFinder;
    private final InboundConnectorHolder inboundConnectorHolder;
    private final ConnectionHandler connectionHandler;
    private Path synapseXSDPath;
    private TryOutManager tryOutManager;
    private String miServerPath;
    private ExpressionHelperProvider expressionHelperProvider;
    private DynamicFieldsHandler dynamicFieldsHandler;
    private final URIResolverExtensionManager uriResolverExtensionManager;

    public SynapseLanguageService(XMLTextDocumentService xmlTextDocumentService, XMLLanguageServer xmlLanguageServer) {

        this.xmlTextDocumentService = xmlTextDocumentService;
        this.xmlLanguageServer = xmlLanguageServer;
        uriResolverExtensionManager = xmlLanguageServer.getXMLLanguageService().getResolverExtensionManager();
        this.connectorHolder = ConnectorHolder.getInstance();
        this.inboundConnectorHolder = new InboundConnectorHolder();
        mediatorHandler = new MediatorHandler();
        connectionHandler = new ConnectionHandler();
        this.dynamicFieldsHandler = new DynamicFieldsHandler();
    }

    public void init(String projectUri, Object settings, SynapseLanguageClientAPI languageClient) {

        this.languageClient = languageClient;
        if (settings != null) {
            extensionPath = ((JsonObject) settings).get("extensionPath").getAsString();
            miServerPath = ((JsonObject) settings).get("miServerPath").getAsString();
        }
        if (projectUri != null) {
            this.projectUri = projectUri;
            this.isLegacyProject = Utils.isLegacyProject(projectUri);
            this.projectServerVersion = Utils.getServerVersion(projectUri, Constant.DEFAULT_MI_VERSION);
            try {
                inboundConnectorHolder.init(projectUri, projectServerVersion);
                initializeConnectorLoader();
                mediatorHandler.init(projectUri, projectServerVersion, connectorHolder);
                connectionHandler.init(connectorHolder);
                MediatorFactoryFinder.init(projectServerVersion, projectUri, connectorHolder);
                DynamicClassLoader.updateClassLoader(Path.of(projectUri, "deployment", "libs").toFile());
                this.tryOutManager = new TryOutManager(projectUri, miServerPath, connectorHolder, languageClient);
                packHttpConnector();
            } catch (Exception e) {
                log.log(Level.SEVERE, "Error while updating class loader for DB drivers.", e);
            }
            this.expressionHelperProvider = new ExpressionHelperProvider(projectUri);
            resourceFinder = ResourceFinderFactory.getResourceFinder(isLegacyProject);
            resourceFinder.loadDependentResources(projectUri);
        } else {
            log.log(Level.SEVERE, "Project path is null. Language server initialization failed.");
        }
    }

    private void initializeConnectorLoader() throws InvalidConfigurationException {

        if (isLegacyProject) {
            connectorLoader = new OldProjectConnectorLoader(languageClient, connectorHolder);
        } else {
            connectorLoader = new NewProjectConnectorLoader(languageClient, connectorHolder, inboundConnectorHolder);
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

        DriverLoader.loadTempDrivers(projectUri);
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
            return doDiagnostics(xmlDocument, cancelChecker);
        });
    }

    private PublishDiagnosticsParams doDiagnostics(DOMDocument xmlDocument, CancelChecker cancelChecker) {

        SharedSettings sharedSettings = xmlTextDocumentService.getSharedSettings();
        XMLValidationSettings validationSettingsForUri = sharedSettings != null
                ? sharedSettings.getValidationSettings().getValidationSettings(xmlDocument.getDocumentURI())
                : null;
        List<Diagnostic> diagnostics = xmlLanguageServer.getXMLLanguageService().doDiagnostics(xmlDocument,
                validationSettingsForUri,
                Collections.emptyMap(), cancelChecker);
        return new PublishDiagnosticsParams(xmlDocument.getDocumentURI(), diagnostics);
    }

    @Override
    public CompletableFuture<PublishDiagnosticsParams> codeDiagnostic(CodeDiagnosticRequest param) {

        return CompletableFuture.supplyAsync(() -> {
            DOMDocument xmlDocument = Utils.getDOMDocument(param.getCode(), uriResolverExtensionManager);
            return doDiagnostics(xmlDocument, NULL_CANCEL_CHECKER);
        });
    }

    @Override
    public CompletableFuture<ExpressionValidationResponse> expressionValidation(ExpressionParam param) {

        return CompletableFuture.supplyAsync(() -> {
            String expression = param.getExpression();
            if (expression.startsWith("${") && expression.endsWith("}")) {
                expression = expression.substring(2, expression.length() - 1);
            }
            List<ExpressionError> errors = ExpressionValidator.validate(expression);
            return new ExpressionValidationResponse(errors.isEmpty(), errors);
        });
    }

    @Override
    public CompletableFuture<DirectoryMapResponse> directoryTree(WorkspaceFolder param) {

        DirectoryMapResponse response = DirectoryTreeBuilder.buildDirectoryTree(param);
        return CompletableFuture.supplyAsync(() -> response);
    }

    @Override
    public CompletableFuture<ExtendedLocation> definition(
            DefinitionParams params) {

        log.log(Level.INFO, "Processing definition request for document: " + params.getTextDocument().getUri());
        return xmlTextDocumentService.computeDOMAsync(params.getTextDocument(), (xmlDocument, cancelChecker) -> {
            Map<String, ResourceResponse> dependentResourcesMap = resourceFinder.getDependentResourcesMap();

            return SynapseDefinitionProvider.definition(xmlDocument, params.getPosition(), projectUri,
                    cancelChecker, dependentResourcesMap);
        });
    }

    @Override
    public CompletableFuture<ResourceResponse> availableResources(ResourceParam param) {

        ResourceResponse response = resourceFinder.getAvailableResources(projectUri, param.resourceType);
        return CompletableFuture.supplyAsync(() -> response);
    }

    @Override
    public CompletableFuture<Either3<ConnectorResponse, Connector, Boolean>> availableConnectors(ConnectorParam param) {

        return CompletableFuture.supplyAsync(() -> {
            if (param.connectorName != null && !param.connectorName.isEmpty()) {
                Connector connector = connectorHolder.getConnector(param.connectorName);
                if (connector == null) {
                    return Either3.forThird(Boolean.FALSE);
                }
                return Either3.forSecond(connector);
            }
            return Either3.forFirst(new ConnectorResponse(connectorHolder.getConnectors()));
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

    public void updateInboundConnectors() {

        inboundConnectorHolder.getCustomInboundConnectors();
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
            List<ConfigurableEntry> configurableEntries = ConfigParser.scanConfigurableEntries(projectUri);
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
                        resourceUsagesRequest.getResourceFilePath(), connectorHolder, isLegacyProject);
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
    public CompletableFuture<Boolean> isEqualSwaggers(IsEqualSwaggersParam param) {

        RestApiAdmin generator = new RestApiAdmin();
        return CompletableFuture.supplyAsync(() -> generator.isEqualSwaggers(param));
    }

    @Override
    public CompletableFuture<Boolean> saveInboundConnectorSchema(InboundConnectorParam param) {

        return CompletableFuture.supplyAsync(() -> inboundConnectorHolder.saveInboundConnector(param.connectorName,
                param.uiSchema));
    }

    @Override
    public CompletableFuture<InboundConnectorResponse> getInboundConnectorSchema(InboundConnectorParam param) {

        return CompletableFuture.supplyAsync(() -> {
            if (param.connectorId != null) {
                return inboundConnectorHolder.getInboundConnectorSchemaFromId(param.connectorId);
            } else {
                return inboundConnectorHolder.getInboundConnectorSchema(new File(param.documentPath));
            }
        });
    }

    @Override
    public CompletableFuture<JsonObject> getLocalInboundConnectors() {

        return CompletableFuture.supplyAsync(() -> inboundConnectorHolder.getLocalInboundConnectorList());
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
    public CompletableFuture<CheckDBDriverResponseParams> checkDBDriver(CheckDBDriverRequestParams requestParams) {
        CheckDBDriverResponseParams response = QueryGenerator.isDriverAvailableInClassPath(requestParams.className, projectUri);
        return CompletableFuture.supplyAsync(() -> response);
    }

    @Override
    public CompletableFuture<Boolean> addDBDriver(ModifyDriverRequestParams requestParams) {
        boolean isSuccess = QueryGenerator.addDriverToClassPath(requestParams.addDriverPath, requestParams.className);
        return CompletableFuture.supplyAsync(() -> isSuccess);
    }

    @Override
    public CompletableFuture<Boolean> removeDBDriver(ModifyDriverRequestParams requestParams) {
        boolean response = QueryGenerator.removeDriverFromClassPath(requestParams.removeDriverPath);
        return CompletableFuture.supplyAsync(() -> response);
    }

    @Override
    public CompletableFuture<Boolean> modifyDBDriver(ModifyDriverRequestParams requestParams) {
        boolean response = QueryGenerator.modifyDriverInClassPath(requestParams.addDriverPath,
                requestParams.removeDriverPath, requestParams.className);
        return CompletableFuture.supplyAsync(() -> response);
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
    public CompletableFuture<List<String>> getProjectIntegrationType(WorkspaceFolder param) {

        List<String> response = OverviewPage.getProjectIntegrationType(param);
        return CompletableFuture.supplyAsync(() -> response);
    }

    @Override
    public CompletableFuture<JsonObject> getMediators(MediatorRequest mediatorRequest) {

        return CompletableFuture.supplyAsync(() -> mediatorHandler.getSupportedMediators(mediatorRequest.documentIdentifier, mediatorRequest.position));
    }

    @Override
    public CompletableFuture<JsonObject> getMediatorUISchema(UISchemaRequest uiSchemaRequest) {

        return CompletableFuture.supplyAsync(() -> mediatorHandler.getUiSchema(uiSchemaRequest.mediatorType, uiSchemaRequest.documentIdentifier, uiSchemaRequest.position));
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
                () -> mediatorHandler.getUISchemaWithValues(mediatorRequest.documentIdentifier,
                        mediatorRequest.position));
    }

    @Override
    public CompletableFuture<MediatorTryoutInfo> tryOutMediator(MediatorTryoutRequest request) {

        return CompletableFuture.supplyAsync(() -> tryOutManager.tryout(request));
    }

    @Override
    public CompletableFuture<Boolean> shutDownTryoutServer() {

        return CompletableFuture.supplyAsync(() -> Boolean.valueOf(tryOutManager.shutdown()));
    }

    @Override
    public CompletableFuture<MediatorTryoutInfo> mediatorInputOutputSchema(MediatorTryoutRequest request) {

        return CompletableFuture.supplyAsync(() -> tryOutManager.getInputOutputSchema(request));
    }

    @Override
    public CompletableFuture<TestConnectionResponse> testConnectorConnection(TestConnectionRequest request) {

        return CompletableFuture.supplyAsync(() -> tryOutManager.testConnectorConnection(request));
    }

    @Override
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
    public CompletableFuture<UpdateResponse> updateProperty(UpdatePropertyRequest request) {
        UpdateResponse response = PomParser.updateProperty(projectUri, request);
        return CompletableFuture.supplyAsync(() -> response);
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
        String statusMessage = DependencyDownloadManager.downloadDependencies(projectUri);
        updateConnectors();
        return CompletableFuture.supplyAsync(() -> statusMessage);
    }

    @Override
    public CompletableFuture<String> refetchIntegrationProjectDependencies() {
        
		log.info("Refetching integration project dependencies for project: " + projectUri);
        return CompletableFuture.supplyAsync(() -> {
			return DependencyDownloadManager.refetchIntegrationProjectDependencies(projectUri);
		});
    }

    @Override
    public CompletableFuture<DependencyStatusResponse> getDependencyStatusList() {

        return CompletableFuture.supplyAsync(() -> DependencyDownloadManager.getDependencyStatusList(projectUri));
    }

    @Override
    public CompletableFuture<LoadDependentResourcesResponse> loadDependentResources() {

        return CompletableFuture.supplyAsync(() -> {
			log.info("Loading dependent resources for project: " + projectUri);
			LoadDependentResourcesResponse result = resourceFinder.loadDependentResources(projectUri);
        	updateConnectors();
			log.fine("Dependent resources loaded successfully for project: " + projectUri);
			return result;
		});
    }

    @Override
    public CompletableFuture<ConnectorGeneratorResponse> generateConnector(ConnectorGenerateRequest connectorGenReq) {
        String filePath = null;
        try {
            if (connectorGenReq.openAPIPath.endsWith(".proto")) {
                filePath = GRPCConnectorGenerator.generateConnector(connectorGenReq.openAPIPath,
                        connectorGenReq.connectorProjectPath, projectServerVersion, projectUri);
            } else {
                filePath = ConnectorGenerator.generateConnector(connectorGenReq.openAPIPath,
                        connectorGenReq.connectorProjectPath, projectServerVersion, projectUri);
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error occurred while generating the connector", e);
        }
        ConnectorGeneratorResponse response = new ConnectorGeneratorResponse(filePath != null, filePath);
        return CompletableFuture.supplyAsync(() -> response);
    }

    @Override
    public CompletableFuture<ArtifactTypeResponse> getArtifactType(TextDocumentIdentifier artifactIdentifier) {

        return CompletableFuture.supplyAsync(() -> SyntaxTreeGenerator.getArtifactType(artifactIdentifier.getUri()));
    }

    @Override
    public CompletableFuture<Map<String, List<DynamicField>>> getDynamicFields(GetDynamicFieldsRequest request) {

        return CompletableFuture.supplyAsync(() -> {
            DriverLoader.loadTempDrivers(projectUri);
            return dynamicFieldsHandler.handleDynamicFieldsRequest(request).getFields();
        });
    }

    @Override
    public CompletableFuture<List<String>> getStoredProcedures(QueryGenRequestParams request) {

        return CompletableFuture.supplyAsync(() -> {
            DriverLoader.loadTempDrivers(projectUri);
            return dynamicFieldsHandler.getStoredProcedures(request);
        });
    }

    @Override
    public CompletableFuture<String> downloadDriverForConnector(DriverDownloadRequest request) {

        return CompletableFuture.supplyAsync(() -> ConnectorDownloadManager.downloadDriverForConnector(
                projectUri,
                request.getConnectorName(),
                request.getConnectionType()));
    }

    @Override
    public CompletableFuture<DeployPluginDetails> updateMavenDeployPlugin(DeployPluginDetails pluginDetails) {

        return CompletableFuture.supplyAsync(() -> PomParser.addCarDeployPluginToPom(
                new File(projectUri + File.separator + Constants.POM_FILE), pluginDetails));
    }

    @Override
    public CompletableFuture<DeployPluginDetails> getMavenDeployPluginDetails() {

        return CompletableFuture.supplyAsync(() -> PomParser.extractCarDeployPluginFields(
                new File(projectUri + File.separator + Constants.POM_FILE)));
    }

    @Override
    public CompletableFuture<TextEdit> removeMavenDeployPlugin() {

        return CompletableFuture.supplyAsync(() -> PomParser.removeDeployPlugin(
                new File(projectUri + File.separator + Constants.POM_FILE)));
    }

    @Override
    public CompletableFuture<List<ConfigDetails>> getConfigurableList() {

        return CompletableFuture.supplyAsync(() -> ConfigParser.getConfigDetails(projectUri));
    }

    @Override
    public CompletableFuture<String> getLocalInboundEndpointsListForCopilot() {

        return CompletableFuture.supplyAsync(() -> inboundConnectorHolder.getLocalInboundEndpointsListForCopilot());
    }

    @Override
    public CompletableFuture<List<String>> pdfToImagesBase64(PdfToImagesRequest param) {

    	return CompletableFuture.supplyAsync(() -> Utils.pdfToImage(param.getBase64()));
    }

    public String getProjectUri() {
        return projectUri;
    }

    public ConnectorHolder getConnectorHolder() {

        return connectorHolder;
    }

    public String getExtensionPath() {

        return extensionPath;
    }

    public Path getSynapseXSDPath() {

        return synapseXSDPath;
    }

    public void setSynapseXSDPath(Path synapseXSDPath) {

        this.synapseXSDPath = synapseXSDPath;
    }

    public void dispose() {

        tryOutManager.shutdown();
    }

    private void packHttpConnector() {

        if (Utils.compareVersions(projectServerVersion, Constant.MI_440_VERSION) >= 0) {
            String projectId = new File(projectUri).getName() + "_" + Utils.getHash(projectUri);
            String connectorDownloadPath = Path.of(System.getProperty(Constant.USER_HOME), Constant.WSO2_MI,
                    Constant.CONNECTORS, projectId, Constant.DOWNLOADED).toString();
            File connectorDownloadFolder = new File(connectorDownloadPath);
            if (!connectorDownloadFolder.exists()) {
                boolean isDirectoryCreationSuccessful = connectorDownloadFolder.mkdirs();
                if (!isDirectoryCreationSuccessful) {
					log.log(Level.SEVERE, "Error occurred while creating directory: " + connectorDownloadFolder);
                }
            } else {
                File[] matchingFiles = connectorDownloadFolder.listFiles((dir, name) ->
                        name.startsWith("mi-connector-http") && name.endsWith(".zip")
                );
                if (matchingFiles != null && matchingFiles.length > 0) {
                    return;
                }
            }
            try {
                InputStream inputStream = SynapseLanguageService.class.getResourceAsStream(
                        "/org/eclipse/lemminx/connectors/mi-connector-http-0.1.14.zip");
                if (inputStream == null) {
                    throw new FileNotFoundException("HTTP connector not found.");
                }
                Path httpConnectorPath = Paths.get(connectorDownloadPath, "mi-connector-http-0.1.14.zip");
                Files.copy(inputStream, httpConnectorPath, StandardCopyOption.REPLACE_EXISTING);
                inputStream.close();
                updateConnectors();
            } catch (Exception e) {
                log.log(Level.SEVERE, "Error while packing the HTTP connector to the project. ", e);
            }
        }
    }
}
