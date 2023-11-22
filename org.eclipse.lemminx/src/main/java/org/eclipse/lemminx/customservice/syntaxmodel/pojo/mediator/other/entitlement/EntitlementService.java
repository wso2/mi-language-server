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

package org.eclipse.lemminx.customservice.syntaxmodel.pojo.mediator.other.entitlement;

import org.eclipse.lemminx.customservice.syntaxmodel.pojo.mediator.Mediator;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.misc.common.Sequence;

public class EntitlementService extends Mediator {

    Sequence onReject;
    Sequence onAccept;
    Sequence advice;
    Sequence obligations;
    String remoteServiceUrl;
    String remoteServiceUserName;
    String remoteServicePassword;
    String callbackClass;
    String client;
    String thriftHost;
    String thriftPort;
    String onRejectAttribute;
    String onAcceptAttribute;
    String adviceAttribute;
    String obligationsAttribute;
    String description;

    public Sequence getOnReject() {

        return onReject;
    }

    public void setOnReject(Sequence onReject) {

        this.onReject = onReject;
    }

    public Sequence getOnAccept() {

        return onAccept;
    }

    public void setOnAccept(Sequence onAccept) {

        this.onAccept = onAccept;
    }

    public Sequence getAdvice() {

        return advice;
    }

    public void setAdvice(Sequence advice) {

        this.advice = advice;
    }

    public Sequence getObligations() {

        return obligations;
    }

    public void setObligations(Sequence obligations) {

        this.obligations = obligations;
    }

    public String getRemoteServiceUrl() {

        return remoteServiceUrl;
    }

    public void setRemoteServiceUrl(String remoteServiceUrl) {

        this.remoteServiceUrl = remoteServiceUrl;
    }

    public String getRemoteServiceUserName() {

        return remoteServiceUserName;
    }

    public void setRemoteServiceUserName(String remoteServiceUserName) {

        this.remoteServiceUserName = remoteServiceUserName;
    }

    public String getRemoteServicePassword() {

        return remoteServicePassword;
    }

    public void setRemoteServicePassword(String remoteServicePassword) {

        this.remoteServicePassword = remoteServicePassword;
    }

    public String getCallbackClass() {

        return callbackClass;
    }

    public void setCallbackClass(String callbackClass) {

        this.callbackClass = callbackClass;
    }

    public String getClient() {

        return client;
    }

    public void setClient(String client) {

        this.client = client;
    }

    public String getThriftHost() {

        return thriftHost;
    }

    public void setThriftHost(String thriftHost) {

        this.thriftHost = thriftHost;
    }

    public String getThriftPort() {

        return thriftPort;
    }

    public void setThriftPort(String thriftPort) {

        this.thriftPort = thriftPort;
    }

    public String getOnRejectAttribute() {

        return onRejectAttribute;
    }

    public void setOnRejectAttribute(String onRejectAttribute) {

        this.onRejectAttribute = onRejectAttribute;
    }

    public String getOnAcceptAttribute() {

        return onAcceptAttribute;
    }

    public void setOnAcceptAttribute(String onAcceptAttribute) {

        this.onAcceptAttribute = onAcceptAttribute;
    }

    public String getAdviceAttribute() {

        return adviceAttribute;
    }

    public void setAdviceAttribute(String adviceAttribute) {

        this.adviceAttribute = adviceAttribute;
    }

    public String getObligationsAttribute() {

        return obligationsAttribute;
    }

    public void setObligationsAttribute(String obligationsAttribute) {

        this.obligationsAttribute = obligationsAttribute;
    }

    public String getDescription() {

        return description;
    }

    public void setDescription(String description) {

        this.description = description;
    }
}