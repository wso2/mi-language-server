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

package org.eclipse.lemminx.customservice.synapse.configurable;

import org.eclipse.lemminx.customservice.synapse.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConfigurableEntryScanner {

    public static List<ConfigurableEntry> scanConfigurableEntries(String projectPath)
            throws IOException {

        String configurableFilePath = getConfigurableFilePath(projectPath);
        File configurableFile = new File(configurableFilePath);
        if (configurableFile.exists()) {
            String configurableFileContent = Utils.readFile(configurableFile);
            return extractConfigurableFileContent(configurableFileContent);
        }
        return Collections.emptyList();
    }

    private static List<ConfigurableEntry> extractConfigurableFileContent(String content) {
        List<ConfigurableEntry> result = new ArrayList<>();
        if (content != null) {
            String[] lines = content.split("\n");
            for (String line : lines) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    String configurableName = parts[0];
                    String configurableType = parts[1];
                    ConfigurableEntry configurableEntry = new ConfigurableEntry(configurableName, configurableType);
                    result.add(configurableEntry);
                }
            }
        }
        return result;
    }

    private static String getConfigurableFilePath(String projectPath) {

        if (projectPath != null) {
            Path configurableFilePath = Path.of(projectPath, "src", "main", "wso2mi", "resources", "conf", "config.properties");
            return configurableFilePath.toString();
        }
        return "";
    }
}
