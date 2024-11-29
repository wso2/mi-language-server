package org.eclipse.lemminx.customservice.synapse.mediatorService.mediators;

import org.eclipse.lemminx.customservice.synapse.mediatorService.MediatorUtils;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.transformation.payload.PayloadFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.transformation.payload.PayloadFactoryArgsArg;
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
}
