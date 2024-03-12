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

import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.Mediator;

public class Cache extends Mediator {

    CacheOnCacheHit onCacheHit;
    CacheProtocol protocol;
    CacheImplementation implementation;
    String id;
    int timeout;
    boolean collector;
    int maxMessageSize;
    String scope;
    String hashGenerator;
    String description;

    public String getId() {

        return id;
    }

    public void setId(String id) {

        this.id = id;
    }

    public CacheOnCacheHit getOnCacheHit() {

        return onCacheHit;
    }

    public void setOnCacheHit(CacheOnCacheHit onCacheHit) {

        this.onCacheHit = onCacheHit;
    }

    public CacheProtocol getProtocol() {

        return protocol;
    }

    public void setProtocol(CacheProtocol protocol) {

        this.protocol = protocol;
    }

    public CacheImplementation getImplementation() {

        return implementation;
    }

    public void setImplementation(CacheImplementation implementation) {

        this.implementation = implementation;
    }

    public int getTimeout() {

        return timeout;
    }

    public void setTimeout(int timeout) {

        this.timeout = timeout;
    }

    public boolean isCollector() {

        return collector;
    }

    public void setCollector(boolean collector) {

        this.collector = collector;
    }

    public int getMaxMessageSize() {

        return maxMessageSize;
    }

    public void setMaxMessageSize(int maxMessageSize) {

        this.maxMessageSize = maxMessageSize;
    }

    public String getScope() {

        return scope;
    }

    public void setScope(String scope) {

        this.scope = scope;
    }

    public String getHashGenerator() {

        return hashGenerator;
    }

    public void setHashGenerator(String hashGenerator) {

        this.hashGenerator = hashGenerator;
    }

    public String getDescription() {

        return description;
    }

    public void setDescription(String description) {

        this.description = description;
    }
}
