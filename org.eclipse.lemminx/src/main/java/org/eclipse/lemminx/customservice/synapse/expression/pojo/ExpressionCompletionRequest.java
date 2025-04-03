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

import org.eclipse.lemminx.commons.BadLocationException;
import org.eclipse.lemminx.dom.DOMAttr;
import org.eclipse.lemminx.dom.DOMDocument;
import org.eclipse.lemminx.dom.DOMElement;
import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.dom.LineIndentInfo;
import org.eclipse.lemminx.extensions.contentmodel.utils.XMLGenerator;
import org.eclipse.lemminx.services.extensions.completion.ICompletionRequest;
import org.eclipse.lemminx.settings.SharedSettings;
import org.eclipse.lsp4j.InsertTextFormat;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;

public class ExpressionCompletionRequest implements ICompletionRequest {

    private final DOMDocument xmlDocument;
    private final Position position;
    private int offset;

    public ExpressionCompletionRequest(DOMDocument xmlDocument, Position position) {

        this.xmlDocument = xmlDocument;
        this.position = position;
    }

    public ExpressionCompletionRequest(DOMDocument xmlDocument, Position position, int offset) {

        this.xmlDocument = xmlDocument;
        this.position = position;
        this.offset = offset;
    }

    @Override
    public int getOffset() {

        return offset;
    }

    @Override
    public Position getPosition() {

        return position;
    }

    @Override
    public DOMNode getNode() {

        throw new UnsupportedOperationException();
    }

    @Override
    public DOMElement getParentElement() {

        throw new UnsupportedOperationException();
    }

    @Override
    public DOMDocument getXMLDocument() {

        return xmlDocument;
    }

    @Override
    public String getCurrentTag() {

        throw new UnsupportedOperationException();
    }

    @Override
    public DOMAttr getCurrentAttribute() {

        throw new UnsupportedOperationException();
    }

    @Override
    public String getCurrentAttributeName() {

        throw new UnsupportedOperationException();
    }

    @Override
    public LineIndentInfo getLineIndentInfo() throws BadLocationException {

        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T getComponent(Class clazz) {

        throw new UnsupportedOperationException();
    }

    @Override
    public boolean canSupportMarkupKind(String kind) {

        return false;
    }

    @Override
    public SharedSettings getSharedSettings() {

        return null;
    }

    @Override
    public Range getReplaceRange() {

        return null;
    }

    @Override
    public Range getReplaceRangeForTagName() {

        return null;
    }

    @Override
    public XMLGenerator getXMLGenerator() throws BadLocationException {

        return null;
    }

    @Override
    public String getFilterForStartTagName(String tagName) {

        return null;
    }

    @Override
    public String getInsertAttrValue(String value) {

        return null;
    }

    @Override
    public boolean isCompletionSnippetsSupported() {

        return false;
    }

    @Override
    public boolean isAutoCloseTags() {

        return false;
    }

    @Override
    public boolean isResolveDocumentationSupported() {

        return false;
    }

    @Override
    public boolean isResolveAdditionalTextEditsSupported() {

        return false;
    }

    @Override
    public InsertTextFormat getInsertTextFormat() {

        return null;
    }
}
