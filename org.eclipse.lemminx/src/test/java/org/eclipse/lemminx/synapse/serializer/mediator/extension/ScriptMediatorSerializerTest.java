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

package org.eclipse.lemminx.synapse.serializer.mediator.extension;

import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.extension.ScriptFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.mediator.extension.ScriptMediatorSerializer;
import org.eclipse.lemminx.synapse.serializer.mediator.MediatorSerializerTest;
import org.junit.jupiter.api.Test;

public class ScriptMediatorSerializerTest extends MediatorSerializerTest {

    public ScriptMediatorSerializerTest() {

        factory = new ScriptFactory();
        serializer = new ScriptMediatorSerializer();
    }

    @Test
    public void testScriptWithRegistryKey() {

        String xml = "<script xmlns=\"http://ws.apache.org/ns/synapse\" language=\"js\" " +
                "key=\"conf:/repository/EI/transform.js\" function=\"transform\"/>";
        testSerializeMediator(xml, true);
    }

    @Test
    public void testScriptWithCDATAJS() {

        String xml1 = "<script xmlns=\"http://ws.apache.org/ns/synapse\" language=\"js\"><![CDATA[mc.getPayloadXML()." +
                ".symbol != \"IBM\";]]></script>";
        testSerializeMediator(xml1, true);

        String xml2 = "<script xmlns=\"http://ws.apache.org/ns/synapse\" language=\"js\"><![CDATA[\n" +
                "var wsse = new Namespace('http://docs.oasis-open" +
                ".org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd'); \n" +
                "var envelope = mc.getEnvelopeXML(); \n" +
                "var username = envelope..wsse::Username.toString(); \n" +
                "var password = envelope..wsse::Password.toString();   \n" +
                "mc.addHeader(false, <urn:AuthenticationInfo><urn:userName>{username}</urn:userName><urn:password" +
                ">{password}</urn:password></urn:AuthenticationInfo>); \n" +
                "]]></script>";
        testSerializeMediator(xml2, true);
    }

    @Test
    public void testScriptWithIncludes() {

        String xml = "<script xmlns=\"http://ws.apache.org/ns/synapse\" language=\"js\" key=\"stockquoteScript\" " +
                "function=\"transformRequest\"><include key=\"sampleScript\"/></script>";
        testSerializeMediator(xml, true);
    }

}
