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

package org.eclipse.lemminx.customservice.syntaxmodel.pojo.misc.wsdl20;

import java.util.List;

public class BindingType extends ExtensibleDocumentedType {

    Object otherAttributes;
    List<Object> operationOrFaultOrAny;
    String name;
    String type;
    String _interface;

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

    public String getType() {

        return type;
    }

    public void setType(String type) {

        this.type = type;
    }

    public String get_interface() {

        return _interface;
    }

    public void set_interface(String _interface) {

        this._interface = _interface;
    }
}
