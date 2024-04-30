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

package org.eclipse.lemminx.customservice.synapse.resourceFinder;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistryFileScanner {

    public static List<String> scanRegistryFiles(String path) {

        String registryPath = getRegistryPath(path);
        File folder = new File(registryPath);
        File[] listOfFiles = folder.listFiles();
        List<String> registryFiles = new ArrayList<>();
        if (listOfFiles != null) {
            traverseFiles(listOfFiles, registryFiles);
        }
        return registryFiles;
    }

    private static void traverseFiles(File[] listOfFiles, List<String> registryFiles) {

        for (File file : listOfFiles) {
            if (file.isFile() && !file.isHidden()) {
                String regFilePath = extractRegistryFilePath(file);
                if (regFilePath.contains("gov") || regFilePath.contains("conf")) {
                    registryFiles.add(regFilePath);
                }
            } else if (file.isDirectory()) {
                if (!".meta".equals(file.getName())) {
                    traverseFiles(file.listFiles(), registryFiles);
                }
            }
        }
    }

    private static String extractRegistryFilePath(File file) {

        String path = file.getAbsolutePath();
        Path regFolderPath = Path.of("src", "main", "wso2mi", "resources", "registry");
        Pattern pattern = Pattern.compile(regFolderPath + File.separator + "(.*)");
        Matcher matcher = pattern.matcher(path);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }

    private static String getRegistryPath(String projectPath) {

        if (projectPath != null) {
            Path registryPath = Path.of(projectPath, "src", "main", "wso2mi", "resources", "registry");
            return registryPath.toString();
        }
        return "";
    }
}
