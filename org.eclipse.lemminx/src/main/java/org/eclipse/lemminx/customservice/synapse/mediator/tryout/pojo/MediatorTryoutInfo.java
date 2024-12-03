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

public class MediatorTryoutInfo {

    private MediatorInfo input;
    private MediatorInfo output;
    private String error;

    public MediatorTryoutInfo() {

        input = new MediatorInfo();
        output = new MediatorInfo();
    }

    public MediatorTryoutInfo(MediatorInfo input, MediatorInfo output) {

        this.input = input;
        this.output = output;
    }

    public MediatorTryoutInfo(MediatorInfo input, String error) {

        this.input = input;
        this.error = error;
    }

    public MediatorTryoutInfo(String error) {

        this.error = error;
    }

    public MediatorInfo getInput() {

        return input.deepCopy();
    }

    public MediatorInfo getOutput() {

        return output.deepCopy();
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

    public void replaceInputWithOutput() {

        input = output.deepCopy();
    }

    @Override
    public String toString() {

        return "MediatorTryoutInfo{" +
                "input=" + input +
                ", output=" + output +
                ", error='" + error + '\'' +
                '}';
    }
}
