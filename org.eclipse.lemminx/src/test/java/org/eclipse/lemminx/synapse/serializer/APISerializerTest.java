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
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.APIFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.api.API;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.api.APISerializer;
import org.eclipse.lemminx.dom.DOMDocument;
import org.eclipse.lemminx.dom.DOMParser;
import org.eclipse.lemminx.uriresolver.URIResolverExtensionManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class APISerializerTest {

    @Test
    public void testSerializeAPI() {

        String xml = "<api xmlns=\"http://ws.apache.org/ns/synapse\" name=\"CalculatorAPI\" context=\"/calculate\" " +
                "statistics=\"enable\" trace=\"enable\">" +
                "<resource methods=\"POST\">" +
                "<inSequence/>" +
                "<outSequence/>" +
                "<faultSequence/>" +
                "</resource>" +
                "</api>";

        test(xml);

    }

    private void test(String xml) {

        TextDocument document = new TextDocument(xml, "test.xml");

        DOMDocument xmlDocument = DOMParser.getInstance().parse(document,
                new URIResolverExtensionManager());

        APIFactory factory = new APIFactory();
        API api = (API) factory.create(xmlDocument.getDocumentElement());
        String actual = APISerializer.serializeAPI(api);
        System.out.println(actual);
        assertEquals(xml, actual);
    }
}
