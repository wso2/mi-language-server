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
package org.eclipse.lemminx.customservice.synapse.api.generator;

import java.util.regex.Pattern;

/**
 * Constants used in Swagger definition generation.
 */
public class SwaggerConstants {

    static final String SERVERS = "servers";

    static final String VARIABLES = "variables";

    static final String DEFAULT_CONTEXT = "/default";

    static final String TEMPLATE_REGEX = "\\{(.*?)\\}";

    static final String URL = "url";
    /**
     * Swagger element "info" in http://swagger.io/specification/
     */
    static final String INFO = "info";

    /**
     * Swagger element "description" in http://swagger.io/specification/
     */
    static final String DESCRIPTION = "description";

    /**
     * Swagger element "version" in http://swagger.io/specification/
     */
    static final String VERSION = "version";

    /**
     * Swagger element "title" in http://swagger.io/specification/
     */
    static final String TITLE = "title";

    /**
     * Swagger element "paths" in http://swagger.io/specification/
     */
    static final String PATHS = "paths";

    /**
     * Swagger element "parameters" in http://swagger.io/specification/
     */
    static final String PARAMETERS = "parameters";

    /**
     * Swagger element "responses" in http://swagger.io/specification/
     */
    static final String RESPONSES = "responses";

    /**
     * Swagger element "description" of parameters in http://swagger.io/specification/
     */
    static final String PARAMETER_DESCRIPTION = "description";

    /**
     * Swagger element "in" of parameters in http://swagger.io/specification/
     */
    static final String PARAMETER_IN = "in";

    /**
     * Swagger element "name" of parameters in http://swagger.io/specification/
     */
    static final String PARAMETER_NAME = "name";

    /**
     * Swagger element "required" of parameters in http://swagger.io/specification/
     */
    static final String PARAMETER_REQUIRED = "required";

    /**
     * Swagger element "type" of parameters in http://swagger.io/specification/
     */
    static final String PARAMETER_TYPE = "type";

    /**
     * String to be used in default "responses" elements
     */
    static final String DEFAULT_VALUE = "default";

    /**
     * Default value for "response" element since it is not provided by API definition
     */
    static final String DEFAULT_RESPONSE = "Default Response";

    /**
     * Default value for parameter type since it is not provided by API configuration
     */
    static final String PARAMETER_TYPE_STRING = "string";

    /**
     * Parameter type "path"
     */
    static final String PARAMETER_IN_PATH = "path";

    /**
     * Parameter type "query"
     */
    static final String PARAMETER_IN_QUERY = "query";

    /**
     * Parameter type "body"
     */
    static final String PARAMETER_IN_BODY = "body";

    /**
     * Swagger element "schema" of body parameter in http://swagger.io/specification/
     */
    static final String PARAMETER_BODY_SCHEMA = "schema";

    /**
     * Swagger element "properties" of body schema in http://swagger.io/specification/
     */
    static final String PARAMETER_PROPERTIES = "properties";

    /**
     * Pattern to identify path parameters
     */
    static final Pattern PATH_PARAMETER_PATTERN = Pattern.compile("\\{(.*?)\\}");

    /**
     * Path separator character
     */
    static final String PATH_SEPARATOR = "/";

    /**
     * Swagger operations
     */
    public static final String OPERATION_HTTP_GET = "get";
    public static final String OPERATION_HTTP_POST = "post";
    public static final String OPERATION_HTTP_PUT = "put";
    public static final String OPERATION_HTTP_PATCH = "patch";
    public static final String OPERATION_HTTP_DELETE = "delete";
    public static final String OPERATION_HTTP_OPTIONS = "options";
    public static final String OPERATION_HTTP_HEAD = "head";

    /**
     * Protocols supported by API - both HTTP and HTTPS
     */
    static final int PROTOCOL_HTTP_AND_HTTPS = 0;

    /**
     * Protocols supported by API - HTTP
     */
    static final int PROTOCOL_HTTP_ONLY = 1;

    /**
     * Protocol name for HTTP
     */
    static final String PROTOCOL_HTTP = "http";

    /**
     * Protocol name for HTTPs
     */
    static final String PROTOCOL_HTTPS = "https";

    /**
     * Default host for Swagger API
     */
    public static String DEFAULT_HOST = "localhost";

    /**
     * Default port for Swagger API
     */
    public static int DEFAULT_PORT = 8290;

    /**
     *  Path param regex
     */
    public static final String PATH_PARAMETER_REGEX = "\\{[^}]+\\}";

    /**
     *  Path param normaized placeholder
     */
    public static final String NORMALIZED_PLACEHOLDER = "{}";
}
