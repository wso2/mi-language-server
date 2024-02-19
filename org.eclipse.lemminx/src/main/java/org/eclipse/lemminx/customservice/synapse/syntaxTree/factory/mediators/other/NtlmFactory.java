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

package org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.other;

import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.AbstractMediatorFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.STNode;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.Mediator;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.other.ntlm.Ntlm;
import org.eclipse.lemminx.customservice.synapse.utils.Constant;
import org.eclipse.lemminx.dom.DOMElement;

public class NtlmFactory extends AbstractMediatorFactory {

    private static final String NTLM = "NTLM";

    @Override
    public Mediator createSpecificMediator(DOMElement element) {

        Ntlm ntlm = new Ntlm();
        ntlm.elementNode(element);
        populateAttributes(ntlm, element);
        return ntlm;
    }

    @Override
    public void populateAttributes(STNode node, DOMElement element) {

        String domain = element.getAttribute(Constant.DOMAIN);
        if (domain != null && !domain.isEmpty()) {
            ((Ntlm) node).setDomain(domain);
        }
        String host = element.getAttribute(Constant.HOST);
        if (host != null && !host.isEmpty()) {
            ((Ntlm) node).setHost(host);
        }
        String username = element.getAttribute(Constant.USERNAME);
        if (username != null && !username.isEmpty()) {
            ((Ntlm) node).setUsername(username);
        }
        String password = element.getAttribute(Constant.PASSWORD);
        if (password != null && !password.isEmpty()) {
            ((Ntlm) node).setPassword(password);
        }
        String ntlmVersion = element.getAttribute(Constant.NTLM_VERSION);
        if (ntlmVersion != null && !ntlmVersion.isEmpty()) {
            ((Ntlm) node).setNtlmVersion(ntlmVersion);
        }
        String description = element.getAttribute(Constant.DESCRIPTION);
        if (description != null && !description.isEmpty()) {
            ((Ntlm) node).setDescription(description);
        }
    }

    @Override
    public String getTagName() {

        return NTLM;
    }
}
