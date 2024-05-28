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

package org.eclipse.lemminx.synapse.serializer.endpoint;

import org.junit.jupiter.api.Test;

public class FailoverEndpointSerializerTest extends EndpointSerializerTest {

    @Test
    public void testSerializeFailoverEndpoint() {

        String xml = "<endpoint xmlns=\"http://ws.apache.org/ns/synapse\" name=\"failoverTest\">" +
                "<failover>" +
                "<endpoint name=\"endpoint_urn_uuid_6179155B57847314A656794419902014617670556\">" +
                "<address uri=\"http://localhost\">" +
                "<suspendOnFailure>" +
                "<initialDuration>-1</initialDuration>" +
                "<progressionFactor>1</progressionFactor>" +
                "</suspendOnFailure>" +
                "<markForSuspension>" +
                "<retriesBeforeSuspension>0</retriesBeforeSuspension>" +
                "</markForSuspension>" +
                "</address>" +
                "</endpoint>" +
                "<endpoint key=\"address\"/>" +
                "</failover>" +
                "<property name=\"property_name\" value=\"property_value\"/>" +
                "<description>Test</description>" +
                "</endpoint>";
        testSerializeEndpoint(xml);
    }
}
