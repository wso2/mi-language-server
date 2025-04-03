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

package org.eclipse.lemminx.synapse.resource.finder;

import org.eclipse.lemminx.customservice.synapse.resourceFinder.AbstractResourceFinder;
import org.eclipse.lemminx.customservice.synapse.resourceFinder.ResourceFinderFactory;
import org.eclipse.lemminx.customservice.synapse.resourceFinder.pojo.RequestedResource;
import org.eclipse.lemminx.customservice.synapse.resourceFinder.pojo.Resource;
import org.eclipse.lemminx.customservice.synapse.resourceFinder.pojo.ResourceResponse;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ResourceFinderTest {

    private static final String PROJECT_PATH = "/synapse/resource.finder/test_project";
    private AbstractResourceFinder resourceFinder;
    private String projectPath;

    public ResourceFinderTest() {

        this.resourceFinder = ResourceFinderFactory.getResourceFinder(false);
        String path = ResourceFinderTest.class.getResource(PROJECT_PATH).getPath();
        projectPath = new File(path).getAbsolutePath();
    }

    @Test
    public void testApiResource() {

        ResourceResponse apiResources = resourceFinder.getAvailableResources(projectPath, Either.forLeft("api"));

        assertEquals(1, apiResources.getResources().size());
        assertEquals("testApi", apiResources.getResources().get(0).getName());
    }

    @Test
    public void testSequenceResource() {

        ResourceResponse sequenceResources = resourceFinder.getAvailableResources(projectPath, Either.forLeft(
                "sequence"));

        assertEquals(1, sequenceResources.getResources().size());
        assertEquals(1, sequenceResources.getRegistryResources().size());

        String[] expectedSequenceNames = {"testSequence1"};
        assertEqualResourceNames(expectedSequenceNames, sequenceResources.getResources());
        assertEqualResourceNames(expectedSequenceNames, sequenceResources.getRegistryResources());
    }

    @Test
    public void testEndpointResource() {

        ResourceResponse endpointResources = resourceFinder.getAvailableResources(projectPath, Either.forLeft(
                "endpoint"));

        assertEquals(2, endpointResources.getResources().size());
        assertEquals(2, endpointResources.getRegistryResources().size());

        String[] expectedEndpointNames = {"testEndpoint1", "testEndpoint2"};
        assertEqualResourceNames(expectedEndpointNames, endpointResources.getResources());
        assertEqualResourceNames(expectedEndpointNames, endpointResources.getRegistryResources());
    }

    @Test
    public void testProxyResource() {

        ResourceResponse proxyResources = resourceFinder.getAvailableResources(projectPath, Either.forLeft(
                "proxyService"));

        assertEquals(1, proxyResources.getResources().size());

        assertEquals("testProxy1", proxyResources.getResources().get(0).getName());
    }

    @Test
    public void testMessageProcessorResource() {

        ResourceResponse messageProcessorResources = resourceFinder.getAvailableResources(projectPath,
                Either.forLeft("messageProcessor"));

        assertEquals(1, messageProcessorResources.getResources().size());
        assertEquals("testMessageProcessor", messageProcessorResources.getResources().get(0).getName());
    }

    @Test
    public void testMessageStoreResource() {

        ResourceResponse messageStoreResources = resourceFinder.getAvailableResources(projectPath, Either.forLeft(
                "messageStore"));

        assertEquals(1, messageStoreResources.getResources().size());
        assertEquals("testMessageStore", messageStoreResources.getResources().get(0).getName());
    }

    @Test
    public void testSequenceTemplateResource() {

        ResourceResponse sequenceTemplateResources = resourceFinder.getAvailableResources(projectPath,
                Either.forLeft("sequenceTemplate"));

        assertEquals(1, sequenceTemplateResources.getResources().size());
        assertEquals("testSequenceTemplate", sequenceTemplateResources.getResources().get(0).getName());
    }

    @Test
    public void testEndpointTemplateResource() {

        ResourceResponse endpointTemplateResources = resourceFinder.getAvailableResources(projectPath,
                Either.forLeft("endpointTemplate"));

        assertEquals(1, endpointTemplateResources.getResources().size());
        assertEquals("testEndpointTemplate", endpointTemplateResources.getResources().get(0).getName());
    }

    @Test
    public void testTaskResource() {

        ResourceResponse taskResources = resourceFinder.getAvailableResources(projectPath, Either.forLeft("task"));

        assertEquals(1, taskResources.getResources().size());
        assertEquals("testTask", taskResources.getResources().get(0).getName());
    }

    @Test
    public void testLocalEntryResource() {

        ResourceResponse localEntryResources = resourceFinder.getAvailableResources(projectPath, Either.forLeft(
                "localEntry"));

        List<String> localEntries = Arrays.asList(localEntryResources.getResources().get(0).getName(),
                localEntryResources.getResources().get(1).getName());
        assertEquals(2, localEntryResources.getResources().size());
        assertTrue(localEntries.contains("testLocalEntry") && localEntries.contains("HttpsCon"));
    }

      // TODO: Need uncomment this test case after implementing handling of dmc files
//    @Test
//    public void testDataMapperResource() {
//
//        ResourceResponse dataMapperResources = resourceFinder.getAvailableResources(projectPath, Either.forLeft(
//                "dataMapper"));
//
//        assertEquals(1, dataMapperResources.getRegistryResources().size());
//        assertEquals("sample.dmc", dataMapperResources.getRegistryResources().get(0).getName());
//    }


    @Test
    public void testJSResource() {

        ResourceResponse jsResources = resourceFinder.getAvailableResources(projectPath, Either.forLeft("js"));

        assertEquals(1, jsResources.getRegistryResources().size());
        assertEquals("test.js", jsResources.getRegistryResources().get(0).getName());
    }

    @Test
    public void testJSONResource() {

        ResourceResponse jsonResources = resourceFinder.getAvailableResources(projectPath, Either.forLeft("json"));

        assertEquals(2, jsonResources.getRegistryResources().size());

        String[] expectedJsonNames = {"swagger.json", "test.json"};
        assertEqualResourceNames(expectedJsonNames, jsonResources.getRegistryResources());
    }

    @Test
    public void testSmooksConfigResource() {

        ResourceResponse smooksConfigResources = resourceFinder.getAvailableResources(projectPath, Either.forLeft(
                "smooksConfig"));

        assertEquals(1, smooksConfigResources.getRegistryResources().size());
        assertEquals("test_smooks_config.xml", smooksConfigResources.getRegistryResources().get(0).getName());
    }

    @Test
    public void testWSDLResource() {

        ResourceResponse wsdlResources = resourceFinder.getAvailableResources(projectPath, Either.forLeft("wsdl"));

        assertEquals(1, wsdlResources.getRegistryResources().size());
        assertEquals("wsdlfile.wsdl", wsdlResources.getRegistryResources().get(0).getName());
    }

    @Test
    public void testWSPolicyResource() {

        ResourceResponse wsPolicyResources = resourceFinder.getAvailableResources(projectPath, Either.forLeft(
                "ws_policy"));

        assertEquals(1, wsPolicyResources.getRegistryResources().size());
        assertEquals("ws_policy.xml", wsPolicyResources.getRegistryResources().get(0).getName());
    }

    @Test
    public void testXSDResource() {

        ResourceResponse xsdResources = resourceFinder.getAvailableResources(projectPath, Either.forLeft("xsd"));

        assertEquals(1, xsdResources.getRegistryResources().size());
        assertEquals("sample.xsd", xsdResources.getRegistryResources().get(0).getName());
    }

    @Test
    public void testXSLResource() {

        ResourceResponse xslResources = resourceFinder.getAvailableResources(projectPath, Either.forLeft("xsl"));

        assertEquals(1, xslResources.getRegistryResources().size());
        assertEquals("sample.xsl", xslResources.getRegistryResources().get(0).getName());
    }

    @Test
    public void testXSLTResource() {

        ResourceResponse xsltResources = resourceFinder.getAvailableResources(projectPath, Either.forLeft("xslt"));

        assertEquals(1, xsltResources.getRegistryResources().size());
        assertEquals("sample.xslt", xsltResources.getRegistryResources().get(0).getName());
    }

    @Test
    public void testYAMLResource() {

        ResourceResponse yamlResources = resourceFinder.getAvailableResources(projectPath, Either.forLeft("yaml"));

        assertEquals(2, yamlResources.getRegistryResources().size());

        String[] expectedYamlNames = {"swagger.yaml", "sample.yaml"};
        assertEqualResourceNames(expectedYamlNames, yamlResources.getRegistryResources());
    }

    @Test
    public void testRegistryResource() {

        ResourceResponse registryResources = resourceFinder.getAvailableResources(projectPath, Either.forLeft(
                "registry"));

        assertEquals(16, registryResources.getRegistryResources().size());
        assertTrue(registryResources.getResources().isEmpty());
    }

    @Test
    public void testSwaggerResource() {

        ResourceResponse swaggerResources = resourceFinder.getAvailableResources(projectPath, Either.forLeft("swagger"
        ));

        assertEquals(2, swaggerResources.getRegistryResources().size());

        String[] expectedSwaggerNames = {"swagger.yaml", "swagger.json"};
        assertEqualResourceNames(expectedSwaggerNames, swaggerResources.getRegistryResources());
    }

    @Test
    public void testSchemaResource() {

        ResourceResponse schemaResources = resourceFinder.getAvailableResources(projectPath, Either.forLeft("schema"));

        assertEquals(3, schemaResources.getRegistryResources().size());

        String[] expectedSchemaNames = {"swagger.json", "test.json", "sample.xsd"};
        assertEqualResourceNames(expectedSchemaNames, schemaResources.getRegistryResources());
    }

    @Test
    public void testRequestMultipleArtifactResource() {

        RequestedResource requestedResource1 = new RequestedResource("sequence", true);
        RequestedResource requestedResource2 = new RequestedResource("endpoint", true);

        ResourceResponse multipleResources = resourceFinder.getAvailableResources(projectPath,
                Either.forRight(List.of(requestedResource1, requestedResource2)));

        assertEquals(3, multipleResources.getResources().size());
        assertEquals(3, multipleResources.getRegistryResources().size());

        String[] expectedResourceNames = {"testSequence1", "testEndpoint1", "testEndpoint2"};
        assertEqualResourceNames(expectedResourceNames, multipleResources.getResources());
        assertEqualResourceNames(expectedResourceNames, multipleResources.getRegistryResources());
    }

    @Test
    public void testRequestMultipleArtifactResourceWithoutRegistry() {

        RequestedResource requestedResource1 = new RequestedResource("sequence", false);
        RequestedResource requestedResource2 = new RequestedResource("endpoint", false);

        ResourceResponse multipleResources = resourceFinder.getAvailableResources(projectPath,
                Either.forRight(List.of(requestedResource1, requestedResource2)));

        assertEquals(3, multipleResources.getResources().size());
        assertTrue(multipleResources.getRegistryResources().isEmpty());

        String[] expectedResourceNames = {"testSequence1", "testEndpoint1", "testEndpoint2"};
        assertEqualResourceNames(expectedResourceNames, multipleResources.getResources());
    }

    @Test
    public void testRequestMultipleRegistryResource() {

        RequestedResource requestedResource1 = new RequestedResource("json", true);
        RequestedResource requestedResource2 = new RequestedResource("js", true);

        ResourceResponse multipleResources = resourceFinder.getAvailableResources(projectPath,
                Either.forRight(List.of(requestedResource1, requestedResource2)));

        assertTrue(multipleResources.getResources().isEmpty());
        assertEquals(3, multipleResources.getRegistryResources().size());

        String[] expectedResourceNames = {"swagger.json", "test.json", "test.js"};
        assertEqualResourceNames(expectedResourceNames, multipleResources.getRegistryResources());
    }

    private void assertEqualResourceNames(String[] expectedResourceNames, List<Resource> resources) {

        List<String> actualResourceNames =
                resources.stream().map(resource -> resource.getName()).collect(Collectors.toList());
        assertTrue(areListsEqual(new ArrayList<>(List.of(expectedResourceNames)), actualResourceNames));
    }

    public static boolean areListsEqual(List<String> list1, List<String> list2) {

        if (list1.size() != list2.size()) {
            return false;
        }
        Collections.sort(list1);
        Collections.sort(list2);
        return list1.equals(list2);
    }
}
