package org.eclipse.lemminx.customservice.synapse.mediatorService.mediators;

import org.eclipse.lemminx.customservice.synapse.mediatorService.MediatorUtils;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.TagRanges;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.advanced.Clone.Clone;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.advanced.Clone.CloneTarget;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

import java.util.*;

public class CloneMediator {
    public static Either<Map<String, Object>, Map<Range, Map<String, Object>>> processData(Map<String, Object> data,
                                                                                           Clone clone,
                                                                                           List<String> dirtyFields) {
        data.remove("soapAction");
        data.remove("toAddress");

        if (clone == null) {
            return Either.forLeft(getNewMediator(data));
        }
        return Either.forRight(getEdits(data,clone,dirtyFields));

    }
    private static Map<String, Object> getNewMediator(Map<String, Object> data) {
        Map<String, Object> newData = new HashMap<>(data);
        newData.put("newMediator", true);

        // Process targets
        List<Map<String, Object>> targets = new ArrayList<>();
        Object targetList = newData.get("targets");
        if (targetList instanceof List<?>) {
            for (Object targetObj : (List<?>) targetList) {
                if (targetObj instanceof List<?>) {
                    targets.add(processTargetData((List<?>) targetObj));
                }
            }
        }
        newData.put("targets", targets);

        return newData;
    }

    private static Map<String, Object> processTargetData(List<?> target) {
        boolean isRegistrySeqAndEndpoint = false;
        String sequenceRegistryKey = null;
        String endpointRegistryKey = null;

        if (target.get(0).equals("REGISTRY_REFERENCE") || target.get(0).equals("NONE")) {
            isRegistrySeqAndEndpoint = true;
        }
        if (target.get(0).equals("REGISTRY_REFERENCE")) {
            sequenceRegistryKey = (String) target.get(1);
        }
        if (target.size() > 3 && target.get(2).equals("REGISTRY_REFERENCE")) {
            endpointRegistryKey = (String) target.get(3);
        }

        return Map.of(
                "isRegistrySeqAndEndpoint", isRegistrySeqAndEndpoint,
                "sequenceRegistryKey", sequenceRegistryKey,
                "endpointRegistryKey", endpointRegistryKey,
                "soapAction", target.size() > 4 ? target.get(4) : null,
                "toAddress", target.size() > 5 ? target.get(5) : null
        );
    }

    private static Map<Range, Map<String, Object>> getEdits(
            Map<String, Object> data, Clone clone,List<String> dirtyFields) {

        Map<Range, Map<String, Object>> edits = new HashMap<>();
        List<String> cloneAttributes = List.of("cloneId", "sequentialMediation", "continueParent", "description");

        if (MediatorUtils.anyMatch(dirtyFields, cloneAttributes)) {
            Map<String, Object> cloneData = new HashMap<>(data);
            cloneData.put("editClone", true);

            TagRanges range = clone.getRange();
            Range editRange = new Range(range.getStartTagRange().getStart(), range.getStartTagRange().getEnd());

            edits.put(editRange, cloneData);
        }

        if (dirtyFields.contains("targets")) {
            List<List<?>> targets = (List<List<?>>) data.get("targets");

            for (List<?> target : targets) {
                Map<String, Object> targetData = processTargetData(target);
                Range editRange;
                Integer oldIndex = (Integer) target.get(6);

                if (oldIndex == null) {
                    // Add new target
                    TagRanges cloneRange = clone.getRange();
                    editRange = new Range(cloneRange.getEndTagRange().getStart(), cloneRange.getEndTagRange().getStart());
                    targetData.put("newTarget", true);
                } else {
                    // Edit existing target
                    List<?> oldTarget = getOldTarget(clone.getTarget(), oldIndex);
                    boolean addSequence = !oldTarget.get(0).equals("ANONYMOUS") && targetData.get("sequenceRegistryKey") == null;
                    boolean removeSequence = oldTarget.get(0).equals("ANONYMOUS") && (Boolean) targetData.get("isRegistrySeqAndEndpoint");

                    TagRanges targetRange = getTargetRange(clone.getTarget(), oldIndex);
                    if (!addSequence && !removeSequence) {
                        editRange = new Range(targetRange.getStartTagRange().getStart(), targetRange.getStartTagRange().getEnd());
                    } else {
                        editRange = new Range(targetRange.getStartTagRange().getStart(),
                                targetRange.getEndTagRange() != null ? targetRange.getEndTagRange().getEnd() : targetRange.getStartTagRange().getEnd());
                    }
                }

                edits.put(editRange, targetData);
            }

            // Remove deleted targets
            List<List<?>> removedTargets = filterRemovedElements(clone.getTarget(), targets);
            for (List<?> removedTarget : removedTargets) {
                boolean selfClosed = (Boolean) removedTarget.get(1);
                TagRanges targetRange = (TagRanges) removedTarget.get(2);
                Range editRange;

                if (selfClosed) {
                    editRange = targetRange.getStartTagRange();
                } else {
                    editRange = new Range(targetRange.getStartTagRange().getStart(), targetRange.getEndTagRange().getEnd());
                }

                edits.put(editRange, Map.of("removeTarget", true));
            }
        }

        return edits;
    }

    private static List<?> getOldTarget(CloneTarget[] targets, int index) {
//        for (CloneTarget target : targets) {
//            if (target.get(6).equals(index)) {
//                return target;
//            }
//        }
        return new ArrayList<>();
    }

    private static TagRanges getTargetRange(CloneTarget[] targetRanges, int index) {
        for (CloneTarget range : targetRanges) {
            return range.getRange();
        }
        return null;
    }

    private static List<List<?>> filterRemovedElements(CloneTarget[] originalTargets, List<List<?>> updatedTargets) {
//        Set<Integer> updatedIndices = updatedTargets.stream().map(target -> (Integer) target.get(6)).collect(Collectors.toSet());
//        return originalTargets.stream().filter(target -> !updatedIndices.contains(target.get(0))).collect(Collectors.toList());
        return updatedTargets;
    }

    public static Map<String, Object> getDataFromST(Clone node) {

        Map<String, Object> data = new HashMap<>();
        data.put("description", node.getDescription());
        return data;
    }
}
