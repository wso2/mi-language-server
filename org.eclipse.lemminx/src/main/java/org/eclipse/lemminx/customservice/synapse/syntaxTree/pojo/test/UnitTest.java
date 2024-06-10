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

package org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.test;

import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.STNode;

public class UnitTest extends STNode {

    UnitTestArtifacts unitTestArtifacts;
    TestCases testCases;
    MockServices mockServices;

    public UnitTestArtifacts getTestArtifacts() {

        return unitTestArtifacts;
    }

    public void setTestArtifacts(UnitTestArtifacts unitTestArtifacts) {

        this.unitTestArtifacts = unitTestArtifacts;
    }

    public TestCases getTestCases() {

        return testCases;
    }

    public void setTestCases(TestCases testCases) {

        this.testCases = testCases;
    }

    public MockServices getMockServices() {

        return mockServices;
    }

    public void setMockServices(MockServices mockServices) {

        this.mockServices = mockServices;
    }
}
