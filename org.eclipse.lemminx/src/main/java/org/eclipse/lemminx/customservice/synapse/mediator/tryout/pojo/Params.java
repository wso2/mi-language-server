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
    private List<Property> pathParams;
    private List<Property> functionParams;

    public Params() {

        queryParams = new ArrayList<>();
        pathParams = new ArrayList<>();
        functionParams = new ArrayList<>();
    }

    public List<Property> getQueryParams() {

        return queryParams;
    }

    public void setQueryParams(
            List<Property> queryParams) {

        this.queryParams = queryParams;
    }

    public List<Property> getPathParams() {

        return pathParams;
    }

    public void setPathParams(List<Property> pathParams) {

        this.pathParams = pathParams;
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

    public void addPathParam(Property property) {

        pathParams.add(property);
    }

    public void addFunctionParam(Property property) {

        functionParams.add(property);
    }

    public void addQueryParams(List<Property> properties) {

        queryParams.addAll(properties);
    }

    public void addPathParams(List<Property> properties) {

        pathParams.addAll(properties);
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
            case PATH:
                return pathParams;
            case FUNC:
                return functionParams;
            default:
                return Collections.emptyList();
        }
    }

    public Params deepCopy() {

        Params params = new Params();
        params.addQueryParams(new ArrayList<>(queryParams));
        params.addPathParams(new ArrayList<>(pathParams));
        params.addFunctionParams(new ArrayList<>(functionParams));
        return params;
    }

    @Override
    public String toString() {

        return "Params{" +
                "queryParams=" + queryParams +
                ", uriParams=" + pathParams +
                ", functionParams=" + functionParams +
                '}';
    }

    public enum Type {
        QUERY,
        PATH,
        FUNC
    }
}
