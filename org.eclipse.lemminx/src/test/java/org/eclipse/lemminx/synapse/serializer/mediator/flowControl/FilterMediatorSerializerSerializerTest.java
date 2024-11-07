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

import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.filter.FilterFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.mediator.flowControl.FilterMediatorSerializer;
import org.eclipse.lemminx.synapse.serializer.mediator.MediatorSerializerTest;
import org.junit.jupiter.api.Test;

public class FilterMediatorSerializerSerializerTest extends MediatorSerializerTest {

    public FilterMediatorSerializerSerializerTest() {

        factory = new FilterFactory();
        serializer = new FilterMediatorSerializer();
    }

    @Test
    public void testWithoutChildren() {

        String xml = "<filter xmlns=\"http://ws.apache.org/ns/synapse\" source=\"get-property('Action')\" regex=\"" +
                ".*getBusNo\"/>";
        testSerializeMediator(xml, true);
    }

    @Test
    public void testWithChildren() {

        String xml = "<filter xmlns=\"http://ws.apache.org/ns/synapse\" source=\"get-property('Action')\" regex=\"" +
                ".*getBusNo\"><then><log level=\"custom\"><property name=\"service\" value=\"Bus Services is " +
                "called\"/></log></then><else><log level=\"custom\"><property name=\"service\" value=\"Bus Services " +
                "is called\"/></log></else></filter>";
        testSerializeMediator(xml, true);
    }
}
