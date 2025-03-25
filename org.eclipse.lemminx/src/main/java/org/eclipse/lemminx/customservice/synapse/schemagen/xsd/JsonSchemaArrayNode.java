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
 * The JsonSchemaArrayNode class represents a JSON Schema node for array types.
 * It extends the JsonSchemaNode class and provides methods to manage array items.
 */
public class JsonSchemaArrayNode extends JsonSchemaNode {

    private final ArrayNode items;

    /**
     * Constructs a new JsonSchemaArrayNode with the specified ID and parent node.
     *
     * @param id   the identifier for the JSON Schema node
     * @param node the parent JSON Schema object node
     */
    public JsonSchemaArrayNode(String id, ObjectNode node) {

        super(node, id + Utils.ARRAY_FIRST_ELEMENT_IDENTIFIER);
        items = JsonNodeFactory.instance.arrayNode();

        node.put(Utils.ID, id);
        node.put(Utils.TYPE, Utils.ARRAY);
        node.set(Utils.ITEMS, items);
    }

    /**
     * Adds an item to the array node of this JSON Schema array node.
     *
     * @param item the JSON Schema node to add as an item
     */
    public void addItem(JsonSchemaNode item) {

        items.add(item.getNode());
    }
}
