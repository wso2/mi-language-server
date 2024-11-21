package org.eclipse.lemminx.customservice.synapse.mediatorService.mediators;

import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.other.rule.Rule;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RuleMediator {
    public static Either<Map<String, Object>, Map<Range, Map<String, Object>>> processData(Map<String, Object> data,
                                                                                           Rule rule,
                                                                                           List<String> dirtyFields) {
        if (data.containsKey("targetNamespaces") && data.get("targetNamespaces") instanceof List<?>) {
            List<Object> targetNamespaces = (List<Object>) data.get("targetNamespaces");
            List<Map<String, String>> processedNamespaces = new ArrayList<>();
            for (Object namespaceObj : targetNamespaces) {
                if (namespaceObj instanceof List<?>) {
                    List<String> namespace = (List<String>) namespaceObj;
                    processedNamespaces.add(Map.of(
                            "prefix", namespace.get(0),
                            "uri", namespace.get(1)
                    ));
                }
            }
            data.put("targetNamespaces", processedNamespaces);
        }

        if (data.containsKey("targetResultXPath") && data.get("targetResultXPath") instanceof Map<?, ?>) {
            data.put("targetResultXPath", ((Map<?, ?>) data.get("targetResultXPath")).get("value"));
        }
        if (data.containsKey("targetXPath") && data.get("targetXPath") instanceof Map<?, ?>) {
            data.put("targetXPath", ((Map<?, ?>) data.get("targetXPath")).get("value"));
        }

        if (data.containsKey("factsConfiguration") && data.get("factsConfiguration") instanceof List<?>) {
            List<Object> factsConfiguration = (List<Object>) data.get("factsConfiguration");
            List<Map<String, Object>> processedFacts = new ArrayList<>();
            for (Object factObj : factsConfiguration) {
                if (factObj instanceof List<?>) {
                    List<Object> fact = (List<Object>) factObj;
                    String factType = getFactType(fact);
                    processedFacts.add(Map.of(
                            "elementName", fact.get(2),
                            "factType", factType,
                            "propertyExpression", fact.get(3) instanceof Map<?, ?>
                                    ? ((Map<?, ?>) fact.get(3)).get("value")
                                    : null
                    ));
                }
            }
            data.put("facts", processedFacts);
        }

        if (data.containsKey("resultsConfiguration") && data.get("resultsConfiguration") instanceof List<?>) {
            List<Object> resultsConfiguration = (List<Object>) data.get("resultsConfiguration");
            List<Map<String, String>> processedResults = new ArrayList<>();
            for (Object resultObj : resultsConfiguration) {
                if (resultObj instanceof List<?>) {
                    List<Object> result = (List<Object>) resultObj;
                    String factType = getFactType(result);
                    processedResults.add(Map.of(
                            "resultName", (String) result.get(2),
                            "resultType", factType
                    ));
                }
            }
            data.put("results", processedResults);
        }

        // Handle rule set source type
        String ruleSetSourceType = (String) data.get("ruleSetSourceType");
        if ("URL".equals(ruleSetSourceType)) {
            data.remove("inlineRegistryKey");
            data.remove("ruleSetSourceCode");
        } else if ("REGISTRY_REFERENCE".equals(ruleSetSourceType)) {
            data.remove("ruleSetURL");
            data.remove("ruleSetSourceCode");
        } else {
            data.remove("inlineRegistryKey");
            data.remove("ruleSetURL");
        }

        // Convert fields to lowercase
        if (data.get("ruleSetType") instanceof String) {
            data.put("ruleSetType", ((String) data.get("ruleSetType")).toLowerCase());
        }
        if (data.get("ruleSetSourceType") instanceof String) {
            data.put("ruleSetSourceType", ((String) data.get("ruleSetSourceType")).toLowerCase());
        }
        if (data.get("targetAction") instanceof String) {
            data.put("targetAction", ((String) data.get("targetAction")).toLowerCase());
        }
        return Either.forLeft(data);

    }

    public static Map<String, Object> getDataFromST(Rule node) {

        Map<String, Object> data = new HashMap<>();
        data.put("description", node.getDescription());
        return data;
    }

    private static String getFactType(List<Object> fact) {
        if ("CUSTOM".equals(fact.get(0))) {
            return (String) fact.get(1);
        }
        return (String) fact.get(0);
    }
}
