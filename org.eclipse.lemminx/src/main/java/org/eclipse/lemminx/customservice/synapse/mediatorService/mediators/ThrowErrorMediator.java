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
package org.eclipse.lemminx.customservice.synapse.mediatorService.mediators;

import org.eclipse.lemminx.customservice.synapse.mediatorService.MediatorUtils;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.ThrowError;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThrowErrorMediator {
    public static Either<Map<String, Object>, Map<Range, Map<String, Object>>> processData(Map<String, Object> data,
                                                                                           ThrowError throwError,
                                                                                           List<String> dirtyFields) {
        Object errorMessageObj = data.get("errorMessage");
        if (errorMessageObj instanceof Map<?, ?>) {
            Map<String, Object> errorMessage = (Map<String, Object>) errorMessageObj;
            if (Boolean.TRUE.equals(errorMessage.get("isExpression"))) {
                errorMessage.put("value", "{" + errorMessage.get("value") + "}");
            } else {
                errorMessage.put("value", errorMessage.get("value"));
            }
        }

        return Either.forLeft(data);
    }

    public static Map<String, Object> getDataFromST(ThrowError throwError) {
        Map<String, Object> data = new HashMap<>();

        boolean isExpression = throwError.getErrorMessage() != null && throwError.getErrorMessage().startsWith("{") &&
                throwError.getErrorMessage().endsWith("}");
        String value = throwError.getErrorMessage();
        if (isExpression && value != null) {
            value = value.substring(1, value.length() - 1);
        }

        Map<String, Object> referringSequence = new HashMap<>();
        referringSequence.put("isExpression", isExpression);
        referringSequence.put("value", value);
        referringSequence.put("namespaces", MediatorUtils.transformNamespaces(throwError.getNamespaces()));
        data.put("description", throwError.getDescription());
        data.put("type", throwError.getType());
        data.put("errorMessage", referringSequence);
        return data;
    }
}
