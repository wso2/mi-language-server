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

public class OverviewPageDetailsResponse {

    private PrimaryDetails primaryDetails;
    private BuildDetails buildDetails;
    private DependenciesDetails dependencies;
    private UnitTestDetails unitTest;
    private List<Node> configurables;

    public OverviewPageDetailsResponse() {
        primaryDetails = new PrimaryDetails();
        buildDetails = new BuildDetails();
        dependencies = new DependenciesDetails();
        unitTest = new UnitTestDetails();
        configurables = new ArrayList<>();
    }

    public PrimaryDetails getPrimaryDetails() {
        return this.primaryDetails;
    }

    public BuildDetails getBuildDetails() {
        return this.buildDetails;
    }

    public DependenciesDetails getDependenciesDetails() {
        return this.dependencies;
    }

    public UnitTestDetails getUnitTestDetails() {
        return this.unitTest;
    }

    public void setConfig(Node config) {
        this.configurables.add(config);
    }
}
