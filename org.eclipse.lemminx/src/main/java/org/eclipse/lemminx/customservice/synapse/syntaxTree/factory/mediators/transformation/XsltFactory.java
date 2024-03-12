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

package org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.transformation;

import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.AbstractMediatorFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.STNode;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.Mediator;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.MediatorProperty;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.transformation.xslt.Xslt;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.transformation.xslt.XsltFeature;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.transformation.xslt.XsltResource;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.utils.SyntaxTreeUtils;
import org.eclipse.lemminx.customservice.synapse.utils.Constant;
import org.eclipse.lemminx.dom.DOMElement;
import org.eclipse.lemminx.dom.DOMNode;

import java.util.ArrayList;
import java.util.List;

public class XsltFactory extends AbstractMediatorFactory {

    private static final String XSLT = "xslt";

    @Override
    public Mediator createSpecificMediator(DOMElement element) {

        Xslt xslt = new Xslt();
        xslt.elementNode(element);
        populateAttributes(xslt, element);
        List<DOMNode> childNodes = element.getChildren();
        List<MediatorProperty> mediatorProperties = new ArrayList<>();
        List<XsltFeature> xsltFeatures = new ArrayList<>();
        List<XsltResource> xsltResources = new ArrayList<>();
        if (childNodes != null && !childNodes.isEmpty()) {
            for (DOMNode childNode : childNodes) {
                if (childNode.getNodeName().equalsIgnoreCase(Constant.PROPERTY)) {
                    MediatorProperty mediatorProperty = SyntaxTreeUtils.createMediatorProperty(childNode);
                    mediatorProperties.add(mediatorProperty);
                } else if (childNode.getNodeName().equalsIgnoreCase(Constant.FEATURE)) {
                    XsltFeature xsltFeature = createXsltFeature(childNode);
                    xsltFeatures.add(xsltFeature);
                } else if (childNode.getNodeName().equalsIgnoreCase(Constant.RESOURCE)) {
                    XsltResource xsltResource = createXsltResource(childNode);
                    xsltResources.add(xsltResource);
                }
            }
            xslt.setProperty(mediatorProperties.toArray(new MediatorProperty[mediatorProperties.size()]));
            xslt.setFeature(xsltFeatures.toArray(new XsltFeature[xsltFeatures.size()]));
            xslt.setResource(xsltResources.toArray(new XsltResource[xsltResources.size()]));
        }
        return xslt;
    }

    private XsltFeature createXsltFeature(DOMNode childNode) {

        XsltFeature xsltFeature = new XsltFeature();
        xsltFeature.elementNode((DOMElement) childNode);
        String name = childNode.getAttribute(Constant.NAME);
        if (name != null) {
            xsltFeature.setName(name);
        }
        String value = childNode.getAttribute(Constant.VALUE);
        if (value != null) {
            xsltFeature.setValue(Boolean.parseBoolean(value));
        }
        return xsltFeature;
    }

    private XsltResource createXsltResource(DOMNode childNode) {

        XsltResource xsltResource = new XsltResource();
        xsltResource.elementNode((DOMElement) childNode);
        String location = childNode.getAttribute(Constant.LOCATION);
        if (location != null) {
            xsltResource.setLocation(location);
        }
        String key = childNode.getAttribute(Constant.KEY);
        if (key != null) {
            xsltResource.setKey(key);
        }
        return xsltResource;
    }

    @Override
    public void populateAttributes(STNode node, DOMElement element) {

        String key = element.getAttribute(Constant.KEY);
        if (key != null) {
            ((Xslt) node).setKey(key);
        }
        String source = element.getAttribute(Constant.SOURCE);
        if (source != null) {
            ((Xslt) node).setSource(source);
        }
        String description = element.getAttribute(Constant.DESCRIPTION);
        if (description != null) {
            ((Xslt) node).setDescription(description);
        }
    }

    @Override
    public String getTagName() {

        return XSLT;
    }
}
