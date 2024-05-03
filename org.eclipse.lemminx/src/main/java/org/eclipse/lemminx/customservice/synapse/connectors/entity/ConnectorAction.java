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

package org.eclipse.lemminx.customservice.synapse.connectors.entity;

import java.util.ArrayList;
import java.util.List;

public class ConnectorAction {

    private String name;
    private String tag;
    List<String> parameters;
    private String description;
    private Boolean isHidden;

    public ConnectorAction() {

        parameters = new ArrayList<>();
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public String getDescription() {

        return description;
    }

    public void setDescription(String description) {

        this.description = description;
    }

    public String getTag() {

        return tag;
    }

    public void setTag(String tag) {

        this.tag = tag;
    }

    public void addParameter(String parameter) {

        parameters.add(parameter);
    }

    public List<String> getParameters() {

        return parameters;
    }

    public void setParameters(List<String> parameters) {

        this.parameters = parameters;
    }

    public Boolean getHidden() {

        return isHidden;
    }

    public void setHidden(Boolean hidden) {

        isHidden = hidden;
    }
}
