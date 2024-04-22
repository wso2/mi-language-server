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

package org.eclipse.lemminx.customservice.synapse.directoryTree;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.eclipse.lemminx.customservice.synapse.directoryTree.legacyBuilder.DirectoryMap;
import org.eclipse.lemminx.customservice.synapse.utils.Constant;
import org.eclipse.lemminx.customservice.synapse.utils.OptionalTypeAdapter;

import java.util.Optional;

public class DirectoryMapResponse {

    private JsonElement directoryMap;

    public DirectoryMapResponse(DirectoryMap directoryMap) {

        Gson gson = new GsonBuilder()
                .registerTypeHierarchyAdapter(Optional.class, new OptionalTypeAdapter())
                .serializeNulls()
                .disableHtmlEscaping()
                .create();
        JsonElement jsonTree = gson.toJsonTree(directoryMap);
        this.directoryMap = jsonTree;
    }

    public DirectoryMapResponse(Tree directoryTree) {

        Gson gson = new GsonBuilder()
                .registerTypeHierarchyAdapter(Optional.class, new OptionalTypeAdapter())
                .serializeNulls()
                .disableHtmlEscaping()
                .create();
        JsonElement jsonTree = gson.toJsonTree(directoryTree);
        if (jsonTree != null && !jsonTree.isJsonNull()) {
            JsonElement projectType = jsonTree.getAsJsonObject().get(Constant.PROJECT_TYPE);
            if (!projectType.isJsonNull() && Constant.INTEGRATION_PROJECT.equalsIgnoreCase(projectType.getAsString())) {
                jsonTree = convertFormat(jsonTree);
            }
            this.directoryMap = jsonTree;
        }
    }

    //Convert the format to default file structure view.
    private JsonElement convertFormat(JsonElement jsonTree) {

        JsonObject jsonObject = jsonTree.getAsJsonObject();
        JsonObject convertedObject = new JsonObject();

        JsonObject src = new JsonObject();
        JsonObject main = new JsonObject();
        JsonObject wso2mi = new JsonObject();
        JsonObject artifacts = new JsonObject();

        String[] artifactNames = {"apis", "endpoints", "sequences", "proxyServices", "inboundEndpoints",
                "messageStores", "messageProcessors", "tasks", "localEntries", "templates", "dataServices",
                "dataSources"};
        processLocalEntries(jsonObject);
        for (String element : artifactNames) {
            artifacts.add(element, jsonObject.get(element));
        }

        wso2mi.add("artifacts", artifacts);
        JsonElement resources = jsonObject.get("resources");
        wso2mi.add("resources", resources);

        main.add("wso2mi", wso2mi);
        JsonElement java = jsonObject.get("java");
        main.add("java", java);

        src.add("main", main);
        JsonElement tests = jsonObject.get("tests");
        src.add("tests", tests);

        convertedObject.add("src", src);
        return convertedObject;
    }

    private void processLocalEntries(JsonObject jsonElement) {

        JsonElement localEntries = jsonElement.get("localEntries");
        JsonObject connectionEntries = new JsonObject();
        JsonArray otherEntries = new JsonArray();
        localEntries.getAsJsonArray().forEach(localEntry -> {
            JsonObject localEntryObject = localEntry.getAsJsonObject();
            JsonElement connectorNameEle = localEntryObject.get("connectorName");
            if (connectorNameEle != null) {
                addToProcessedLocalEntries(connectionEntries, localEntryObject, connectorNameEle);
            } else {
                otherEntries.getAsJsonArray().add(localEntryObject);
            }
        });
        JsonElement processedLocalEntries = combineLocalEntries(connectionEntries, otherEntries);
        jsonElement.add("localEntries", processedLocalEntries);
    }

    private void addToProcessedLocalEntries(JsonObject connectionEntries, JsonObject localEntryObject,
                                            JsonElement connectorNameEle) {

        String connectorName = connectorNameEle.getAsString();
        if (!connectorName.isEmpty()) {
            JsonElement connectorElement = connectionEntries.get(connectorName);
            if (connectorElement != null) {
                connectorElement.getAsJsonArray().add(localEntryObject);
            } else {
                connectionEntries.add(connectorName, new JsonArray());
                connectionEntries.get(connectorName).getAsJsonArray().add(localEntryObject);
            }
        }
    }

    private JsonElement combineLocalEntries(JsonObject connectionEntries, JsonArray otherEntries) {

        JsonArray localEntriesArray = new JsonArray();
        localEntriesArray.addAll(otherEntries);
        connectionEntries.entrySet().forEach(entry -> {
            JsonObject connectorObject = new JsonObject();
            connectorObject.add(entry.getKey(), entry.getValue());
            localEntriesArray.add(connectorObject);
        });
        return localEntriesArray;
    }

    public JsonElement getDirectoryMap() {

        return directoryMap;
    }

    public void setDirectoryMap(JsonElement directoryMap) {

        this.directoryMap = directoryMap;
    }
}
