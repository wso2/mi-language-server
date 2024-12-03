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

package org.eclipse.lemminx.customservice.synapse.expression.pojo;

import org.eclipse.lsp4j.CompletionItem;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class HelperPanelData {

    private List<CompletionItem> payload;
    private List<CompletionItem> variables;
    private List<CompletionItem> properties;
    private List<CompletionItem> params;
    private List<CompletionItem> headers;
    private Map<String, List<CompletionItem>> functions;

    public List<CompletionItem> getPayload() {

        return Collections.unmodifiableList(payload);
    }

    public void setPayload(List<CompletionItem> payload) {

        this.payload = payload;
    }

    public List<CompletionItem> getVariables() {

        return Collections.unmodifiableList(variables);
    }

    public void setVariables(List<CompletionItem> variables) {

        this.variables = variables;
    }

    public List<CompletionItem> getProperties() {

        return Collections.unmodifiableList(properties);
    }

    public void setProperties(List<CompletionItem> properties) {

        this.properties = properties;
    }

    public List<CompletionItem> getParams() {

        return params;
    }

    public void setParams(List<CompletionItem> params) {

        this.params = params;
    }

    public List<CompletionItem> getHeaders() {

        return headers;
    }

    public void setHeaders(List<CompletionItem> headers) {

        this.headers = headers;
    }

    public Map<String, List<CompletionItem>> getFunctions() {

        return Collections.unmodifiableMap(functions);
    }

    public void setFunctions(Map<String, List<CompletionItem>> functions) {

        this.functions = functions;
    }

    @Override
    public String toString() {

        return "HelperPanelData{" +
                "payload=" + payload +
                ", variables=" + variables +
                ", properties=" + properties +
                ", params=" + params +
                ", headers=" + headers +
                ", functions=" + functions +
                '}';
    }
}
