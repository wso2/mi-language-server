/*
 * Copyright (c) 2025, WSO2 LLC. (http://www.wso2.com).
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

package org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.ai;

import org.eclipse.lemminx.customservice.synapse.syntaxTree.SyntaxTreeGenerator;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.STNode;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.connector.ai.AIAgent;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.connector.ai.AIConnector;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.connector.ai.AgentTool;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.connector.ai.AgentTools;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.Mediator;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.template.Template;
import org.eclipse.lemminx.customservice.synapse.utils.ConfigFinder;
import org.eclipse.lemminx.customservice.synapse.utils.Constant;
import org.eclipse.lemminx.customservice.synapse.utils.Utils;
import org.eclipse.lemminx.dom.DOMDocument;
import org.eclipse.lemminx.dom.DOMElement;
import org.eclipse.lemminx.dom.DOMNode;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AIAgentConnectorFactory extends AIConnectorFactory {

    private static final Logger LOGGER = Logger.getLogger(AIAgentConnectorFactory.class.getName());
    private static final List<String> ALLOWED_CONNECTION_TAGS = List.of("llmConfigKey", "memoryConfigKey");

    @Override
    protected Mediator createSpecificMediator(DOMElement element) {

        AIConnector aiAgent = new AIAgent();
        populateConnectorConfigs(aiAgent, element);
        populateConnections(aiAgent, element, ALLOWED_CONNECTION_TAGS);

        DOMNode agentIDNode = Utils.getChildNodeByName(element, Constant.AGENT_ID);
        if (agentIDNode != null) {
            ((AIAgent) aiAgent).setAgentID(Utils.getInlineString(agentIDNode.getFirstChild()));
        }

        populateTools((AIAgent) aiAgent, element);
        return aiAgent;
    }

    private void populateTools(AIAgent aiAgent, DOMElement element) {

        DOMNode toolsElement = Utils.getChildNodeByName(element, Constant.TOOLS);
        if (toolsElement != null) {
            AgentTools agentTools = new AgentTools();
            agentTools.elementNode((DOMElement) toolsElement);
            List<DOMNode> toolElements = toolsElement.getChildren();
            for (DOMNode toolElement : toolElements) {
                if (toolElement instanceof DOMElement && Constant.TOOL.equals(toolElement.getNodeName())) {
                    String templateName = toolElement.getAttribute(Constant.TEMPLATE);
                    try {
                        String templatePath = ConfigFinder.getTemplatePath(templateName, getProjectPath());
                        Mediator mediator = getMediatorFromTemplate(templatePath);
                        AgentTool agentTool = new AgentTool();
                        agentTool.elementNode((DOMElement) toolElement);
                        agentTool.setName(toolElement.getAttribute(Constant.NAME));
                        agentTool.setTemplate(templateName);
                        agentTool.setTemplatePath(templatePath);
                        agentTool.setDescription(toolElement.getAttribute(Constant.DESCRIPTION));
                        agentTool.setResultExpression(toolElement.getAttribute(Constant.RESULT_EXPRESSION));
                        agentTool.setMediator(mediator);
                        agentTools.addTool(agentTool);
                    } catch (IOException e) {
                        LOGGER.log(Level.SEVERE, "Error while reading the template file", e);
                    }
                }
            }
            aiAgent.setTools(agentTools);
        }
    }

    private Mediator getMediatorFromTemplate(String templatePath) throws IOException {

        Mediator mediator = null;
        if (templatePath != null) {
            DOMDocument templateDoc = Utils.getDOMDocument(new File(templatePath));
            STNode node = SyntaxTreeGenerator.buildTree(templateDoc.getDocumentElement());
            if (node instanceof Template && ((Template) node).getSequence() != null &&
                    ((Template) node).getSequence().getMediatorList().size() > 0) {
                mediator = ((Template) node).getSequence().getMediatorList().get(0);
            }
        }
        return mediator;
    }

    @Override
    public String getTagName() {

        return Constant.AI_AGENT_TAG;
    }
}
