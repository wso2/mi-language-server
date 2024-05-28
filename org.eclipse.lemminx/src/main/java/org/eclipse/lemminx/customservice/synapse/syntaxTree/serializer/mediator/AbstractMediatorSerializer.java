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

package org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.mediator;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.CommentMediator;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.Mediator;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.MediatorProperty;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.InvalidConfigurationException;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.SerializerUtils;
import org.eclipse.lemminx.customservice.synapse.utils.Constant;

import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractMediatorSerializer {

    private static Logger log = Logger.getLogger(AbstractMediatorSerializer.class.getName());
    protected static final OMFactory fac = OMAbstractFactory.getOMFactory();
    protected static final OMNamespace synNS = Constant.SYNAPSE_OMNAMESPACE;
    protected static final OMNamespace nullNS
            = fac.createOMNamespace(Constant.EMPTY_STRING, "");

    /**
     * Return the XML representation of this mediator
     *
     * @param m      mediator to be serialized
     * @param parent the OMElement to which the serialization should be attached
     * @return the serialized mediator XML
     */
    public final OMElement serializeMediator(OMElement parent, Mediator m) {

        if (m instanceof CommentMediator) {
            return ((CommentMediatorSerializer) this).serializeComment(parent, (CommentMediator) m);
        }
        OMElement mediatorElt = serializeSpecificMediator(m);
        if (parent != null) {
            parent.addChild(mediatorElt);
        }
        return mediatorElt;
    }

    protected abstract OMElement serializeSpecificMediator(Mediator m);

    /**
     * Return the class name of the mediator which can be serialized
     *
     * @return the class name
     */
    public abstract String getMediatorClassName();

    protected void serializeMediatorProperties(OMElement mediatorElt, MediatorProperty[] properties) {

        if (properties != null) {
            for (MediatorProperty property : properties) {
                OMElement propertyElt = serializeMediatorProperty(property);
                mediatorElt.addChild(propertyElt);
            }
        }
    }

    private OMElement serializeMediatorProperty(MediatorProperty property) {

        OMElement propertyElt = fac.createOMElement("property", synNS);
        if (property.getName() != null) {
            propertyElt.addAttribute("name", property.getName(), nullNS);
        } else {
            handleException("Mediator property must have a name");
        }
        if (property.getValue() != null) {
            propertyElt.addAttribute("value", property.getValue(), nullNS);
        } else if (property.getExpression() != null) {
            SerializerUtils.serializeExpression(property.getExpression(), propertyElt, "expression", property);
        } else {
            handleException("Mediator property must have a literal value or be an expression");
        }
        return propertyElt;
    }

    protected void handleException(String s) {

        log.log(Level.SEVERE, s);
        throw new InvalidConfigurationException(s);
    }
}