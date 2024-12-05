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
package org.eclipse.lemminx.customservice.synapse.parser.config;

import org.eclipse.lemminx.customservice.synapse.parser.ConfigDetails;
import org.eclipse.lemminx.customservice.synapse.parser.Constants;
import org.eclipse.lemminx.customservice.synapse.parser.Node;
import org.eclipse.lemminx.customservice.synapse.parser.OverviewPageDetailsResponse;
import org.eclipse.lemminx.customservice.synapse.parser.UpdateConfigRequest;
import org.eclipse.lemminx.customservice.synapse.parser.UpdateResponse;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.TextEdit;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfigParser {

    private static final Logger LOGGER = Logger.getLogger(ConfigParser.class.getName());

    public static void getConfigDetails(String projectUri, OverviewPageDetailsResponse detailsResponse) {
        File propertyFilePath = getFilePath(projectUri);
        if (isConfigFileExist(propertyFilePath)) {
            try (BufferedReader reader = new BufferedReader(new FileReader(propertyFilePath))) {
                String line;
                int lineNumber = 1;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.startsWith(Constants.HASH) || line.isEmpty()) {
                        continue;
                    }
                    int delimiterIndex = line.indexOf(':');
                    if (delimiterIndex != -1) {
                        String key = line.substring(0, delimiterIndex).trim();
                        detailsResponse.setConfig(
                                new Node(key, line.substring(delimiterIndex + 1).trim(),
                                        Either.forLeft(new Range(
                                                new Position(lineNumber, line.indexOf(key) + 1),
                                                new Position(lineNumber, line.length() + 1)))));
                    }
                    lineNumber++;
                }
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error processing the config file: " + e.getMessage());
            }
        }
    }

    public static UpdateResponse updateConfigFile(String projectUri, UpdateConfigRequest request) {
        File propertyFilePath = getFilePath(projectUri);
        if (isConfigFileExist(propertyFilePath)) {
            try {
                List<String> fileLines = new ArrayList<>(Files.readAllLines(propertyFilePath.toPath()));
                UpdateResponse updateResponse = new UpdateResponse();
                int startLine = fileLines.size();
                for (ConfigDetails entry : request.configs) {
                    String value = entry.key + Constants.COLON + entry.value;
                    if (entry.range != null) {
                        Range range = entry.range;
                        updateResponse.add(new TextEdit(new Range(range.getStart(),
                                new Position(range.getStart().getLine(), range.getEnd().getCharacter())), value));
                    } else {
                        startLine++;
                        updateResponse.add(new TextEdit(new Range(new Position(startLine, 0),
                                new Position(startLine, value.length() + 1)), value));
                    }
                }
                return updateResponse;
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error processing the config file: " + e.getMessage());
            }
        }
        return null;
    }

    private static File getFilePath(String projectUri) {
        return new File(Path.of(projectUri, Constants.SRC, Constants.MAIN, Constants.WSO2_MI,
                Constants.RESOURCES, Constants.CONF ,Constants.CONFIG_FILE).toUri());
    }

    private static boolean isConfigFileExist(File propertiesFile) {
        if (!propertiesFile.exists()) {
            LOGGER.log(Level.SEVERE, "Config file does not exist: " + propertiesFile.getAbsolutePath());
            return false;
        }
        return true;
    }
}
