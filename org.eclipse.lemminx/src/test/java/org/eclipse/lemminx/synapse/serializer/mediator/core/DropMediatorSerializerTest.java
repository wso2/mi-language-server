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

import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.core.DropFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.mediator.core.DropMediatorSerializer;
import org.eclipse.lemminx.synapse.serializer.mediator.MediatorSerializerTest;
import org.junit.jupiter.api.Test;

public class DropMediatorSerializerTest extends MediatorSerializerTest {

    public DropMediatorSerializerTest() {

        factory = new DropFactory();
        serializer = new DropMediatorSerializer();
    }

    @Test
    public void testDropMediator() {

        String xml = "<drop xmlns=\"http://ws.apache.org/ns/synapse\"/>";
        testSerializeMediator(xml, true);
    }

    @Test
    public void testDropMediatorWithDescription() {

        String xml = "<drop xmlns=\"http://ws.apache.org/ns/synapse\" description=\"test\"/>";
        testSerializeMediator(xml, true);
    }
}
