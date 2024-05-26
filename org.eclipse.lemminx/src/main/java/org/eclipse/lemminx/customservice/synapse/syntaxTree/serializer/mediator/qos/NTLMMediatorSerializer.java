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

package org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.mediator.qos;

import org.apache.axiom.om.OMElement;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.Mediator;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.other.ntlm.Ntlm;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.mediator.AbstractMediatorSerializer;

public class NTLMMediatorSerializer extends AbstractMediatorSerializer {

    @Override
    protected OMElement serializeSpecificMediator(Mediator m) {

        Ntlm ntlm = (Ntlm) m;
        OMElement ntlmElt = fac.createOMElement("NTLM", synNS);

        serializeAttributes(ntlmElt, ntlm);

        return ntlmElt;
    }

    private void serializeAttributes(OMElement ntlmElt, Ntlm ntlm) {

        if (ntlm.getDomain() != null) {
            ntlmElt.addAttribute("domain", ntlm.getDomain(), nullNS);
        }
        if (ntlm.getHost() != null) {
            ntlmElt.addAttribute("host", ntlm.getHost(), nullNS);
        }
        if (ntlm.getUsername() != null) {
            ntlmElt.addAttribute("username", ntlm.getUsername(), nullNS);
        }
        if (ntlm.getPassword() != null) {
            ntlmElt.addAttribute("password", ntlm.getPassword(), nullNS);
        }
        if (ntlm.getNtlmVersion() != null) {
            ntlmElt.addAttribute("ntlmVersion", ntlm.getNtlmVersion(), nullNS);
        }
        if (ntlm.getDescription() != null) {
            ntlmElt.addAttribute("description", ntlm.getDescription(), nullNS);
        }
    }

    @Override
    public String getMediatorClassName() {

        return Ntlm.class.getName();
    }
}
