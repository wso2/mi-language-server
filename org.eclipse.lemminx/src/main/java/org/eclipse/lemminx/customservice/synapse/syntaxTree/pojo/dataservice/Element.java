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

package org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.dataservice;

import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.STNode;

import java.util.ArrayList;
import java.util.List;

public class Element extends STNode {

    List<Element> elements;
    List<Attribute> attributes;
    List<CallQuery> callQueries;
    String name;
    String column;
    String requiredRoles;
    String export;
    String exportType;
    String xsdType;
    String namespace;
    boolean optional;
    String arrayName;
    String queryParam;

    public List<Element> getElements() {

        return elements;
    }

    public void setElements(List<Element> elements) {

        this.elements = elements;
    }

    public List<Attribute> getAttributes() {

        return attributes;
    }

    public void setAttributes(List<Attribute> attributes) {

        this.attributes = attributes;
    }

    public List<CallQuery> getCallQueries() {

        return callQueries;
    }

    public void setCallQueries(List<CallQuery> callQueries) {

        this.callQueries = callQueries;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public String getColumn() {

        return column;
    }

    public void setColumn(String column) {

        this.column = column;
    }

    public String getRequiredRoles() {

        return requiredRoles;
    }

    public void setRequiredRoles(String requiredRoles) {

        this.requiredRoles = requiredRoles;
    }

    public String getExport() {

        return export;
    }

    public void setExport(String export) {

        this.export = export;
    }

    public String getExportType() {

        return exportType;
    }

    public void setExportType(String exportType) {

        this.exportType = exportType;
    }

    public String getXsdType() {

        return xsdType;
    }

    public void setXsdType(String xsdType) {

        this.xsdType = xsdType;
    }

    public String getNamespace() {

        return namespace;
    }

    public void setNamespace(String namespace) {

        this.namespace = namespace;
    }

    public boolean isOptional() {

        return optional;
    }

    public void setOptional(boolean optional) {

        this.optional = optional;
    }

    public String getArrayName() {

        return arrayName;
    }

    public void setArrayName(String arrayName) {

        this.arrayName = arrayName;
    }

    public String getQueryParam() {

        return queryParam;
    }

    public void setQueryParam(String queryParam) {

        this.queryParam = queryParam;
    }

    public void addElement(Element resultElement) {

        if (elements == null) {
            elements = new ArrayList<>();
        }
        elements.add(resultElement);
    }

    public void addAttribute(Attribute attribute) {

        if (attributes == null) {
            attributes = new ArrayList<>();
        }
        attributes.add(attribute);
    }

    public void addCallQuery(CallQuery callQuery) {

        if (callQueries == null) {
            callQueries = new ArrayList<>();
        }
        callQueries.add(callQuery);
    }
}
