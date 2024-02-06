/*
 * Copyright (c) 2023, WSO2 LLC. (http://www.wso2.com).
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

package org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.dataservice;

import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.STNode;

public class Resource extends STNode {

    STNode description;
    CallQuery callQuery;
    String path;
    String method;
    boolean disableStreaming;
    boolean returnRequestStatus;

    public STNode getDescription() {

        return description;
    }

    public void setDescription(STNode description) {

        this.description = description;
    }

    public CallQuery getCallQuery() {

        return callQuery;
    }

    public void setCallQuery(CallQuery callQuery) {

        this.callQuery = callQuery;
    }

    public String getPath() {

        return path;
    }

    public void setPath(String path) {

        this.path = path;
    }

    public String getMethod() {

        return method;
    }

    public void setMethod(String method) {

        this.method = method;
    }

    public boolean isDisableStreaming() {

        return disableStreaming;
    }

    public void setDisableStreaming(boolean disableStreaming) {

        this.disableStreaming = disableStreaming;
    }

    public boolean isReturnRequestStatus() {

        return returnRequestStatus;
    }

    public void setReturnRequestStatus(boolean returnRequestStatus) {

        this.returnRequestStatus = returnRequestStatus;
    }
}
