/*
 *   Copyright (c) 2025, WSO2 LLC. (http://www.wso2.com).
 *
 *   WSO2 LLC. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 */

package org.eclipse.lemminx.customservice.synapse.schemagen.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.xerces.dom.DOMInputImpl;
import org.apache.xerces.impl.xs.XMLSchemaLoader;

import org.apache.commons.io.FileUtils;
import org.apache.xerces.xs.XSConstants;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSModel;
import org.apache.xerces.xs.XSNamedMap;
import org.eclipse.lemminx.customservice.synapse.schemagen.xsd.TypeProcessor;
import org.eclipse.lemminx.customservice.synapse.schemagen.xsd.TypeProcessorFactory;
import org.eclipse.lemminx.customservice.synapse.schemagen.xsd.Utils;

import java.io.File;
import java.io.IOException;

public class SchemaGeneratorForXSD extends SchemaGeneratorForXML implements ISchemaGenerator {

    private static final String SCHEMA_ID = "$schema";
    private static final String SCHEMA_URL = "http://wso2.org/json-schema/wso2-data-mapper-v5.0.0/schema#";
    private static final String ROOT_ID = "http://wso2jsonschema.org";

    @Override
    public String getSchemaResourcePath(String filePath, FileType type, String delimiter) throws IOException {
        String entireFileText = FileUtils.readFileToString(new File(filePath));
        return getSchemaContent(entireFileText, type, delimiter);
    }

    @Override
    public String getSchemaContent(String content, FileType type, String delimiter) throws IOException {

        return generateJsonSchemaFromXsd(content);
    }

    public String generateJsonSchemaFromXsd(String xsdContent) throws JsonProcessingException {
        XMLSchemaLoader schemaLoader = new XMLSchemaLoader();
        XSModel xsModel = schemaLoader.load(new DOMInputImpl(null, null, null, xsdContent, null));
        JsonNode jsonSchemaElements = convertXsModelToJsonSchemaElements(xsModel);

        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(jsonSchemaElements);
    }

    public JsonNode convertXsModelToJsonSchemaElements(XSModel xsModel) {
        ObjectNode rootNode = JsonNodeFactory.instance.objectNode();
        rootNode.put(SCHEMA_ID, SCHEMA_URL);

        XSNamedMap elements = xsModel.getComponents(XSConstants.ELEMENT_DECLARATION);
        if (elements.getLength() == 1) {
            XSElementDeclaration rootElement = (XSElementDeclaration) elements.item(0);
            processRootElement(rootElement, rootNode);
        } else if (elements.getLength() > 1) {
            processMultipleRootElements(elements, rootNode);
        }
        return rootNode;
    }

    private void processRootElement(XSElementDeclaration rootElement, ObjectNode rootNode) {
        TypeProcessor processor = TypeProcessorFactory.getTypeProcessor(rootElement);
        processor.processRootType(rootElement, rootNode, ROOT_ID, true);
    }

    private void processMultipleRootElements(XSNamedMap elements, ObjectNode rootNode) {
        ArrayNode oneOfArray = JsonNodeFactory.instance.arrayNode();
        for (Object elementObj : elements.values()) {
            if (elementObj instanceof XSElementDeclaration) {
                XSElementDeclaration element = (XSElementDeclaration) elementObj;
                ObjectNode elementNode = JsonNodeFactory.instance.objectNode();

                TypeProcessor processor = TypeProcessorFactory.getTypeProcessor(element);
                processor.processType(element, null, elementNode, ROOT_ID + Utils.ID_VALUE_SEPERATOR + element.getName(), false);
                elementNode.put(Utils.TITLE, element.getName());
                oneOfArray.add(elementNode);
            }
        }
        rootNode.set(Utils.ONE_OF, oneOfArray);
    }
}
