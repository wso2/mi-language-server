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

package org.eclipse.lemminx.customservice.syntaxmodel.pojo.connector;

import org.eclipse.lemminx.customservice.syntaxmodel.pojo.mediator.Mediator;

import java.util.List;

public class Connector extends Mediator {

    String connectorName;
    String method;
    List<ConnectorParameter> parameters;

    public String getConnectorName() {

        return connectorName;
    }

    public void setConnectorName(String connectorName) {

        this.connectorName = connectorName;
    }

    public String getMethod() {

        return method;
    }

    public void setMethod(String method) {

        this.method = method;
    }

    public List<ConnectorParameter> getParameters() {

        return parameters;
    }

    public void setParameters(List<ConnectorParameter> parameters) {

        this.parameters = parameters;
    }
}
