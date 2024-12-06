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

import java.util.ArrayList;
import java.util.List;

public class MediatorInfo {

    private JsonPrimitive payload;

    // Old types
    private List<Property> synapse;
    private List<Property> axis2;
    private List<Property> axis2Client;
    private List<Property> axis2Transport;
    private List<Property> axis2Operation;

    // New types
    private List<Property> variables;
    private List<Property> headers;
    private Properties attributes;

    public MediatorInfo() {

        payload = new JsonPrimitive("");
        synapse = new ArrayList<>();
        axis2 = new ArrayList<>();
        axis2Client = new ArrayList<>();
        axis2Transport = new ArrayList<>();
        axis2Operation = new ArrayList<>();

        variables = new ArrayList<>();
        headers = new ArrayList<>();
        attributes = new Properties();
    }

    public void addSynapseProperties(List<Property> properties) {

        variables.addAll(properties);
        attributes.addSynapseProperties(properties);
        synapse.addAll(properties);
    }

    public void addAxis2Properties(List<Property> properties) {

        attributes.addAxis2Properties(properties);
        axis2.addAll(properties);
    }

    public void addAxis2ClientProperties(List<Property> properties) {

        attributes.addAxis2ClientProperties(properties);
        axis2Client.addAll(properties);
    }

    public void addAxis2TransportProperties(List<Property> properties) {

        attributes.addAxis2TransportProperties(properties);
        headers.addAll(properties);
        axis2Transport.addAll(properties);
    }

    public void addAxis2OperationProperties(List<Property> properties) {

        attributes.addAxis2OperationProperties(properties);
        axis2Operation.addAll(properties);
    }

    public void addSynapseProperty(Property property) {

        variables.add(property);
        synapse.add(property);
    }

    public void addAxis2Property(Property property) {

        attributes.addSynapseProperty(property);
        axis2.add(property);
    }

    public void addAxis2ClientProperty(Property property) {

        attributes.addAxis2ClientProperty(property);
        axis2Client.add(property);
    }

    public void addAxis2TransportProperty(Property property) {

        attributes.addAxis2TransportProperty(property);
        headers.add(property);
        axis2Transport.add(property);
    }

    public void addAxis2OperationProperty(Property property) {

        attributes.addAxis2OperationProperty(property);
        axis2Operation.add(property);
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

        return synapse;
    }

    public List<Property> getAxis2() {

        return axis2;
    }

    public List<Property> getAxis2Client() {

        return axis2Client;
    }

    public List<Property> getAxis2Transport() {

        return axis2Transport;
    }

    public List<Property> getAxis2Operation() {

        return axis2Operation;
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
        mediatorInfo.setPayload(payload);
        mediatorInfo.addSynapseProperties(synapse);
        mediatorInfo.addAxis2Properties(axis2);
        mediatorInfo.addAxis2ClientProperties(axis2Client);
        mediatorInfo.addAxis2TransportProperties(axis2Transport);
        mediatorInfo.addAxis2OperationProperties(axis2Operation);
        return mediatorInfo;
    }

    @Override
    public String toString() {

        return "MediatorInfo{" +
                "payload=" + payload +
                ", synapse=" + synapse +
                ", axis2=" + axis2 +
                ", axis2Client=" + axis2Client +
                ", axis2Transport=" + axis2Transport +
                ", axis2Operation=" + axis2Operation +
                ", variables=" + variables +
                ", headers=" + headers +
                ", attributes=" + attributes +
                '}';
    }
}
