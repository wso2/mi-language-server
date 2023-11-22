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

package org.eclipse.lemminx.customservice.syntaxmodel.factory.mediators.filter;

import org.eclipse.lemminx.customservice.syntaxmodel.factory.mediators.AbstractMediatorFactory;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.STNode;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.mediator.Mediator;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.mediator.filter.throttle.ID;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.mediator.filter.throttle.MediatorThrottleAssertion;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.mediator.filter.throttle.Policy;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.mediator.filter.throttle.Throttle;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.mediator.filter.throttle.ThrottlePolicy;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.misc.common.Sequence;
import org.eclipse.lemminx.customservice.syntaxmodel.utils.Constant;
import org.eclipse.lemminx.customservice.syntaxmodel.utils.Utils;
import org.eclipse.lemminx.dom.DOMElement;
import org.eclipse.lemminx.dom.DOMNode;

import java.util.ArrayList;
import java.util.List;

public class ThrottleFactory extends AbstractMediatorFactory {

    private static final String THROTTLE = "throttle";

    @Override
    public Mediator createSpecificMediator(DOMElement element) {

        Throttle throttle = new Throttle();
        throttle.elementNode(element);
        populateAttributes(throttle, element);
        List<DOMNode> children = element.getChildren();
        if (children != null && !children.isEmpty()) {
            for (DOMNode child : children) {
                if (child.getNodeName().equalsIgnoreCase(Constant.POLICY)) {
                    ThrottlePolicy throttlePolicy = createThrottlePolicy(child);
                    throttle.setPolicy(throttlePolicy);
                } else if (child.getNodeName().equalsIgnoreCase(Constant.ON_ACCEPT)) {
                    Sequence onAccept = Utils.createSequence(child);
                    throttle.setOnAccept(onAccept);
                } else if (child.getNodeName().equalsIgnoreCase(Constant.ON_REJECT)) {
                    Sequence onReject = Utils.createSequence(child);
                    throttle.setOnReject(onReject);
                }
            }
        }

        return throttle;
    }

    @Override
    public void populateAttributes(STNode node, DOMElement element) {

        String id = element.getAttribute(Constant.ID);
        if (id != null && !id.isEmpty()) {
            ((Throttle) node).setId(id);
        }
        String onAcceptAttribute = element.getAttribute(Constant.ON_ACCEPT);
        if (onAcceptAttribute != null && !onAcceptAttribute.isEmpty()) {
            ((Throttle) node).setOnAcceptAttribute(onAcceptAttribute);
        }
        String onRejectAttribute = element.getAttribute(Constant.ON_REJECT);
        if (onRejectAttribute != null && !onRejectAttribute.isEmpty()) {
            ((Throttle) node).setOnRejectAttribute(onRejectAttribute);
        }
        String description = element.getAttribute(Constant.DESCRIPTION);
        if (description != null && !description.isEmpty()) {
            ((Throttle) node).setDescription(description);
        }
    }

    private ThrottlePolicy createThrottlePolicy(DOMNode node) {

        ThrottlePolicy throttlePolicy = new ThrottlePolicy();
        throttlePolicy.elementNode((DOMElement) node);
        String key = node.getAttribute(Constant.KEY);
        if (key != null && !key.isEmpty()) {
            throttlePolicy.setKey(key);
        }
        List<DOMNode> children = node.getChildren();
        if (children != null && !children.isEmpty()) {
            List<Policy> policies = new ArrayList<>();
            for (DOMNode child : children) {
                if (child.getNodeName().equalsIgnoreCase(Constant.WSP_POLICY)) {
                    Policy policy = createPolicy(child);
                    policies.add(policy);
                }
            }
            throttlePolicy.setContent(policies.toArray(new Policy[policies.size()]));
        }
        return throttlePolicy;
    }

    private Policy createPolicy(DOMNode node) {

        Policy policy = new Policy();
        policy.elementNode((DOMElement) node);
        String name = node.getAttribute(Constant.NAME);
        if (name != null && !name.isEmpty()) {
            policy.setName(name);
        }
        List<DOMNode> children = node.getChildren();
        if (children != null && !children.isEmpty()) {
            List<Object> stElements = new ArrayList<>();
            for (DOMNode child : children) {
                String name1 = child.getNodeName();
                if (name1.equalsIgnoreCase(Constant.THROTTLE_MEDIATOR_THROTTLE_ASSERTION)) {
                    MediatorThrottleAssertion mediatorThrottleAssertion = createMediatorThrottleAssertion(child);
                    stElements.add(mediatorThrottleAssertion);
                } else if (name1.equalsIgnoreCase(Constant.THROTTLE_ID)) {
                    ID id = createID(child);
                    policy.setId(id);
                } else if (name1.equalsIgnoreCase(Constant.WSP_POLICY)) {
                    Policy childPolicy = createPolicy(child);
                    stElements.add(childPolicy);
                    // TODO: check if the following logic is correct
                } else if (name1.equalsIgnoreCase(Constant.THROTTLE_ALLOW) || name1.equalsIgnoreCase(
                        Constant.THROTTLE_DENY) || name1.equalsIgnoreCase(Constant.THROTTLE_CONTROL)) {
                    STNode accessType = new STNode();
                    accessType.elementNode((DOMElement) child);
                    stElements.add(accessType);
                } else if (name1.equalsIgnoreCase(Constant.THROTTLE_MAXIMUM_COUNT)) {
                    STNode maximumCount = new STNode();
                    maximumCount.elementNode((DOMElement) child);
                    stElements.add(maximumCount);
                } else if (name1.equalsIgnoreCase(Constant.THROTTLE_UNIT_TIME)) {
                    STNode unitTime = new STNode();
                    unitTime.elementNode((DOMElement) child);
                    stElements.add(unitTime);
                } else if (name1.equalsIgnoreCase(Constant.THROTTLE_PROHIBIT_TIME_PERIOD)) {
                    STNode prohibitTimePeriod = new STNode();
                    prohibitTimePeriod.elementNode((DOMElement) child);
                    stElements.add(prohibitTimePeriod);
                }
            }
            policy.setPolicyOrAllOrExactlyOne(stElements);
        }
        return policy;
    }

    private ID createID(DOMNode node) {

        ID id = new ID();
        id.elementNode((DOMElement) node);
        String type = node.getAttribute(Constant.THROTTLE_TYPE);
        if (type != null && !type.isEmpty()) {
            id.setType(type);
        }
        DOMNode valueNode = node.getFirstChild();
        String value = Utils.getInlineString(valueNode);
        if (value != null && !value.isEmpty()) {
            id.setValue(value);
        }
        return id;
    }

    private MediatorThrottleAssertion createMediatorThrottleAssertion(DOMNode node) {

        MediatorThrottleAssertion mediatorThrottleAssertion = new MediatorThrottleAssertion();
        mediatorThrottleAssertion.elementNode((DOMElement) node);
        List<DOMNode> children = node.getChildren();
        List<Policy> policies = new ArrayList<>();
        if (children != null && !children.isEmpty()) {
            for (DOMNode child : children) {
                if (child.getNodeName().equalsIgnoreCase(Constant.WSP_POLICY)) {
                    Policy policy = createPolicy(child);
                    policies.add(policy);
                } else if (child.getNodeName().equalsIgnoreCase(Constant.THROTTLE_MAXIMUM_CONCURRENT_ACCESS)) {
                    STNode maximumConcurrentAccess = new STNode();
                    maximumConcurrentAccess.elementNode((DOMElement) child);
                    mediatorThrottleAssertion.setMaximumConcurrentAccess(maximumConcurrentAccess);
                }
            }
            mediatorThrottleAssertion.setPolicy(policies.toArray(new Policy[policies.size()]));
        }
        return mediatorThrottleAssertion;
    }

    @Override
    public String getTagName() {

        return THROTTLE;
    }
}
