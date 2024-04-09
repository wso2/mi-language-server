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

package org.eclipse.lemminx.customservice.synapse.debugger.debuginfo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ApiDebugInfo extends DebugInfo {

    String apiKey;
    String method;
    String uriTemplate;
    String urlMapping;
    String sequenceType;

    public ApiDebugInfo() {

        objectMapper = new ObjectMapper();
        this.isValid = true;
    }

    public void setApiKey(String apiKey) {

        this.apiKey = apiKey;
    }

    public void setMethod(String method) {

        this.method = method;
    }

    public void setSequenceType(String sequenceType) {

        this.sequenceType = sequenceType;
    }

    public void setUriTemplate(String uriTemplate) {

        this.uriTemplate = uriTemplate;
    }

    public void setUrlMapping(String urlMapping) {

        this.urlMapping = urlMapping;
    }

    public String toJsonString() throws JsonProcessingException {

        ObjectNode rootNode = objectMapper.createObjectNode();

        ObjectNode sequence = objectMapper.createObjectNode();
        ObjectNode api = objectMapper.createObjectNode();
        api.put("api-key", apiKey);
        ObjectNode resource = objectMapper.createObjectNode();
        resource.put("method", method);
        if (uriTemplate != null) {
            resource.put("uri-template", uriTemplate);
        } else {
            resource.put("url-mapping", urlMapping);
        }
        api.set("resource", resource);
        api.put("sequence-type", sequenceType);
        api.put("mediator-position", mediatorPosition);
        sequence.set("api", api);
        rootNode.set("sequence", sequence);

        rootNode.put("command", "set");
        rootNode.put("command-argument", "breakpoint");
        rootNode.put("mediation-component", "sequence");

        return objectMapper.writeValueAsString(rootNode);
    }
}
