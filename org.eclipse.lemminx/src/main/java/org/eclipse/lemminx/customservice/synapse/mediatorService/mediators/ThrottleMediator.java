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

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.eclipse.lemminx.customservice.synapse.mediatorService.MediatorUtils;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.TagRanges;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.filter.throttle.Policy;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.filter.throttle.Throttle;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.filter.throttle.ThrottlePolicy;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

import java.util.*;
import java.util.stream.Collectors;

public class ThrottleMediator {

    private static final List<String> throttleTagAttributes = Arrays.asList(
            "groupId", "description", "onAcceptBranchsequenceKey", "onRejectBranchsequenceKey",
            "onAcceptBranchsequenceType", "onRejectBranchsequenceType");

    private static final List<String> policyTagAttributes = Arrays.asList(
            "policyType", "policyKey", "maximumConcurrentAccess", "policyEntries");

    private static final List<String> onAcceptTagAttributes = Arrays.asList(
            "onAcceptBranchsequenceType", "onAcceptBranchsequenceKey");

    private static final List<String> onRejectTagAttributes = Arrays.asList(
            "onRejectBranchsequenceType", "onRejectBranchsequenceKey");

    public static Either<Map<String, Object>, Map<Range, Map<String, Object>>> processData430(Map<String, Object> data,
                                                                                              Throttle throttle,
                                                                                              List<String> dirtyFields) {

        data.put("newMediator", throttle == null);
        String policyType = (String) data.get("policyType");

        if ("INLINE".equals(policyType)) {
            data.remove("policyKey");

            // Processing policyEntries if they are present
            List<List<String>> policyEntries = (List<List<String>>) data.get("policyEntries");
            if (policyEntries != null) {
                List<Map<String, Object>> formattedEntries = policyEntries.stream()
                        .map(entry -> Map.<String, Object>of(
                                "throttleType", entry.get(0),
                                "throttleRange", entry.get(1),
                                "accessType", entry.get(2),
                                "maxRequestCount", entry.get(3),
                                "unitTime", entry.get(4),
                                "prohibitPeriod", entry.get(5)
                        ))
                        .collect(Collectors.toList());
                data.put("policyEntries", formattedEntries);
                data.put("hasPolicyEntries", !formattedEntries.isEmpty());
            }
        } else {
            data.remove("policyEntries");
            data.put("policyKey", data.getOrDefault("policyKey", ""));
        }

        // Process onAcceptBranchsequenceType
        if ("ANONYMOUS".equals(data.get("onAcceptBranchsequenceType"))) {
            data.remove("onAcceptBranchsequenceKey");
        }

        // Process onRejectBranchsequenceType
        if ("ANONYMOUS".equals(data.get("onRejectBranchsequenceType"))) {
            data.remove("onRejectBranchsequenceKey");
        }

        if (throttle != null) {
            return Either.forRight(processForEdit(data, throttle, dirtyFields));
        }
        return Either.forLeft(data);
    }

    private static Map<Range, Map<String, Object>> processForEdit(Map<String, Object> data, Throttle throttle,
                                                                  List<String> dirtyFields) {

        Map<Range, Map<String, Object>> editsData = new HashMap<>();
        Map<String, TagRanges> ranges = getRanges(throttle);
        if (MediatorUtils.anyMatch(dirtyFields, throttleTagAttributes)) {
            getEdit("throttle", data, ranges, true, editsData);
        }
        if (MediatorUtils.anyMatch(dirtyFields, policyTagAttributes)) {
            getEdit("policy", data, ranges, false, editsData);
        }
        if (MediatorUtils.anyMatch(dirtyFields, onAcceptTagAttributes)) {
            getEdit("onAccept", data, ranges, false, editsData);
        }
        if (MediatorUtils.anyMatch(dirtyFields, onRejectTagAttributes)) {
            getEdit("onReject", data, ranges, false, editsData);
        }
        return editsData;
    }

    private static Map<String, TagRanges> getRanges(Throttle throttle) {

        Map<String, TagRanges> ranges = new HashMap<>();
        ranges.put("throttle", throttle.getRange());
        if (throttle.getPolicy() != null) {
            ranges.put("policy", throttle.getPolicy().getRange());
        }
        if (throttle.getOnAccept() != null) {
            ranges.put("onAccept", throttle.getOnAccept().getRange());
        }
        if (throttle.getOnReject() != null) {
            ranges.put("onReject", throttle.getOnReject().getRange());
        }
        return ranges;
    }

    public static void getEdit(
            String key,
            Map<String, Object> data,
            Map<String, TagRanges> ranges,
            boolean editStartTagOnly, Map<Range, Map<String, Object>> editsData) {

        // Make a copy of the data map
        Map<String, Object> dataCopy = new HashMap<>(data);

        // Construct the edit key
        String editKey = "edit" + Character.toUpperCase(key.charAt(0)) + key.substring(1);
        dataCopy.put(editKey, true);

        // Retrieve the range
        TagRanges range = ranges.get(key);
        Range editRange;

        if (range != null) {
            // Get start and end based on `editStartTagOnly`
            Position start = range.getStartTagRange().getStart();
            Position end = editStartTagOnly
                    ? range.getStartTagRange().getEnd() :
                    (range.getEndTagRange() != null && range.getEndTagRange().getEnd() != null ?
                            range.getEndTagRange().getEnd() : range.getStartTagRange().getEnd());

            editRange = new Range(start, end);

        } else {
            // Fallback to "throttle" range if the specified key range is not present
            TagRanges throttleRange = ranges.get("throttle");
            Position start = throttleRange.getEndTagRange().getStart();
            editRange = new Range(start, start);
        }

        // Construct the edit map
        editsData.put(editRange, dataCopy);
    }

    public static Map<String, Object> getDataFromST430(Throttle node) {

        Map<String, Object> data = new HashMap<>();

        data.put("groupId", node.getId());
        data.put("description", node.getDescription());

        // Handle onAcceptBranchsequenceType and onAcceptBranchsequenceKey
        String onAcceptAttribute = node.getOnAcceptAttribute();
        if (onAcceptAttribute != null) {
            data.put("onAcceptBranchsequenceKey", onAcceptAttribute);
            data.put("onAcceptBranchsequenceType", "REGISTRY_REFERENCE");
        } else {
            data.put("onAcceptBranchsequenceType", "ANONYMOUS");
        }

        // Handle onRejectBranchsequenceType and onRejectBranchsequenceKey
        String onRejectAttribute = node.getOnRejectAttribute();
        if (onRejectAttribute != null) {
            data.put("onRejectBranchsequenceKey", onRejectAttribute);
            data.put("onRejectBranchsequenceType", "REGISTRY_REFERENCE");
        } else {
            data.put("onRejectBranchsequenceType", "ANONYMOUS");
        }

        // Handle policyType and policyEntries
        ThrottlePolicy policy = node.getPolicy();
        if (policy != null) {
            String policyKey = policy.getKey();
            if (policyKey != null) {
                data.put("policyType", "REGISTRY_REFERENCE");
                data.put("policyKey", policyKey);
            } else {
                data.put("policyType", "INLINE");

                Policy[] policyArray = policy.getContent();
                if (policyArray != null && policyArray.length > 0) {
                    Policy firstContent = policyArray[0];
                    Object policyDetails =
                            firstContent.getPolicyOrAllOrExactlyOne().get(0);
                    Gson gson = new Gson();
                    JsonObject policyDetailsJson = gson.toJsonTree(policyDetails).getAsJsonObject();
                    JsonElement maxConcurrentAccess = policyDetailsJson.get("maximumConcurrentAccess");
                    if (maxConcurrentAccess != null) {
                        data.put("maximumConcurrentAccess",
                                maxConcurrentAccess.getAsJsonObject().get("textNode").getAsString());
                    }

                    JsonArray policies = policyDetailsJson.getAsJsonArray("policy");
                    if (policies != null) {
                        List<List<Object>> policyEntries = new ArrayList<>();
                        for (JsonElement policyElement : policies) {
                            JsonObject policyObj = policyElement.getAsJsonObject();
                            Map<String, Object> extractedPolicy = extractPolicyData(policyObj);

                            List<Object> entry = List.of(
                                    extractedPolicy.get("throttleType") != null ? extractedPolicy.get("throttleType") : "",
                                    extractedPolicy.get("throttleRange") != null ? extractedPolicy.get("throttleRange") : "",
                                    extractedPolicy.get("accessType") != null ? extractedPolicy.get("accessType") : "",
                                    extractedPolicy.get("maxRequestCount") != null ? extractedPolicy.get("maxRequestCount") : "",
                                    extractedPolicy.get("unitTime") != null ? extractedPolicy.get("unitTime") : "",
                                    extractedPolicy.get("prohibitPeriod") != null ? extractedPolicy.get("prohibitPeriod") : ""
                            );
                            policyEntries.add(entry);
                        }
                        data.put("policyEntries", policyEntries);
                    }
                }
            }
        } else {
            data.put("policyType", "INLINE");
            data.put("policyEntries", new ArrayList<>());
        }

        return data;
    }

    private static Map<String, Object> extractPolicyData(JsonObject policyJson) {

        Map<String, Object> policy = new HashMap<>();

        // Handling throttleType and throttleRange
        JsonObject id = policyJson.getAsJsonObject("id");
        if (id != null) {
            policy.put("throttleType", id.get("type").getAsString());
            policy.put("throttleRange", id.get("value").getAsString());
        }

        // Processing policyOrAllOrExactlyOne entries
        JsonArray policyEntries = policyJson.getAsJsonArray("policyOrAllOrExactlyOne");
        for (JsonElement entry : policyEntries) {
            JsonObject entryObj = entry.getAsJsonObject();
            String tag = entryObj.get("tag").getAsString();
            switch (tag) {
                case "throttle:UnitTime":
                    policy.put("unitTime", entryObj.get("textNode").getAsString());
                    break;
                case "throttle:ProhibitTimePeriod":
                    policy.put("prohibitPeriod", entryObj.get("textNode").getAsString());
                    break;
                case "throttle:MaximumCount":
                    policy.put("maxRequestCount", entryObj.get("textNode").getAsString());
                    break;
                case "wsp:Policy":
                    if (entryObj.has("policyOrAllOrExactlyOne")) {
                        JsonArray innerPolicies = entryObj.getAsJsonArray("policyOrAllOrExactlyOne");
                        if (!innerPolicies.isEmpty()) {
                            String accessType =
                                    innerPolicies.get(0).getAsJsonObject().get("tag").getAsString().split(":")[1];
                            policy.put("accessType", accessType);
                        }
                    }
                    break;
            }
        }
        return policy;
    }
}
