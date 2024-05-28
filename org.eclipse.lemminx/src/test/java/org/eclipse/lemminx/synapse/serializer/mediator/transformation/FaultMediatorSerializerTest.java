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

import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.transformation.FaultFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.mediator.transformation.FaultMediatorSerializer;
import org.eclipse.lemminx.synapse.serializer.mediator.MediatorSerializerTest;
import org.junit.jupiter.api.Test;

public class FaultMediatorSerializerTest extends MediatorSerializerTest {

    public FaultMediatorSerializerTest() {

        factory = new FaultFactory();
        serializer = new FaultMediatorSerializer();
    }

    @Test
    public void testSOAP11FaultMediator() {

        String xml = "<makefault xmlns=\"http://ws.apache.org/ns/synapse\" version=\"soap11\" response=\"true\" " +
                "description=\"description\"><code value=\"soap11Env:VersionMismatch\" " +
                "xmlns:soap11Env=\"http://schemas.xmlsoap.org/soap/envelope/\"/><reason " +
                "expression=\"reason\"/><role>actor</role><detail>detail</detail></makefault>";
        testSerializeMediator(xml, true);
    }

    @Test
    public void testSOAP12FaultMediator() {

        String xml = "<makefault xmlns=\"http://ws.apache.org/ns/synapse\" version=\"soap12\" response=\"true\" " +
                "description=\"description\"><code value=\"soap12Env:\" xmlns:soap12Env=\"http://schemas.xmlsoap" +
                ".org/soap/envelope/\"/><reason expression=\"reason\"/><node>node</node><detail>detail</detail" +
                "></makefault>";
        testSerializeMediator(xml, true);
    }
}
