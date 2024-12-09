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
    public static final String RESULT_TARGET = "resultTarget";
    public static final String VARIABLE_NAME = "variableName";
    public static final String EXPRESSION = "expression";
    public static final String CONDITION = "condition";
    public static final String VARIABLE = "Variable";
    public static final String VALUE = "value";
    public static final String X_PATH_EXPRESSION = "xPathExpression";
    public static final String CONTENT_TYPE = "contentType";
    public static final String ROOT_ELEMENT = "rootElement";
    public static final String PARALLEL_EXECUTION = "parallelExecution";
    public static final String COMPLETE_TIMEOUT = "completeTimeout";
    public static final String MIN_MESSAGES = "minMessages";
    public static final String MAX_MESSAGES = "maxMessages";

    public static Either<Map<String, Object>, Map<Range, Map<String, Object>>> processData(Map<String, Object> data,
                                                                                           ScatterGather scatterGather,
                                                                                           List<String> dirtyFields) {

        if (VARIABLE.equals(data.get(RESULT_TARGET))) {
            data.put(RESULT_TARGET, data.get(VARIABLE_NAME));
        }
        setExpression(data);
        Map<String, Object> condition = (Map<String, Object>) data.get(CONDITION);
        if (condition != null) {
            data.put(CONDITION, condition.get(VALUE));
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
        List<String> scatterGatherAttributes = List.of(PARALLEL_EXECUTION, CONTENT_TYPE, RESULT_TARGET,
                VARIABLE_NAME, ROOT_ELEMENT);
        List<String> aggregationAttributes = List.of(EXPRESSION, X_PATH_EXPRESSION, CONDITION, COMPLETE_TIMEOUT,
                MIN_MESSAGES, MAX_MESSAGES);

        if (dirtyFields.contains(CONTENT_TYPE)) {
            if (JSON_CONTENT_TYPE.equals(data.get(CONTENT_TYPE))) {
                dirtyFields.add(EXPRESSION);
            } else {
                dirtyFields.add(X_PATH_EXPRESSION);
                dirtyFields.add(ROOT_ELEMENT);
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
        data.put(PARALLEL_EXECUTION, scatterGather.isExecuteParallel());

        String contentType = scatterGather.getContentType();
        data.put(CONTENT_TYPE, contentType);
        if (JSON_CONTENT_TYPE.equals(contentType)) {
            data.put(EXPRESSION, getExpressionData(scatterGather.getScatterGatherAggregation().getExpression()));
        } else {
            data.put(X_PATH_EXPRESSION, getExpressionData(scatterGather.getScatterGatherAggregation().getExpression()));
            data.put(ROOT_ELEMENT, scatterGather.getRootElement());
        }
        String resultTarget = scatterGather.getResultTarget();
        if ("Body".equalsIgnoreCase(resultTarget)) {
            data.put(RESULT_TARGET, resultTarget);
        } else {
            data.put(RESULT_TARGET, VARIABLE);
            data.put(VARIABLE_NAME, resultTarget);
        }
        if (scatterGather.getScatterGatherAggregation().getCondition() != null) {
            data.put(CONDITION, getExpressionData(scatterGather.getScatterGatherAggregation().getCondition()));
        }
        data.put(COMPLETE_TIMEOUT, scatterGather.getScatterGatherAggregation().getCompleteTimeout());
        data.put(MIN_MESSAGES, scatterGather.getScatterGatherAggregation().getMinMessages());
        data.put(MAX_MESSAGES, scatterGather.getScatterGatherAggregation().getMaxMessages());
        return data;
    }

    private static void setExpression(Map<String, Object> data) {

        Map<String, Object> jsonExpression = (Map<String, Object>) data.get(EXPRESSION);
        Map<String, Object> xPathExpression = (Map<String, Object>) data.get(X_PATH_EXPRESSION);
        if (JSON_CONTENT_TYPE.equals(data.get(CONTENT_TYPE))) {
            data.put(EXPRESSION, jsonExpression.get(VALUE));
        } else {
            data.put(EXPRESSION, xPathExpression.get(VALUE));
        }
    }

    private static Map<?, ?> getExpressionData(String expression) {

        return Map.of("isExpression", true, VALUE, expression);
    }
}
