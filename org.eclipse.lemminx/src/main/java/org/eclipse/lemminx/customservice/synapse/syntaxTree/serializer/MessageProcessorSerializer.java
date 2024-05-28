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

package org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.MessageProcessor;
import org.eclipse.lemminx.customservice.synapse.utils.Constant;

public class MessageProcessorSerializer {

    private static final OMFactory fac = OMAbstractFactory.getOMFactory();

    public static String serializeMessageProcessor(MessageProcessor messageProcessor) {

        OMElement messageProcessorElt = fac.createOMElement("messageProcessor", Constant.SYNAPSE_OMNAMESPACE);

        serializeAttributes(messageProcessor, messageProcessorElt);
        SerializerUtils.serializeParameters(messageProcessor.getParameter(), messageProcessorElt);

        return messageProcessorElt.toString();
    }

    private static void serializeAttributes(MessageProcessor messageProcessor, OMElement messageProcessorElt) {

        if (messageProcessor.getName() != null) {
            messageProcessorElt.addAttribute("name", messageProcessor.getName(), null);
        }
        if (messageProcessor.getClazz() != null) {
            messageProcessorElt.addAttribute("class", messageProcessor.getClazz(), null);
        }
        if (messageProcessor.getMessageStore() != null) {
            messageProcessorElt.addAttribute("messageStore", messageProcessor.getMessageStore(), null);
        }
        if (messageProcessor.getTargetEndpoint() != null) {
            messageProcessorElt.addAttribute("targetEndpoint", messageProcessor.getTargetEndpoint(), null);
        }
    }
}
