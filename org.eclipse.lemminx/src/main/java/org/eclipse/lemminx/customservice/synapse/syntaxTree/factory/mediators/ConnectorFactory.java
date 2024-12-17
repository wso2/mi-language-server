/*
 * Copyright (c) 2024, WSO2 LLC. (http://www.wso2.com).
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

package org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators;

import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.STNode;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.connector.Connector;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.connector.ConnectorParameter;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.Mediator;
import org.eclipse.lemminx.customservice.synapse.utils.Constant;
import org.eclipse.lemminx.customservice.synapse.utils.Utils;
import org.eclipse.lemminx.dom.DOMElement;
import org.eclipse.lemminx.dom.DOMNode;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConnectorFactory extends AbstractMediatorFactory {

    private static final String CONNECTOR = "connector";
    private static List<String> connectors = new ArrayList<>();

    @Override
    public void populateAttributes(STNode node, DOMElement element) {

        String configKey = element.getAttribute("configKey");
        if (configKey != null) {
            ((Connector) node).setConfigKey(configKey);
        }
        addConnectorParameters((Connector) node, element);
    }

    @Override
    protected Mediator createSpecificMediator(DOMElement element) {

        Connector connector = new Connector();
        connector.elementNode(element);
        String elementName = element.getNodeName();
        String connectorName = elementName.substring(0, elementName.indexOf(Constant.DOT));
        connector.setConnectorName(connectorName);
        connector.setMethod(elementName.substring(elementName.indexOf(Constant.DOT) + 1));
        populateAttributes(connector, element);
        return connector;
    }

    private void addConnectorParameters(Connector connector, DOMElement element) {

        List<DOMNode> children = element.getChildren();
        if (children != null && !children.isEmpty()) {
            List<ConnectorParameter> parameters = new ArrayList<>();
            for (DOMNode child : children) {
                if (child instanceof DOMElement) {
                    DOMElement childElement = (DOMElement) child;
                    ConnectorParameter parameter = new ConnectorParameter();
                    parameter.elementNode(childElement);
                    parameter.setName(childElement.getNodeName());
                    String inline = Utils.getInlineString(childElement.getFirstChild());
                    Boolean isExpression = isExpression(inline);
                    parameter.setIsExpression(isExpression);
                    if (isExpression) {
                        parameter.setExpression(inline);
                    } else {
                        parameter.setValue(inline);
                    }
                    parameter.setValue(Utils.getInlineString(childElement.getFirstChild()));
                    parameters.add(parameter);
                }
            }
            connector.setParameters(parameters);
        }
    }

    private Boolean isExpression(String inline) {

        if (inline == null) {
            return false;
        }
        return (inline.startsWith("${") || inline.startsWith("{")) && inline.endsWith("}");
    }

    public static void addConnector(String connector) {

        connectors.add(connector);
    }

    public static void removeConnector(String connector) {

        connectors.remove(connector);
    }

    public static List<String> getConnectors() {

        return connectors;
    }

    @Override
    public String getTagName() {

        return CONNECTOR;
    }
}
