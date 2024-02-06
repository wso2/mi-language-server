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

package org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.endpoint;

import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.STNode;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.endpoint.common.EndpointEnableAddressing;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.endpoint.common.EndpointEnableRM;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.endpoint.common.EndpointEnableSec;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.endpoint.common.EndpointMarkForSuspension;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.endpoint.common.EndpointSuspendOnFailure;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.endpoint.common.EndpointTimeout;

public class DefaultEndpoint extends STNode {

    EndpointEnableSec enableSec;
    EndpointEnableRM enableRM;
    EndpointEnableAddressing enableAddressing;
    EndpointTimeout timeout;
    EndpointSuspendOnFailure suspendOnFailure;
    EndpointMarkForSuspension markForSuspension;
    String format;
    String optimize;
    String encoding;
    String statistics;
    String trace;

    public EndpointEnableSec getEnableSec() {

        return enableSec;
    }

    public void setEnableSec(EndpointEnableSec enableSec) {

        this.enableSec = enableSec;
    }

    public EndpointEnableRM getEnableRM() {

        return enableRM;
    }

    public void setEnableRM(EndpointEnableRM enableRM) {

        this.enableRM = enableRM;
    }

    public EndpointEnableAddressing getEnableAddressing() {

        return enableAddressing;
    }

    public void setEnableAddressing(EndpointEnableAddressing enableAddressing) {

        this.enableAddressing = enableAddressing;
    }

    public EndpointTimeout getTimeout() {

        return timeout;
    }

    public void setTimeout(EndpointTimeout timeout) {

        this.timeout = timeout;
    }

    public EndpointSuspendOnFailure getSuspendOnFailure() {

        return suspendOnFailure;
    }

    public void setSuspendOnFailure(EndpointSuspendOnFailure suspendOnFailure) {

        this.suspendOnFailure = suspendOnFailure;
    }

    public EndpointMarkForSuspension getMarkForSuspension() {

        return markForSuspension;
    }

    public void setMarkForSuspension(EndpointMarkForSuspension markForSuspension) {

        this.markForSuspension = markForSuspension;
    }

    public String getFormat() {

        return format;
    }

    public void setFormat(String format) {

        this.format = format;
    }

    public String getOptimize() {

        return optimize;
    }

    public void setOptimize(String optimize) {

        this.optimize = optimize;
    }

    public String getEncoding() {

        return encoding;
    }

    public void setEncoding(String encoding) {

        this.encoding = encoding;
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