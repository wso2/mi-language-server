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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class TemplateDebugInfo extends DebugInfo {

    String templateKey;

    public void setTemplateKey(String templateKey) {

        this.templateKey = templateKey;
    }

    @Override
    public JsonElement toJson() {

        JsonObject rootNode = new JsonObject();
        JsonObject template = new JsonObject();
        template.addProperty("template-key", templateKey);
        template.addProperty("mediator-position", mediatorPosition);
        rootNode.add("template", template);

        rootNode.addProperty("command", "set");
        rootNode.addProperty("command-argument", "breakpoint");
        rootNode.addProperty("mediation-component", "template");

        return rootNode;
    }
}
