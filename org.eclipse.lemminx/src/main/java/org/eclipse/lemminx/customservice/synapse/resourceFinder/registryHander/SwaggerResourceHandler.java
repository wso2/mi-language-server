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

package org.eclipse.lemminx.customservice.synapse.resourceFinder.registryHander;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.eclipse.lemminx.customservice.synapse.resourceFinder.pojo.Resource;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class SwaggerResourceHandler extends NonXMLRegistryHandler {

    public SwaggerResourceHandler(List<Resource> resources) {

        super(resources);
    }

    @Override
    protected boolean canHandle(File file) {

        ObjectMapper objectMapper;
        Map<String, Object> content;

        if (file.getAbsolutePath().endsWith(".yaml") || file.getAbsolutePath().endsWith(".yml")) {
            objectMapper = new ObjectMapper(new YAMLFactory());
        } else if (file.getAbsolutePath().endsWith(".json")) {
            objectMapper = new ObjectMapper();
        } else {
            return false;
        }
        try {
            content = objectMapper.readValue(file, Map.class);
            return isSwaggerContent(content);
        } catch (IOException e) {
        }
        return false;
    }

    private boolean isSwaggerContent(Map<String, Object> content) {

        if (content.containsKey("swagger") && "2.0".equals(content.get("swagger"))) {
            return true;
        }
        if (content.containsKey("openapi") && content.get("openapi").toString().startsWith("3.")) {
            return true;
        }
        return content.containsKey("info") && content.containsKey("paths");
    }
}
