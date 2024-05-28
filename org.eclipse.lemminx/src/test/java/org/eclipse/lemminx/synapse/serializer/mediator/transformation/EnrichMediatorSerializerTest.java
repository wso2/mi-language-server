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

import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.transformation.EnrichFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.mediator.transformation.EnrichMediatorSerializer;
import org.eclipse.lemminx.synapse.serializer.mediator.MediatorSerializerTest;
import org.junit.jupiter.api.Test;

public class EnrichMediatorSerializerTest extends MediatorSerializerTest {

    public EnrichMediatorSerializerTest() {

        factory = new EnrichFactory();
        serializer = new EnrichMediatorSerializer();
    }

    @Test
    public void testEnrichMediatorWithCustomSource() {

        String xml = "<enrich xmlns=\"http://ws.apache.org/ns/synapse\" description=\"test\"><source clone=\"true\" " +
                "type=\"custom\" xpath=\"$ctx:test\"/><target action=\"replace\" type=\"body\"/></enrich>";
        testSerializeMediator(xml, true);
    }

    @Test
    public void testEnrichMediatorWithEnvelopeSource() {

        String xml = "<enrich xmlns=\"http://ws.apache.org/ns/synapse\" description=\"test\"><source clone=\"true\" " +
                "type=\"envelope\"/><target action=\"replace\" type=\"body\"/></enrich>";
        testSerializeMediator(xml, true);
    }

    @Test
    public void testEnrichMediatorWithPropertySource() {

        String xml = "<enrich xmlns=\"http://ws.apache.org/ns/synapse\" description=\"test\"><source clone=\"true\" " +
                "type=\"property\" property=\"test-property\"/><target action=\"replace\" type=\"body\"/></enrich>";
        testSerializeMediator(xml, true);
    }

    @Test
    public void testEnrichMediatorWithInlineSource() {

        String xml = "<enrich xmlns=\"http://ws.apache.org/ns/synapse\" description=\"test\"><source clone=\"true\" " +
                "type=\"inline\"><inline xmlns=\"\"/></source><target action=\"replace\" type=\"body\"/></enrich>";
        testSerializeMediator(xml, true);
    }

    @Test
    public void testEnrichMediatorWithCustomTarget() {

        String xml = "<enrich xmlns=\"http://ws.apache.org/ns/synapse\" description=\"test\"><source clone=\"true\" " +
                "type=\"body\"/><target action=\"replace\" xpath=\"$ctx:test\"/></enrich>";
        testSerializeMediator(xml, true);
    }

    @Test
    public void testEnrichMediatorWithEnvelopeTarget() {

        String xml = "<enrich xmlns=\"http://ws.apache.org/ns/synapse\" description=\"test\"><source clone=\"true\" " +
                "type=\"body\"/><target action=\"replace\" type=\"envelope\"/></enrich>";
        testSerializeMediator(xml, true);
    }

    @Test
    public void testEnrichMediatorWithPropertyTarget() {

        String xml = "<enrich xmlns=\"http://ws.apache.org/ns/synapse\" description=\"test\"><source clone=\"true\" " +
                "type=\"body\"/><target action=\"replace\" type=\"property\" property=\"test-property\"/></enrich>";
        testSerializeMediator(xml, true);
    }

    @Test
    public void testEnrichMediatorWithKeyTarget() {

        String xml = "<enrich xmlns=\"http://ws.apache.org/ns/synapse\" description=\"test\"><source clone=\"true\" " +
                "type=\"body\"/><target action=\"replace\" type=\"key\" xpath=\"$ctx:test\"/></enrich>";
        testSerializeMediator(xml, true);
    }

}
