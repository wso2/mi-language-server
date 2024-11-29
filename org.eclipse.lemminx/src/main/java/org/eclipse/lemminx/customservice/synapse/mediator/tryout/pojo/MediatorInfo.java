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
    private Properties attributes;

    public MediatorInfo() {

        payload = new JsonPrimitive(StringUtils.EMPTY);
        variables = new ArrayList<>();
        headers = new ArrayList<>();
        attributes = new Properties();
    }

    public void addSynapseProperties(List<Property> properties) {

        variables.addAll(properties); // TODO: need to remove this
        attributes.addSynapseProperties(properties);
    }

    public void addAxis2Properties(List<Property> properties) {

        attributes.addAxis2Properties(properties);
    }

    public void addAxis2ClientProperties(List<Property> properties) {

        attributes.addAxis2ClientProperties(properties);
    }

    public void addAxis2TransportProperties(List<Property> properties) {

        attributes.addAxis2TransportProperties(properties);
        headers.addAll(properties);
    }

    public void addAxis2OperationProperties(List<Property> properties) {

        attributes.addAxis2OperationProperties(properties);
    }

    public void addSynapseProperty(Property property) {

        variables.add(property);
    }

    public void addAxis2Property(Property property) {

        attributes.addSynapseProperty(property);
    }

    public void addAxis2ClientProperty(Property property) {

        attributes.addAxis2ClientProperty(property);
    }

    public void addAxis2TransportProperty(Property property) {

        attributes.addAxis2TransportProperty(property);
        headers.add(property);
    }

    public void addAxis2OperationProperty(Property property) {

        attributes.addAxis2OperationProperty(property);
    }

    public void addVariable(String key, String value) {

        variables.add(new Property(key, value));
    }

    public void addHeader(String key, String value) {

        headers.add(new Property(key, value));
    }

    public void setPayload(JsonPrimitive payload) {

        this.payload = payload;
    }

    public JsonPrimitive getPayload() {

        return payload;
    }

    public List<Property> getSynapse() {

        return attributes.getSynapse();
    }

    public List<Property> getAxis2() {

        return attributes.getAxis2();
    }

    public List<Property> getAxis2Client() {

        return attributes.getAxis2Client();
    }

    public List<Property> getAxis2Transport() {

        return attributes.getAxis2Transport();
    }

    public List<Property> getAxis2Operation() {

        return attributes.getAxis2Operation();
    }

    public List<Property> getVariables() {

        return variables;
    }

    public List<Property> getHeaders() {

        return headers;
    }

    public Properties getAttributes() {

        return attributes;
    }

    public MediatorInfo clone() {

        MediatorInfo mediatorInfo = new MediatorInfo();
        mediatorInfo.setPayload(payload.deepCopy());
        mediatorInfo.addSynapseProperties(new ArrayList<>(variables));
        mediatorInfo.addAxis2Properties(new ArrayList<>(attributes.getAxis2()));
        mediatorInfo.addAxis2ClientProperties(new ArrayList<>(attributes.getAxis2Client()));
        mediatorInfo.addAxis2TransportProperties(new ArrayList<>(attributes.getAxis2Transport()));
        mediatorInfo.addAxis2OperationProperties(new ArrayList<>(attributes.getAxis2Operation()));
        mediatorInfo.addHeaders(new ArrayList<>(headers));
        return mediatorInfo;
    }

    private void addHeaders(List<Property> headers) {

        this.headers.addAll(headers);
    }

    @Override
    public String toString() {

        return "MediatorInfo{" +
                "payload=" + payload +
                ", variables=" + variables +
                ", headers=" + headers +
                ", attributes=" + attributes +
                '}';
    }
}
