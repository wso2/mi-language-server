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

package org.eclipse.lemminx.customservice.synapse.mediator;

import java.nio.file.Path;

public class TryOutConstants {

    public static final Path TEMP_FOLDER_PATH = Path.of(System.getProperty("user.home"), ".wso2-mi", "tryout");
    public static final String SYNAPSE_PROPERTIES = "synapse-properties";
    public static final String AXIS2_PROPERTIES = "axis2-properties";
    public static final String AXIS2_CLIENT_PROPERTIES = "axis2Client-properties";
    public static final String AXIS2_TRANSPORT_PROPERTIES = "axis2Transport-properties";
    public static final String AXIS2_OPERATION_PROPERTIES = "axis2Operation-properties";
    public static final String ENVELOPE = "Envelope";
    public static final String API_KEY = "api-key";
    public static final String URI_MAPPING = "uri-mapping";
    public static final String HTTP_PREFIX = "http://";
    public static final String SLASH = "/";
    public static final String POST = "POST";
    public static final String RESUME_COMMAND = "{\"command\":\"resume\"}";
    public static final String EVENT = "event";
    public static final String BREAKPOINT = "breakpoint";
    public static final String LOCALHOST = "localhost";
    public static final Path CARBON_XML_J2_PATH = Path.of("repository", "resources", "conf", "templates",
            "conf", "carbon.xml.j2");
    public static final Path CARBON_XML_PATH = Path.of("conf", "carbon.xml");
    public static final Path MI_REPOSITORY_PATH = Path.of("repository", "deployment", "server",
            "synapse-configs", "default");
    public static final Path PROJECT_ARTIFACT_PATH = Path.of("src", "main", "wso2mi", "artifacts");
    public static final Path PROJECT_CONNECTOR_PATH = Path.of("src", "main", "wso2mi", "resources", "connectors");
    public static final Path MI_CONNECTOR_PATH = Path.of("repository", "deployment", "server", "synapse-libs");
    public static final Path PROJECT_REGSTRY_PATH = Path.of("src", "main", "wso2mi", "resources", "registry");
    public static final Path DEPLOYMENT_TOML_PATH = Path.of("conf", "deployment.toml");
    public static final String GOV = "gov";
    public static final String CONF = "conf";
    public static final Path MI_GOV_PATH = Path.of("registry", "governance");
    public static final Path MI_CONF_PATH = Path.of("registry", "config");

    private TryOutConstants() {

    }
}
