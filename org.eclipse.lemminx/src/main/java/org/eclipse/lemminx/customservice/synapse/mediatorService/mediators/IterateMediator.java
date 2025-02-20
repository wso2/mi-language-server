/*
 * Copyright (c) 2024, WSO2 LLC. (http://www.wso2.com).
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.eclipse.lemminx.customservice.synapse.mediatorService.mediators;

import org.eclipse.lemminx.customservice.synapse.mediatorService.MediatorUtils;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.TagRanges;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.eip.Iterate;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IterateMediator {

    private static final List<String> iterateTagAttributes = List.of(
            "attachPath", "continueParent", "iterateExpression", "iterateID", "preservePayload",
            "sequentialMediation", "description"
    );
    private static final List<String> targetTagAttributes = List.of("sequenceKey");

    public static Either<Map<String, Object>, Map<Range, Map<String, Object>>> processData430(Map<String, Object> data,
                                                                                              Iterate iterate,
                                                                                              List<String> dirtyFields) {
        if ("Anonymous".equals(data.get("sequenceType"))) {
            data.remove("sequenceKey");
            data.put("isAnnonymousSequence", true);
        }

        Map<String, Object> iterateExpression = data.get("iterateExpression") instanceof Map<?, ?> ?
                (Map<String, Object>) data.get("iterateExpression") : null;
        if (iterateExpression != null) {
            data.put("namespaces", iterateExpression.get("namespaces"));
            data.put("iterateExpression", iterateExpression.get("value"));
        }

        Map<String, Object> attachPath = data.get("attachPath") instanceof Map<?, ?> ?
                (Map<String, Object>) data.get("attachPath") : null;
        if (attachPath != null) {
            data.put("attachPath", attachPath.get("value"));
        }

        if (iterate == null) {
            data.put("isNewMediator", true);
            return Either.forLeft(data);
        }

        return Either.forRight(getEdits(data, iterate, dirtyFields));

    }

    private static Map<Range, Map<String, Object>> getEdits(Map<String, Object> data,
                                                            Iterate iterate,
                                                            List<String> dirtyFields) {
        Map<Range, Map<String, Object>> editsData = new HashMap<>();

        if (MediatorUtils.anyMatch(dirtyFields, iterateTagAttributes)) {
            Map<String, Object> dataCopy = new HashMap<>(data);
            dataCopy.put("selfClosed", iterate.isSelfClosed());
            dataCopy.put("editIterate", true);

            TagRanges iterateRange = iterate.getRange();
            Range editRange = new Range(
                    iterateRange.getStartTagRange().getStart(),
                    iterateRange.getStartTagRange().getEnd()
            );

            editsData.put(editRange, dataCopy);
        }

        if (MediatorUtils.anyMatch(dirtyFields, targetTagAttributes)) {
            Map<String, Object> dataCopy = new HashMap<>(data);
            dataCopy.put("editTarget", true);

            TagRanges targetRange = iterate.getTarget().getRange();
            Range editRange = new Range(
                    targetRange.getStartTagRange().getStart(),
                    targetRange.getEndTagRange() != null && targetRange.getEndTagRange().getEnd() != null
                            ? targetRange.getEndTagRange().getEnd() : targetRange.getStartTagRange().getEnd()
            );

            editsData.put(editRange, dataCopy);
        }
        return editsData;
    }

    public static Map<String, Object> getDataFromST430(Iterate node) {

        Map<String, Object> data = new HashMap<>();

        data.put("attachPath", Map.of(
                "isExpression", true,
                "value", node.getAttachPath() != null ? node.getAttachPath() : ""
        ));

        data.put("continueParent", node.isContinueParent());
        data.put("iterateExpression", Map.of(
                "isExpression", true,
                "value", node.getExpression() != null ? node.getExpression() : "",
                "namespaces", MediatorUtils.transformNamespaces(node.getNamespaces())
        ));
        data.put("iterateID", node.getId());
        data.put("preservePayload", node.isPreservePayload());
        data.put("sequentialMediation", node.isSequential());

        String sequence = node.getTarget() != null ? node.getTarget().getSequenceAttribute() : null;
        if (sequence != null) {
            data.put("sequenceType", "Key");
            data.put("sequenceKey", sequence);
        } else {
            data.put("sequenceType", "Anonymous");
        }

        data.put("description", node.getDescription());
        data.put("isSelfClosed", node.isSelfClosed());

        return data;
    }
}
