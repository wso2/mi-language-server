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

import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.transformation.JsonTransformFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.mediator.transformation.JsonTransformMediatorSerializer;
import org.eclipse.lemminx.synapse.serializer.mediator.MediatorSerializerTest;
import org.junit.jupiter.api.Test;

public class JsontransformMediatorSerializerTest extends MediatorSerializerTest {

    public JsontransformMediatorSerializerTest() {

        factory = new JsonTransformFactory();
        serializer = new JsonTransformMediatorSerializer();
    }

    @Test
    public void testJsontransformMediator() {

        String xml = "<jsontransform xmlns=\"http://ws.apache.org/ns/synapse\" schema=\"conf:tasks/tem.xml\" " +
                "description=\"test\"/>";
        testSerializeMediator(xml, true);
    }

    @Test
    public void testJsontransformMediatorWithProperty() {

        String xml = "<jsontransform xmlns=\"http://ws.apache.org/ns/synapse\" schema=\"conf:tasks/tem.xml\" " +
                "description=\"test\"><property name=\"prop1\" value=\"val1\"/><property name=\"prop2\" " +
                "value=\"val2\"/></jsontransform>";
        testSerializeMediator(xml, true);
    }
}
