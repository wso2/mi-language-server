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

import com.github.fge.jackson.JsonLoader;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.SyntaxTreeGenerator;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.inbound.InboundEndpoint;
import org.eclipse.lemminx.customservice.synapse.utils.Constant;
import org.eclipse.lemminx.customservice.synapse.utils.UISchemaMapper;
import org.eclipse.lemminx.customservice.synapse.utils.Utils;
import org.eclipse.lemminx.dom.DOMDocument;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InboundConnectorHolder {

    private static final Logger LOGGER = Logger.getLogger(InboundConnectorHolder.class.getName());
    private String projectId;
    private String projectPath;
    private String tempFolderPath;
    // <Connector name, Connector ID> map
    private HashMap<String, String> connectorIdMap;
    // <Connector ID, UI schema path> map
    private HashMap<String, String> inboundConnectors;
    private Map<String, JsonObject> localInboundConnectors;
    private JsonObject inboundConnectorListJson;

    public InboundConnectorHolder() {

        this.inboundConnectors = new HashMap<>();
        this.connectorIdMap = new HashMap<>();
    }

    public void init(String projectPath, String projectRuntimeVersion) {

        if (projectPath == null) {
            LOGGER.log(Level.SEVERE, "Project path is null. Cannot initialize inbound connector holder.");
            return;
        }
        this.projectPath = projectPath;
        this.projectId = Utils.getHash(projectPath);
        this.tempFolderPath = System.getProperty("user.home") + File.separator + ".wso2-mi" + File.separator +
                "inbound.connectors" + File.separator + new File(projectPath).getName() + "_" +projectId;
        InputStream inputStream = JsonLoader.class
                .getResourceAsStream("/org/eclipse/lemminx/inbound-endpoints/inbound_endpoints_"
                        + projectRuntimeVersion.replace(".", "") + ".json");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        this.inboundConnectorListJson = JsonParser.parseReader(reader).getAsJsonObject();
        this.localInboundConnectors = Utils.getUISchemaMap("org/eclipse/lemminx/inbound-endpoints/"
                + projectRuntimeVersion.replace(".", ""));
        loadInboundConnectors();
    }

    private void loadInboundConnectors() {

        File folder = new File(tempFolderPath);
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (!file.isHidden()) {
                        loadInboundConnector(file);
                    }
                }
            }
        }
    }

    private void loadInboundConnector(File file) {

        try {
            String connectorName = file.getName().replace(".json", "");
            String uiSchema = Utils.readFile(file);
            JsonObject inboundConnector = Utils.getJsonObject(uiSchema);
            if (inboundConnector == null || !inboundConnector.has(Constant.ID)) {
                return;
            }
            String id = inboundConnector.get(Constant.ID).getAsString();
            connectorIdMap.put(connectorName, id);
            inboundConnectors.put(id, file.getAbsolutePath());
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error occurred while loading inbound connector schema from file", e);
        }
    }

    public Boolean saveInboundConnector(String connectorName, String uiSchema) {

        // Save inbound connector
        JsonObject inboundConnector = Utils.getJsonObject(uiSchema);
        if (inboundConnector == null || !inboundConnector.has(Constant.ID)) {
            return false;
        }
        String id = inboundConnector.get(Constant.ID).getAsString();
        Path filePath = Path.of(tempFolderPath, connectorName + ".json");
        if (saveToFile(filePath.toFile(), uiSchema)) {
            connectorIdMap.put(connectorName, id);
            inboundConnectors.put(id, filePath.toString());
            return true;
        }
        return false;
    }

    public InboundConnectorResponse getInboundConnectorSchema(File inboundEPFile) {

        try {
            DOMDocument inboundEPElement = Utils.getDOMDocument(inboundEPFile);
            if (inboundEPElement != null) {
                InboundEndpoint ib =
                        (InboundEndpoint) SyntaxTreeGenerator.buildTree(inboundEPElement.getDocumentElement());
                if (ib != null) {
                    String id = getIdFromInboundEP(ib);
                    if (id != null) {
                        InboundConnectorResponse response = getInboundConnectorSchemaFromId(id);
                        if (response.getUiSchema() != null) {
                            JsonObject schemaWithValues =
                                    UISchemaMapper.mapInputToUISchemaForInboundEndpoint(ib, response.getUiSchema());
                            response.setUiSchema(schemaWithValues);
                        }
                        return response;
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error occurred while reading inbound endpoint file", e);
        }
        return new InboundConnectorResponse();
    }

    private String getIdFromInboundEP(InboundEndpoint ib) {

        String id = ib.getProtocol();
        if (id == null) {
            id = ib.getClazz();
        }
        return id;
    }

    public JsonObject getLocalInboundConnectorList() {

        return inboundConnectorListJson;
    }

    public InboundConnectorResponse getInboundConnectorSchema(String connectorName) {

        InboundConnectorResponse inboundConnector = new InboundConnectorResponse();
        inboundConnector.connectorName = connectorName;
        String connectorId = connectorIdMap.get(connectorName);
        return getInboundConnectorSchemaFromId(connectorId);
    }

    public InboundConnectorResponse getInboundConnectorSchemaFromId(String connectorId) {

        InboundConnectorResponse inboundConnector = new InboundConnectorResponse();

        if (localInboundConnectors.containsKey(connectorId)) {
            inboundConnector.uiSchema = localInboundConnectors.get(connectorId);
            return inboundConnector;
        }

        String uiSchemaPath = inboundConnectors.get(connectorId);
        if (uiSchemaPath == null) {
            return inboundConnector;
        }
        try {
            String uiSchema = Utils.readFile(new File(uiSchemaPath));
            JsonObject inboundConnectorJson = Utils.getJsonObject(uiSchema);
            inboundConnector.uiSchema = inboundConnectorJson;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error occurred while reading inbound connector schema from file", e);
        }
        return inboundConnector;
    }

    public boolean saveToFile(File file, String text) {

        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
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
