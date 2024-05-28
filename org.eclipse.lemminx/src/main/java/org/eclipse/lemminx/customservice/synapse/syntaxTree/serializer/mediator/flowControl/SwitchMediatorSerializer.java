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

package org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.mediator.flowControl;

import org.apache.axiom.om.OMElement;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.Mediator;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.filter.switchMediator.Switch;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.filter.switchMediator.SwitchCase;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.filter.switchMediator.SwitchDefault;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.AnonymousSequenceSerializer;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.SerializerUtils;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.mediator.AbstractMediatorSerializer;

public class SwitchMediatorSerializer extends AbstractMediatorSerializer {

    @Override
    protected OMElement serializeSpecificMediator(Mediator m) {

        Switch switchMediator = (Switch) m;
        OMElement switchElt = fac.createOMElement("switch", synNS);

        if (switchMediator.getSource() != null) {
            SerializerUtils.serializeExpression(switchMediator.getSource(), switchElt, "source", switchMediator);
        } else {
            handleException("Invalid switch mediator. " +
                    "Should have a 'source' ");
        }

        serializeCases(switchElt, switchMediator);
        serializeDefault(switchElt, switchMediator);
        return switchElt;
    }

    private void serializeCases(OMElement switchElt, Switch switchMediator) {

        SwitchCase[] cases = switchMediator.get_case();
        if (cases != null) {
            for (SwitchCase aCase : cases) {
                OMElement caseElt = AnonymousSequenceSerializer.serializeAnonymousSequence(aCase.getMediatorList());
                caseElt.setLocalName("case");
                caseElt.addAttribute("regex", aCase.getRegex(), null);
                switchElt.addChild(caseElt);
            }
        }
    }

    private void serializeDefault(OMElement switchElt, Switch switchMediator) {

        SwitchDefault defaultCase = switchMediator.get_default();
        if (defaultCase != null) {
            OMElement defaultElt = AnonymousSequenceSerializer.serializeAnonymousSequence(defaultCase.getMediatorList());
            defaultElt.setLocalName("default");
            switchElt.addChild(defaultElt);
        }
    }

    @Override
    public String getMediatorClassName() {

        return Switch.class.getName();
    }
}
