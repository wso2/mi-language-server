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

import com.google.gson.JsonObject;
import org.eclipse.lemminx.customservice.ISynapseLanguageService;
import org.eclipse.lemminx.customservice.synapse.api.generator.APIGenerateParam;
import org.eclipse.lemminx.customservice.synapse.api.generator.RestApiAdmin;
import org.eclipse.lemminx.customservice.synapse.resourceFinder.ResourceFinder;
import org.eclipse.lemminx.customservice.synapse.resourceFinder.ResourceParam;
import org.eclipse.lemminx.customservice.synapse.resourceFinder.ResourceResponse;
import org.eclipse.lemminx.customservice.synapse.connectors.ConnectorHolder;
import org.eclipse.lemminx.customservice.synapse.connectors.ConnectorLoader;
import org.eclipse.lemminx.customservice.synapse.connectors.SchemaGenerate;
import org.eclipse.lemminx.customservice.synapse.definition.SynapseDefinitionProvider;
import org.eclipse.lemminx.customservice.synapse.directoryTree.DirectoryMapResponse;
import org.eclipse.lemminx.customservice.synapse.directoryTree.DirectoryTreeBuilder;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.SyntaxTreeGenerator;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.SyntaxTreeResponse;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.MediatorFactoryFinder;
import org.eclipse.lemminx.extensions.contentmodel.settings.XMLValidationSettings;
import org.eclipse.lemminx.settings.SharedSettings;
import org.eclipse.lsp4j.DefinitionParams;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.TextDocumentIdentifier;
import org.eclipse.lsp4j.WorkspaceFolder;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SynapseLanguageService implements ISynapseLanguageService {

    private XMLTextDocumentService xmlTextDocumentService;
    private XMLLanguageServer xmlLanguageServer;
    private static String extensionPath;

    public SynapseLanguageService(XMLTextDocumentService xmlTextDocumentService, XMLLanguageServer xmlLanguageServer) {

        this.xmlTextDocumentService = xmlTextDocumentService;
        this.xmlLanguageServer = xmlLanguageServer;
    }

    public void init(Object settings) {

        if (settings != null) {
            extensionPath = ((JsonObject) settings).get("extensionPath").getAsString();
        }
    }

    @Override
    public CompletableFuture<SyntaxTreeResponse> syntaxTree(TextDocumentIdentifier param) {

        return xmlTextDocumentService.computeDOMAsync(param, (xmlDocument, cancelChecker) -> {
            SyntaxTreeGenerator generator = new SyntaxTreeGenerator();
            return generator.getSyntaxTree(xmlDocument);
        });
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
            Location location = SynapseDefinitionProvider.definition(xmlDocument, params.getPosition(), cancelChecker);
            return location;
        });
    }

    @Override
    public CompletableFuture<ResourceResponse> availableResources(ResourceParam param) {

        return xmlTextDocumentService.computeDOMAsync(param.documentIdentifier, (xmlDocument, cancelChecker) -> {
            ResourceResponse response = ResourceFinder.getAvailableResources(xmlDocument, param.resourceType);
            return response;
        });
    }

    @Override
    public CompletableFuture<ConnectorHolder> availableConnectors(TextDocumentIdentifier param) {

        return xmlTextDocumentService.computeDOMAsync(param, (xmlDocument, cancelChecker) -> {
            ConnectorLoader connectorLoader = new ConnectorLoader();
            connectorLoader.updateConnectorLoader(xmlDocument.getDocumentURI());
            ConnectorHolder availableConnectors = connectorLoader.loadConnector();
            return availableConnectors;
        });
    }

    @Override
    public void updateConnectors(TextDocumentIdentifier param) {

        String uri = param.getUri();
        MediatorFactoryFinder.getInstance().updateConnectors(uri);

        //Generate xsd schema for the available connectors and write it to the schema file.
        ConnectorLoader connectorLoader = new ConnectorLoader();
        connectorLoader.updateConnectorLoader(uri);
        ConnectorHolder holder = connectorLoader.loadConnector();
        String connectorPath =
                extensionPath + File.separator + "synapse-schemas" + File.separator +
                        "mediators" + File.separator + "connectors.xsd";
        SchemaGenerate.generate(holder, connectorPath);
    }

    @Override
    public CompletableFuture<String> generateAPI(APIGenerateParam param) {

        RestApiAdmin generator = new RestApiAdmin();
        String apiXml = generator.createAPI(param.apiName, param.swaggerOrWsdlPath, param.mode);
        return CompletableFuture.supplyAsync(() -> apiXml);
    }

    public static String getExtensionPath() {

        return extensionPath;
    }
}
