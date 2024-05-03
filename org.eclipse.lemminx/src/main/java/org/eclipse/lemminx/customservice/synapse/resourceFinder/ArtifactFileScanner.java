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

public class ArtifactFileScanner {

    public static List<String> scanArtifactFiles(String projectPath) {

        List<String> artifactFiles = new ArrayList<>();
        if (projectPath != null) {
            String artifactPath = Path.of(projectPath, "src", "main", "wso2mi", "artifacts").toString();
            traverseFiles(artifactPath, artifactFiles);
        }
        return artifactFiles;
    }

    private static void traverseFiles(String projectPath, List<String> artifactFiles) {

        File folder = new File(projectPath);
        File[] listOfFiles = folder.listFiles();
        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile() && !file.isHidden()) {
                    String artifactFilePath = extractArtifactFile(file);
                    artifactFiles.add(artifactFilePath);
                } else if (file.isDirectory()) {
                    traverseFiles(file.getAbsolutePath(), artifactFiles);
                }
            }
        }
    }

    private static String extractArtifactFile(File file) {

        String artifactFilePath = file.getAbsolutePath();
        Pattern pattern = Pattern.compile(".*" + Pattern.quote(File.separator) + "(.*)\\.(xml|dbs)");
        Matcher matcher = pattern.matcher(artifactFilePath);
        if (matcher.find()) {
            artifactFilePath = matcher.group(1);
        } else {
            artifactFilePath = null;
        }
        return artifactFilePath;
    }
}
