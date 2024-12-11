/*
 *   Copyright (c) 2024, WSO2 LLC. (http://www.wso2.com).
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

package org.eclipse.lemminx.customservice.synapse.mediator.tryout;

import org.eclipse.lemminx.customservice.synapse.connectors.ConnectorHolder;
import org.eclipse.lemminx.customservice.synapse.connectors.entity.Connector;
import org.eclipse.lemminx.customservice.synapse.connectors.entity.ConnectorAction;
import org.eclipse.lemminx.customservice.synapse.dependency.tree.DependencyLookUp;
import org.eclipse.lemminx.customservice.synapse.dependency.tree.pojo.Dependency;
import org.eclipse.lemminx.customservice.synapse.dependency.tree.visitor.MediatorDependencyVisitor;
import org.eclipse.lemminx.customservice.synapse.mediator.TryOutConstants;
import org.eclipse.lemminx.customservice.synapse.mediator.tryout.pojo.InvalidConfigurationException;
import org.eclipse.lemminx.customservice.synapse.mediator.tryout.pojo.MediatorTryoutInfo;
import org.eclipse.lemminx.customservice.synapse.mediator.tryout.pojo.MediatorTryoutRequest;
import org.eclipse.lemminx.customservice.synapse.mediator.tryout.pojo.TestConnectionRequest;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.SyntaxTreeGenerator;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.MediatorFactoryFinder;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.LocalEntry;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.api.API;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.api.APIResource;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.connector.ConnectorParameter;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.Mediator;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.Respond;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.misc.common.Sequence;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.LocalEntrySerializer;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.api.APISerializer;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.mediator.ConnectorSerializer;
import org.eclipse.lemminx.customservice.synapse.utils.Utils;
import org.eclipse.lemminx.dom.DOMDocument;
import org.eclipse.lsp4j.Position;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import static org.eclipse.lemminx.customservice.synapse.mediator.TryOutConstants.TEMP_FOLDER_PATH;

public class IsolatedTryOutHandler {

    private String projectRoot;
    private final TryOutHandler tryOutHandler;
    private ConnectorHolder connectorHolder;

    public IsolatedTryOutHandler(TryOutHandler tryOutHandler, String projectRoot, ConnectorHolder connectorHolder) {

        this.tryOutHandler = tryOutHandler;
        this.projectRoot = projectRoot;
        this.connectorHolder = connectorHolder;
    }

    public MediatorTryoutInfo tryOut(MediatorTryoutRequest request) {

        String mediatorString = request.getEdits()[0].getText();
        DOMDocument dom = Utils.getDOMDocument(mediatorString);
        if (dom == null) {
            return new MediatorTryoutInfo("Invalid mediator content");
        }

        Mediator mediator = MediatorFactoryFinder.getInstance().getMediator(dom.getDocumentElement());
        if (mediator == null) {
            return new MediatorTryoutInfo("Invalid mediator content");
        }
        String tempProjectPath = TEMP_FOLDER_PATH.resolve(mediator.getTag() + "_" + UUID.randomUUID()).toString();
        String tryoutApi;
        try {
            tryoutApi = createAPI(mediator, tempProjectPath);
            copyDependencies(mediator, tempProjectPath);
            Position position = getMediatorPosition(tryoutApi);
            MediatorTryoutRequest mediatorTryoutRequest =
                    new MediatorTryoutRequest(tryoutApi, position.getLine(), position.getCharacter(),
                            request.getInputPayload(), null);
            mediatorTryoutRequest.setMediatorInfo(request.getMediatorInfo());
            return tryOutHandler.handleIsolatedTryOut(tempProjectPath, mediatorTryoutRequest);
        } catch (InvalidConfigurationException e) {
            return new MediatorTryoutInfo("Error while creating the API for the mediator");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Position getMediatorPosition(String tryoutApi) throws IOException {

        DOMDocument dom = Utils.getDOMDocument(new File(tryoutApi));
        if (dom != null) {
            API api = (API) SyntaxTreeGenerator.buildTree(dom.getDocumentElement());
            if (api != null) {
                return api.getResource()[0].getInSequence().getMediatorList().get(0).getRange().getStartTagRange()
                        .getStart();
            }
        }
        throw new IOException("Error while getting the mediator position");
    }

    private void copyDependencies(Mediator mediator, String tempProjectPath) {

        MediatorDependencyVisitor visitor = new MediatorDependencyVisitor(projectRoot, new DependencyLookUp());
        visitor.visit(mediator);
        List<Dependency> dependencies = visitor.getDependencies();
        if (dependencies != null && !dependencies.isEmpty()) {
            for (Dependency dependency : dependencies) {
                String path = dependency.getPath();
                String targetPath = null;
                switch (dependency.getType()) {
                    case API:
                        targetPath = Path.of(tempProjectPath, "src", "main", "wso2mi", "artifacts", "apis").toString();
                        break;
                    case ENDPOINT:
                        targetPath =
                                Path.of(tempProjectPath, "src", "main", "wso2mi", "artifacts", "endpoints").toString();
                        break;
                    case SEQUENCE:
                        targetPath =
                                Path.of(tempProjectPath, "src", "main", "wso2mi", "artifacts", "sequences").toString();
                        break;
                    case TEMPLATE:
                        targetPath =
                                Path.of(tempProjectPath, "src", "main", "wso2mi", "artifacts", "templates").toString();
                        break;
                    case CONNECTION:
                    case LOCAL_ENTRY:
                        targetPath = Path.of(tempProjectPath, "src", "main", "wso2mi", "artifacts", "local-entries")
                                .toString();
                        break;
                    case MESSAGE_STORE:
                        targetPath = Path.of(tempProjectPath, "src", "main", "wso2mi", "artifacts", "message-stores")
                                .toString();
                        break;
                    case DATA_SERVICE:
                        targetPath = Path.of(tempProjectPath, "src", "main", "wso2mi", "artifacts", "data-services")
                                .toString();
                        break;
                    case DATASOURCE:
                        targetPath = Path.of(tempProjectPath, "src", "main", "wso2mi", "artifacts", "datasources")
                                .toString();
                        break;
                    case INBOUND_ENDPOINT:
                        targetPath = Path.of(tempProjectPath, "src", "main", "wso2mi", "artifacts", "inbound-endpoints")
                                .toString();
                        break;
                    case MESSAGE_PROCESSOR:
                        targetPath =
                                Path.of(tempProjectPath, "src", "main", "wso2mi", "artifacts", "message-processors")
                                        .toString();
                        break;
                }
                if (targetPath != null) {
                    try {
                        Utils.copyFile(path, targetPath);
                    } catch (IOException e) {
                        System.out.println("Error while copying the dependencies");
                    }
                }
            }
        }
    }

    private String createAPI(Mediator mediator, String tempPath) throws
            InvalidConfigurationException {

        try {
            if (mediator != null) {
                String apiName = mediator.getTag() + "_tryout_" + UUID.randomUUID();
                API api = new API();
                api.setName(apiName);
                api.setContext(TryOutConstants.SLASH + apiName);
                APIResource resource = new APIResource();
                resource.setMethods(new String[]{"POST"});
                resource.setUrlMapping(TryOutConstants.SLASH);
                api.setResource(new APIResource[]{resource});
                Sequence sequence = new Sequence();
                resource.setInSequence(sequence);
                sequence.addToMediatorList(mediator);
                sequence.addToMediatorList(new Respond());
                String apiContent = APISerializer.serializeAPI(api);
                Path apiPath = Path.of(tempPath, "src", "main", "wso2mi", "artifacts", "apis",
                        apiName + ".xml");
                if (!apiPath.toFile().exists()) {
                    apiPath.toFile().getParentFile().mkdirs();
                }
                Utils.writeToFile(apiPath.toString(), apiContent);
                return apiPath.toString();
            }
        } catch (IOException e) {
            throw new InvalidConfigurationException("Error while creating the API for the mediator", e);
        }
        return null;
    }

    public boolean testConnection(TestConnectionRequest request) {

        String connectorName = request.getConnectorName();
        if (!connectorHolder.exists(connectorName)) {
            return false;
        }
        LocalEntry localEntry = new LocalEntry();
        String key = request.getParameters().get("name");
        localEntry.setKey(key);

        Connector connector = connectorHolder.getConnector(connectorName);
        ConnectorAction initOperation = connector.getAction("init");
        if (initOperation == null) {
            return false;
        }

        org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.connector.Connector connection =
                new org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.connector.Connector();
        connection.setConnectorName(connectorName);
        connection.setTag(initOperation.getTag());
        connection.setMethod("init");
        for (String parameter : initOperation.getParameters()) {
            if (request.getParameters().containsKey(parameter)) {
                ConnectorParameter connectorParameter = new ConnectorParameter();
                connectorParameter.setName(parameter);
                connectorParameter.setValue(request.getParameters().get(parameter));
                connection.addParameter(connectorParameter);
            }
        }
        ConnectorSerializer connectorSerializer = new ConnectorSerializer();
        String connectionXml = connectorSerializer.serializeMediator(null, connection).toString();
        localEntry.setContent(connectionXml);
        String localEntryXml = LocalEntrySerializer.serializeLocalEntry(localEntry);

        String tempProjectPath = TEMP_FOLDER_PATH.resolve(connectorName + "_" + UUID.randomUUID()).toString();
        try {
            Path localEntryPath = Path.of(tempProjectPath, "src", "main", "wso2mi", "artifacts", "local-entries",
                    localEntry.getKey() + ".xml");
            if (!localEntryPath.toFile().exists()) {
                localEntryPath.toFile().getParentFile().mkdirs();
            }
            Utils.writeToFile(localEntryPath.toString(), localEntryXml);

            ConnectorAction testConnectionOperation = connector.getAction("testconnection");
            if (testConnectionOperation == null) {
                return false;
                //TODO: make it as a object and send the error
            }

            org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.connector.Connector testConnection =
                    new org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.connector.Connector();
            testConnection.setConnectorName(connectorName);
            testConnection.setTag(testConnectionOperation.getTag());
            testConnection.setMethod("testconnection");
            testConnection.setConfigKey(localEntry.getKey());
            String apiPath = createAPI(testConnection, tempProjectPath);
            Position position = getMediatorPosition(apiPath);
            MediatorTryoutRequest mediatorTryoutRequest =
                    new MediatorTryoutRequest(apiPath, position.getLine(), position.getCharacter(),
                            "{}", null);
            MediatorTryoutInfo info = tryOutHandler.handleIsolatedTryOut(tempProjectPath, mediatorTryoutRequest);
            return info.getError() == null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
    }
}
