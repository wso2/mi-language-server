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

package org.eclipse.lemminx.customservice.synapse.mediator.tryout.pojo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Params {

    private List<Property> queryParams;
    private List<Property> uriParams;
    private List<Property> functionParams;

    public Params() {

        queryParams = new ArrayList<>();
        uriParams = new ArrayList<>();
        functionParams = new ArrayList<>();
    }

    public List<Property> getQueryParams() {

        return queryParams;
    }

    public void setQueryParams(
            List<Property> queryParams) {

        this.queryParams = queryParams;
    }

    public List<Property> getUriParams() {

        return uriParams;
    }

    public void setUriParams(List<Property> uriParams) {

        this.uriParams = uriParams;
    }

    public List<Property> getFunctionParams() {

        return functionParams;
    }

    public void setFunctionParams(
            List<Property> functionParams) {

        this.functionParams = functionParams;
    }

    public void addQueryParam(Property property) {

        queryParams.add(property);
    }

    public void addUriParam(Property property) {

        uriParams.add(property);
    }

    public void addFunctionParam(Property property) {

        functionParams.add(property);
    }

    public void addQueryParams(List<Property> properties) {

        queryParams.addAll(properties);
    }

    public void addUriParams(List<Property> properties) {

        uriParams.addAll(properties);
    }

    public void addFunctionParams(List<Property> properties) {

        functionParams.addAll(properties);
    }

    public List<Property> getPropertiesByType(Type key) {

        if (key == null) {
            return Collections.emptyList();
        }
        switch (key) {
            case QUERY:
                return queryParams;
            case URI:
                return uriParams;
            case FUNC:
                return functionParams;
            default:
                return Collections.emptyList();
        }
    }

    public Params deepCopy() {

        Params params = new Params();
        params.addQueryParams(new ArrayList<>(queryParams));
        params.addUriParams(new ArrayList<>(uriParams));
        params.addFunctionParams(new ArrayList<>(functionParams));
        return params;
    }

    @Override
    public String toString() {

        return "Params{" +
                "queryParams=" + queryParams +
                ", uriParams=" + uriParams +
                ", functionParams=" + functionParams +
                '}';
    }

    public enum Type {
        QUERY,
        URI,
        FUNC
    }
}
