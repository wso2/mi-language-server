package org.eclipse.lemminx.customservice.synapse.mediatorService;

import org.eclipse.lemminx.customservice.synapse.mediatorService.pojo.Namespace;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MediatorUtils {

    public static List<Namespace> transformNamespaces(Map<String, String> namespaces) {

        List<Namespace> transformedNamespaces = new ArrayList<>();
        if (namespaces != null && !namespaces.isEmpty()) {
            for (Map.Entry<String, String> entry : namespaces.entrySet()) {
                String key = entry.getKey();
                String uri = entry.getValue();
                String[] parts = key.split(":");
                String prefix = parts.length > 1 ? parts[1] : "";
                transformedNamespaces.add(new Namespace(prefix, uri));
            }
        }
        return transformedNamespaces;
    }

    public static boolean anyMatch(List<String> first, List<String> second) {

        return first.stream().anyMatch(second::contains);
    }
}
