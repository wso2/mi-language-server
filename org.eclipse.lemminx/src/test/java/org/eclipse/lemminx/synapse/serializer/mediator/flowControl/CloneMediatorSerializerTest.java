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

import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.advanced.CloneFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.mediator.flowControl.CloneMediatorSerializer;
import org.eclipse.lemminx.synapse.serializer.mediator.MediatorSerializerTest;
import org.junit.jupiter.api.Test;

public class CloneMediatorSerializerTest extends MediatorSerializerTest {

    public CloneMediatorSerializerTest() {

        factory = new CloneFactory();
        serializer = new CloneMediatorSerializer();
    }

    @Test
    public void testCloneMediator() {

        String xml = "<clone xmlns=\"http://ws.apache.org/ns/synapse\"/>";
        testSerializeMediator(xml, true);
    }

    @Test
    public void testCloneMediatorWithChildren() {

        String xml = "<clone xmlns=\"http://ws.apache.org/ns/synapse\"><target><sequence><log " +
                "level=\"custom\"><property name=\"service\" value=\"Bus Services is " +
                "called\"/></log></sequence></target></clone>";
        testSerializeMediator(xml, true);
    }

    @Test
    public void testCloneMediatorWithMultipleTargets() {

        String xml = "<clone xmlns=\"http://ws.apache.org/ns/synapse\" id=\"test\" continueParent=\"true\" " +
                "sequential=\"true\" description=\"test\"><target to=\"to\" soapAction=\"action\" " +
                "sequence=\"testSequence\"/><target to=\"to\" soapAction=\"action\"><sequence><log " +
                "description=\"test\"/></sequence></target></clone>";
        testSerializeMediator(xml, true);
    }
}
