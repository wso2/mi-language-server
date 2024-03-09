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

package org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.transformation;

import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.AbstractMediatorFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.STNode;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.Mediator;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.transformation.enrich.Enrich;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.transformation.enrich.SourceEnrich;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.transformation.enrich.TargetEnrich;
import org.eclipse.lemminx.customservice.synapse.utils.Constant;
import org.eclipse.lemminx.customservice.synapse.utils.Utils;
import org.eclipse.lemminx.dom.DOMElement;
import org.eclipse.lemminx.dom.DOMNode;

import java.util.List;

public class EnrichFactory extends AbstractMediatorFactory {

    private static final String ENRICH = "enrich";

    @Override
    public Mediator createSpecificMediator(DOMElement element) {

        Enrich enrich = new Enrich();
        enrich.elementNode(element);
        populateAttributes(enrich, element);
        List<DOMNode> childNodes = element.getChildren();
        if (childNodes != null && !childNodes.isEmpty()) {
            for (DOMNode child : childNodes) {
                if (child.getNodeName().equalsIgnoreCase(Constant.SOURCE)) {
                    SourceEnrich sourceEnrich = createSourceEnrich(child);
                    enrich.setSource(sourceEnrich);
                } else if (child.getNodeName().equalsIgnoreCase(Constant.TARGET)) {
                    TargetEnrich targetEnrich = createTargetEnrich(child);
                    enrich.setTarget(targetEnrich);
                }
            }
        }
        return enrich;
    }

    @Override
    public void populateAttributes(STNode node, DOMElement element) {

        String description = element.getAttribute(Constant.DESCRIPTION);
        if (description != null) {
            ((Enrich) node).setDescription(description);
        }
    }

    private SourceEnrich createSourceEnrich(DOMNode element) {

        SourceEnrich sourceEnrich = new SourceEnrich();
        sourceEnrich.elementNode((DOMElement) element);
        String clone = element.getAttribute(Constant.CLONE);
        if (clone != null) {
            sourceEnrich.setClone(Boolean.parseBoolean(clone));
        }
        String xpath = element.getAttribute(Constant.XPATH);
        if (xpath != null) {
            sourceEnrich.setXpath(xpath);
        }
        String key = element.getAttribute(Constant.KEY);
        if (key != null) {
            sourceEnrich.setKey(key);
        }
        String type = element.getAttribute(Constant.TYPE);
        if (type != null) {
            sourceEnrich.setType(type);
        }
        String property = element.getAttribute(Constant.PROPERTY);
        if (property != null) {
            sourceEnrich.setProperty(property);
        }

        DOMNode inline = element.getFirstChild();
        if (inline != null) {
            String inlineString = Utils.getInlineString(inline);
            sourceEnrich.setContent(inlineString);
        }
        return sourceEnrich;
    }

    private TargetEnrich createTargetEnrich(DOMNode element) {

        TargetEnrich targetEnrich = new TargetEnrich();
        targetEnrich.elementNode((DOMElement) element);
        String action = element.getAttribute(Constant.ACTION);
        if (action != null) {
            targetEnrich.setAction(action);
        }
        String type = element.getAttribute(Constant.TYPE);
        if (type != null) {
            targetEnrich.setType(type);
        }
        String xpath = element.getAttribute(Constant.XPATH);
        if (xpath != null) {
            targetEnrich.setXpath(xpath);
        }
        String property = element.getAttribute(Constant.PROPERTY);
        if (property != null) {
            targetEnrich.setProperty(property);
        }
        return targetEnrich;
    }

    @Override
    public String getTagName() {

        return ENRICH;
    }
}
