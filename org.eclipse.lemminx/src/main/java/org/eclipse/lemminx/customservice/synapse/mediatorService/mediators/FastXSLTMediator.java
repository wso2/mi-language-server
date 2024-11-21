package org.eclipse.lemminx.customservice.synapse.mediatorService.mediators;

import org.eclipse.lemminx.customservice.synapse.mediatorService.MediatorUtils;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.transformation.FastXSLT;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FastXSLTMediator {
    public static Either<Map<String, Object>, Map<Range, Map<String, Object>>> processData(Map<String, Object> data,
                                                                                           FastXSLT fastXSLT,
                                                                                           List<String> dirtyFields) {
        if (data.containsKey("schemaKay") && data.get("schemaKay") instanceof Map<?, ?>) {
            Map<String, Object> schemaKay = (Map<String, Object>) data.get("schemaKay");
            if (Boolean.TRUE.equals(schemaKay.get("isExpression"))) {
                data.put("key", "{" + schemaKay.get("value") + "}");
                data.put("namespaces", schemaKay.get("namespaces"));
            } else {
                data.put("key", schemaKay.get("value"));
            }
        }
        return Either.forLeft(data);

    }

    public static Map<String, Object> getDataFromST(FastXSLT node) {

        Map<String, Object> data = new HashMap<>();
        data.put("description", node.getDescription());
        if (node.getKey() != null) {
            String key = node.getKey();
            if (key.matches("\\{([^}]*)\\}")) {
                String value = key.replaceAll("\\{([^}]*)\\}", "$1");
                data.put("schemaKay", Map.of(
                        "isExpression", true,
                        "value", value,
                        "namespaces", MediatorUtils.transformNamespaces(node.getNamespaces())
                ));
            } else {
                data.put("schemaKay", Map.of(
                        "isExpression", false,
                        "value", key
                ));
            }
        }
        return data;
    }
}
