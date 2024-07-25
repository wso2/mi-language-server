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

package org.eclipse.lemminx.customservice.synapse.inbound.conector;

import org.eclipse.lemminx.customservice.synapse.utils.Utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InboundConnectorHolder {

    private static final Logger LOGGER = Logger.getLogger(InboundConnectorHolder.class.getName());
    private String projectId;
    private String projectPath;
    private String tempFolderPath;

    // Connector name ui schema path map
    private HashMap<String, String> inboundConnectors;

    public InboundConnectorHolder() {

        this.inboundConnectors = new HashMap<>();
    }

    public void init(String projectPath) {

        if (projectPath != null) {
            this.projectPath = projectPath;
            this.projectId = Utils.getHash(projectPath);
            this.tempFolderPath =
                    System.getProperty("user.home") + File.separator + ".wso2-mi" + File.separator + "inbound" +
                            ".connectors"
                            + File.separator + projectId;
            loadInboundConnectors();
        } else {
            LOGGER.log(Level.SEVERE, "Project path is null. Cannot initialize inbound connector holder.");
        }
    }

    private void loadInboundConnectors() {

        File folder = new File(tempFolderPath);
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    inboundConnectors.put(file.getName().replace(".json", ""), file.getAbsolutePath());
                }
            }
        }
    }

    public Boolean saveInboundConnector(String connectorName, String uiSchema) {

        // Save inbound connector
        Path filePath = Path.of(tempFolderPath, connectorName + ".json");
        if (saveToFile(filePath.toFile(), uiSchema)) {
            inboundConnectors.put(connectorName, filePath.toString());
            return true;
        }
        return false;
    }

    public InboundConnector getInboundConnectorSchema(String connectorName) {

        InboundConnector inboundConnector = new InboundConnector();
        inboundConnector.connectorName = connectorName;
        inboundConnector.uiSchemaPath = inboundConnectors.get(connectorName);

        return inboundConnector;
    }

    public boolean saveToFile(File file, String text) {

        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                int chunkSize = 8192; // 8KB chunks
                int length = text.length();
                for (int i = 0; i < length; i += chunkSize) {
                    if (i + chunkSize > length) {
                        writer.write(text.substring(i));
                    } else {
                        writer.write(text.substring(i, i + chunkSize));
                    }
                }
            }
            return Boolean.TRUE;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error occurred while saving inbound connector schema to file", e);
        }
        return Boolean.FALSE;
    }

    public void setProjectPath(String projectPath) {

        this.projectPath = projectPath;
    }
}
