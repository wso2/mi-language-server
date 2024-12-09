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

package org.eclipse.lemminx.customservice.synapse.connectors;

import org.eclipse.lemminx.customservice.SynapseLanguageClientAPI;
import org.eclipse.lemminx.customservice.synapse.utils.Constant;
import org.eclipse.lemminx.customservice.synapse.utils.Utils;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

/**
 * Class to load connectors for old projects.
 */
public class OldProjectConnectorLoader extends AbstractConnectorLoader {

    public OldProjectConnectorLoader(SynapseLanguageClientAPI languageClient, ConnectorHolder connectorHolder) {

        super(languageClient, connectorHolder);
    }

    @Override
    protected File getConnectorExtractFolder() {

        File connectorsFolderPath = Path.of(System.getProperty(Constant.USER_HOME), Constant.WSO2_MI,
                Constant.CONNECTORS, Utils.getHash(this.getProjectUri()), Constant.EXTRACTED).toFile();
        return connectorsFolderPath;
    }

    @Override
    protected boolean canContinue(File connectorExtractFolder) {

        if (connectorExtractFolder.exists()) {
            return true;
        }
        return false;
    }

    @Override
    protected void cleanOldConnectors(File connectorExtractFolder, List<File> connectorZips) {

    }

    @Override
    protected void setConnectorsZipFolderPath(String projectRoot) {

        File projectFile = new File(projectRoot);
        File parentFolder = projectFile.getParentFile();
        String workspacePath = parentFolder.getAbsolutePath();
        connectorsZipFolderPath.add(Path.of(workspacePath, ".metadata", ".Connectors").toString());
    }
}
