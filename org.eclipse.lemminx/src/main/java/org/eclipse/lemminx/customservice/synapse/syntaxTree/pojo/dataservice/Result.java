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

public class Result extends STNode {

    List<Element> elements;
    List<Attribute> attributes;
    List<CallQuery> callQueries;
    String element;
    String rowName;
    String defaultNamespace;
    Boolean useColumnNumbers;
    Boolean escapeNonPrintableChar;
    String xsltPath;
    String rdfBaseURI;
    String outputType;

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

    public String getElement() {

        return element;
    }

    public void setElement(String element) {

        this.element = element;
    }

    public String getRowName() {

        return rowName;
    }

    public void setRowName(String rowName) {

        this.rowName = rowName;
    }

    public String getDefaultNamespace() {

        return defaultNamespace;
    }

    public void setDefaultNamespace(String defaultNamespace) {

        this.defaultNamespace = defaultNamespace;
    }

    public Boolean getUseColumnNumbers() {

        return useColumnNumbers;
    }

    public void setUseColumnNumbers(Boolean useColumnNumbers) {

        this.useColumnNumbers = useColumnNumbers;
    }

    public Boolean getEscapeNonPrintableChar() {

        return escapeNonPrintableChar;
    }

    public void setEscapeNonPrintableChar(Boolean escapeNonPrintableChar) {

        this.escapeNonPrintableChar = escapeNonPrintableChar;
    }

    public String getXsltPath() {

        return xsltPath;
    }

    public void setXsltPath(String xsltPath) {

        this.xsltPath = xsltPath;
    }

    public String getRdfBaseURI() {

        return rdfBaseURI;
    }

    public void setRdfBaseURI(String rdfBaseURI) {

        this.rdfBaseURI = rdfBaseURI;
    }

    public String getOutputType() {

        return outputType;
    }

    public void setOutputType(String outputType) {

        this.outputType = outputType;
    }

    public void addElement(Element element) {

        if (this.elements == null) {
            this.elements = new ArrayList<>();
        }
        this.elements.add(element);
    }

    public void addAttribute(Attribute attribute) {

        if (this.attributes == null) {
            this.attributes = new ArrayList<>();
        }
        this.attributes.add(attribute);
    }

    public void addCallQuery(CallQuery callQuery) {

        if (this.callQueries == null) {
            this.callQueries = new ArrayList<>();
        }
        this.callQueries.add(callQuery);
    }
}
