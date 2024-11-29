package org.eclipse.lemminx.customservice.synapse.mediatorService.mediators;

import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.MediatorProperty;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.transformation.Jsontransform;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsontransformMediator {
    public static Either<Map<String, Object>, Map<Range, Map<String, Object>>> processData430(Map<String, Object> data,
                                                                                           Jsontransform jsonTransform,
                                                                                           List<String> dirtyFields) {

        List<Object> jsonTransformProperties = data.get("jsonTransformProperties") instanceof List<?>
                ? (List<Object>) data.get("jsonTransformProperties")
                : new ArrayList<>();
        List<Map<String, String>> transformedProperties = new ArrayList<>();

        for (Object propertyObj : jsonTransformProperties) {
            if (propertyObj instanceof List<?>) {
                List<Object> property = (List<Object>) propertyObj;
                Map<String, String> propertyMap = new HashMap<>();
                propertyMap.put("propertyName", property.get(0) instanceof String ? (String) property.get(0) : "");
                propertyMap.put("propertyValue", property.get(1) instanceof String ? (String) property.get(1) : "");
                transformedProperties.add(propertyMap);
            }
        }
        data.put("jsonTransformProperties", transformedProperties);

        if (!transformedProperties.isEmpty()) {
            data.put("hasProperties", true);
        }

        return Either.forLeft(data);
    }


    public static Map<String, Object> getDataFromST430(Jsontransform node) {

        Map<String, Object> data = new HashMap<>();

        data.put("schema", node.getSchema());
        data.put("description", node.getDescription());

        if (node.getProperty() != null) {
            List<List<String>> jsonTransformProperties = new ArrayList<>();
            for (MediatorProperty property : node.getProperty()) {
                jsonTransformProperties.add(List.of(property.getName(), property.getValue()));
            }
            data.put("jsonTransformProperties", jsonTransformProperties);
        } else {
            data.put("jsonTransformProperties", new ArrayList<>());
        }

        return data;
    }

}
