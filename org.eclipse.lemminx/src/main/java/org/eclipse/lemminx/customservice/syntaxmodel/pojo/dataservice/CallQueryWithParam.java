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

import org.eclipse.lemminx.customservice.syntaxmodel.pojo.STNode;

public class CallQueryWithParam extends STNode {

    String name;
    String queryParam;
    String column;
    String param;

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public String getQueryParam() {

        return queryParam;
    }

    public void setQueryParam(String queryParam) {

        this.queryParam = queryParam;
    }

    public String getColumn() {

        return column;
    }

    public void setColumn(String column) {

        this.column = column;
    }

    public String getParam() {

        return param;
    }

    public void setParam(String param) {

        this.param = param;
    }
}
