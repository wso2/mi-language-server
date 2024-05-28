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

package org.eclipse.lemminx.synapse.serializer;

import org.eclipse.lemminx.commons.TextDocument;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.MessageProcessorFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.MessageProcessor;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.MessageProcessorSerializer;
import org.eclipse.lemminx.dom.DOMDocument;
import org.eclipse.lemminx.dom.DOMParser;
import org.eclipse.lemminx.uriresolver.URIResolverExtensionManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MessageProcessorSerializerTest {

    @Test
    public void testSerializeMessageProcessor() {

        String xml = "<messageProcessor xmlns=\"http://ws.apache.org/ns/synapse\" name=\"mp\" class=\"org.apache.synapse.message.processor.impl.forwarder.ScheduledMessageForwardingProcessor\" messageStore=\"messageStore\" targetEndpoint=\"/hello\">" +
                "<parameter name=\"client.retry.interval\">1000</parameter>" +
                "<parameter name=\"max.delivery.attempts\">4</parameter>" +
                "<parameter name=\"member.count\">1</parameter>" +
                "<parameter name=\"message.processor.fault.sequence\">sample</parameter>" +
                "<parameter name=\"store.connection.retry.interval\">1000</parameter>" +
                "<parameter name=\"max.store.connection.attempts\">-1</parameter>" +
                "<parameter name=\"max.delivery.drop\">Disabled</parameter>" +
                "<parameter name=\"interval\">1000</parameter>" +
                "<parameter name=\"is.active\">true</parameter>" +
                "</messageProcessor>";

        test(xml);
    }

    private void test(String xml) {

        TextDocument document = new TextDocument(xml, "test.xml");

        DOMDocument xmlDocument = DOMParser.getInstance().parse(document,
                new URIResolverExtensionManager());

        MessageProcessorFactory messageProcessorFactory = new MessageProcessorFactory();
        MessageProcessor messageProcessor =
                (MessageProcessor) messageProcessorFactory.create(xmlDocument.getDocumentElement());

        String actual = MessageProcessorSerializer.serializeMessageProcessor(messageProcessor);

        System.out.println(actual);
        assertEquals(xml, actual);
    }

}
