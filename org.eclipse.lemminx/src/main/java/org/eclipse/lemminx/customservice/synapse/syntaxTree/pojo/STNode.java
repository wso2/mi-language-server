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
import org.eclipse.lemminx.customservice.synapse.utils.Utils;
import org.eclipse.lemminx.dom.DOMAttr;
import org.eclipse.lemminx.dom.DOMDocument;
import org.eclipse.lemminx.dom.DOMElement;
import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;

import java.util.HashMap;
import java.util.List;

public class STNode {

    TagRanges range;
    boolean hasTextNode;
    String textNode;
    boolean selfClosed;
    String tag;
    HashMap<String, String> namespaces;

    public STNode() {

        this.namespaces = new HashMap<>();
    }

    public void elementNode(DOMElement node) {

        this.selfClosed = node.isSelfClosed();
        this.tag = node.getTagName();
        this.range = findRange(node);
        DOMNode firstChild = node.getFirstChild();
        if (firstChild != null && firstChild.isText()) {
            this.hasTextNode = firstChild.isText();
            this.textNode = firstChild.getTextContent();
        }
        namespaces = new HashMap<>();
        populateNamespaces(node);
    }

    private TagRanges findRange(DOMElement node) {

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
        TagRanges range = new TagRanges(startTagRange, endTagRange);
        calculateLeadingAndTrailingSpaces(node, range);
        return range;
    }

    private void calculateLeadingAndTrailingSpaces(DOMElement node, TagRanges range) {

        calculateStartTagSpaces(node, range);
        calculateEndTagSpaces(node, range);
    }

    private void calculateStartTagSpaces(DOMElement node, TagRanges range) {

        calculateStartTagLeadingSpaces(node, range.getStartTagRange());
        calculateStartTagTrailingSpaces(node, range.getStartTagRange());
    }

    private void calculateStartTagLeadingSpaces(DOMElement node, TagRange range) {

        DOMDocument document = node.getOwnerDocument();
        int startOffsetOfLeadingSpaces;
        if (node.getPreviousNonTextSibling() != null) {
            DOMNode previousNonTextSibling = node.getPreviousNonTextSibling();
            startOffsetOfLeadingSpaces = previousNonTextSibling.getEnd();
        } else if (node.getParentElement() != null) {
            DOMElement parentElement = node.getParentElement();
            startOffsetOfLeadingSpaces = parentElement.getStartTagCloseOffset() + 1;
        } else {
            startOffsetOfLeadingSpaces = 0;
        }
        try {
            Position startLeadingSpacesPosition = document.positionAt(startOffsetOfLeadingSpaces);
            Position endLeadingSpacesPosition = range.getStart();
            range.setLeadingSpace(new Range(startLeadingSpacesPosition, endLeadingSpacesPosition));
            range.setLeadingSpaceText(getTextInRange(document, startLeadingSpacesPosition, endLeadingSpacesPosition));
        } catch (BadLocationException e) {
        }
    }

    private void calculateStartTagTrailingSpaces(DOMElement node, TagRange range) {

        DOMDocument document = node.getOwnerDocument();
        int endOffsetOfTrailingSpaces;
        if (hasChildNodes(node)) {
            DOMNode firstChild = node.getFirstChild();
            endOffsetOfTrailingSpaces = firstChild.getStart();
        } else if (!node.isSelfClosed()) {
            endOffsetOfTrailingSpaces = node.getEndTagOpenOffset();
        } else if (node.getParentElement() != null) {
            endOffsetOfTrailingSpaces = node.getParentElement().getEndTagOpenOffset();
        } else {
            endOffsetOfTrailingSpaces = node.getOwnerDocument().getEnd();
        }
        try {
            Position startTrailingSpacesPosition = range.getEnd();
            Position endTrailingSpacesPosition = document.positionAt(endOffsetOfTrailingSpaces);
            range.setTrailingSpace(new Range(startTrailingSpacesPosition, endTrailingSpacesPosition));
            range.setTrailingSpaceText(getTextInRange(document, startTrailingSpacesPosition,
                    endTrailingSpacesPosition));
        } catch (BadLocationException e) {
        }
    }

    private void calculateEndTagSpaces(DOMElement node, TagRanges range) {

        if (node.isSelfClosed()) {
            return;
        }

        calculateEndTagLeadingSpaces(node, range.getEndTagRange());
        calculateEndTagTrailingSpaces(node, range.getEndTagRange());
    }

    private void calculateEndTagLeadingSpaces(DOMElement node, TagRange range) {

        DOMDocument document = node.getOwnerDocument();

        int startOffsetOfLeadingSpaces;
        if (hasChildNodes(node)) {
            DOMNode lastChild = node.getLastChild();
            startOffsetOfLeadingSpaces = lastChild.getEnd();
        } else if (!node.isSelfClosed()) {
            startOffsetOfLeadingSpaces = node.getStartTagCloseOffset() + 1;
        } else if (node.getPreviousNonTextSibling() != null) {
            DOMNode previousNonTextSibling = node.getPreviousNonTextSibling();
            startOffsetOfLeadingSpaces = previousNonTextSibling.getEnd();
        } else if (node.getParentElement() != null) {
            DOMElement parentElement = node.getParentElement();
            startOffsetOfLeadingSpaces = parentElement.getEndTagCloseOffset() + 1;
        } else {
            startOffsetOfLeadingSpaces = 0;
        }
        try {
            Position startLeadingSpacesPosition = document.positionAt(startOffsetOfLeadingSpaces);
            Position endLeadingSpacesPosition = range.getStart();
            range.setLeadingSpace(new Range(startLeadingSpacesPosition, endLeadingSpacesPosition));
            range.setLeadingSpaceText(getTextInRange(document, startLeadingSpacesPosition, endLeadingSpacesPosition));
        } catch (BadLocationException e) {
        }
    }

    private void calculateEndTagTrailingSpaces(DOMElement node, TagRange range) {

        DOMDocument document = node.getOwnerDocument();
        int endOffsetOfTrailingSpaces;

        if (node.getNextSibling() != null) {
            endOffsetOfTrailingSpaces = node.getNextSibling().getStart();
        } else if (node.getParentElement() != null) {
            endOffsetOfTrailingSpaces = node.getParentElement().getEndTagOpenOffset();
        } else {
            endOffsetOfTrailingSpaces = node.getOwnerDocument().getEnd();
        }
        try {
            Position startTrailingSpacesPosition = range.getEnd();
            Position endTrailingSpacesPosition = document.positionAt(endOffsetOfTrailingSpaces);
            range.setTrailingSpace(new Range(startTrailingSpacesPosition, endTrailingSpacesPosition));
            range.setTrailingSpaceText(getTextInRange(document, startTrailingSpacesPosition,
                    endTrailingSpacesPosition));
        } catch (BadLocationException e) {
        }
    }

    private boolean hasChildNodes(DOMElement node) {

        DOMElement firstChild = Utils.getFirstElement(node);
        return firstChild != null;
    }

    private void populateNamespaces(DOMElement node) {

        List<DOMAttr> attributes = node.getAttributeNodes();
        if (attributes != null) {
            for (DOMAttr attribute : attributes) {
                if (attribute.getName().startsWith("xmlns")) {
                    namespaces.put(attribute.getName(), attribute.getValue());
                }
            }
        }
    }

    private String getTextInRange(DOMDocument document, Position startTrailingSpacesPosition,
                                  Position endTrailingSpacesPosition) throws BadLocationException {

        String text = document.getText();
        int startOffset = document.offsetAt(startTrailingSpacesPosition);
        int endOffset = document.offsetAt(endTrailingSpacesPosition);

        if (text != null) {
            return text.substring(startOffset, endOffset);
        }
        return null;
    }


    public void addNamespace(String prefix, String uri) {

        namespaces.put(prefix, uri);
    }

    public TagRanges getRange() {

        return range;
    }

    public String getTag() {

        return tag;
    }

    public boolean isSelfClosed() {

        return selfClosed;
    }

    public String getTextNode() {

        return textNode;
    }

    public void setTextNode(String textNode) {

        this.textNode = textNode;
    }

    public void setTag(String tag) {

        this.tag = tag;
    }

    public HashMap<String, String> getNamespaces() {

        return namespaces;
    }
}
