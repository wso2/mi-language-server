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
        try {
            File pomFile = new File(projectUri + File.separator + Constants.POM_FILE);
            if (!isPomFileExist(pomFile)) {
                return null;
            }
            List<String> lines = Files.readAllLines(pomFile.toPath());
            int index = request.range.getLeft().getStart().getLine();
            List<String> newLines = new ArrayList<>(lines);
            String[] newContentLines = request.value.split("\n");
            for (int i = newContentLines.length - 1; i >= 0; i--) {
                newLines.add(index, newContentLines[i]);
            }
            return String.join("\n", newLines);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error adding the dependency to the POM file: " + e.getMessage());
            return null;
        }
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
}

class PluginHandler extends DefaultHandler {
    private Locator locator;
    private final StringBuilder contentBuffer = new StringBuilder();
    private boolean isDependency, isPlugin, isRepository, isPluginRepository, isProperties, isImages;
    private int valueStartLine, valueStartColumn, dependencyStartLine, dependencyStartColumn;

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
        this.locator = locator;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        contentBuffer.setLength(0);
        valueStartLine = locator.getLineNumber();
        valueStartColumn = locator.getColumnNumber();
        if (Constants.DEPENDENCY.equals(qName)) {
            isDependency = true;
            groupId = new Node();
            artifactId = new Node();
            version = new Node();
            dependencyStartLine = locator.getLineNumber();
            dependencyStartColumn = locator.getColumnNumber() - (qName.length() + 2);
            dependencyType = "";
        } else if (Constants.PLUGIN.equals(qName)) {
            isPlugin = true;
            pluginArtifactId = "";
            pluginVersion = "";
            range = new Range();
        } else if (Constants.REPOSITORY.equals(qName)) {
            isRepository = true;
        } else if (Constants.PLUGIN_REPOSITORY.equals(qName)) {
            isPluginRepository = true;
        } else if (Constants.PROPERTIES.equals(qName)) {
            isProperties = true;
        } else if (Constants.IMAGES.equals(qName) && isPlugin) {
            isImages = true;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        String value = contentBuffer.toString().trim();
        int closingTagLength = qName.length() + 3;
        int valueEndLine = locator.getLineNumber();
        int valueEndColumn = locator.getColumnNumber();
        if (isPlugin) {
            processPlugins(qName, value, valueStartLine, valueStartColumn, valueEndLine, valueEndColumn,
                    closingTagLength);
        } else if (isDependency) {
            processDependencies(qName, value, valueStartLine, valueStartColumn, valueEndLine, valueEndColumn,
                    closingTagLength);
        } else if (isRepository) {
            if (Constants.REPOSITORY.equals(qName)) {
                isRepository = false;
            }
        } else if (isPluginRepository) {
            if (Constants.PLUGIN_REPOSITORY.equals(qName)) {
                isPluginRepository = false;
            }
        } else if (isProperties) {
            processProperties(qName, value, valueStartLine, valueStartColumn, valueEndLine,
                    valueEndColumn - closingTagLength);
        } else {
            processPrimaryDetails(qName, value, valueStartLine, valueStartColumn, valueEndLine,
                    valueEndColumn - closingTagLength);

        }
    }

    private void processPlugins(String qName, String value, int valueStartLine, int valueStartColumn,
                                int valueEndLine, int valueEndColumn, int closingTagLength) {
        switch (qName) {
            case Constants.ARTIFACT_ID:
                pluginArtifactId = value;
                break;
            case Constants.VERSION:
                pluginVersion = value;
                range = getRange(valueStartLine, valueStartColumn, valueEndLine,
                        valueEndColumn - closingTagLength);
                break;
            case Constants.NAME:
                if (pluginArtifactId.equals(Constants.DOCKER_MAVEN_PLUGIN)) {
                    pomDetailsResponse.setDockerName(new Node(value, getRange(valueStartLine, valueStartColumn,
                            valueEndLine, valueEndColumn - closingTagLength)));
                }
                break;
            case Constants.IMAGE:
                isImages = false;
                break;
            case Constants.TEST_SERVER_TYPE:
                pomDetailsResponse.setServerType(new Node(value, getRange(valueStartLine, valueStartColumn,
                        valueEndLine, valueEndColumn - closingTagLength)));
                break;
            case Constants.TEST_SERVER_HOST:
                pomDetailsResponse.setServerHost(new Node(value, getRange(valueStartLine, valueStartColumn,
                        valueEndLine, valueEndColumn - closingTagLength)));
                break;
            case Constants.TEST_SERVER_PORT:
                pomDetailsResponse.setServerPort(new Node(value, getRange(valueStartLine, valueStartColumn,
                        valueEndLine, valueEndColumn - closingTagLength)));
                break;
            case Constants.TEST_SERVER_PATH:
                pomDetailsResponse.setServerPath(new Node(value, getRange(valueStartLine, valueStartColumn,
                        valueEndLine, valueEndColumn - closingTagLength)));
                break;
            case Constants.TEST_SERVER_VERSION:
                pomDetailsResponse.setServerVersion(new Node(value, getRange(valueStartLine, valueStartColumn,
                        valueEndLine, valueEndColumn - closingTagLength)));
                break;
            case Constants.TEST_SERVER_DOWNLOAD_LINK:
                pomDetailsResponse.setServerDownloadLink(new Node(value, getRange(valueStartLine, valueStartColumn,
                        valueEndLine, valueEndColumn - closingTagLength)));
                break;
            case Constants.PLUGIN:
                switch (pluginArtifactId.trim()) {
                    case Constants.VSCODE_CAR_PLUGIN:
                        pomDetailsResponse.setProjectBuildPluginVersion(pluginVersion, range);
                        break;
                    case Constants.MI_CONTAINER_CONFIG_MAPPER:
                        pomDetailsResponse.setMiContainerPluginVersion(new Node(pluginVersion, range));
                        break;
                    case Constants.SYNAPSE_UNIT_TEST_MAVEN_PLUGIN:
                        pomDetailsResponse.setUnitTestPluginVersion(new Node(pluginVersion, range));
                        break;
                }
                isPlugin = false;
                break;
        }
    }

    private void processDependencies(String qName, String value, int valueStartLine, int valueStartColumn,
                                   int valueEndLine, int valueEndColumn, int closingTagLength) {
        switch (qName) {
            case Constants.GROUP_ID:
                groupId = new Node( StringUtils.getString(value), getRange(valueStartLine, valueStartColumn,
                        valueEndLine, valueEndColumn - closingTagLength));
                break;
            case Constants.ARTIFACT_ID:
                artifactId = new Node( StringUtils.getString(value), getRange(valueStartLine, valueStartColumn,
                        valueEndLine, valueEndColumn - closingTagLength));
                break;
            case Constants.VERSION:
                version = new Node( StringUtils.getString(value), getRange(valueStartLine, valueStartColumn,
                        valueEndLine, valueEndColumn - closingTagLength));
                break;
            case Constants.TYPE:
                dependencyType = value;
                break;
            case Constants.DEPENDENCY:
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
                isDependency = false;
                break;
        }
    }

    private void processProperties(String qName, String value, int valueStartLine, int valueStartColumn,
                                   int valueEndLine, int valueEndColumn) {
        Range range = getRange(valueStartLine, valueStartColumn, valueEndLine, valueEndColumn);
        switch (qName) {
            case Constants.PROJECT_RUNTIME_VERSION:
                pomDetailsResponse.setRuntimeVersion(new Node(value, range));
                break;
            case Constants.KEY_STORE_TYPE:
                pomDetailsResponse.setKeyStoreType(new Node(value, range));
                break;
            case Constants.KEY_STORE_NAME:
                pomDetailsResponse.setKeyStoreName(new Node(value, range));
                break;
            case Constants.KEY_STORE_PASSWORD:
                pomDetailsResponse.setKeyStorePassword(new Node(value, range));
                break;
            case Constants.KEY_STORE_ALIAS:
                pomDetailsResponse.setKeyStoreAlias(new Node(value, range));
                break;
            case Constants.CIPHER_TOOL_ENABLE:
                pomDetailsResponse.setCipherToolEnable(new Node(value, range));
                break;
            case Constants.DOCKER_FILE_BASE_IMAGE:
                pomDetailsResponse.setDockerFileBaseImage(new Node(value, range));
                break;
            case Constants.PROPERTIES:
                isProperties = false;
                break;
        }
    }

    private void processPrimaryDetails(String qName, String value, int valueStartLine, int valueStartColumn,
                                       int valueEndLine, int valueEndColumn) {
        Range range = getRange(valueStartLine, valueStartColumn, valueEndLine, valueEndColumn);
        switch (qName) {
            case Constants.GROUP_ID:
                pomDetailsResponse.setProjectGroupId(new Node(value, range));
                break;
            case Constants.ARTIFACT_ID:
                pomDetailsResponse.setProjectArtifactId(new Node(value, range));
                break;
            case Constants.VERSION:
                pomDetailsResponse.setProjectVersion(new Node(value, range));
                break;
            case Constants.DESCRIPTION:
                pomDetailsResponse.setProjectDescription(new Node(value, range));
                break;
            case Constants.NAME:
                pomDetailsResponse.setProjectName(new Node(value, range));
                break;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        contentBuffer.append(new String(ch, start, length));
    }

    private Range getRange(int startLine, int startColumn, int endLine, int endColumn) {
        return new Range(new Position(startLine, startColumn), new Position(endLine, endColumn));
    }
}
