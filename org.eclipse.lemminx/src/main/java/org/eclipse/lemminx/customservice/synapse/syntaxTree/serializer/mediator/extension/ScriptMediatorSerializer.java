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

package org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.mediator.extension;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMText;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.Mediator;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.extension.Script;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.SerializerUtils;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.mediator.AbstractMediatorSerializer;

public class ScriptMediatorSerializer extends AbstractMediatorSerializer {

    @Override
    protected OMElement serializeSpecificMediator(Mediator m) {

        Script script = (Script) m;
        OMElement scriptElt = fac.createOMElement("script", synNS);

        serializeAttributes(script, scriptElt);
        serializeChildren(script, scriptElt);

        return scriptElt;
    }

    private void serializeAttributes(Script script, OMElement scriptElt) {

        if (script.getLanguage() != null) {
            scriptElt.addAttribute("language", script.getLanguage().name(), nullNS);
        } else {
            handleException("Script mediator must have a language attribute");
        }
        if (script.getKey() != null) {
            scriptElt.addAttribute("key", script.getKey(), nullNS);
        }
        if (script.getFunction() != null) {
            scriptElt.addAttribute("function", script.getFunction(), nullNS);
        }
        if (script.getDescription() != null) {
            scriptElt.addAttribute("description", script.getDescription(), nullNS);
        }
    }

    private void serializeChildren(Script script, OMElement scriptElt) {

        serializeIncludes(script.getInclude(), scriptElt);
        serializeContent(script.getContent(), scriptElt);
    }

    private void serializeIncludes(String[] includes, OMElement scriptElt) {

        if (includes != null) {
            for (String include : includes) {
                OMElement includeElt = fac.createOMElement("include", synNS);
                includeElt.addAttribute("key", include, nullNS);
                scriptElt.addChild(includeElt);
            }
        }
    }

    private void serializeContent(Object[] contents, OMElement scriptElt) {

        if (contents != null) {
            for (Object content : contents) {
                if (content.toString().startsWith("<![CDATA[")) {

                    OMText cdata = SerializerUtils.stringToCDATA(content.toString());
                    if (cdata != null) {
                        scriptElt.addChild(cdata);
                    }
                } else {
                    OMText text = fac.createOMText(content.toString());
                    scriptElt.addChild(text);
                }
            }
        }
    }

    @Override
    public String getMediatorClassName() {

        return Script.class.getName();
    }
}
