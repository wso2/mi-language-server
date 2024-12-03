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

public class UnitTestDetails {

    private Node serverDownloadLink;
    private Node serverVersion;
    private Node serverPath;
    private Node serverPort;
    private Node serverHost;
    private Node serverType;
    private Node skipTest;

    UnitTestDetails(){}

    public void setSkipTest(Node skipTest) {
        this.skipTest = skipTest;
    }

    public void setServerType(Node serverType) {
        this.serverType = serverType;
    }

    public void setServerHost(Node serverHost) {
        this.serverHost = serverHost;
    }

    public void setServerPort(Node serverPort) {
        this.serverPort = serverPort;
    }

    public void setServerPath(Node serverPath) {
        this.serverPath = serverPath;
    }

    public void setServerVersion(Node serverVersion) {
        this.serverVersion = serverVersion;
    }

    public void setServerDownloadLink(Node serverDownloadLink) {
        this.serverDownloadLink = serverDownloadLink;
    }
}
