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

import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.transformation.XsltFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.mediator.transformation.XsltMediatorSerializer;
import org.eclipse.lemminx.synapse.serializer.mediator.MediatorSerializerTest;
import org.junit.jupiter.api.Test;

public class XsltMediatorSerializerTest extends MediatorSerializerTest {

    public XsltMediatorSerializerTest() {

        factory = new XsltFactory();
        serializer = new XsltMediatorSerializer();
    }

    @Test
    public void testXsltMediator() {

        String xml = "<xslt xmlns=\"http://ws.apache.org/ns/synapse\" key=\"asdfasf\" description=\"test\"/>";
        testSerializeMediator(xml, true);
    }

    @Test
    public void testXsltMediatorWithProperty() {

        String xml = "<xslt xmlns=\"http://ws.apache.org/ns/synapse\" key=\"gov:wff.xslt\" source=\"test\" " +
                "description=\"test\"><property name=\"prop1\" value=\"val1\"/><property name=\"prop2\" " +
                "expression=\"exp\"/></xslt>";
        testSerializeMediator(xml, true);
    }

    @Test
    public void testXsltMediatorWithFeature() {

        String xml = "<xslt xmlns=\"http://ws.apache.org/ns/synapse\" key=\"gov:wff.xslt\" source=\"test\" " +
                "description=\"test\"><feature name=\"feature\" value=\"true\"/></xslt>";
        testSerializeMediator(xml, true);
    }

    @Test
    public void testXsltMediatorWithResource() {

        String xml = "<xslt xmlns=\"http://ws.apache.org/ns/synapse\" key=\"gov:wff.xslt\" source=\"test\" " +
                "description=\"test\"><resource location=\"location\" key=\"key\"/></xslt>";
        testSerializeMediator(xml, true);
    }

}
