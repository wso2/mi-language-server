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

import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.core.PropertyFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.mediator.core.PropertyMediatorSerializer;
import org.eclipse.lemminx.synapse.serializer.mediator.MediatorSerializerTest;
import org.junit.jupiter.api.Test;

public class PropertyMediatorSerializerTest extends MediatorSerializerTest {

    public PropertyMediatorSerializerTest() {

        factory = new PropertyFactory();
        serializer = new PropertyMediatorSerializer();
    }

    @Test
    public void testPropertyMediator() {

        String xml = "<property xmlns=\"http://ws.apache.org/ns/synapse\" name=\"prop1\" value=\"value1\" " +
                "scope=\"default\" type=\"STRING\"/>";
        testSerializeMediator(xml, true);
    }

    @Test
    public void testPropertyMediatorWithRemoveAction() {

        String xml = "<property xmlns=\"http://ws.apache.org/ns/synapse\" name=\"prop1\" scope=\"default\" " +
                "action=\"remove\" description=\"test\"/>";
        testSerializeMediator(xml, true);
    }

    @Test
    public void testPropertyMediatorWithInlineValue() {

        String xml = "<property xmlns=\"http://ws.apache.org/ns/synapse\" name=\"prop1\" scope=\"default\" " +
                "description=\"test\"><value xmlns=\"\"/></property>";
        testSerializeMediator(xml, true);
    }

    @Test
    public void testPropertyMediatorWithExpression() {

        String xml = "<property xmlns=\"http://ws.apache.org/ns/synapse\" name=\"prop1\" expression=\"$ctx:test\" " +
                "xmlns:sample=\"http://www.test.com/\" scope=\"default\" type=\"STRING\" description=\"test\"/>";
        testSerializeMediator(xml, true);
    }

    @Test
    public void testPropertyMediatorWithPattern() {

        String xml = "<property xmlns=\"http://ws.apache.org/ns/synapse\" name=\"prop1\" expression=\"$ctx:test\" " +
                "xmlns:sample=\"http://www.test.com/\" scope=\"default\" type=\"STRING\" pattern=\".*\" group=\"0\" " +
                "description=\"test\"/>";
        testSerializeMediator(xml, true);
    }
}
