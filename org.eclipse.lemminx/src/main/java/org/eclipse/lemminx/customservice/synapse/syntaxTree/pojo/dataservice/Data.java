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

import java.util.List;

public class Data extends STNode {

    List<STNode> descriptionOrConfigOrQuery;
    String baseURI;
    String name;
    boolean enableBatchRequests;
    boolean enableBoxcarring;
    boolean disableLegacyBoxcarringMode;
    boolean disableStreaming;
    String txManagerJNDIName;
    String serviceNamespace;
    String serviceGroup;
    String publishSwagger;
    String transports;
    String serviceStatus;

    public List<STNode> getDescriptionOrConfigOrQuery() {

        return descriptionOrConfigOrQuery;
    }

    public void setDescriptionOrConfigOrQuery(List<STNode> descriptionOrConfigOrQuery) {

        this.descriptionOrConfigOrQuery = descriptionOrConfigOrQuery;
    }

    public String getBaseURI() {

        return baseURI;
    }

    public void setBaseURI(String baseURI) {

        this.baseURI = baseURI;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public boolean isEnableBatchRequests() {

        return enableBatchRequests;
    }

    public void setEnableBatchRequests(boolean enableBatchRequests) {

        this.enableBatchRequests = enableBatchRequests;
    }

    public boolean isEnableBoxcarring() {

        return enableBoxcarring;
    }

    public void setEnableBoxcarring(boolean enableBoxcarring) {

        this.enableBoxcarring = enableBoxcarring;
    }

    public boolean isDisableLegacyBoxcarringMode() {

        return disableLegacyBoxcarringMode;
    }

    public void setDisableLegacyBoxcarringMode(boolean disableLegacyBoxcarringMode) {

        this.disableLegacyBoxcarringMode = disableLegacyBoxcarringMode;
    }

    public boolean isDisableStreaming() {

        return disableStreaming;
    }

    public void setDisableStreaming(boolean disableStreaming) {

        this.disableStreaming = disableStreaming;
    }

    public String getTxManagerJNDIName() {

        return txManagerJNDIName;
    }

    public void setTxManagerJNDIName(String txManagerJNDIName) {

        this.txManagerJNDIName = txManagerJNDIName;
    }

    public String getServiceNamespace() {

        return serviceNamespace;
    }

    public void setServiceNamespace(String serviceNamespace) {

        this.serviceNamespace = serviceNamespace;
    }

    public String getServiceGroup() {

        return serviceGroup;
    }

    public void setServiceGroup(String serviceGroup) {

        this.serviceGroup = serviceGroup;
    }

    public String getPublishSwagger() {

        return publishSwagger;
    }

    public void setPublishSwagger(String publishSwagger) {

        this.publishSwagger = publishSwagger;
    }

    public String getTransports() {

        return transports;
    }

    public void setTransports(String transports) {

        this.transports = transports;
    }

    public String getServiceStatus() {

        return serviceStatus;
    }

    public void setServiceStatus(String serviceStatus) {

        this.serviceStatus = serviceStatus;
    }
}
