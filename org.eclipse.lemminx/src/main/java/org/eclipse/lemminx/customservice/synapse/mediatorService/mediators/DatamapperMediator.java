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

package org.eclipse.lemminx.customservice.synapse.mediatorService.mediators;

import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.transformation.Datamapper;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DatamapperMediator {
    public static Either<Map<String, Object>, Map<Range, Map<String, Object>>> processData430(Map<String, Object> data,
                                                                                              Datamapper datamapper,
                                                                                              List<String> dirtyFields) {
        String configurationLocalPath = "gov:/datamapper/" + data.get("name") + "/" +data.get("name")+ "/.dmc";
        String inputSchemaLocalPath = "gov:/datamapper/" + data.get("name") + "/" +data.get("name")+ "_inputSchema.json";
        String outputSchemaLocalPath = "gov:/datamapper/" + data.get("name") + "/" +data.get("name")+ "_outputSchema.json";
        data.put("configurationLocalPath", configurationLocalPath);
        data.put("inputSchemaLocalPath", inputSchemaLocalPath);
        data.put("outputSchemaLocalPath", outputSchemaLocalPath);
        return Either.forLeft(data);

    }

    public static Map<String, Object> getDataFromST430(Datamapper node) {

        Map<String, Object> data = new HashMap<>();
        data.put("description", node.getDescription());
        data.put("inputType", node.getInputType());
        data.put("outputType", node.getOutputType());
        String configPath = node.getConfig();
        Pattern pattern = Pattern.compile("gov:/datamapper/([^/]+)/.*\\.dmc");
        Matcher matcher = pattern.matcher(configPath);
        if (matcher.find()) {
            data.put("name", matcher.group(1));
        }
        return data;
    }
}
