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

/**
 * The JsonSchemaOneOfNode class represents a JSON Schema node for the "oneOf" keyword.
 * It extends the JsonSchemaNode class and provides methods to manage the "oneOf" schema.
 */
public class JsonSchemaOneOfNode extends JsonSchemaNode {

    /**
     * Constructs a new JsonSchemaOneOfNode with an empty JSON object node.
     */
    public JsonSchemaOneOfNode() {

        node = JsonNodeFactory.instance.objectNode();
    }

    /**
     * Constructs a new JsonSchemaOneOfNode with the specified JSON object node and name.
     *
     * @param node the JSON object node
     * @param name the name of the JSON Schema node
     */
    public JsonSchemaOneOfNode(ObjectNode node, String name) {

        super(node);
        this.name = name;
    }

    /**
     * Sets the JSON array node representing the content of oneOf.
     *
     * @param oneOf the JSON array node containing objects
     */
    public void setOneOf(ArrayNode oneOf) {

        node.set(Utils.ONE_OF, oneOf);
    }
}
