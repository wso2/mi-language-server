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

import org.eclipse.lemminx.customservice.synapse.utils.Constant;
import org.eclipse.lemminx.customservice.synapse.utils.Utils;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.eclipse.lemminx.customservice.synapse.parser.pom.PomParser.getPomDetails;

/**
 * Manages the downloading of project dependencies defined in a Maven pom.xml file.
 * Handles both connector and integration project dependencies, logging failures if any occur.
 */
public class DependencyDownloadManager {

    private static final Logger LOGGER = Logger.getLogger(ConnectorDownloadManager.class.getName());

    /**
     * Downloads the dependencies specified in the pom.xml file of the given project.
     *
     * @param projectPath The path to the project directory containing the pom.xml file.
     * @return A message indicating the success or failure of the download operation.
     */
    public static String downloadDependencies(String projectPath) {

        StringBuilder errorMessage = new StringBuilder();
        boolean hasErrors = false;
        OverviewPageDetailsResponse pomDetailsResponse = new OverviewPageDetailsResponse();
        getPomDetails(projectPath, pomDetailsResponse);
        List<DependencyDetails> connectorDependencies =
                pomDetailsResponse.getDependenciesDetails().getConnectorDependencies();
        List<DependencyDetails> integrationProjectDependencies =
                pomDetailsResponse.getDependenciesDetails().getIntegrationProjectDependencies();
        List<String> failedConnectorDependencies =
                ConnectorDownloadManager.downloadDependencies(projectPath, connectorDependencies);
        Node isVersionedDeployment = pomDetailsResponse.getBuildDetails().getVersionedDeployment();
        boolean isVersionedDeploymentEnabled = isVersionedDeployment != null ?
                Boolean.parseBoolean(isVersionedDeployment.getValue()) : false;
        DependencyDownloadResult failedIntegrationProjectDependencies =
                IntegrationProjectDownloadManager.downloadDependencies(projectPath, integrationProjectDependencies,
                        isVersionedDeploymentEnabled);

        if (!failedConnectorDependencies.isEmpty()) {
            String connectorError = "Some connectors were not downloaded: " + String.join(", ", failedConnectorDependencies);
            LOGGER.log(Level.SEVERE, connectorError);
            errorMessage.append(connectorError);
            hasErrors = true;
        }

        if (!failedIntegrationProjectDependencies.getFailedDependencies().isEmpty()) {
            String projectError = "Following integration project dependencies were unavailable: " +
                    String.join(", ", failedIntegrationProjectDependencies.getFailedDependencies());
            LOGGER.log(Level.SEVERE, projectError);
            if (hasErrors) {
                errorMessage.append(". ");
            }
            errorMessage.append(projectError);
            hasErrors = true;
        }

        if (!failedIntegrationProjectDependencies.getNoDescriptorDependencies().isEmpty()) {
            String descriptorError = "Following dependencies do not contain the descriptor file: " +
                    String.join(", ", failedIntegrationProjectDependencies.getNoDescriptorDependencies());
            LOGGER.log(Level.SEVERE, descriptorError);
            if (hasErrors) {
                errorMessage.append(". ");
            }
            errorMessage.append(descriptorError);
            hasErrors = true;
        }

        if (!failedIntegrationProjectDependencies.getVersioningTypeMismatchDependencies().isEmpty()) {
            String versioningTypeError = "Versioned deployment status is different from the dependent project: " +
                    String.join(", ", failedIntegrationProjectDependencies.getVersioningTypeMismatchDependencies());
            LOGGER.log(Level.SEVERE, versioningTypeError);
            if (hasErrors) {
                errorMessage.append(". ");
            }
            errorMessage.append(versioningTypeError);
            hasErrors = true;
        }

        if (hasErrors) {
            return errorMessage.toString();
        }
        LOGGER.log(Level.INFO, "All dependencies downloaded successfully for project: " + projectPath);
        return "Success";
    }

    /**
     * Clears the Downloaded and Extracted directories and re-fetches all integration project
     * dependencies from scratch for the given project.
     *
     * @param projectPath The path to the project directory containing the pom.xml file.
     * @return A message indicating the success or failure of the re-fetch operation.
     */
    public static String refetchIntegrationProjectDependencies(String projectPath) {

        OverviewPageDetailsResponse pomDetailsResponse = new OverviewPageDetailsResponse();
        getPomDetails(projectPath, pomDetailsResponse);
        List<DependencyDetails> integrationProjectDependencies =
                pomDetailsResponse.getDependenciesDetails().getIntegrationProjectDependencies();
        Node isVersionedDeployment = pomDetailsResponse.getBuildDetails().getVersionedDeployment();
        boolean isVersionedDeploymentEnabled = isVersionedDeployment != null ?
                Boolean.parseBoolean(isVersionedDeployment.getValue()) : false;
        DependencyDownloadResult result =
                IntegrationProjectDownloadManager.refetchDependencies(projectPath, integrationProjectDependencies,
                        isVersionedDeploymentEnabled);

        return buildResultMessage(result, projectPath);
    }

    /**
     * Builds a human-readable result message from a {@link DependencyDownloadResult},
     * logging and concatenating each category of failure. Returns {@code "Success"} if
     * there were no failures.
     */
    private static String buildResultMessage(DependencyDownloadResult result, String projectPath) {

        StringBuilder errorMessage = new StringBuilder();
        boolean hasErrors = false;

        if (!result.getFailedDependencies().isEmpty()) {
            String projectError = "Following integration project dependencies were unavailable: " +
                    String.join(", ", result.getFailedDependencies());
            LOGGER.log(Level.SEVERE, projectError);
            errorMessage.append(projectError);
            hasErrors = true;
        }

        if (!result.getNoDescriptorDependencies().isEmpty()) {
            String descriptorError = "Following dependencies do not contain the descriptor file: " +
                    String.join(", ", result.getNoDescriptorDependencies());
            LOGGER.log(Level.SEVERE, descriptorError);
            if (hasErrors) {
                errorMessage.append(". ");
            }
            errorMessage.append(descriptorError);
            hasErrors = true;
        }

        if (!result.getVersioningTypeMismatchDependencies().isEmpty()) {
            String versioningTypeError = "Versioned deployment status is different from the dependent project: " +
                    String.join(", ", result.getVersioningTypeMismatchDependencies());
            LOGGER.log(Level.SEVERE, versioningTypeError);
            if (hasErrors) {
                errorMessage.append(". ");
            }
            errorMessage.append(versioningTypeError);
            hasErrors = true;
        }

        if (hasErrors) {
            return errorMessage.toString();
        }
        LOGGER.log(Level.INFO, "Integration project dependencies downloaded successfully for project: " + projectPath);
        return "Success";
    }

    public static DependencyStatusResponse getDependencyStatusList(String projectPath) {
        OverviewPageDetailsResponse pomDetailsResponse = new OverviewPageDetailsResponse();
        getPomDetails(projectPath, pomDetailsResponse);
        List<DependencyDetails> dependencies = new ArrayList<>(
                pomDetailsResponse.getDependenciesDetails().getConnectorDependencies()
        );
        dependencies.addAll(pomDetailsResponse.getDependenciesDetails().getIntegrationProjectDependencies());
        List<DependencyDetails> downloadedDependencies = new ArrayList<>();
        List<DependencyDetails> pendingDependencies = new ArrayList<>();
        String projectId = new File(projectPath).getName() + "_" + Utils.getHash(projectPath);
        File directory = Path.of(System.getProperty(Constant.USER_HOME), Constant.WSO2_MI, Constant.CONNECTORS,
                projectId).toFile();
        File downloadDirectory = Path.of(directory.getAbsolutePath(), Constant.DOWNLOADED).toFile();
        for (DependencyDetails dependency : dependencies) {
            File connector = Path.of(downloadDirectory.getAbsolutePath(),
                    dependency.getArtifact() + Constant.HYPHEN + dependency.getVersion() + Constant.DOT + dependency.getType()).toFile();
            if (connector.exists() && connector.isFile()) {
                downloadedDependencies.add(dependency);
            } else {
                pendingDependencies.add(dependency);
            }
        }
        return new DependencyStatusResponse(downloadedDependencies, pendingDependencies);
    }
}
