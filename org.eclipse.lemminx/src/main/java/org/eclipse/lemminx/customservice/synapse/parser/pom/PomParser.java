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

import org.eclipse.lemminx.customservice.synapse.parser.Node;
import org.eclipse.lemminx.customservice.synapse.parser.OverviewPageDetailsResponse;
import org.eclipse.lemminx.customservice.synapse.parser.PomXmlEditRequest;
import org.eclipse.lemminx.utils.StringUtils;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PomParser {

    private static final Logger LOGGER = Logger.getLogger(PomParser.class.getName());
    private static OverviewPageDetailsResponse pomDetailsResponse;

    public static void getPomDetails(String projectUri, OverviewPageDetailsResponse detailsResponse) {
        pomDetailsResponse = detailsResponse;
        extractPomContent(projectUri);
    }

    public static String removeContent(String projectUri, Range range) {
        List<String> lines = null;
        try {
            lines = Files.readAllLines(Paths.get(projectUri + File.separator + "pom.xml"));
            int startLine = range.getStart().getLine();
            int endLine = range.getEnd().getLine();
            for (int i = endLine - 1; i >= startLine - 1; i--) {
                if (i == (endLine - 1)) {
                    String line = lines.get(i);
                    String value = line.substring(range.getEnd().getCharacter() - 1);
                    if (value.trim().isEmpty()) {
                        lines.remove(i);
                    } else {
                        lines.set(i, value);
                    }
                } else if (i == (startLine - 1)) {
                    String line = lines.get(i);
                    String value = line.substring(0, range.getStart().getCharacter() - 1);
                    if (value.trim().isEmpty()) {
                        lines.remove(i);
                    } else {
                        lines.set(i, value);
                    }
                } else {
                    lines.remove(i);
                }
            }
            return lines.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String addContent(String projectUri, PomXmlEditRequest request) {
        // Read the POM file into a list of lines
        List<String> lines = null;
        try {
            // Step 1: Read all lines from the pom.xml file
            lines = Files.readAllLines(Paths.get(projectUri + File.separator + "pom.xml"));

            // Step 2: Insert new content at the specified line (adjust for 0-based index)
            int index = ((Range) request.range).getStart().getLine() - 1; // Convert to 0-based index
            List<String> newLines = new ArrayList<>(lines);

            // Split the new content into lines and insert at the specific line
            String[] newContentLines = request.value.split("\n");
            for (int i = newContentLines.length - 1; i >= 0; i--) {
                newLines.add(index, newContentLines[i]);  // Add each new line before the current position
            }
            return newLines.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String updateValue(String projectUri, PomXmlEditRequest request) {
        // Read the POM file into a list of lines
        List<String> lines = null;
        try {
            // Step 1: Read all lines from the XML file
            lines = Files.readAllLines(Paths.get(projectUri + File.separator + "pom.xml"));
            Range range = (Range) request.range;
            int startLine = range.getStart().getLine();
            int endLine = range.getEnd().getLine();
            int startColumn = range.getEnd().getCharacter();
            int endColumn = range.getEnd().getCharacter();
            // Step 2: Modify the lines within the specified range
            for (int i = startLine - 1; i < endLine; i++) {  // Adjust for 0-based index
                String line = lines.get(i);

                // Ensure the line is long enough to contain content in the specified column range
                if (line.length() > startColumn) {
                    // Get the substring that needs to be replaced
                    String beforeReplacement = line.substring(0, startColumn);
                    String afterReplacement = line.length() > endColumn
                            ? line.substring(endColumn)
                            : "";

                    // Replace the content between the startColumn and endColumn with newValue
                    String modifiedLine = beforeReplacement + request.value + afterReplacement;

                    // Replace the line with the modified content
                    lines.set(i, modifiedLine);
                }
            }
            return lines.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void extractPomContent(String projectUri) {
        try {
            File pomFile = new File(projectUri + File.separator + "pom.xml");

            // Verify the file exists
            if (!pomFile.exists()) {
                LOGGER.log(Level.SEVERE, "POM file does not exist: " + pomFile.getAbsolutePath());
                return;
            }

            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();

            // Parse the XML file with a custom handler
            PluginHandler handler = new PluginHandler(pomDetailsResponse);
            saxParser.parse(pomFile, handler);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }
    }
}

class PluginHandler extends DefaultHandler {
    private Locator locator; // Tracks current position in the file
    private final StringBuilder contentBuffer = new StringBuilder(); // Buffer for element content
    private boolean isDependency, isPlugin, isRepository, isPluginRepository, isProperties, isImages; // Flags for sections
    private int valueStartLine, valueStartColumn, dependencyStartLine, dependencyStartColumn; // Start position of current value

    private String pluginArtifactId, pluginVersion, dependencyType = "";
    private Range range;
    Node groupId;
    Node artifactId;
    Node version;

    private final OverviewPageDetailsResponse pomDetailsResponse;

    public PluginHandler(OverviewPageDetailsResponse pomDetailsResponse) {
        this.pomDetailsResponse = pomDetailsResponse;
    }

    @Override
    public void setDocumentLocator(Locator locator) {
        this.locator = locator; // Assign the locator for line/column tracking
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        contentBuffer.setLength(0); // Clear buffer

        // Record the starting position of the element content
        valueStartLine = locator.getLineNumber();
        valueStartColumn = locator.getColumnNumber();

        // Set flags for dependencies and plugins sections
        if ("dependency".equals(qName)) {
            isDependency = true;
            groupId = new Node();
            artifactId = new Node();
            version = new Node();
            dependencyStartLine = locator.getLineNumber();
            dependencyStartColumn = locator.getColumnNumber() - (qName.length() + 2);
            dependencyType = ""; // Reset version
        } else if ("plugin".equals(qName)) {
            isPlugin = true; // Start of a plugin section
            pluginArtifactId = ""; // Reset artifact ID
            pluginVersion = ""; // Reset version
            range = new Range();
        } else if ("repository".equals(qName)) {
            isRepository = true; // Start of a plugin section
        } else if ("pluginRepository".equals(qName)) {
            isPluginRepository = true; // Start of a plugin section
        } else if ("properties".equals(qName)) {
            isProperties = true; // Start of a plugin section
        } else if ("images".equals(qName) && isPlugin) {
            isImages = true; // Start processing images
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        String value = contentBuffer.toString().trim();
        int closingTagLength = qName.length() + 3;
        int valueEndLine = locator.getLineNumber();
        int valueEndColumn = locator.getColumnNumber();
        if (isPlugin) {
            switch (qName) {
                case "artifactId":
                    pluginArtifactId = value;
                    break;
                case "version":
                    pluginVersion = value;
                    range = getRange(valueStartLine, valueStartColumn, valueEndLine,
                            valueEndColumn - closingTagLength);
                    break;
                case "name":
                    if (pluginArtifactId.equals("docker-maven-plugin")) {
                        pomDetailsResponse.setDockerName(new Node(value, getRange(valueStartLine, valueStartColumn,
                                valueEndLine, valueEndColumn - closingTagLength)));
                    }
                    break;
                case "image":
                    isImages = false;
                    break;
                case "testServerType":
                    pomDetailsResponse.setServerType(new Node(value, getRange(valueStartLine, valueStartColumn,
                            valueEndLine, valueEndColumn - closingTagLength)));
                    break;
                case "testServerHost":
                    pomDetailsResponse.setServerHost(new Node(value, getRange(valueStartLine, valueStartColumn,
                            valueEndLine, valueEndColumn - closingTagLength)));
                    break;
                case "testServerPort":
                    pomDetailsResponse.setServerPort(new Node(value, getRange(valueStartLine, valueStartColumn,
                            valueEndLine, valueEndColumn - closingTagLength)));
                    break;
                case "testServerPath":
                    pomDetailsResponse.setServerPath(new Node(value, getRange(valueStartLine, valueStartColumn,
                            valueEndLine, valueEndColumn - closingTagLength)));
                    break;
                case "testServerVersion":
                    pomDetailsResponse.setServerVersion(new Node(value, getRange(valueStartLine, valueStartColumn,
                            valueEndLine, valueEndColumn - closingTagLength)));
                    break;
                case "testServerDownloadLink":
                    pomDetailsResponse.setServerDownloadLink(new Node(value, getRange(valueStartLine, valueStartColumn,
                            valueEndLine, valueEndColumn - closingTagLength)));
                    break;
                case "plugin":
                    // End of plugin, validate the artifact ID
                    switch (pluginArtifactId.trim()) {
                        case "vscode-car-plugin":
                            pomDetailsResponse.setProjectBuildPluginVersion(pluginVersion, range);
                            break;
                        case "mi-container-config-mapper":
                            pomDetailsResponse.setMiContainerPluginVersion(new Node(pluginVersion, range));
                            break;
                        case "synapse-unit-test-maven-plugin":
                            pomDetailsResponse.setUnitTestPluginVersion(new Node(pluginVersion, range));
                            break;
                    }
                    isPlugin = false; // Reset flag
                    break;
            }
        } else if (isDependency) {
            switch (qName) {
                case "groupId":
                    groupId = new Node( StringUtils.getString(value), getRange(valueStartLine, valueStartColumn,
                            valueEndLine, valueEndColumn - closingTagLength));
                    break;
                case "artifactId":
                    artifactId = new Node( StringUtils.getString(value), getRange(valueStartLine, valueStartColumn,
                            valueEndLine, valueEndColumn - closingTagLength));
                    break;
                case "version":
                    version = new Node( StringUtils.getString(value), getRange(valueStartLine, valueStartColumn,
                            valueEndLine, valueEndColumn - closingTagLength));
                    break;
                case "type":
                    dependencyType = value;
                    break;
                case "dependency":
                    Map<String, Node> dependency = new HashMap<>();;
                    dependency.put("groupId", groupId);
                    dependency.put("artifactId", artifactId);
                    dependency.put("version", version);
                    dependency.put("fullRange", new Node("", getRange(dependencyStartLine, dependencyStartColumn,
                            valueEndLine, valueEndColumn)));
                    if (dependencyType.equals("zip")) {
                        pomDetailsResponse.setConnectorDependencies(dependency);
                    } else {
                        pomDetailsResponse.setOtherDependencies(dependency);
                    }
                    pomDetailsResponse.setLastDependencyEndTagRange(getRange(valueEndLine,
                            valueEndColumn - closingTagLength, valueEndLine, valueEndColumn));
                    isDependency = false; // Reset flag
                    break;
            }
        } else if (isRepository) {
            if ("repository".equals(qName)) {
                isRepository = false;
            }
        } else if (isPluginRepository) {
            if ("pluginRepository".equals(qName)) {
                isPluginRepository = false;
            }
        } else if (isProperties) {
            Range range = getRange(valueStartLine, valueStartColumn, valueEndLine,
                    valueEndColumn - closingTagLength);
            switch (qName) {
                case "project.runtime.version":
                    pomDetailsResponse.setRuntimeVersion(new Node(value, range));
                    break;
                case "keystore.type":
                    pomDetailsResponse.setKeyStoreType(new Node(value, range));
                    break;
                case "keystore.name":
                    pomDetailsResponse.setKeyStoreName(new Node(value, range));
                    break;
                case "keystore.password":
                    pomDetailsResponse.setKeyStorePassword(new Node(value, range));
                    break;
                case "keystore.alias":
                    pomDetailsResponse.setKeyStoreAlias(new Node(value, range));
                    break;
                case "ciphertool.enable":
                    pomDetailsResponse.setCipherToolEnable(new Node(value, range));
                    break;
                case "dockerfile.base.image":
                    pomDetailsResponse.setDockerFileBaseImage(new Node(value, range));
                    break;
                case "properties":
                    isProperties = false;
                    break;
            }
        } else {
            Range range = getRange(valueStartLine, valueStartColumn, valueEndLine,
                    valueEndColumn - closingTagLength);
            switch (qName) {
                case "groupId":
                    pomDetailsResponse.setProjectGroupId(new Node(value, range));
                    break;
                case "artifactId":
                    pomDetailsResponse.setProjectArtifactId(new Node(value, range));
                    break;
                case "version":
                    pomDetailsResponse.setProjectVersion(new Node(value, range));
                    break;
                case "description":
                    pomDetailsResponse.setProjectDescription(new Node(value, range));
                    break;
                case "name":
                    pomDetailsResponse.setProjectName(new Node(value, range));
                    break;
            }
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        // Append character data to buffer
        contentBuffer.append(new String(ch, start, length));
    }

    private void printWithRange(String label, String value, int startLine, int startColumn, int endLine, int endColumn) {
        System.out.printf("%s: %s [Start: Line %d, Column %d, End: Line %d, Column %d]%n",
                label, value, startLine, startColumn, endLine, endColumn);
    }

    private Range getRange(int startLine, int startColumn, int endLine, int endColumn) {
        return new Range(new Position(startLine, startColumn), new Position(endLine, endColumn));
    }
}
