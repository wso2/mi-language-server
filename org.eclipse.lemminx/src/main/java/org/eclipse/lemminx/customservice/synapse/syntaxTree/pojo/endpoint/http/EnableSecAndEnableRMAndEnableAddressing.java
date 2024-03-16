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
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.endpoint.common.EndpointEnableAddressing;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.endpoint.common.EndpointEnableRM;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.endpoint.common.EndpointEnableSec;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.endpoint.common.EndpointMarkForSuspension;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.endpoint.common.EndpointRetryConfig;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.endpoint.common.EndpointSuspendOnFailure;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.endpoint.common.EndpointTimeout;

import java.util.Optional;

public class EnableSecAndEnableRMAndEnableAddressing extends STNode {

    Optional<EndpointEnableSec> enableSec;
    Optional<EndpointEnableRM> enableRM;
    Optional<EndpointEnableAddressing> enableAddressing;
    Optional<EndpointTimeout> timeout;
    Optional<EndpointSuspendOnFailure> suspendOnFailure;
    Optional<EndpointMarkForSuspension> markForSuspension;
    Optional<EndpointHttpAuthentication> authentication;
    Optional<EndpointRetryConfig> retryConfig;

    public Optional<EndpointEnableSec> getEnableSec() {

        return enableSec;
    }

    public void setEnableSec(Optional<EndpointEnableSec> enableSec) {

        this.enableSec = enableSec;
    }

    public Optional<EndpointEnableRM> getEnableRM() {

        return enableRM;
    }

    public void setEnableRM(Optional<EndpointEnableRM> enableRM) {

        this.enableRM = enableRM;
    }

    public Optional<EndpointEnableAddressing> getEnableAddressing() {

        return enableAddressing;
    }

    public void setEnableAddressing(Optional<EndpointEnableAddressing> enableAddressing) {

        this.enableAddressing = enableAddressing;
    }

    public Optional<EndpointTimeout> getTimeout() {

        return timeout;
    }

    public void setTimeout(Optional<EndpointTimeout> timeout) {

        this.timeout = timeout;
    }

    public Optional<EndpointSuspendOnFailure> getSuspendOnFailure() {

        return suspendOnFailure;
    }

    public void setSuspendOnFailure(Optional<EndpointSuspendOnFailure> suspendOnFailure) {

        this.suspendOnFailure = suspendOnFailure;
    }

    public Optional<EndpointMarkForSuspension> getMarkForSuspension() {

        return markForSuspension;
    }

    public void setMarkForSuspension(Optional<EndpointMarkForSuspension> markForSuspension) {

        this.markForSuspension = markForSuspension;
    }

    public Optional<EndpointHttpAuthentication> getAuthentication() {

        return authentication;
    }

    public void setAuthentication(Optional<EndpointHttpAuthentication> authentication) {

        this.authentication = authentication;
    }

    public Optional<EndpointRetryConfig> getRetryConfig() {

        return retryConfig;
    }

    public void setRetryConfig(Optional<EndpointRetryConfig> retryConfig) {

        this.retryConfig = retryConfig;
    }
}
