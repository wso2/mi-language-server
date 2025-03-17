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

package org.eclipse.lemminx.customservice.synapse.schemagen.xsd;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.xerces.xs.StringList;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSParticle;
import org.apache.xerces.xs.XSSimpleTypeDefinition;

import static org.eclipse.lemminx.customservice.synapse.schemagen.xsd.Utils.getTypeName;
import static org.eclipse.lemminx.customservice.synapse.schemagen.xsd.Utils.isElementArray;

/**
 * The SimpleTypeProcessor class processes XML Schema simple type definitions and converts them into JSON Schema nodes.
 */
public class SimpleTypeProcessor implements TypeProcessor {

    /**
     * Processes an XML Schema element declaration and updates the provided JSON Schema object node.
     *
     * @param element          the XML Schema element declaration
     * @param elementStructure the XML Schema particle structure
     * @param node             the JSON Schema object node to update
     * @param id               the identifier for the JSON Schema node
     * @param addTitle         flag indicating if the title should be added
     */
    @Override
    public void processType(XSElementDeclaration element, XSParticle elementStructure, ObjectNode node, String id,
                            boolean addTitle) {

        JsonSchemaNode jsonSchemaNode = new JsonSchemaNode(node, id);
        SimpleTypeProcessor.processSimpleType(element, elementStructure, jsonSchemaNode);
    }

    /**
     * Processes the root XML Schema element declaration and updates the provided JSON Schema object node.
     *
     * @param element  the XML Schema element declaration
     * @param node     the JSON Schema object node to update
     * @param id       the identifier for the JSON Schema node
     * @param addTitle flag indicating if the title should be added
     */
    @Override
    public void processRootType(XSElementDeclaration element, ObjectNode node, String id, boolean addTitle) {

        SimpleTypeProcessor.processRootSimpleType(element, node, id);
    }

    /**
     * Processes the root XML Schema simple type definition and updates the provided JSON Schema object node.
     *
     * @param rootElement the root XML Schema element declaration
     * @param rootNode    the root JSON Schema object node to update
     * @param rootId      the identifier for the root JSON Schema node
     */
    public static void processRootSimpleType(XSElementDeclaration rootElement, ObjectNode rootNode, String rootId) {

        JsonSchemaObjectNode rootObject = new JsonSchemaObjectNode(rootNode, rootId, Utils.ROOT);
        JsonSchemaNode property = new JsonSchemaNode(rootId + Utils.ID_VALUE_SEPERATOR + rootElement.getName());
        rootObject.addProperty(rootElement.getName(), property.getNode());
        rootObject.addRequiredElement(rootElement.getName());
        processSimpleType(rootElement, null, property);
        rootObject.update(true);
    }

    /**
     * Processes an XML Schema simple type definition and updates the provided JSON Schema node.
     *
     * @param element          the XML Schema element declaration
     * @param elementStructure the XML Schema particle structure
     * @param schemaNode       the JSON Schema node to update
     */
    public static void processSimpleType(XSElementDeclaration element, XSParticle elementStructure,
                                         JsonSchemaNode schemaNode) {

        boolean isArray = isElementArray(elementStructure);
        XSSimpleTypeDefinition simpleType = (XSSimpleTypeDefinition) element.getTypeDefinition();
        String type = Utils.mapXsdTypeToJsonType(getTypeName(simpleType));

        if (isArray) {
            JsonSchemaArrayNode jsonSchemaArray = new JsonSchemaArrayNode(schemaNode.getId(), schemaNode.getNode());
            JsonSchemaNode item = new JsonSchemaNode(schemaNode.getId() + Utils.ARRAY_FIRST_ELEMENT_IDENTIFIER, type);
            jsonSchemaArray.addItem(item);
            schemaNode = item;
        } else {
            schemaNode.setType(type);
        }

        StringList lexicalEnumeration = simpleType.getLexicalEnumeration();
        if (lexicalEnumeration.getLength() > 0) {
            ArrayNode enumArray = JsonNodeFactory.instance.arrayNode();
            for (int i = 0; i < lexicalEnumeration.getLength(); i++) {
                enumArray.add(lexicalEnumeration.item(i));
            }
            schemaNode.set(Utils.ENUM, enumArray);
        }
    }
}
