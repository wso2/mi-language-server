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

package org.eclipse.lemminx.customservice.synapse.mediator.tryout.server;

import org.eclipse.lemminx.customservice.synapse.mediator.TryOutConstants;
import org.eclipse.lemminx.customservice.synapse.mediator.tryout.pojo.ArtifactDeploymentException;
import org.eclipse.lemminx.customservice.synapse.mediator.tryout.pojo.DeployedArtifactType;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.SyntaxTreeGenerator;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.NamedSequence;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.STNode;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.api.API;
import org.eclipse.lemminx.customservice.synapse.utils.Utils;
import org.eclipse.lemminx.dom.DOMDocument;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MIServer {

    private static final Logger LOGGER = Logger.getLogger(MIServer.class.getName());
    private static final int DEFAULT_SERVER_PORT = 8280;
    private static final int DEFAULT_INBOUND_PORT = 9191;
    private static final int DEFAULT_DEBUGGER_COMMAND_PORT = 9007;
    private static final int DEFAULT_DEBUGGER_EVENT_PORT = 9008;
    private static final int SERVER_START_TIMEOUT = 30000;
    private static final String SERVER_CONFIG_REGEX = "(?s)(?<=\\[server\\]\\n)(.*?)(?=\\n\\[|$)";
    private static final String MEDIATION_CONFIG_REGEX = "(?s)(?<=\\[mediation\\]\\n)(.*?)(?=\\n\\[|$)";
    private static final String IS_MEDIATION_CONFIG_EXISTS_REGEX = "(?s).*(?<!#)\\s*\\[mediation\\].*";
    private static final String DEPLOYMENT_INTERVAL_REGEX =
            "(?s)(?<=<DeploymentUpdateInterval>)(.*?)(?=</DeploymentUpdateInterval>)";
    private static final String HOT_DEPLOYMENT_INTERVAL = "1";
    private static final String ENTER_PASSWORD_REGEX = ".*Enter KeyStore and Private Key Password.*";
    private static final String SERVER_START_REGEX = ".*Listen on ports : Command \\d+ - Event \\d+.*";
    private int debuggerCommandPort;
    private int debuggerEventPort;
    private int offset; // Port offset for starting the server.
    private final Path serverPath;
    private Process serverProcess;

    // Maps the artifact folder names to the corresponding folder names in the MI server.
    private static final HashMap<String, String> ARTIFACT_FOLDERS_MAP = new HashMap<>();
    private final Executor executor;
    private final List<String> deployedFiles;
    private boolean isStarted = true;

    static {
        ARTIFACT_FOLDERS_MAP.put("apis", "api");
        ARTIFACT_FOLDERS_MAP.put("sequences", "sequences");
        ARTIFACT_FOLDERS_MAP.put("endpoints", "endpoints");
        ARTIFACT_FOLDERS_MAP.put("inbound-endpoints", "inbound-endpoints");
        ARTIFACT_FOLDERS_MAP.put("local-entries", "local-entries");
        ARTIFACT_FOLDERS_MAP.put("message-processors", "message-processors");
        ARTIFACT_FOLDERS_MAP.put("message-stores", "message-stores");
        ARTIFACT_FOLDERS_MAP.put("proxy-services", "proxy-services");
        ARTIFACT_FOLDERS_MAP.put("tasks", "tasks");
        ARTIFACT_FOLDERS_MAP.put("templates", "templates");
    }

    public MIServer(Path serverPath) {

        this.serverPath = serverPath;
        this.executor = Executors.newSingleThreadExecutor();
        deployedFiles = new ArrayList<>();
    }

    public void startServer() {

        assignServerPort();
        assignDebuggerPorts();
        updateDeploymentConfiguration();
        updateHotDeploymentInterval();
        if (!serverPath.toFile().exists()) {
            return;
        }
        try {
            serverProcess = startServerProcess();
            handleKeystorePassword();

            // Graceful shutdown hook
            addShutDownHook();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, String.format("Error starting or running server: %s", e.getMessage()));
        }
    }

    private void assignServerPort() {

        calculatePortOffset();
    }

    private void calculatePortOffset() {

        if (isPortAvailable(DEFAULT_SERVER_PORT)) {
            offset = 0;
        } else {
            offset = getFreePortOffset(DEFAULT_SERVER_PORT, List.of(8290));
        }
    }

    private void assignDebuggerPorts() {

        int debuggerCommandPortOffset = getFreePortOffset(DEFAULT_DEBUGGER_COMMAND_PORT, List.of(getServerPort()));
        debuggerCommandPort = DEFAULT_DEBUGGER_COMMAND_PORT + debuggerCommandPortOffset;
        int debuggerEventPortOffset =
                getFreePortOffset(DEFAULT_DEBUGGER_EVENT_PORT, List.of(getServerPort(), debuggerCommandPort));
        debuggerEventPort = DEFAULT_DEBUGGER_EVENT_PORT + debuggerEventPortOffset;
    }

    private int getFreePortOffset(int port, List<Integer> excludedPorts) {

        int tempPort = port;
        while (excludedPorts.contains(tempPort) || !isPortAvailable(tempPort)) {
            tempPort++;
        }
        return tempPort - port;
    }

    private boolean isPortAvailable(int port) {

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocket.setReuseAddress(true);  // Optional, ensures immediate reuse after the check
            return Boolean.TRUE;  // Port is free if no exception is thrown
        } catch (IOException e) {
            return Boolean.FALSE; // Port is in use
        }
    }

    private void updateDeploymentConfiguration() {

        Path deploymentConfigPath = serverPath.resolve(TryOutConstants.DEPLOYMENT_TOML_PATH);
        try {
            String deploymentConfig = Files.readString(deploymentConfigPath);
            String serverConfig = "hostname = \"localhost\"\n" + "offset = " + offset + "\n";
            String mediationConfig =
                    "synapse.command_debugger_port=" + debuggerCommandPort + "\n" + "synapse.event_debugger_port=" +
                            debuggerEventPort + "\n";
            String updatedConfig;
            if (deploymentConfig.matches(IS_MEDIATION_CONFIG_EXISTS_REGEX)) {
                updatedConfig = deploymentConfig.replaceAll(SERVER_CONFIG_REGEX, serverConfig)
                        .replaceAll(MEDIATION_CONFIG_REGEX, mediationConfig);
            } else {
                String updateText = serverConfig + "\n[mediation]\n" + mediationConfig;
                updatedConfig = deploymentConfig.replaceAll(SERVER_CONFIG_REGEX, updateText);
            }
            Files.write(deploymentConfigPath, updatedConfig.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, String.format("Error updating deployment configuration: %s", e.getMessage()));
        }
    }

    private void updateHotDeploymentInterval() {

        try {
            // Update carbon.xml
            Path carbonConfigPath = serverPath.resolve(TryOutConstants.CARBON_XML_PATH);
            String carbonConfig = Files.readString(carbonConfigPath);
            String updatedConfig = carbonConfig.replaceFirst(DEPLOYMENT_INTERVAL_REGEX, HOT_DEPLOYMENT_INTERVAL);
            Files.write(carbonConfigPath, updatedConfig.getBytes(StandardCharsets.UTF_8));
            Path carbonConfigJ2Path = serverPath.resolve(TryOutConstants.CARBON_XML_J2_PATH);

            // Update carbon.xml.j2
            String carbonConfigJ2 = Files.readString(carbonConfigJ2Path);
            String updatedJ2Config = carbonConfigJ2.replaceFirst(DEPLOYMENT_INTERVAL_REGEX, HOT_DEPLOYMENT_INTERVAL);
            Files.write(carbonConfigJ2Path, updatedJ2Config.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, String.format("Error updating hot deployment interval: %s", e.getMessage()));
        }
    }

    private Process startServerProcess() throws IOException {

        ProcessBuilder processBuilder = new ProcessBuilder(
                "./micro-integrator.sh",
                "-Desb.debug=true"
        );
        Path serverBinPath = Path.of(serverPath.toString(), "bin");
        processBuilder.directory(serverBinPath.toFile());

        processBuilder.redirectErrorStream(true);
        return processBuilder.start();
    }

    private void handleKeystorePassword() {

        // Handle password input
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(serverProcess.getInputStream(), StandardCharsets.UTF_8));
             BufferedWriter writer = new BufferedWriter(
                     new OutputStreamWriter(serverProcess.getOutputStream(), StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.toLowerCase().matches(ENTER_PASSWORD_REGEX)) {
                    String password = "wso2carbon\n";
                    writer.write(password);
                    writer.flush();
                } else if (line.matches(SERVER_START_REGEX)) {
                    isStarted = true;
                    break;
                }
            }
        } catch (IOException e) {
            if (!Thread.currentThread().isInterrupted()) {
                LOGGER.log(Level.SEVERE, String.format("Error handling server I/O: %s", e.getMessage()));
            }
        }
    }

    private void addShutDownHook() {

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOGGER.info("Initiating graceful shutdown...");
            shutDown();
        }));
    }

    public void shutDown() {

        try {
            // Send SIGINT (Ctrl+C) signal
            new ProcessBuilder("kill", "-SIGINT", String.valueOf(serverProcess.pid())).start();

            // Wait for up to 10 seconds for the process to terminate
            if (serverProcess.waitFor(10, TimeUnit.SECONDS)) {
                LOGGER.info("Server terminated gracefully");
            } else {
                LOGGER.warning("Server didn't terminate in time, forcing shutdown...");
                serverProcess.destroyForcibly();
            }
            isStarted = false;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.log(Level.WARNING, String.format("Shutdown interrupted: %s", e.getMessage()));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, String.format("Error shutting down server: %s", e.getMessage()));
        }
    }

    public void deployProject(String tempProjectUri, String file) throws ArtifactDeploymentException {

        copyToMI(tempProjectUri);
        // TODO: Change it to check all the artifacts
        waitForDeployment(Path.of(file));
    }

    private void waitForDeployment(Path filePath) throws ArtifactDeploymentException {

        try {
            DOMDocument document = Utils.getDOMDocument(filePath.toFile());
            if (document != null) {
                String resourceName;
                DeployedArtifactType type;
                STNode node = SyntaxTreeGenerator.buildTree(document.getDocumentElement());
                if (node instanceof API) {
                    resourceName = ((API) node).getName();
                    type = DeployedArtifactType.APIS;
                } else if (node instanceof NamedSequence) {
                    resourceName = ((NamedSequence) node).getName();
                    type = DeployedArtifactType.SEQUENCES;
                } else {
                    return;
                }
                ManagementAPIClient managementAPIClient = new ManagementAPIClient(offset);
                boolean isDeployed = isDeployed(managementAPIClient, resourceName, type);
                if (isDeployed) {
                    return;
                }
                throw new ArtifactDeploymentException("Artifact deployment failed");
            }
        } catch (IOException e) {
            throw new ArtifactDeploymentException("Artifact deployment failed", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ArtifactDeploymentException("Artifact deployment interrupted", e);
        }

    }

    private boolean isDeployed(ManagementAPIClient managementAPIClient, String resourceName, DeployedArtifactType type)
            throws InterruptedException, IOException {

        int count = 0;
        while (count < 10) {
            count++;
            List<ManagementAPIClient.DeployedArtifact> deployedArtifacts =
                    managementAPIClient.getArtifacts(type);
            if (deployedArtifacts != null) {
                boolean res = deployedArtifacts.stream()
                        .anyMatch(artifact -> artifact.getName().equals(resourceName));
                if (res) {
                    return Boolean.TRUE;
                }
                Thread.sleep(100);
            }
        }
        return Boolean.FALSE;
    }

    private void copyToMI(String tempFolderPath) throws ArtifactDeploymentException {

        try {
            copyArtifactsToMI(tempFolderPath);
            copyConnectorsToMI(tempFolderPath);
            copyClassMediatorsToMI(tempFolderPath);
            copyDataMapperConfigsToMI(tempFolderPath);
            copyRegistryResourcesToMI(tempFolderPath);
        } catch (IOException e) {
            throw new ArtifactDeploymentException("Error copying artifacts to MI", e);
        }
    }

    private void copyArtifactsToMI(String tempFolderPath) throws IOException {

        String repositoryPath = serverPath.resolve(TryOutConstants.MI_REPOSITORY_PATH).toString();
        Path artifactPath = Path.of(tempFolderPath).resolve(TryOutConstants.PROJECT_ARTIFACT_PATH);
        for (Map.Entry<String, String> entry : ARTIFACT_FOLDERS_MAP.entrySet()) {
            Path sourcePath = artifactPath.resolve(entry.getKey());
            Path targetPath = Path.of(repositoryPath, entry.getValue());
            Utils.copyFolder(sourcePath, targetPath, deployedFiles);
        }
    }

    private void copyConnectorsToMI(String tempFolderPath) throws IOException {

        Path connectorPath = Path.of(tempFolderPath).resolve(TryOutConstants.PROJECT_CONNECTOR_PATH);
        Path targetPath = serverPath.resolve(TryOutConstants.MI_CONNECTOR_PATH);
        Utils.copyFolder(connectorPath, targetPath, deployedFiles);
    }

    private void copyClassMediatorsToMI(String tempFolderPath) {

        //TODO: Implement this
    }

    private void copyDataMapperConfigsToMI(String tempFolderPath) {

        //TODO: Implement this
    }

    private void copyRegistryResourcesToMI(String tempFolderPath) throws IOException {

        Path registryPath = Path.of(tempFolderPath).resolve(TryOutConstants.PROJECT_REGSTRY_PATH);
        Path govRegistryPath = registryPath.resolve(TryOutConstants.GOV);
        Path configRegistryPath = registryPath.resolve(TryOutConstants.CONF);

        Path targetGovPath = serverPath.resolve(TryOutConstants.MI_GOV_PATH);
        Path targetConfPath = serverPath.resolve(TryOutConstants.MI_CONF_PATH);

        Utils.copyFolder(govRegistryPath, targetGovPath, deployedFiles);
        Utils.copyFolder(configRegistryPath, targetConfPath, deployedFiles);
    }

    public void deleteDeployedFilesAsync() {

        executor.execute(this::deleteDeployedFiles);
    }

    public void deleteDeployedFiles() {

        Iterator<String> iterator = deployedFiles.iterator();
        while (iterator.hasNext()) {
            File deployedFile = new File(iterator.next());
            try {
                if (deployedFile.exists()) {
                    Files.delete(deployedFile.toPath());
                }
                iterator.remove();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, String.format("Error while deleting the file: %s", deployedFile), e);
            }
        }
    }

    public void waitForServerStartup() {

        long startTime = System.currentTimeMillis();

        while (System.currentTimeMillis() - startTime < SERVER_START_TIMEOUT) {
            try {
                if (isServerRunning()) {
                    LOGGER.log(Level.INFO, "Server started successfully.");
                    return;
                }
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                LOGGER.log(Level.WARNING, "Server startup interrupted", e);
                return;
            }
        }
        LOGGER.log(Level.WARNING, "Server did not start within the timeout period");
    }

    private boolean isServerRunning() {

        try (Socket socket = new Socket("localhost", DEFAULT_INBOUND_PORT + offset)) {
            return socket.isConnected();
        } catch (IOException e) {
            return false;
        }
    }

    public int getServerPort() {

        if (isStarted) {
            return DEFAULT_SERVER_PORT + offset;
        }
        return -1;
    }

    public boolean isStarted() {

        return isStarted;
    }

    public int getDebuggerCommandPort() {

        return debuggerCommandPort;
    }

    public int getDebuggerEventPort() {

        return debuggerEventPort;
    }

    public Path getServerPath() {

        return serverPath;
    }
}
