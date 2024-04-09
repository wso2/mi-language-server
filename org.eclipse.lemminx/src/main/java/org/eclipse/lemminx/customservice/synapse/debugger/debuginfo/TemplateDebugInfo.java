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

public class TemplateDebugInfo extends DebugInfo {

    String templateKey;

    public TemplateDebugInfo() {

        objectMapper = new ObjectMapper();
    }

    public void setTemplateKey(String templateKey) {

        this.templateKey = templateKey;
    }

    public String toJsonString() throws JsonProcessingException {

        ObjectNode rootNode = objectMapper.createObjectNode();

        ObjectNode template = objectMapper.createObjectNode();
        template.put("template-key", templateKey);
        template.put("mediator-position", mediatorPosition);
        rootNode.set("template", template);

        rootNode.put("command", "set");
        rootNode.put("command-argument", "breakpoint");
        rootNode.put("mediation-component", "template");

        return objectMapper.writeValueAsString(rootNode);
    }

}
