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

import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.LocalEntry;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.STNode;
import org.eclipse.lemminx.customservice.synapse.utils.Constant;
import org.eclipse.lemminx.customservice.synapse.utils.Utils;
import org.eclipse.lemminx.dom.DOMElement;
import org.eclipse.lemminx.dom.DOMNode;

public class LocalEntryFactory extends AbstractFactory {

    @Override
    public STNode create(DOMElement element) {

        LocalEntry localEntry = new LocalEntry();
        localEntry.elementNode(element);
        populateAttributes(localEntry, element);
        DOMNode inline = element.getFirstChild();
        if (inline != null) {
            String inlineString = Utils.getInlineString(inline, Boolean.FALSE);
            localEntry.setContent(inlineString);
            String subType = getSubType(inline);
            localEntry.setSubType(subType);
        }
        return localEntry;
    }

    @Override
    public void populateAttributes(STNode node, DOMElement element) {

        LocalEntry localEntry = (LocalEntry) node;
        String key = element.getAttribute(Constant.KEY);
        if (key != null) {
            localEntry.setKey(key);
        }
        String src = element.getAttribute(Constant.SRC);
        if (src != null) {
            localEntry.setSrc(src);
        }
    }

    private String getSubType(DOMNode inline) {

        String nodeName = inline.getNodeName();
        if (nodeName != null) {
            return nodeName.toUpperCase();
        }
        return null;
    }
}
