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

package org.eclipse.lemminx.customservice.synapse.schemagen.util;

import java.io.IOException;

/**
 * TODO
 *
 */
public class SchemaGeneratorHelper {

	/**
	 * This method returns the generated schema as a string. It will load the
	 * schema generator class depending on the file type of the file and pass
	 * the file to the schema generator class which would generate the schema.
	 * 
	 * @param option
	 * @param filePath
	 * @return
	 */
	public String getSchemaContent(FileType option, String filePath, String delimiter) {

		SchemaGeneratorFactory schemaGenFactory = new SchemaGeneratorFactory();
		ISchemaGenerator schemaGenerator = schemaGenFactory.getSchemaGenerator(option);

		try {
			return schemaGenerator.getSchemaResourcePath(filePath, option, delimiter);
		} catch (IOException e) {
			// log.error("Error while generating schema", e);
		}
		
		return null;
	}

	public String getSchemaFromContent(FileType option, String fileContent, String delimiter) {

		SchemaGeneratorFactory schemaGenFactory = new SchemaGeneratorFactory();
		ISchemaGenerator schemaGenerator = schemaGenFactory.getSchemaGenerator(option);

		try {
			return schemaGenerator.getSchemaContent(fileContent, option, delimiter);
		} catch (IOException e) {
			// ignore
		}

		return null;
	}

}
