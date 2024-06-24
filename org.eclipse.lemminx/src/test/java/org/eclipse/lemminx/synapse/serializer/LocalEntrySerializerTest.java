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

package org.eclipse.lemminx.synapse.serializer;

import org.eclipse.lemminx.commons.TextDocument;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.LocalEntryFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.LocalEntry;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.LocalEntrySerializer;
import org.eclipse.lemminx.dom.DOMDocument;
import org.eclipse.lemminx.dom.DOMParser;
import org.eclipse.lemminx.uriresolver.URIResolverExtensionManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LocalEntrySerializerTest {

    @Test
    public void testSerializeLocalEntry() {

        String xml = "<localEntry xmlns=\"http://ws.apache.org/ns/synapse\" " +
                "key=\"SALESFORCESOAP_CONNECTION_1\"><salesforce.init xmlns=\"\">\n" +
                "<connectionType>init</connectionType>\n" +
                "<password>Demo2024#S6hDusAEUMbD3yhMgvFzMwY2</password>\n" +
                "<loginUrl>https://sample-dev-ed.my.salesforce.com/services/Soap/u/39.0</loginUrl>\n" +
                "<name>SALESFORCESOAP_CONNECTION_1</name>\n" +
                "<username>sample.user@salesforce.com</username>\n" +
                "</salesforce.init></localEntry>";

        test(xml);
    }

    private void test(String xml) {

        TextDocument document = new TextDocument(xml, "test.xml");

        DOMDocument xmlDocument = DOMParser.getInstance().parse(document,
                new URIResolverExtensionManager());

        LocalEntryFactory factory = new LocalEntryFactory();
        LocalEntry localEntry = (LocalEntry) factory.create(xmlDocument.getDocumentElement());
        String actual = LocalEntrySerializer.serializeLocalEntry(localEntry);
        System.out.println(actual);
        assertEquals(xml, actual);
    }
}
