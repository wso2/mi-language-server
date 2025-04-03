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
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

import java.util.ArrayList;
import java.util.List;

public class HelperPanelItem extends CompletionItem {

    private List<CompletionItem> children;

    public HelperPanelItem(String label, String insertText) {

        setLabel(label);
        setInsertText(insertText);
        children = new ArrayList<>();
    }

    public void addChild(CompletionItem data) {

        this.children.add(data);
    }

    public void addChildren(List<CompletionItem> data) {

        this.children.addAll(data);
    }

    @Override
    public String toString() {

        ToStringBuilder builder = new ToStringBuilder(this);
        builder.add("label", getLabel());
        builder.add("kind", getKind());
        builder.add("insertText", getInsertText());
        builder.add("insertTextFormat", getInsertTextFormat());
        builder.add("detail", getDetail());
        builder.add("documentation", getDocumentation());
        builder.add("sortText", getSortText());
        builder.add("filterText", getFilterText());
        builder.add("children", children);
        return builder.toString();
    }

    @Override
    public boolean equals(Object obj) {

        return super.equals(obj) && children.equals(((HelperPanelItem) obj).children);
    }

    @Override
    public int hashCode() {

        return super.hashCode() + children.hashCode();
    }
}
