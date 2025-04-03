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

package org.eclipse.lemminx.customservice.synapse.mediator.tryout.pojo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MediatorTryoutRequest {

    private final String file;
    private String tryoutId;
    private final int line;
    private final int column;
    private final String contentType;
    private final String inputPayload;
    private final List<Property> queryParams;
    private final List<Property> pathParams;
    private final Edit[] edits;
    private MediatorInfo mediatorInfo;
    private boolean isIsolatedTryout;

    public MediatorTryoutRequest(String file, int line, int column, String inputPayload, Edit[] edits) {

        this(file, line, column, "application/json", inputPayload, edits);
    }

    public MediatorTryoutRequest(String file, int line, int column, String contentType, String inputPayload,
                                 Edit[] edits) {

        this.file = file;
        this.line = line;
        this.column = column;
        this.contentType = contentType;
        this.inputPayload = inputPayload;
        this.edits = edits;
        this.queryParams = new ArrayList<>();
        this.pathParams = new ArrayList<>();
    }

    public String getFile() {

        return file;
    }

    public int getLine() {

        return line;
    }

    public int getColumn() {

        return column;
    }

    public String getContentType() {

        return contentType;
    }

    public String getInputPayload() {

        return inputPayload;
    }

    public Edit[] getEdits() {

        if (edits == null) {
            return null;
        }
        return edits.clone();
    }

    public MediatorInfo getMediatorInfo() {

        return mediatorInfo;
    }

    public void setMediatorInfo(MediatorInfo mediatorInfo) {

        this.mediatorInfo = mediatorInfo;
    }

    public String getTryoutId() {

        return tryoutId;
    }

    public void setTryoutId(String tryoutId) {

        this.tryoutId = tryoutId;
    }

    public boolean isIsolatedTryout() {

        return isIsolatedTryout;
    }

    public List<Property> getQueryParams() {

        return queryParams;
    }

    public List<Property> getPathParams() {

        return pathParams;
    }

    @Override
    public String toString() {

        return "MediatorTryoutRequest{" +
                "file='" + file + '\'' +
                ", tryoutId='" + tryoutId + '\'' +
                ", line=" + line +
                ", column=" + column +
                ", inputPayload='" + inputPayload + '\'' +
                ", edits=" + Arrays.toString(edits) +
                ", mediatorInfo=" + mediatorInfo +
                ", isIsolatedTryout=" + isIsolatedTryout +
                '}';
    }
}
