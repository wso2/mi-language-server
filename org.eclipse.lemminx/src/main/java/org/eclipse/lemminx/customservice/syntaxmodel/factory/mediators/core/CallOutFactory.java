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

package org.eclipse.lemminx.customservice.syntaxmodel.factory.mediators.core;

import org.eclipse.lemminx.customservice.syntaxmodel.factory.mediators.AbstractMediatorFactory;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.STNode;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.mediator.Mediator;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.mediator.core.callout.Callout;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.mediator.core.callout.CalloutConfiguration;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.mediator.core.callout.CalloutEnableSec;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.mediator.core.callout.CalloutSource;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.mediator.core.callout.CalloutTarget;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.mediator.core.callout.SourceOrTargetOrConfiguration;
import org.eclipse.lemminx.customservice.syntaxmodel.utils.Constant;
import org.eclipse.lemminx.dom.DOMElement;
import org.eclipse.lemminx.dom.DOMNode;

import java.util.List;
import java.util.Optional;

public class CallOutFactory extends AbstractMediatorFactory {

    private static final String CALL_OUT = "CallOut";

    @Override
    public Mediator createSpecificMediator(DOMElement element) {

        Callout callOut = new Callout();
        callOut.elementNode(element);
        populateAttributes(callOut, element);
        List<DOMNode> children = element.getChildren();
        if (children != null && !children.isEmpty()) {
            SourceOrTargetOrConfiguration sourceOrTargetOrConfiguration = new SourceOrTargetOrConfiguration();
            for (DOMNode child : children) {
                String name = child.getNodeName();
                if (name.equalsIgnoreCase(Constant.SOURCE)) {
                    CalloutSource source = createSource(child);
                    sourceOrTargetOrConfiguration.setSource(Optional.of(source));
                } else if (name.equalsIgnoreCase(Constant.TARGET)) {
                    CalloutTarget target = createTarget(child);
                    sourceOrTargetOrConfiguration.setTarget(Optional.of(target));
                } else if (name.equalsIgnoreCase(Constant.ENABLE_SEC)) {
                    CalloutEnableSec enableSec = createEnableSec(child);
                    sourceOrTargetOrConfiguration.setEnableSec(Optional.of(enableSec));
                } else if (name.equalsIgnoreCase(Constant.CONFIGURATION)) {
                    CalloutConfiguration configuration = createConfiguration(child);
                    sourceOrTargetOrConfiguration.setConfiguration(Optional.of(configuration));
                }
            }
            callOut.setSourceOrTargetOrConfiguration(sourceOrTargetOrConfiguration);
        }
        return callOut;
    }

    private CalloutSource createSource(DOMNode child) {

        CalloutSource source = new CalloutSource();
        source.elementNode((DOMElement) child);
        String xpath = child.getAttribute(Constant.XPATH);
        if (xpath != null && !xpath.isEmpty()) {
            source.setXpath(xpath);
        }
        String key = child.getAttribute(Constant.KEY);
        if (key != null && !key.isEmpty()) {
            source.setKey(key);
        }
        return source;
    }

    private CalloutTarget createTarget(DOMNode child) {

        CalloutTarget target = new CalloutTarget();
        target.elementNode((DOMElement) child);
        String xpath = child.getAttribute(Constant.XPATH);
        if (xpath != null && !xpath.isEmpty()) {
            target.setXpath(xpath);
        }
        String key = child.getAttribute(Constant.KEY);
        if (key != null && !key.isEmpty()) {
            target.setKey(key);
        }
        return target;
    }

    private CalloutEnableSec createEnableSec(DOMNode child) {

        CalloutEnableSec enableSec = new CalloutEnableSec();
        enableSec.elementNode((DOMElement) child);
        String policy = child.getAttribute(Constant.POLICY);
        if (policy != null && !policy.isEmpty()) {
            enableSec.setPolicy(policy);
        }
        String outboundPolicy = child.getAttribute(Constant.OUTBOUND_POLICY);
        if (outboundPolicy != null && !outboundPolicy.isEmpty()) {
            enableSec.setOutboundPolicy(outboundPolicy);
        }
        String inboundPolicy = child.getAttribute(Constant.INBOUND_POLICY);
        if (inboundPolicy != null && !inboundPolicy.isEmpty()) {
            enableSec.setInboundPolicy(inboundPolicy);
        }
        return enableSec;
    }

    private CalloutConfiguration createConfiguration(DOMNode child) {

        CalloutConfiguration configuration = new CalloutConfiguration();
        configuration.elementNode((DOMElement) child);
        String axis2xml = child.getAttribute(Constant.AXIS2XML);
        if (axis2xml != null && !axis2xml.isEmpty()) {
            configuration.setAxis2Xml(axis2xml);
        }
        String repository = child.getAttribute(Constant.REPOSITORY);
        if (repository != null && !repository.isEmpty()) {
            configuration.setRepository(repository);
        }
        return configuration;
    }

    @Override
    public void populateAttributes(STNode node, DOMElement element) {

        String serviceURL = element.getAttribute(Constant.SERVICE_URL);
        if (serviceURL != null && !serviceURL.isEmpty()) {
            ((Callout) node).setServiceURL(serviceURL);
        }
        String action = element.getAttribute(Constant.ACTION);
        if (action != null && !action.isEmpty()) {
            ((Callout) node).setAction(action);
        }
        String initAxis2ClientOptions = element.getAttribute(Constant.INIT_AXIS2_CLIENT_OPTIONS);
        if (initAxis2ClientOptions != null && !initAxis2ClientOptions.isEmpty()) {
            ((Callout) node).setInitAxis2ClientOptions(Boolean.valueOf(initAxis2ClientOptions));
        }
        String endpointKey = element.getAttribute(Constant.ENDPOINT_KEY);
        if (endpointKey != null && !endpointKey.isEmpty()) {
            ((Callout) node).setEndpointKey(endpointKey);
        }
        String description = element.getAttribute(Constant.DESCRIPTION);
        if (description != null && !description.isEmpty()) {
            ((Callout) node).setDescription(description);
        }
    }

    @Override
    public String getTagName() {

        return CALL_OUT;
    }

}
