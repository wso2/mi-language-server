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

package org.eclipse.lemminx.customservice.syntaxmodel.factory;

import org.eclipse.lemminx.customservice.syntaxmodel.pojo.LocalEntry;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.STNode;
import org.eclipse.lemminx.customservice.syntaxmodel.utils.Constant;
import org.eclipse.lemminx.customservice.syntaxmodel.utils.Utils;
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
            String inlineString = Utils.getInlineString(inline);
            localEntry.setContent(inlineString);
        }
        return localEntry;
    }

    @Override
    public void populateAttributes(STNode node, DOMElement element) {

        LocalEntry localEntry = (LocalEntry) node;
        String key = element.getAttribute(Constant.KEY);
        if (key != null && !key.isEmpty()) {
            localEntry.setKey(key);
        }
        String src = element.getAttribute(Constant.SRC);
        if (src != null && !src.isEmpty()) {
            localEntry.setSrc(src);
        }
    }
}
