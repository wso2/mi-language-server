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

package org.eclipse.lemminx.synapse.overview;

import org.eclipse.lemminx.customservice.synapse.connectors.ConnectorHolder;
import org.eclipse.lemminx.customservice.synapse.dependency.tree.OverviewModelGenerator;
import org.eclipse.lemminx.customservice.synapse.dependency.tree.pojo.OverviewModel;
import org.eclipse.lemminx.customservice.synapse.resourceFinder.NewProjectResourceFinder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mockStatic;

public class OverviewModelGeneratorTest {

    private NewProjectResourceFinder newProjectResourceFinder;
    private OverviewModelGenerator overviewModelGenerator;
    private static MockedStatic<ConnectorHolder> connectorHolderMock;

    @BeforeEach
    void setUp() {
        newProjectResourceFinder = new NewProjectResourceFinder();
        overviewModelGenerator = new OverviewModelGenerator();
        connectorHolderMock = mockStatic(ConnectorHolder.class);
    }

    @Test
    void generateOverviewModelWithValidProjectPath() {
        String path = OverviewModelGeneratorTest.class.getResource("/synapse/resource.finder/test_project").getPath();
        String projectPath = new File(path).getAbsolutePath();
        connectorHolderMock.when(() -> ConnectorHolder.isValidConnector(any())).thenReturn(true);
        OverviewModel result = OverviewModelGenerator.getOverviewModel(projectPath);
        connectorHolderMock.close();

        assertEquals("test_project", result.getName());
        assertEquals(3, result.getEntrypoints().size());
        assertEquals(1, result.getConnections().size());
    }

    @Test
    void generateOverviewModelWithInvalidProjectPath() {
        String path = OverviewModelGeneratorTest.class.getResource("/synapse/resource.finder").getPath();
        String projectPath = new File(path).getAbsolutePath();
        connectorHolderMock.when(() -> ConnectorHolder.isValidConnector(any())).thenReturn(true);
        OverviewModel result = OverviewModelGenerator.getOverviewModel(projectPath);
        connectorHolderMock.close();

        assertEquals(0, result.getEntrypoints().size());
        assertEquals(0, result.getConnections().size());
    }
}
