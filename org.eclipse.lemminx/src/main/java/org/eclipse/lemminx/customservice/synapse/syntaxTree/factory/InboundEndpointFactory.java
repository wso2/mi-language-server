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

import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.STNode;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.api.EnableDisable;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.inbound.InboundEndpoint;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.inbound.InboundEndpointParameters;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.misc.common.Parameter;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.utils.SyntaxTreeUtils;
import org.eclipse.lemminx.customservice.synapse.utils.Constant;
import org.eclipse.lemminx.customservice.synapse.utils.Utils;
import org.eclipse.lemminx.dom.DOMElement;
import org.eclipse.lemminx.dom.DOMNode;

import java.util.ArrayList;
import java.util.List;

public class InboundEndpointFactory extends AbstractFactory {

    @Override
    public STNode create(DOMElement element) {

        InboundEndpoint inboundEndpoint = new InboundEndpoint();
        inboundEndpoint.elementNode(element);
        populateAttributes(inboundEndpoint, element);
        List<DOMNode> children = element.getChildren();
        List<InboundEndpointParameters> parameters = new ArrayList<>();
        if (children != null && !children.isEmpty()) {
            for (DOMNode node : children) {
                if (node.getNodeName().equalsIgnoreCase(Constant.PARAMETERS)) {
                    InboundEndpointParameters parameter = createParameters(node);
                    parameters.add(parameter);
                }
            }
            inboundEndpoint.setParameters(parameters.toArray(new InboundEndpointParameters[parameters.size()]));
        }
        return inboundEndpoint;
    }

    private InboundEndpointParameters createParameters(DOMNode node) {

        InboundEndpointParameters parameters = new InboundEndpointParameters();
        parameters.elementNode((DOMElement) node);
        List<DOMNode> children = node.getChildren();
        List<Parameter> parameterList = new ArrayList<>();
        if (children != null && !children.isEmpty()) {
            for (DOMNode child : children) {
                if (child.getNodeName().equalsIgnoreCase(Constant.PARAMETER)) {
                    Parameter parameter = SyntaxTreeUtils.createParameter(child);
                    parameterList.add(parameter);
                }
            }
            parameters.setParameter(parameterList.toArray(new Parameter[parameterList.size()]));
        }
        return parameters;
    }

    @Override
    public void populateAttributes(STNode node, DOMElement element) {

        String name = element.getAttribute(Constant.NAME);
        if (name != null) {
            ((InboundEndpoint) node).setName(name);
        }
        String protocol = element.getAttribute(Constant.PROTOCOL);
        if (protocol != null) {
            ((InboundEndpoint) node).setProtocol(protocol);
        }
        String onError = element.getAttribute(Constant.ON_ERROR);
        if (onError != null) {
            ((InboundEndpoint) node).setOnError(onError);
        }
        String suspend = element.getAttribute(Constant.SUSPEND);
        if (suspend != null) {
            ((InboundEndpoint) node).setSuspend(Boolean.parseBoolean(suspend));
        }
        String clazz = element.getAttribute(Constant.CLASS);
        if (clazz != null) {
            ((InboundEndpoint) node).setClazz(clazz);
        }
        String statistics = element.getAttribute(Constant.STATISTICS);
        EnableDisable statisticsEnum = Utils.getEnumFromValue(statistics, EnableDisable.class);
        if (statisticsEnum != null) {
            ((InboundEndpoint) node).setStatistics(statisticsEnum);
        }
        String trace = element.getAttribute(Constant.TRACE);
        EnableDisable traceEnum = Utils.getEnumFromValue(trace, EnableDisable.class);
        if (traceEnum != null) {
            ((InboundEndpoint) node).setTrace(traceEnum);
        }
        String sequence = element.getAttribute(Constant.SEQUENCE);
        if (sequence != null) {
            ((InboundEndpoint) node).setSequence(sequence);
        }
    }
}
