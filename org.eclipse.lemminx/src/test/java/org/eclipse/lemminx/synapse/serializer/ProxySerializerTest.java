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
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.ProxyFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.proxy.Proxy;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.ProxySerializer;
import org.eclipse.lemminx.dom.DOMDocument;
import org.eclipse.lemminx.dom.DOMParser;
import org.eclipse.lemminx.uriresolver.URIResolverExtensionManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProxySerializerTest {

    @Test
    public void testSerializeProxy() {

        String xml = "<proxy xmlns=\"http://ws.apache.org/ns/synapse\" name=\"SimpleStockQuoteService\" " +
                "transports=\"http https\" startOnLoad=\"true\">" +
                "<description>test</description>" +
                "<target>" +
                "<inSequence>" +
                "<switch source=\"get-property('Action')\">" +
                "<case regex=\"getQuote\">" +
                "<payloadFactory media-type=\"xml\">" +
                "<format>" +
                "<message xmlns=\"\">Action getQuote is not implemented</message>" +
                "</format>" +
                "</payloadFactory>" +
                "</case>" +
                "<case regex=\".*+\">" +
                "<payloadFactory media-type=\"xml\">" +
                "<format>" +
                "<message xmlns=\"\">Action not implemented</message>" +
                "</format>" +
                "</payloadFactory>" +
                "</case>" +
                "<default>" +
                "<log/>" +
                "</default>" +
                "</switch>" +
                "<respond/>" +
                "</inSequence>" +
                "<outSequence>" +
                "<log/>" +
                "</outSequence>" +
                "<faultSequence>" +
                "<log/>" +
                "</faultSequence>" +
                "</target>" +
                "<publishWSDL uri=\"file:/path/to/wsdl.wsdl\" preservePolicy=\"true\">" +
                "<wsdl:definitions xmlns:wsdl=\"http://schemas.xmlsoap.org/wsdl/\" xmlns:ns1=\"http://org.apache" +
                ".axis2/xsd\" xmlns:ns=\"http://c.b.a\" xmlns:wsaw=\"http://www.w3.org/2006/05/addressing/wsdl\" " +
                "xmlns:http=\"http://schemas.xmlsoap.org/wsdl/http/\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" " +
                "xmlns:soap=\"http://schemas.xmlsoap.org/wsdl/soap/\" xmlns:mime=\"http://schemas.xmlsoap" +
                ".org/wsdl/mime/\" xmlns:soap12=\"http://schemas.xmlsoap.org/wsdl/soap12/\" " +
                "targetNamespace=\"http://c.b.a\">" +
                "<wsdl:documentation>Calculator</wsdl:documentation>" +
                "<wsdl:types>" +
                "<xs:schema attributeFormDefault=\"qualified\" elementFormDefault=\"qualified\" " +
                "targetNamespace=\"http://c.b.a\">" +
                "<xs:element name=\"add\">" +
                "<xs:complexType>" +
                "<xs:sequence>" +
                "<xs:element minOccurs=\"0\" name=\"n1\" type=\"xs:int\"/>" +
                "<xs:element minOccurs=\"0\" name=\"n2\" type=\"xs:int\"/>" +
                "</xs:sequence>" +
                "</xs:complexType>" +
                "</xs:element>" +
                "<xs:element name=\"addResponse\">" +
                "<xs:complexType>" +
                "<xs:sequence>" +
                "<xs:element minOccurs=\"0\" name=\"return\" type=\"xs:int\"/>" +
                "</xs:sequence>" +
                "</xs:complexType>" +
                "</xs:element>" +
                "</xs:schema>" +
                "</wsdl:types>" +
                "<wsdl:message name=\"addRequest\">" +
                "<wsdl:part name=\"parameters\" element=\"ns:add\"/>" +
                "</wsdl:message>" +
                "<wsdl:message name=\"addResponse\">" +
                "<wsdl:part name=\"parameters\" element=\"ns:addResponse\"/>" +
                "</wsdl:message>" +
                "<wsdl:portType name=\"CalculatorPortType\"><wsdl:operation name=\"add\"><wsdl:input " +
                "message=\"ns:addRequest\" wsaw:Action=\"urn:add\"/><wsdl:output message=\"ns:addResponse\" " +
                "wsaw:Action=\"urn:addResponse\"/></wsdl:operation></wsdl:portType><wsdl:binding " +
                "name=\"CalculatorSoap11Binding\" type=\"ns:CalculatorPortType\"><soap:binding " +
                "transport=\"http://schemas.xmlsoap.org/soap/http\" style=\"document\"/><wsdl:operation " +
                "name=\"add\"><soap:operation soapAction=\"urn:add\" style=\"document\"/><wsdl:input><soap:body " +
                "use=\"literal\"/></wsdl:input><wsdl:output><soap:body " +
                "use=\"literal\"/></wsdl:output></wsdl:operation></wsdl:binding><wsdl:binding " +
                "name=\"CalculatorSoap12Binding\" type=\"ns:CalculatorPortType\"><soap12:binding " +
                "transport=\"http://schemas.xmlsoap.org/soap/http\" style=\"document\"/><wsdl:operation " +
                "name=\"add\"><soap12:operation soapAction=\"urn:add\" style=\"document\"/><wsdl:input><soap12:body " +
                "use=\"literal\"/></wsdl:input><wsdl:output><soap12:body " +
                "use=\"literal\"/></wsdl:output></wsdl:operation></wsdl:binding><wsdl:binding " +
                "name=\"CalculatorHttpBinding\" type=\"ns:CalculatorPortType\"><http:binding " +
                "verb=\"POST\"/><wsdl:operation name=\"add\"><http:operation " +
                "location=\"add\"/><wsdl:input><mime:content type=\"text/xml\" " +
                "part=\"parameters\"/></wsdl:input><wsdl:output><mime:content type=\"text/xml\" " +
                "part=\"parameters\"/></wsdl:output></wsdl:operation></wsdl:binding><wsdl:service " +
                "name=\"Calculator\"><wsdl:port name=\"CalculatorHttpsSoap11Endpoint\" " +
                "binding=\"ns:CalculatorSoap11Binding\"><soap:address location=\"https://156.56.179" +
                ".164:9443/services/Calculator.CalculatorHttpsSoap11Endpoint/\"/></wsdl:port><wsdl:port " +
                "name=\"CalculatorHttpSoap11Endpoint\" binding=\"ns:CalculatorSoap11Binding\"><soap:address " +
                "location=\"http://156.56.179.164:9763/services/Calculator" +
                ".CalculatorHttpSoap11Endpoint/\"/></wsdl:port><wsdl:port name=\"CalculatorHttpSoap12Endpoint\" " +
                "binding=\"ns:CalculatorSoap12Binding\"><soap12:address location=\"http://156.56.179" +
                ".164:9763/services/Calculator.CalculatorHttpSoap12Endpoint/\"/></wsdl:port><wsdl:port " +
                "name=\"CalculatorHttpsSoap12Endpoint\" binding=\"ns:CalculatorSoap12Binding\"><soap12:address " +
                "location=\"https://156.56.179.164:9443/services/Calculator" +
                ".CalculatorHttpsSoap12Endpoint/\"/></wsdl:port><wsdl:port name=\"CalculatorHttpsEndpoint\" " +
                "binding=\"ns:CalculatorHttpBinding\"><http:address location=\"https://156.56.179" +
                ".164:9443/services/Calculator.CalculatorHttpsEndpoint/\"/></wsdl:port><wsdl:port " +
                "name=\"CalculatorHttpEndpoint\" binding=\"ns:CalculatorHttpBinding\"><http:address " +
                "location=\"http://156.56.179.164:9763/services/Calculator" +
                ".CalculatorHttpEndpoint/\"/></wsdl:port></wsdl:service></wsdl:definitions></publishWSDL" +
                "><enableAddressing/><enableSec/><enableRM/><policy key=\"policy\"/><parameter " +
                "name=\"param1\"/><parameter name=\"param2\"/></proxy>";
        test(xml);
    }

    private void test(String xml) {

        TextDocument document = new TextDocument(xml, "test.xml");

        DOMDocument xmlDocument = DOMParser.getInstance().parse(document,
                new URIResolverExtensionManager());

        ProxyFactory factory = new ProxyFactory();
        Proxy proxy = (Proxy) factory.create(xmlDocument.getDocumentElement());
        String actual = ProxySerializer.serializeProxy(proxy);
        System.out.println(actual);
        assertEquals(xml, actual);
    }

}
