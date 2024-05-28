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

package org.eclipse.lemminx.synapse.serializer.mediator.extension;

import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.extension.ClassFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.mediator.extension.ClassMediatorSerializer;
import org.eclipse.lemminx.synapse.serializer.mediator.MediatorSerializerTest;
import org.junit.jupiter.api.Test;

public class ClassMediatorSerializerTest extends MediatorSerializerTest {

    public ClassMediatorSerializerTest() {

        factory = new ClassFactory();
        serializer = new ClassMediatorSerializer();
    }

    @Test
    public void testClassMediator() {

        String xml = "<class xmlns=\"http://ws.apache.org/ns/synapse\" name=\"testClass\" " +
                "description=\"test\"><property name=\"arg1\" value=\"value1\"/><property name=\"arg2\" " +
                "expression=\"$ctx:exp\" xmlns:m0=\"http://services.samples/xsd\"/></class>";
        testSerializeMediator(xml, true);
    }

    @Test
    public void testClassMediatorWithoutProperties() {

        String xml = "<class xmlns=\"http://ws.apache.org/ns/synapse\" name=\"testClass\" description=\"test\"/>";
        testSerializeMediator(xml, true);
    }
}
