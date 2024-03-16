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

package org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.endpoint;

import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.STNode;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.endpoint.common.EndpointEnableAddressing;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.endpoint.common.EndpointEnableRM;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.endpoint.common.EndpointEnableSec;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.endpoint.common.EndpointMarkForSuspension;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.endpoint.common.EndpointRetryConfig;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.endpoint.common.EndpointSuspendOnFailure;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.endpoint.common.EndpointTimeout;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.endpoint.http.EndpointHttpAuthenticationOauthRequestParameters;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.misc.common.Parameter;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.utils.SyntaxTreeUtils;
import org.eclipse.lemminx.customservice.synapse.utils.Constant;
import org.eclipse.lemminx.dom.DOMElement;
import org.eclipse.lemminx.dom.DOMNode;

import java.util.ArrayList;
import java.util.List;

public class EndpointUtils {

    public static EndpointEnableSec createEnableSec(DOMNode node) {

        EndpointEnableSec enableSec = new EndpointEnableSec();
        enableSec.elementNode((DOMElement) node);
        String inboundPolicy = node.getAttribute(Constant.INBOUND_POLICY);
        if (inboundPolicy != null && !inboundPolicy.isEmpty()) {
            enableSec.setInboundPolicy(inboundPolicy);
        }
        String outboundPolicy = node.getAttribute(Constant.OUTBOUND_POLICY);
        if (outboundPolicy != null && !outboundPolicy.isEmpty()) {
            enableSec.setOutboundPolicy(outboundPolicy);
        }
        return enableSec;
    }

    public static EndpointEnableRM createEnableRM(DOMNode node) {

        EndpointEnableRM enableRM = new EndpointEnableRM();
        enableRM.elementNode((DOMElement) node);
        String policy = node.getAttribute(Constant.POLICY);
        if (policy != null && !policy.isEmpty()) {
            enableRM.setPolicy(policy);
        }
        return enableRM;
    }

    public static EndpointEnableAddressing createEnableAddressing(DOMNode node) {

        EndpointEnableAddressing enableAddressing = new EndpointEnableAddressing();
        enableAddressing.elementNode((DOMElement) node);
        String version = node.getAttribute(Constant.VERSION);
        if (version != null && !version.isEmpty()) {
            enableAddressing.setVersion(version);
        }
        String separateListener = node.getAttribute(Constant.SEPARATE_LISTENER);
        if (separateListener != null && !separateListener.isEmpty()) {
            enableAddressing.setSeparateListener(Boolean.parseBoolean(separateListener));
        }
        return enableAddressing;
    }

    public static EndpointTimeout createTimeout(DOMNode node) {

        EndpointTimeout timeout = new EndpointTimeout();
        timeout.elementNode((DOMElement) node);
        List<DOMNode> children = node.getChildren();
        List<Object> elements = new ArrayList<>();
        if (children != null && !children.isEmpty()) {
            for (DOMNode child : children) {
                if (child instanceof DOMElement) {
                    STNode content = new STNode();
                    content.elementNode((DOMElement) child);
                    elements.add(content);
                }
            }
            timeout.setContent(elements);
        }
        return timeout;
    }

    public static EndpointSuspendOnFailure createSuspendOnFailure(DOMNode node) {

        EndpointSuspendOnFailure suspendOnFailure = new EndpointSuspendOnFailure();
        suspendOnFailure.elementNode((DOMElement) node);
        List<DOMNode> children = node.getChildren();
        if (children != null && !children.isEmpty()) {
            for (DOMNode child : children) {
                if (!(child instanceof DOMElement)) {
                    continue;
                }
                STNode stElement = new STNode();
                stElement.elementNode((DOMElement) child);
                String name = child.getNodeName();
                if (name.equalsIgnoreCase(Constant.ERROR_CODES)) {
                    suspendOnFailure.setErrorCodes(stElement);
                } else if (name.equalsIgnoreCase(Constant.INITIAL_DURATION)) {
                    suspendOnFailure.setInitialDuration(stElement);
                } else if (name.equalsIgnoreCase(Constant.PROGRESSION_FACTOR)) {
                    suspendOnFailure.setProgressionFactor(stElement);
                } else if (name.equalsIgnoreCase(Constant.MAXIMUM_DURATION)) {
                    suspendOnFailure.setMaximumDuration(stElement);
                }
            }
        }
        return suspendOnFailure;
    }

    public static EndpointMarkForSuspension createMarkForSuspension(DOMNode node) {

        EndpointMarkForSuspension markForSuspension = new EndpointMarkForSuspension();
        markForSuspension.elementNode((DOMElement) node);
        List<DOMNode> children = node.getChildren();
        if (children != null && !children.isEmpty()) {
            for (DOMNode child : children) {
                if (!(child instanceof DOMElement)) {
                    continue;
                }
                String name = child.getNodeName();
                STNode stElement = new STNode();
                stElement.elementNode((DOMElement) child);
                if (name.equalsIgnoreCase(Constant.ERROR_CODES)) {
                    markForSuspension.setErrorCodes(stElement);
                } else if (name.equalsIgnoreCase(Constant.RETRIES_BEFORE_SUSPENSION)) {
                    markForSuspension.setRetriesBeforeSuspension(stElement);
                } else if (name.equalsIgnoreCase(Constant.RETRY_DELAY)) {
                    markForSuspension.setRetryDelay(stElement);
                }
            }
        }
        return markForSuspension;
    }

    public static EndpointHttpAuthenticationOauthRequestParameters createOauthRequestParameters(DOMNode node) {

        EndpointHttpAuthenticationOauthRequestParameters requestParameters =
                new EndpointHttpAuthenticationOauthRequestParameters();
        requestParameters.elementNode((DOMElement) node);
        List<DOMNode> children = node.getChildren();
        List<Parameter> parameters = new ArrayList<>();
        if (children != null && !children.isEmpty()) {
            for (DOMNode child : children) {
                String name = child.getNodeName();
                if (name.equalsIgnoreCase(Constant.PARAMETER)) {
                    Parameter parameter = SyntaxTreeUtils.createParameter(child);
                    parameters.add(parameter);
                }
            }
            requestParameters.setParameter(parameters.toArray(new Parameter[parameters.size()]));
        }
        return requestParameters;
    }

    public static EndpointRetryConfig createRetryConfig(DOMNode node) {

        EndpointRetryConfig retryConfig = new EndpointRetryConfig();
        List<DOMNode> children = node.getChildren();
        if (children != null && !children.isEmpty()) {
            for (DOMNode child : children) {
                String name = child.getNodeName();
                STNode stNode = new STNode();
                stNode.elementNode((DOMElement) child);
                if (Constant.ENABLED_ERROR_CODES.equalsIgnoreCase(name)) {
                    retryConfig.setEnabledErrorCodes(stNode);
                } else if (Constant.DISABLED_ERROR_CODES.equalsIgnoreCase(name)) {
                    retryConfig.setDisabledErrorCodes(stNode);
                }
            }
        }
        return retryConfig;
    }
}
