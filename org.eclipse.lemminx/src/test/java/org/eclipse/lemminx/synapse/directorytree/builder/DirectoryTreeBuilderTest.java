/*
 * Copyright (c) 2025, WSO2 LLC. (http://www.wso2.com).
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

package org.eclipse.lemminx.synapse.directorytree.builder;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.eclipse.lemminx.customservice.synapse.connectors.ConnectorHolder;
import org.eclipse.lemminx.customservice.synapse.directoryTree.DirectoryMapResponse;
import org.eclipse.lemminx.customservice.synapse.directoryTree.DirectoryTreeBuilder;
import org.eclipse.lsp4j.WorkspaceFolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mockStatic;

public class DirectoryTreeBuilderTest {

    private DirectoryTreeBuilder directoryTreeBuilder;
    private static MockedStatic<ConnectorHolder> connectorHolderMock;

    @BeforeEach
    void setUp() {
        directoryTreeBuilder = new DirectoryTreeBuilder();
        connectorHolderMock = mockStatic(ConnectorHolder.class);
    }

    @Test
    void buildDirectoryTreeWithValidProjectFolder() throws Exception {
        String path = DirectoryTreeBuilderTest.class.getResource("/synapse/resource.finder/test_project").getPath();
        String projectPath = new File(path).getAbsolutePath();
        connectorHolderMock.when(() -> ConnectorHolder.isValidConnector(any())).thenReturn(true);
        path = DirectoryTreeBuilderTest.class.getResource("/synapse/directorytree.builder/generated-directory-tree.json").getPath();
        String expectedResultPath = new File(path).getAbsolutePath();
        String expectedResult = Files.readString(Paths.get(expectedResultPath));
        DirectoryMapResponse result = DirectoryTreeBuilder.buildDirectoryTree(new WorkspaceFolder(projectPath));
        connectorHolderMock.close();

        assertEquals(expectedResult, sanitizeJson(result.getDirectoryMap().getAsJsonObject()).toString());
    }

    @Test
    void buildDirectoryTreeWithInvalidValidProjectFolder() {
        String path = DirectoryTreeBuilderTest.class.getResource("/synapse/resource.finder").getPath();
        String projectPath = new File(path).getAbsolutePath();
        DirectoryMapResponse result = DirectoryTreeBuilder.buildDirectoryTree(new WorkspaceFolder(projectPath));
        connectorHolderMock.close();

        assertNull(result.getDirectoryMap());
    }

    @Test
    void getProjectExplorerModelWithValidProjectFolder() throws Exception {
        String path = DirectoryTreeBuilderTest.class.getResource("/synapse/resource.finder/test_project").getPath();
        String projectPath = new File(path).getAbsolutePath();
        connectorHolderMock.when(() -> ConnectorHolder.isValidConnector(any())).thenReturn(true);
        path = DirectoryTreeBuilderTest.class.getResource("/synapse/directorytree.builder/generated-project-explorer.json").getPath();
        String expectedResultPath = new File(path).getAbsolutePath();
        String expectedResult = Files.readString(Paths.get(expectedResultPath));
        DirectoryMapResponse result = DirectoryTreeBuilder.getProjectExplorerModel(new WorkspaceFolder(projectPath));
        connectorHolderMock.close();

        assertEquals(expectedResult, sanitizeJson(result.getDirectoryMap().getAsJsonObject()).toString());
    }

    @Test
    void getProjectExplorerModelWithInvalidProjectFolder() {
        String path = DirectoryTreeBuilderTest.class.getResource("/synapse/resource.finder").getPath();
        String projectPath = new File(path).getAbsolutePath();
        DirectoryMapResponse result = DirectoryTreeBuilder.getProjectExplorerModel(new WorkspaceFolder(projectPath));
        connectorHolderMock.close();

        assertNull(result);
    }

    @Test
    void getProjectIdentifiersWithArtifactList() {
        String resourcePath = "/synapse/resource.finder/test_project";
        String projectPath = new File(DirectoryTreeBuilderTest.class.getResource(resourcePath).getPath()).getAbsolutePath();
        String apiPath = new File(DirectoryTreeBuilderTest.class.getResource(resourcePath + "/src/main/wso2mi/artifacts/apis/testApi.xml").
                getPath()).getAbsolutePath();
        String sequencePath = new File(DirectoryTreeBuilderTest.class.getResource(resourcePath + "/src/main/wso2mi/artifacts/sequences/testSequence1.xml")
                .getPath()).getAbsolutePath();
        List<String> artifactIdentifierList = Arrays.asList("apis/testApi", "sequences/testSequence1");
        List<String> result = DirectoryTreeBuilder.getProjectIdentifiers(new WorkspaceFolder(projectPath),
                Arrays.asList(apiPath, sequencePath));
        connectorHolderMock.close();

        assertEquals(artifactIdentifierList, result);
    }

    @Test
    void getProjectIdentifiersWithEmptyArtifactList() {
        String path = DirectoryTreeBuilderTest.class.getResource("/synapse/resource.finder/test_project").getPath();
        String projectPath = new File(path).getAbsolutePath();
        List<String> result = DirectoryTreeBuilder.getProjectIdentifiers(new WorkspaceFolder(projectPath), new ArrayList<>());
        connectorHolderMock.close();

        assertTrue(result.isEmpty());
    }

    private static JsonObject sanitizeJson(JsonObject jsonObject) {
        JsonObject sanitizedJson = new JsonObject();
        for (String key : jsonObject.keySet()) {
            if (!(key.equals("path") || key.equals("registryPath"))) {
                JsonElement value = jsonObject.get(key);
                if (value.isJsonObject()) {
                    sanitizedJson.add(key, sanitizeJson(value.getAsJsonObject()));
                } else if (value.isJsonArray()) {
                    sanitizedJson.add(key, sanitizeJson(value.getAsJsonArray()));
                } else {
                    sanitizedJson.add(key, value);
                }
            }
        }
        return sanitizedJson;
    }

    private static JsonArray sanitizeJson(JsonArray jsonArray) {
        JsonArray sanitizedArray = new JsonArray();
        for (JsonElement element : jsonArray) {
            if (element.isJsonObject()) {
                sanitizedArray.add(sanitizeJson(element.getAsJsonObject()));
            } else if (element.isJsonArray()) {
                sanitizedArray.add(sanitizeJson(element.getAsJsonArray()));
            } else {
                sanitizedArray.add(element);
            }
        }
        return sanitizedArray;
    }
}
