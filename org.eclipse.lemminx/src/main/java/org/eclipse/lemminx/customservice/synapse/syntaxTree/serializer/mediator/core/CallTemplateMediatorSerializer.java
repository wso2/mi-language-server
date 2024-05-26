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

package org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.mediator.core;

import org.apache.axiom.om.OMElement;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.Mediator;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.CallTemplate;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.WithParam;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.SerializerUtils;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.mediator.AbstractMediatorSerializer;

public class CallTemplateMediatorSerializer extends AbstractMediatorSerializer {

    @Override
    protected OMElement serializeSpecificMediator(Mediator m) {

        CallTemplate callTemplate = (CallTemplate) m;
        OMElement callTemplateElt = fac.createOMElement("call-template", synNS);

        if (callTemplate.getTarget() != null) {
            callTemplateElt.addAttribute("target", callTemplate.getTarget(), nullNS);
        }

        if (callTemplate.getOnError() != null) {
            callTemplateElt.addAttribute("onError", callTemplate.getOnError(), nullNS);
        }

        WithParam[] withParams = callTemplate.getWithParam();

        serializeParams(withParams, callTemplateElt);

        if (callTemplate.getDescription() != null) {
            callTemplateElt.addAttribute("description", callTemplate.getDescription(), nullNS);
        }
        return callTemplateElt;
    }

    private void serializeParams(WithParam[] withParams, OMElement callTemplateElt) {

        if (withParams != null) {
            for (WithParam withParam : withParams) {
                OMElement withParamElt = fac.createOMElement("with-param", synNS);
                if (withParam.getName() != null) {
                    withParamElt.addAttribute("name", withParam.getName(), nullNS);
                }
                String value = withParam.getValue();
                if (value != null) {
                    withParamElt.addAttribute("value", value, nullNS);
                    if (value.matches("^\\{.*}$")) {
                        SerializerUtils.serializeNamespaces(withParam, withParamElt);
                    }
                }
                callTemplateElt.addChild(withParamElt);
            }
        }
    }

    @Override
    public String getMediatorClassName() {

        return CallTemplate.class.getName();
    }
}
