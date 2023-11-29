/*
 * Copyright (c) 2023, WSO2 LLC. (http://www.wso2.com).
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

package org.eclipse.lemminx.customservice.syntaxmodel.pojo.dataservice;

import java.util.Optional;

public class QueryElements {

    Optional<Sql> sql;
    Optional<Expression> expression;
    Optional<Sparql> sparql;
    Optional<QueryProperties> properties;
    Optional<Object> result;
    Optional<Param> param;

    public Optional<Sql> getSql() {

        return sql;
    }

    public void setSql(Optional<Sql> sql) {

        this.sql = sql;
    }

    public Optional<Expression> getExpression() {

        return expression;
    }

    public void setExpression(Optional<Expression> expression) {

        this.expression = expression;
    }

    public Optional<Sparql> getSparql() {

        return sparql;
    }

    public void setSparql(Optional<Sparql> sparql) {

        this.sparql = sparql;
    }

    public Optional<QueryProperties> getProperties() {

        return properties;
    }

    public void setProperties(Optional<QueryProperties> properties) {

        this.properties = properties;
    }

    public Optional<Object> getResult() {

        return result;
    }

    public void setResult(Optional<Object> result) {

        this.result = result;
    }

    public Optional<Param> getParam() {

        return param;
    }

    public void setParam(Optional<Param> param) {

        this.param = param;
    }
}
