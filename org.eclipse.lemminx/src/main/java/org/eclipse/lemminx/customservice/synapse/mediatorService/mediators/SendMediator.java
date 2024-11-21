package org.eclipse.lemminx.customservice.synapse.mediatorService.mediators;

import org.eclipse.lemminx.customservice.synapse.mediatorService.MediatorUtils;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.endpoint.NamedEndpoint;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.Send;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SendMediator {
    public static Either<Map<String, Object>, Map<Range, Map<String, Object>>> processData(Map<String, Object> data,
                                                                                           Send send,
                                                                                           List<String> dirtyFields) {
        if ("Static".equals(data.get("receivingSequenceType"))) {
            data.put("receivingSequence", data.get("staticReceivingSequence"));
        } else if ("Dynamic".equals(data.get("receivingSequenceType"))) {
            if (data.get("dynamicReceivingSequence") instanceof Map<?, ?>) {
                Map<String, Object> dynamicReceivingSequence = (Map<String, Object>) data.get("dynamicReceivingSequence");
                data.put("receivingSequence", "{" + dynamicReceivingSequence.get("value") + "}");
                data.put("namespaces", dynamicReceivingSequence.get("namespaces"));

                // Remove namespaces if empty
                if (data.get("namespaces") instanceof Map<?, ?>) {
                    Map<?, ?> namespaces = (Map<?, ?>) data.get("namespaces");
                    if (namespaces.isEmpty()) {
                        data.remove("namespaces");
                    }
                }
            }
        }

        // Remove endpoint if invalid
        if (data.get("endpoint") == null || "".equals(data.get("endpoint")) || "NONE".equals(data.get("endpoint"))) {
            data.remove("endpoint");
        }

        // Handle skipSerialization flag
        if (Boolean.TRUE.equals(data.get("skipSerialization"))) {
            data.remove("receivingSequence");
            data.remove("buildMessageBeforeSending");
            data.remove("endpoint");
            data.remove("namespaces");
        }
        return Either.forLeft(data);

    }

    public static Map<String, Object> getDataFromST(Send node) {

        Map<String, Object> data = new HashMap<>();
        data.put("skipSerialization", false);
        data.put("buildMessageBeforeSending", node.isBuildmessage());
        data.put("description", node.getDescription());
        data.put("selfClosed", node.isSelfClosed());

        // Determine receiving sequence type
        if (node.getReceive() != null) {
            if (node.getReceive().startsWith("{")) {
                data.put("receivingSequenceType", "Dynamic");
            } else {
                data.put("receivingSequenceType", "Static");
            }
        } else {
            data.put("receivingSequenceType", "Default");
        }

        // Process static or dynamic receiving sequence
        if ("Static".equals(data.get("receivingSequenceType"))) {
            data.put("staticReceivingSequence", node.getReceive());
        } else if ("Dynamic".equals(data.get("receivingSequenceType"))) {
            String value = node.getReceive();
            if (value != null && value.matches("\\{([^}]*)\\}")) {
                value = value.replaceAll("\\{([^}]*)\\}", "$1");
            }
            data.put("dynamicReceivingSequence", Map.of(
                    "isExpression", true,
                    "value", value,
                    "namespaces", MediatorUtils.transformNamespaces(node.getNamespaces())
            ));
        }

        // Process endpoint
        NamedEndpoint endpoint = node.getEndpoint();
        if (endpoint != null) {
            if (endpoint.getKey() == null && endpoint.getKeyExpression() == null) {
                data.put("endpoint", "INLINE");

                Position endpointClosePosition = endpoint.isSelfClosed()
                        ? endpoint.getRange().getStartTagRange().getEnd()
                        : endpoint.getRange().getEndTagRange() != null
                        ? endpoint.getRange().getEndTagRange().getEnd()
                        : node.getRange().getEndTagRange().getStart();

                // Retrieve XML content using RpcClient TODO:inline xml
                String xml = "";

                String leadingSpaces = xml.split("\n")[0].replaceAll("\\n", "").replaceAll("\t", "    ");
                String inlineEndpoint = Arrays.stream(xml.split("\n"))
                        .map(line -> !line.isEmpty()
                                ? line.replaceAll("^(\\s+)", "$1".replace(leadingSpaces, ""))
                                : "")
                        .collect(Collectors.joining("\n"))
                        .trim();

                data.put("inlineEndpoint", inlineEndpoint);
            } else {
                data.put("endpoint", endpoint.getKey() != null ? endpoint.getKey() : endpoint.getKeyExpression());
            }
        } else {
            data.put("endpoint", "NONE");
        }

        data.put("range", node.getRange());
        return data;
    }
}
