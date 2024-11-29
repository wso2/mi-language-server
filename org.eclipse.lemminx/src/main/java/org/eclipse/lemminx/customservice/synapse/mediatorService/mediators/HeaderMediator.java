package org.eclipse.lemminx.customservice.synapse.mediatorService.mediators;

import org.eclipse.lemminx.customservice.synapse.mediatorService.MediatorUtils;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.Header;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HeaderMediator {
    public static Either<Map<String, Object>, Map<Range, Map<String, Object>>> processData430(Map<String, Object> data,
                                                                                           Header header,
                                                                                           List<String> dirtyFields) {

        if ("remove".equals(data.get("headerAction"))) {
            data.remove("valueExpression");
            data.remove("valueLiteral");
            data.remove("valueInline");
        }

        String valueType = (String) data.get("valueType");
        if ("LITERAL".equals(valueType)) {
            data.remove("valueExpression");
            data.remove("valueInline");
        } else if ("EXPRESSION".equals(valueType)) {
            data.remove("valueLiteral");
            data.remove("valueInline");
        } else {
            data.remove("valueExpression");
            data.remove("valueLiteral");
        }

        return Either.forLeft(data);
    }


    public static Map<String, Object> getDataFromST430(Header node) {

        Map<String, Object> data = new HashMap<>();
        data.put("description", node.getDescription());
        if (node.getName() != null) {
            Map<String, Object> headerName = new HashMap<>();
            headerName.put("isExpression", true);
            headerName.put("value", node.getName());
            headerName.put("namespaces", MediatorUtils.transformNamespaces(node.getNamespaces()));
            data.put("headerName", headerName);
        }

        if (node.getAction() != null) {
            data.put("headerAction", node.getAction());
        }

        if (node.getAny() != null) {
            data.put("valueType", "INLINE");
        } else if (node.getValue() != null) {
            data.put("valueType", "LITERAL");
        } else {
            data.put("valueType", "EXPRESSION");
        }

        String valueType = (String) data.get("valueType");
        if ("EXPRESSION".equals(valueType)) {
            Map<String, Object> valueExpression = new HashMap<>();
            valueExpression.put("isExpression", true);
            valueExpression.put("value", node.getExpression());
            valueExpression.put("namespaces", MediatorUtils.transformNamespaces(node.getNamespaces()));
            data.put("valueExpression", valueExpression);
        } else if ("LITERAL".equals(valueType)) {
            data.put("valueLiteral", node.getValue());
        } else if ("INLINE".equals(valueType)) {
            data.put("valueInline", node.getAny());
        }

        data.put("scope", node.getScope().getValue());

        return data;
    }

}
