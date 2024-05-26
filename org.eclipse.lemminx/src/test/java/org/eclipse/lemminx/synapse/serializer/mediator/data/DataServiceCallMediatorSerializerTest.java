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

package org.eclipse.lemminx.synapse.serializer.mediator.data;

import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.advanced.DataServiceFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.mediator.data.DataServiceCallMediatorSerializer;
import org.eclipse.lemminx.synapse.serializer.mediator.MediatorSerializerTest;
import org.junit.jupiter.api.Test;

public class DataServiceCallMediatorSerializerTest extends MediatorSerializerTest {

    public DataServiceCallMediatorSerializerTest() {

        factory = new DataServiceFactory();
        serializer = new DataServiceCallMediatorSerializer();
    }

    @Test
    public void testSimpleDataServiceCall() {

        String xml = "<dataServiceCall xmlns=\"http://ws.apache.org/ns/synapse\" " +
                "serviceName=\"DSSCallMediatorTest\"><source type=\"body\"/><target type=\"body\"/></dataServiceCall>";
        testSerializeMediator(xml, true);
    }

    @Test
    public void testDataServiceCall() {

        String xml = "<dataServiceCall xmlns=\"http://ws.apache.org/ns/synapse\" " +
                "serviceName=\"DSSCallMediatorTest\"><source type=\"inline\"/><operations " +
                "type=\"request-box\"><operation name=\"addEmployee\"><param name=\"employeeNumber\" " +
                "value=\"444\"/><param name=\"firstname\" value=\"Ellie\"/><param name=\"lastName\" " +
                "value=\"Dina\"/><param name=\"email\" value=\"dina@wso2.com\"/><param name=\"salary\" " +
                "value=\"4000\"/></operation><operation name=\"getEmployeeByNumber\"><param name=\"employeeNumber\" " +
                "value=\"444\"/></operation></operations><target type=\"body\"/></dataServiceCall>";
        testSerializeMediator(xml, true);
    }
}
