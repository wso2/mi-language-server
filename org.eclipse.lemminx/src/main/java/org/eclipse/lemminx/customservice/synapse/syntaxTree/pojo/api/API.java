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

package org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.api;

import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.STNode;

public class API extends STNode {

    APIResource[] resource;
    APIHandlers handlers;
    String name;
    String context;
    String hostname;
    String port;
    String version;
    ApiVersionType versionType;
    String publishSwagger;
    String description;
    EnableDisable statistics;
    EnableDisable trace;

    public APIResource[] getResource() {

        return resource;
    }

    public void setResource(APIResource[] resource) {

        this.resource = resource;
    }

    public APIHandlers getHandlers() {

        return handlers;
    }

    public void setHandlers(APIHandlers handlers) {

        this.handlers = handlers;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public String getContext() {

        return context;
    }

    public void setContext(String context) {

        this.context = context;
    }

    public String getHostname() {

        return hostname;
    }

    public void setHostname(String hostname) {

        this.hostname = hostname;
    }

    public String getPort() {

        return port;
    }

    public void setPort(String port) {

        this.port = port;
    }

    public String getVersion() {

        return version;
    }

    public void setVersion(String version) {

        this.version = version;
    }

    public ApiVersionType getVersionType() {

        return versionType;
    }

    public void setVersionType(ApiVersionType versionType) {

        this.versionType = versionType;
    }

    public String getPublishSwagger() {

        return publishSwagger;
    }

    public void setPublishSwagger(String publishSwagger) {

        this.publishSwagger = publishSwagger;
    }

    public String getDescription() {

        return description;
    }

    public void setDescription(String description) {

        this.description = description;
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
