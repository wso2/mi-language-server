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

package org.eclipse.lemminx.synapse.serializer.mediator.core;

import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.core.CallFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.InvalidConfigurationException;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.mediator.core.CallMediatorSerializer;
import org.eclipse.lemminx.synapse.serializer.mediator.MediatorSerializerTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class CallMediatorSerializerTest extends MediatorSerializerTest {

    public CallMediatorSerializerTest() {

        factory = new CallFactory();
        serializer = new CallMediatorSerializer();
    }

    @Test
    public void testNoSource() {

        String xml = "<call xmlns=\"http://ws.apache.org/ns/synapse\" blocking=\"true\" " +
                "description=\"test\"><endpoint key=\"httpTest\"/><target type=\"body\"/></call>";
        testSerializeMediator(xml, true);
    }

    @Test
    public void testNoTarget() {

        String xml = "<call xmlns=\"http://ws.apache.org/ns/synapse\" blocking=\"true\" " +
                "description=\"test\"><endpoint key=\"httpTest\"/><source type=\"body\"/></call>";
        testSerializeMediator(xml, true);
    }

    @Test
    public void testNoEndpoint() {

        String xml = "<call xmlns=\"http://ws.apache.org/ns/synapse\" blocking=\"true\" description=\"test\"><source " +
                "type=\"body\"/><target type=\"body\"/></call>";
        assertThrows(InvalidConfigurationException.class, () -> {
            testSerializeMediator(xml, true);
        });
    }

    @Test
    public void testBodySource() {

        String xml = "<call xmlns=\"http://ws.apache.org/ns/synapse\" blocking=\"true\" " +
                "description=\"test\"><endpoint key=\"httpTest\"/><source type=\"body\"/><target " +
                "type=\"body\"/></call>";
        testSerializeMediator(xml, true);
    }

    @Test
    public void testCustomSource() {

        String xml = "<call xmlns=\"http://ws.apache.org/ns/synapse\" blocking=\"true\" " +
                "description=\"test\"><endpoint key=\"httpTest\"/><source type=\"custom\" " +
                "contentType=\"json\">test-source</source><target type=\"property\">test-target</target></call>";
        testSerializeMediator(xml, true);
    }

    @Test
    public void testPropertySource() {

        String xml = "<call xmlns=\"http://ws.apache.org/ns/synapse\" description=\"test\"><endpoint " +
                "key=\"httpTest\"/><source type=\"property\" contentType=\"test-type\">test-property</source><target " +
                "type=\"body\"/></call>";
        testSerializeMediator(xml, true);
    }

    @Test
    public void testInlineSource() {

        String xml = "<call xmlns=\"http://ws.apache.org/ns/synapse\" blocking=\"true\" " +
                "description=\"test\"><endpoint key=\"httpTest\"/><source type=\"inline\" " +
                "contentType=\"test-type\"><inline xmlns=\"\"/></source><target type=\"body\"/></call>";
        testSerializeMediator(xml, true);
    }

    @Test
    public void testPropertyTarget() {

        String xml = "<call xmlns=\"http://ws.apache.org/ns/synapse\" blocking=\"true\" " +
                "description=\"test\"><endpoint key=\"httpTest\"/><source type=\"body\"/><target " +
                "type=\"property\">test-property</target></call>";
        testSerializeMediator(xml, true);
    }
}
