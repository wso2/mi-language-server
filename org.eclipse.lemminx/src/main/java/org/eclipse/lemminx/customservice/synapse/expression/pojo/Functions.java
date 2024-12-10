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

import java.util.ArrayList;
import java.util.List;

public class Functions {

    private String sortText;
    private List<CompletionItem> items;

    public Functions() {

        this.items = new ArrayList<>();
    }

    public Functions(String sortText, List<CompletionItem> functions) {

        this.sortText = sortText;
        this.items = functions;
    }

    public String getSortText() {

        return sortText;
    }

    public void setSortText(String sortText) {

        this.sortText = sortText;
    }

    public List<CompletionItem> getItems() {

        return items;
    }

    public void setItems(List<CompletionItem> items) {

        this.items = items;
    }

    public Functions deepCopy() {

        return new Functions(this.sortText, new ArrayList<>(this.items));
    }
}
