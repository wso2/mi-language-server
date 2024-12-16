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

import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.eclipse.lemminx.customservice.synapse.connectors.ConnectorHolder;
import org.eclipse.lemminx.customservice.synapse.connectors.entity.Connector;
import org.eclipse.lemminx.customservice.synapse.mediator.TryOutConstants;
import org.eclipse.lemminx.customservice.synapse.mediator.tryout.pojo.ArtifactDeploymentException;
import org.eclipse.lemminx.customservice.synapse.mediator.tryout.pojo.DeployedArtifactType;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.SyntaxTreeGenerator;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.NamedSequence;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.STNode;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.api.API;
import org.eclipse.lemminx.customservice.synapse.utils.Constant;
import org.eclipse.lemminx.customservice.synapse.utils.Utils;
import org.eclipse.lemminx.dom.DOMDocument;
import org.apache.maven.shared.invoker.Invoker;


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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MIServer {

    private static final Logger LOGGER = Logger.getLogger(MIServer.class.getName());
    private static final int DEFAULT_SERVER_PORT = 8290;
    private static final int DEFAULT_INBOUND_PORT = 9201;
    private static final int DEFAULT_DEBUGGER_COMMAND_PORT = 9005;
    private static final int DEFAULT_DEBUGGER_EVENT_PORT = 9006;
    private static final int SERVER_START_TIMEOUT = 30000;
    private static final String SERVER_CONFIG_REGEX = "(?s)(?<=\\[server\\]\\n)(.*?)(?=\\n\\[|$)";
    private static final String MEDIATION_CONFIG_REGEX = "(?s)(?<=\\[mediation\\]\\n)(.*?)(?=\\n\\[|$)";
    private static final String IS_MEDIATION_CONFIG_EXISTS_REGEX = "(?s).*(?<!#)\\s*\\[mediation\\].*";
    private static final String DEPLOYMENT_INTERVAL_REGEX =
            "(?s)(?<=<DeploymentUpdateInterval>)(.*?)(?=</DeploymentUpdateInterval>)";
    private static final String HOT_DEPLOYMENT_INTERVAL = "1";
    private static final String ENTER_PASSWORD_REGEX = ".*Enter KeyStore and Private Key Password.*";
    private static final String SERVER_START_REGEX = ".*Listen on ports : Command \\d+ - Event \\d+.*";
    private static final long SERVER_SHUTDOWN_TIMEOUT = 30;
    private int debuggerCommandPort = DEFAULT_DEBUGGER_COMMAND_PORT;
    private int debuggerEventPort = DEFAULT_DEBUGGER_EVENT_PORT;
    private int offset = 0; // Port offset for starting the server.
    private Path serverPath;
    private Process serverProcess;

    // Maps the artifact folder names to the corresponding folder names in the MI server.
    private static final HashMap<String, String> ARTIFACT_FOLDERS_MAP = new HashMap<>();
    private final Executor executor;
    private final List<String> deployedFiles;
    private boolean isStarted = false;
    private boolean isStarting = false;
    private ManagementAPIClient managementAPIClient;
    private ConnectorHolder connectorHolder;

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

    public MIServer(Path serverPath, ConnectorHolder connectorHolder) {

        this.serverPath = serverPath;
        this.executor = Executors.newSingleThreadExecutor();
        this.connectorHolder = connectorHolder;
        deployedFiles = new ArrayList<>();
    }

    public synchronized void startServer() {

        if (isStarted || isStarting || isServerRunning()) {
            return;
        }
//        assignServerPort();
//        assignDebuggerPorts();
//        updateDeploymentConfiguration();
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
            isStarting = false;
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

    private synchronized void updateDeploymentConfiguration() {

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

    private synchronized void updateHotDeploymentInterval() {

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

    private synchronized Process startServerProcess() throws IOException {

        ProcessBuilder processBuilder = new ProcessBuilder(
                "./micro-integrator.sh",
                "-Desb.debug=true"
        );
        Path serverBinPath = Path.of(serverPath.toString(), "bin");
        processBuilder.directory(serverBinPath.toFile());

        processBuilder.redirectErrorStream(true);
        isStarting = true;
        return processBuilder.start();
    }

    private synchronized void handleKeystorePassword() {

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
                    isStarting = false;
                    this.notifyAll();
                    break;
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, String.format("Error handling server I/O: %s", e.getMessage()));
        }
    }

    private void addShutDownHook() {

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOGGER.info("Initiating graceful shutdown...");
            deleteDeployedFiles();
            shutDown();
        }));
    }

    public void shutDown() {

        try {
            // Send SIGINT (Ctrl+C) signal
            new ProcessBuilder("kill", "-SIGINT", String.valueOf(serverProcess.pid())).start();

            // Wait for up to 10 seconds for the process to terminate
            if (serverProcess.waitFor(SERVER_SHUTDOWN_TIMEOUT, TimeUnit.SECONDS)) {
                LOGGER.info("Server terminated gracefully");
            } else {
                LOGGER.warning("Server didn't terminate in time, forcing shutdown...");
//                serverProcess.destroyForcibly();
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

        // TODO: find a better solution to deploy connector, class, and data mapper artifacts in the old method.
        buildProject(tempProjectUri);
//        copyToMI(tempProjectUri);
        waitForDeployment(Path.of(file));
        LOGGER.log(Level.INFO, "Project deployed successfully");
    }

    private void buildProject(String tempProjectUri) {

        Invoker invoker = new DefaultInvoker();
        invoker.setMavenHome(new File(tempProjectUri));
        File mvnwFile = Path.of(tempProjectUri).resolve("mvnw").toFile();
        invoker.setMavenExecutable(mvnwFile);
        InvocationRequest request = new DefaultInvocationRequest();
        request.setQuiet(true);
        request.setPomFile(Path.of(tempProjectUri, Constant.POM).toFile());
        request.setGoals(List.of("clean", "install"));
        Properties properties = new Properties();
        properties.setProperty("maven.test.skip", "true");
        request.setProperties(properties);
        try {
            invoker.execute(request);
            copyCarToMI(tempProjectUri);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error building the project", e);
        }
    }

    private void copyCarToMI(String tempProjectUri) {

        File[] files = Arrays.stream(Path.of(tempProjectUri).resolve("target").toFile().listFiles()).filter(
                file -> file.getName().endsWith(".car")).toArray(File[]::new);
        if (files != null && files.length > 0) {
            Path targetPath = serverPath.resolve(TryOutConstants.MI_DEPLOYMENT_PATH);
            try {
                Utils.copyFile(files[0].getAbsolutePath(), targetPath.toString());
                deployedFiles.add(targetPath.resolve(files[0].getName()).toString());
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error copying the car file to MI", e);
            }
        }
    }

    private void waitForDeployment(Path filePath) throws ArtifactDeploymentException {

        try {
//            DOMDocument document = Utils.getDOMDocument(filePath.toFile());
//            if (document != null) {
//                String resourceName;
//                DeployedArtifactType type;
//                STNode node = SyntaxTreeGenerator.buildTree(document.getDocumentElement());
//                if (node instanceof API) {
//                    resourceName = ((API) node).getName();
//                    type = DeployedArtifactType.APIS;
//                } else if (node instanceof NamedSequence) {
//                    resourceName = ((NamedSequence) node).getName();
//                    type = DeployedArtifactType.SEQUENCES;
//                } else {
//                    return;
//                }
                int count = 0;
                while (count < 5) {
                    boolean isDeployed = isDeployed();
//                        isDeployed(managementAPIClient, resourceName, DeployedArtifactType.APPLICATIONS);
                    if (isDeployed) {
                        return;
                    }
                    count++;
                    Thread.sleep(1000);
                }
                throw new ArtifactDeploymentException("Artifact deployment failed");
//            }
        } catch (IOException e) {
            throw new ArtifactDeploymentException("Artifact deployment failed", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ArtifactDeploymentException("Artifact deployment interrupted", e);
        }

    }

    private boolean isDeployed() throws IOException, InterruptedException {

        List<String> deployedArtifacts = managementAPIClient.getDeployedCapps();
        return deployedArtifacts != null && deployedArtifacts.size() > 0;
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
            copyConnectorsToMI(tempFolderPath);
            copyRegistryResourcesToMI(tempFolderPath);
            copyClassMediatorsToMI(tempFolderPath);
            copyDataMapperConfigsToMI(tempFolderPath);
            copyArtifactsToMI(tempFolderPath);
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
//        addConnectorImportsToMI();
    }

    private void addConnectorImportsToMI() {

        Path connectorImportPath =
                serverPath.resolve(TryOutConstants.MI_REPOSITORY_PATH).resolve(TryOutConstants.IMPORTS);
        for (Connector connector : connectorHolder.getConnectors()) {
            Path targetPath = connectorImportPath.resolve("{" + connector.getPackageName() + "}" +
                    connector.getName() + ".xml");
            try {
                StringBuilder xml = new StringBuilder();
                xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
                xml.append("<import xmlns=\"http://ws.apache.org/ns/synapse\" name=\"").append(connector.getName())
                        .append("\" package=\"")
                        .append(connector.getPackageName()).append("\" status=\"enabled\"/>");
                Files.write(targetPath, xml.toString().getBytes(StandardCharsets.UTF_8));
                deployedFiles.add(targetPath.toString());
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, String.format("Error writing connector import file: %s", targetPath), e);
            }
        }
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

    public synchronized void waitForServerStartup() {

        long startTime = System.currentTimeMillis();

        while (System.currentTimeMillis() - startTime < SERVER_START_TIMEOUT) {
            try {
                if (isServerRunning()) {
                    managementAPIClient = new ManagementAPIClient(offset);
                    LOGGER.log(Level.INFO, "Server started successfully.");
                    return;
                }
                wait(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                LOGGER.log(Level.WARNING, "Server startup interrupted", e);
                return;
            }
        }
        LOGGER.log(Level.WARNING, "Server did not start within the timeout period");
    }

    public boolean isServerRunning() {

        try (Socket socket = new Socket(TryOutConstants.LOCALHOST, DEFAULT_INBOUND_PORT + offset)) {
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

    public void setServerPath(Path serverPath) {

        this.serverPath = serverPath;
    }

    public Path getServerPath() {

        return serverPath;
    }

    public boolean isStarting() {

        return isStarting;
    }
}
