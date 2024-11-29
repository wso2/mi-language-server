package org.eclipse.lemminx.customservice.synapse.mediatorService.mediators;

import org.eclipse.lemminx.customservice.synapse.mediatorService.MediatorUtils;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.MediatorProperty;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.extension.Class;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassMediator {
    public static Either<Map<String, Object>, Map<Range, Map<String, Object>>> processData430(Map<String, Object> data,
                                                                                           Class aClass,
                                                                                           List<String> dirtyFields) {
        List<Map<String, Object>> properties = new ArrayList<>();
        List<Object> propertiesData = data.get("properties") instanceof List ? (List<Object>) data.get("properties") : new ArrayList<>();
        for (Object propertyObj : propertiesData) {
            if (propertyObj instanceof List<?>) {
                List<Object> property = (List<Object>) propertyObj;
                Map<String, Object> propertyData = property.get(1) instanceof Map ? (Map<String, Object>) property.get(1) : null;
                if (propertyData != null) {
                    String propertyName = property.get(0).toString();
                    List<String> namespaces = propertyData.get("namespaces") instanceof List ? (List<String>) propertyData.get("namespaces") : null;
                    Map<String, Object> propertyValue = new HashMap<>(Map.of(
                            "propertyName", property.get(0),
                            "value", propertyData.get("value"),
                            "isExpression", propertyData.get("isExpression")
                    ));
                    if (namespaces != null) {
                        propertyValue.put("namespaces", namespaces);
                    }
                    propertyValue.put("propertyName", propertyName);
                    properties.add(propertyValue);
                }
            }
        }

        data.put("properties", properties);
        return Either.forLeft(data);

    }

    public static Map<String, Object> getDataFromST430(Class node) {
        Map<String, Object> data = new HashMap<>();
        data.put("description",node.getDescription());
        data.put("className", node.getName());
        if (node.getProperty() != null) {
            List<List<Object>> properties = new ArrayList<>();

            for (MediatorProperty property : node.getProperty()) {
                boolean isExpression = property.getValue() == null;
                Map<String, Object> propertyDetails = new HashMap<>();
                propertyDetails.put("isExpression", isExpression);
                propertyDetails.put("value", isExpression ? property.getExpression() : property.getValue());

                if (isExpression && property.getNamespaces() != null) {
                    propertyDetails.put("namespaces", MediatorUtils.transformNamespaces(property.getNamespaces()));
                }

                properties.add(List.of(property.getName(), propertyDetails));
            }

            data.put("properties", properties);
        }
        return data;
    }

}
