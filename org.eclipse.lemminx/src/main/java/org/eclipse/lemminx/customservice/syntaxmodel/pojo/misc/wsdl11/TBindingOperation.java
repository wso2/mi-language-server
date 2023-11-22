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

package org.eclipse.lemminx.customservice.syntaxmodel.pojo.misc.wsdl11;

public class TBindingOperation extends TExtensibleDocumented {

    TBindingOperationMessage input;
    TBindingOperationMessage output;
    TBindingOperationFault[] fault;
    String name;

    public TBindingOperationMessage getInput() {

        return input;
    }

    public void setInput(TBindingOperationMessage input) {

        this.input = input;
    }

    public TBindingOperationMessage getOutput() {

        return output;
    }

    public void setOutput(TBindingOperationMessage output) {

        this.output = output;
    }

    public TBindingOperationFault[] getFault() {

        return fault;
    }

    public void setFault(TBindingOperationFault[] fault) {

        this.fault = fault;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }
}
