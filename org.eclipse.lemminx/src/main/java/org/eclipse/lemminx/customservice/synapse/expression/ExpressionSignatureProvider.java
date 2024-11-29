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

import org.eclipse.lemminx.customservice.synapse.expression.pojo.ExpressionParam;
import org.eclipse.lemminx.customservice.synapse.expression.pojo.FunctionInfo;
import org.eclipse.lemminx.customservice.synapse.expression.pojo.FunctionCompletionItem;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.SignatureHelp;
import org.eclipse.lsp4j.SignatureInformation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.logging.Logger;

public class ExpressionSignatureProvider {

    private static final Logger LOGGER = Logger.getLogger(ExpressionSignatureProvider.class.getName());
    private static Map<String, List<SignatureInformation>> functionSignatures = new HashMap<>();

    static {
        loadFunctionSignatures();
    }

    private static void loadFunctionSignatures() {

        Map<String, List<CompletionItem>> functionCompletionItems = ExpressionCompletionUtils.getFunctions();
        for (Map.Entry<String, List<CompletionItem>> entry : functionCompletionItems.entrySet()) {
            for (CompletionItem completionItem : entry.getValue()) {
                SignatureInformation signatureInformation = ((FunctionCompletionItem) completionItem).getSignature();
                String label = completionItem.getLabel().substring(0, completionItem.getLabel().indexOf('('));
                if (functionSignatures.containsKey(label)) {
                    functionSignatures.get(label).add(signatureInformation);
                } else {
                    List<SignatureInformation> signatures = new ArrayList<>();
                    signatures.add(signatureInformation);
                    functionSignatures.put(label, signatures);
                }
            }
        }
    }

    public static SignatureHelp getFunctionSignatures(ExpressionParam params) {

        return getFunctionSignatures(params.getExpression(), params.getOffset());

    }

    private static SignatureHelp getFunctionSignatures(String expression, int expressionOffset) {

        FunctionInfo currentFunctionInfo = findCurrentFunction(expression, expressionOffset);
        if (currentFunctionInfo != null) {
            List<SignatureInformation> signatureInformation = functionSignatures.get(currentFunctionInfo.getName());
            if (signatureInformation != null) {
                int activeSignature =
                        findActiveSignature(signatureInformation, currentFunctionInfo.getCurrentParameterIndex());
                return new SignatureHelp(signatureInformation, activeSignature,
                        currentFunctionInfo.getCurrentParameterIndex());
            }
        }
        return null;
    }

    private static int findActiveSignature(List<SignatureInformation> signatureInformation, int currentParameterIndex) {

        int activeSignature = 0;
        for (int i = 0; i < signatureInformation.size(); i++) {
            if (signatureInformation.get(i).getParameters().size() > currentParameterIndex) {
                activeSignature = i;
                break;
            }
        }
        return activeSignature;
    }

    public static FunctionInfo findCurrentFunction(String input, int cursorPosition) {

        Stack<FunctionInfo> functionStack = new Stack<>();

        for (int i = 0; i < cursorPosition; i++) {
            char c = input.charAt(i);

            if (c == '(') {
                // Identify the function name before '('
                int start = i - 1;
                while (start >= 0 && (Character.isLetterOrDigit(input.charAt(start)) || input.charAt(start) == '_')) {
                    start--;
                }
                start++;

                String functionName = input.substring(start, i).trim();
                functionStack.push(new FunctionInfo(functionName, 0));
            } else if (c == ')') {
                if (!functionStack.isEmpty()) {
                    functionStack.pop();
                }
            } else if (c == ',' && !functionStack.isEmpty()) {
                // Increment parameter index of the current function context
                functionStack.peek().incrementParameterIndex();
            }
        }

        // If the stack is not empty, we are inside a function
        if (!functionStack.isEmpty()) {
            FunctionInfo currentContext = functionStack.peek();
            return new FunctionInfo(currentContext.getName(), currentContext.getCurrentParameterIndex());
        }

        return null;
    }
}
