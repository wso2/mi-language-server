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
package org.eclipse.lemminx.customservice.synapse.parser;

import java.util.ArrayList;
import java.util.List;

public class DependenciesDetails {

    private List<DependencyDetails> connectorDependencies;
    private List<DependencyDetails> otherDependencies;

    DependenciesDetails() {
        connectorDependencies = new ArrayList<>();
        otherDependencies = new ArrayList<>();
    }

    public List<DependencyDetails> getConnectorDependencies() {
        return connectorDependencies;
    }

    public void addConnectorDependencies(DependencyDetails dependencyDetails) {
        connectorDependencies.add(dependencyDetails);
    }

    public void addOtherDependencies(DependencyDetails dependencyDetails) {
        otherDependencies.add(dependencyDetails);
    }
}
