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

package org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.core;

import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.AbstractMediatorFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.STNode;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.Mediator;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.Log;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.LogCategory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.LogLevel;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.MediatorProperty;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.utils.SyntaxTreeUtils;
import org.eclipse.lemminx.customservice.synapse.utils.Constant;
import org.eclipse.lemminx.customservice.synapse.utils.Utils;
import org.eclipse.lemminx.dom.DOMElement;
import org.eclipse.lemminx.dom.DOMNode;

import java.util.ArrayList;
import java.util.List;

public class LogFactory extends AbstractMediatorFactory {

    private static final String LOG = "log";

    @Override
    protected Mediator createSpecificMediator(DOMElement element) {

        Log log = new Log();
        log.elementNode(element);
        populateAttributes(log, element);
        List<DOMNode> children = element.getChildren();
        List<MediatorProperty> properties = new ArrayList<>();
        for (DOMNode node : children) {
            if (node.getNodeName().equalsIgnoreCase(Constant.PROPERTY)) {
                MediatorProperty property = SyntaxTreeUtils.createMediatorProperty(node);
                properties.add(property);
            } else if (node.getNodeName().equalsIgnoreCase(Constant.MESSAGE)) {
                log.setMessage(Utils.getInlineString(node.getFirstChild()));
            }
        }
        log.setProperty(properties.toArray(new MediatorProperty[properties.size()]));
        return log;
    }

    @Override
    public void populateAttributes(STNode node, DOMElement element) {

        try {
            String level = element.getAttribute(Constant.LEVEL);
            if (level != null) {
                ((Log) node).setLevel(LogLevel.valueOf(level));
            }
            String separator = element.getAttribute(Constant.SEPARATOR);
            if (separator != null) {
                ((Log) node).setSeparator(separator);
            }
            String category = element.getAttribute(Constant.CATEGORY);
            if (category != null) {
                ((Log) node).setCategory(LogCategory.valueOf(category));
            }
            String description = element.getAttribute(Constant.DESCRIPTION);
            if (description != null) {
                ((Log) node).setDescription(description);
            }
            String logMessageIDAttr = element.getAttribute(Constant.LOG_MESSAGE_ID);
            if (logMessageIDAttr != null) {
                ((Log) node).setLogMessageID(Boolean.parseBoolean(logMessageIDAttr));
            } else {
                ((Log) node).setLogMessageID(false);
            }
            String logFullPayloadAttr = element.getAttribute(Constant.LOG_FULL_PAYLOAD);
            if (logFullPayloadAttr != null) {
                ((Log) node).setLogFullPayload(Boolean.parseBoolean(logFullPayloadAttr));
            } else {
                ((Log) node).setLogFullPayload(false);
            }
        } catch (IllegalArgumentException e) {
            // ignore
        }
    }

    @Override
    public String getTagName() {

        return LOG;
    }
}
