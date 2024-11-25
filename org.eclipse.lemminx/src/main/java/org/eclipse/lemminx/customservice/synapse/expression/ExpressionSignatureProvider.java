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

package org.eclipse.lemminx.customservice.synapse.expression;

import org.eclipse.lemminx.commons.BadLocationException;
import org.eclipse.lemminx.customservice.synapse.utils.Utils;
import org.eclipse.lemminx.dom.DOMAttr;
import org.eclipse.lemminx.dom.DOMDocument;
import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.SignatureHelp;
import org.eclipse.lsp4j.SignatureHelpParams;
import org.eclipse.lsp4j.TextDocumentIdentifier;

import java.io.File;
import java.io.IOException;

public class ExpressionSignatureProvider {

    public static final SignatureHelp getFunctionSignatures(SignatureHelpParams params) {

        try {
            TextDocumentIdentifier documentIdentifier = params.getTextDocument();
            String filePath = documentIdentifier.getUri();
            if (filePath.startsWith("file://")) {
                filePath = filePath.substring(7);
            }
            Position position = params.getPosition();
            DOMDocument document = Utils.getDOMDocument(new File(filePath));
            DOMAttr attr = document.findAttrAt(document.offsetAt(position));
            DOMNode node = document.findNodeAt(document.offsetAt(position));
            String expression;
            int expressionOffset;
            if (attr != null) {
                expressionOffset = getExpressionOffset(attr, document.offsetAt(position));
                expression = attr.getValue();

            } else if (node != null) {
                expressionOffset = getExpressionOffset(node, document.offsetAt(position));
                expression = Utils.getInlineString(node);
            } else {
                return null;
            }
            return getFunctionSignatures(expression, expressionOffset);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (BadLocationException e) {
            throw new RuntimeException(e);
        }
    }

    private static int getExpressionOffset(DOMNode attr, int offset) {

        return offset - attr.getStart();
    }

    private static SignatureHelp getFunctionSignatures(String expression, int expressionOffset) {

        return null;
    }

    private static String getCurrentFunction(String expression, int expressionOffset) {

        StringBuilder currentFunction = new StringBuilder();
        boolean isFunction = false;
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            if (c == '(') {
                isFunction = true;
            } else if (ExpressionConstants.OPERATORS.contains(String.valueOf(c)) || c == ',' || c == ' ') {
                isFunction = false;
                currentFunction = new StringBuilder();
            } else {
                currentFunction.append(c);
            }
        }
        return isFunction ? currentFunction.toString() : null;
    }

    public static void main(String[] args) {

        String expression = "concat('Hell') + new(\"\")+var.var1+substring(";
        String currentFunction = getCurrentFunction(expression, expression.length());
        System.out.println(currentFunction);
    }

}
