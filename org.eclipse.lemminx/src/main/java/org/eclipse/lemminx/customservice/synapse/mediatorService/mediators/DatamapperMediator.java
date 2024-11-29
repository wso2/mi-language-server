package org.eclipse.lemminx.customservice.synapse.mediatorService.mediators;

import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.transformation.Datamapper;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatamapperMediator {
    public static Either<Map<String, Object>, Map<Range, Map<String, Object>>> processData430(Map<String, Object> data,
                                                                                           Datamapper datamapper,
                                                                                           List<String> dirtyFields) {
        return Either.forLeft(data);

    }

    public static Map<String, Object> getDataFromST430(Datamapper node) {

        Map<String, Object> data = new HashMap<>();
        data.put("description", node.getDescription());
        data.put("configurationLocalPath", node.getConfig());
        data.put("inputSchemaLocalPath", node.getInputSchema());
        data.put("inputType", node.getInputType());
        data.put("outputSchemaLocalPath", node.getOutputSchema());
        data.put("outputType", node.getOutputType());
        return data;
    }
}
