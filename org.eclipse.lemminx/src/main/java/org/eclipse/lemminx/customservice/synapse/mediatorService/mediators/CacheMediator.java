package org.eclipse.lemminx.customservice.synapse.mediatorService.mediators;

import org.eclipse.lemminx.customservice.synapse.mediatorService.MediatorUtils;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.TagRanges;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.advanced.cache.Cache;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CacheMediator {
    public static List<String> cacheTagAttributes = List.of("cacheMediatorImplementation", "cacheType", "cacheTimeout",
            "maxMessageSize", "scope", "hashGeneratorAttribute", "description");
    public static List<String> protocolTagAttributes = List.of("cacheMediatorImplementation", "cacheProtocolType",
            "cacheProtocolMethods", "headersToIncludeInHash", "headersToExcludeInHash", "responseCodes",
            "enableCacheControl", "includeAgeHeader", "hashGenerator");
    public static List<String> onCacheHitTagAttributes = List.of("sequenceType", "sequenceKey");
    public static List<String> implementationTagAttributes = List.of("maxEntryCount", "implementationType", "cacheType");

    public static Either<Map<String, Object>, Map<Range, Map<String, Object>>> processData(Map<String, Object> data,
                                                                                           Cache cache,
                                                                                           List<String> dirtyFields) {
        Object scope = data.get("scope");
        if (scope instanceof String) {
            data.put("scope", ((String) scope).toLowerCase());
        }

        if ("COLLECTOR".equals(data.get("cacheType"))) {
            data.put("isCollector", true);
        }
        if ("611 Compatible".equals(data.get("cacheMediatorImplementation"))) {
            data.put("is611Compatible", true);
        }

        if ("ANONYMOUS".equals(data.get("sequenceType"))) {
            data.put("isAnonymousSequence", true);
        }

        Object headersToExcludeInHash = data.get("headersToExcludeInHash");
        if (headersToExcludeInHash instanceof List<?> && !((List<?>) headersToExcludeInHash).isEmpty()) {
            data.put("hasHeadersToExcludeInHash", true);
        }

        Object headersToIncludeInHash = data.get("headersToIncludeInHash");
        if (headersToIncludeInHash instanceof List<?> && !((List<?>) headersToIncludeInHash).isEmpty()) {
            data.put("hasHeadersToIncludeInHash", true);
        }

        boolean collectorToFinder = cache != null && cache.isCollector() && "FINDER".equals(data.get("cacheType"));

        if (cache == null || collectorToFinder) {
            data.put("isNewMediator", true);
            return Either.forLeft(data);
        }

        return Either.forRight(getEdits(data, cache, dirtyFields));
    }

    private static Map<Range, Map<String, Object>> getEdits(Map<String, Object> data, Cache cache, List<String> dirtyFields) {
        Map<Range, Map<String, Object>> edits = new HashMap<>();

        if (MediatorUtils.anyMatch(dirtyFields, cacheTagAttributes)) {
            Map<String, Object> cacheData = new HashMap<>(data);
            cacheData.put("isEditCache", true);

            TagRanges range = cache.getRange();
            Range editRange;
            if (Boolean.TRUE.equals(cacheData.get("isCollector"))) {
                editRange = new Range(range.getStartTagRange().getStart(),
                        range.getEndTagRange() != null ? range.getEndTagRange().getEnd() : range.getStartTagRange().getEnd());
            } else {
                editRange = new Range(range.getStartTagRange().getStart(), range.getStartTagRange().getEnd());
            }

            edits.put(editRange, cacheData);
            if (Boolean.TRUE.equals(cacheData.get("isCollector"))) {
                return edits;
            }
        }

        if (MediatorUtils.anyMatch(dirtyFields, protocolTagAttributes) && !Boolean.TRUE.equals(data.get("isCollector"))) {
            Map<String, Object> protocolData = new HashMap<>(data);
            protocolData.put("isEditProtocol", true);

            TagRanges range = cache.getProtocol().getRange();
            Range editRange;
            if (range != null) {
                editRange = new Range(range.getStartTagRange().getStart(),
                        range.getEndTagRange() != null ? range.getEndTagRange().getEnd() : range.getStartTagRange().getEnd());
            } else {
                TagRanges cacheRange = cache.getRange();
                editRange = new Range(cacheRange.getEndTagRange().getStart(), cacheRange.getEndTagRange().getStart());
            }

            edits.put(editRange, protocolData);
        }

        if (MediatorUtils.anyMatch(dirtyFields, onCacheHitTagAttributes) && Boolean.TRUE.equals(data.get("isCollector"))) {
            Map<String, Object> onCacheHitData = new HashMap<>(data);
            onCacheHitData.put("isEditOnCacheHit", true);

            TagRanges range = cache.getOnCacheHit().getRange();
            Range editRange;
            if (range != null) {
                editRange = new Range(range.getStartTagRange().getStart(),
                        range.getEndTagRange() != null ? range.getEndTagRange().getEnd() : range.getStartTagRange().getEnd());
            } else {
                TagRanges cacheRange = cache.getRange();
                editRange = new Range(cacheRange.getEndTagRange().getStart(), cacheRange.getEndTagRange().getStart());
            }

            edits.put(editRange, onCacheHitData);
        }

        if (MediatorUtils.anyMatch(dirtyFields, implementationTagAttributes) && !Boolean.TRUE.equals(data.get("isCollector"))) {
            Map<String, Object> implementationData = new HashMap<>(data);
            implementationData.put("isEditImplementation", true);

            TagRanges range = cache.getImplementation().getRange();
            Range editRange = new Range(range.getStartTagRange().getStart(),
                    range.getEndTagRange() != null ? range.getEndTagRange().getEnd() : range.getStartTagRange().getEnd());

            edits.put(editRange, implementationData);
        }

        return edits;
    }

    public static Map<String, Object> getDataFromST(Cache node) {

        Map<String, Object> data = new HashMap<>();
        data.put("description", node.getDescription());
        return data;
    }
}
