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

package org.eclipse.lemminx.customservice.synapse.syntaxTree.factory;

import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.misc.SequenceFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.STNode;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.api.APIResource;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.misc.common.Sequence;
import org.eclipse.lemminx.customservice.synapse.utils.Constant;
import org.eclipse.lemminx.dom.DOMElement;
import org.eclipse.lemminx.dom.DOMNode;

import java.util.List;
import java.util.Objects;

public class ResourceFactory extends AbstractFactory {

    private String apiName;

    public ResourceFactory(String apiName) {

        this.apiName = apiName;
    }

    @Override
    public STNode create(DOMElement element) {

        APIResource apiResource = new APIResource();
        apiResource.elementNode(element);
        apiResource.setApi(apiName);
        populateAttributes(apiResource, element);
        List<DOMNode> children = element.getChildren();

        if (Objects.nonNull(children)) {
            for (DOMNode node : children) {
                if (!(node instanceof DOMElement)) {
                    continue;
                }
                STNode sequence = createSequence(node);
                String name = node.getNodeName();
                if (name.equalsIgnoreCase(Constant.IN_SEQUENCE)) {
                    apiResource.setInSequence((Sequence) sequence);
                } else if (name.equalsIgnoreCase(Constant.OUT_SEQUENCE)) {
                    apiResource.setOutSequence((Sequence) sequence);
                } else if (name.equalsIgnoreCase(Constant.FAULT_SEQUENCE)) {
                    apiResource.setFaultSequence((Sequence) sequence);
                }
            }
        }
        return apiResource;
    }

    @Override
    public void populateAttributes(STNode node, DOMElement element) {

        APIResource apiResource = (APIResource) node;

        String urlMapping = element.getAttribute(Constant.URL_MAPPING);
        if (Objects.nonNull(urlMapping)) {
            apiResource.setUrlMapping(urlMapping);
        }
        String uriTemplate = element.getAttribute(Constant.URI_TEMPLATE);
        if (Objects.nonNull(uriTemplate)) {
            apiResource.setUriTemplate(uriTemplate);
        }
        String protocol = element.getAttribute(Constant.PROTOCOL);
        if (Objects.nonNull(protocol)) {
            apiResource.setProtocol(getProtocols(protocol));
        }
        String methods = element.getAttribute(Constant.METHODS);
        if (Objects.nonNull(methods)) {
            apiResource.setMethods(getMethods(methods));
        }
        String inSequence = element.getAttribute(Constant.IN_SEQUENCE);
        if (Objects.nonNull(inSequence)) {
            apiResource.setInSequenceAttribute(inSequence);
        }
        String outSequence = element.getAttribute(Constant.OUT_SEQUENCE);
        if (Objects.nonNull(outSequence)) {
            apiResource.setOutSequenceAttribute(outSequence);
        }
        String faultSequence = element.getAttribute(Constant.FAULT_SEQUENCE);
        if (Objects.nonNull(faultSequence)) {
            apiResource.setFaultSequenceAttribute(faultSequence);
        }
    }

    private STNode createSequence(DOMNode node) {

        SequenceFactory sequenceFactory = new SequenceFactory();
        STNode sequence = sequenceFactory.create((DOMElement) node);
        return sequence;
    }

    private String[] getProtocols(String value) {

        String[] protocols = value.split(Constant.SPACE);
        return protocols;
    }

    private String[] getMethods(String value) {

        String[] methods = value.split(Constant.SPACE);
        return methods;
    }
}
