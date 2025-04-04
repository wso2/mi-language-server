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

package org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.api;

import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.STNode;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.misc.common.Sequence;

public class APIResource extends STNode {

    String api;
    Sequence inSequence;
    Sequence outSequence;
    Sequence faultSequence;
    String[] methods;
    String[] protocol;
    String inSequenceAttribute;
    String outSequenceAttribute;
    String faultSequenceAttribute;
    String uriTemplate;
    String urlMapping;

    public String getApi() {

        return api;
    }

    public void setApi(String api) {

        this.api = api;
    }

    public Sequence getInSequence() {

        return inSequence;
    }

    public void setInSequence(Sequence inSequence) {

        this.inSequence = inSequence;
    }

    public Sequence getOutSequence() {

        return outSequence;
    }

    public void setOutSequence(Sequence outSequence) {

        this.outSequence = outSequence;
    }

    public Sequence getFaultSequence() {

        return faultSequence;
    }

    public void setFaultSequence(Sequence faultSequence) {

        this.faultSequence = faultSequence;
    }

    public String[] getMethods() {

        return methods;
    }

    public void setMethods(String[] methods) {

        this.methods = methods;
    }

    public String[] getProtocol() {

        return protocol;
    }

    public void setProtocol(String[] protocol) {

        this.protocol = protocol;
    }

    public String getInSequenceAttribute() {

        return inSequenceAttribute;
    }

    public void setInSequenceAttribute(String inSequenceAttribute) {

        this.inSequenceAttribute = inSequenceAttribute;
    }

    public String getOutSequenceAttribute() {

        return outSequenceAttribute;
    }

    public void setOutSequenceAttribute(String outSequenceAttribute) {

        this.outSequenceAttribute = outSequenceAttribute;
    }

    public String getFaultSequenceAttribute() {

        return faultSequenceAttribute;
    }

    public void setFaultSequenceAttribute(String faultSequenceAttribute) {

        this.faultSequenceAttribute = faultSequenceAttribute;
    }

    public String getUriTemplate() {

        return uriTemplate;
    }

    public void setUriTemplate(String uriTemplate) {

        this.uriTemplate = uriTemplate;
    }

    public String getUrlMapping() {

        return urlMapping;
    }

    public void setUrlMapping(String urlMapping) {

        this.urlMapping = urlMapping;
    }

    public void addMethod(String method) {

        if (this.methods == null) {
            this.methods = new String[1];
            this.methods[0] = method;
        } else {
            String[] newMethod = new String[this.methods.length + 1];
            System.arraycopy(this.methods, 0, method, 0, this.methods.length);
            newMethod[this.methods.length] = method;
            this.methods = newMethod;
        }
    }

    public String getResourcePath() {

        if (this.urlMapping != null) {
            return this.urlMapping;
        } else {
            return this.uriTemplate;
        }
    }
}