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

package org.eclipse.lemminx.synapse.serializer.mediator.qos;

import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.advanced.CacheFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.mediator.qos.CacheMediatorSerializer;
import org.eclipse.lemminx.synapse.serializer.mediator.MediatorSerializerTest;
import org.junit.jupiter.api.Test;

public class CacheMediatorSerializerTest extends MediatorSerializerTest {

    public CacheMediatorSerializerTest() {

        factory = new CacheFactory();
        serializer = new CacheMediatorSerializer();
    }

    @Test
    public void testDefaultCacheMediator() {

        String xml = "<cache xmlns=\"http://ws.apache.org/ns/synapse\" collector=\"false\" timeout=\"120\" " +
                "maxMessageSize=\"2000\" description=\"test\"><onCacheHit/><protocol " +
                "type=\"HTTP\"><methods>*</methods><headersToExcludeInHash/><headersToIncludeInHash/><responseCodes>" +
                ".*</responseCodes><enableCacheControl>false</enableCacheControl><includeAgeHeader>false" +
                "</includeAgeHeader><hashGenerator>org.wso2.carbon.mediator.cache.digest" +
                ".HttpRequestHashGenerator</hashGenerator></protocol><implementation maxSize=\"1000\"/></cache>";
        testSerializeMediator(xml, true);
    }

    @Test
    public void test611CompatibleCacheMediator() {

        String xml = "<cache xmlns=\"http://ws.apache.org/ns/synapse\" collector=\"false\" id=\"id\" " +
                "scope=\"per-host\" hashGenerator=\"org.wso2.carbon.mediator.cache.digest.DOMHASHGenerator\" " +
                "timeout=\"120\" maxMessageSize=\"2000\" description=\"test\"><onCacheHit " +
                "sequence=\"testSequence\"/><implementation maxSize=\"1000\" type=\"memory\"/></cache>";
        testSerializeMediator(xml, true);
    }

    @Test
    public void testCollectorCacheMediator() {

        String xml = "<cache xmlns=\"http://ws.apache.org/ns/synapse\" collector=\"true\" description=\"test\"/>";
        testSerializeMediator(xml, true);
    }
}
