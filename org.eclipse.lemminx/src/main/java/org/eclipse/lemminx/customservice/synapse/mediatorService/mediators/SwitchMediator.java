package org.eclipse.lemminx.customservice.synapse.mediatorService.mediators;

import org.eclipse.lemminx.customservice.synapse.mediatorService.MediatorUtils;
import org.eclipse.lemminx.customservice.synapse.mediatorService.pojo.Namespace;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.filter.filter.Filter;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.filter.switchMediator.Switch;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.filter.switchMediator.SwitchCase;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SwitchMediator {

    public static Either<Map<String, Object>, Map<Range, Map<String, Object>>> processData(Map<String, Object> data, Switch switchMediator, List<String> dirtyFields) {

        data.put("isNewMediator", switchMediator == null);
        if (switchMediator != null) {
            return Either.forRight(getEdits(switchMediator, dirtyFields));
        }
        List<Map<String,String>> caseBranchesList = new ArrayList<>();
        List<List<String>> caseBranches = data.get("caseBranches") instanceof List<?> ? (List<List<String>>) data.get("caseBranches") : new ArrayList<>();
        for (List<String> caseBranch : caseBranches) {
            caseBranchesList.add(Map.of("caseRegex", caseBranch.get(0)));
        }
        data.put("caseBranches", caseBranchesList);
        return Either.forLeft(data);
    }

    public static Map<String, Object> getDataFromST(Switch node) {
        Map<String, Object> data = new HashMap<>();

        data.put("sourceXPath", Map.of("isExpression", true, "value", node.getSource(), "namespaces", MediatorUtils.transformNamespaces(node.getNamespaces())));

        data.put("description", node.getDescription());

        if (node.get_case() != null) {
            List<List<Object>> caseBranches = new ArrayList<>();
            List<List<Object>> caseBranchesData = new ArrayList<>();

            for (int i = 0; i < node.get_case().length; i++) {
                SwitchCase caseBranch = node.get_case()[i];
                caseBranches.add(List.of(caseBranch.getRegex(), i));
                caseBranchesData.add(List.of(
                        caseBranch.getRegex(),
                        i,
                        caseBranch.getRange(),
                        caseBranch.isSelfClosed()
                ));
            }

            data.put("caseBranches", caseBranches);
            data.put("caseBranchesData", caseBranchesData);
        }

        Map<String, Object> ranges = new HashMap<>();
        ranges.put("switch", node.getRange());
        if (node.get_default() != null) {
            ranges.put("_default", node.get_default().getRange());
        }
        data.put("ranges", ranges);
        data.put("switchSelfClosed", node.isSelfClosed());

        return data;
    }

    private static Map<Range, Map<String, Object>> getEdits(Switch node, List<String> dirtyFields) {
        Map<Range, Map<String, Object>> edits = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        data.put("description", node.getDescription());
        data.put("selfClosed", node.isSelfClosed());
        data.put("range", node.getRange());
        edits.put(node.getRange().getStartTagRange(), data);
        return edits;
    }
}
