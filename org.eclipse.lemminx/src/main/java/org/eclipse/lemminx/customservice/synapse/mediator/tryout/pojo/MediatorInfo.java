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

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.ArrayList;
import java.util.List;

public class MediatorInfo {

    private JsonPrimitive payload;
    private List<Property> synapse;
    private List<Property> axis2;
    private List<Property> axis2Client;
    private List<Property> axis2Transport;
    private List<Property> axis2Operation;

    public MediatorInfo() {

        payload = new JsonPrimitive("");
        synapse = new ArrayList<>();
        axis2 = new ArrayList<>();
        axis2Client = new ArrayList<>();
        axis2Transport = new ArrayList<>();
        axis2Operation = new ArrayList<>();
    }

    public void addSynapseProperties(List<Property> properties) {

        synapse.addAll(properties);
    }

    public void addAxis2Properties(List<Property> properties) {

        axis2.addAll(properties);
    }

    public void addAxis2ClientProperties(List<Property> properties) {

        axis2Client.addAll(properties);
    }

    public void addAxis2TransportProperties(List<Property> properties) {

        axis2Transport.addAll(properties);
    }

    public void addAxis2OperationProperties(List<Property> properties) {

        axis2Operation.addAll(properties);
    }

    public void addSynapseProperty(String key, String value) {

        synapse.add(new Property(key, value));
    }

    public void addAxis2Property(String key, String value) {

        axis2.add(new Property(key, value));
    }

    public void addAxis2ClientProperty(String key, String value) {

        axis2Client.add(new Property(key, value));
    }

    public void addAxis2TransportProperty(String key, String value) {

        axis2Transport.add(new Property(key, value));
    }

    public void addAxis2OperationProperty(String key, String value) {

        axis2Operation.add(new Property(key, value));
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

    @Override
    public String toString() {

        return "MediatorInfo{" +
                "payload=" + payload +
                ", synapse=" + synapse +
                ", axis2=" + axis2 +
                ", axis2Client=" + axis2Client +
                ", axis2Transport=" + axis2Transport +
                ", axis2Operation=" + axis2Operation +
                '}';
    }
}
