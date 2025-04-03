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

package org.eclipse.lemminx.synapse.connector.downloader;

import org.eclipse.lemminx.customservice.synapse.parser.ConnectorDownloadManager;
import org.eclipse.lemminx.customservice.synapse.utils.Utils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mockStatic;

public class ConnectorDownloadManagerTest {

    private ConnectorDownloadManager connectorDownloadManager;
    private static MockedStatic<Utils> utilsMock;

    @BeforeEach
    void setUp() {
        connectorDownloadManager = new ConnectorDownloadManager();
        utilsMock = mockStatic(Utils.class);
    }

    @Test
    void downloadConnectorsWithValidDependencies() {
        String path = ConnectorDownloadManagerTest.class.getResource("/synapse/pom.parser/test_pom_parser").getPath();
        String projectPath = new File(path).getAbsolutePath();
        utilsMock.when(() -> Utils.downloadConnector(any(), any(), any(), any())).thenAnswer(invocationOnMock -> { return null; });
        String result = connectorDownloadManager.downloadConnectors(projectPath);
        utilsMock.close();

        assertEquals("Success", result);
    }

    @Test
    void downloadConnectorsWithInvalidDependencies() {
        String path = ConnectorDownloadManagerTest.class.getResource("/synapse/pom.parser/test_pom_parser").getPath();
        String projectPath = new File(path).getAbsolutePath();
        utilsMock.when(() -> Utils.downloadConnector(any(), any(), any(), any())).thenThrow(new IOException());
        String result = connectorDownloadManager.downloadConnectors(projectPath);
        utilsMock.close();

        assertTrue(result.contains("Some connectors were not downloaded:"));
    }
}
