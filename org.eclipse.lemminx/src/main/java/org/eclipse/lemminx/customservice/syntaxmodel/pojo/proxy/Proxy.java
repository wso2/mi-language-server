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

package org.eclipse.lemminx.customservice.syntaxmodel.pojo.proxy;

import org.eclipse.lemminx.customservice.syntaxmodel.pojo.STNode;

import java.util.List;

public class Proxy extends STNode {

    List<STNode> descriptionOrTargetOrPublishWSDL;
    String name;
    String transports;
    String pinnedServers;
    String serviceGroup;
    boolean startOnLoad;
    String statistics;
    String trace;

    public List<STNode> getDescriptionOrTargetOrPublishWSDL() {

        return descriptionOrTargetOrPublishWSDL;
    }

    public void setDescriptionOrTargetOrPublishWSDL(List<STNode> descriptionOrTargetOrPublishWSDL) {

        this.descriptionOrTargetOrPublishWSDL = descriptionOrTargetOrPublishWSDL;
    }

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
}