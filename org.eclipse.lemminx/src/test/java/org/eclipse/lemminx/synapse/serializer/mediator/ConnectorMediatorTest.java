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

package org.eclipse.lemminx.synapse.serializer.mediator;

import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.ConnectorFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.mediator.ConnectorSerializer;
import org.junit.jupiter.api.Test;

public class ConnectorMediatorTest extends MediatorSerializerTest {

    public ConnectorMediatorTest() {

        factory = new ConnectorFactory();
        serializer = new ConnectorSerializer();
    }

    @Test
    public void testSimpleConnectorMediator() {

        String xml = "<email.list xmlns=\"http://ws.apache.org/ns/synapse\" " +
                "configKey=\"EMAIL_CONNECTION_1\"><deleteAfterRetrieve>false</deleteAfterRetrieve><receivedSince " +
                "xmlns:test=\"http://www.test.com\">{test}</receivedSince><receivedUntil>test</receivedUntil><offset" +
                ">test</offset><limit>test</limit><folder>test</folder></email.list>";

        testSerializeMediator(xml, true);
    }
}
