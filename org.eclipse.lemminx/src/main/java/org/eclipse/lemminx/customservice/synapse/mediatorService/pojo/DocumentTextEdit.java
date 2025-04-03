/*
 * Copyright (c) 2025, WSO2 LLC. (http://www.wso2.com).
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

package org.eclipse.lemminx.customservice.synapse.mediatorService.pojo;

import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.TextEdit;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

public class DocumentTextEdit extends TextEdit {

    private String documentUri;
    private boolean isCreateNewFile;

    public DocumentTextEdit(@NonNull Range range, @NonNull String newText, String documentUri) {

        super(range, newText);
        this.documentUri = documentUri;
    }

    public String getDocumentUri() {

        return documentUri;
    }

    public void setDocumentUri(String documentUri) {

        this.documentUri = documentUri;
    }

    public boolean isCreateNewFile() {

        return isCreateNewFile;
    }

    public void setCreateNewFile(boolean createNewFile) {

        isCreateNewFile = createNewFile;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("DocumentUri: ").append(getDocumentUri()).append("\n");
        sb.append("Range: ").append(getRange()).append("\n");
        sb.append("NewText: ").append(getNewText()).append("\n");
        sb.append("IsCreateNewFile: ").append(isCreateNewFile()).append("\n");
        return sb.toString();
    }
}
