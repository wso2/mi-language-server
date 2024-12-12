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
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.eclipse.lemminx.customservice.synapse.parser.pom.PomParser.getPomDetails;

public class ConnectorDownloadManager {

    private static final Logger LOGGER = Logger.getLogger(ConnectorDownloadManager.class.getName());

    public static String downloadConnectors(String projectPath) {

        File directory = Path.of(System.getProperty(Constant.USER_HOME), Constant.WSO2_MI, Constant.CONNECTORS,
                Utils.getHash(projectPath)).toFile();
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
                    downloadConnector(dependency.getGroupId(), dependency.getArtifact(),
                            dependency.getVersion(), downloadDirectory);
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error occurred while downloading dependencies: " + e.getMessage());
                return "Error occurred while downloading connectors.";
            }
        }
        return "Success";
    }

    private static void downloadConnector(String groupId, String artifactId, String version, File targetDirectory)
            throws IOException {

        if (!targetDirectory.exists()) {
            targetDirectory.mkdirs();
        }
        String url = String.format("https://maven.wso2.org/nexus/content/groups/public/%s/%s/%s/%s-%s.zip",
                groupId.replace(".", "/"), artifactId, version, artifactId, version);
        File targetFile = new File(targetDirectory, artifactId + "-" + version + Constant.ZIP_EXTENSION);
        try (BufferedInputStream in = new BufferedInputStream(new URL(url).openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(targetFile)) {
            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error occurred while downloading dependency: " + e.getMessage());
            throw e;
        }
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
