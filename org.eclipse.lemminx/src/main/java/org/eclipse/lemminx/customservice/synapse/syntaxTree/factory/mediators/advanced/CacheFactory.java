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

package org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.advanced;

import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.AbstractMediatorFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.STNode;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.Mediator;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.advanced.cache.Cache;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.advanced.cache.CacheImplementation;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.advanced.cache.CacheOnCacheHit;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.advanced.cache.CacheProtocol;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.utils.SyntaxTreeUtils;
import org.eclipse.lemminx.customservice.synapse.utils.Constant;
import org.eclipse.lemminx.customservice.synapse.utils.Utils;
import org.eclipse.lemminx.dom.DOMElement;
import org.eclipse.lemminx.dom.DOMNode;

import java.util.List;

public class CacheFactory extends AbstractMediatorFactory {

    private static final String CACHE_MEDIATOR = "cache";

    @Override
    public Mediator createSpecificMediator(DOMElement element) {

        Cache cacheMediator = new Cache();
        cacheMediator.elementNode(element);
        populateAttributes(cacheMediator, element);
        List<DOMNode> children = element.getChildren();
        for (DOMNode node : children) {
            if (node.getNodeName().equalsIgnoreCase(Constant.ON_CACHE_HIT)) {
                CacheOnCacheHit cacheOnCacheHit = createCacheOnCacheHit(node);
                cacheMediator.setOnCacheHit(cacheOnCacheHit);
            } else if (node.getNodeName().equalsIgnoreCase(Constant.PROTOCOL)) {
                CacheProtocol cacheProtocol = createCacheProtocol(node);
                cacheMediator.setProtocol(cacheProtocol);
            } else if (node.getNodeName().equalsIgnoreCase(Constant.IMPLEMENTATION)) {
                CacheImplementation cacheImplementation = createCacheImplementation(node);
                cacheMediator.setImplementation(cacheImplementation);
            }
        }
        return cacheMediator;
    }

    @Override
    public void populateAttributes(STNode node, DOMElement element) {

        Cache cacheMediator = (Cache) node;
        String id = element.getAttribute(Constant.ID);
        if (id != null) {
            cacheMediator.setId(id);
        }
        String timeout = element.getAttribute(Constant.TIMEOUT);
        if (timeout != null) {
            cacheMediator.setTimeout(Utils.parseInt(timeout));
        }
        String collector = element.getAttribute(Constant.COLLECTOR);
        if (collector != null) {
            cacheMediator.setCollector(Boolean.valueOf(collector));
        }
        String maxMessageSize = element.getAttribute(Constant.MAX_MESSAGE_SIZE);
        if (maxMessageSize != null) {
            cacheMediator.setMaxMessageSize(Utils.parseInt(maxMessageSize));
        }
        String scope = element.getAttribute(Constant.SCOPE);
        if (scope != null) {
            cacheMediator.setScope(scope);
        }
        String hashGenerator = element.getAttribute(Constant.HASH_GENERATOR);
        if (hashGenerator != null) {
            cacheMediator.setHashGenerator(hashGenerator);
        }
        String description = element.getAttribute(Constant.DESCRIPTION);
        if (description != null) {
            cacheMediator.setDescription(description);
        }
    }

    private CacheOnCacheHit createCacheOnCacheHit(DOMNode element) {

        CacheOnCacheHit cacheOnCacheHit = new CacheOnCacheHit();
        cacheOnCacheHit.elementNode((DOMElement) element);
        String sequence = element.getAttribute(Constant.SEQUENCE);
        if (sequence != null) {
            cacheOnCacheHit.setSequence(sequence);
        }
        List<DOMNode> children = element.getChildren();
        List<Mediator> mediators = SyntaxTreeUtils.createMediators(children);
        cacheOnCacheHit.setMediatorList(mediators);
        return cacheOnCacheHit;
    }

    private CacheProtocol createCacheProtocol(DOMNode element) {

        CacheProtocol cacheProtocol = new CacheProtocol();
        cacheProtocol.elementNode((DOMElement) element);
        populateCacheProtocolAttributes(cacheProtocol, element);
        List<DOMNode> children = element.getChildren();
        for (DOMNode node : children) {
            if (!(node instanceof DOMElement)) {
                continue;
            }
            String name = node.getNodeName();
            STNode stElement = new STNode();
            stElement.elementNode((DOMElement) node);
            if (name.equalsIgnoreCase(Constant.METHODS)) {
                cacheProtocol.setMethods(stElement);
            } else if (name.equalsIgnoreCase(Constant.HEADERS_TO_EXCLUDE_IN_HASH)) {
                cacheProtocol.setHeadersToExcludeInHash(stElement);
            } else if (name.equalsIgnoreCase(Constant.HEADERS_TO_INCLUDE_IN_HASH)) {
                cacheProtocol.setHeadersToIncludeInHash(stElement);
            } else if (name.equalsIgnoreCase(Constant.RESPONSE_CODES)) {
                cacheProtocol.setResponseCodes(stElement);
            } else if (name.equalsIgnoreCase(Constant.ENABLE_CACHE_CONTROL)) {
                cacheProtocol.setEnableCacheControl(stElement);
            } else if (name.equalsIgnoreCase(Constant.INCLUDE_AGE_HEADER)) {
                cacheProtocol.setIncludeAgeHeader(stElement);
            } else if (name.equalsIgnoreCase(Constant.HASH_GENERATOR)) {
                cacheProtocol.setHashGenerator(stElement);
            }
        }
        return cacheProtocol;
    }

    public void populateCacheProtocolAttributes(CacheProtocol cacheProtocol, DOMNode element) {

        String type = element.getAttribute(Constant.TYPE);
        if (type != null) {
            cacheProtocol.setType(type);
        }
    }

    private CacheImplementation createCacheImplementation(DOMNode element) {

        CacheImplementation cacheImplementation = new CacheImplementation();
        cacheImplementation.elementNode((DOMElement) element);
        String type = element.getAttribute(Constant.TYPE);
        if (type != null) {
            cacheImplementation.setType(type);
        }
        String maxSize = element.getAttribute(Constant.MAX_SIZE);
        if (maxSize != null) {
            cacheImplementation.setMaxSize(Utils.parseInt(maxSize));
        }
        return cacheImplementation;
    }

    @Override
    public String getTagName() {

        return CACHE_MEDIATOR;
    }
}
