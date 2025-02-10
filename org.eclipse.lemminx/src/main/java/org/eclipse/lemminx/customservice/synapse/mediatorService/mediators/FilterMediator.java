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
import org.eclipse.lemminx.customservice.synapse.mediatorService.pojo.Namespace;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.filter.filter.Filter;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FilterMediator {

    public static Either<Map<String, Object>, Map<Range, Map<String, Object>>> processData430(Map<String, Object> data,
                                                                                              Filter filter,
                                                                                              List<String> dirtyFields) {
        data.put("isNewMediator", filter == null);

        String conditionType = (String) data.get("conditionType");
        if ("Source and Regular Expression".equals(conditionType)) {
            if (data.get("source") instanceof Map<?, ?>) {
                data.put("namespaces", ((Map<?, ?>) data.get("source")).get("namespaces"));
                data.put("source", ((Map<?, ?>) data.get("source")).get("value"));
            }
            data.remove("xpath");
        } else if ("XPath".equals(conditionType)) {
            if (data.get("xPath") instanceof Map<?, ?>) {
                data.put("namespaces", ((Map<?, ?>) data.get("xPath")).get("namespaces"));
                data.put("xPath", ((Map<?, ?>) data.get("xPath")).get("value"));
            }
            data.remove("regularExpression");
            data.remove("source");
        }
        if (filter != null) {
            Map<Range, Map<String, Object>> filterData = new HashMap<>();
            filterData.put(filter.getRange().getStartTagRange(), data);
            return Either.forRight(filterData);
        }

        return Either.forLeft(data);
    }

    public static Map<String, Object> getDataFromST430(Filter node) {
        Map<String, Object> data = new HashMap<>();
        data.put("description", node.getDescription());
        data.put("regularExpression", node.getRegex());

        List<Namespace> namespaces = MediatorUtils.transformNamespaces(node.getNamespaces());
        String source = node.getSource();
        String xpath = node.getXpath();
        if (source != null) {
            data.put("source", Map.of("isExpression", true, "value", source, "namespaces", namespaces));
        }
        if (xpath != null) {
            data.put("xPath", Map.of("isExpression", true, "value", xpath, "namespaces", namespaces));
        }

        if (node.getXpath() != null && !node.getXpath().isEmpty()) {
            data.put("conditionType", "XPath");
        } else if ((node.getSource() != null && !node.getSource().isEmpty()) || (node.getRegex() != null && !node.getRegex().isEmpty())) {
            data.put("conditionType", "Source and Regular Expression");
        }

        data.put("selfClosed", node.isSelfClosed());
        data.put("range", node.getRange());

        return data;
    }

    public static Either<Map<String, Object>, Map<Range, Map<String, Object>>> processData440(Map<String, Object> data,
                                                                                              Filter filter,
                                                                                              List<String> dirtyFields) {

        Boolean useRegex = (Boolean) data.get("useRegex");
        if (useRegex) {
            if (data.get("source") instanceof Map<?, ?>) {
                data.put("source", ((Map<?, ?>) data.get("source")).get("value"));
            }
            data.remove("xpath");
        } else {
            if (data.get("xPath") instanceof Map<?, ?>) {
                data.put("xPath", ((Map<?, ?>) data.get("xPath")).get("value"));
            }
            data.remove("regularExpression");
            data.remove("source");
        }
        if (filter != null) {
            Map<Range, Map<String, Object>> filterData = new HashMap<>();
            filterData.put(filter.getRange().getStartTagRange(), data);
            return Either.forRight(filterData);
        }
        data.put("isNewMediator", true);
        return Either.forLeft(data);
    }

    public static Map<String, Object> getDataFromST440(Filter node) {

        Map<String, Object> data = new HashMap<>();
        data.put("description", node.getDescription());
        if (node.getXpath() != null) {
            data.put("useRegex", false);
            data.put("xPath", Map.of("isExpression", true,
                    "value", node.getXpath() != null ? node.getXpath() : ""
            ));
        } else {
            data.put("useRegex", true);
            data.put("source", Map.of("isExpression", true,
                    "value", node.getSource() != null ? node.getSource() : ""
            ));
            data.put("regularExpression", node.getRegex());
        }
        return data;
    }

    private static String getFilterDescription(Filter node) {
        if (node.getRegex() != null && node.getSource() != null) {
            return node.getSource() + " matches " + node.getRegex();
        }
        if (node.getRegex() != null) {
            return node.getRegex();
        } else if (node.getSource() != null) {
            return node.getSource();
        } else if (node.getXpath() != null) {
            return node.getXpath();
        }
        return "";
    }

}
