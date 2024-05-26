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

import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.other.EntitlementFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.mediator.qos.EntitlementMediatorSerializer;
import org.eclipse.lemminx.synapse.serializer.mediator.MediatorSerializerTest;
import org.junit.jupiter.api.Test;

public class EntitlementMediatorSerializerTest extends MediatorSerializerTest {

    public EntitlementMediatorSerializerTest() {

        factory = new EntitlementFactory();
        serializer = new EntitlementMediatorSerializer();
    }

    @Test
    public void testEntitlementWithAnonymousSequence() {

        String xml = "<entitlementService xmlns=\"http://ws.apache.org/ns/synapse\" remoteServiceUrl=\"serverUrl\" " +
                "remoteServiceUserName=\"username\" remoteServicePassword=\"password\" callbackClass=\"org.wso2" +
                ".carbon.identity.entitlement.mediator.callback.UTEntitlementCallbackHandler\" client=\"basicAuth\" " +
                "description=\"test\"><onReject><log description=\"onAccept Test\"/></onReject><onAccept><log " +
                "description=\"onReject Test\"/></onAccept><advice><log description=\"advice " +
                "Test\"/></advice><obligations><log description=\"obligations " +
                "Test\"/></obligations></entitlementService>";
        testSerializeMediator(xml, true);
    }
}
