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

package org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.extension;

import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.AbstractMediatorFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.STNode;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.Mediator;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.extension.Bean;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.extension.BeanAction;
import org.eclipse.lemminx.customservice.synapse.utils.Constant;
import org.eclipse.lemminx.customservice.synapse.utils.Utils;
import org.eclipse.lemminx.dom.DOMElement;

public class BeanFactory extends AbstractMediatorFactory {

    private static final String BEAN = "bean";

    @Override
    protected Mediator createSpecificMediator(DOMElement element) {

        Bean bean = new Bean();
        bean.elementNode(element);
        populateAttributes(bean, element);
        return bean;
    }

    @Override
    public void populateAttributes(STNode node, DOMElement element) {

        String action = element.getAttribute(Constant.ACTION);
        BeanAction actionEnum = Utils.getEnumFromValue(action, BeanAction.class);
        if (actionEnum != null) {
            ((Bean) node).setAction(actionEnum);
        }
        String var = element.getAttribute(Constant.VAR);
        if (var != null) {
            ((Bean) node).setVar(var);
        }
        String clazz = element.getAttribute(Constant.CLASS);
        if (clazz != null) {
            ((Bean) node).setClazz(clazz);
        }
        String property = element.getAttribute(Constant.PROPERTY);
        if (property != null) {
            ((Bean) node).setProperty(property);
        }
        String value = element.getAttribute(Constant.VALUE);
        if (value != null) {
            ((Bean) node).setValue(value);
        }
        String target = element.getAttribute(Constant.TARGET);
        if (target != null) {
            ((Bean) node).setTarget(target);
        }
        String description = element.getAttribute(Constant.DESCRIPTION);
        if (description != null) {
            ((Bean) node).setDescription(description);
        }
    }

    @Override
    public String getTagName() {

        return BEAN;
    }
}
