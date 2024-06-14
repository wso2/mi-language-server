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

public class MockService extends STNode {

    STNode serviceName;
    STNode port;
    STNode context;
    MockServiceResources resources;

    public STNode getServiceName() {

        return serviceName;
    }

    public void setServiceName(STNode serviceName) {

        this.serviceName = serviceName;
    }

    public STNode getPort() {

        return port;
    }

    public void setPort(STNode port) {

        this.port = port;
    }

    public STNode getContext() {

        return context;
    }

    public void setContext(STNode context) {

        this.context = context;
    }

    public MockServiceResources getResources() {

        return resources;
    }

    public void setResources(MockServiceResources resources) {

        this.resources = resources;
    }
}
