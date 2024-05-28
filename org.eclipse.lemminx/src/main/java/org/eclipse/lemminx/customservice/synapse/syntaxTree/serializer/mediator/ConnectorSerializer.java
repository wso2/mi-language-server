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

package org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.mediator;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMText;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.connector.Connector;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.connector.ConnectorParameter;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.Mediator;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.SerializerUtils;

import java.util.List;

public class ConnectorSerializer extends AbstractMediatorSerializer {

    @Override
    protected OMElement serializeSpecificMediator(Mediator m) {

        Connector connector = (Connector) m;
        String connectorName = connector.getConnectorName();
        String method = connector.getMethod();
        String tag = connectorName + "." + method;
        OMElement connectorElt = fac.createOMElement(tag, synNS);

        if (connector.getConfigKey() != null) {
            connectorElt.addAttribute("configKey", connector.getConfigKey(), nullNS);
        }
        serializeParameters(connector.getParameters(), connectorElt);

        return connectorElt;
    }

    private void serializeParameters(List<ConnectorParameter> parameters, OMElement connectorElt) {

        if (parameters != null) {
            for (ConnectorParameter parameter : parameters) {
                OMElement parameterElt = serializeParameter(parameter);
                if (parameterElt != null) {
                    connectorElt.addChild(parameterElt);
                }
            }
        }
    }

    private OMElement serializeParameter(ConnectorParameter parameter) {

        if (parameter != null && parameter.getName() != null) {
            String name = parameter.getName();
            OMElement parameterElt = fac.createOMElement(name, synNS);
            if (parameter.getIsExpression()) {
                String expression = parameter.getExpression();
                OMText expressionElt = fac.createOMText(expression);
                if (expressionElt != null) {
                    parameterElt.addChild(expressionElt);
                }
                SerializerUtils.serializeNamespaces(parameter, parameterElt);
            } else {
                String value = parameter.getValue();
                if (value != null) {
                    parameterElt.setText(value);
                }
            }
            return parameterElt;
        }
        return null;
    }

    @Override
    public String getMediatorClassName() {

        return Connector.class.getName();
    }
}
