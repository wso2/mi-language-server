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

import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.AbstractMediatorFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.STNode;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.Mediator;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.filter.switchMediator.Switch;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.filter.switchMediator.SwitchCase;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.filter.switchMediator.SwitchDefault;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.utils.SyntaxTreeUtils;
import org.eclipse.lemminx.customservice.synapse.utils.Constant;
import org.eclipse.lemminx.dom.DOMElement;
import org.eclipse.lemminx.dom.DOMNode;

import java.util.ArrayList;
import java.util.List;

public class SwitchFactory extends AbstractMediatorFactory {

    private static final String SWITCH = "switch";

    @Override
    protected Mediator createSpecificMediator(DOMElement element) {

        Switch _switch = new Switch();
        _switch.elementNode(element);
        populateAttributes(_switch, element);
        List<DOMNode> children = element.getChildren();
        List<SwitchCase> caseList = new ArrayList<>();
        if (children != null && !children.isEmpty()) {
            for (DOMNode child : children) {
                if (child.getNodeName().equalsIgnoreCase(Constant.CASE)) {
                    SwitchCase switchCase = createSwitchCase(child);
                    caseList.add(switchCase);
                } else if (child.getNodeName().equalsIgnoreCase(Constant.DEFAULT)) {
                    SwitchDefault switchDefault = createSwitchDefault(child);
                    _switch.set_default(switchDefault);
                }
            }
            _switch.set_case(caseList.toArray(new SwitchCase[caseList.size()]));
        }
        return _switch;
    }

    @Override
    public void populateAttributes(STNode node, DOMElement element) {

        String source = element.getAttribute(Constant.SOURCE);
        if (source != null) {
            ((Switch) node).setSource(source);
        }
        String description = element.getAttribute(Constant.DESCRIPTION);
        if (description != null) {
            ((Switch) node).setDescription(description);
        }
    }

    private SwitchCase createSwitchCase(DOMNode child) {

        SwitchCase switchCase = new SwitchCase();
        switchCase.elementNode((DOMElement) child);
        String regex = child.getAttribute(Constant.REGEX);
        if (regex != null) {
            switchCase.setRegex(regex);
        }
        List<DOMNode> children = child.getChildren();
        if (children != null && !children.isEmpty()) {
            List<Mediator> mediatorList = SyntaxTreeUtils.createMediators(children);
            switchCase.setMediatorList(mediatorList);
        }
        return switchCase;
    }

    private SwitchDefault createSwitchDefault(DOMNode child) {

        SwitchDefault switchDefault = new SwitchDefault();
        switchDefault.elementNode((DOMElement) child);
        List<DOMNode> children = child.getChildren();
        if (children != null && !children.isEmpty()) {
            List<Mediator> mediatorList = SyntaxTreeUtils.createMediators(children);
            switchDefault.setMediatorList(mediatorList);
        }
        return switchDefault;
    }

    @Override
    public String getTagName() {

        return SWITCH;
    }

}
