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
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.extension.ejb.Ejb;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.extension.ejb.EjbArgsArg;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EjbMediator {
    public static Either<Map<String, Object>, Map<Range, Map<String, Object>>> processData430(Map<String, Object> data,
                                                                                              Ejb ejb,
                                                                                              List<String> dirtyFields) {
        if ("EXPRESSION".equals(data.get("sessionIdType"))) {
            Map<String, Object> sessionIdExpression = data.get("sessionIdExpression") instanceof Map<?, ?>
                    ? (Map<String, Object>) data.get("sessionIdExpression")
                    : null;

            if (sessionIdExpression != null) {
                data.put("namespaces", sessionIdExpression.get("namespaces"));
                data.put("sessionIdExpression", "{" + sessionIdExpression.get("value") + "}");
            }
            data.remove("sessionIdLiteral");
        } else {
            data.remove("sessionIdExpression");
        }

        boolean argsAvailable = false;
        List<Map<String, Object>> methodArguments = new ArrayList<>();
        if (data.get("methodArguments") instanceof List<?>) {
            List<Object> arguments = (List<Object>) data.get("methodArguments");
            for (Object argumentObj : arguments) {
                if (argumentObj instanceof List<?>) {
                    List<Object> argument = (List<Object>) argumentObj;
                    argsAvailable = true;

                    Map<String, Object> argumentData = new HashMap<>();
                    argumentData.put("value", "EXPRESSION".equals(argument.get(1))
                            ? "{" + (((Map<?, ?>) argument.get(3)).get("value")) + "}"
                            : argument.get(2));
                    methodArguments.add(argumentData);
                }
            }
        }

        data.put("methodArguments", methodArguments);
        data.put("argsAvailable", argsAvailable);

        return Either.forLeft(data);

    }

    public static Map<String, Object> getDataFromST430(Ejb node) {
        Map<String, Object> data = new HashMap<>();

        Pattern regex = Pattern.compile("\\{([^}]*)}");
        data.put("beanstalk", node.getBeanstalk());
        data.put("class", node.getClazz());
        data.put("target", node.getTarget());
        data.put("jndiName", node.getJndiName());
        data.put("description", node.getDescription());
        data.put("remove", node.isRemove());
        data.put("method", node.getMethod());

        if (node.getArgs() != null && node.getArgs().getArg() != null) {
            List<List<Object>> methodArguments = new ArrayList<>();

            for (EjbArgsArg arg : node.getArgs().getArg()) {
                Matcher valueMatch = regex.matcher(arg.getValue());
                String value = "";
                Map<String, Object> expression = new HashMap<>();

                if (valueMatch.find()) {
                    expression = Map.of(
                            "isExpression", true,
                            "value", valueMatch.group(1)
                    );
                } else {
                    value = arg.getValue();
                }
                value = value == null ? "" : value;

                methodArguments.add(List.of(
                        "",
                        arg.getValue().startsWith("{") ? "EXPRESSION" : "LITERAL",
                        value,
                        expression
                ));
            }

            data.put("methodArguments", methodArguments);
        }

        // Process session ID
        if (node.getId() != null) {
            Matcher sessionIdMatch = regex.matcher(node.getId());
            if (sessionIdMatch.find()) {
                data.put("sessionIdType", "EXPRESSION");
                data.put("sessionIdExpression", Map.of(
                        "isExpression", true,
                        "value", sessionIdMatch.group(1),
                        "namespaces", MediatorUtils.transformNamespaces(node.getNamespaces())
                ));
            } else {
                data.put("sessionIdType", "LITERAL");
                data.remove("sessionIdExpression");
                data.put("sessionIdLiteral", node.getId());
            }
        }

        return data;
    }

}
