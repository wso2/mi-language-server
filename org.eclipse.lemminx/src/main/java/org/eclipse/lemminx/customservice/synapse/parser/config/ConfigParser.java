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
import org.eclipse.lemminx.customservice.synapse.parser.Node;
import org.eclipse.lemminx.customservice.synapse.parser.OverviewPageDetailsResponse;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfigParser {

    private static final Logger LOGGER = Logger.getLogger(ConfigParser.class.getName());

    public static void getConfigDetails(String projectUri, OverviewPageDetailsResponse detailsResponse) {
        File propertiesFile = new File(projectUri + File.separator + "src" + File.separator + "main" +
                File.separator + "wso2mi" + File.separator + "resources" + File.separator +  "conf" +
                File.separator + "config.properties");
        // Verify the file exists
        if (!propertiesFile.exists()) {
            LOGGER.log(Level.SEVERE, "Config file does not exist: " + propertiesFile.getAbsolutePath());
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(propertiesFile))) {
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;

                // Skip comments and blank lines
                line = line.trim();
                if (line.startsWith("#") || line.isEmpty()) {
                    continue;
                }

                // Find the delimiter '='
                int delimiterIndex = line.indexOf(':');
                if (delimiterIndex != -1) {
                    // Extract key and value
                    String key = line.substring(0, delimiterIndex).trim();
                    String value = line.substring(delimiterIndex + 1).trim();
                    detailsResponse.setConfig(new Node(key, value, new Range(new Position(lineNumber, line.indexOf(key)),
                            new Position(lineNumber, line.indexOf(value) + value.length() - 1))));
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String updateConfigFileValue(String projectUri, ConfigFileEditRequest request) {
        File propertiesFile = new File(projectUri + File.separator + "src" + File.separator + "main" +
                File.separator + "wso2mi" + File.separator + "resources" + File.separator +  "conf" +
                File.separator + "config.properties");
        List<String> fileLines = new ArrayList<>();
        try {
            if (!propertiesFile.exists()) {
                LOGGER.log(Level.SEVERE, "Config file does not exist: " + propertiesFile.getAbsolutePath());
                return null;
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(propertiesFile))) {
                String line;
                int lineNumber = 0;

                while ((line = reader.readLine()) != null) {
                    lineNumber++;
                    String trimmedLine = line.trim();

                    // Skip comments and blank lines
                    if (trimmedLine.startsWith("#") || trimmedLine.isEmpty()) {
                        fileLines.add(line);
                        continue;
                    }
                    if (((Range) request.range).getStart().getLine() == lineNumber) {
                        line = request.key + ":" + request.value;
                    }
                    fileLines.add(line);
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error updating properties file: " + e.getMessage());
        }
        return Arrays.toString(fileLines.toArray());
    }
}
