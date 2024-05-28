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

import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.core.LogFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.InvalidConfigurationException;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.mediator.core.LogMediatorSerializer;
import org.eclipse.lemminx.synapse.serializer.mediator.MediatorSerializerTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class LogMediatorSerializerTest extends MediatorSerializerTest {

    public LogMediatorSerializerTest() {

        factory = new LogFactory();
        serializer = new LogMediatorSerializer();
    }

    @Test
    public void testSimpleLogMediator() {

        String xml = "<log xmlns=\"http://ws.apache.org/ns/synapse\" level=\"full\"/>";
        testSerializeMediator(xml, true);
    }

    @Test
    public void testLogMediatorWithInvalidInfo() {

        String xml = "<log xmlns=\"http://ws.apache.org/ns/synapse\" level=\"full\" info=\"invalid\"/>";

        testSerializeMediator(xml, false);
    }

    @Test
    public void testLogMediatorWithInvalidLevel() {

        String xml = "<log xmlns=\"http://ws.apache.org/ns/synapse\" level=\"invalid\"/>";

        testSerializeMediator(xml, false);
    }

    @Test
    public void testLogMediatorWithProperties() {

        String xml = "<log xmlns=\"http://ws.apache.org/ns/synapse\" level=\"full\"><property name=\"prop1\" " +
                "value=\"val1\"/></log>";

        testSerializeMediator(xml, true);
    }

    @Test
    public void testLogMediatorWithPropertyWithNamespace() {

        String xml = "<log xmlns=\"http://ws.apache.org/ns/synapse\" level=\"full\"><property name=\"prop1\" " +
                "expression=\"$ctx:test\" xmlns:sample1=\"http://www.test1.com/\" xmlns:sample2=\"http://www.test2" +
                ".com/\"/></log>";

        testSerializeMediator(xml, true);
    }

    @Test
    public void testLogMediatorWithNoPropertyName() {

        String xml = "<log xmlns=\"http://ws.apache.org/ns/synapse\" level=\"full\"><property value=\"val1\"/></log>";

        assertThrows(InvalidConfigurationException.class, () -> {
            testSerializeMediator(xml, false);
        });
    }

    @Test
    public void testLogMediatorWithNoPropertyValue() {

        String xml = "<log xmlns=\"http://ws.apache.org/ns/synapse\" level=\"full\"><property name=\"prop1\"/></log>";
        assertThrows(InvalidConfigurationException.class, () -> {
            testSerializeMediator(xml, false);
        });
    }
}
