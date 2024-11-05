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

        return input;
    }

    public MediatorInfo getOutput() {

        return output;
    }

    public String getError() {

        return error;
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
