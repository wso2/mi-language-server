/*
 * Copyright (c) 2025, WSO2 LLC. (http://www.wso2.com).
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

package org.eclipse.lemminx.customservice.synapse.dependency.tree.pojo;

import org.eclipse.lemminx.customservice.synapse.dependency.tree.ArtifactType;

public class ConnectorDependency extends Dependency {

    private String operationName;

    public ConnectorDependency(String name, ArtifactType type,
                               String path) {

        super(name, type, path);
    }

    public String getOperationName() {

        return operationName;
    }

    public void setOperationName(String operationName) {

        this.operationName = operationName;
    }
}
