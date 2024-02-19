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

package org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.advanced.cache;

import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.STNode;

public class CacheProtocol extends STNode {

    STNode methods;
    STNode headersToExcludeInHash;
    STNode headersToIncludeInHash;
    STNode responseCodes;
    STNode enableCacheControl;
    STNode includeAgeHeader;
    STNode hashGenerator;
    String type;

    public STNode getMethods() {

        return methods;
    }

    public void setMethods(STNode methods) {

        this.methods = methods;
    }

    public STNode getHeadersToExcludeInHash() {

        return headersToExcludeInHash;
    }

    public void setHeadersToExcludeInHash(STNode headersToExcludeInHash) {

        this.headersToExcludeInHash = headersToExcludeInHash;
    }

    public STNode getHeadersToIncludeInHash() {

        return headersToIncludeInHash;
    }

    public void setHeadersToIncludeInHash(STNode headersToIncludeInHash) {

        this.headersToIncludeInHash = headersToIncludeInHash;
    }

    public STNode getResponseCodes() {

        return responseCodes;
    }

    public void setResponseCodes(STNode responseCodes) {

        this.responseCodes = responseCodes;
    }

    public STNode getEnableCacheControl() {

        return enableCacheControl;
    }

    public void setEnableCacheControl(STNode enableCacheControl) {

        this.enableCacheControl = enableCacheControl;
    }

    public STNode getIncludeAgeHeader() {

        return includeAgeHeader;
    }

    public void setIncludeAgeHeader(STNode includeAgeHeader) {

        this.includeAgeHeader = includeAgeHeader;
    }

    public STNode getHashGenerator() {

        return hashGenerator;
    }

    public void setHashGenerator(STNode hashGenerator) {

        this.hashGenerator = hashGenerator;
    }

    public String getType() {

        return type;
    }

    public void setType(String type) {

        this.type = type;
    }
}
