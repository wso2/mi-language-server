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

package org.eclipse.lemminx.customservice.synapse.dataService.queryBuilders;

import java.util.List;

public class InsertQueryBuilder {
    private String tableName;
    private String schema;
    private List<String> columns;

    public InsertQueryBuilder setTableName(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public InsertQueryBuilder setSchema(String schema) {
        this.schema = schema;
        return this;
    }

    public InsertQueryBuilder setColumns(List<String> columns) {
        this.columns = columns;
        return this;
    }

    public String build() {
        return "INSERT INTO " +
                ((schema == null || schema.trim().isEmpty()) ? "" : (schema.trim() + ".")) + tableName.trim() +
                " (" + String.join(",", columns) + ") VALUES (" +
                "?,".repeat(Math.max(0, columns.size() - 1)) + "?)";
    }
}
