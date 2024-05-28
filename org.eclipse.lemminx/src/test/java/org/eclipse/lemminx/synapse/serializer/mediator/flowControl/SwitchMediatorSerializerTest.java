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

import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.filter.SwitchFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.mediator.flowControl.SwitchMediatorSerializer;
import org.eclipse.lemminx.synapse.serializer.mediator.MediatorSerializerTest;
import org.junit.jupiter.api.Test;

public class SwitchMediatorSerializerTest extends MediatorSerializerTest {

    public SwitchMediatorSerializerTest() {

        factory = new SwitchFactory();
        serializer = new SwitchMediatorSerializer();
    }

    @Test
    public void testWithoutChildren() {

        String xml = "<switch xmlns=\"http://ws.apache.org/ns/synapse\" source=\"//m0:getQuote/m0:request/m0:symbol\"" +
                " xmlns:m0=\"http://services.samples/xsd\"/>";
        testSerializeMediator(xml, true);
    }

    @Test
    public void testWithChildren() {

        String xml = "<switch xmlns=\"http://ws.apache.org/ns/synapse\" source=\"//m0:getQuote/m0:request/m0:symbol\"" +
                " xmlns:m0=\"http://services.samples/xsd\"><case regex=\"IBM\"><property name=\"symbol\" " +
                "value=\"Great stock - IBM\"/></case><case regex=\"MSFT\"><property name=\"symbol\" " +
                "expression=\"fn:concat('Normal Stock - ', //m1:getQuote/m1:request/m0:symbol)\" " +
                "xmlns:m1=\"http://services.samples1/xsd\"/></case><default><property name=\"symbol\" " +
                "expression=\"fn:concat('Normal Stock - ', //m0:getQuote/m0:request/m0:symbol)\"/></default></switch>";
        testSerializeMediator(xml, true);
    }

    @Test
    public void testWithEmptyCase() {

        String xml = "<switch xmlns=\"http://ws.apache.org/ns/synapse\" source=\"//m0:getQuote/m0:request/m0:symbol\"" +
                " xmlns:m0=\"http://services.samples/xsd\"><case regex=\"IBM\"/><default/></switch>";
        testSerializeMediator(xml, true);
    }
}
