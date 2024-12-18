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

import java.util.List;

public class MediatorTryoutInfo {

    private String id;
    private MediatorInfo input;
    private MediatorInfo output;
    private String error;

    public MediatorTryoutInfo() {

        input = new MediatorInfo();
        output = new MediatorInfo();
    }

    public MediatorTryoutInfo(String id, MediatorInfo input, MediatorInfo output) {

        this.input = input;
        this.output = output;
        this.id = id;
    }

    public MediatorTryoutInfo(String id, MediatorInfo input, String error) {

        this.id = id;
        this.input = input;
        this.error = error;
    }

    public MediatorTryoutInfo(MediatorInfo input, MediatorInfo output) {

        this.input = input;
        this.output = output;
    }

    public MediatorTryoutInfo(String error) {

        this.error = error;
    }

    public String getId() {

        return id;
    }

    public void setId(String id) {

        this.id = id;
    }

    public MediatorInfo getInput() {

        if (input != null) {
            return input.deepCopy();
        }
        return null;
    }

    public MediatorInfo getOutput() {

        if (output != null) {
            return output.deepCopy();
        }
        return null;
    }

    public String getError() {

        return error;
    }

    public void setInputPayload(JsonPrimitive jsonPrimitive) {

        input.setPayload(jsonPrimitive);
    }

    public void addInputSynapseProperties(Property property) {

        input.addSynapseProperty(property);
    }

    public void addInputAxis2Properties(Property property) {

        input.addAxis2Property(property);
    }

    public void addInputAxis2ClientProperties(Property property) {

        input.addAxis2ClientProperty(property);
    }

    public void addInputAxis2TransportProperties(Property property) {

        input.addAxis2TransportProperty(property);
    }

    public void addInputAxis2OperationProperties(Property property) {

        input.addAxis2OperationProperty(property);
    }

    public void setOutputPayload(JsonPrimitive jsonPrimitive) {

        output.setPayload(jsonPrimitive);
    }

    public void addOutputSynapseProperties(Property property) {

        output.addSynapseProperty(property);
    }

    public void addOutputAxis2Properties(Property property) {

        output.addAxis2Property(property);
    }

    public void addOutputAxis2ClientProperties(Property property) {

        output.addAxis2ClientProperty(property);
    }

    public void addOutputAxis2TransportProperties(Property property) {

        output.addAxis2TransportProperty(property);
    }

    public void addOutputAxis2OperationProperties(Property property) {

        output.addAxis2OperationProperty(property);
    }

    public void addOutputVariable(Property property) {

        output.addVariable(property);
    }

    public void addOutputVariable(String name, String value) {

        output.addVariable(new Property(name, value));
    }

    public void setInputConfigs(List<Property> configs) {

        input.setConfigs(configs);
    }

    public void setOutputConfigs(List<Property> configs) {

        output.setConfigs(configs);
    }

    public void removeOutputVariable(String name) {

        output.removeVariable(name);
    }

    public void replaceInputWithOutput() {

        input = output.deepCopy();
    }

    @Override
    public String toString() {

        return "MediatorTryoutInfo{" +
                "id='" + id + '\'' +
                ", input=" + input +
                ", output=" + output +
                ", error='" + error + '\'' +
                '}';
    }
}
