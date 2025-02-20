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
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.transformation.smooks.Smooks;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SmooksMediator {
    public static Either<Map<String, Object>, Map<Range, Map<String, Object>>> processData430(Map<String, Object> data,
                                                                                              Smooks smooks,
                                                                                              List<String> dirtyFields) {
        String outputMethod = (String) data.get("outputMethod");
        if ("Property".equals(outputMethod)) {
            data.remove("outputExpression");
        } else if ("Expression".equals(outputMethod)) {
            data.remove("outputProperty");
        } else {
            data.remove("outputProperty");
            data.remove("outputExpression");
        }
        return Either.forLeft(data);

    }

    public static Map<String, Object> getDataFromST430(Smooks node) {

        Map<String, Object> data = new HashMap<>();
        data.put("description", node.getDescription());
        data.put("configurationKey", node.getConfigKey());

        // Process input expression
        if (node.getInput() != null) {
            data.put("inputExpression", Map.of(
                    "isExpression", true,
                    "value", node.getInput().getExpression() != null ? node.getInput().getExpression() : "",
                    "namespaces", MediatorUtils.transformNamespaces(node.getInput().getNamespaces())
            ));
            data.put("inputType", node.getInput().getType());
        }

        // Process output type and method
        if (node.getOutput() != null) {
            data.put("outputType", node.getOutput().getType());

            if (node.getOutput().getExpression() != null) {
                data.put("outputMethod", "Expression");
                data.put("outputExpression", node.getOutput().getExpression());
                data.put("outputAction", node.getOutput().getAction());
            } else if (node.getOutput().getProperty() != null) {
                data.put("outputMethod", "Property");
                data.put("outputProperty", node.getOutput().getProperty());
            } else {
                data.put("outputMethod", "Default");
            }
        }
        return data;
    }
}
