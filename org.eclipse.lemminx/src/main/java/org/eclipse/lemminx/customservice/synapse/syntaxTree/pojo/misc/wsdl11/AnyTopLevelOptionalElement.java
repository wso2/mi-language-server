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

package org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.misc.wsdl11;

import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.STNode;

import java.util.Optional;

public class AnyTopLevelOptionalElement extends STNode {

    Optional<TImport> _import;
    Optional<TTypes> types;
    Optional<TMessage> message;
    Optional<TPortType> portType;
    Optional<TBinding> binding;
    Optional<TService> service;

    public Optional<TImport> get_import() {

        return _import;
    }

    public void set_import(Optional<TImport> _import) {

        this._import = _import;
    }

    public Optional<TTypes> getTypes() {

        return types;
    }

    public void setTypes(Optional<TTypes> types) {

        this.types = types;
    }

    public Optional<TMessage> getMessage() {

        return message;
    }

    public void setMessage(Optional<TMessage> message) {

        this.message = message;
    }

    public Optional<TPortType> getPortType() {

        return portType;
    }

    public void setPortType(Optional<TPortType> portType) {

        this.portType = portType;
    }

    public Optional<TBinding> getBinding() {

        return binding;
    }

    public void setBinding(Optional<TBinding> binding) {

        this.binding = binding;
    }

    public Optional<TService> getService() {

        return service;
    }

    public void setService(Optional<TService> service) {

        this.service = service;
    }
}
