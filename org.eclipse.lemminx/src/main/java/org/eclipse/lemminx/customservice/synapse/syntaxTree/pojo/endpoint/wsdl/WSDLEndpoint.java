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

package org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.endpoint.wsdl;

import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.STNode;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.api.EnableDisable;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.endpoint.Format;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.endpoint.Optimize;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.endpoint.common.EndpointEnableAddressing;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.endpoint.common.EndpointEnableRM;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.endpoint.common.EndpointEnableSec;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.endpoint.common.EndpointMarkForSuspension;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.endpoint.common.EndpointSuspendOnFailure;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.endpoint.common.EndpointTimeout;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.misc.wsdl11.TDefinitions;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.misc.wsdl20.DescriptionType;

public class WSDLEndpoint extends STNode {

    TDefinitions definitions;
    DescriptionType description;
    EndpointEnableSec enableSec;
    EndpointEnableRM enableRM;
    EndpointEnableAddressing enableAddressing;
    EndpointTimeout timeout;
    EndpointSuspendOnFailure suspendOnFailure;
    EndpointMarkForSuspension markForSuspension;
    String uri;
    String service;
    String port;
    Format format;
    Optimize optimize;
    String encoding;
    EnableDisable statistics;
    EnableDisable trace;

    public TDefinitions getDefinitions() {

        return definitions;
    }

    public void setDefinitions(TDefinitions definitions) {

        this.definitions = definitions;
    }

    public DescriptionType getDescription() {

        return description;
    }

    public void setDescription(DescriptionType description) {

        this.description = description;
    }

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

    public String getUri() {

        return uri;
    }

    public void setUri(String uri) {

        this.uri = uri;
    }

    public String getService() {

        return service;
    }

    public void setService(String service) {

        this.service = service;
    }

    public String getPort() {

        return port;
    }

    public void setPort(String port) {

        this.port = port;
    }

    public Format getFormat() {

        return format;
    }

    public void setFormat(Format format) {

        this.format = format;
    }

    public Optimize getOptimize() {

        return optimize;
    }

    public void setOptimize(Optimize optimize) {

        this.optimize = optimize;
    }

    public String getEncoding() {

        return encoding;
    }

    public void setEncoding(String encoding) {

        this.encoding = encoding;
    }

    public EnableDisable getStatistics() {

        return statistics;
    }

    public void setStatistics(EnableDisable statistics) {

        this.statistics = statistics;
    }

    public EnableDisable getTrace() {

        return trace;
    }

    public void setTrace(EnableDisable trace) {

        this.trace = trace;
    }
}