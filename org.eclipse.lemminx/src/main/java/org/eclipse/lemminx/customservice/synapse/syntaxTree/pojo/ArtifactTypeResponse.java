/*
 *   Copyright (c) 2025, WSO2 LLC. (http://www.wso2.com).
 *
 *   WSO2 LLC. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 */

package org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo;

public class ArtifactTypeResponse {

    private String artifactType;
    private String artifactFolder;
    private String error;

    public ArtifactTypeResponse(ArtifactType artifact) {

        this.artifactType = artifact.getValue();
        this.artifactFolder = artifact.getFolder();
    }

    public ArtifactTypeResponse(String error) {

        this.error = error;
    }

    public String getArtifactType() {

        return artifactType;
    }

    public String getArtifactFolder() {

        return artifactFolder;
    }

    public String getError() {

        return error;
    }

    public enum ArtifactType {

        API("API", "apis"),
        AUTOMATION("Automation", "tasks"),
        EVENT_INTEGRATION("Event Integration", "inbound-endpoints"),
        ENDPOINT("Endpoint", "endpoints"),
        SEQUENCE("Sequence", "sequences"),
        CLASS_MEDIATOR("Class Mediator", "java"),
        RESOURCE("Resource", "resources"),
        MESSAGE_PROCESSOR("Message Processor", "message-processors"),
        MESSAGE_STORE("Message Store", "message-stores"),
        TEMPLATE("Template", "templates"),
        LOCAL_ENTRY("Local Entry", "local-entries"),
        CONNECTIONS("Connections", "local-entries"),
        PROXY("Proxy", "proxy-services"),
        DATA_SERVICE("Data Service", "data-services"),
        DATA_SOURCE("Data Source", "data-sources");

        private String value;
        private String folder;

        ArtifactType(String value, String folder) {

            this.value = value;
            this.folder = folder;
        }

        public String getValue() {

            return value;
        }

        public String getFolder() {

            return folder;
        }
    }
}
