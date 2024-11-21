package org.eclipse.lemminx.customservice.synapse.mediatorService.mediators;

import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.TagRanges;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.validate.Validate;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

import java.util.*;

public class ValidateMediator {
    public static Either<Map<String, Object>, Map<Range, Map<String, Object>>> processData(Map<String, Object> data,
                                                                                           Validate validate,
                                                                                           List<String> dirtyFields) {
        List<Map<String, Object>> schemas = new ArrayList<>();
        Object schemasData = data.get("schemas");
        if (schemasData instanceof List<?>) {
            for (Object schemaObj : (List<?>) schemasData) {
                if (schemaObj instanceof List<?>) {
                    List<?> schema = (List<?>) schemaObj;
                    if (!schema.isEmpty() && schema.get(0) instanceof String) {
                        schemas.add(Map.of("key", schema.get(0)));
                    }
                }
            }
        }
        data.put("schemas", schemas);

        List<Map<String, Object>> features = new ArrayList<>();
        Object featuresData = data.get("features");
        if (featuresData instanceof List<?>) {
            for (Object featureObj : (List<?>) featuresData) {
                if (featureObj instanceof List<?>) {
                    List<?> feature = (List<?>) featureObj;
                    if (feature.size() >= 2 && feature.get(0) instanceof String && feature.get(1) instanceof Boolean) {
                        features.add(Map.of(
                                "featureName", feature.get(0),
                                "featureEnable", (Boolean) feature.get(1) ? "true" : "false"
                        ));
                    }
                }
            }
        }
        data.put("features", features);

        List<Map<String, Object>> resources = new ArrayList<>();
        Object resourcesData = data.get("resources");
        if (resourcesData instanceof List<?>) {
            for (Object resourceObj : (List<?>) resourcesData) {
                if (resourceObj instanceof List<?>) {
                    List<?> resource = (List<?>) resourceObj;
                    if (resource.size() >= 2 && resource.get(0) instanceof String && resource.get(1) instanceof String) {
                        resources.add(Map.of(
                                "location", resource.get(0),
                                "locationKey", resource.get(1)
                        ));
                    }
                }
            }
        }
        data.put("resources", resources);

        if (validate == null) {
            data.put("isNewMediator", true);
            return Either.forLeft(data);
        }

        return Either.forRight(getEdits(data, validate, dirtyFields));
    }

    private static Map<Range, Map<String, Object>> getEdits(Map<String, Object> data, Validate validate, List<String> dirtyFields) {
        Map<Range, Map<String, Object>> edits = new HashMap<>();

        if (!dirtyFields.isEmpty()) {
            Map<String, Object> dataCopy = new HashMap<>(data);
            TagRanges validateRange = validate.getRange();
            TagRanges onFailRange = validate.getOnFail().getRange();

            if (onFailRange != null && onFailRange.getEndTagRange() != null) {
                Range editRange = new Range(
                        validateRange.getStartTagRange().getStart(),
                        onFailRange.getStartTagRange().getStart()
                );
                edits.put(editRange, dataCopy);

                editRange = new Range(
                        onFailRange.getEndTagRange().getEnd(),
                        validateRange.getEndTagRange().getEnd()
                );
                edits.put(editRange,  Map.of("endXML", true));
            } else {
                data.put("isNewMediator", true);
                Range editRange = new Range(
                        validateRange.getStartTagRange().getStart(),
                        validateRange.getEndTagRange() != null
                                ? validateRange.getEndTagRange().getEnd()
                                : validateRange.getStartTagRange().getEnd()
                );
                edits.put(editRange, data);
            }
        }

//        edits.sort(Comparator.comparingInt(edit -> ((Range) edit.get("range")).getStart().getLine()));
        return edits;
    }

    public static Map<String, Object> getDataFromST(Validate node) {

        Map<String, Object> data = new HashMap<>();
        data.put("description", node.getDescription());
        return data;
    }
}
