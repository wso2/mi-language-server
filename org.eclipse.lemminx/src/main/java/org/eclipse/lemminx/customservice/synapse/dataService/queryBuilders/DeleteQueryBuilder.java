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
import java.util.stream.Collectors;

public class DeleteQueryBuilder {
    private String tableName;
    private String schema;
    private List<String> primaryKeys;

    public DeleteQueryBuilder setTableName(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public DeleteQueryBuilder setSchema(String schema) {
        this.schema = schema;
        return this;
    }

    public DeleteQueryBuilder setPrimaryKeys(List<String> primaryKeys) {
        this.primaryKeys = primaryKeys;
        return this;
    }

    public String build() {
        StringBuilder statement = new StringBuilder();
        statement.append("DELETE FROM ");
        statement.append((schema == null || schema.trim().isEmpty()) ? "" : (schema.trim() + "."))
                .append(tableName.trim()).append(" WHERE ");
        statement.append(String.join(" AND ", primaryKeys.stream().map(pKey -> pKey + "=?")
                .collect(Collectors.toList())));
        return statement.toString();
    }
}

