package org.eclipse.lemminx.customservice.synapse.schemagen.xsd;

/**
 * The JsonSchemaElementValueNode class represents a JSON Schema node for element values.
 * It extends the JsonSchemaNode class and provides methods to manage element value nodes.
 */
public class JsonSchemaElementValueNode extends JsonSchemaNode {

    /**
     * Constructs a new JsonSchemaElementValueNode with the specified ID and type.
     *
     * @param id   the identifier for the JSON Schema node
     * @param type the type of the JSON Schema node
     */
    public JsonSchemaElementValueNode(String id, String type) {

        super(id + Utils.ID_VALUE_SEPERATOR + Utils.ELEMENT_VALUE, type);
    }
}
