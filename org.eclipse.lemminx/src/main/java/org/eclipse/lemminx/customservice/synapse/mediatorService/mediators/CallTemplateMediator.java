package org.eclipse.lemminx.customservice.synapse.mediatorService.mediators;

import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.CallTemplate;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.Drop;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CallTemplateMediator {
    public static Either<Map<String, Object>, Map<Range, Map<String, Object>>> processData(Map<String, Object> data,
                                                                                           CallTemplate callTemplate,
                                                                                           List<String> dirtyFields) {
        return Either.forLeft(data);

    }

    public static Map<String, Object> getDataFromST(CallTemplate node) {

        Map<String, Object> data = new HashMap<>();
        data.put("description", node.getDescription());
        return data;
    }
}
