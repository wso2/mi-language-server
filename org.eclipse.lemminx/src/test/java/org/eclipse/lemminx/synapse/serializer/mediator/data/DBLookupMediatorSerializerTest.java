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

import org.apache.axiom.om.OMElement;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.advanced.DBLookupFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.advanced.DbMediator.DBLookup;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.advanced.DbMediator.DbMediatorConnection;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.advanced.DbMediator.DbMediatorConnectionPool;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.advanced.KeyAttribute;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.mediator.data.DBLookupMediatorSerializer;
import org.eclipse.lemminx.synapse.serializer.mediator.MediatorSerializerTest;
import org.junit.jupiter.api.Test;

public class DBLookupMediatorSerializerTest extends MediatorSerializerTest {

    public DBLookupMediatorSerializerTest() {

        factory = new DBLookupFactory();
        serializer = new DBLookupMediatorSerializer();
    }

    @Test
    public void testDBLookupMediator() {

        String xml = "<dblookup xmlns=\"http://ws.apache.org/ns/synapse\"><connection><pool><driver>org.apache.derby" +
                ".jdbc.ClientDriver</driver><url>jdbc:derby://localhost:1527/esbdb;" +
                "create=false</url><user>esb</user><password>esb</password></pool></connection><statement><sql" +
                "><![CDATA[select * from company where name =?]]></sql><parameter type=\"VARCHAR\" " +
                "expression=\"//m0:getQuote/m0:request/m0:symbol\" xmlns:m0=\"http://services.samples/xsd\"/><result " +
                "column=\"id\" name=\"company_id\"/></statement></dblookup>";
        testSerializeMediator(xml, true);
    }

    @Test
    public void test() {

        DBLookup dbLookup = new DBLookup();
        dbLookup.setDescription("DBLookup Mediator");

        DbMediatorConnection connection = new DbMediatorConnection();
        DbMediatorConnectionPool pool = new DbMediatorConnectionPool();
        KeyAttribute driver = new KeyAttribute();
        driver.setValue("org.apache.derby.jdbc.ClientDriver");
        pool.setDriver(driver);
        KeyAttribute url = new KeyAttribute();
        url.setValue("jdbc:derby://localhost:1527/esbdb;create=false");
        pool.setUrl(url);
        KeyAttribute user = new KeyAttribute();
        user.setValue("esb");
        pool.setUser(user);

        connection.setPool(pool);
        dbLookup.setConnection(connection);

        OMElement omElement = serializer.serializeMediator(null, dbLookup);
        System.out.println(omElement.toString());
    }
}
