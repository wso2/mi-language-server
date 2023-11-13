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

package org.eclipse.lemminx.customservice.syntaxmodel.factory.mediators.other;

import org.eclipse.lemminx.customservice.syntaxmodel.factory.mediators.AbstractMediatorFactory;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.STNode;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.mediator.Mediator;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.mediator.other.Attribute;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.mediator.other.publishEvent.PublishEvent;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.mediator.other.publishEvent.PublishEventAttributes;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.mediator.other.publishEvent.PublishEventAttributesArbitrary;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.mediator.other.publishEvent.PublishEventAttributesArbitraryAttribute;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.mediator.other.publishEvent.PublishEventAttributesCorrelation;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.mediator.other.publishEvent.PublishEventAttributesMeta;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.mediator.other.publishEvent.PublishEventAttributesPayload;
import org.eclipse.lemminx.customservice.syntaxmodel.utils.Constant;
import org.eclipse.lemminx.customservice.syntaxmodel.utils.Utils;
import org.eclipse.lemminx.dom.DOMElement;
import org.eclipse.lemminx.dom.DOMNode;

import java.util.List;

public class PublishEventFactory extends AbstractMediatorFactory {

    private static final String PUBLISH_EVENT = "publishEvent";

    @Override
    public Mediator createSpecificMediator(DOMElement element) {

        PublishEvent publishEvent = new PublishEvent();
        publishEvent.elementNode(element);
        populateAttributes(publishEvent, element);
        List<DOMNode> children = element.getChildren();
        if (children != null && !children.isEmpty()) {
            for (DOMNode child : children) {
                if (child.getNodeName().equalsIgnoreCase(Constant.EVENT_SINK)) {
                    String eventSink = Utils.getInlineString(child.getFirstChild());
                    publishEvent.setEventSink(eventSink);
                } else if (child.getNodeName().equalsIgnoreCase(Constant.STREAM_NAME)) {
                    String streamName = Utils.getInlineString(child.getFirstChild());
                    publishEvent.setStreamName(streamName);
                } else if (child.getNodeName().equalsIgnoreCase(Constant.STREAM_VERSION)) {
                    String streamVersion = Utils.getInlineString(child.getFirstChild());
                    publishEvent.setStreamVersion(streamVersion);
                } else if (child.getNodeName().equalsIgnoreCase(Constant.ATTRIBUTES)) {
                    PublishEventAttributes attributes = createPublishEventAttributes(child);
                    publishEvent.setAttributes(attributes);
                }
            }
        }

        return publishEvent;
    }

    @Override
    public void populateAttributes(STNode node, DOMElement element) {

        String description = element.getAttribute(Constant.DESCRIPTION);
        if (description != null && !description.isEmpty()) {
            ((PublishEvent) node).setDescription(description);
        }
    }

    private PublishEventAttributes createPublishEventAttributes(DOMNode node) {

        PublishEventAttributes publishEventAttributes = new PublishEventAttributes();
        publishEventAttributes.elementNode((DOMElement) node);
        List<DOMNode> children = node.getChildren();
        if (children != null && !children.isEmpty()) {
            for (DOMNode child : children) {
                if (child.getNodeName().equalsIgnoreCase(Constant.META)) {
                    PublishEventAttributesMeta publishEventAttributesMeta = createPublishEventAttributesMeta(child);
                    publishEventAttributes.setMeta(publishEventAttributesMeta);
                } else if (child.getNodeName().equalsIgnoreCase(Constant.CORRELATION)) {
                    PublishEventAttributesCorrelation publishEventAttributesCorrelation = createPublishEventAttributesCorrelation(child);
                    publishEventAttributes.setCorrelation(publishEventAttributesCorrelation);
                } else if (child.getNodeName().equalsIgnoreCase(Constant.PAYLOAD)) {
                    PublishEventAttributesPayload publishEventAttributesPayload = createPublishEventAttributesPayload(child);
                    publishEventAttributes.setPayload(publishEventAttributesPayload);
                } else if (child.getNodeName().equalsIgnoreCase(Constant.ARBITRARY)) {
                    PublishEventAttributesArbitrary publishEventAttributesArbitrary = createPublishEventAttributesArbitrary(child);
                    publishEventAttributes.setArbitrary(publishEventAttributesArbitrary);
                }
            }
        }
        return publishEventAttributes;
    }

    private Attribute createAttribute(DOMNode childNode) {

        Attribute attribute = new Attribute();
        attribute.elementNode((DOMElement) childNode);
        String name = childNode.getAttribute(Constant.NAME);
        if (name != null && !name.isEmpty()) {
            attribute.setName(name);
        }
        String dataType = childNode.getAttribute(Constant.DATA_TYPE);
        if (dataType != null && !dataType.isEmpty()) {
            attribute.setDataType(dataType);
        }
        String _default = childNode.getAttribute(Constant.DEFAULT);
        if (_default != null && !_default.isEmpty()) {
            attribute.set_default(_default);
        }
        String value = childNode.getAttribute(Constant.VALUE);
        if (value != null && !value.isEmpty()) {
            attribute.setValue(value);
        }
        String expression = childNode.getAttribute(Constant.EXPRESSION);
        if (expression != null && !expression.isEmpty()) {
            attribute.setExpression(expression);
        }
        return attribute;
    }

    private PublishEventAttributesMeta createPublishEventAttributesMeta(DOMNode child) {

        PublishEventAttributesMeta publishEventAttributesMeta = new PublishEventAttributesMeta();
        publishEventAttributesMeta.elementNode((DOMElement) child);
        List<DOMNode> children = child.getChildren();
        if (children != null && !children.isEmpty()) {
            for (DOMNode childNode : children) {
                if (childNode.getNodeName().equalsIgnoreCase(Constant.ATTRIBUTE)) {
                    Attribute attribute = createAttribute(childNode);
                    publishEventAttributesMeta.setAttribute(attribute);
                }
            }
        }
        return publishEventAttributesMeta;
    }

    private PublishEventAttributesCorrelation createPublishEventAttributesCorrelation(DOMNode child) {

        PublishEventAttributesCorrelation publishEventAttributesCorrelation = new PublishEventAttributesCorrelation();
        publishEventAttributesCorrelation.elementNode((DOMElement) child);
        List<DOMNode> children = child.getChildren();
        if (children != null && !children.isEmpty()) {
            for (DOMNode childNode : children) {
                if (childNode.getNodeName().equalsIgnoreCase(Constant.ATTRIBUTE)) {
                    Attribute attribute = createAttribute(childNode);
                    publishEventAttributesCorrelation.setAttribute(attribute);
                }
            }
        }
        return publishEventAttributesCorrelation;
    }

    private PublishEventAttributesPayload createPublishEventAttributesPayload(DOMNode child) {

        PublishEventAttributesPayload publishEventAttributesPayload = new PublishEventAttributesPayload();
        publishEventAttributesPayload.elementNode((DOMElement) child);
        List<DOMNode> children = child.getChildren();
        if (children != null && !children.isEmpty()) {
            for (DOMNode childNode : children) {
                if (childNode.getNodeName().equalsIgnoreCase(Constant.ATTRIBUTE)) {
                    Attribute attribute = createAttribute(childNode);
                    publishEventAttributesPayload.setAttribute(attribute);
                }
            }
        }
        return publishEventAttributesPayload;
    }

    private PublishEventAttributesArbitrary createPublishEventAttributesArbitrary(DOMNode child) {

        PublishEventAttributesArbitrary publishEventAttributesArbitrary = new PublishEventAttributesArbitrary();
        publishEventAttributesArbitrary.elementNode((DOMElement) child);
        List<DOMNode> children = child.getChildren();
        if (children != null && !children.isEmpty()) {
            for (DOMNode childNode : children) {
                if (childNode.getNodeName().equalsIgnoreCase(Constant.ATTRIBUTE)) {
                    PublishEventAttributesArbitraryAttribute attribute = createArbitaryAttribute(childNode);
                    publishEventAttributesArbitrary.setAttribute(attribute);
                }
            }
        }
        return publishEventAttributesArbitrary;
    }

    private PublishEventAttributesArbitraryAttribute createArbitaryAttribute(DOMNode childNode) {

        PublishEventAttributesArbitraryAttribute attribute = new PublishEventAttributesArbitraryAttribute();
        attribute.elementNode((DOMElement) childNode);
        String name = childNode.getAttribute(Constant.NAME);
        if (name != null && !name.isEmpty()) {
            attribute.setName(name);
        }
        String dataType = childNode.getAttribute(Constant.DATA_TYPE);
        if (dataType != null && !dataType.isEmpty()) {
            attribute.setDataType(dataType);
        }
        String _default = childNode.getAttribute(Constant.DEFAULT);
        if (_default != null && !_default.isEmpty()) {
            attribute.set_default(_default);
        }
        String value = childNode.getAttribute(Constant.VALUE);
        if (value != null && !value.isEmpty()) {
            attribute.setValue(value);
        }
        return attribute;
    }

    @Override
    public String getTagName() {

        return PUBLISH_EVENT;
    }
}
