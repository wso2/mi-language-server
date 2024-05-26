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

package org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.template.Template;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.template.TemplateParameter;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.endpoint.EndpointSerializer;
import org.eclipse.lemminx.customservice.synapse.utils.Constant;

import java.util.logging.Level;
import java.util.logging.Logger;

public class TemplateSerializer {

    private static Logger log = Logger.getLogger(TemplateSerializer.class.getName());

    private static final OMFactory fac = OMAbstractFactory.getOMFactory();

    public static String serializeTemplate(Template template) {

        OMElement templateElt = fac.createOMElement("template", Constant.SYNAPSE_OMNAMESPACE);

        serializeAttributes(template, templateElt);
        serializeChildren(template, templateElt);

        return templateElt.toString();
    }

    private static void serializeAttributes(Template template, OMElement templateElt) {

        if (template.getName() != null) {
            templateElt.addAttribute("name", template.getName(), null);
        }
        if (template.getOnError() != null) {
            templateElt.addAttribute("onError", template.getOnError(), null);
        }
    }

    private static void serializeChildren(Template template, OMElement templateElt) {

        if (template.getParameter() != null) {
            serializeParameters(template.getParameter(), templateElt);
        }
        if (template.getEndpoint() != null) {
            OMElement endpointElt = EndpointSerializer.serializeEndpoint(template.getEndpoint());
            templateElt.addChild(endpointElt);
        } else if (template.getSequence() != null) {
            OMElement sequenceElt =
                    InlineSequenceSerializer.serializeAnonymousSequence(template.getSequence().getMediatorList());
            templateElt.addChild(sequenceElt);
        } else {
            handleException("Template must have either an endpoint or a sequence");
        }

    }

    private static void serializeParameters(TemplateParameter[] parameter, OMElement templateElt) {

        for (TemplateParameter param : parameter) {

            OMElement paramElt = fac.createOMElement("parameter", Constant.SYNAPSE_OMNAMESPACE);
            String paramNSPrefix = param.getParamNamespacePrefix();
            if (paramNSPrefix != null) {
                String uri = param.getNamespaces().get("xmlns:" + paramNSPrefix);
                if (uri != null) {
                    paramElt.setNamespace(fac.createOMNamespace(uri, paramNSPrefix));
                } else {
                    handleException("Namespace:" + paramNSPrefix + " is not defined in the parameter");
                }
            }
            if (param.getName() != null) {
                paramElt.addAttribute("name", param.getName(), null);
            } else {
                handleException("Parameter name is required");
            }
            if (param.getDefaultValue() != null) {
                paramElt.addAttribute("defaultValue", param.getDefaultValue(), null);
            }
            paramElt.addAttribute("isMandatory", String.valueOf(param.isMandatory()), null);
            templateElt.addChild(paramElt);
        }
    }

    private static void handleException(String s) {

        log.log(Level.SEVERE, s);
        throw new InvalidConfigurationException(s);
    }
}
