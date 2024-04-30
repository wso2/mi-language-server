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

import java.io.File;
import java.io.IOException;

// import org.json.Schema;
import org.apache.commons.io.FileUtils;

public class AbstractSchemaGenerator implements ISchemaGenerator {

	/* (non-Javadoc)
	 * @see org.eclipse.lemminx.customservice.synapse.schemagen.util.ISchemaGenerator#getAvroSchema(java.lang.String)
	 */
	@Override
	public String getSchemaResourcePath(String filePath, FileType type, String delimiter) throws IOException {

		String entireFileText = FileUtils.readFileToString(new File(filePath));
		//Schema schema = Schema.parse(entireFileText);
		//return schema.toString();
		return entireFileText;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.lemminx.customservice.synapse.schemagen.util.ISchemaGenerator#getAvroSchemaContent(java.lang.String)
	 */
	@Override
	public String getSchemaContent(String fileText, FileType type, String delimiter) throws IOException {
		// Schema schema = Schema.parse(fileText);//TODO remove the deprecated method usage
		return fileText;
	}
}
