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

import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

import java.util.List;

public class Node {

    private Either<Range, List<Range>> range;
    private String value;
    private String key;
    private String displayValue;

    public Node() {
    }

    public Node(String value, Either<Range, List<Range>> range) {
        this.range = range;
        this.value = value;
    }

    public Node(String key, String value, Either<Range, List<Range>> range) {
        this.range = range;
        this.key = key;
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public void setDisplayValue(String displayValue) {
        this.displayValue = displayValue;
    }
}
