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

import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.advanced.DBReportFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.mediator.data.DBReportMediatorSerializer;
import org.eclipse.lemminx.synapse.serializer.mediator.MediatorSerializerTest;
import org.junit.jupiter.api.Test;

public class DBReportMediatorSerializerTest extends MediatorSerializerTest {

    public DBReportMediatorSerializerTest() {

        factory = new DBReportFactory();
        serializer = new DBReportMediatorSerializer();
    }

    @Test
    public void testDBReportMediator() {

        String xml = "<dbreport xmlns=\"http://ws.apache.org/ns/synapse\"><connection><pool><driver>org.apache.derby" +
                ".jdbc.ClientDriver</driver><url>jdbc:derby://localhost:1527/esbdb;" +
                "create=false</url><user>esb</user><password>esb</password></pool></connection><statement><sql" +
                "><![CDATA[update company set price=? where name =?]]></sql><parameter type=\"DOUBLE\" " +
                "expression=\"//m0:return/m1:last/child::text()\" xmlns:m1=\"http://services.samples/xsd\" " +
                "xmlns:m0=\"http://services.samples\"/><parameter type=\"VARCHAR\" " +
                "expression=\"//m0:return/m1:symbol/child::text()\" xmlns:m1=\"http://services.samples/xsd\" " +
                "xmlns:m0=\"http://services.samples\"/></statement></dbreport>";
        testSerializeMediator(xml, true);
    }
}
