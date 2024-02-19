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

package org.eclipse.lemminx.customservice.synapse.syntaxTree;

import com.google.gson.JsonElement;

public class SyntaxTreeResponse {

    private JsonElement syntaxTree;
    private String defFilePath;

    public JsonElement getSyntaxTree() {

        return syntaxTree;
    }

    public void setSyntaxTree(JsonElement syntaxTree) {

        this.syntaxTree = syntaxTree;
    }

    public void setDefFilePath(String defFilePath) {

        this.defFilePath = defFilePath;
    }

    public String getDefFilePath() {

        return defFilePath;
    }

    public SyntaxTreeResponse(JsonElement syntaxTree, String defFilePath) {

        this.syntaxTree = syntaxTree;
        this.defFilePath = defFilePath;
    }
}
