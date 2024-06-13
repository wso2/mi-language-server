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

package org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.test.mockservice;

import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.STNode;

public class MockServiceResource extends STNode {

    STNode subContext;
    STNode method;
    MockServiceResourceRequest request;
    MockServiceResourceResponse response;

    public STNode getSubContext() {

        return subContext;
    }

    public void setSubContext(STNode subContext) {

        this.subContext = subContext;
    }

    public STNode getMethod() {

        return method;
    }

    public void setMethod(STNode method) {

        this.method = method;
    }

    public MockServiceResourceRequest getRequest() {

        return request;
    }

    public void setRequest(MockServiceResourceRequest request) {

        this.request = request;
    }

    public MockServiceResourceResponse getResponse() {

        return response;
    }

    public void setResponse(MockServiceResourceResponse response) {

        this.response = response;
    }
}
