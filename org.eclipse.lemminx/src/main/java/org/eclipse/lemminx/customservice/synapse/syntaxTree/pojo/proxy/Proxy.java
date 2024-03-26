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

package org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.proxy;

import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.STNode;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.misc.common.Parameter;

public class Proxy extends STNode {

    ProxyTarget target;
    ProxyPublishWSDL publishWSDL;
    ProxyPolicy[] policies;
    STNode enableAddressing;
    STNode enableSec;
    STNode enableRM;
    Parameter[] parameters;
    String description;
    String name;
    String transports;
    String pinnedServers;
    String serviceGroup;
    boolean startOnLoad;
    String statistics;
    String trace;

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public String getTransports() {

        return transports;
    }

    public void setTransports(String transports) {

        this.transports = transports;
    }

    public String getPinnedServers() {

        return pinnedServers;
    }

    public void setPinnedServers(String pinnedServers) {

        this.pinnedServers = pinnedServers;
    }

    public String getServiceGroup() {

        return serviceGroup;
    }

    public void setServiceGroup(String serviceGroup) {

        this.serviceGroup = serviceGroup;
    }

    public boolean isStartOnLoad() {

        return startOnLoad;
    }

    public void setStartOnLoad(boolean startOnLoad) {

        this.startOnLoad = startOnLoad;
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

    public ProxyTarget getTarget() {

        return target;
    }

    public void setTarget(ProxyTarget target) {

        this.target = target;
    }

    public ProxyPublishWSDL getPublishWSDL() {

        return publishWSDL;
    }

    public void setPublishWSDL(ProxyPublishWSDL publishWSDL) {

        this.publishWSDL = publishWSDL;
    }

    public ProxyPolicy[] getPolicies() {

        return policies;
    }

    public void setPolicies(ProxyPolicy[] policies) {

        this.policies = policies;
    }

    public STNode getEnableAddressing() {

        return enableAddressing;
    }

    public void setEnableAddressing(STNode enableAddressing) {

        this.enableAddressing = enableAddressing;
    }

    public STNode getEnableSec() {

        return enableSec;
    }

    public void setEnableSec(STNode enableSec) {

        this.enableSec = enableSec;
    }

    public STNode getEnableRM() {

        return enableRM;
    }

    public void setEnableRM(STNode enableRM) {

        this.enableRM = enableRM;
    }

    public Parameter[] getParameters() {

        return parameters;
    }

    public void setParameters(Parameter[] parameters) {

        this.parameters = parameters;
    }

    public String getDescription() {

        return description;
    }

    public void setDescription(String description) {

        this.description = description;
    }
}
