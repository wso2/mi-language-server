/*
 * Copyright (c) 2025, WSO2 LLC. (http://www.wso2.com).
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

package org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.ai;

import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.connector.ai.AIChat;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.connector.ai.AIConnector;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.Mediator;
import org.eclipse.lemminx.dom.DOMElement;

import java.util.List;
import java.util.logging.Logger;

public class AIChatConnectorFactory extends AIConnectorFactory {

    private static final Logger LOGGER = Logger.getLogger(AIChatConnectorFactory.class.getName());
    private static final String AI_CHAT_AGENT = "ai.chat";
    private static final List<String> ALLOWED_CONNECTION_TAGS = List.of("llmConfigKey", "memoryConfigKey");

    @Override
    protected Mediator createSpecificMediator(DOMElement element) {

        AIConnector aiChat = new AIChat();
        populateConnectorConfigs(aiChat, element);
        populateConnections((AIChat) aiChat, element);
        return aiChat;
    }

    protected void populateConnections(AIChat aiChat, DOMElement element) {

        populateConnections(aiChat, element, ALLOWED_CONNECTION_TAGS);
    }

    @Override
    public String getTagName() {

        return AI_CHAT_AGENT;
    }
}
