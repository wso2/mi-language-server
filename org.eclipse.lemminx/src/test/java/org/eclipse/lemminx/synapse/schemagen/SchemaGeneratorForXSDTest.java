package org.eclipse.lemminx.synapse.schemagen;

import org.eclipse.lemminx.customservice.synapse.schemagen.util.FileType;
import org.eclipse.lemminx.customservice.synapse.schemagen.util.SchemaGeneratorForXSD;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SchemaGeneratorForXSDTest {

    @Test
    public void testGenerateSchemaWithAllIndicator() throws IOException {
        SchemaGeneratorForXSD schemaGenerator = new SchemaGeneratorForXSD();
        String jsonSchema = schemaGenerator.getSchemaResourcePath(
                "src/test/resources/schemagen/xsd/xsdSchemas/sampleWithAllIndicator.xsd", FileType.XSD, ",");
        String expectedSchema = new String(Files.readAllBytes(
                Paths.get("src/test/resources/schemagen/xsd/expectedJsonSchema/sampleWithAllIndicator.json")));
        assertEquals(removeNewLinesAndSpaces(expectedSchema), removeNewLinesAndSpaces(jsonSchema));
    }

    @Test
    public void testGenerateSchemaWithAttributes() throws IOException {
        SchemaGeneratorForXSD schemaGenerator = new SchemaGeneratorForXSD();
        String jsonSchema = schemaGenerator.getSchemaResourcePath(
                "src/test/resources/schemagen/xsd/xsdSchemas/sampleWithAttributes.xsd", FileType.XSD, ",");
        String expectedSchema = new String(Files.readAllBytes(
                Paths.get("src/test/resources/schemagen/xsd/expectedJsonSchema/sampleWithAttributes.json")));
        assertEquals(removeNewLinesAndSpaces(expectedSchema), removeNewLinesAndSpaces(jsonSchema));
    }

    @Test
    public void testGenerateSchemaWithBaseTypes() throws IOException {
        SchemaGeneratorForXSD schemaGenerator = new SchemaGeneratorForXSD();
        String jsonSchema = schemaGenerator.getSchemaResourcePath(
                "src/test/resources/schemagen/xsd/xsdSchemas/sampleWithBaseTypes.xsd", FileType.XSD, ",");
        String expectedSchema = new String(Files.readAllBytes(
                Paths.get("src/test/resources/schemagen/xsd/expectedJsonSchema/sampleWithBaseTypes.json")));
        assertEquals(removeNewLinesAndSpaces(expectedSchema), removeNewLinesAndSpaces(jsonSchema));
    }

    @Test
    public void testGenerateSchemaWithBasicChoices() throws IOException {
        SchemaGeneratorForXSD schemaGenerator = new SchemaGeneratorForXSD();
        String jsonSchema = schemaGenerator.getSchemaResourcePath(
                "src/test/resources/schemagen/xsd/xsdSchemas/sampleWithBasicChoices.xsd", FileType.XSD, ",");
        String expectedSchema = new String(Files.readAllBytes(
                Paths.get("src/test/resources/schemagen/xsd/expectedJsonSchema/sampleWithBasicChoices.json")));
        assertEquals(removeNewLinesAndSpaces(expectedSchema), removeNewLinesAndSpaces(jsonSchema));
    }

    @Test
    public void testGenerateSchemaWithBasicTypes() throws IOException {
        SchemaGeneratorForXSD schemaGenerator = new SchemaGeneratorForXSD();
        String jsonSchema = schemaGenerator.getSchemaResourcePath(
                "src/test/resources/schemagen/xsd/xsdSchemas/sampleWithBasicTypes.xsd", FileType.XSD, ",");
        String expectedSchema = new String(Files.readAllBytes(
                Paths.get("src/test/resources/schemagen/xsd/expectedJsonSchema/sampleWithBasicTypes.json")));
        assertEquals(removeNewLinesAndSpaces(expectedSchema), removeNewLinesAndSpaces(jsonSchema));
    }

    @Test
    public void testGenerateSchemaWithElementAndAttributeGroups() throws IOException {
        SchemaGeneratorForXSD schemaGenerator = new SchemaGeneratorForXSD();
        String jsonSchema = schemaGenerator.getSchemaResourcePath(
                "src/test/resources/schemagen/xsd/xsdSchemas/sampleWithElementAndAttributeGroups.xsd", FileType.XSD, ",");
        String expectedSchema = new String(Files.readAllBytes(
                Paths.get("src/test/resources/schemagen/xsd/expectedJsonSchema/sampleWithElementAndAttributeGroups.json")));
        assertEquals(removeNewLinesAndSpaces(expectedSchema), removeNewLinesAndSpaces(jsonSchema));
    }

    @Test
    public void testGenerateSchemaWithEmptyContent() throws IOException {
        SchemaGeneratorForXSD schemaGenerator = new SchemaGeneratorForXSD();
        String jsonSchema = schemaGenerator.getSchemaResourcePath(
                "src/test/resources/schemagen/xsd/xsdSchemas/sampleWithEmptyContent.xsd", FileType.XSD, ",");
        String expectedSchema = new String(Files.readAllBytes(
                Paths.get("src/test/resources/schemagen/xsd/expectedJsonSchema/sampleWithEmptyContent.json")));
        assertEquals(removeNewLinesAndSpaces(expectedSchema), removeNewLinesAndSpaces(jsonSchema));
    }

    @Test
    public void testGenerateSchemaWithEmptyContentRestriction() throws IOException {
        SchemaGeneratorForXSD schemaGenerator = new SchemaGeneratorForXSD();
        String jsonSchema = schemaGenerator.getSchemaResourcePath(
                "src/test/resources/schemagen/xsd/xsdSchemas/sampleWithEmptyContentRestriction.xsd", FileType.XSD, ",");
        String expectedSchema = new String(Files.readAllBytes(
                Paths.get("src/test/resources/schemagen/xsd/expectedJsonSchema/sampleWithEmptyContentRestriction.json")));
        assertEquals(removeNewLinesAndSpaces(expectedSchema), removeNewLinesAndSpaces(jsonSchema));
    }

    @Test
    public void testGenerateSchemaWithEnum() throws IOException {
        SchemaGeneratorForXSD schemaGenerator = new SchemaGeneratorForXSD();
        String jsonSchema = schemaGenerator.getSchemaResourcePath(
                "src/test/resources/schemagen/xsd/xsdSchemas/sampleWithEnum.xsd", FileType.XSD, ",");
        String expectedSchema = new String(Files.readAllBytes(
                Paths.get("src/test/resources/schemagen/xsd/expectedJsonSchema/sampleWithEnum.json")));
        assertEquals(removeNewLinesAndSpaces(expectedSchema), removeNewLinesAndSpaces(jsonSchema));
    }

    @Test
    public void testGenerateSchemaWithEnumFieldInObject() throws IOException {
        SchemaGeneratorForXSD schemaGenerator = new SchemaGeneratorForXSD();
        String jsonSchema = schemaGenerator.getSchemaResourcePath(
                "src/test/resources/schemagen/xsd/xsdSchemas/sampleWithEnumFieldInObject.xsd", FileType.XSD, ",");
        String expectedSchema = new String(Files.readAllBytes(
                Paths.get("src/test/resources/schemagen/xsd/expectedJsonSchema/sampleWithEnumFieldInObject.json")));
        assertEquals(removeNewLinesAndSpaces(expectedSchema), removeNewLinesAndSpaces(jsonSchema));
    }

    @Test
    public void testGenerateSchemaWithGroupElement() throws IOException {
        SchemaGeneratorForXSD schemaGenerator = new SchemaGeneratorForXSD();
        String jsonSchema = schemaGenerator.getSchemaResourcePath(
                "src/test/resources/schemagen/xsd/xsdSchemas/sampleWithGroupElement.xsd", FileType.XSD, ",");
        String expectedSchema = new String(Files.readAllBytes(
                Paths.get("src/test/resources/schemagen/xsd/expectedJsonSchema/sampleWithGroupElement.json")));
        assertEquals(removeNewLinesAndSpaces(expectedSchema), removeNewLinesAndSpaces(jsonSchema));
    }

    @Test
    public void testGenerateSchemaWithInDepthSubTypes() throws IOException {
        SchemaGeneratorForXSD schemaGenerator = new SchemaGeneratorForXSD();
        String jsonSchema = schemaGenerator.getSchemaResourcePath(
                "src/test/resources/schemagen/xsd/xsdSchemas/sampleWithInDepthSubTypes.xsd", FileType.XSD, ",");
        String expectedSchema = new String(Files.readAllBytes(
                Paths.get("src/test/resources/schemagen/xsd/expectedJsonSchema/sampleWithInDepthSubTypes.json")));
        assertEquals(removeNewLinesAndSpaces(expectedSchema), removeNewLinesAndSpaces(jsonSchema));
    }

    @Test
    public void testGenerateSchemaWithList() throws IOException {
        SchemaGeneratorForXSD schemaGenerator = new SchemaGeneratorForXSD();
        String jsonSchema = schemaGenerator.getSchemaResourcePath(
                "src/test/resources/schemagen/xsd/xsdSchemas/sampleWithList.xsd", FileType.XSD, ",");
        String expectedSchema = new String(Files.readAllBytes(
                Paths.get("src/test/resources/schemagen/xsd/expectedJsonSchema/sampleWithList.json")));
        assertEquals(removeNewLinesAndSpaces(expectedSchema), removeNewLinesAndSpaces(jsonSchema));
    }

    @Test
    public void testGenerateSchemaWithMaxOccursN() throws IOException {
        SchemaGeneratorForXSD schemaGenerator = new SchemaGeneratorForXSD();
        String jsonSchema = schemaGenerator.getSchemaResourcePath(
                "src/test/resources/schemagen/xsd/xsdSchemas/sampleWithMaxOccursN.xsd", FileType.XSD, ",");
        String expectedSchema = new String(Files.readAllBytes(
                Paths.get("src/test/resources/schemagen/xsd/expectedJsonSchema/sampleWithMaxOccursN.json")));
        assertEquals(removeNewLinesAndSpaces(expectedSchema), removeNewLinesAndSpaces(jsonSchema));
    }

    @Test
    public void testGenerateSchemaWithMaxOccursUnbounded() throws IOException {
        SchemaGeneratorForXSD schemaGenerator = new SchemaGeneratorForXSD();
        String jsonSchema = schemaGenerator.getSchemaResourcePath(
                "src/test/resources/schemagen/xsd/xsdSchemas/sampleWithMaxOccursUnbounded.xsd", FileType.XSD, ",");
        String expectedSchema = new String(Files.readAllBytes(
                Paths.get("src/test/resources/schemagen/xsd/expectedJsonSchema/sampleWithMaxOccursUnbounded.json")));
        assertEquals(removeNewLinesAndSpaces(expectedSchema), removeNewLinesAndSpaces(jsonSchema));
    }

    @Test
    public void testGenerateSchemaWithMaxOccursZero() throws IOException {
        SchemaGeneratorForXSD schemaGenerator = new SchemaGeneratorForXSD();
        String jsonSchema = schemaGenerator.getSchemaResourcePath(
                "src/test/resources/schemagen/xsd/xsdSchemas/sampleWithMaxOccursZero.xsd", FileType.XSD, ",");
        String expectedSchema = new String(Files.readAllBytes(
                Paths.get("src/test/resources/schemagen/xsd/expectedJsonSchema/sampleWithMaxOccursZero.json")));
        assertEquals(removeNewLinesAndSpaces(expectedSchema), removeNewLinesAndSpaces(jsonSchema));
    }

    @Test
    public void testGenerateSchemaWithMinOccursN() throws IOException {
        SchemaGeneratorForXSD schemaGenerator = new SchemaGeneratorForXSD();
        String jsonSchema = schemaGenerator.getSchemaResourcePath(
                "src/test/resources/schemagen/xsd/xsdSchemas/sampleWithMinOccursN.xsd", FileType.XSD, ",");
        String expectedSchema = new String(Files.readAllBytes(
                Paths.get("src/test/resources/schemagen/xsd/expectedJsonSchema/sampleWithMinOccursN.json")));
        assertEquals(removeNewLinesAndSpaces(expectedSchema), removeNewLinesAndSpaces(jsonSchema));
    }

    @Test
    public void testGenerateSchemaWithMinOccursOne() throws IOException {
        SchemaGeneratorForXSD schemaGenerator = new SchemaGeneratorForXSD();
        String jsonSchema = schemaGenerator.getSchemaResourcePath(
                "src/test/resources/schemagen/xsd/xsdSchemas/sampleWithMinOccursOne.xsd", FileType.XSD, ",");
        String expectedSchema = new String(Files.readAllBytes(
                Paths.get("src/test/resources/schemagen/xsd/expectedJsonSchema/sampleWithMinOccursOne.json")));
        assertEquals(removeNewLinesAndSpaces(expectedSchema), removeNewLinesAndSpaces(jsonSchema));
    }

    @Test
    public void testGenerateSchemaWithMinOccursZero() throws IOException {
        SchemaGeneratorForXSD schemaGenerator = new SchemaGeneratorForXSD();
        String jsonSchema = schemaGenerator.getSchemaResourcePath(
                "src/test/resources/schemagen/xsd/xsdSchemas/sampleWithMinOccursZero.xsd", FileType.XSD, ",");
        String expectedSchema = new String(Files.readAllBytes(
                Paths.get("src/test/resources/schemagen/xsd/expectedJsonSchema/sampleWithMinOccursZero.json")));
        assertEquals(removeNewLinesAndSpaces(expectedSchema), removeNewLinesAndSpaces(jsonSchema));
    }

    @Test
    public void testGenerateSchemaWithMixedType() throws IOException {
        SchemaGeneratorForXSD schemaGenerator = new SchemaGeneratorForXSD();
        String jsonSchema = schemaGenerator.getSchemaResourcePath(
                "src/test/resources/schemagen/xsd/xsdSchemas/sampleWithMixedType.xsd", FileType.XSD, ",");
        String expectedSchema = new String(Files.readAllBytes(
                Paths.get("src/test/resources/schemagen/xsd/expectedJsonSchema/sampleWithMixedType.json")));
        assertEquals(removeNewLinesAndSpaces(expectedSchema), removeNewLinesAndSpaces(jsonSchema));
    }

    @Test
    public void testGenerateSchemaWithMultipleComplexTypesInDepth() throws IOException {
        SchemaGeneratorForXSD schemaGenerator = new SchemaGeneratorForXSD();
        String jsonSchema = schemaGenerator.getSchemaResourcePath(
                "src/test/resources/schemagen/xsd/xsdSchemas/sampleWithMultipleComplexTypesInDepth.xsd", FileType.XSD, ",");
        String expectedSchema = new String(Files.readAllBytes(
                Paths.get("src/test/resources/schemagen/xsd/expectedJsonSchema/sampleWithMultipleComplexTypesInDepth.json")));
        assertEquals(removeNewLinesAndSpaces(expectedSchema), removeNewLinesAndSpaces(jsonSchema));
    }

    @Test
    public void testGenerateSchemaWithMultipleDataTypes() throws IOException {
        SchemaGeneratorForXSD schemaGenerator = new SchemaGeneratorForXSD();
        String jsonSchema = schemaGenerator.getSchemaResourcePath(
                "src/test/resources/schemagen/xsd/xsdSchemas/sampleWithMultipleDataTypes.xsd", FileType.XSD, ",");
        String expectedSchema = new String(Files.readAllBytes(
                Paths.get("src/test/resources/schemagen/xsd/expectedJsonSchema/sampleWithMultipleDataTypes.json")));
        assertEquals(removeNewLinesAndSpaces(expectedSchema), removeNewLinesAndSpaces(jsonSchema));
    }

    @Test
    public void testGenerateSchemaWithMultipleSimpleContent() throws IOException {
        SchemaGeneratorForXSD schemaGenerator = new SchemaGeneratorForXSD();
        String jsonSchema = schemaGenerator.getSchemaResourcePath(
                "src/test/resources/schemagen/xsd/xsdSchemas/sampleWithMultipleSimpleContent.xsd", FileType.XSD, ",");
        String expectedSchema = new String(Files.readAllBytes(
                Paths.get("src/test/resources/schemagen/xsd/expectedJsonSchema/sampleWithMultipleSimpleContent.json")));
        assertEquals(removeNewLinesAndSpaces(expectedSchema), removeNewLinesAndSpaces(jsonSchema));
    }

    @Test
    public void testGenerateSchemaWithSimpleContentInArray() throws IOException {
        SchemaGeneratorForXSD schemaGenerator = new SchemaGeneratorForXSD();
        String jsonSchema = schemaGenerator.getSchemaResourcePath(
                "src/test/resources/schemagen/xsd/xsdSchemas/sampleWithSimpleContentInArray.xsd", FileType.XSD, ",");
        String expectedSchema = new String(Files.readAllBytes(
                Paths.get("src/test/resources/schemagen/xsd/expectedJsonSchema/sampleWithSimpleContentInArray.json")));
        assertEquals(removeNewLinesAndSpaces(expectedSchema), removeNewLinesAndSpaces(jsonSchema));
    }

    @Test
    public void testGenerateSchemaWithSimpleContentWithoutAttribute() throws IOException {
        SchemaGeneratorForXSD schemaGenerator = new SchemaGeneratorForXSD();
        String jsonSchema = schemaGenerator.getSchemaResourcePath(
                "src/test/resources/schemagen/xsd/xsdSchemas/sampleWithSimpleContentWithoutAttribute.xsd", FileType.XSD, ",");
        String expectedSchema = new String(Files.readAllBytes(
                Paths.get("src/test/resources/schemagen/xsd/expectedJsonSchema/sampleWithSimpleContentWithoutAttribute.json")));
        assertEquals(removeNewLinesAndSpaces(expectedSchema), removeNewLinesAndSpaces(jsonSchema));
    }

    @Test
    public void testGenerateSchemaWithSimpleTypeInAttribute() throws IOException {
        SchemaGeneratorForXSD schemaGenerator = new SchemaGeneratorForXSD();
        String jsonSchema = schemaGenerator.getSchemaResourcePath(
                "src/test/resources/schemagen/xsd/xsdSchemas/sampleWithSimpleTypeInAttribute.xsd", FileType.XSD, ",");
        String expectedSchema = new String(Files.readAllBytes(
                Paths.get("src/test/resources/schemagen/xsd/expectedJsonSchema/sampleWithSimpleTypeInAttribute.json")));
        assertEquals(removeNewLinesAndSpaces(expectedSchema), removeNewLinesAndSpaces(jsonSchema));
    }

    @Test
    public void testGenerateSchemaWithSimpleTypeWithEnumInArray() throws IOException {
        SchemaGeneratorForXSD schemaGenerator = new SchemaGeneratorForXSD();
        String jsonSchema = schemaGenerator.getSchemaResourcePath(
                "src/test/resources/schemagen/xsd/xsdSchemas/sampleWithSimpleTypeWithEnumInArray.xsd", FileType.XSD, ",");
        String expectedSchema = new String(Files.readAllBytes(
                Paths.get("src/test/resources/schemagen/xsd/expectedJsonSchema/sampleWithSimpleTypeWithEnumInArray.json")));
        assertEquals(removeNewLinesAndSpaces(expectedSchema), removeNewLinesAndSpaces(jsonSchema));
    }

    @Test
    public void testGenerateSchemaWithMultipleSubTypes() throws IOException {
        SchemaGeneratorForXSD schemaGenerator = new SchemaGeneratorForXSD();
        String jsonSchema = schemaGenerator.getSchemaResourcePath(
                "src/test/resources/schemagen/xsd/xsdSchemas/sampleWithMultipleSubTypes.xsd", FileType.XSD, ",");
        String expectedSchema = new String(Files.readAllBytes(
                Paths.get("src/test/resources/schemagen/xsd/expectedJsonSchema/sampleWithMultipleSubTypes.json")));
        assertEquals(removeNewLinesAndSpaces(expectedSchema), removeNewLinesAndSpaces(jsonSchema));
    }

    @Test
    public void testGenerateSchemaWithMultipleTopLevelComplexElements() throws IOException {
        SchemaGeneratorForXSD schemaGenerator = new SchemaGeneratorForXSD();
        String jsonSchema = schemaGenerator.getSchemaResourcePath(
                "src/test/resources/schemagen/xsd/xsdSchemas/sampleWithMultipleTopLevelComplexElements.xsd", FileType.XSD, ",");
        String expectedSchema = new String(Files.readAllBytes(
                Paths.get("src/test/resources/schemagen/xsd/expectedJsonSchema/sampleWithMultipleTopLevelComplexElements.json")));
        assertEquals(removeNewLinesAndSpaces(expectedSchema), removeNewLinesAndSpaces(jsonSchema));
    }

    @Test
    public void testGenerateSchemaWithMultipleTopLevelElements() throws IOException {
        SchemaGeneratorForXSD schemaGenerator = new SchemaGeneratorForXSD();
        String jsonSchema = schemaGenerator.getSchemaResourcePath(
                "src/test/resources/schemagen/xsd/xsdSchemas/sampleWithMultipleTopLevelElements.xsd", FileType.XSD, ",");
        String expectedSchema = new String(Files.readAllBytes(
                Paths.get("src/test/resources/schemagen/xsd/expectedJsonSchema/sampleWithMultipleTopLevelElements.json")));
        assertEquals(removeNewLinesAndSpaces(expectedSchema), removeNewLinesAndSpaces(jsonSchema));
    }

    @Test
    public void testGenerateSchemaWithMultipleTypes() throws IOException {
        SchemaGeneratorForXSD schemaGenerator = new SchemaGeneratorForXSD();
        String jsonSchema = schemaGenerator.getSchemaResourcePath(
                "src/test/resources/schemagen/xsd/xsdSchemas/sampleWithMultipleTypes.xsd", FileType.XSD, ",");
        String expectedSchema = new String(Files.readAllBytes(
                Paths.get("src/test/resources/schemagen/xsd/expectedJsonSchema/sampleWithMultipleTypes.json")));
        assertEquals(removeNewLinesAndSpaces(expectedSchema), removeNewLinesAndSpaces(jsonSchema));
    }

    @Test
    public void testGenerateSchemaWithNestedSimpleContent() throws IOException {
        SchemaGeneratorForXSD schemaGenerator = new SchemaGeneratorForXSD();
        String jsonSchema = schemaGenerator.getSchemaResourcePath(
                "src/test/resources/schemagen/xsd/xsdSchemas/sampleWithNestedSimpleContent.xsd", FileType.XSD, ",");
        String expectedSchema = new String(Files.readAllBytes(
                Paths.get("src/test/resources/schemagen/xsd/expectedJsonSchema/sampleWithNestedSimpleContent.json")));
        assertEquals(removeNewLinesAndSpaces(expectedSchema), removeNewLinesAndSpaces(jsonSchema));
    }

    @Test
    public void testGenerateSchemaWithNotationElement() throws IOException {
        SchemaGeneratorForXSD schemaGenerator = new SchemaGeneratorForXSD();
        String jsonSchema = schemaGenerator.getSchemaResourcePath(
                "src/test/resources/schemagen/xsd/xsdSchemas/sampleWithNotationElement.xsd", FileType.XSD, ",");
        String expectedSchema = new String(Files.readAllBytes(
                Paths.get("src/test/resources/schemagen/xsd/expectedJsonSchema/sampleWithNotationElement.json")));
        assertEquals(removeNewLinesAndSpaces(expectedSchema), removeNewLinesAndSpaces(jsonSchema));
    }

    @Test
    public void testGenerateSchemaWithOnlyAttributesInComplexType() throws IOException {
        SchemaGeneratorForXSD schemaGenerator = new SchemaGeneratorForXSD();
        String jsonSchema = schemaGenerator.getSchemaResourcePath(
                "src/test/resources/schemagen/xsd/xsdSchemas/sampleWithOnlyAttributesInComplexType.xsd", FileType.XSD, ",");
        String expectedSchema = new String(Files.readAllBytes(
                Paths.get("src/test/resources/schemagen/xsd/expectedJsonSchema/sampleWithOnlyAttributesInComplexType.json")));
        assertEquals(removeNewLinesAndSpaces(expectedSchema), removeNewLinesAndSpaces(jsonSchema));
    }

    @Test
    public void testGenerateSchemaWithAnnotationAndDocumentation() throws IOException {
        SchemaGeneratorForXSD schemaGenerator = new SchemaGeneratorForXSD();
        String jsonSchema = schemaGenerator.getSchemaResourcePath(
                "src/test/resources/schemagen/xsd/xsdSchemas/sampleWithAnnotationAndDocumentation.xsd", FileType.XSD, ",");
        String expectedSchema = new String(Files.readAllBytes(
                Paths.get("src/test/resources/schemagen/xsd/expectedJsonSchema/sampleWithAnnotationAndDocumentation.json")));
        assertEquals(removeNewLinesAndSpaces(expectedSchema), removeNewLinesAndSpaces(jsonSchema));
    }

    @Test
    public void testGenerateSchemaWithAnyAttribute() throws IOException {
        SchemaGeneratorForXSD schemaGenerator = new SchemaGeneratorForXSD();
        String jsonSchema = schemaGenerator.getSchemaResourcePath(
                "src/test/resources/schemagen/xsd/xsdSchemas/sampleWithAnyAttribute.xsd", FileType.XSD, ",");
        String expectedSchema = new String(Files.readAllBytes(
                Paths.get("src/test/resources/schemagen/xsd/expectedJsonSchema/sampleWithAnyAttribute.json")));
        assertEquals(removeNewLinesAndSpaces(expectedSchema), removeNewLinesAndSpaces(jsonSchema));
    }

    @Test
    public void testGenerateSchemaWithAnyElements() throws IOException {
        SchemaGeneratorForXSD schemaGenerator = new SchemaGeneratorForXSD();
        String jsonSchema = schemaGenerator.getSchemaResourcePath(
                "src/test/resources/schemagen/xsd/xsdSchemas/sampleWithAnyElements.xsd", FileType.XSD, ",");
        String expectedSchema = new String(Files.readAllBytes(
                Paths.get("src/test/resources/schemagen/xsd/expectedJsonSchema/sampleWithAnyElements.json")));
        assertEquals(removeNewLinesAndSpaces(expectedSchema), removeNewLinesAndSpaces(jsonSchema));
    }

    @Test
    public void testGenerateSchemaWithArray() throws IOException {
        SchemaGeneratorForXSD schemaGenerator = new SchemaGeneratorForXSD();
        String jsonSchema = schemaGenerator.getSchemaResourcePath(
                "src/test/resources/schemagen/xsd/xsdSchemas/sampleWithArray.xsd", FileType.XSD, ",");
        String expectedSchema = new String(Files.readAllBytes(
                Paths.get("src/test/resources/schemagen/xsd/expectedJsonSchema/sampleWithArray.json")));
        assertEquals(removeNewLinesAndSpaces(expectedSchema), removeNewLinesAndSpaces(jsonSchema));
    }

    @Test
    public void testGenerateSchemaWithReferencedSimpleContent() throws IOException {
        SchemaGeneratorForXSD schemaGenerator = new SchemaGeneratorForXSD();
        String jsonSchema = schemaGenerator.getSchemaResourcePath(
                "src/test/resources/schemagen/xsd/xsdSchemas/sampleWithReferencedSimpleContent.xsd", FileType.XSD, ",");
        String expectedSchema = new String(Files.readAllBytes(
                Paths.get("src/test/resources/schemagen/xsd/expectedJsonSchema/sampleWithReferencedSimpleContent.json")));
        assertEquals(removeNewLinesAndSpaces(expectedSchema), removeNewLinesAndSpaces(jsonSchema));
    }

    @Test
    public void testGenerateSchemaWithSelectorElement() throws IOException {
        SchemaGeneratorForXSD schemaGenerator = new SchemaGeneratorForXSD();
        String jsonSchema = schemaGenerator.getSchemaResourcePath(
                "src/test/resources/schemagen/xsd/xsdSchemas/sampleWithSelectorElement.xsd", FileType.XSD, ",");
        String expectedSchema = new String(Files.readAllBytes(
                Paths.get("src/test/resources/schemagen/xsd/expectedJsonSchema/sampleWithSelectorElement.json")));
        assertEquals(removeNewLinesAndSpaces(expectedSchema), removeNewLinesAndSpaces(jsonSchema));
    }

    @Test
    public void testGenerateSchemaWithSimpleChoiceInArray() throws IOException {
        SchemaGeneratorForXSD schemaGenerator = new SchemaGeneratorForXSD();
        String jsonSchema = schemaGenerator.getSchemaResourcePath(
                "src/test/resources/schemagen/xsd/xsdSchemas/sampleWithSimpleChoiceInArray.xsd", FileType.XSD, ",");
        String expectedSchema = new String(Files.readAllBytes(
                Paths.get("src/test/resources/schemagen/xsd/expectedJsonSchema/sampleWithSimpleChoiceInArray.json")));
        assertEquals(removeNewLinesAndSpaces(expectedSchema), removeNewLinesAndSpaces(jsonSchema));
    }

    @Test
    public void testGenerateSchemaWithSimpleContent() throws IOException {
        SchemaGeneratorForXSD schemaGenerator = new SchemaGeneratorForXSD();
        String jsonSchema = schemaGenerator.getSchemaResourcePath(
                "src/test/resources/schemagen/xsd/xsdSchemas/sampleWithSimpleContent.xsd", FileType.XSD, ",");
        String expectedSchema = new String(Files.readAllBytes(
                Paths.get("src/test/resources/schemagen/xsd/expectedJsonSchema/sampleWithSimpleContent.json")));
        assertEquals(removeNewLinesAndSpaces(expectedSchema), removeNewLinesAndSpaces(jsonSchema));
    }

    @Test
    public void testGenerateSchemaWithChoiceAndSimpleContent() throws IOException {
        SchemaGeneratorForXSD schemaGenerator = new SchemaGeneratorForXSD();
        String jsonSchema = schemaGenerator.getSchemaResourcePath(
                "src/test/resources/schemagen/xsd/xsdSchemas/sampleWithChoiceAndSimpleContent.xsd", FileType.XSD, ",");
        String expectedSchema = new String(Files.readAllBytes(
                Paths.get("src/test/resources/schemagen/xsd/expectedJsonSchema/sampleWithChoiceAndSimpleContent.json")));
        assertEquals(removeNewLinesAndSpaces(expectedSchema), removeNewLinesAndSpaces(jsonSchema));
    }

    @Test
    public void testGenerateSchemaWithChoiceInNestedObjects() throws IOException {
        SchemaGeneratorForXSD schemaGenerator = new SchemaGeneratorForXSD();
        String jsonSchema = schemaGenerator.getSchemaResourcePath(
                "src/test/resources/schemagen/xsd/xsdSchemas/sampleWithChoiceInNestedObjects.xsd", FileType.XSD, ",");
        String expectedSchema = new String(Files.readAllBytes(
                Paths.get("src/test/resources/schemagen/xsd/expectedJsonSchema/sampleWithChoiceInNestedObjects.json")));
        assertEquals(removeNewLinesAndSpaces(expectedSchema), removeNewLinesAndSpaces(jsonSchema));
    }

    @Test
    public void testGenerateSchemaWithChoiceInsideChoices() throws IOException {
        SchemaGeneratorForXSD schemaGenerator = new SchemaGeneratorForXSD();
        String jsonSchema = schemaGenerator.getSchemaResourcePath(
                "src/test/resources/schemagen/xsd/xsdSchemas/sampleWithChoiceInsideChoices.xsd", FileType.XSD, ",");
        String expectedSchema = new String(Files.readAllBytes(
                Paths.get("src/test/resources/schemagen/xsd/expectedJsonSchema/sampleWithChoiceInsideChoices.json")));
        assertEquals(removeNewLinesAndSpaces(expectedSchema), removeNewLinesAndSpaces(jsonSchema));
    }

    @Test
    public void testGenerateSchemaWithChoiceObjectInArray() throws IOException {
        SchemaGeneratorForXSD schemaGenerator = new SchemaGeneratorForXSD();
        String jsonSchema = schemaGenerator.getSchemaResourcePath(
                "src/test/resources/schemagen/xsd/xsdSchemas/sampleWithChoiceObjectInArray.xsd", FileType.XSD, ",");
        String expectedSchema = new String(Files.readAllBytes(
                Paths.get("src/test/resources/schemagen/xsd/expectedJsonSchema/sampleWithChoiceObjectInArray.json")));
        assertEquals(removeNewLinesAndSpaces(expectedSchema), removeNewLinesAndSpaces(jsonSchema));
    }

    @Test
    public void testGenerateSchemaWithChoicesAsObjects() throws IOException {
        SchemaGeneratorForXSD schemaGenerator = new SchemaGeneratorForXSD();
        String jsonSchema = schemaGenerator.getSchemaResourcePath(
                "src/test/resources/schemagen/xsd/xsdSchemas/sampleWithChoicesAsObjects.xsd", FileType.XSD, ",");
        String expectedSchema = new String(Files.readAllBytes(
                Paths.get("src/test/resources/schemagen/xsd/expectedJsonSchema/sampleWithChoicesAsObjects.json")));
        assertEquals(removeNewLinesAndSpaces(expectedSchema), removeNewLinesAndSpaces(jsonSchema));
    }

    @Test
    public void testGenerateSchemaWithComplexContent() throws IOException {
        SchemaGeneratorForXSD schemaGenerator = new SchemaGeneratorForXSD();
        String jsonSchema = schemaGenerator.getSchemaResourcePath(
                "src/test/resources/schemagen/xsd/xsdSchemas/sampleWithComplexContent.xsd", FileType.XSD, ",");
        String expectedSchema = new String(Files.readAllBytes(
                Paths.get("src/test/resources/schemagen/xsd/expectedJsonSchema/sampleWithComplexContent.json")));
        assertEquals(removeNewLinesAndSpaces(expectedSchema), removeNewLinesAndSpaces(jsonSchema));
    }

    @Test
    public void testGenerateSchemaWithComplexContentInArray() throws IOException {
        SchemaGeneratorForXSD schemaGenerator = new SchemaGeneratorForXSD();
        String jsonSchema = schemaGenerator.getSchemaResourcePath(
                "src/test/resources/schemagen/xsd/xsdSchemas/sampleWithComplexContentInArray.xsd", FileType.XSD, ",");
        String expectedSchema = new String(Files.readAllBytes(
                Paths.get("src/test/resources/schemagen/xsd/expectedJsonSchema/sampleWithComplexContentInArray.json")));
        assertEquals(removeNewLinesAndSpaces(expectedSchema), removeNewLinesAndSpaces(jsonSchema));
    }

    @Test
    public void testGenerateSchemaWithSimpleTypeInArray() throws IOException {
        SchemaGeneratorForXSD schemaGenerator = new SchemaGeneratorForXSD();
        String jsonSchema = schemaGenerator.getSchemaResourcePath(
                "src/test/resources/schemagen/xsd/xsdSchemas/sampleWithSimpleTypeInArray.xsd", FileType.XSD, ",");
        String expectedSchema = new String(Files.readAllBytes(
                Paths.get("src/test/resources/schemagen/xsd/expectedJsonSchema/sampleWithSimpleTypeInArray.json")));
        assertEquals(removeNewLinesAndSpaces(expectedSchema), removeNewLinesAndSpaces(jsonSchema));
    }

    @Test
    public void testGenerateSchemaWithSingleElement() throws IOException {
        SchemaGeneratorForXSD schemaGenerator = new SchemaGeneratorForXSD();
        String jsonSchema = schemaGenerator.getSchemaResourcePath(
                "src/test/resources/schemagen/xsd/xsdSchemas/sampleWithSingleElement.xsd", FileType.XSD, ",");
        String expectedSchema = new String(Files.readAllBytes(
                Paths.get("src/test/resources/schemagen/xsd/expectedJsonSchema/sampleWithSingleElement.json")));
        assertEquals(removeNewLinesAndSpaces(expectedSchema), removeNewLinesAndSpaces(jsonSchema));
    }

    @Test
    public void testGenerateSchemaWithUnionTypes() throws IOException {
        SchemaGeneratorForXSD schemaGenerator = new SchemaGeneratorForXSD();
        String jsonSchema = schemaGenerator.getSchemaResourcePath(
                "src/test/resources/schemagen/xsd/xsdSchemas/sampleWithUnionTypes.xsd", FileType.XSD, ",");
        String expectedSchema = new String(Files.readAllBytes(
                Paths.get("src/test/resources/schemagen/xsd/expectedJsonSchema/sampleWithUnionTypes.json")));
        assertEquals(removeNewLinesAndSpaces(expectedSchema), removeNewLinesAndSpaces(jsonSchema));
    }

    @Test
    public void testGenerateSchemaWithUniqueElement() throws IOException {
        SchemaGeneratorForXSD schemaGenerator = new SchemaGeneratorForXSD();
        String jsonSchema = schemaGenerator.getSchemaResourcePath(
                "src/test/resources/schemagen/xsd/xsdSchemas/sampleWithUniqueElement.xsd", FileType.XSD, ",");
        String expectedSchema = new String(Files.readAllBytes(
                Paths.get("src/test/resources/schemagen/xsd/expectedJsonSchema/sampleWithUniqueElement.json")));
        assertEquals(removeNewLinesAndSpaces(expectedSchema), removeNewLinesAndSpaces(jsonSchema));
    }

    public String removeNewLinesAndSpaces(String input) {
        if (input == null) {
            return null;
        }
        return input.replaceAll("\\s+", "");
    }

    public String removeOnlySpaces(String input) {
        if (input == null) {
            return null;
        }
        return input.replace(" ", "");
    }
}
