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
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.transformation.payload.PayloadFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.transformation.payload.PayloadFactoryArgsArg;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.transformation.payload.TemplateType;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PayloadFactoryMediator {

    public static Either<Map<String, Object>, Map<Range, Map<String, Object>>> processData430(Map<String, Object> data,
                                                                                           PayloadFactory payloadFactory,
                                                                                           List<String> dirtyFields) {
        data.put("isInlined", "Inline".equals(data.get("payloadFormat")));
        data.put("isFreemarker", "Freemarker".equals(data.get("templateType")));

        // Process args
        List<Object> argsList = data.get("args") instanceof List<?> ? (List<Object>) data.get("args") : new ArrayList<>();
        List<Map<String, Object>> args = new ArrayList<>();

        for (Object propertyObj : argsList) {
            if (propertyObj instanceof List<?>) {
                List<Object> property = (List<Object>) propertyObj;
                Map<String, Object> argMap = new HashMap<>();
                if (property.get(0) instanceof Map<?, ?> && !Boolean.TRUE.equals(((Map<?, ?>) property.get(0)).get("isExpression"))) {
                    argMap.put("value", ((Map<?, ?>) property.get(0)).get("value"));
                    argMap.put("literal", property.size() > 2 ? property.get(2) : null);
                } else {
                    argMap.put("expression", property.get(0));
                    argMap.put("evaluator", property.size() > 1 ? property.get(1) : null);
                    argMap.put("literal", property.size() > 2 ? property.get(2) : null);
                }
                args.add(argMap);
            }
        }

        data.put("args", args);

        // Convert templateType to lowercase
        if (data.get("templateType") instanceof String) {
            data.put("templateType", ((String) data.get("templateType")).toLowerCase());
        }


        return Either.forLeft(data);
    }

    public static Map<String, Object> getDataFromST430(PayloadFactory node) {
        Map<String, Object> data = new HashMap<>();
        data.put("description", node.getDescription());

        if (node.getMediaType() != null) {
            data.put("mediaType", node.getMediaType());
        }

        if (node.getTemplateType() != null) {
            String templateType = node.getTemplateType().getValue();
            data.put("templateType", templateType.substring(0, 1).toUpperCase() + templateType.substring(1));
        }

        if (node.getFormat() != null) {
            data.put("format", node.getFormat());
        }

        if (node.getArgs() != null) {
            data.put("args", node.getArgs());
        }

        if (node.getFormat() != null && node.getFormat().getContent() != null) {
            if ("Freemarker".equals(data.get("templateType"))) {
                String content = node.getFormat().getContent() instanceof String
                        ? (String) node.getFormat().getContent()
                        : node.getFormat().getContent().toString();
                String matchedContent = content.matches("<!\\[CDATA\\[(.*?)]]>")
                        ? content.replaceAll("<!\\[CDATA\\[(.*?)]]>", "$1")
                        : content;
                data.put("payload", matchedContent);
            } else {
                data.put("payload", node.getFormat().getContent());
            }
            data.put("payloadFormat", "Inline");
        } else {
            data.put("payloadFormat", "Registry Reference");
            data.put("payloadKey", node.getFormat() != null ? node.getFormat().getKey() : null);
        }

        // Process args with transformations
        if (node.getArgs() != null && node.getArgs().getArg() != null) {
            List<List<Object>> args = new ArrayList<>();
            for (PayloadFactoryArgsArg arg : node.getArgs().getArg()) {
                boolean isExpression = arg.getValue() == null;
                Map<String, Object> argMap = new HashMap<>();
                argMap.put("isExpression", isExpression);
                argMap.put("value", arg.getValue() != null ? arg.getValue() : arg.getExpression());
                argMap.put("namespaces", MediatorUtils.transformNamespaces(arg.getNamespaces()));

                args.add(List.of(argMap,
                        arg.getEvaluator() != null ? arg.getEvaluator().toString() : "",
                        arg.isLiteral()));
            }
            data.put("args", args);
        } else {
            data.put("args", new ArrayList<>());
        }

        return data;
    }

    public static Either<Map<String, Object>, Map<Range, Map<String, Object>>> processData440(Map<String, Object> data,
                                                                                           PayloadFactory payloadFactory,
                                                                                           List<String> dirtyFields) {

        Boolean useTemplateResource = (Boolean) data.get("useTemplateResource");
        if (!useTemplateResource) {
            data.put("isInlined", true);
        }
        String templateType = (String) data.get("templateType");
        if ("freemarker".equals(templateType)) {
            data.put("isFreemarker", true);
        }
        return Either.forLeft(data);
    }

    public static Map<String, Object> getDataFromST440(PayloadFactory payloadFactory) {

        Map<String, Object> data = new HashMap<>();
        data.put("description", payloadFactory.getDescription());
        TemplateType templateType = payloadFactory.getTemplateType();
        data.put("templateType", templateType.getValue());
        data.put("mediaType", payloadFactory.getMediaType());
        if (payloadFactory.getFormat().getKey() != null) {
            data.put("useTemplateResource", true);
            data.put("payloadKey", payloadFactory.getFormat().getKey());
        } else {
            String inlineContent = (String) payloadFactory.getFormat().getContent();
            // If the payload is inline and the template type is freemarker, then remove the CDATA tags
            if (templateType.equals(TemplateType.FREE_MARKER)) {
                inlineContent = removeCDATAFromPayload(inlineContent);
            }
            data.put("payload", inlineContent);
        }
        return data;
    }

    public static String removeCDATAFromPayload(String inputPayload) {

        if (inputPayload.startsWith("<![CDATA[")) {
            inputPayload = inputPayload.substring(9);
            int i = inputPayload.lastIndexOf("]]>");
            if (i == -1)
                throw new IllegalStateException("Inline content starts with <![CDATA[ but cannot find pairing ]]>");
            inputPayload = inputPayload.substring(0, i);
        }
        return inputPayload;
    }
}
