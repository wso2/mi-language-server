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

package org.eclipse.lemminx.customservice.syntaxmodel.pojo.proxy;

import org.eclipse.lemminx.customservice.syntaxmodel.pojo.STNode;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.endpoint.NamedEndpoint;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.misc.common.Sequence;

public class ProxyTarget extends STNode {

    Sequence inSequence;
    Sequence outSequence;
    Sequence faultSequence;
    NamedEndpoint endpoint;
    String inSequenceAttribute;
    String outSequenceAttribute;
    String faultSequenceAttribute;
    String endpointAttribute;

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

    public NamedEndpoint getEndpoint() {

        return endpoint;
    }

    public void setEndpoint(NamedEndpoint endpoint) {

        this.endpoint = endpoint;
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

    public String getEndpointAttribute() {

        return endpointAttribute;
    }

    public void setEndpointAttribute(String endpointAttribute) {

        this.endpointAttribute = endpointAttribute;
    }
}