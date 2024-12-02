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
import org.eclipse.lemminx.utils.StringUtils;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.helpers.DefaultHandler;

import java.util.HashMap;
import java.util.Map;

public class PluginHandler extends DefaultHandler {
    private Locator locator;
    private final StringBuilder contentBuffer = new StringBuilder();
    private boolean isDependency, isPlugin, isRepository, isPluginRepository, isProperties;
    private int valueStartLine, valueStartColumn, dependencyStartLine, dependencyStartColumn;

    private String pluginArtifactId, pluginVersion, dependencyType = "";
    private Range range;
    private Node groupId;
    private Node artifactId;
    private Node version;

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
                    pomDetailsResponse.setDockerName(new Node(value, Either.forLeft(getRange(valueStartLine,
                            valueStartColumn, valueEndLine, valueEndColumn - closingTagLength))));
                }
                break;
            case Constants.TEST_SERVER_TYPE:
                pomDetailsResponse.setServerType(new Node(value, Either.forLeft(getRange(valueStartLine,
                        valueStartColumn, valueEndLine, valueEndColumn - closingTagLength))));
                break;
            case Constants.TEST_SERVER_HOST:
                pomDetailsResponse.setServerHost(new Node(value, Either.forLeft(getRange(valueStartLine,
                        valueStartColumn, valueEndLine, valueEndColumn - closingTagLength))));
                break;
            case Constants.TEST_SERVER_PORT:
                pomDetailsResponse.setServerPort(new Node(value, Either.forLeft(getRange(valueStartLine,
                        valueStartColumn, valueEndLine, valueEndColumn - closingTagLength))));
                break;
            case Constants.TEST_SERVER_PATH:
                pomDetailsResponse.setServerPath(new Node(value, Either.forLeft(getRange(valueStartLine,
                        valueStartColumn, valueEndLine, valueEndColumn - closingTagLength))));
                break;
            case Constants.TEST_SERVER_VERSION:
                pomDetailsResponse.setServerVersion(new Node(value, Either.forLeft(getRange(valueStartLine,
                        valueStartColumn, valueEndLine, valueEndColumn - closingTagLength))));
                break;
            case Constants.TEST_SERVER_DOWNLOAD_LINK:
                pomDetailsResponse.setServerDownloadLink(new Node(value, Either.forLeft(getRange(valueStartLine,
                        valueStartColumn, valueEndLine, valueEndColumn - closingTagLength))));
                break;
            case Constants.PLUGIN:
                switch (pluginArtifactId.trim()) {
                    case Constants.VSCODE_CAR_PLUGIN:
                        pomDetailsResponse.setProjectBuildPluginVersion(pluginVersion, range);
                        break;
                    case Constants.MI_CONTAINER_CONFIG_MAPPER:
                        pomDetailsResponse.setMiContainerPluginVersion(new Node(pluginVersion, Either.forLeft(range)));
                        break;
                    case Constants.SYNAPSE_UNIT_TEST_MAVEN_PLUGIN:
                        pomDetailsResponse.setUnitTestPluginVersion(new Node(pluginVersion, Either.forLeft(range)));
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
                groupId = new Node( StringUtils.getString(value), Either.forLeft(getRange(valueStartLine,
                        valueStartColumn, valueEndLine, valueEndColumn - closingTagLength)));
                break;
            case Constants.ARTIFACT_ID:
                artifactId = new Node( StringUtils.getString(value), Either.forLeft(getRange(valueStartLine,
                        valueStartColumn, valueEndLine, valueEndColumn - closingTagLength)));
                break;
            case Constants.VERSION:
                version = new Node( StringUtils.getString(value), Either.forLeft(getRange(valueStartLine,
                        valueStartColumn, valueEndLine, valueEndColumn - closingTagLength)));
                break;
            case Constants.TYPE:
                dependencyType = value;
                break;
            case Constants.DEPENDENCY:
                Map<String, Node> dependency = new HashMap<>();;
                dependency.put(Constants.GROUP_ID, groupId);
                dependency.put(Constants.ARTIFACT_ID, artifactId);
                dependency.put(Constants.VERSION, version);
                dependency.put(Constants.FULL_RANGE, new Node("", Either.forLeft(getRange(dependencyStartLine,
                        dependencyStartColumn, valueEndLine, valueEndColumn))));
                if (dependencyType.equals(Constants.ZIP)) {
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
                pomDetailsResponse.setRuntimeVersion(new Node(value, Either.forLeft(range)));
                break;
            case Constants.KEY_STORE_TYPE:
                pomDetailsResponse.setKeyStoreType(new Node(value, Either.forLeft(range)));
                break;
            case Constants.KEY_STORE_NAME:
                pomDetailsResponse.setKeyStoreName(new Node(value, Either.forLeft(range)));
                break;
            case Constants.KEY_STORE_PASSWORD:
                pomDetailsResponse.setKeyStorePassword(new Node(value, Either.forLeft(range)));
                break;
            case Constants.KEY_STORE_ALIAS:
                pomDetailsResponse.setKeyStoreAlias(new Node(value, Either.forLeft(range)));
                break;
            case Constants.CIPHER_TOOL_ENABLE:
                pomDetailsResponse.setCipherToolEnable(new Node(value, Either.forLeft(range)));
                break;
            case Constants.DOCKER_FILE_BASE_IMAGE:
                pomDetailsResponse.setDockerFileBaseImage(new Node(value, Either.forLeft(range)));
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
                pomDetailsResponse.setProjectGroupId(new Node(value, Either.forLeft(range)));
                break;
            case Constants.ARTIFACT_ID:
                pomDetailsResponse.setProjectArtifactId(new Node(value, Either.forLeft(range)));
                break;
            case Constants.VERSION:
                pomDetailsResponse.setProjectVersion(new Node(value, Either.forLeft(range)));
                break;
            case Constants.DESCRIPTION:
                pomDetailsResponse.setProjectDescription(new Node(value, Either.forLeft(range)));
                break;
            case Constants.NAME:
                pomDetailsResponse.setProjectName(new Node(value, Either.forLeft(range)));
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
