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

package org.eclipse.lemminx.customservice.synapse.expression;

import java.util.List;

public class ExpressionConstants {

    public static final String EXPRESSION_PREFIX = "${";
    public static final List<String> OPERATORS_CHARS =
            List.of("+", "-", "*", "/", "?", ":", ">", "<", ">= ", "<=", "==", "!=", "&&", "||", "and", "or",
                    "=", "&", "|");
    public static final String LABEL = "label";
    public static final String INSERT_TEXT = "insertText";
    public static final String DETAIL = "details";
    public static final String CATEGORY = "category";
    public static final String SIGNATURE = "signature";
    public static final String VARS = "vars";
    public static final String PROPERTIES = "properties";
    public static final String PROPS = "props";
    public static final String PARAMS = "params";
    public static final String AXIS2 = "axis2";
    public static final String AXIS2_CLIENT = "axis2Client";
    public static final String AXIS2_TRANSPORT = "axis2Transport";
    public static final String AXIS2_OPERATION = "axis2Operation";
    public static final String SYNAPSE = "synapse";
    public static final String HEADERS = "headers";
    public static final String PAYLOAD = "payload";
    public static final List<String> ATTRIBUTES_SECOND_LEVEL = List.of(SYNAPSE, AXIS2);
    public static final String QUERY_PARAMS = "queryParams";
    public static final String PATH_PARAMS = "pathParams";
    public static final String FUNCTION_PARAMS = "functionParams";
    public static final String CONFIGS = "configs";
    public static final List<String> ROOT_LEVEL_TOKENS =
            List.of(VARS, PROPERTIES, PROPS, PARAMS, HEADERS, PAYLOAD, CONFIGS);
    public static final List<String> PARAMS_SECOND_LEVEL = List.of(QUERY_PARAMS, PATH_PARAMS, FUNCTION_PARAMS);
    public static final String OBJECT = "Object";
    public static final String ITEMS = "items";
    public static final String SORT_TEXT = "sortText";
    public static final String ARRAY_COMPLETION_INSERT_TEXT = "[0]";
    public static final String ARRAY_COMPLETION_LABEL = "[]";

    private ExpressionConstants() {

    }
}
