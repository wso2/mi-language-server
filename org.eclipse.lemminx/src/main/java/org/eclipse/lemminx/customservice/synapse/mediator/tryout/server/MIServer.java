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
import org.eclipse.lemminx.customservice.synapse.mediator.TryOutUtils;
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
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class MIServer {

    private static final Logger LOGGER = Logger.getLogger(MIServer.class.getName());
    private static final int SERVER_START_TIMEOUT = 30000;
    private static final String DEPLOYMENT_INTERVAL_REGEX =
            "(?s)(?<=<DeploymentUpdateInterval>)(.*?)(?=</DeploymentUpdateInterval>)";
    private static final String HOT_DEPLOYMENT_INTERVAL = "1";
    private static final String ENTER_PASSWORD_REGEX = ".*Enter KeyStore and Private Key Password.*";
    private static final String SERVER_START_REGEX = ".*Listen on ports : Command \\d+ - Event \\d+.*";
    private Path serverPath;
    private Process serverProcess;

    // Maps the artifact folder names to the corresponding folder names in the MI server.
    private static final HashMap<String, String> ARTIFACT_FOLDERS_MAP = new HashMap<>();
    private final List<String> deployedFiles;
    private boolean isStarted = false;
    private boolean isStarting = false;
    private ManagementAPIClient managementAPIClient;

    static {
        ARTIFACT_FOLDERS_MAP.put("apis", "api");
        ARTIFACT_FOLDERS_MAP.put("sequences", "sequences");
        ARTIFACT_FOLDERS_MAP.put("endpoints", "endpoints");
        ARTIFACT_FOLDERS_MAP.put("inbound-endpoints", "inbound-endpoints");
        ARTIFACT_FOLDERS_MAP.put("local-entries", "local-entries");
        ARTIFACT_FOLDERS_MAP.put("message-processors", "message-processors");
        ARTIFACT_FOLDERS_MAP.put("message-stores", "message-stores");
        ARTIFACT_FOLDERS_MAP.put("proxy-services", "proxy-services");
        ARTIFACT_FOLDERS_MAP.put("templates", "templates");
    }

    public MIServer(Path serverPath) {

        this.serverPath = serverPath;
        deployedFiles = new ArrayList<>();
    }

    public synchronized void startServer() {

        if (isStarted || isStarting || isServerRunning()) {
            return;
        }
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

        String os = System.getProperty("os.name").toLowerCase();
        Path serverBinPath = Path.of(serverPath.toString(), "bin");
        ProcessBuilder processBuilder;

        if (os.contains("win")) {
            String batchFile = new File(serverBinPath.toFile(), "micro-integrator.bat")
                    .getAbsolutePath();
            processBuilder = new ProcessBuilder();
            processBuilder.command("cmd", "/c", batchFile, "-Desb.debug=true");
        } else {
            // Unix-like systems
            processBuilder = new ProcessBuilder("./micro-integrator.sh", "-Desb.debug=true");
        }
        Map<String, String> env = processBuilder.environment();
        env.put("JAVA_HOME", System.getProperty("java.home"));
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

    public boolean shutDown() {

        if (!isStarted) {
            return Boolean.TRUE;
        }
        long parentPid = serverProcess.pid();
        try {
            ProcessHandle parentProcess = ProcessHandle.of(parentPid).orElseThrow();
            Stream<ProcessHandle> descendants = parentProcess.descendants();
            descendants.forEach(process -> process.destroy());
            parentProcess.destroy();
            boolean isAlive = parentProcess.onExit().toCompletableFuture().join().isAlive();
            if (!isAlive) {
                isStarted = false;
            }
            return !isAlive;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, String.format("Error terminating process tree: %s", e.getMessage()));
            return Boolean.FALSE;
        }
    }

    public void deployProject(String tempProjectUri, String file, String projectUri)
            throws ArtifactDeploymentException {

        copyToMI(tempProjectUri, projectUri);
        waitForDeployment(Path.of(file));
        LOGGER.log(Level.INFO, "Project deployed successfully");
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
                int count = 0;
                while (count < 5) {
                    boolean isDeployed =
                            isDeployed(managementAPIClient, resourceName, type);
                    if (isDeployed) {
                        return;
                    }
                    count++;
                    Thread.sleep(1000);
                }
                throw new ArtifactDeploymentException(TryOutConstants.INVALID_ARTIFACT_ERROR);
            }
        } catch (IOException e) {
            throw new ArtifactDeploymentException(TryOutConstants.TRYOUT_FAILURE_MESSAGE, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ArtifactDeploymentException(TryOutConstants.TRYOUT_FAILURE_MESSAGE, e);
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

    private void copyToMI(String tempFolderPath, String projectUri) throws ArtifactDeploymentException {

        try {
            copyDependencyCappToMI(projectUri);
            copyRegistryResourcesToMI(tempFolderPath);
            copyArtifactsToMI(tempFolderPath);
        } catch (IOException e) {
            throw new ArtifactDeploymentException("Error copying artifacts to MI", e);
        }
    }

    private void copyDependencyCappToMI(String projectUri) throws ArtifactDeploymentException {

        Path targetPath = serverPath.resolve(TryOutConstants.MI_DEPLOYMENT_PATH);
        try {
            String projectId = Utils.getHash(projectUri);
            Path projectCAPPPath = TryOutUtils.findCAPP(TryOutConstants.CAPP_CACHE_LOCATION.resolve(projectId));
            if (Files.exists(projectCAPPPath)) {
                Utils.copyFile(projectCAPPPath.toString(), targetPath.toString());
                deployedFiles.add(targetPath.resolve(projectCAPPPath.getFileName()).toString());
            }
            waitForCAPPDeployment();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error copying the capp file to MI", e);
        }
    }

    private void waitForCAPPDeployment() throws ArtifactDeploymentException {

        boolean isDeployed = false;
        int count = 0;
        while (!isDeployed) {
            try {
                Thread.sleep(1000);
                isDeployed = isDeployed();
                if (count > 5) {
                    throw new ArtifactDeploymentException("Error waiting for CAPP deployment");
                }
                count++;
            } catch (InterruptedException | IOException e) {
                LOGGER.log(Level.SEVERE, "Error waiting for CAPP deployment", e);
            }
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

    private void copyRegistryResourcesToMI(String tempFolderPath) throws IOException {

        Path registryPath = Path.of(tempFolderPath).resolve(TryOutConstants.PROJECT_REGSTRY_PATH);
        Path govRegistryPath = registryPath.resolve(TryOutConstants.GOV);
        Path configRegistryPath = registryPath.resolve(TryOutConstants.CONF);

        Path targetGovPath = serverPath.resolve(TryOutConstants.MI_GOV_PATH);
        Path targetConfPath = serverPath.resolve(TryOutConstants.MI_CONF_PATH);

        Utils.copyFolder(govRegistryPath, targetGovPath, deployedFiles);
        Utils.copyFolder(configRegistryPath, targetConfPath, deployedFiles);
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
                    managementAPIClient = new ManagementAPIClient();
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

        try (Socket socket = new Socket(TryOutConstants.LOCALHOST, TryOutConstants.DEFAULT_SERVER_INBOUND_PORT)) {
            return socket.isConnected();
        } catch (IOException e) {
            return false;
        }
    }

    public int getServerPort() {

        if (isStarted) {
            return TryOutConstants.DEFAULT_SERVER_PORT;
        }
        return -1;
    }

    public boolean isStarted() {

        return isStarted;
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
