/*
 *   Copyright (c) 2025, WSO2 LLC. (http://www.wso2.com).
 *
 *   WSO2 LLC. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 */

package org.eclipse.lemminx.synapse.schemagen;

import org.eclipse.lemminx.customservice.synapse.schemagen.util.FileType;
import org.eclipse.lemminx.customservice.synapse.schemagen.util.SchemaGeneratorForXML;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SchemaGeneratorForXMLTest {

    @Test
    public void testGenerateSchemaWithAttributes() throws IOException {
        SchemaGeneratorForXML schemaGenerator = new SchemaGeneratorForXML();
        String fileContent = new String(Files.readAllBytes(
                Paths.get("src/test/resources/synapse/schemagen/xml/sampleWithAttributes.xml")));
        String xmlSchema = schemaGenerator.getSchemaContent(fileContent, FileType.XML, ",");
        String expectedSchema = new String(Files.readAllBytes(
                Paths.get("src/test/resources/synapse/schemagen/xml/expectedJsonSchema/sampleWithAttributes.json")));
        assertEquals(expectedSchema.replace("\r\n", "").replace("\n", "").replace(" ", "").trim(), xmlSchema.trim().replace(" ", "").trim());
    }

    @Test
    public void testGenerateSchemaWithComplexContentInArray() throws IOException {
        SchemaGeneratorForXML schemaGenerator = new SchemaGeneratorForXML();
        String fileContent = new String(Files.readAllBytes(
                Paths.get("src/test/resources/synapse/schemagen/xml/sampleWithComplexContentInArray.xml")));
        String xmlSchema = schemaGenerator.getSchemaContent(fileContent, FileType.XML, ",");
        String expectedSchema = new String(Files.readAllBytes(
                Paths.get("src/test/resources/synapse/schemagen/xml/expectedJsonSchema/sampleWithComplexContentInArray.json")));
        assertEquals(expectedSchema.replace("\r\n", "").replace("\n", "").replace(" ", "").trim(), xmlSchema.trim().replace(" ", "").trim());
    }

    @Test
    public void testGenerateSchemaWithMultipleAttributesInComplexContent() throws IOException {
        SchemaGeneratorForXML schemaGenerator = new SchemaGeneratorForXML();
        String fileContent = new String(Files.readAllBytes(
                Paths.get("src/test/resources/synapse/schemagen/xml/sampleWithMultipleAttributesInComplexContent.xml")));
        String xmlSchema = schemaGenerator.getSchemaContent(fileContent, FileType.XML, ",");
        String expectedSchema = new String(Files.readAllBytes(
                Paths.get("src/test/resources/synapse/schemagen/xml/expectedJsonSchema/sampleWithMultipleAttributesInComplexContent.json")));
        assertEquals(expectedSchema.replace("\r\n", "").replace("\n", "").replace(" ", "").trim(), xmlSchema.trim().replace(" ", "").trim());
    }

    @Test
    public void testGenerateSchemaWithInDepthSubTypes() throws IOException {
        SchemaGeneratorForXML schemaGenerator = new SchemaGeneratorForXML();
        String fileContent = new String(Files.readAllBytes(
                Paths.get("src/test/resources/synapse/schemagen/xml/sampleWithInDepthSubTypes.xml")));
        String xmlSchema = schemaGenerator.getSchemaContent(fileContent, FileType.XML, ",");
        String expectedSchema = new String(Files.readAllBytes(
                Paths.get("src/test/resources/synapse/schemagen/xml/expectedJsonSchema/sampleWithInDepthSubTypes.json")));
        assertEquals(expectedSchema.replace("\r\n", "").replace("\n", "").replace(" ", "").trim(), xmlSchema.trim().replace(" ", "").trim());
    }

    @Test
    public void testGenerateSchemaWithMultipleComplexTypesInDepth() throws IOException {
        SchemaGeneratorForXML schemaGenerator = new SchemaGeneratorForXML();
        String fileContent = new String(Files.readAllBytes(
                Paths.get("src/test/resources/synapse/schemagen/xml/sampleWithMultipleComplexTypesInDepth.xml")));
        String xmlSchema = schemaGenerator.getSchemaContent(fileContent, FileType.XML, ",");
        String expectedSchema = new String(Files.readAllBytes(
                Paths.get("src/test/resources/synapse/schemagen/xml/expectedJsonSchema/sampleWithMultipleComplexTypesInDepth.json")));
        assertEquals(expectedSchema.replace("\r\n", "").replace("\n", "").replace(" ", "").trim(), xmlSchema.trim().replace(" ", "").trim());
    }

    @Test
    public void testGenerateSchemaWithAttributesInSimpleContent() throws IOException {
        SchemaGeneratorForXML schemaGenerator = new SchemaGeneratorForXML();
        String fileContent = new String(Files.readAllBytes(
                Paths.get("src/test/resources/synapse/schemagen/xml/sampleWithAttributesInSimpleContent.xml")));
        String xmlSchema = schemaGenerator.getSchemaContent(fileContent, FileType.XML, ",");
        String expectedSchema = new String(Files.readAllBytes(
                Paths.get("src/test/resources/synapse/schemagen/xml/expectedJsonSchema/sampleWithAttributesInSimpleContent.json")));
        assertEquals(expectedSchema.replace("\r\n", "").replace("\n", "").replace(" ", "").trim(), xmlSchema.trim().replace(" ", "").trim());
    }

    @Test
    public void testGenerateSchemaWithSimpleContentInArray() throws IOException {
        SchemaGeneratorForXML schemaGenerator = new SchemaGeneratorForXML();
        String fileContent = new String(Files.readAllBytes(
                Paths.get("src/test/resources/synapse/schemagen/xml/sampleWithSimpleContentInArray.xml")));
        String xmlSchema = schemaGenerator.getSchemaContent(fileContent, FileType.XML, ",");
        String expectedSchema = new String(Files.readAllBytes(
                Paths.get("src/test/resources/synapse/schemagen/xml/expectedJsonSchema/sampleWithSimpleContentInArray.json")));
        assertEquals(expectedSchema.replace("\r\n", "").replace("\n", "").replace(" ", "").trim(), xmlSchema.trim().replace(" ", "").trim());
    }

    @Test
    public void testGenerateSchemaWithMultipleSubTypes() throws IOException {
        SchemaGeneratorForXML schemaGenerator = new SchemaGeneratorForXML();
        String fileContent = new String(Files.readAllBytes(
                Paths.get("src/test/resources/synapse/schemagen/xml/sampleWithMultipleSubTypes.xml")));
        String xmlSchema = schemaGenerator.getSchemaContent(fileContent, FileType.XML, ",");
        String expectedSchema = new String(Files.readAllBytes(
                Paths.get("src/test/resources/synapse/schemagen/xml/expectedJsonSchema/sampleWithMultipleSubTypes.json")));
        assertEquals(expectedSchema.replace("\r\n", "").replace("\n", "").replace(" ", "").trim(), xmlSchema.trim().replace(" ", "").trim());
    }

    @Test
    public void testGenerateSchemaWithSingleElementMultipleAttributes() throws IOException {
        SchemaGeneratorForXML schemaGenerator = new SchemaGeneratorForXML();
        String fileContent = new String(Files.readAllBytes(
                Paths.get("src/test/resources/synapse/schemagen/xml/sampleWithSingleElementMultipleAttributes.xml")));
        String xmlSchema = schemaGenerator.getSchemaContent(fileContent, FileType.XML, ",");
        String expectedSchema = new String(Files.readAllBytes(
                Paths.get("src/test/resources/synapse/schemagen/xml/expectedJsonSchema/sampleWithSingleElementMultipleAttributes.json")));
        assertEquals(expectedSchema.replace("\r\n", "").replace("\n", "").replace(" ", "").trim(), xmlSchema.trim().replace(" ", "").trim());
    }

    @Test
    public void testGenerateSchemaWithOnlyAttributesInSingleElement() throws IOException {
        SchemaGeneratorForXML schemaGenerator = new SchemaGeneratorForXML();
        String fileContent = new String(Files.readAllBytes(
                Paths.get("src/test/resources/synapse/schemagen/xml/sampleWithOnlyAttributesInSingleElement.xml")));
        String xmlSchema = schemaGenerator.getSchemaContent(fileContent, FileType.XML, ",");
        String expectedSchema = new String(Files.readAllBytes(
                Paths.get("src/test/resources/synapse/schemagen/xml/expectedJsonSchema/sampleWithOnlyAttributesInSingleElement.json")));
        assertEquals(expectedSchema.replace("\r\n", "").replace("\n", "").replace(" ", "").trim(), xmlSchema.trim().replace(" ", "").trim());
    }

    @Test
    public void testGenerateSchemaWithSimpleContentArrayAmongOthers() throws IOException {
        SchemaGeneratorForXML schemaGenerator = new SchemaGeneratorForXML();
        String fileContent = new String(Files.readAllBytes(
                Paths.get("src/test/resources/synapse/schemagen/xml/sampleWithSimpleContentArrayAmongOthers.xml")));
        String xmlSchema = schemaGenerator.getSchemaContent(fileContent, FileType.XML, ",");
        String expectedSchema = new String(Files.readAllBytes(
                Paths.get("src/test/resources/synapse/schemagen/xml/expectedJsonSchema/sampleWithSimpleContentArrayAmongOthers.json")));
        assertEquals(expectedSchema.replace("\r\n", "").replace("\n", "").replace(" ", "").trim(), xmlSchema.trim().replace(" ", "").trim());
    }

    @Test
    public void testGenerateSchemaWithMixedType() throws IOException {
        SchemaGeneratorForXML schemaGenerator = new SchemaGeneratorForXML();
        String fileContent = new String(Files.readAllBytes(
                Paths.get("src/test/resources/synapse/schemagen/xml/sampleWithMixedType.xml")));
        String xmlSchema = schemaGenerator.getSchemaContent(fileContent, FileType.XML, ",");
        String expectedSchema = new String(Files.readAllBytes(
                Paths.get("src/test/resources/synapse/schemagen/xml/expectedJsonSchema/sampleWithMixedType.json")));
        assertEquals(expectedSchema.replace("\r\n", "").replace("\n", "").replace(" ", "").trim(), xmlSchema.trim().replace(" ", "").trim());
    }

    @Test
    public void testGenerateSchemaWithAllMultipleDataTypes() throws IOException {
        SchemaGeneratorForXML schemaGenerator = new SchemaGeneratorForXML();
        String fileContent = new String(Files.readAllBytes(
                Paths.get("src/test/resources/synapse/schemagen/xml/sampleWithAllMultipleDataTypes.xml")));
        String xmlSchema = schemaGenerator.getSchemaContent(fileContent, FileType.XML, ",");
        String expectedSchema = new String(Files.readAllBytes(
                Paths.get("src/test/resources/synapse/schemagen/xml/expectedJsonSchema/sampleWithAllMultipleDataTypes.json")));
        assertEquals(expectedSchema.replace("\r\n", "").replace("\n", "").replace(" ", "").trim(), xmlSchema.trim().replace(" ", "").trim());
    }

    @Test
    public void testGenerateSchemaWithBaseTypes() throws IOException {
        SchemaGeneratorForXML schemaGenerator = new SchemaGeneratorForXML();
        String fileContent = new String(Files.readAllBytes(
                Paths.get("src/test/resources/synapse/schemagen/xml/sampleWithBaseTypes.xml")));
        String xmlSchema = schemaGenerator.getSchemaContent(fileContent, FileType.XML, ",");
        String expectedSchema = new String(Files.readAllBytes(
                Paths.get("src/test/resources/synapse/schemagen/xml/expectedJsonSchema/sampleWithBaseTypes.json")));
        assertEquals(expectedSchema.replace("\r\n", "").replace("\n", "").replace(" ", "").trim(), xmlSchema.trim().replace(" ", "").trim());
    }

    @Test
    public void testGenerateSchemaWithCDATA() throws IOException {
        SchemaGeneratorForXML schemaGenerator = new SchemaGeneratorForXML();
        String fileContent = new String(Files.readAllBytes(
                Paths.get("src/test/resources/synapse/schemagen/xml/sampleWithCDATA.xml")));
        String xmlSchema = schemaGenerator.getSchemaContent(fileContent, FileType.XML, ",");
        String expectedSchema = new String(Files.readAllBytes(
                Paths.get("src/test/resources/synapse/schemagen/xml/expectedJsonSchema/sampleWithCDATA.json")));
        assertEquals(expectedSchema.replace("\r\n", "").replace("\n", "").replace(" ", "").trim(), xmlSchema.trim().replace(" ", "").trim());
    }

    @Test
    public void testGenerateSchemaWithEmptyElement() throws IOException {
        SchemaGeneratorForXML schemaGenerator = new SchemaGeneratorForXML();
        String fileContent = new String(Files.readAllBytes(
                Paths.get("src/test/resources/synapse/schemagen/xml/sampleWithEmptyElement.xml")));
        String xmlSchema = schemaGenerator.getSchemaContent(fileContent, FileType.XML, ",");
        String expectedSchema = new String(Files.readAllBytes(
                Paths.get("src/test/resources/synapse/schemagen/xml/expectedJsonSchema/sampleWithEmptyElement.json")));
        assertEquals(expectedSchema.replace("\r\n", "").replace("\n", "").replace(" ", "").trim(), xmlSchema.trim().replace(" ", "").trim());
    }

    @Test
    public void testGenerateSchemaWithNamespaces() throws IOException {
        SchemaGeneratorForXML schemaGenerator = new SchemaGeneratorForXML();
        String fileContent = new String(Files.readAllBytes(
                Paths.get("src/test/resources/synapse/schemagen/xml/sampleWithNamespaces.xml")));
        String xmlSchema = schemaGenerator.getSchemaContent(fileContent, FileType.XML, ",");
        String expectedSchema = new String(Files.readAllBytes(
                Paths.get("src/test/resources/synapse/schemagen/xml/expectedJsonSchema/sampleWithNamespaces.json")));
        assertEquals(expectedSchema.replace("\r\n", "").replace("\n", "").replace(" ", "").trim(), xmlSchema.trim().replace(" ", "").trim());
    }

    @Test
    public void testGenerateSchemaWithHierarchicalStructureWithArray() throws IOException {
        SchemaGeneratorForXML schemaGenerator = new SchemaGeneratorForXML();
        String fileContent = new String(Files.readAllBytes(
                Paths.get("src/test/resources/synapse/schemagen/xml/sampleWithHierarchicalStructureWithArray.xml")));
        String xmlSchema = schemaGenerator.getSchemaContent(fileContent, FileType.XML, ",");
        String expectedSchema = new String(Files.readAllBytes(
                Paths.get("src/test/resources/synapse/schemagen/xml/expectedJsonSchema/sampleWithHierarchicalStructureWithArray.json")));
        assertEquals(expectedSchema.replace("\r\n", "").replace("\n", "").replace(" ", "").trim(), xmlSchema.trim().replace(" ", "").trim());
    }

    @Test
    public void testGenerateSchemaWithSpecialCharacters() throws IOException {
        SchemaGeneratorForXML schemaGenerator = new SchemaGeneratorForXML();
        String fileContent = new String(Files.readAllBytes(
                Paths.get("src/test/resources/synapse/schemagen/xml/sampleWithSpecialCharacters.xml")));
        String xmlSchema = schemaGenerator.getSchemaContent(fileContent, FileType.XML, ",");
        String expectedSchema = new String(Files.readAllBytes(
                Paths.get("src/test/resources/synapse/schemagen/xml/expectedJsonSchema/sampleWithSpecialCharacters.json")));
        assertEquals(expectedSchema.replace("\r\n", "").replace("\n", "").replace(" ", "").trim(), xmlSchema.trim().replace(" ", "").trim());
    }

    @Test
    public void testGenerateSchemaWithDeeplyNestedStructure() throws IOException {
        SchemaGeneratorForXML schemaGenerator = new SchemaGeneratorForXML();
        String fileContent = new String(Files.readAllBytes(
                Paths.get("src/test/resources/synapse/schemagen/xml/sampleWithDeeplyNestedStructure.xml")));
        String xmlSchema = schemaGenerator.getSchemaContent(fileContent, FileType.XML, ",");
        String expectedSchema = new String(Files.readAllBytes(
                Paths.get("src/test/resources/synapse/schemagen/xml/expectedJsonSchema/sampleWithDeeplyNestedStructure.json")));
        assertEquals(expectedSchema.replace("\r\n", "").replace("\n", "").replace(" ", "").trim(), xmlSchema.trim().replace(" ", "").trim());
    }
}
