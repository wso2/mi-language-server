/*
 *   Copyright (c) 2025, WSO2 LLC. (http://www.wso2.com).
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

import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.eclipse.lemminx.customservice.synapse.mediator.TryOutConstants;
import org.eclipse.lemminx.customservice.synapse.mediator.TryOutUtils;
import org.eclipse.lemminx.customservice.synapse.mediator.tryout.pojo.ArtifactDeploymentException;
import org.eclipse.lemminx.customservice.synapse.utils.Constant;
import org.eclipse.lemminx.customservice.synapse.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class CAPPCacheManager {

    private static final Logger LOGGER = Logger.getLogger(CAPPCacheManager.class.getName());
    private static final Path TRYOUT_CAPP_BUILD_TEMP =
            Path.of(System.getProperty("user.home")).resolve(".wso2-mi").resolve("tryout_capp_build_temp");

    public static void validateCAPPCache(String projectUri) throws ArtifactDeploymentException {

        try {
            if (!isValidCache(projectUri)) {
                buildDependencyCAPP(projectUri);
            }
        } catch (IOException | ArtifactDeploymentException e) {
            throw new ArtifactDeploymentException(TryOutConstants.BUILD_FAILURE_MESSAGE);
        }
    }

    private static boolean isValidCache(String projectUri) throws IOException {

        Path projectPath = Path.of(projectUri);
        Path pomPath = projectPath.resolve("pom.xml");
        Path javaDir = projectPath.resolve(Constant.SRC).resolve(Constant.MAIN).resolve("java");
        Path datamapperDir =
                projectPath.resolve(TryOutConstants.PROJECT_RESOURCES_RELATIVE_PATH).resolve("datamapper");

        String projectId = Utils.getHash(projectUri);
        Path projectCAPPPath = TryOutConstants.CAPP_CACHE_LOCATION.resolve(projectId);
        if (!Files.exists(projectCAPPPath)) {
            return Boolean.FALSE;
        }

        // Check pom changes for new connector dependencies
        FileTime pomLastModifiedTime = Files.getLastModifiedTime(pomPath);
        if (pomLastModifiedTime.compareTo(Files.getLastModifiedTime(projectCAPPPath)) > 0) {
            return Boolean.FALSE;
        }

        // Check for changes in the datamapper and java directories
        FileTime cappCreatedTime = Files.getLastModifiedTime(projectCAPPPath);
        boolean isDataMapperDirChanged = checkFileChanges(datamapperDir, cappCreatedTime);
        if (isDataMapperDirChanged) {
            return Boolean.FALSE;
        }
        return !checkFileChanges(javaDir, cappCreatedTime);
    }

    private static boolean checkFileChanges(Path javaDir, FileTime cappCreatedTime) {

        try {
            if (!Files.exists(javaDir) || !Files.isDirectory(javaDir)) {
                return Boolean.FALSE;
            }

            // Walk through all files in the directory and its subdirectories
            try (Stream<Path> walk = Files.walk(javaDir)) {
                return walk
                        .filter(Files::isRegularFile)
                        .anyMatch(file -> {
                            try {
                                FileTime lastModifiedTime =
                                        Files.readAttributes(file, BasicFileAttributes.class).lastModifiedTime();
                                return lastModifiedTime.compareTo(cappCreatedTime) > 0;
                            } catch (IOException e) {
                                LOGGER.log(Level.SEVERE, "Error reading file attributes for: " + file, e);
                                return Boolean.TRUE;
                            }
                        });
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error walking directory: " + javaDir, e);
            return Boolean.TRUE;
        }
    }

    private static void buildDependencyCAPP(String projectUri) throws IOException, ArtifactDeploymentException {

        TRYOUT_CAPP_BUILD_TEMP.toFile().mkdirs();
        Path projectPath = Path.of(projectUri);

        Utils.copyFolder(projectPath, TRYOUT_CAPP_BUILD_TEMP, null);

        Path artifactPath = TRYOUT_CAPP_BUILD_TEMP.resolve(TryOutConstants.PROJECT_ARTIFACT_PATH);
        Utils.deleteDirectory(artifactPath);

        Path resourcesPath = TRYOUT_CAPP_BUILD_TEMP.resolve(TryOutConstants.PROJECT_RESOURCES_RELATIVE_PATH);
        File resourcesDir = resourcesPath.toFile();
        File[] resourcesFolders = resourcesDir.listFiles();
        if (resourcesFolders != null) {
            for (File file : resourcesFolders) {
                if (file.isDirectory()) {
                    if (!Constant.DATA_MAPPER.equals(file.getName()) && !Constant.CONNECTORS.equals(file.getName())) {
                        Utils.deleteDirectory(file.toPath());
                    }
                } else {
                    file.delete();
                }
            }
        }

        Path targetPath = TRYOUT_CAPP_BUILD_TEMP.resolve(Constant.TARGET);
        Utils.deleteDirectory(targetPath);

        buildCapp(TRYOUT_CAPP_BUILD_TEMP);
        cacheCAPP(TRYOUT_CAPP_BUILD_TEMP, projectPath);
        Utils.deleteDirectory(TRYOUT_CAPP_BUILD_TEMP);
    }

    private static void buildCapp(Path tempProjectPath) {

        Invoker invoker = new DefaultInvoker();
        invoker.setMavenHome(tempProjectPath.toFile());
        File mvnwFile = tempProjectPath.resolve("mvnw").toFile();
        invoker.setMavenExecutable(mvnwFile);
        InvocationRequest request = new DefaultInvocationRequest();
        request.setQuiet(true);
        request.setPomFile(tempProjectPath.resolve("pom.xml").toFile());
        request.setGoals(List.of("clean", "install"));
        Properties properties = new Properties();
        properties.setProperty("maven.test.skip", "true");
        request.setProperties(properties);
        request.setJavaHome(new File(System.getProperty("java.home")));
        try {
            invoker.execute(request);
        } catch (MavenInvocationException e) {
            LOGGER.log(Level.SEVERE, "Error building the project", e);
        }
    }

    private static void cacheCAPP(Path tempProjectPath, Path projectPath) throws ArtifactDeploymentException {

        String projectId = Utils.getHash(projectPath.toString());
        String projectCAPPPath = TryOutConstants.CAPP_CACHE_LOCATION.resolve(projectId).toString();
        try {
            Path cappPath = findCappPath(tempProjectPath);
            Utils.copyFile(cappPath.toString(), projectCAPPPath);
        } catch (IOException | ArtifactDeploymentException e) {
            throw new ArtifactDeploymentException(TryOutConstants.BUILD_FAILURE_MESSAGE);
        }
    }

    private static Path findCappPath(Path tempProjectPath) throws ArtifactDeploymentException {

        Path targetPath = tempProjectPath.resolve(Constant.TARGET);
        return TryOutUtils.findCAPP(targetPath);
    }
}
