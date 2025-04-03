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

package org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.eip;

import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.AbstractFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.AbstractMediatorFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.misc.SequenceFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.STNode;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.Mediator;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.advanced.Clone.CloneTarget;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.eip.ScatterGather;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.eip.aggregate.ScatterGatherAggregation;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.misc.common.Sequence;
import org.eclipse.lemminx.customservice.synapse.utils.Constant;
import org.eclipse.lemminx.dom.DOMElement;
import org.eclipse.lemminx.dom.DOMNode;

import java.util.ArrayList;
import java.util.List;

public class ScatterGatherFactory extends AbstractMediatorFactory {

    private static final String SCATTER_GATHER = "scatter-gather";

    @Override
    protected Mediator createSpecificMediator(DOMElement element) {

        ScatterGather scatterGather = new ScatterGather();
        scatterGather.elementNode(element);
        populateAttributes(scatterGather, element);
        List<DOMNode> children = element.getChildren();
        List<CloneTarget> cloneTargetList = new ArrayList<>();
        for (DOMNode node : children) {
            if (node.getNodeName().equalsIgnoreCase("aggregation")) {
                ScatterGatherAggregation scatterGatherAggregation = new ScatterGatherAggregation();
                scatterGatherAggregation.elementNode((DOMElement) node);
                scatterGatherAggregation.setExpression(node.getAttribute("expression"));
                if (node.getAttribute("condition") != null) {
                    scatterGatherAggregation.setCondition(node.getAttribute("condition"));
                }
                if (node.getAttribute("timeout") != null) {
                    scatterGatherAggregation.setCompleteTimeout(node.getAttribute("timeout"));
                }
                if (node.getAttribute("min-messages") != null) {
                    scatterGatherAggregation.setMinMessages(node.getAttribute("min-messages"));
                }
                if (node.getAttribute("max-messages") != null) {
                    scatterGatherAggregation.setMaxMessages(node.getAttribute("max-messages"));
                }
                scatterGather.setScatterGatherAggregation(scatterGatherAggregation);
            } else if (node.getNodeName().equalsIgnoreCase("sequence")) {
                AbstractFactory sequenceFactory = new SequenceFactory();
                Sequence sequence = (Sequence) sequenceFactory.create((DOMElement) node);
                CloneTarget cloneTarget = new CloneTarget();
                cloneTarget.elementNode((DOMElement) node);
                cloneTarget.setSequence(sequence);
                cloneTargetList.add(cloneTarget);
            }
        }
        scatterGather.addTarget(cloneTargetList.toArray(new CloneTarget[cloneTargetList.size()]));
        return scatterGather;
    }

    @Override
    public void populateAttributes(STNode node, DOMElement element) {

        try {
            String parallelExecute = element.getAttribute("parallel-execution");
            if (parallelExecute != null) {
                ((ScatterGather) node).setExecuteParallel(Boolean.parseBoolean(parallelExecute));
            } else {
                ((ScatterGather) node).setExecuteParallel(true);
            }
            String resultTarget = element.getAttribute(Constant.TARGET);
            if (resultTarget != null) {
                ((ScatterGather) node).setResultTarget(resultTarget);
            }
            String variableName = element.getAttribute(Constant.TARGET_VARIABLE);
            if (variableName != null) {
                ((ScatterGather) node).setVariableName(variableName);
            }
            String contentType = element.getAttribute(Constant.RESULT_CONTENT_TYPE);
            if (contentType != null) {
                ((ScatterGather) node).setContentType(contentType);
            }
            String rootElement = element.getAttribute(Constant.RESULT_ENCLOSING_ELEMENT);
            if (rootElement != null) {
                ((ScatterGather) node).setRootElement(rootElement);
            }
            String description = element.getAttribute(Constant.DESCRIPTION);
            if (description != null) {
                ((ScatterGather) node).setDescription(description);
            }
        } catch (IllegalArgumentException e) {
            // ignore
        }
    }

    @Override
    public String getTagName() {

        return SCATTER_GATHER;
    }
}
