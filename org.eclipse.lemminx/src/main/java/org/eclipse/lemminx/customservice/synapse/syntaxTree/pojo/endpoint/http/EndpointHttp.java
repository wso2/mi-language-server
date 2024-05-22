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

package org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.endpoint.http;

import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.STNode;

public class EndpointHttp extends STNode {

    EnableSecAndEnableRMAndEnableAddressing enableSecAndEnableRMAndEnableAddressing;
    String uriTemplate;
    HttpMethod method;
    String statistics;
    String trace;

    public EnableSecAndEnableRMAndEnableAddressing getEnableSecAndEnableRMAndEnableAddressing() {

        return enableSecAndEnableRMAndEnableAddressing;
    }

    public void setEnableSecAndEnableRMAndEnableAddressing(EnableSecAndEnableRMAndEnableAddressing enableSecAndEnableRMAndEnableAddressing) {

        this.enableSecAndEnableRMAndEnableAddressing = enableSecAndEnableRMAndEnableAddressing;
    }

    public String getUriTemplate() {

        return uriTemplate;
    }

    public void setUriTemplate(String uriTemplate) {

        this.uriTemplate = uriTemplate;
    }

    public HttpMethod getMethod() {

        return method;
    }

    public void setMethod(HttpMethod method) {

        this.method = method;
    }

    public String getStatistics() {

        return statistics;
    }

    public void setStatistics(String statistics) {

        this.statistics = statistics;
    }

    public String getTrace() {

        return trace;
    }

    public void setTrace(String trace) {

        this.trace = trace;
    }
}