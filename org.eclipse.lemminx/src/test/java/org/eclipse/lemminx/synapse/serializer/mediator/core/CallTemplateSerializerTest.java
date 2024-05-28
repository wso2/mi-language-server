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

import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.core.CallTemplateFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.mediator.core.CallTemplateMediatorSerializer;
import org.eclipse.lemminx.synapse.serializer.mediator.MediatorSerializerTest;
import org.junit.jupiter.api.Test;

public class CallTemplateSerializerTest extends MediatorSerializerTest {

    public CallTemplateSerializerTest() {

        factory = new CallTemplateFactory();
        serializer = new CallTemplateMediatorSerializer();
    }

    @Test
    public void testCallTemplateMediator() {

        String xml = "<call-template xmlns=\"http://ws.apache.org/ns/synapse\" target=\"targetSeq\" " +
                "onError=\"errorSeq\" description=\"test\"/>";
        testSerializeMediator(xml, true);
    }

    @Test
    public void testCallTemplateMediatorWithParams() {

        String xml = "<call-template xmlns=\"http://ws.apache.org/ns/synapse\" target=\"targetSeq\" " +
                "onError=\"errorSeq\" description=\"test\"><with-param name=\"param1\" value=\"val1\"/><with-param " +
                "name=\"param2\" value=\"{$ctx:exp}\" xmlns:test=\"http://www.test.com/\"/></call-template>";
        testSerializeMediator(xml, true);
    }
}
