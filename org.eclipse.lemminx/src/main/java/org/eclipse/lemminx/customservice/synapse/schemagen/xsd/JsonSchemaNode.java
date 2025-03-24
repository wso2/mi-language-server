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
import org.apache.commons.lang3.StringUtils;

/**
 * The JsonSchemaNode class represents a generic JSON Schema node.
 * It provides methods to manage the properties and structure of the node.
 */
public class JsonSchemaNode {

    protected String name;
    protected ObjectNode node;
    private String id;

    /**
     * Constructs a new JsonSchemaNode with an empty JSON object node.
     */
    public JsonSchemaNode() {

        this(JsonNodeFactory.instance.objectNode(), null, null, null);
    }

    /**
     * Constructs a new JsonSchemaNode with the specified JSON object node.
     */
    public JsonSchemaNode(ObjectNode node) {

        this(node, null, null, null);
    }

    /**
     * Constructs a new JsonSchemaNode with the specified ID.
     *
     * @param id the identifier for the JSON Schema node
     */
    public JsonSchemaNode(String id) {

        this(JsonNodeFactory.instance.objectNode(), id, null, null);
    }

    /**
     * Constructs a new JsonSchemaNode with the specified JSON object node and ID.
     *
     * @param node the JSON object node
     * @param id   the identifier for the JSON Schema node
     */
    public JsonSchemaNode(ObjectNode node, String id) {

        this(node, id, null, null);
    }

    /**
     * Constructs a new JsonSchemaNode with the specified ID and type.
     *
     * @param id   the identifier for the JSON Schema node
     * @param type the type of the JSON Schema node
     */
    public JsonSchemaNode(String id, String type) {

        this(JsonNodeFactory.instance.objectNode(), id, type, null);
    }

    /**
     * Constructs a new JsonSchemaNode with the specified JSON object node, ID, type, and name.
     *
     * @param node the JSON object node
     * @param id   the identifier for the JSON Schema node
     * @param type the type of the JSON Schema node
     * @param name the name of the JSON Schema node
     */
    public JsonSchemaNode(ObjectNode node, String id, String type, String name) {

        this.id = id;
        this.name = name;
        this.node = node;
        put(Utils.ID, id);
        put(Utils.TYPE, type);
    }

    /**
     * Returns the identifier of the JSON Schema node.
     *
     * @return the identifier of the JSON Schema node
     */
    public String getId() {

        return id;
    }

    /**
     * Sets the type of the JSON Schema node.
     *
     * @param type the type to set
     */
    public void setType(String type) {

        node.put(Utils.TYPE, type);
    }

    /**
     * Returns the JSON object node.
     *
     * @return the JSON object node
     */
    public ObjectNode getNode() {

        return node;
    }

    /**
     * Updates the JSON Schema node, optionally adding a title.
     *
     * @param addTitle flag indicating whether to add a title
     */
    public void update(boolean addTitle) {

        if (addTitle && StringUtils.isNotEmpty(name)) {
            node.put(Utils.TITLE, name);
        }
    }

    /**
     * Sets a property of the JSON object node with the specified key and value.
     *
     * @param key   the key of the property
     * @param value the value of the property
     */
    public void set(String key, ObjectNode value) {

        node.set(key, value);
    }

    /**
     * Sets a property of the JSON object node with the specified key and array value.
     *
     * @param key   the key of the property
     * @param value the array value of the property
     */
    public void set(String key, ArrayNode value) {

        node.set(key, value);
    }

    /**
     * Puts a property of the JSON object node with the specified key and string value.
     *
     * @param key   the key of the property
     * @param value the string value of the property
     */
    public void put(String key, String value) {

        if (value != null) {
            node.put(key, value);
        }
    }
}
