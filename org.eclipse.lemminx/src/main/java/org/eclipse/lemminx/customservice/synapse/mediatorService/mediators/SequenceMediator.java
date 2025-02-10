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
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SequenceMediator {

    public static Either<Map<String, Object>, Map<Range, Map<String, Object>>> processData430(Map<String, Object> data,
                                                                                              org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.SequenceMediator sequence,
                                                                                              List<String> dirtyFields) {
        Object referringSequenceObj = data.get("referringSequence");
        if (referringSequenceObj instanceof Map<?, ?>) {
            Map<String, Object> referringSequence = (Map<String, Object>) referringSequenceObj;
            if (Boolean.TRUE.equals(referringSequence.get("isExpression"))) {
                referringSequence.put("value", "{" + referringSequence.get("value") + "}");
            } else {
                referringSequence.put("value", referringSequence.get("value"));
            }
        }

        return Either.forLeft(data);

    }

    public static Map<String, Object> getDataFromST430(org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.SequenceMediator node) {

        Map<String, Object> data = new HashMap<>();
        boolean isExpression = node.getKey() != null && node.getKey().startsWith("{") && node.getKey().endsWith("}");
        String value = node.getKey();
        if (isExpression && value != null) {
            value = value.substring(1, value.length() - 1);
        }

        Map<String, Object> referringSequence = new HashMap<>();
        referringSequence.put("isExpression", isExpression);
        referringSequence.put("value", value);
        referringSequence.put("namespaces", MediatorUtils.transformNamespaces(node.getNamespaces()));
        data.put("description", node.getDescription());
        data.put("referringSequence", referringSequence);

        return data;
    }

}
