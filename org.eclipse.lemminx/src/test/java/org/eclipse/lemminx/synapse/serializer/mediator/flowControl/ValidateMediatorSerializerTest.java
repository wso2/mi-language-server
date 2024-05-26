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

import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.core.ValidateFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.mediator.flowControl.ValidateMediatorSerializer;
import org.eclipse.lemminx.synapse.serializer.mediator.MediatorSerializerTest;
import org.junit.jupiter.api.Test;

public class ValidateMediatorSerializerTest extends MediatorSerializerTest {

    public ValidateMediatorSerializerTest() {

        factory = new ValidateFactory();
        serializer = new ValidateMediatorSerializer();

    }

    @Test
    public void testValidate() {

        String xml = "<validate xmlns=\"http://ws.apache.org/ns/synapse\" cache-schema=\"true\" " +
                "source=\"$ctx:source\" description=\"test\"><schema key=\"gov:datamapper/sample.xsd\"/><feature " +
                "name=\"feature1\" value=\"true\"/><feature name=\"feature2\" value=\"false\"/><resource key=\"gov:js" +
                ".json\" location=\"location\"/><on-fail><log separator=\",\" " +
                "description=\"test\"/></on-fail></validate>";
        testSerializeMediator(xml, true);
    }
}
