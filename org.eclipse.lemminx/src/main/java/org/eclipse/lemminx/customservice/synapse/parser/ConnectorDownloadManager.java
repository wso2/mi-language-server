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

package org.eclipse.lemminx.customservice.synapse.parser;

import org.eclipse.lemminx.customservice.synapse.mediator.TryOutConstants;
import org.eclipse.lemminx.customservice.synapse.utils.Constant;
import org.eclipse.lemminx.customservice.synapse.utils.Utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static org.eclipse.lemminx.customservice.synapse.parser.pom.PomParser.getPomDetails;

public class ConnectorDownloadManager {

    private static final Logger LOGGER = Logger.getLogger(ConnectorDownloadManager.class.getName());

    public static String downloadConnectors(String projectPath) {

        String projectId = new File(projectPath).getName() + "_" + Utils.getHash(projectPath);
        File directory = Path.of(System.getProperty(Constant.USER_HOME), Constant.WSO2_MI, Constant.CONNECTORS,
                projectId).toFile();
        File downloadDirectory = Path.of(directory.getAbsolutePath(), Constant.DOWNLOADED).toFile();
        File extractDirectory = Path.of(directory.getAbsolutePath(), Constant.EXTRACTED).toFile();

        if (!directory.exists()) {
            directory.mkdirs();
        }
        if (!extractDirectory.exists()) {
            extractDirectory.mkdirs();
        }
        if (!downloadDirectory.exists()) {
            downloadDirectory.mkdirs();
        }

        OverviewPageDetailsResponse pomDetailsResponse = new OverviewPageDetailsResponse();
        getPomDetails(projectPath, pomDetailsResponse);
        List<DependencyDetails> dependencies = pomDetailsResponse.getDependenciesDetails().getConnectorDependencies();
        deleteRemovedConnectors(downloadDirectory, dependencies, projectPath);
        List<String> failedDependencies = new ArrayList<>();
        for (DependencyDetails dependency : dependencies) {
            try {
                File connector = Path.of(downloadDirectory.getAbsolutePath(),
                        dependency.getArtifact() + "-" + dependency.getVersion() + Constant.ZIP_EXTENSION).toFile();
                File existingArtifact = null;
                if (connector.exists() && connector.isFile()) {
                    LOGGER.log(Level.INFO, "Dependency already downloaded: " + connector.getName());
                } else if ((existingArtifact = getDependencyFromLocalRepo(dependency.getGroupId(),
                        dependency.getArtifact(), dependency.getVersion())) != null ) {
                    LOGGER.log(Level.INFO, "Copying dependency from local repository: " + connector.getName());
                    copyFile(existingArtifact, downloadDirectory);
                } else {
                    LOGGER.log(Level.INFO, "Downloading dependency: " + connector.getName());
                    Utils.downloadConnector(dependency.getGroupId(), dependency.getArtifact(),
                            dependency.getVersion(), downloadDirectory);
                }
            } catch (Exception e) {
                String failedDependency = dependency.getGroupId() + "-" + dependency.getArtifact() + "-" + dependency.getVersion();
                LOGGER.log(Level.WARNING, "Error occurred while downloading dependency " + failedDependency + ": " + e.getMessage());
                failedDependencies.add(failedDependency);
            }
        }
        if (!failedDependencies.isEmpty()) {
            LOGGER.log(Level.SEVERE, "Some connectors were not downloaded: " + String.join(", ", failedDependencies));
            return "Some connectors were not downloaded: " + String.join(", ", failedDependencies);
        }
        return "Success";
    }

    private static void deleteRemovedConnectors(File downloadDirectory, List<DependencyDetails> dependencies,
                                                String projectPath) {

        List<String> existingConnectors =
                dependencies.stream().map(dependency -> dependency.getArtifact() + "-" + dependency.getVersion())
                        .collect(Collectors.toList());
        File[] files = downloadDirectory.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            if (isConnectorRemoved(file, existingConnectors)) {
                try {
                    Files.delete(file.toPath());
                    removeFromProjectIfUsingOldCARPlugin(projectPath, file.getName());
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, "Error occurred while deleting removed connector: " + file.getName());
                }
            }
        }
    }

    private static void removeFromProjectIfUsingOldCARPlugin(String projectPath, String name) throws IOException {

        if (!Utils.isOlderCARPlugin(projectPath)) {
            return;
        }
        File connectorInProject =
                Path.of(projectPath).resolve(TryOutConstants.PROJECT_CONNECTOR_PATH).resolve(name).toFile();
        if (connectorInProject.exists()) {
            Files.delete(connectorInProject.toPath());
        }
    }

    private static boolean isConnectorRemoved(File file, List<String> existingConnectors) {

        return file.isFile() && !existingConnectors.contains(file.getName().replace(Constant.ZIP_EXTENSION, ""));
    }

    private static File getDependencyFromLocalRepo(String groupId, String artifactId, String version) {

        String localMavenRepo = Path.of(System.getProperty(Constant.USER_HOME),  Constant.M2,
                Constant.REPOSITORY).toString();
        String artifactPath = Path.of(localMavenRepo, groupId.replace(".", File.separator), artifactId,
                version, artifactId + "-" + version + Constant.ZIP_EXTENSION).toString();
        File artifactFile = new File(artifactPath);
        if(artifactFile.exists()) {
            LOGGER.log(Level.INFO, "Dependency found in the local repository: " + artifactId);
            return artifactFile;
        } else {
            LOGGER.log(Level.INFO, "Dependency not found in the local repository: " + artifactId);
            return null;
        }
    }

    private static void copyFile(File source, File destinationFolder) throws IOException {

        if (!destinationFolder.exists()) {
            destinationFolder.mkdirs();
        }
        File destinationFile = Path.of(destinationFolder.getAbsolutePath(), source.getName()).toFile();
        try (InputStream in = new FileInputStream(source); OutputStream out = new FileOutputStream(destinationFile)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error occurred while copying dependency from local repository: " + e.getMessage());
            throw e;
        }
    }
}
