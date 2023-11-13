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

package org.eclipse.lemminx.customservice.syntaxmodel.factory.mediators.other;

import org.eclipse.lemminx.customservice.syntaxmodel.factory.mediators.AbstractMediatorFactory;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.STNode;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.mediator.Mediator;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.mediator.other.builder.Builder;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.mediator.other.builder.BuilderMessageBuilder;
import org.eclipse.lemminx.customservice.syntaxmodel.utils.Constant;
import org.eclipse.lemminx.dom.DOMElement;
import org.eclipse.lemminx.dom.DOMNode;

import java.util.List;

public class BuilderFactory extends AbstractMediatorFactory {

    private static final String BUILDER = "builder";

    @Override
    public Mediator createSpecificMediator(DOMElement element) {

        Builder builder = new Builder();
        builder.elementNode(element);
        populateAttributes(builder, element);
        List<DOMNode> children = element.getChildren();
        if (children != null && !children.isEmpty()) {
            for (DOMNode child : children) {
                if (child.getNodeName().equalsIgnoreCase(Constant.MESSAGE_BUILDER)) {
                    BuilderMessageBuilder messageBuilder = createBuilderMessageBuilder(child);
                    builder.setMessageBuilder(messageBuilder);
                }
            }
        }
        return builder;
    }

    @Override
    public void populateAttributes(STNode node, DOMElement element) {

        String description = element.getAttribute(Constant.DESCRIPTION);
        if (description != null && !description.isEmpty()) {
            ((Builder) node).setDescription(description);
        }
    }

    private BuilderMessageBuilder createBuilderMessageBuilder(DOMNode node) {

        BuilderMessageBuilder builderMessageBuilder = new BuilderMessageBuilder();
        builderMessageBuilder.elementNode((DOMElement) node);
        String contentType = node.getAttribute(Constant.CONTENT_TYPE);
        if (contentType != null && !contentType.isEmpty()) {
            builderMessageBuilder.setContentType(contentType);
        }
        String clazz = node.getAttribute(Constant.CLASS);
        if (clazz != null && !clazz.isEmpty()) {
            builderMessageBuilder.setClazz(clazz);
        }
        String formatterClass = node.getAttribute(Constant.FORMATTER_CLASS);
        if (formatterClass != null && !formatterClass.isEmpty()) {
            builderMessageBuilder.setFormatterClass(formatterClass);
        }
        return builderMessageBuilder;
    }

    @Override
    public String getTagName() {

        return BUILDER;
    }
}
