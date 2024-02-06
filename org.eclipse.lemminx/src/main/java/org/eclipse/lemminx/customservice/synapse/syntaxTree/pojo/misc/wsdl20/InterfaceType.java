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

package org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.misc.wsdl20;

import java.util.List;

public class InterfaceType extends ExtensibleDocumentedType {

    Object otherAttributes;
    List<Object> operationOrFaultOrAny;
    String name;
    Object _extends;
    Object styleDefault;

    @Override
    public Object getOtherAttributes() {

        return otherAttributes;
    }

    @Override
    public void setOtherAttributes(Object otherAttributes) {

        this.otherAttributes = otherAttributes;
    }

    public List<Object> getOperationOrFaultOrAny() {

        return operationOrFaultOrAny;
    }

    public void setOperationOrFaultOrAny(List<Object> operationOrFaultOrAny) {

        this.operationOrFaultOrAny = operationOrFaultOrAny;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public Object get_extends() {

        return _extends;
    }

    public void set_extends(Object _extends) {

        this._extends = _extends;
    }

    public Object getStyleDefault() {

        return styleDefault;
    }

    public void setStyleDefault(Object styleDefault) {

        this.styleDefault = styleDefault;
    }
}
