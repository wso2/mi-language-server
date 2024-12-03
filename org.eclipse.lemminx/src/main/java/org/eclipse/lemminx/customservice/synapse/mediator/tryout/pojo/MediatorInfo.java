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

package org.eclipse.lemminx.customservice.synapse.mediator.tryout.pojo;

import com.google.gson.JsonPrimitive;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class MediatorInfo {

    private JsonPrimitive payload;
    private List<Property> variables;
    private List<Property> headers;
    private Properties properties;
    private Params params;

    public MediatorInfo() {

        payload = new JsonPrimitive(StringUtils.EMPTY);
        variables = new ArrayList<>();
        headers = new ArrayList<>();
        properties = new Properties();
        params = new Params();
    }

    public void addSynapseProperties(List<Property> properties) {

        variables.addAll(properties); // TODO: need to remove this once variables added to debugger
        this.properties.addSynapseProperties(properties);
    }

    public void addAxis2Properties(List<Property> properties) {

        this.properties.addAxis2Properties(properties);
    }

    public void addAxis2ClientProperties(List<Property> properties) {

        this.properties.addAxis2ClientProperties(properties);
    }

    public void addAxis2TransportProperties(List<Property> properties) {

        this.properties.addAxis2TransportProperties(properties);
        headers.addAll(properties);
    }

    public void addAxis2OperationProperties(List<Property> properties) {

        this.properties.addAxis2OperationProperties(properties);
    }

    public void addSynapseProperty(Property property) {

        variables.add(property);
        properties.addSynapseProperty(property);
    }

    public void addAxis2Property(Property property) {

        properties.addSynapseProperty(property);
    }

    public void addAxis2ClientProperty(Property property) {

        properties.addAxis2ClientProperty(property);
    }

    public void addAxis2TransportProperty(Property property) {

        properties.addAxis2TransportProperty(property);
        headers.add(property);
    }

    public void addAxis2OperationProperty(Property property) {

        properties.addAxis2OperationProperty(property);
    }

    public void addVariable(String key, String value) {

        variables.add(new Property(key, value));
    }

    public void addHeader(String key, String value) {

        headers.add(new Property(key, value));
    }

    public void addQueryParam(String key, String value) {

        params.addQueryParam(new Property(key, value));
    }

    public void addUriParam(String key, String value) {

        params.addUriParam(new Property(key, value));
    }

    public void addFunctionParam(String key, String value) {

        params.addFunctionParam(new Property(key, value));
    }

    public void setPayload(JsonPrimitive payload) {

        this.payload = payload;
    }

    public JsonPrimitive getPayload() {

        return payload;
    }

    public List<Property> getSynapse() {

        return properties.getSynapse();
    }

    public List<Property> getAxis2() {

        return properties.getAxis2();
    }

    public List<Property> getAxis2Client() {

        return properties.getAxis2Client();
    }

    public List<Property> getAxis2Transport() {

        return properties.getAxis2Transport();
    }

    public List<Property> getAxis2Operation() {

        return properties.getAxis2Operation();
    }

    public List<Property> getVariables() {

        return variables;
    }

    public List<Property> getHeaders() {

        return headers;
    }

    public Properties getProperties() {

        return properties;
    }

    public Params getParams() {

        return params;
    }

    public void setParams(Params params) {

        this.params = params;
    }

    public void setVariables(List<Property> variables) {

        this.variables = variables;
    }

    public void setHeaders(List<Property> headers) {

        this.headers = headers;
    }

    public void setProperties(Properties properties) {

        this.properties = properties;
    }

    public MediatorInfo deepCopy() {

        MediatorInfo mediatorInfo = new MediatorInfo();
        mediatorInfo.setPayload(payload.deepCopy());
        mediatorInfo.setVariables(new ArrayList<>(variables));
        mediatorInfo.setProperties(properties.deepCopy());
        mediatorInfo.setHeaders(new ArrayList<>(headers));
        mediatorInfo.setParams(params.deepCopy());
        return mediatorInfo;
    }

    @Override
    public String toString() {

        return "MediatorInfo{" +
                "payload=" + payload +
                ", variables=" + variables +
                ", headers=" + headers +
                ", properties=" + properties +
                ", params=" + params +
                '}';
    }
}
