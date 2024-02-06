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

import org.eclipse.lemminx.dom.DOMElement;
import org.eclipse.lemminx.dom.DOMNode;

public class STNode {

    Range range;
    boolean hasTextNode;
    String textNode;
    boolean selfClosed;
    String tag;

    public void elementNode(DOMElement node) {

        this.range = findRange(node);
        this.tag = node.getTagName();
        this.selfClosed = node.isSelfClosed();
        DOMNode firstChild = node.getFirstChild();
        if (firstChild != null && firstChild.isText()) {
            this.hasTextNode = firstChild.isText();
            this.textNode = firstChild.getTextContent();
        }
    }

    private Range findRange(DOMNode node) {

        String content = node.getOwnerDocument().getTextDocument().getText();
        Position startposition = findLineCharPosition(content, node.getStart());
        Position endposition = findLineCharPosition(content, node.getEnd());
        return new Range(startposition, endposition);
    }

    private static Position findLineCharPosition(String content, int offset) {

        int line = 0;
        int character = 0;
        for (int i = 0; i < offset; i++) {
            char currentChar = content.charAt(i);

            if (currentChar == '\n') {
                line++;
                character = 0;
            } else {
                character++;
            }
        }

        return new Position(line, character);
    }

    public String getTag() {

        return tag;
    }

    public void setTag(String tag) {

        this.tag = tag;
    }
}
