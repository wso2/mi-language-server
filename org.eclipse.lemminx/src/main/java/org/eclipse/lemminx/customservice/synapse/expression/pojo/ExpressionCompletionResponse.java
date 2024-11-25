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

import org.eclipse.lemminx.services.extensions.completion.ICompletionResponse;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionList;

import java.util.ArrayList;
import java.util.List;

public class ExpressionCompletionResponse extends CompletionList implements ICompletionResponse {

    private transient List<String> seenAttributes;
    private transient boolean hasSomeItemFromGrammar;

    public ExpressionCompletionResponse() {

        super.setIsIncomplete(false);
    }

    @Override
    public void addCompletionItem(CompletionItem completionItem, boolean fromGrammar) {

        if (fromGrammar) {
            hasSomeItemFromGrammar = true;
        }
        addCompletionItem(completionItem);
    }

    @Override
    public void addCompletionItem(CompletionItem completionItem) {

        super.getItems().add(completionItem);
    }

    @Override
    public boolean hasSomeItemFromGrammar() {

        return hasSomeItemFromGrammar;
    }

    @Override
    public boolean hasAttribute(String attribute) {
        /*
         * if (node != null && node.hasAttribute(attribute)) { return true; }
         */
        return seenAttributes != null ? seenAttributes.contains(attribute) : false;
    }

    @Override
    public void addCompletionAttribute(CompletionItem completionItem) {

        if (seenAttributes == null) {
            seenAttributes = new ArrayList<>();
        }
        // TODO: Add quotations to the completion item.
        seenAttributes.add(completionItem.getLabel());
        addCompletionItem(completionItem);
    }
}
