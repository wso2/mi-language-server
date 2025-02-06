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

package org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.filter;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.AbstractMediatorFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.STNode;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.Mediator;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.filter.filter.Filter;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.filter.filter.FilterElse;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.filter.filter.FilterThen;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.utils.SyntaxTreeUtils;
import org.eclipse.lemminx.customservice.synapse.utils.Constant;
import org.eclipse.lemminx.customservice.synapse.utils.Utils;
import org.eclipse.lemminx.dom.DOMElement;
import org.eclipse.lemminx.dom.DOMNode;

import java.util.List;

public class FilterFactory extends AbstractMediatorFactory {

    private static final String FILTER = "filter";

    @Override
    protected Mediator createSpecificMediator(DOMElement element) {

        Filter filter = new Filter();
        if (StringUtils.isNotEmpty(getMiVersion()) &&
                Utils.compareVersions(getMiVersion(), Constant.MI_440_VERSION) < 0) {
            filter.setDisplayName("Filter");
        }
        filter.elementNode(element);
        populateAttributes(filter, element);
        List<DOMNode> children = element.getChildren();
        if (children != null && !children.isEmpty()) {
            for (DOMNode child : children) {
                if (child.getNodeName().equalsIgnoreCase(Constant.THEN)) {
                    filter.setThen((FilterThen) createThenElse(child));
                } else if (child.getNodeName().equalsIgnoreCase(Constant.ELSE)) {
                    filter.setElse_((FilterElse) createThenElse(child));
                }
            }
        }
        return filter;
    }

    @Override
    public void populateAttributes(STNode node, DOMElement element) {

        String xpath = element.getAttribute(Constant.XPATH);
        if (xpath != null) {
            ((Filter) node).setXpath(xpath);
        }
        String regex = element.getAttribute(Constant.REGEX);
        if (regex != null) {
            ((Filter) node).setRegex(regex);
        }
        String source = element.getAttribute(Constant.SOURCE);
        if (source != null) {
            ((Filter) node).setSource(source);
        }
        String description = element.getAttribute(Constant.DESCRIPTION);
        if (description != null) {
            ((Filter) node).setDescription(description);
        }
    }

    private STNode createThenElse(DOMNode child) {

        STNode then_else = null;
        if (child.getNodeName().equalsIgnoreCase(Constant.THEN)) {
            then_else = new FilterThen();
        } else if (child.getNodeName().equalsIgnoreCase(Constant.ELSE)) {
            then_else = new FilterElse();
        }
        then_else.elementNode((DOMElement) child);
        String sequence = child.getAttribute(Constant.SEQUENCE);
        List<DOMNode> children = child.getChildren();
        List<Mediator> mediators = null;
        if (children != null && !children.isEmpty()) {
            mediators = SyntaxTreeUtils.createMediators(children);
        }
        if (then_else instanceof FilterThen) {
            ((FilterThen) then_else).setMediatorList(mediators);
            ((FilterThen) then_else).setSequence(sequence);
        } else {
            ((FilterElse) then_else).setMediatorList(mediators);
            ((FilterElse) then_else).setSequence(sequence);
        }
        return then_else;
    }

    @Override
    public String getTagName() {

        return FILTER;
    }
}
