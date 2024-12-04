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
import org.eclipse.lemminx.customservice.synapse.parser.UpdateDependency;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PomParser {

    private static final Logger LOGGER = Logger.getLogger(PomParser.class.getName());
    private static OverviewPageDetailsResponse pomDetailsResponse;

    private static boolean hasDependencies = false;

    public static void getPomDetails(String projectUri, OverviewPageDetailsResponse detailsResponse) {
        pomDetailsResponse = detailsResponse;
        extractPomContent(projectUri);
    }

    public static Either<UpdateDependency, List<UpdateDependency>> updateDependency(
            String projectUri, Either<DependencyDetails, List<DependencyDetails>> dependencyDetails) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.newDocument();
            List<String> pomContent;
            if (dependencyDetails.isLeft()) {
                DependencyDetails details = dependencyDetails.getLeft();
                if (details.range != null) {
                    return Either.forLeft(new UpdateDependency(
                            elementToString(createDependencyElement(document, details)), details.range));
                } else {
                    pomContent = readPom(projectUri);
                    assert pomContent != null;
                    Range range = getRange(pomContent);
                    return Either.forLeft(getUpdateDependency(document, details, range));
                }
            } else {
                List<UpdateDependency> updateDependencyResponse = new ArrayList<>();
                List<DependencyDetails> dependencyDetail = dependencyDetails.getRight();
                boolean checkPomFile = false;
                Range range = null;
                for (DependencyDetails dependency : dependencyDetail) {
                    if (dependency.range != null) {
                        updateDependencyResponse.add(new UpdateDependency(
                                elementToString(createDependencyElement(document, dependency)), dependency.range));
                    } else {
                        if (!checkPomFile) {
                            pomContent = readPom(projectUri);
                            assert pomContent != null;
                            range = getRange(pomContent);
                            if (!hasDependencies) {
                                Position start = range.getStart();
                                int characterLength = start.getCharacter();
                                updateDependencyResponse.add(
                                        new UpdateDependency(
                                                elementToString(document.createElement("dependencies")),
                                                new Range(start, new Position(start.getLine() + 1,
                                                        characterLength + "</dependencies>".length()))));
                                range = new Range(new Position(start.getLine() + 1, characterLength + 4),
                                        new Position());
                            }
                            checkPomFile = true;
                        }
                        Element processedDependency = createDependencyElement(document, dependency);
                        String dependenciesString = elementToString(processedDependency);
                        Position start = range.getStart();
                        int length = dependenciesString.split("\n").length;
                        int endLine = start.getLine() + length;
                        int characterLength = start.getCharacter();
                        range = new Range(new Position(endLine + 1, characterLength), new Position());
                        updateDependencyResponse.add(
                                new UpdateDependency(dependenciesString, new Range(start,
                                        new Position(endLine, characterLength + "</dependency>".length()))));
                    }
                }
                return Either.forRight(updateDependencyResponse);
            }
        } catch (ParserConfigurationException e) {
            LOGGER.log(Level.SEVERE, "Error process : " + e.getMessage());
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
        for (String content : pomContent) {
            if (content.trim().contains("</dependencies>")) {
                hasDependencies = true;
                return new Range(new Position(i, content.indexOf("</dependencies>")), new Position());
            }
            if (content.trim().contains("</project>")) {
                return new Range(new Position(i, content.indexOf("</project>")), new Position());
            }
            i++;
        }
        return new Range();
    }

    private static UpdateDependency getUpdateDependency(Document document, DependencyDetails dependencyDetails,
                                                        Range range) {
        String dependenciesString;
        if (hasDependencies) {
            Element dependency = createDependencyElement(document, dependencyDetails);
            dependenciesString = elementToString(dependency);
        } else {
            Element dependencies = document.createElement("dependencies");
            dependencies.appendChild(createDependencyElement(document, dependencyDetails));
            dependenciesString = elementToString(dependencies);
        }
        Position start = range.getStart();
        int length = dependenciesString.split("\n").length;
        return new UpdateDependency(dependenciesString, new Range(start,
                new Position((start.getLine() + length - 1), start.getCharacter() +
                        "</dependencies>".length() - 1)));
    }

    private static Element createDependencyElement(Document document, DependencyDetails dependencyDetails) {
        Element dependency = document.createElement("dependency");
        Element groupId = document.createElement("groupId");
        groupId.setTextContent(dependencyDetails.groupId);
        dependency.appendChild(groupId);
        Element artifactId = document.createElement("artifactId");
        artifactId.setTextContent(dependencyDetails.artifact);
        dependency.appendChild(artifactId);
        Element version = document.createElement("version");
        version.setTextContent(dependencyDetails.version);
        dependency.appendChild(version);
        if (dependencyDetails.type != null) {
            Element type = document.createElement("type");
            version.setTextContent(dependencyDetails.type);
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
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            DOMSource domSource = new DOMSource(element);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            transformer.transform(domSource, result);
            return writer.toString().trim();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
