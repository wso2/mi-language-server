/*
 * Copyright (c) 2023, WSO2 LLC. (http://www.wso2.com).
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

package org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo;

import org.eclipse.lemminx.commons.BadLocationException;
import org.eclipse.lemminx.dom.DOMDocument;
import org.eclipse.lemminx.dom.DOMElement;
import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lsp4j.Position;

public class STNode {

    Range range;
    boolean hasTextNode;
    String textNode;
    boolean selfClosed;
    String tag;

    public void elementNode(DOMElement node) {

        this.selfClosed = node.isSelfClosed();
        this.tag = node.getTagName();
        this.range = findRange(node);
        DOMNode firstChild = node.getFirstChild();
        if (firstChild != null && firstChild.isText()) {
            this.hasTextNode = firstChild.isText();
            this.textNode = firstChild.getTextContent();
        }
    }

    private Range findRange(DOMElement node) {

        int startTagOpenOffset = node.getStart();
        int startTagCloseOffset = selfClosed ? node.getEnd() : node.getStartTagCloseOffset() + 1;
        int endTagOpenOffset = node.getEndTagOpenOffset();
        int endTagCloseOffset = node.getEnd();

        DOMDocument document = node.getOwnerDocument();
        Position startTagOpenPosition = null;
        Position startTagClosePosition = null;
        Position endTagOpenPosition = null;
        Position endTagClosePosition = null;
        try {
            startTagOpenPosition = document.positionAt(startTagOpenOffset);
            startTagClosePosition = document.positionAt(startTagCloseOffset);
            endTagOpenPosition = document.positionAt(endTagOpenOffset);
            endTagClosePosition = document.positionAt(endTagCloseOffset);
        } catch (BadLocationException e) {
        }
        TagRange startTagRange = new TagRange(startTagOpenPosition, startTagClosePosition);
        TagRange endTagRange = new TagRange(endTagOpenPosition, endTagClosePosition);

        return new Range(startTagRange, endTagRange);
    }

    public String getTag() {

        return tag;
    }

    public void setTag(String tag) {

        this.tag = tag;
    }
}
