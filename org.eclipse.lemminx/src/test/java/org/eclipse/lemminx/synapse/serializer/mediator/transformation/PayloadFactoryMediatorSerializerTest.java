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

package org.eclipse.lemminx.synapse.serializer.mediator.transformation;

import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.transformation.PayloadFactoryFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.mediator.transformation.PayloadFactoryMediatorSerializer;
import org.eclipse.lemminx.synapse.serializer.mediator.MediatorSerializerTest;
import org.junit.jupiter.api.Test;

public class PayloadFactoryMediatorSerializerTest extends MediatorSerializerTest {

    public PayloadFactoryMediatorSerializerTest() {

        factory = new PayloadFactoryFactory();
        serializer = new PayloadFactoryMediatorSerializer();
    }

    @Test
    public void testPayloadFactoryMediatorWithStaticKey() {

        String xml = "<payloadFactory xmlns=\"http://ws.apache.org/ns/synapse\" media-type=\"xml\" " +
                "description=\"test\"/>";
        testSerializeMediator(xml, true);
    }

    @Test
    public void testPayloadFactoryMediatorWithDefaultPayload() {

        String xml = "<payloadFactory xmlns=\"http://ws.apache.org/ns/synapse\" media-type=\"xml\" " +
                "description=\"test\"><format><inline xmlns=\"\"/></format><args><arg value=\"default\" " +
                "literal=\"true\"/><arg expression=\"$ctx:test\" xmlns:test=\"http://www.test.com\" " +
                "evaluator=\"xml\"/></args></payloadFactory>";
        testSerializeMediator(xml, true);
    }

    @Test
    public void testPayloadFactoryMediatorWithFreemarkerPayload() {

        String xml = "<payloadFactory xmlns=\"http://ws.apache.org/ns/synapse\" media-type=\"xml\" " +
                "template-type=\"freemarker\" description=\"test\"><format><![CDATA[<inline/>]]></format><args><arg " +
                "value=\"default\" literal=\"true\"/><arg expression=\"$ctx:test\" xmlns:test=\"http://www.test.com\"" +
                " evaluator=\"xml\"/></args></payloadFactory>";
        testSerializeMediator(xml, true);
    }

    @Test
    public void testPayloadFactoryMediatorWithRegistryPayload() {

        String xml = "<payloadFactory xmlns=\"http://ws.apache.org/ns/synapse\" media-type=\"xml\" " +
                "description=\"test\"><format key=\"conf:/repository/registry.xml\"/></payloadFactory>";
        testSerializeMediator(xml, true);
    }
}
