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
import org.eclipse.lemminx.customservice.synapse.parser.OverviewPage;
import org.eclipse.lemminx.customservice.synapse.parser.OverviewPageDetailsResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.eclipse.lsp4j.WorkspaceFolder;
import org.mockito.MockedStatic;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;

public class OverviewPageTest {

    private OverviewPage overviewPage;
    private static MockedStatic<ConnectorHolder> connectorHolderMock;

    @BeforeEach
    void setUp() {
        overviewPage = new OverviewPage();
        connectorHolderMock = mockStatic(ConnectorHolder.class);
    }

    @Test
    void getDetailsWithValidProjectUri() {
        String path = OverviewPageTest.class.getResource("/synapse/resource.finder/test_project").getPath();
        String projectPath = new File(path).getAbsolutePath();
        OverviewPageDetailsResponse result = OverviewPage.getDetails(projectPath);
        connectorHolderMock.close();

        assertEquals("test", result.getPrimaryDetails().getProjectName().getValue());
        assertEquals(2, result.getConfigurables().size());
    }

    @Test
    void getDetailsWithInvalidProjectUri() {
        String path = OverviewPageTest.class.getResource("/synapse/resource.finder").getPath();
        String projectPath = new File(path).getAbsolutePath();
        OverviewPageDetailsResponse result = OverviewPage.getDetails(projectPath);
        connectorHolderMock.close();

        assertNull(result.getPrimaryDetails().getProjectName());
        assertEquals(0, result.getConfigurables().size());
    }

    @Test
    void getProjectIntegrationTypeWithValidProjectFolder() {
        String path = OverviewPageTest.class.getResource("/synapse/resource.finder/test_project").getPath();
        String projectPath = new File(path).getAbsolutePath();
        connectorHolderMock.when(() -> ConnectorHolder.isValidConnector(any())).thenReturn(true);
        List<String> result = OverviewPage.getProjectIntegrationType(new WorkspaceFolder(projectPath));
        List<String> expectedResult = Arrays.asList("INTEGRATION_AS_API", "EVENT_INTEGRATION", "AUTOMATION");
        connectorHolderMock.close();

        assertEquals(expectedResult, result);
        assertEquals(3, result.size());
    }

    @Test
    void getProjectIntegrationTypeWithInvalidProjectFolder() {
        String path = OverviewPageTest.class.getResource("/synapse/resource.finder").getPath();
        String projectPath = new File(path).getAbsolutePath();
        connectorHolderMock.when(() -> ConnectorHolder.isValidConnector(any())).thenReturn(true);
        List<String> result = OverviewPage.getProjectIntegrationType(new WorkspaceFolder(projectPath));
        connectorHolderMock.close();

        assertEquals(new ArrayList<>(), result);
        assertEquals(0, result.size());
    }
}
