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

        node = JsonNodeFactory.instance.objectNode();
    }

    /**
     * Constructs a new JsonSchemaNode with the specified JSON object node.
     */
    public JsonSchemaNode(ObjectNode node) {

        this.node = node;
    }

    /**
     * Constructs a new JsonSchemaNode with the specified ID.
     *
     * @param id the identifier for the JSON Schema node
     */
    public JsonSchemaNode(String id) {

        this.id = id;
        node = JsonNodeFactory.instance.objectNode();
        node.put(Utils.ID, id);
    }

    /**
     * Constructs a new JsonSchemaNode with the specified JSON object node and ID.
     *
     * @param node the JSON object node
     * @param id   the identifier for the JSON Schema node
     */
    public JsonSchemaNode(ObjectNode node, String id) {

        this.id = id;
        this.node = node;
        node.put(Utils.ID, id);
    }

    /**
     * Constructs a new JsonSchemaNode with the specified ID and type.
     *
     * @param id   the identifier for the JSON Schema node
     * @param type the type of the JSON Schema node
     */
    public JsonSchemaNode(String id, String type) {

        this.id = id;
        this.node = JsonNodeFactory.instance.objectNode();
        node.put(Utils.ID, id);
        node.put(Utils.TYPE, type);
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
        this.node.put(Utils.ID, id);
        this.node.put(Utils.TYPE, type);
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

        node.put(key, value);
    }
}
