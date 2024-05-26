/*
 * Copyright (c) 2024, WSO2 LLC. (http://www.wso2.com).
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

package org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.mediator.qos;

import org.apache.axiom.om.OMElement;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.Mediator;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.advanced.cache.Cache;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.advanced.cache.CacheImplementation;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.advanced.cache.CacheOnCacheHit;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.advanced.cache.CacheProtocol;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.InlineSequenceSerializer;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.mediator.AbstractMediatorSerializer;

public class CacheMediatorSerializer extends AbstractMediatorSerializer {

    @Override
    protected OMElement serializeSpecificMediator(Mediator m) {

        Cache cacheMediator = (Cache) m;
        OMElement cacheElt = fac.createOMElement("cache", synNS);

        if (cacheMediator.isCollector()) {
            cacheElt.addAttribute("collector", "true", null);
        } else {
            cacheElt.addAttribute("collector", "false", null);
            if (cacheMediator.getId() != null) {
                cacheElt.addAttribute("id", cacheMediator.getId(), null);
            }
            if (cacheMediator.getScope() != null) {
                cacheElt.addAttribute("scope", cacheMediator.getScope().getValue(), null);
            }
            if (cacheMediator.getHashGenerator() != null) {
                cacheElt.addAttribute("hashGenerator", cacheMediator.getHashGenerator(), null);
            }
            if (cacheMediator.getTimeout() >= 0) {
                cacheElt.addAttribute("timeout", Integer.toString(cacheMediator.getTimeout()), null);
            }
            if (cacheMediator.getMaxMessageSize() >= 0) {
                cacheElt.addAttribute("maxMessageSize", Integer.toString(cacheMediator.getMaxMessageSize()), null);
            }
            serializeOnCacheHit(cacheElt, cacheMediator.getOnCacheHit());
            serializeProtocol(cacheElt, cacheMediator.getProtocol());
            serializeImplementation(cacheElt, cacheMediator.getImplementation());
        }

        if (cacheMediator.getDescription() != null) {
            cacheElt.addAttribute("description", cacheMediator.getDescription(), null);
        }
        return cacheElt;
    }

    private void serializeOnCacheHit(OMElement cacheElt, CacheOnCacheHit onCacheHit) {

        if (onCacheHit != null) {
            OMElement onCacheHitElt = fac.createOMElement("onCacheHit", synNS);
            if (onCacheHit.getSequence() != null) {
                onCacheHitElt.addAttribute("sequence", onCacheHit.getSequence(), null);
            } else if (onCacheHit.getMediatorList() != null) {
                onCacheHitElt = InlineSequenceSerializer.serializeAnonymousSequence(onCacheHit.getMediatorList());
                onCacheHitElt.setLocalName("onCacheHit");
            }
            cacheElt.addChild(onCacheHitElt);
        }
    }

    private void serializeProtocol(OMElement cacheElt, CacheProtocol protocol) {

        if (protocol != null) {
            OMElement protocolElt = fac.createOMElement("protocol", synNS);
            if (protocol.getType() != null) {
                protocolElt.addAttribute("type", protocol.getType(), null);
            }
            if (protocol.getMethods() != null) {
                OMElement methodsElt = fac.createOMElement("methods", synNS);
                methodsElt.setText(protocol.getMethods().getTextNode());
                protocolElt.addChild(methodsElt);
            }
            if (protocol.getHeadersToExcludeInHash() != null) {
                OMElement headersElt = fac.createOMElement("headersToExcludeInHash", synNS);
                headersElt.setText(protocol.getHeadersToExcludeInHash().getTextNode());
                protocolElt.addChild(headersElt);
            }
            if (protocol.getHeadersToIncludeInHash() != null) {
                OMElement headersElt = fac.createOMElement("headersToIncludeInHash", synNS);
                headersElt.setText(protocol.getHeadersToIncludeInHash().getTextNode());
                protocolElt.addChild(headersElt);
            }
            if (protocol.getResponseCodes() != null) {
                OMElement responseCodesElt = fac.createOMElement("responseCodes", synNS);
                responseCodesElt.setText(protocol.getResponseCodes().getTextNode());
                protocolElt.addChild(responseCodesElt);
            }
            if (protocol.getEnableCacheControl() != null) {
                OMElement enableCacheControlElt = fac.createOMElement("enableCacheControl", synNS);
                enableCacheControlElt.setText(protocol.getEnableCacheControl().getTextNode());
                protocolElt.addChild(enableCacheControlElt);
            }
            if (protocol.getIncludeAgeHeader() != null) {
                OMElement includeAgeHeaderElt = fac.createOMElement("includeAgeHeader", synNS);
                includeAgeHeaderElt.setText(protocol.getIncludeAgeHeader().getTextNode());
                protocolElt.addChild(includeAgeHeaderElt);
            }
            if (protocol.getHashGenerator() != null) {
                OMElement hashGeneratorElt = fac.createOMElement("hashGenerator", synNS);
                hashGeneratorElt.setText(protocol.getHashGenerator().getTextNode());
                protocolElt.addChild(hashGeneratorElt);
            }
            cacheElt.addChild(protocolElt);
        }
    }

    private void serializeImplementation(OMElement cacheElt, CacheImplementation implementation) {

        if (implementation != null) {
            OMElement implElt = fac.createOMElement("implementation", synNS);
            if (implementation.getMaxSize() >= 0) {
                implElt.addAttribute("maxSize", Integer.toString(implementation.getMaxSize()), null);
            }
            if (implementation.getType() != null) {
                implElt.addAttribute("type", implementation.getType().name(), null);
            }
            cacheElt.addChild(implElt);
        }
    }

    @Override
    public String getMediatorClassName() {

        return Cache.class.getName();
    }
}
