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

package org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.mediator.transformation;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMText;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.Mediator;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.transformation.payload.PayloadFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.transformation.payload.PayloadFactoryArgs;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.transformation.payload.PayloadFactoryArgsArg;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.transformation.payload.PayloadFactoryFormat;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.transformation.payload.TemplateType;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.SerializerUtils;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.mediator.AbstractMediatorSerializer;

public class PayloadFactoryMediatorSerializer extends AbstractMediatorSerializer {

    @Override
    protected OMElement serializeSpecificMediator(Mediator m) {

        PayloadFactory payloadFactory = (PayloadFactory) m;
        OMElement payloadFactoryElt = fac.createOMElement("payloadFactory", synNS);

        if (payloadFactory.getMediaType() != null) {
            payloadFactoryElt.addAttribute("media-type", payloadFactory.getMediaType().name(), nullNS);
        }

        boolean isFreemarkerTemplate = false;
        if (TemplateType.FREE_MARKER.equals(payloadFactory.getTemplateType())) {
            isFreemarkerTemplate = true;
            payloadFactoryElt.addAttribute("template-type", payloadFactory.getTemplateType().getValue(), nullNS);
        }

        serializeFormat(payloadFactoryElt, payloadFactory.getFormat(), isFreemarkerTemplate);
        serializeArgs(payloadFactoryElt, payloadFactory.getArgs());

        if (payloadFactory.getDescription() != null) {
            payloadFactoryElt.addAttribute("description", payloadFactory.getDescription(), nullNS);
        }
        return payloadFactoryElt;
    }

    private void serializeFormat(OMElement payloadFactoryElt, PayloadFactoryFormat format,
                                 boolean isFreemarkerTemplate) {

        if (format != null) {
            OMElement formatElt = fac.createOMElement("format", synNS);
            if (format.getKey() != null) {
                formatElt.addAttribute("key", format.getKey(), nullNS);
            } else if (format.getContent() != null) {
                String content = format.getContent().toString();
                if (isFreemarkerTemplate) {
                    OMText text = SerializerUtils.stringToCDATA(content);
                    formatElt.addChild(text);
                } else {
                    OMNode inline;
                    if (content.startsWith("<") && content.endsWith(">")) {
                        inline = SerializerUtils.stringToOM(content);
                    } else {
                        inline = fac.createOMText(content);
                    }
                    if (inline != null) {
                        formatElt.addChild(inline);
                    }
                }
            }
            payloadFactoryElt.addChild(formatElt);
        }
    }

//    public static String removeCDATAFromPayload(String inputPayload) {
//
//        if (inputPayload.startsWith("<![CDATA[")) {
//            inputPayload = inputPayload.substring(9);
//            int i = inputPayload.lastIndexOf("]]>");
//            if (i == -1)
//                throw new IllegalStateException("argument starts with <![CDATA[ but cannot find pairing ]]>");
//            inputPayload = inputPayload.substring(0, i);
//        }
//
//        return inputPayload;
//    }

    private void serializeArgs(OMElement payloadFactoryElt, PayloadFactoryArgs args) {

        if (args != null && args.getArg() != null) {
            OMElement argsElt = fac.createOMElement("args", synNS);
            PayloadFactoryArgsArg[] argsList = args.getArg();
            for (PayloadFactoryArgsArg arg : argsList) {
                OMElement argElt = serializeArg(arg);
                if (argElt != null) {
                    argsElt.addChild(argElt);
                }
            }
            payloadFactoryElt.addChild(argsElt);
        }
    }

    private OMElement serializeArg(PayloadFactoryArgsArg arg) {

        if (arg != null) {
            OMElement argElt = fac.createOMElement("arg", synNS);
            if (arg.getValue() != null) {
                argElt.addAttribute("value", arg.getValue(), nullNS);
            } else if (arg.getExpression() != null) {
                SerializerUtils.serializeExpression(arg.getExpression(), argElt, "expression", arg);
                if (arg.getEvaluator() != null) {
                    argElt.addAttribute("evaluator", arg.getEvaluator().name(), nullNS);
                }
            }
            if (arg.isLiteral()) {
                argElt.addAttribute("literal", "true", nullNS);
            }
            return argElt;
        }
        return null;
    }

    @Override
    public String getMediatorClassName() {

        return PayloadFactory.class.getName();
    }
}
