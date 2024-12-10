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

package org.eclipse.lemminx.customservice.synapse.mediatorService.pojo;

public class ExpressionFieldValue {

    private String value;
    private boolean isExpression;
    private Namespace namespaces[];

    public ExpressionFieldValue(String value, boolean isExpression, Namespace[] namespaces) {
        this.value = value;
        this.isExpression = isExpression;
        this.namespaces = namespaces;
    }

    public String getValue() {
        return value;
    }

    public boolean isExpression() {
        return isExpression;
    }

    public Namespace[] getNamespaces() {
        return namespaces;
    }
}
