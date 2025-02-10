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

import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.other.builder.Builder;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.other.builder.BuilderMessageBuilder;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuilderMediator {
    public static Either<Map<String, Object>, Map<Range, Map<String, Object>>> processData430(Map<String, Object> data,
                                                                                              Builder builder,
                                                                                              List<String> dirtyFields) {
        List<Object> messageBuildersData = data.get("messageBuilders") instanceof List<?> ?
                (List<Object>) data.get("messageBuilders") : new ArrayList<>();
        List<Map<String, String>> messageBuilders = new ArrayList<>();

        for (Object messageBuilderDataObj : messageBuildersData) {
            if (messageBuilderDataObj instanceof List<?>) {
                Map<String, String> messageBuilder = new HashMap<>();
                List<Object> messageBuilderData = (List<Object>) messageBuilderDataObj;
                messageBuilder.put("contentType", messageBuilderData.get(0) instanceof String ?
                        (String) messageBuilderData.get(0) : "");
                messageBuilder.put("builderClass", messageBuilderData.get(1) instanceof String ?
                        (String) messageBuilderData.get(1) : "");
                messageBuilder.put("formatterClass", messageBuilderData.get(2) instanceof String ?
                        (String) messageBuilderData.get(2) : "");
                messageBuilders.add(messageBuilder);
            }
        }
        data.put("messageBuilders", messageBuilders);

        return Either.forLeft(data);
    }

    public static Map<String, Object> getDataFromST430(Builder node) {
        Map<String, Object> data = new HashMap<>();
        data.put("description", node.getDescription());

        List<List<String>> messageBuilders = new ArrayList<>();
        for (BuilderMessageBuilder messageBuilder : node.getMessageBuilders()) {
            List<String> messageBuilderData = new ArrayList<>();
            messageBuilderData.add(messageBuilder.getContentType());
            messageBuilderData.add(messageBuilder.getClazz());
            messageBuilderData.add(messageBuilder.getFormatterClass());
            messageBuilders.add(messageBuilderData);
        }
        data.put("messageBuilders", messageBuilders);
        return data;
    }
}
