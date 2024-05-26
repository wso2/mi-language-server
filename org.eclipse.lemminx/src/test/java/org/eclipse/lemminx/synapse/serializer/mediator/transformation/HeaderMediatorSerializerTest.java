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

import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.core.HeaderFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.mediator.transformation.HeaderMediatorSerializer;
import org.eclipse.lemminx.synapse.serializer.mediator.MediatorSerializerTest;
import org.junit.jupiter.api.Test;

public class HeaderMediatorSerializerTest extends MediatorSerializerTest {

    public HeaderMediatorSerializerTest() {

        factory = new HeaderFactory();
        serializer = new HeaderMediatorSerializer();
    }

    @Test
    public void testHeaderMediatorWithValue() {

        String xml = "<header xmlns=\"http://ws.apache.org/ns/synapse\" name=\"To\" action=\"set\" scope=\"default\" " +
                "value=\"test\" description=\"test\"/>";
        testSerializeMediator(xml, true);
    }

    @Test
    public void testHeaderMediatorWithExpression() {

        String xml = "<header xmlns=\"http://ws.apache.org/ns/synapse\" name=\"To\" xmlns:m0=\"http://services" +
                ".samples/xsd\" action=\"set\" scope=\"default\" expression=\"//m0:getQuote/m0:request/m0:symbol\" " +
                "description=\"test\"/>";
        testSerializeMediator(xml, true);
    }

    @Test
    public void testHeaderMediatorWithInlineValue() {

        String xml = "<header xmlns=\"http://ws.apache.org/ns/synapse\" name=\"To\" action=\"set\" scope=\"default\" " +
                "description=\"test\"><inline xmlns=\"\"/></header>";
        testSerializeMediator(xml, true);
    }

    @Test
    public void testHeaderMediatorWithRemoveAction() {

        String xml = "<header xmlns=\"http://ws.apache.org/ns/synapse\" name=\"To\" action=\"remove\" " +
                "scope=\"default\" description=\"afaf\"/>";
        testSerializeMediator(xml, true);
    }
}
