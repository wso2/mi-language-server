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

package org.eclipse.lemminx.customservice.synapse.connectors;

import org.eclipse.lemminx.customservice.synapse.connectors.entity.Connector;
import org.eclipse.lemminx.customservice.synapse.connectors.entity.ConnectorAction;
import org.eclipse.lemminx.customservice.synapse.connectors.entity.TestConnectionRequest;
import org.eclipse.lemminx.customservice.synapse.connectors.entity.TestConnectionResponse;
import org.eclipse.lemminx.customservice.synapse.mediator.TryOutConstants;
import org.eclipse.lemminx.customservice.synapse.mediator.TryOutUtils;
import org.eclipse.lemminx.customservice.synapse.mediator.tryout.TryOutHandler;
import org.eclipse.lemminx.customservice.synapse.mediator.tryout.pojo.InvalidConfigurationException;
import org.eclipse.lemminx.customservice.synapse.mediator.tryout.pojo.MediatorInfo;
import org.eclipse.lemminx.customservice.synapse.mediator.tryout.pojo.MediatorTryoutInfo;
import org.eclipse.lemminx.customservice.synapse.mediator.tryout.pojo.MediatorTryoutRequest;
import org.eclipse.lemminx.customservice.synapse.mediator.tryout.pojo.Property;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.LocalEntry;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.connector.ConnectorParameter;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.LocalEntrySerializer;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.mediator.ConnectorSerializer;
import org.eclipse.lemminx.customservice.synapse.utils.Constant;
import org.eclipse.lemminx.customservice.synapse.utils.Utils;
import org.eclipse.lsp4j.Position;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.eclipse.lemminx.customservice.synapse.mediator.TryOutConstants.TEMP_FOLDER_PATH;

public class ConnectionTester {

    private static final Logger LOGGER = Logger.getLogger(ConnectionTester.class.getName());
    private static final Path CONNECTOR_PROJECT_TEMP_PATH = Path.of(System.getProperty("user.home"), ".wso2-mi");
    private static final Path LOCAL_ENTRY_RELATIVE_PATH =
            Path.of("src", "main", "wso2mi", "artifacts", "local-entries");
    private String projectRoot;
    private final TryOutHandler tryOutHandler;
    private ConnectorHolder connectorHolder;

    public ConnectionTester(String projectRoot, TryOutHandler tryOutHandler, ConnectorHolder connectorHolder) {

        this.projectRoot = projectRoot;
        this.tryOutHandler = tryOutHandler;
        this.connectorHolder = connectorHolder;
    }

    public TestConnectionResponse testConnection(TestConnectionRequest request) {

        String connectorName = request.getConnectorName();
        if (!connectorHolder.exists(connectorName)) {
            return new TestConnectionResponse("Connector not found");
        }

        LocalEntry localEntry = new LocalEntry();
        String key = getLocalEntryKey(request);
        localEntry.setKey(key);

        Connector connector = connectorHolder.getConnector(connectorName);
        ConnectorAction initOperation = connector.getAction(Constant.INIT);
        if (initOperation == null) {
            return new TestConnectionResponse("Connection operation not found");
        }

        String connectionXml = getConnectionXml(connector, initOperation, request.getParameters());
        localEntry.setContent(connectionXml);
        String localEntryXml = LocalEntrySerializer.serializeLocalEntry(localEntry);

        Path tempProjectPath = CONNECTOR_PROJECT_TEMP_PATH.resolve(connectorName + "_" + UUID.randomUUID());
        try {
            tempProjectPath.toFile().mkdirs();
            copyConnectorDependencies(tempProjectPath, connector);
            addPomFile(Path.of(projectRoot), tempProjectPath);
            createLocalEntryFile(tempProjectPath, localEntryXml, localEntry.getKey());

            ConnectorAction testConnectionOperation = connector.getAction(Constant.TEST_CONNECTION_OPERATION);
            if (testConnectionOperation == null) {
                return new TestConnectionResponse("Test connection operation not found");
            }

            org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.connector.Connector testConnection =
                    getTestConnectionOperation(connectorName, testConnectionOperation, localEntry.getKey());
            MediatorTryoutInfo info = getMediatorTryoutInfo(testConnection, tempProjectPath.toString());
            Boolean isValidConnection = checkIsValidConnection(info);
            if (isValidConnection != null) {
                return new TestConnectionResponse(isValidConnection);
            }
            return new TestConnectionResponse("Error while testing the connection");
        } catch (IOException | InvalidConfigurationException e) {
            LOGGER.log(Level.SEVERE, "Error while testing the connection", e);
            return new TestConnectionResponse("Error while testing the connection");
        } finally {
            try {
                Utils.deleteDirectory(tempProjectPath);
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Error while deleting the temp project directory", e);
            }
        }
    }

    private void copyConnectorDependencies(Path tempProjectPath, Connector connector) throws IOException {

        List<File> connectorZips = connectorHolder.getConnectorZips();
        if (connectorZips != null) {
            File connectorZip = connectorZips.stream()
                    .filter(file -> file.getName().startsWith(connector.getName()))
                    .findFirst()
                    .orElse(null);
            if (connectorZip != null) {
                Utils.copyFile(connectorZip.getAbsolutePath(),
                        tempProjectPath.resolve(TryOutConstants.PROJECT_CONNECTOR_PATH).toString());
            }
        }
    }

    private MediatorTryoutInfo getMediatorTryoutInfo(
            org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.connector.Connector testConnection,
            String tempProjectPath)
            throws IOException, InvalidConfigurationException {

        String apiPath = TryOutUtils.createAPI(testConnection, tempProjectPath.toString());
        Position position = TryOutUtils.getMediatorPosition(apiPath, 0, 0);
        MediatorTryoutRequest mediatorTryoutRequest =
                new MediatorTryoutRequest(apiPath, position.getLine(), position.getCharacter(),
                        "{}", null);
        return tryOutHandler.handleIsolatedTryOut(tempProjectPath.toString(), mediatorTryoutRequest);
    }

    private void createLocalEntryFile(Path tempProjectPath, String localEntryXml, String localEntryKey)
            throws IOException {

        Path localEntryPath =
                tempProjectPath.resolve(LOCAL_ENTRY_RELATIVE_PATH).resolve(localEntryKey + ".xml");
        if (!localEntryPath.toFile().exists()) {
            localEntryPath.toFile().getParentFile().mkdirs();
        }
        Utils.writeToFile(localEntryPath.toString(), localEntryXml);
    }

    private String getLocalEntryKey(TestConnectionRequest request) {

        String key = request.getParameters().get(Constant.NAME);
        if (key == null) {
            String uuid = UUID.randomUUID().toString();
            key = request.getConnectorName() + "_" + uuid;
            request.addParameter(Constant.NAME, key);
        }
        return key;
    }

    private String getConnectionXml(Connector connector, ConnectorAction initOperation,
                                    Map<String, String> parameters) {

        org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.connector.Connector connection =
                new org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.connector.Connector();
        connection.setConnectorName(connector.getName());
        connection.setTag(initOperation.getTag());
        connection.setMethod(Constant.INIT);
        if (parameters != null) {
            for (String parameter : initOperation.getParameters()) {
                if (parameters.containsKey(parameter)) {
                    ConnectorParameter connectorParameter = new ConnectorParameter();
                    connectorParameter.setName(parameter);
                    connectorParameter.setValue(parameters.get(parameter));
                    connection.addParameter(connectorParameter);
                }
            }
        }
        ConnectorSerializer connectorSerializer = new ConnectorSerializer();
        return connectorSerializer.serializeMediator(null, connection).toString();
    }

    private org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.connector.Connector getTestConnectionOperation(
            String connectorName, ConnectorAction testConnectionOperation, String localEntryKey) {

        org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.connector.Connector testConnection =
                new org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.connector.Connector();
        testConnection.setConnectorName(connectorName);
        testConnection.setTag(testConnectionOperation.getTag());
        testConnection.setMethod(Constant.TEST_CONNECTION_OPERATION);
        testConnection.setConfigKey(localEntryKey);
        return testConnection;
    }

    private Boolean checkIsValidConnection(MediatorTryoutInfo info) {

        if (info != null) {
            MediatorInfo outputInfo = info.getOutput();
            if (outputInfo != null) {
                List<Property> synapseProperties = outputInfo.getSynapse();
                Property isValidConnection = synapseProperties.stream()
                        .filter(property -> Constant.IS_VALID_CONNECTION.equals(property.getKey()))
                        .findFirst()
                        .orElse(null);
                if (isValidConnection != null) {
                    return Boolean.parseBoolean(isValidConnection.getValue());
                }
            }
        }
        return null;
    }

    private void addPomFile(Path projectRoot, Path tempProjectPath) throws IOException {

        Path pomPath = projectRoot.resolve(Constant.POM);
        Path mavenWrapperPath = projectRoot.resolve("mvnw");
        Path dotMvnPath = projectRoot.resolve(".mvn");

        for (Path path : List.of(pomPath, mavenWrapperPath, dotMvnPath)) {
            if (path.toFile().exists()) {
                if (path.toFile().isDirectory()) {
                    Utils.copyFolder(path, tempProjectPath.resolve(path.getFileName()), null);
                } else {
                    Utils.copyFile(path.toString(), tempProjectPath.toString());
                }
            }
        }
    }
}
