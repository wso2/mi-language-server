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

package org.eclipse.lemminx.customservice.syntaxmodel.pojo.endpoint;

import org.eclipse.lemminx.customservice.syntaxmodel.pojo.STNode;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.endpoint.common.EndpointParameter;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.endpoint.common.EndpointProperty;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.endpoint.failover.EndpointFailover;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.endpoint.http.EndpointHttp;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.endpoint.loadbalance.EndpointLoadbalance;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.endpoint.recipientList.EndpointRecipientlist;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.endpoint.wsdl.WSDLEndpoint;

public class Endpoint extends STNode {

    DefaultEndpoint _default;
    EndpointHttp http;
    EndpointAddress address;
    WSDLEndpoint wsdl;
    EndpointLoadbalance loadbalance;
    EndpointSession session;
    EndpointFailover failover;
    EndpointRecipientlist recipientlist;
    EndpointProperty[] property;
    EndpointParameter[] parameter;
    String description;
    String key;
    String template;
    String uri;
    EndpointType type;

    public DefaultEndpoint get_default() {

        return _default;
    }

    public void set_default(DefaultEndpoint _default) {

        this._default = _default;
    }

    public EndpointHttp getHttp() {

        return http;
    }

    public void setHttp(EndpointHttp http) {

        this.http = http;
    }

    public EndpointAddress getAddress() {

        return address;
    }

    public void setAddress(EndpointAddress address) {

        this.address = address;
    }

    public WSDLEndpoint getWsdl() {

        return wsdl;
    }

    public void setWsdl(WSDLEndpoint wsdl) {

        this.wsdl = wsdl;
    }

    public EndpointLoadbalance getLoadbalance() {

        return loadbalance;
    }

    public void setLoadbalance(EndpointLoadbalance loadbalance) {

        this.loadbalance = loadbalance;
    }

    public EndpointSession getSession() {

        return session;
    }

    public void setSession(EndpointSession session) {

        this.session = session;
    }

    public EndpointFailover getFailover() {

        return failover;
    }

    public void setFailover(EndpointFailover failover) {

        this.failover = failover;
    }

    public EndpointRecipientlist getRecipientlist() {

        return recipientlist;
    }

    public void setRecipientlist(EndpointRecipientlist recipientlist) {

        this.recipientlist = recipientlist;
    }

    public EndpointProperty[] getProperty() {

        return property;
    }

    public void setProperty(EndpointProperty[] property) {

        this.property = property;
    }

    public EndpointParameter[] getParameter() {

        return parameter;
    }

    public void setParameter(EndpointParameter[] parameter) {

        this.parameter = parameter;
    }

    public String getKey() {

        return key;
    }

    public void setKey(String key) {

        this.key = key;
    }

    public String getTemplate() {

        return template;
    }

    public void setTemplate(String template) {

        this.template = template;
    }

    public String getUri() {

        return uri;
    }

    public void setUri(String uri) {

        this.uri = uri;
    }

    public String getDescription() {

        return description;
    }

    public void setDescription(String description) {

        this.description = description;
    }

    public EndpointType getType() {

        return type;
    }

    public void setType(EndpointType type) {

        this.type = type;
    }
}
