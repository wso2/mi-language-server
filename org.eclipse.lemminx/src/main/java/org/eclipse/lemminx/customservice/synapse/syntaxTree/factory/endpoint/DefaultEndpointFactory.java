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

import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.AbstractFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.STNode;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.endpoint.DefaultEndpoint;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.endpoint.common.EndpointEnableAddressing;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.endpoint.common.EndpointEnableRM;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.endpoint.common.EndpointEnableSec;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.endpoint.common.EndpointMarkForSuspension;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.endpoint.common.EndpointSuspendOnFailure;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.endpoint.common.EndpointTimeout;
import org.eclipse.lemminx.customservice.synapse.utils.Constant;
import org.eclipse.lemminx.dom.DOMElement;
import org.eclipse.lemminx.dom.DOMNode;

import java.util.List;

public class DefaultEndpointFactory extends AbstractFactory {

    @Override
    public STNode create(DOMElement element) {

        DefaultEndpoint defaultEndpoint = new DefaultEndpoint();
        defaultEndpoint.elementNode(element);
        populateAttributes(defaultEndpoint, element);
        List<DOMNode> children = element.getChildren();
        if (children != null && !children.isEmpty()) {
            for (DOMNode node : children) {
                String name = node.getNodeName();
                if (name.equalsIgnoreCase(Constant.ENABLE_SEC)) {
                    EndpointEnableSec enableSec = EndpointUtils.createEnableSec(node);
                    defaultEndpoint.setEnableSec(enableSec);
                } else if (name.equalsIgnoreCase(Constant.ENABLE_RM)) {
                    EndpointEnableRM enableRM = EndpointUtils.createEnableRM(node);
                    defaultEndpoint.setEnableRM(enableRM);
                } else if (name.equalsIgnoreCase(Constant.ENABLE_ADDRESSING)) {
                    EndpointEnableAddressing enableAddressing = EndpointUtils.createEnableAddressing(node);
                    defaultEndpoint.setEnableAddressing(enableAddressing);
                } else if (name.equalsIgnoreCase(Constant.TIMEOUT)) {
                    EndpointTimeout timeout = EndpointUtils.createTimeout(node);
                    defaultEndpoint.setTimeout(timeout);
                } else if (name.equalsIgnoreCase(Constant.SUSPEND_ON_FAILURE)) {
                    EndpointSuspendOnFailure suspendOnFailure = EndpointUtils.createSuspendOnFailure(node);
                    defaultEndpoint.setSuspendOnFailure(suspendOnFailure);
                } else if (name.equalsIgnoreCase(Constant.MARK_FOR_SUSPENSION)) {
                    EndpointMarkForSuspension markForSuspension = EndpointUtils.createMarkForSuspension(node);
                    defaultEndpoint.setMarkForSuspension(markForSuspension);
                }
            }
        }
        return defaultEndpoint;
    }

    @Override
    public void populateAttributes(STNode node, DOMElement element) {

        String format = element.getAttribute(Constant.FORMAT);
        if (format != null && !format.isEmpty()) {
            ((DefaultEndpoint) node).setFormat(format);
        }
        String optimize = element.getAttribute(Constant.OPTIMIZE);
        if (optimize != null && !optimize.isEmpty()) {
            ((DefaultEndpoint) node).setOptimize(optimize);
        }
        String encoding = element.getAttribute(Constant.ENCODING);
        if (encoding != null && !encoding.isEmpty()) {
            ((DefaultEndpoint) node).setEncoding(encoding);
        }
        String statistics = element.getAttribute(Constant.STATISTICS);
        if (statistics != null && !statistics.isEmpty()) {
            ((DefaultEndpoint) node).setStatistics(statistics);
        }
        String trace = element.getAttribute(Constant.TRACE);
        if (trace != null && !trace.isEmpty()) {
            ((DefaultEndpoint) node).setTrace(trace);
        }
    }
}
