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
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.other.OauthService;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.mediator.AbstractMediatorSerializer;

public class OauthMediatorSerializer extends AbstractMediatorSerializer {

    @Override
    protected OMElement serializeSpecificMediator(Mediator m) {

        OauthService oauthService = (OauthService) m;
        OMElement oauthServiceElt = fac.createOMElement("oauthService", synNS);

        serializeAttributes(oauthService, oauthServiceElt);

        return oauthServiceElt;
    }

    private void serializeAttributes(OauthService oauthService, OMElement oauthServiceElt) {

        if (oauthService.getRemoteServiceUrl() != null) {
            oauthServiceElt.addAttribute("remoteServiceUrl", oauthService.getRemoteServiceUrl(), nullNS);
        }
        if (oauthService.getUsername() != null) {
            oauthServiceElt.addAttribute("username", oauthService.getUsername(), nullNS);
        }
        if (oauthService.getPassword() != null) {
            oauthServiceElt.addAttribute("password", oauthService.getPassword(), nullNS);
        }
        if (oauthService.getDescription() != null) {
            oauthServiceElt.addAttribute("description", oauthService.getDescription(), nullNS);
        }
    }

    @Override
    public String getMediatorClassName() {

        return OauthService.class.getName();
    }
}
