package org.eclipse.lemminx.customservice.synapse.mediatorService.mediators;

import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.other.OauthService;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OauthServiceMediator {
    public static Either<Map<String, Object>, Map<Range, Map<String, Object>>> processData430(Map<String, Object> data,
                                                                                           OauthService oauthService,
                                                                                           List<String> dirtyFields) {
        if (data.containsKey("remoteServiceURL") && data.get("remoteServiceURL") instanceof String) {
            String remoteServiceURL = (String) data.get("remoteServiceURL");
            if (!remoteServiceURL.endsWith("/")) {
                data.put("remoteServiceURL", remoteServiceURL + "/");
            }
        }
        return Either.forLeft(data);

    }

    public static Map<String, Object> getDataFromST430(OauthService node) {

        Map<String, Object> data = new HashMap<>();
        data.put("description", node.getDescription());
        data.put("password", node.getPassword());
        data.put("username", node.getUsername());
        data.put("remoteServiceURL", node.getRemoteServiceUrl());

        return data;
    }
}
