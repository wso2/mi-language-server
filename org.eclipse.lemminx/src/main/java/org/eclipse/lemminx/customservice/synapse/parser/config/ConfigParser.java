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

import org.eclipse.lemminx.customservice.synapse.parser.ConfigFileEditRequest;
import org.eclipse.lemminx.customservice.synapse.parser.Constants;
import org.eclipse.lemminx.customservice.synapse.parser.Node;
import org.eclipse.lemminx.customservice.synapse.parser.OverviewPageDetailsResponse;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfigParser {

    private static final Logger LOGGER = Logger.getLogger(ConfigParser.class.getName());
    private File propertiesFile;

    public static void getConfigDetails(String projectUri, OverviewPageDetailsResponse detailsResponse) {
        List<String> fileLines = new ArrayList<>();
        File propertyFilePath = getFilePath(projectUri);
        if (isConfigFileExist(propertyFilePath)) {
            try (BufferedReader reader = new BufferedReader(new FileReader(propertyFilePath))) {
                String line;
                int lineNumber = 0;
                while ((line = reader.readLine()) != null) {
                    lineNumber++;
                    line = line.trim();
                    if (line.startsWith("#") || line.isEmpty()) {
                        continue;
                    }
                    int delimiterIndex = line.indexOf(':');
                    if (delimiterIndex != -1) {
                        String key = line.substring(0, delimiterIndex).trim();
                        String value = line.substring(delimiterIndex + 1).trim();
                        detailsResponse.setConfig(new Node(key, value, Either.forLeft(new Range(new Position(lineNumber,
                                line.indexOf(key)), new Position(lineNumber, line.indexOf(value) +
                                value.length() - 1)))));
                    }
                }
            } catch (FileNotFoundException e) {
                LOGGER.log(Level.SEVERE, "Config file does not exist: " + e.getMessage());
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error accessing the config file: " + e.getMessage());
            }
        }
    }

    public static String updateConfigFileValue(String projectUri, ConfigFileEditRequest request) {
        List<String> fileLines = new ArrayList<>();
        File propertyFilePath = getFilePath(projectUri);
        if (isConfigFileExist(propertyFilePath)) {
            try {
                fileLines = new ArrayList<>(Files.readAllLines(propertyFilePath.toPath()));
                int targetLine = request.range.getStart().getLine() - 1;
                if (targetLine >= 0 && targetLine < fileLines.size()) {
                    String trimmedLine = fileLines.get(targetLine).trim();

                    if (!trimmedLine.startsWith("#") && !trimmedLine.isEmpty()) {
                        fileLines.set(targetLine, request.key + ":" + request.value);
                    }
                } else {
                    LOGGER.log(Level.WARNING, "Specified line number is out of bounds: " + (targetLine + 1));
                }
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error updating properties file: " + e.getMessage());
            }
        }
        return String.join("\n", fileLines);
    }

    private static File getFilePath(String projectUri) {
        return new File(projectUri + File.separator + Constants.SRC + File.separator + Constants.MAIN +
                File.separator + Constants.WSO2_MI + File.separator + Constants.RESOURCES + File.separator +
                Constants.CONF + File.separator + Constants.CONFIG_FILE);
    }

    private static boolean isConfigFileExist(File propertiesFile) {
        if (!propertiesFile.exists()) {
            LOGGER.log(Level.SEVERE, "Config file does not exist: " + propertiesFile.getAbsolutePath());
            return false;
        }
        return true;
    }
}
