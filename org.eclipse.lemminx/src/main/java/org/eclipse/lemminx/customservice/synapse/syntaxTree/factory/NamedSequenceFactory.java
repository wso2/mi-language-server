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

package org.eclipse.lemminx.customservice.synapse.syntaxTree.factory;

import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.NamedSequence;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.STNode;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.api.EnableDisable;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.Mediator;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.utils.SyntaxTreeUtils;
import org.eclipse.lemminx.customservice.synapse.utils.Constant;
import org.eclipse.lemminx.customservice.synapse.utils.Utils;
import org.eclipse.lemminx.dom.DOMElement;
import org.eclipse.lemminx.dom.DOMNode;

import java.util.List;

public class NamedSequenceFactory extends AbstractFactory {

    @Override
    public STNode create(DOMElement element) {

        NamedSequence namedSequence = new NamedSequence();
        namedSequence.elementNode(element);
        populateAttributes(namedSequence, element);
        List<DOMNode> children = element.getChildren();
        if (children != null && !children.isEmpty()) {
            List<Mediator> mediators = SyntaxTreeUtils.createMediators(children);
            namedSequence.setMediatorList(mediators);
        }
        return namedSequence;
    }

    @Override
    public void populateAttributes(STNode node, DOMElement element) {

        String name = element.getAttribute(Constant.NAME);
        if (name != null) {
            ((NamedSequence) node).setName(name);
        }
        String onError = element.getAttribute(Constant.ON_ERROR);
        if (onError != null) {
            ((NamedSequence) node).setOnError(onError);
        }
        String description = element.getAttribute(Constant.DESCRIPTION);
        if (description != null) {
            ((NamedSequence) node).setDescription(description);
        }
        String statistics = element.getAttribute(Constant.STATISTICS);
        EnableDisable statisticsEnum = Utils.getEnumFromValue(statistics, EnableDisable.class);
        if (statisticsEnum != null) {
            ((NamedSequence) node).setStatistics(statisticsEnum);
        }
        String trace = element.getAttribute(Constant.TRACE);
        EnableDisable traceEnum = Utils.getEnumFromValue(trace, EnableDisable.class);
        if (traceEnum != null) {
            ((NamedSequence) node).setTrace(traceEnum);
        }
    }
}
