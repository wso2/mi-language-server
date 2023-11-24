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

public class Query extends STNode {

    QueryElements[] queryElements;
    String id;
    String useConfig;
    boolean returnGeneratedKeys;
    String inputEventTrigger;
    String keyColumns;
    String returnUpdatedRowCount;

    public QueryElements[] getQueryElements() {

        return queryElements;
    }

    public void setQueryElements(QueryElements[] queryElements) {

        this.queryElements = queryElements;
    }

    public String getId() {

        return id;
    }

    public void setId(String id) {

        this.id = id;
    }

    public String getUseConfig() {

        return useConfig;
    }

    public void setUseConfig(String useConfig) {

        this.useConfig = useConfig;
    }

    public boolean isReturnGeneratedKeys() {

        return returnGeneratedKeys;
    }

    public void setReturnGeneratedKeys(boolean returnGeneratedKeys) {

        this.returnGeneratedKeys = returnGeneratedKeys;
    }

    public String getInputEventTrigger() {

        return inputEventTrigger;
    }

    public void setInputEventTrigger(String inputEventTrigger) {

        this.inputEventTrigger = inputEventTrigger;
    }

    public String getKeyColumns() {

        return keyColumns;
    }

    public void setKeyColumns(String keyColumns) {

        this.keyColumns = keyColumns;
    }

    public String getReturnUpdatedRowCount() {

        return returnUpdatedRowCount;
    }

    public void setReturnUpdatedRowCount(String returnUpdatedRowCount) {

        this.returnUpdatedRowCount = returnUpdatedRowCount;
    }
}
