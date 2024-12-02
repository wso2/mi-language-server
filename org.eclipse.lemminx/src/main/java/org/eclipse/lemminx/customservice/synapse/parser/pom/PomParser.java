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
package org.eclipse.lemminx.customservice.synapse.parser.pom;

import org.eclipse.lemminx.customservice.synapse.parser.Constants;
import org.eclipse.lemminx.customservice.synapse.parser.OverviewPageDetailsResponse;
import org.eclipse.lemminx.customservice.synapse.parser.PomXmlEditRequest;
import org.eclipse.lsp4j.Range;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PomParser {

    private static final Logger LOGGER = Logger.getLogger(PomParser.class.getName());
    private static OverviewPageDetailsResponse pomDetailsResponse;

    public static void getPomDetails(String projectUri, OverviewPageDetailsResponse detailsResponse) {
        pomDetailsResponse = detailsResponse;
        extractPomContent(projectUri);
    }

    public static String removeDependency(String projectUri, Range range) {
        try {
            File pomFile = new File(projectUri + File.separator + Constants.POM_FILE);
            if (!isPomFileExist(pomFile)) {
                return null;
            }
            List<String> lines = Files.readAllLines(pomFile.toPath());
            int startLine = range.getStart().getLine();
            int endLine = range.getEnd().getLine();
            for (int i = endLine - 1; i >= startLine - 1; i--) {
                if (i == (endLine - 1)) {
                    processString(lines, lines.get(i).substring(range.getEnd().getCharacter() - 1), i);
                } else if (i == (startLine - 1)) {
                    processString(lines, lines.get(i).substring(0, range.getStart().getCharacter() - 1), i);
                } else {
                    lines.remove(i);
                }
            }
            return String.join("\n", lines);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error removing the dependency from the POM file: " + e.getMessage());
            return null;
        }
    }

    public static String addDependency(String projectUri, PomXmlEditRequest request) {
        int index = getIndex(request);
        List<String> lines = getPomXmlList(projectUri);
        assert lines != null;
        List<String> newLines = new ArrayList<>(lines);
        String[] newContentLines = request.value.split("\n");
        if (index > 0) {
            for (int i = newContentLines.length - 1; i >= 0; i--) {
                newLines.add(index, newContentLines[i]);
            }
        } else {
            adNewDependency(lines, newLines, newContentLines);
        }
        return String.join("\n", newLines);
    }

    public static String updateValue(String projectUri, PomXmlEditRequest request) {
        try {
            File pomFile = new File(projectUri + File.separator + Constants.POM_FILE);
            if (!isPomFileExist(pomFile)) {
                return null;
            }
            List<String> lines = Files.readAllLines(pomFile.toPath());
            if (request.range.isRight()) {
                for (Range range : request.range.getRight()) {
                    updateValue(lines, request.value, range);
                }
                return String.join("\n", lines);
            } else {
                updateValue(lines, request.value, request.range.getLeft());
                return String.join("\n", lines);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error modifying the POM file: " + e.getMessage());
            return null;
        }
    }

    private static void updateValue(List<String> lines, String value, Range range) {
        int startLine = range.getStart().getLine();
        int endLine = range.getEnd().getLine();
        int startColumn = range.getStart().getCharacter();
        int endColumn = range.getEnd().getCharacter();
        for (int i = startLine - 1; i < endLine; i++) {
            String line = lines.get(i);
            if (line.length() >= startColumn) {
                String beforeReplacement = line.substring(0, startColumn - 1);
                String afterReplacement = (line.length() > endColumn) ?
                        line.substring(endColumn - 1) : "";
                String modifiedLine = beforeReplacement + value + afterReplacement;
                lines.set(i, modifiedLine);
            }
        }
    }

    private static void extractPomContent(String projectUri) {
        try {
            File pomFile = new File(projectUri + File.separator + Constants.POM_FILE);
            if (!isPomFileExist(pomFile)) {
                return;
            }
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            PluginHandler handler = new PluginHandler(pomDetailsResponse);
            saxParser.parse(pomFile, handler);
        } catch (ParserConfigurationException e) {
            LOGGER.log(Level.SEVERE, "Error configuring the parser for the POM file: " + e.getMessage());
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error accessing the POM file: " + e.getMessage());
        } catch (SAXException e) {
            LOGGER.log(Level.SEVERE, "Error parsing the POM file: " + e.getMessage());
        }
    }

    private static boolean isPomFileExist(File pomFile) {
        if (!pomFile.exists()) {
            LOGGER.log(Level.SEVERE, "POM file does not exist: " + pomFile.getAbsolutePath());
            return false;
        }
        return true;
    }

    private static void processString(List<String> lines, String value, int lineNumber) {
        if (value.trim().isEmpty()) {
            lines.remove(lineNumber);
        } else {
            lines.set(lineNumber, value);
        }
    }

    private static List<String> getPomXmlList(String projectUri) {
        try {
            File pomFile = new File(projectUri + File.separator + Constants.POM_FILE);
            if (!isPomFileExist(pomFile)) {
                return null;
            }
            return Files.readAllLines(pomFile.toPath());
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error adding the dependency to the POM file: " + e.getMessage());
            return null;
        }
    }

    private static int getIndex(PomXmlEditRequest request) {
        if (request.range != null) {
            return request.range.getLeft().getStart().getLine();
        } else {
            return 0;
        }
    }

    private static void adNewDependency(List<String> pomContent, List<String> newPomContent,String[] dependency) {
        int projectEndIndex = -1;
        for (int i = pomContent.size() - 1; i >= 0; i--) {
            if (pomContent.get(i).contains(Constants.PROJECT_END_TAG)) {
                projectEndIndex = i;
                break;
            }
        }
        String value = pomContent.get(projectEndIndex);
        int startColumn = value.lastIndexOf(Constants.END_TAG);
        String afterReplacement = value.substring(startColumn);
        String beforeReplacement = value.substring(0, startColumn);
        if (startColumn > 0 && !beforeReplacement.trim().isEmpty()) {
            newPomContent.set(projectEndIndex, beforeReplacement);
        }
        projectEndIndex++;
        newPomContent.add(projectEndIndex, Constants.DEPENDENCIES_START_TAG);
        for (int i = dependency.length - 1; i >= 0; i--) {
            projectEndIndex++;
            newPomContent.add(projectEndIndex, dependency[i]);
        }
        projectEndIndex++;
        newPomContent.add(projectEndIndex, Constants.DEPENDENCIES_END_TAG);
        projectEndIndex++;
        newPomContent.add(projectEndIndex , afterReplacement);
    }
}
