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
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.eip.ScatterGather;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScatterGatherMediator {

    private static final String JSON_CONTENT_TYPE = "JSON";

    public static Either<Map<String, Object>, Map<Range, Map<String, Object>>> processData(Map<String, Object> data,
                                                                                           ScatterGather scatterGather,
                                                                                           List<String> dirtyFields) {

        if ("Variable".equals(data.get("resultTarget"))) {
            data.put("resultTarget", data.get("variableName"));
        }
        setExpression(data);
        Map<String, Object> condition = (Map<String, Object>) data.get("condition");
        if (condition != null) {
            data.put("condition", condition.get("value"));
        }
        if (scatterGather == null) {
            data.put("newMediator", true);
            return Either.forLeft(data);
        }
        return Either.forRight(getEdits(data, scatterGather, dirtyFields));
    }

    private static Map<Range, Map<String, Object>> getEdits(Map<String, Object> data, ScatterGather scatterGather,
                                                            List<String> dirtyFields) {

        Map<Range, Map<String, Object>> edits = new HashMap<>();
        List<String> scatterGatherAttributes = List.of("parallelExecution", "contentType", "resultTarget",
                "variableName", "rootElement");
        List<String> aggregationAttributes = List.of("expression", "xPathExpression", "condition", "completeTimeout",
                "minMessages", "maxMessages");

        if (dirtyFields.contains("contentType")) {
            if (JSON_CONTENT_TYPE.equals(data.get("contentType"))) {
                dirtyFields.add("expression");
            } else {
                dirtyFields.add("xPathExpression");
                dirtyFields.add("rootElement");
            }
        }

        if (MediatorUtils.anyMatch(dirtyFields, scatterGatherAttributes)) {
            Map<String, Object> scatterGatherData = new HashMap<>(data);
            scatterGatherData.put("editScatterGather", true);

            TagRanges range = scatterGather.getRange();
            Range editRange = new Range(range.getStartTagRange().getStart(), range.getStartTagRange().getEnd());

            edits.put(editRange, scatterGatherData);
        }
        if (MediatorUtils.anyMatch(dirtyFields, aggregationAttributes)) {
            Map<String, Object> scatterGatherData = new HashMap<>(data);
            scatterGatherData.put("editScatterGatherAggregate", true);

            TagRanges range = scatterGather.getScatterGatherAggregation().getRange();
            Range editRange = new Range(range.getStartTagRange().getStart(), range.getStartTagRange().getEnd());

            edits.put(editRange, scatterGatherData);
        }
        return edits;
    }

    public static Map<String, Object> getDataFromST(ScatterGather scatterGather) {

        Map<String, Object> data = new HashMap<>();
        data.put("description", scatterGather.getDescription());
        data.put("parallelExecution", scatterGather.isExecuteParallel());

        String contentType = scatterGather.getContentType();
        data.put("contentType", contentType);
        if (JSON_CONTENT_TYPE.equals(contentType)) {
            data.put("expression", Map.of("isExpression", true, "value",
                    scatterGather.getScatterGatherAggregation().getExpression()));
        } else {
            data.put("xPathExpression", Map.of("isExpression", true, "value",
                    scatterGather.getScatterGatherAggregation().getExpression()));
        }
        data.put("rootElement", scatterGather.getRootElement());
        String resultTarget = scatterGather.getResultTarget();
        if ("Body".equalsIgnoreCase(resultTarget)) {
            data.put("resultTarget", resultTarget);
        } else {
            data.put("resultTarget", "Variable");
            data.put("variableName", resultTarget);
        }
        if (scatterGather.getScatterGatherAggregation().getCondition() != null) {
            data.put("condition", Map.of("isExpression", true, "value",
                    scatterGather.getScatterGatherAggregation().getCondition()));
        }
        data.put("completeTimeout", scatterGather.getScatterGatherAggregation().getCompleteTimeout());
        data.put("minMessages", scatterGather.getScatterGatherAggregation().getMinMessages());
        data.put("maxMessages", scatterGather.getScatterGatherAggregation().getMaxMessages());
        return data;
    }

    private static void setExpression(Map<String, Object> data) {

        Map<String, Object> jsonExpression = (Map<String, Object>) data.get("expression");
        Map<String, Object> xPathExpression = (Map<String, Object>) data.get("xPathExpression");
        if (JSON_CONTENT_TYPE.equals(data.get("contentType"))) {
            data.put("expression", jsonExpression.get("value"));
        } else {
            data.put("expression", xPathExpression.get("value"));
        }
    }
}
