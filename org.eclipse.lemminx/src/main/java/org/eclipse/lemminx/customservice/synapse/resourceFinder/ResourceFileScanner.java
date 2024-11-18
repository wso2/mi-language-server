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

import org.eclipse.lemminx.customservice.synapse.utils.Constant;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResourceFileScanner {

    public static List<String> scanResourceFiles(String path) {

        String resourcePath = getResourcePath(path);
        File folder = new File(resourcePath);
        File[] listOfFiles = folder.listFiles();
        List<String> resourceFiles = new ArrayList<>();
        if (listOfFiles != null) {
            traverseFiles(listOfFiles, resourceFiles);
        }
        return resourceFiles;
    }

    private static void traverseFiles(File[] listOfFiles, List<String> resourceFiles) {

        for (File file : listOfFiles) {
            if (file.isFile() && !file.isHidden()) {
                String resourceFilePath = extractResourceFilePath(file);
                if (!(resourceFilePath.contains("registry/gov") || resourceFilePath.contains("registry/conf") || resourceFilePath.equals("registry/artifact.xml") || resourceFilePath.equals("artifact.xml"))) {
                    resourceFiles.add(Constant.RESOURCES + ":" + resourceFilePath);
                }
            } else if (file.isDirectory()) {
                if (!".meta".equals(file.getName())) {
                    traverseFiles(file.listFiles(), resourceFiles);
                }
            }
        }
    }

    private static String extractResourceFilePath(File file) {

        String path = file.getAbsolutePath();
        String resourceFolderPath = Path.of("src", "main", "wso2mi", "resources").toString();
        resourceFolderPath = resourceFolderPath.replace(File.separator, Pattern.quote(File.separator));
        Pattern pattern = Pattern.compile(resourceFolderPath + Pattern.quote(File.separator) + "(.*)");
        Matcher matcher = pattern.matcher(path);
        if (matcher.find()) {
            return matcher.group(1).replaceAll("\\\\", "/");
        }
        return "";
    }

    private static String getResourcePath(String projectPath) {

        if (projectPath != null) {
            Path resourcePath = Path.of(projectPath, "src", "main", "wso2mi", "resources");
            return resourcePath.toString();
        }
        return "";
    }
}
