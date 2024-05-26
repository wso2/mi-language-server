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

package org.eclipse.lemminx.synapse.serializer.mediator.flowControl;

import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.eip.AggregateFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.mediator.flowControl.AggregateMediatorSerializer;
import org.eclipse.lemminx.synapse.serializer.mediator.MediatorSerializerTest;
import org.junit.jupiter.api.Test;

public class AggregateMediatorSerializerTest extends MediatorSerializerTest {

    public AggregateMediatorSerializerTest() {

        factory = new AggregateFactory();
        serializer = new AggregateMediatorSerializer();
    }

    @Test
    public void testAggregateMediator() {

        String xml = "<aggregate xmlns=\"http://ws.apache.org/ns/synapse\" id=\"id\"><correlateOn " +
                "expression=\"$ctx:test\" xmlns:test=\"http://www.test.com/\"/><completeCondition " +
                "timeout=\"0\"><messageCount min=\"-1\" max=\"-1\"/></completeCondition><onComplete " +
                "aggregateElementType=\"root\" enclosingElementProperty=\"test\" expression=\"$ctx:test\" " +
                "xmlns:test=\"http://www.test.com/\"/></aggregate>";
        testSerializeMediator(xml, true);
    }

    @Test
    public void testAggregateMediatorWithChildMediators() {

        String xml = "<aggregate xmlns=\"http://ws.apache.org/ns/synapse\" id=\"id\"><correlateOn " +
                "expression=\"$ctx:test\" xmlns:test=\"http://www.test.com/\"/><completeCondition " +
                "timeout=\"0\"><messageCount min=\"-1\" max=\"-1\"/></completeCondition><onComplete " +
                "aggregateElementType=\"root\" enclosingElementProperty=\"test\" expression=\"$ctx:test\" " +
                "xmlns:test=\"http://www.test.com/\"><log level=\"full\" category=\"TRACE\" " +
                "description=\"test\"/></onComplete></aggregate>";
        testSerializeMediator(xml, true);
    }

    @Test
    public void testAggregateMediatorWithoutSelfClosingTag() {

        String xml = "<aggregate xmlns=\"http://ws.apache.org/ns/synapse\" id=\"id\"/>";
        testSerializeMediator(xml, true);
    }
}
