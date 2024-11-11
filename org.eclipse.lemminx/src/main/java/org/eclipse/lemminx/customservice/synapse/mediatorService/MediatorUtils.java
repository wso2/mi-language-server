package org.eclipse.lemminx.customservice.synapse.mediatorService;

import org.eclipse.lemminx.customservice.synapse.mediatorService.pojo.Namespace;

import java.util.*;

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

    public static List<Map<String, Object>> filterNamespaces(List<Map<String, Object>> namespaces) {
        List<Map<String, Object>> filteredNamespaces = new ArrayList<>();
        Set<String> prefixes = new HashSet<>();
        Set<String> uris = new HashSet<>();

        for (Map<String, Object> namespace : namespaces) {
            String key = (String) namespace.get("prefix");
            String uri = (String) namespace.get("uri");
            if (!prefixes.contains(key) && !uris.contains(uri)) {
                prefixes.add(key);
                uris.add(uri);
                filteredNamespaces.add(namespace);
            }
        }
        return filteredNamespaces;
    }
}
