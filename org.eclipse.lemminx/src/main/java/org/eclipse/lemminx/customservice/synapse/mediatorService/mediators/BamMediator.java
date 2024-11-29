package org.eclipse.lemminx.customservice.synapse.mediatorService.mediators;

import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.other.bam.Bam;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BamMediator {
    public static Either<Map<String, Object>, Map<Range, Map<String, Object>>> processData430(Map<String, Object> data,
                                                                                           Bam bam,
                                                                                           List<String> dirtyFields) {
        return Either.forLeft(data);

    }

    public static Map<String, Object> getDataFromST430(Bam node) {

        Map<String, Object> data = new HashMap<>();
        data.put("description", node.getDescription());
        data.put("serverProfileName", node.getServerProfile().getName());
        data.put("streamName", node.getServerProfile().getStreamConfig().getName());
        data.put("streamVersion", node.getServerProfile().getStreamConfig().getVersion());

        return data;
    }
}
