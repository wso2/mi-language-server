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
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.Mediator;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.transformation.xslt.Xslt;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.transformation.xslt.XsltFeature;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.transformation.xslt.XsltResource;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.SerializerUtils;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.mediator.AbstractMediatorSerializer;

public class XsltMediatorSerializer extends AbstractMediatorSerializer {

    @Override
    protected OMElement serializeSpecificMediator(Mediator m) {

        Xslt xsltMediator = (Xslt) m;
        OMElement xsltElt = fac.createOMElement("xslt", synNS);

        if (xsltMediator.getKey() != null) {
            xsltElt.addAttribute("key", xsltMediator.getKey(), null);
        } else {
            handleException("Invalid Xslt mediator. " +
                    "Should have a 'key' ");
        }

        if (xsltMediator.getSource() != null) {
            SerializerUtils.serializeExpression(xsltMediator.getSource(), xsltElt, "source", xsltMediator);
        }

        if (xsltMediator.getDescription() != null) {
            xsltElt.addAttribute("description", xsltMediator.getDescription(), null);
        }

        serializeMediatorProperties(xsltElt, xsltMediator.getProperty());
        serializeFeatures(xsltElt, xsltMediator.getFeature());
        serializeResources(xsltElt, xsltMediator.getResource());

        return xsltElt;
    }

    private void serializeFeatures(OMElement xsltElt, XsltFeature[] features) {

        if (features != null) {
            for (XsltFeature feature : features) {
                OMElement featureElt = fac.createOMElement("feature", synNS);
                if (feature.getName() != null) {
                    featureElt.addAttribute("name", feature.getName(), null);
                }
                featureElt.addAttribute("value", String.valueOf(feature.isValue()), null);
                xsltElt.addChild(featureElt);
            }
        }
    }

    private void serializeResources(OMElement xsltElt, XsltResource[] resources) {

        if (resources != null) {
            for (XsltResource resource : resources) {
                OMElement resourceElt = fac.createOMElement("resource", synNS);
                if (resource.getLocation() != null) {
                    resourceElt.addAttribute("location", resource.getLocation(), null);
                }
                if (resource.getKey() != null) {
                    resourceElt.addAttribute("key", resource.getKey(), null);
                }
                xsltElt.addChild(resourceElt);
            }
        }
    }

    @Override
    public String getMediatorClassName() {

        return Xslt.class.getName();
    }
}
