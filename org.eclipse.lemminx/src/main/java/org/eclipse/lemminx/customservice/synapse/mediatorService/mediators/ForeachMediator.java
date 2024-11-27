package org.eclipse.lemminx.customservice.synapse.mediatorService.mediators;

import org.eclipse.lemminx.customservice.synapse.mediatorService.MediatorUtils;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.TagRanges;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.eip.Foreach;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ForeachMediator {
    public static Either<Map<String, Object>, Map<Range, Map<String, Object>>> processData(Map<String, Object> data,
                                                                                           Foreach foreach,
                                                                                           List<String> dirtyFields) {
        if ("Anonymous".equals(data.get("sequenceType"))) {
            data.put("isAnnonymousSequence", true);
        }

        Map<String, Object> forEachExpression = (Map<String, Object>) data.get("forEachExpression");
        if (forEachExpression != null) {
            data.put("namespaces", forEachExpression.get("namespaces"));
            data.put("forEachExpression", forEachExpression.get("value"));
        }

        if (foreach == null) {
            data.put("isNewMediator", true);
            return Either.forLeft(data);
        } else {
            data.put("editForeach", true);

            TagRanges range = foreach.getRange();
            Range editRange = range.getStartTagRange();

            if (foreach.getSequenceAttribute() == null && "Key".equals(data.get("sequenceType"))) {
                editRange = new Range(
                        range.getStartTagRange().getStart(),
                        range.getEndTagRange() != null && range.getEndTagRange().getEnd() != null ?
                                range.getEndTagRange().getEnd() : range.getStartTagRange().getEnd()
                );
            } else if (foreach.getSequenceAttribute() != null  && "Anonymous".equals(data.get("sequenceType"))) {
                data.put("isAnnonymousSequence", true);
                data.put("addSequence", true);
                editRange = new Range(
                        range.getStartTagRange().getStart(),
                        range.getEndTagRange() != null && range.getEndTagRange().getEnd() != null ?
                                range.getEndTagRange().getEnd() : range.getStartTagRange().getEnd()
                );
            }

            if ("".equals(data.get("forEachID"))) {
                data.remove("forEachID");
            }

            return Either.forRight(Map.of(editRange, data)
            );
        }

    }

    public static Map<String, Object> getDataFromST(Foreach node) {

        Map<String, Object> data = new HashMap<>();
        data.put("description", node.getDescription());
        data.put("forEachID", node.getId());
        data.put("forEachExpression", Map.of(
                "isExpression", true,
                "value", node.getExpression() != null ? node.getExpression() : "",
                "namespaces", MediatorUtils.transformNamespaces(node.getNamespaces())));
        if (node.getSequenceAttribute() != null) {
            data.put("sequenceType", "Key");
            data.put("sequenceKey", node.getSequenceAttribute());
        } else if (node.getSequence() != null) {
            data.put("sequenceType", "Anonymous");
        }
        data.put("prevSequenceType", data.get("sequenceType"));
        return data;
    }
}
