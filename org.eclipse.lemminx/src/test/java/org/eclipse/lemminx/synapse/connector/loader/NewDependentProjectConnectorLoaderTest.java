/*
 * Copyright (c) 2026, WSO2 LLC. (http://www.wso2.com).
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

package org.eclipse.lemminx.synapse.connector.loader;

import org.eclipse.lemminx.MockXMLLanguageClient;
import org.eclipse.lemminx.customservice.SynapseLanguageClientAPI;
import org.eclipse.lemminx.customservice.synapse.connectors.ConnectorHolder;
import org.eclipse.lemminx.customservice.synapse.connectors.NewProjectConnectorLoader;
import org.eclipse.lemminx.customservice.synapse.inbound.conector.InboundConnectorHolder;
import org.eclipse.lemminx.customservice.synapse.utils.Constant;
import org.eclipse.lemminx.customservice.synapse.utils.Utils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

import static org.eclipse.lemminx.synapse.TestUtils.getResourceFilePath;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for loading connectors from dependency integration projects
 * in {@link NewProjectConnectorLoader}.
 */
public class NewDependentProjectConnectorLoaderTest {

    private static final Logger LOGGER = Logger.getLogger(NewDependentProjectConnectorLoaderTest.class.getName());

    /**
     * Minimal subclass that exposes {@code connectorsZipFolderPath} for assertions
     * and skips the copy-to-project step which is irrelevant for dependency loading.
     */
    static class TestableLoader extends NewProjectConnectorLoader {

        private final String userHome;

        TestableLoader(SynapseLanguageClientAPI client, ConnectorHolder holder,
                       InboundConnectorHolder inboundHolder, String userHome) {
            super(client, holder, inboundHolder);
            this.userHome = userHome;
        }

        @Override
        protected String getUserHome() {
            return userHome;
        }

        List<String> getConnectorsZipFolderPaths() {
            return connectorsZipFolderPath;
        }

        @Override
        protected void copyToProjectIfNeeded(List<File> connectorZips) {
            // no-op — not relevant for dependency loading tests
        }
    }

    private static final String CONNECTOR_RELATIVE_PATH =
            Constant.SRC + File.separator + Constant.MAIN + File.separator +
            Constant.WSO2MI + File.separator + Constant.RESOURCES + File.separator + Constant.CONNECTORS;

    private Path tempHome;
    private Path projectRoot;
    private TestableLoader loader;

    @BeforeEach
    public void setUp() throws IOException {

        LOGGER.info("Setting up test environment for NewDependentProjectConnectorLoaderTest");
        tempHome = Files.createTempDirectory("mi-test-home-");

        // Create a minimal valid project root (requires pom.xml and src/)
        projectRoot = Files.createTempDirectory("mi-test-project-");
        Files.createFile(projectRoot.resolve("pom.xml"));
        Files.createDirectory(projectRoot.resolve("src"));

        ConnectorHolder connectorHolder = ConnectorHolder.getInstance();
        connectorHolder.clearConnectors();
        InboundConnectorHolder inboundConnectorHolder = new InboundConnectorHolder();
        SynapseLanguageClientAPI languageClient = new MockXMLLanguageClient();

        loader = new TestableLoader(languageClient, connectorHolder, inboundConnectorHolder, tempHome.toString());
    }

    @AfterEach
    public void tearDown() throws IOException {

        LOGGER.info("Tearing down test environment for NewDependentProjectConnectorLoaderTest");
        deleteRecursively(tempHome);
        deleteRecursively(projectRoot);
    }

    /**
     * When there is no dependency extracted directory at all,
     * only the two base connector paths (project source and downloaded) should be present.
     */
    @Test
    public void testNoDependencyExtractedDir_onlyBasePathsAdded() throws Exception {

        loader.init(projectRoot.toString());

        List<String> paths = loader.getConnectorsZipFolderPaths();
        assertEquals(2, paths.size(),
                "Only the two base paths should be present when no dependency dir exists");
    }

    /**
     * When the dependency extracted directory exists but contains no sub-directories,
     * no additional paths should be added.
     */
    @Test
    public void testEmptyDependencyExtractedDir_onlyBasePathsAdded() throws Exception {

        createDependencyExtractedDir();

        loader.init(projectRoot.toString());

        List<String> paths = loader.getConnectorsZipFolderPaths();
        assertEquals(2, paths.size(),
                "No extra paths should be added for an empty dependency extracted dir");
    }

    /**
     * When a dependency project has a connectors directory, that path should be added.
     */
    @Test
    public void testDependencyProjectWithConnectors_pathAdded() throws Exception {

        Path extractedDir = createDependencyExtractedDir();
        Path depProject = extractedDir.resolve("dep-project-A");
        Path connectorDir = depProject.resolve(CONNECTOR_RELATIVE_PATH);
        Files.createDirectories(connectorDir);

        loader.init(projectRoot.toString());
        loader.loadConnector();

        List<String> paths = loader.getConnectorsZipFolderPaths();
        assertEquals(3, paths.size(),
                "The dependency project's connector path should be added");
        assertTrue(paths.contains(connectorDir.toString()),
                "Expected connector path not found: " + connectorDir);
    }

    /**
     * When a dependency project exists but does not have a connectors directory,
     * no extra path should be added for it.
     */
    @Test
    public void testDependencyProjectWithoutConnectors_pathNotAdded() throws Exception {

        Path extractedDir = createDependencyExtractedDir();
        // Dependency project with some structure but no connectors dir
        Files.createDirectories(extractedDir.resolve("dep-project-B").resolve("src").resolve("main"));

        loader.init(projectRoot.toString());
        loader.loadConnector();

        List<String> paths = loader.getConnectorsZipFolderPaths();
        assertEquals(2, paths.size(),
                "No extra path should be added when dependency project has no connectors dir");
    }

    /**
     * When multiple dependency projects exist, only those that have a connectors directory
     * should contribute a path.
     */
    @Test
    public void testMultipleDependencyProjects_onlyThoseWithConnectorsAdded() throws Exception {

        Path extractedDir = createDependencyExtractedDir();

        // Project with connectors dir
        Path depWithConnectors = extractedDir.resolve("dep-with-connectors");
        Path connectorDir = depWithConnectors.resolve(CONNECTOR_RELATIVE_PATH);
        Files.createDirectories(connectorDir);

        // Project without connectors dir
        Files.createDirectories(extractedDir.resolve("dep-without-connectors").resolve("src"));

        loader.init(projectRoot.toString());
        loader.loadConnector();

        List<String> paths = loader.getConnectorsZipFolderPaths();
        assertEquals(3, paths.size(),
                "Only the dependency project with a connectors dir should add a path");
        assertTrue(paths.contains(connectorDir.toString()),
                "Expected connector path not found: " + connectorDir);
    }

    /**
     * When there are multiple dependency projects all having connectors directories,
     * all their paths should be added.
     */
    @Test
    public void testMultipleDependencyProjects_allWithConnectors_allPathsAdded() throws Exception {

        Path extractedDir = createDependencyExtractedDir();

        Path connectorDirA = extractedDir.resolve("dep-A").resolve(CONNECTOR_RELATIVE_PATH);
        Path connectorDirB = extractedDir.resolve("dep-B").resolve(CONNECTOR_RELATIVE_PATH);
        Files.createDirectories(connectorDirA);
        Files.createDirectories(connectorDirB);

        loader.init(projectRoot.toString());
        loader.loadConnector();

        List<String> paths = loader.getConnectorsZipFolderPaths();
        assertEquals(4, paths.size(),
                "Both dependency connector paths should be added");
        assertTrue(paths.contains(connectorDirA.toString()));
        assertTrue(paths.contains(connectorDirB.toString()));
    }

    /**
     * {@code loadConnector()} refreshes dependency paths on each call. After adding a new
     * dependency project's connector directory, a subsequent {@code loadConnector()} call
     * should pick it up.
     */
    @Test
    public void testLoadConnector_refreshesDependencyPaths() throws Exception {

        loader.init(projectRoot.toString());

        // Initially no dependency dir — only 2 base paths
        assertEquals(2, loader.getConnectorsZipFolderPaths().size());

        // Now add a dependency project with connectors
        Path extractedDir = createDependencyExtractedDir();
        Path connectorDir = extractedDir.resolve("new-dep").resolve(CONNECTOR_RELATIVE_PATH);
        Files.createDirectories(connectorDir);

        loader.loadConnector();

        List<String> paths = loader.getConnectorsZipFolderPaths();
        assertEquals(3, paths.size(),
                "loadConnector() should re-scan and pick up the newly added dependency connector path");
        assertTrue(paths.contains(connectorDir.toString()));
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /**
     * Creates the dependency extracted directory for the current project under the temp home,
     * mirroring the path that {@link NewProjectConnectorLoader} expects.
     */
    private Path createDependencyExtractedDir() throws IOException {

        String projectName = projectRoot.toFile().getName();
        String hash = Utils.getHash(projectRoot.toString());
        String projectId = projectName + "_" + hash;

        Path extractedDir = tempHome.resolve(Constant.WSO2_MI)
                .resolve(Constant.INTEGRATION_PROJECT_DEPENDENCIES)
                .resolve(projectId)
                .resolve(Constant.EXTRACTED);
        Files.createDirectories(extractedDir);
        return extractedDir;
    }

    private static void deleteRecursively(Path path) throws IOException {

        if (path == null || !Files.exists(path)) {
            return;
        }
        Files.walk(path)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
    }
}
