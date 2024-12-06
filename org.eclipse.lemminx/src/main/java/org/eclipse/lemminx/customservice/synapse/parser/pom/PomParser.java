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
import org.eclipse.lemminx.customservice.synapse.parser.DependencyDetails;
import org.eclipse.lemminx.customservice.synapse.parser.OverviewPageDetailsResponse;
import org.eclipse.lemminx.customservice.synapse.parser.UpdateDependencyRequest;
import org.eclipse.lemminx.customservice.synapse.parser.UpdateResponse;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.TextEdit;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PomParser {

    private static final Logger LOGGER = Logger.getLogger(PomParser.class.getName());
    private static OverviewPageDetailsResponse pomDetailsResponse;
    private static DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    private static TransformerFactory transformerFactory = TransformerFactory.newInstance();
    private static boolean hasDependencies;

    public static void getPomDetails(String projectUri, OverviewPageDetailsResponse detailsResponse) {
        pomDetailsResponse = detailsResponse;
        extractPomContent(projectUri);
        hasDependencies = false;
    }

    public static UpdateResponse updateDependency(String projectUri, UpdateDependencyRequest request) {
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.newDocument();
            List<String> pomContent;
            UpdateResponse updateResponse= new UpdateResponse();
            Range initialRange;
            Element dependenciesElement = null;
            StringBuilder elementInString = new StringBuilder();
            pomContent = readPom(projectUri);
            assert pomContent != null;
            initialRange = getRange(pomContent);
            if (!hasDependencies) {
                dependenciesElement = document.createElement(Constants.DEPENDENCIES);
            }
            for (DependencyDetails dependency : request.dependencies) {
                if (dependency.getRange() != null) {
                    updateResponse.add(new TextEdit(dependency.getRange(),
                            elementToString(createDependencyElement(document, dependency))));
                } else {
                    if (dependenciesElement != null) {
                        dependenciesElement.appendChild(createDependencyElement(document, dependency));
                    } else {
                        elementInString.append(elementToString(createDependencyElement(document, dependency)));
                    }
                }
            }
            String value;
            if (dependenciesElement != null) {
                value = elementToString(dependenciesElement);
            } else {
                value = elementInString.toString();
            }
            if (value == null) {
                return null;
            }
            String stringValue = removeLineSeparator(value);
            updateResponse.add(new TextEdit(new Range(initialRange.getStart(), new Position(initialRange.getStart().
                    getLine(), initialRange.getStart().getCharacter() + stringValue.length() + 1)), stringValue));
            return updateResponse;
        } catch (ParserConfigurationException e) {
            LOGGER.log(Level.SEVERE, "Error parsing the POM file : " + e.getMessage());
            return null;
        }
    }

    private static List<String> readPom(String projectUri) {
        File pomFile = new File(projectUri + File.separator + Constants.POM_FILE);
        if (!isPomFileExist(pomFile)) {
            return null;
        }
        try {
            return Files.readAllLines(pomFile.toPath());
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error modifying the POM file: " + e.getMessage());
            return null;
        }
    }

    private static Range getRange(List<String> pomContent) {
        int i = 1;
        int dependenciesStartTag = 0;
        int dependenciesCharLength = 0;
        int position = 0;
        int character = 0;
        for (String content : pomContent) {
            String line = content.trim();
            if (line.contains(Constants.DEPENDENCIES_START_TAG)) {
                hasDependencies = true;
                dependenciesStartTag = i;
                dependenciesCharLength = content.indexOf(Constants.DEPENDENCIES_START_TAG) +
                        Constants.DEPENDENCIES_START_TAG.length() + 1;

            }
            if (line.contains(Constants.DEPENDENCY_END_TAG)) {
                hasDependencies = true;
                if (i > position) {
                    position = i;
                    character = content.indexOf(Constants.DEPENDENCY_END_TAG) +
                            Constants.DEPENDENCY_END_TAG.length() + 1;
                }
            }
            if(line.contains(Constants.DEPENDENCIES_END_TAG)) {
                if (position == 0 || (dependenciesStartTag > position)) {
                    position = dependenciesStartTag;
                    character = dependenciesCharLength;
                }
                break;
            }
            if (!hasDependencies) {
                if (content.trim().contains(Constants.PROPERTIES_END_TAG)) {
                    if (i > position) {
                        position = i;
                        character = content.indexOf(Constants.PROPERTIES_END_TAG) +
                                Constants.PROPERTIES_END_TAG.length() + 1;
                    }
                }
            }
            i++;
        }
        return new Range(new Position(position, character), new Position());
    }

    private static Element createDependencyElement(Document document, DependencyDetails dependencyDetails) {
        Element dependency = document.createElement(Constants.DEPENDENCY);
        Element groupId = document.createElement(Constants.GROUP_ID);
        groupId.setTextContent(dependencyDetails.getGroupId());
        dependency.appendChild(groupId);
        Element artifactId = document.createElement(Constants.ARTIFACT_ID);
        artifactId.setTextContent(dependencyDetails.getArtifact());
        dependency.appendChild(artifactId);
        Element version = document.createElement(Constants.VERSION);
        version.setTextContent(dependencyDetails.getVersion());
        dependency.appendChild(version);
        if (dependencyDetails.getType() != null) {
            Element type = document.createElement(Constants.TYPE);
            type.setTextContent(dependencyDetails.getType() );
            dependency.appendChild(type);
        }
        return dependency;
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

    private static String elementToString(Element element) {
        try {
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, Constants.YES);
            transformer.setOutputProperty(OutputKeys.INDENT, Constants.YES);
            DOMSource domSource = new DOMSource(element);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            transformer.transform(domSource, result);
            return writer.toString().trim();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing the XML element: " + e.getMessage());
            return null;
        }
    }

    private static String removeLineSeparator(Element input) {
        return removeLineSeparator(elementToString(input));
    }

    private static String removeLineSeparator(String input) {
        if (input == null) {
            return null;
        }
        return input.replace(System.getProperty(Constants.LINE_SEPARATOR), Constants.EMPTY);
    }
}
