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

package org.eclipse.lemminx.customservice.synapse.expression.pojo;

public class FunctionInfo {

    String name;
    int currentParameterIndex;

    public FunctionInfo(String name, int currentParameterIndex) {

        this.name = name;
        this.currentParameterIndex = currentParameterIndex;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public int getCurrentParameterIndex() {

        return currentParameterIndex;
    }

    public void setCurrentParameterIndex(int currentParameterIndex) {

        this.currentParameterIndex = currentParameterIndex;
    }

    public void incrementParameterIndex() {

        this.currentParameterIndex++;
    }

    @Override
    public String toString() {

        return "FunctionInfo{" +
                "name='" + name + '\'' +
                ", currentParameterIndex=" + currentParameterIndex +
                '}';
    }
}
