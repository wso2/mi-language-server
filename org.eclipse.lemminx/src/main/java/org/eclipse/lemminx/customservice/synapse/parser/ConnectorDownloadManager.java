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

package org.eclipse.lemminx.customservice.synapse.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.lemminx.customservice.synapse.connectors.ConnectorHolder;
import org.eclipse.lemminx.customservice.synapse.connectors.entity.Connector;
import org.eclipse.lemminx.customservice.synapse.mediator.TryOutConstants;
import org.eclipse.lemminx.customservice.synapse.parser.connectorConfig.ConnectorConfigService;
import org.eclipse.lemminx.customservice.synapse.parser.connectorConfig.DependencyOverride;
import org.eclipse.lemminx.customservice.synapse.utils.Constant;
import org.eclipse.lemminx.customservice.synapse.utils.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static org.eclipse.lemminx.customservice.synapse.utils.Utils.copyFile;
import static org.eclipse.lemminx.customservice.synapse.utils.Utils.getDependencyFromLocalRepo;

public class ConnectorDownloadManager {

    private static final Logger LOGGER = Logger.getLogger(ConnectorDownloadManager.class.getName());

    public static List<String> downloadDependencies(String projectPath, List<DependencyDetails> dependencies) {

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
                        dependency.getArtifact(), dependency.getVersion(), dependency.getType())) != null ) {
                    LOGGER.log(Level.INFO, "Copying dependency from local repository: " + connector.getName());
                    copyFile(existingArtifact.getPath(), downloadDirectory.getPath());
                } else {
                    LOGGER.log(Level.INFO, "Downloading dependency: " + connector.getName());
                    Utils.downloadConnector(dependency.getGroupId(), dependency.getArtifact(),
                            dependency.getVersion(), downloadDirectory, Constant.ZIP_EXTENSION_NO_DOT, projectPath);
                }
            } catch (Exception e) {
                String failedDependency = dependency.getGroupId() + "-" + dependency.getArtifact() + "-" + dependency.getVersion();
                LOGGER.log(Level.WARNING, "Error occurred while downloading dependency " + failedDependency + ": " + e.getMessage());
                failedDependencies.add(failedDependency);
            }
        }
        return failedDependencies;
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

     /**
      * Downloads the driver JAR for a specific connector and connection type by parsing the descriptor.yml file
      */
     public static String downloadDriverForConnector(String projectPath, String connectorName, String connectionType) {
         try {
 
             ConnectorHolder connectorHolder;
             connectorHolder = ConnectorHolder.getInstance();
             Connector connector = connectorHolder.getConnector(connectorName);
 
             String projectId = new File(projectPath).getName() + "_" + Utils.getHash(projectPath);
             File connectorsDirectory = Path.of(System.getProperty(Constant.USER_HOME), Constant.WSO2_MI,
                     Constant.CONNECTORS, projectId, Constant.EXTRACTED).toFile();
 
             if (!connectorsDirectory.exists() || !connectorsDirectory.isDirectory()) {
                 LOGGER.log(Level.SEVERE,
                         "Connectors directory does not exist: " + connectorsDirectory.getAbsolutePath());
                 return null;
             }
 
             String connectorPath = connector.getExtractedConnectorPath();
             File connectorDirectory = Path.of(connectorPath).toFile();
             if (!connectorDirectory.exists() || !connectorDirectory.isDirectory()) {
                 LOGGER.log(Level.SEVERE, "Connector directory does not exist: " + connectorDirectory.getAbsolutePath());
                 return null;
             }
 
             // Read descriptor.yml from the connector folder
             File descriptorFile = new File(connectorDirectory, Constant.DESCRIPTOR_FILE);
             if (!descriptorFile.exists()) {
                 LOGGER.log(Level.SEVERE, "descriptor.yml not found in connector: " + connectorName);
                 return null;
             }
 
             Map<String, Object> descriptorData;
             try (InputStream inputStream = new FileInputStream(descriptorFile)) {
                 ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
                 descriptorData = yamlMapper.readValue(inputStream, Map.class);
             } catch (IOException e) {
                 LOGGER.log(Level.SEVERE, "Error reading descriptor.yml: " + e.getMessage());
                 return null;
             }
 
             // Find the driver info that matches the connection type
             Map<String, Object> driverInfo = findDriverForConnectionType(descriptorData, connectionType);
             if (driverInfo == null) {
                 LOGGER.log(Level.SEVERE, "No driver found for connection type: " + connectionType);
                 return null;
             }
 
             // Check global/connector-level omitAllDrivers before reading coordinates
             if (ConnectorConfigService.isOmitAllDrivers(projectPath, connectorName)) {
                 LOGGER.log(Level.INFO, "All drivers for " + connectorName
                         + " are omitted via connector-config.json (omitAllDrivers); skipping download.");
                 return null;
             }

             // Extract driver coordinates from descriptor.yml
             String groupId = (String) driverInfo.get(Constant.GROUP_ID_KEY);
             String artifactId = (String) driverInfo.get(Constant.ARTIFACT_ID_KEY);
             String version = (String) driverInfo.get(Constant.VERSION_KEY);

             // Apply any override from connector-config.json so the IDE uses the same
             // driver version that the build will pack into the CAR.
             DependencyOverride override = ConnectorConfigService.findOverrideByConnectorNameAndConnectionType(
                     projectPath, connectorName, connectionType);
             if (override != null) {
                 if (Boolean.TRUE.equals(override.omit)) {
                     LOGGER.log(Level.INFO, "Driver for " + connectorName + "/" + connectionType
                             + " is omitted via connector-config.json; skipping download.");
                     return null;
                 }
                 if (!StringUtils.isBlank(override.groupId)) {
                     groupId = override.groupId;
                 }
                 if (!StringUtils.isBlank(override.artifactId)) {
                     artifactId = override.artifactId;
                 }
                 if (!StringUtils.isBlank(override.version)) {
                     LOGGER.log(Level.INFO, "Overriding driver version for " + connectorName + "/"
                             + connectionType + " from " + version + " to " + override.version
                             + " as per connector-config.json.");
                     version = override.version;
                 }
             }

             // Validate coordinates after overrides have been applied
             if (StringUtils.isAnyBlank(groupId, artifactId, version)) {
                 LOGGER.log(Level.SEVERE, "Invalid driver coordinates in descriptor");
                 return null;
             }

             // Create temp drivers directory if it doesn't exist
             File driversDirectory = Path.of(System.getProperty(Constant.USER_HOME), Constant.WSO2_MI,
                     Constant.CONNECTORS, projectId, Constant.DRIVERS).toFile();
             if (!driversDirectory.exists()) {
                 driversDirectory.mkdirs();
             }
 
             // Check if driver already exists
             File driverFile = new File(driversDirectory, artifactId + "-" + version + Constant.JAR_EXTENSION);
             if (driverFile.exists()) {
                 LOGGER.log(Level.INFO, "Driver already exists: " + driverFile.getAbsolutePath());
                 return driverFile.getAbsolutePath();
             }
 
             // Try to find the driver in local Maven repository first
             File localDriverFile = getDriverFromLocalRepo(groupId, artifactId, version);
             if (localDriverFile != null) {
                 copyFile(localDriverFile.getPath(), driversDirectory.getPath());
                 return new File(driversDirectory, localDriverFile.getName()).getAbsolutePath();
             }
 
             // Download the driver from Maven repository
             Utils.downloadConnector(groupId, artifactId, version, driversDirectory, Constant.JAR_EXTENSION_NO_DOT, projectPath);

            File expectedDriverFile = new File(driversDirectory, artifactId + "-" + version + Constant.JAR_EXTENSION);

            // Verify the driver file exists after the download attempt
            if (!expectedDriverFile.exists() || !expectedDriverFile.isFile()) {
                LOGGER.log(Level.SEVERE, "Driver JAR not found after calling Utils.downloadConnector: "
                        + expectedDriverFile.getAbsolutePath());
                        
                throw new IOException("Failed to download or locate driver file: " + expectedDriverFile.getName());
            }

            LOGGER.log(Level.INFO, "Driver downloaded " + expectedDriverFile.getAbsolutePath());
            return expectedDriverFile.getAbsolutePath();

            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "IOException occurred while downloading driver: " + e.getMessage());
                return null;
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error while downloading driver: " + e.getMessage());
                return null;
            }
     }
 
     /**
      * Finds driver info for the specified connection type from the descriptor data
      */
     private static Map<String, Object> findDriverForConnectionType(Map<String, Object> descriptorData,
             String connectionType) {
         try {
 
             Object dependenciesObj = descriptorData.get("dependencies");
             if (dependenciesObj != null && dependenciesObj instanceof List) {
                 List<Map<String, Object>> dependencies = (List<Map<String, Object>>) dependenciesObj;
                 Map<String, Object> exactMatch = null;
 
                 for (Map<String, Object> dependency : dependencies) {
                     Object depConnType = dependency.get("connectionType");
                     if (depConnType != null && connectionType.equalsIgnoreCase(depConnType.toString())) {
                         exactMatch = dependency;
                         break;
                     }
                 }
 
                 if (exactMatch != null) {
                     return exactMatch;
                 }
             }
 
             LOGGER.log(Level.WARNING, "No driver found for connection type: " + connectionType);
             return null;
         } catch (Exception e) {
             LOGGER.log(Level.SEVERE, "Error finding driver for connection type: " + e.getMessage());
             return null;
         }
     }
 
     /**
      * Gets driver JAR from local Maven repository
      */
     private static File getDriverFromLocalRepo(String groupId, String artifactId, String version) {
         String localMavenRepo = Path.of(System.getProperty(Constant.USER_HOME), Constant.M2,
                 Constant.REPOSITORY).toString();
         String artifactPath = Path.of(localMavenRepo, groupId.replace(".", File.separator), artifactId,
                 version, artifactId + "-" + version + Constant.JAR_EXTENSION).toString();
         File artifactFile = new File(artifactPath);
 
         if (artifactFile.exists()) {
             LOGGER.log(Level.INFO, "Driver found in local repository: " + artifactId);
             return artifactFile;
         } else {
             LOGGER.log(Level.INFO, "Driver not found in local repository: " + artifactId);
             return null;
         }
     }
}
