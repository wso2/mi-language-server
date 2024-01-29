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

package org.eclipse.lemminx.customservice.syntaxmodel.directoryTree;

public enum ProjectType {
    DATA_SERVICE_CONFIGS("org.wso2.developerstudio.eclipse.ds.project.nature"),
    ESB_CONFIGS("org.wso2.developerstudio.eclipse.esb.project.nature"),
    COMPOSITE_EXPORTER("org.wso2.developerstudio.eclipse.distribution.project.nature"),
    CONNECTOR_EXPORTER("org.wso2.developerstudio.eclipse.artifact.connector.project.nature"),
    DATA_SOURCE_CONFIGS("org.wso2.developerstudio.eclipse.datasource.project.nature"),
    MEDIATOR_PROJECT("org.wso2.developerstudio.eclipse.artifact.mediator.project.nature"),
    REGISTRY_RESOURCE("org.wso2.developerstudio.eclipse.general.project.nature"),
    DOCKER_EXPORTER("org.wso2.developerstudio.eclipse.docker.distribution.project.nature"),
    KUBERNETES_EXPORTER("org.wso2.developerstudio.eclipse.kubernetes.distribution.project.nature"),
    JAVA_LIBRARY_PROJECT("org.wso2.developerstudio.eclipse.java.library.project.nature");

    public String value;

    ProjectType(String value) {

        this.value = value;
    }
}
