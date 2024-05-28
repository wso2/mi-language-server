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
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.filter.filter.Filter;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.AnonymousSequenceSerializer;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.SerializerUtils;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.mediator.AbstractMediatorSerializer;

import java.util.List;

public class FilterMediatorSerializer extends AbstractMediatorSerializer {

    @Override
    protected OMElement serializeSpecificMediator(Mediator m) {

        Filter filter = (Filter) m;
        OMElement filterElt = fac.createOMElement("filter", synNS);

        if (filter.getSource() != null && filter.getRegex() != null) {
            filterElt.addAttribute("source", filter.getSource(), null);
            filterElt.addAttribute("regex", filter.getRegex(), null);
        } else if (filter.getXpath() != null) {
            SerializerUtils.serializeExpression(filter.getXpath(), filterElt, "xpath", filter);
        } else {
            handleException("Invalid filter mediator. " +
                    "Should have either a 'source' and a 'regex' OR an 'xpath' ");
        }

        serializeThenElse(filterElt, filter, "then");
        serializeThenElse(filterElt, filter, "else");

        if (filter.getDescription() != null) {
            filterElt.addAttribute("description", filter.getDescription(), null);
        }
        return filterElt;
    }

    private void serializeThenElse(OMElement filterElt, Filter filter, String tagName) {

        if (filter.getThen() != null) {
            List<Mediator> thenMediatorList = filter.getThen().getMediatorList();
            OMElement thenElement = AnonymousSequenceSerializer.serializeAnonymousSequence(thenMediatorList);
            thenElement.setLocalName(tagName);
            filterElt.addChild(thenElement);
        }
    }

    @Override
    public String getMediatorClassName() {

        return Filter.class.getName();
    }
}
