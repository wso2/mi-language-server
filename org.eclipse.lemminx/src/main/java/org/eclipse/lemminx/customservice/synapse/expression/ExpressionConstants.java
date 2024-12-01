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

    public static final List<String>
            OPERATORS = List.of("+", "-", "*", "/", "?", ":", ">", "<", ">= ", "<=", "==", "!=", "&&", "||");
    public static final String LABEL = "label";
    public static final String INSERT_TEXT = "insertText";
    public static final String DETAIL = "details";
    public static final String CATEGORY = "category";
    public static final String SIGNATURE = "signature";
    public static final String VAR = "var";
    public static final String ATTRIBUTES = "attributes";
    public static final String AXIS2 = "axis2";
    public static final String AXIS2_CLIENT = "axis2Client";
    public static final String AXIS2_TRANSPORT = "axis2Transport";
    public static final String AXIS2_OPERATION = "axis2Operation";
    public static final String SYNAPSE = "synapse";
    public static final String HEADERS = "headers";
    public static final String PAYLOAD = "payload";
    public static final List<String> ATTRIBUTES_SECOND_LEVEL =
            List.of(SYNAPSE, AXIS2, AXIS2_CLIENT, AXIS2_TRANSPORT, AXIS2_OPERATION);

    private ExpressionConstants() {

    }
}
