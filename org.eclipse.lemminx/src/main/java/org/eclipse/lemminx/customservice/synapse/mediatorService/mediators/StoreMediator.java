package org.eclipse.lemminx.customservice.synapse.mediatorService.mediators;

import org.eclipse.lemminx.customservice.synapse.mediatorService.MediatorUtils;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.Drop;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.Store;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StoreMediator {
    public static Either<Map<String, Object>, Map<Range, Map<String, Object>>> processData430(Map<String, Object> data,
                                                                                           Store store,
                                                                                           List<String> dirtyFields) {
        if (data.containsKey("messageStore") && data.get("messageStore") instanceof Map<?, ?>) {
            Map<String, Object> messageStore = (Map<String, Object>) data.get("messageStore");
            if (Boolean.TRUE.equals(messageStore.get("isExpression")) && messageStore.containsKey("value")) {
                messageStore.put("value", "{" + messageStore.get("value") + "}");
            }
        }
        return Either.forLeft(data);

    }

    public static Map<String, Object> getDataFromST430(Store node) {

        Map<String, Object> data = new HashMap<>();
        data.put("description", node.getDescription());
        data.put("onStoreSequence", node.getSequence());
        if (node.getMessageStore() != null && node.getMessageStore().startsWith("{") && node.getMessageStore().endsWith("}")) {
            data.put("messageStore", Map.of(
                    "isExpression", true,
                    "value", node.getMessageStore().substring(1, node.getMessageStore().length() - 1),
                    "namespaces", MediatorUtils.transformNamespaces(node.getNamespaces())
            ));
        } else {
            data.put("messageStore", Map.of(
                    "isExpression", false,
                    "value", node.getMessageStore()
            ));
        }
        return data;
    }
}
