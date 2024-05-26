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

public class AddressEndpointSerializerTest extends EndpointSerializerTest {

    @Test
    public void testAddressEndpoint() {

        String xml = "<endpoint xmlns=\"http://ws.apache.org/ns/synapse\" name=\"address\">" +
                "<address statistics=\"enable\" trace=\"enable\" uri=\"http://www.test.com\">" +
                "<enableSec policy=\"policy\"/>" +
                "<enableAddressing version=\"final\"/>" +
                "<timeout>" +
                "<duration>10000</duration>" +
                "<responseAction>discard</responseAction>" +
                "</timeout>" +
                "<suspendOnFailure>" +
                "<errorCodes>401</errorCodes>" +
                "<initialDuration>100</initialDuration>" +
                "<progressionFactor>1.0</progressionFactor>" +
                "</suspendOnFailure>" +
                "<markForSuspension>" +
                "<errorCodes>403</errorCodes>" +
                "<retriesBeforeSuspension>10</retriesBeforeSuspension>" +
                "<retryDelay>1000</retryDelay>" +
                "</markForSuspension>" +
                "</address>" +
                "<property name=\"property_name\" value=\"property_value\"/>" +
                "<description>test</description>" +
                "</endpoint>";

        testSerializeEndpoint(xml);
    }

}
