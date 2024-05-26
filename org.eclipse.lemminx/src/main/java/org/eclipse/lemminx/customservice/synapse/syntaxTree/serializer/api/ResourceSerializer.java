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

package org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.api;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.api.APIResource;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.InlineSequenceSerializer;
import org.eclipse.lemminx.customservice.synapse.utils.Constant;

public class ResourceSerializer {

    private static final OMFactory fac = OMAbstractFactory.getOMFactory();

    public static OMElement serializeResource(APIResource resource) {

        OMElement resourceElt = fac.createOMElement("resource", Constant.SYNAPSE_OMNAMESPACE);
        String[] methods = resource.getMethods();
        if (methods.length > 0) {
            String value = "";
            for (String method : methods) {
                value += method + " ";
            }
            resourceElt.addAttribute("methods", value.trim(), null);
        }

        if (resource.getProtocol() != null && resource.getProtocol().length == 1) {
            if ("http".equalsIgnoreCase(resource.getProtocol()[0])) {
                resourceElt.addAttribute("protocol", "http", null);
            } else if ("https".equalsIgnoreCase(resource.getProtocol()[0])) {
                resourceElt.addAttribute("protocol", "https", null);
            }
        }

        if (resource.getUriTemplate() != null) {
            resourceElt.addAttribute("uri-template", resource.getUriTemplate(), null);
        } else if (resource.getUrlMapping() != null) {
            resourceElt.addAttribute("url-mapping", resource.getUrlMapping(), null);
        }

        if (resource.getInSequenceAttribute() != null) {
            resourceElt.addAttribute("inSequence", resource.getInSequenceAttribute(), null);
        } else if (resource.getInSequence() != null) {
            OMElement inSequenceElt = InlineSequenceSerializer.serializeAnonymousSequence(resource.getInSequence());
            inSequenceElt.setLocalName("inSequence");
            resourceElt.addChild(inSequenceElt);
        }

        if (resource.getOutSequenceAttribute() != null) {
            resourceElt.addAttribute("outSequence", resource.getOutSequenceAttribute(), null);
        } else if (resource.getOutSequence() != null) {
            OMElement outSequenceElt = InlineSequenceSerializer.serializeAnonymousSequence(resource.getOutSequence());
            outSequenceElt.setLocalName("outSequence");
            resourceElt.addChild(outSequenceElt);
        }

        if (resource.getFaultSequenceAttribute() != null) {
            resourceElt.addAttribute("faultSequence", resource.getFaultSequenceAttribute(), null);
        } else if (resource.getFaultSequence() != null) {
            OMElement faultSequenceElt =
                    InlineSequenceSerializer.serializeAnonymousSequence(resource.getFaultSequence());
            faultSequenceElt.setLocalName("faultSequence");
            resourceElt.addChild(faultSequenceElt);
        }

        return resourceElt;
    }
}
