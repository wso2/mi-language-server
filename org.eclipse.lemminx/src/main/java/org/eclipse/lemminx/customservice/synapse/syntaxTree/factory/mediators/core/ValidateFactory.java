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

package org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.core;

import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.AbstractMediatorFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.STNode;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.Mediator;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.Feature;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.validate.Validate;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.validate.ValidateOnFail;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.validate.ValidateProperty;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.validate.ValidateResource;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.validate.ValidateSchema;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.utils.SyntaxTreeUtils;
import org.eclipse.lemminx.customservice.synapse.utils.Constant;
import org.eclipse.lemminx.dom.DOMElement;
import org.eclipse.lemminx.dom.DOMNode;

import java.util.ArrayList;
import java.util.List;

public class ValidateFactory extends AbstractMediatorFactory {

    private static final String VALIDATE = "validate";

    @Override
    public Mediator createSpecificMediator(DOMElement element) {

        Validate validate = new Validate();
        validate.elementNode(element);
        populateAttributes(validate, element);
        List<DOMNode> children = element.getChildren();
        List<ValidateProperty> validateProperties = new ArrayList<>();
        List<ValidateSchema> validateSchemas = new ArrayList<>();
        List<Feature> features = new ArrayList<>();
        List<ValidateResource> validateResources = new ArrayList<>();
        if (children != null && !children.isEmpty()) {
            for (DOMNode node : children) {
                String nodeName = node.getNodeName();
                if (nodeName.equalsIgnoreCase(Constant.PROPERTY)) {
                    ValidateProperty validateProperty = new ValidateProperty();
                    validateProperty.elementNode((DOMElement) node);
                    populatePropertyAttributes(validateProperty, (DOMElement) node);
                    validateProperties.add(validateProperty);
                } else if (nodeName.equalsIgnoreCase(Constant.SCHEMA)) {
                    ValidateSchema validateSchema = new ValidateSchema();
                    validateSchema.elementNode((DOMElement) node);
                    populateSchemaAttributes(validateSchema, (DOMElement) node);
                    validateSchemas.add(validateSchema);
                } else if (nodeName.equalsIgnoreCase(Constant.ON_FAIL)) {
                    ValidateOnFail validateOnFail = new ValidateOnFail();
                    validateOnFail.elementNode((DOMElement) node);
                    List<DOMNode> onFailChildren = node.getChildren();
                    if (onFailChildren != null && !onFailChildren.isEmpty()) {
                        List<Mediator> mediators = SyntaxTreeUtils.createMediators(onFailChildren);
                        validateOnFail.setMediatorList(mediators);
                    }
                    validate.setOnFail(validateOnFail);
                } else if (nodeName.equalsIgnoreCase(Constant.FEATURE)) {
                    Feature feature = new Feature();
                    feature.elementNode((DOMElement) node);
                    populateFeatureAttributes(feature, (DOMElement) node);
                    features.add(feature);
                } else if (nodeName.equalsIgnoreCase(Constant.RESOURCE)) {
                    ValidateResource validateResource = new ValidateResource();
                    validateResource.elementNode((DOMElement) node);
                    populateResourceAttributes(validateResource, (DOMElement) node);
                    validateResources.add(validateResource);
                } else {
                    throw new RuntimeException("Unexpected child node: " + node.getNodeName());
                }
            }
        }
        validate.setProperty(validateProperties.toArray(new ValidateProperty[validateProperties.size()]));
        validate.setSchema(validateSchemas.toArray(new ValidateSchema[validateSchemas.size()]));
        validate.setFeature(features.toArray(new Feature[features.size()]));
        validate.setResource(validateResources.toArray(new ValidateResource[validateResources.size()]));
        return validate;
    }

    @Override
    public void populateAttributes(STNode node, DOMElement element) {

    }

    private void populatePropertyAttributes(ValidateProperty validateProperty, DOMElement node) {

        String name = node.getAttribute(Constant.NAME);
        if (name != null && !name.isEmpty()) {
            validateProperty.setName(name);
        }
        String value = node.getAttribute(Constant.VALUE);
        if (value != null && !value.isEmpty()) {
            validateProperty.setValue(Boolean.parseBoolean(value));
        }
    }

    private void populateSchemaAttributes(ValidateSchema validateSchema, DOMElement node) {

        String key = node.getAttribute(Constant.KEY);
        if (key != null && !key.isEmpty()) {
            validateSchema.setKey(key);
        }
    }

    private void populateFeatureAttributes(Feature feature, DOMElement node) {

        String name = node.getAttribute(Constant.NAME);
        if (name != null && !name.isEmpty()) {
            feature.setName(name);
        }
        String value = node.getAttribute(Constant.VALUE);
        if (value != null && !value.isEmpty()) {
            feature.setValue(Boolean.parseBoolean(value));
        }
    }

    private void populateResourceAttributes(ValidateResource validateResource, DOMElement node) {

        String location = node.getAttribute(Constant.LOCATION);
        if (location != null && !location.isEmpty()) {
            validateResource.setLocation(location);
        }
        String key = node.getAttribute(Constant.KEY);
        if (key != null && !key.isEmpty()) {
            validateResource.setKey(key);
        }
    }

    @Override
    public String getTagName() {

        return VALIDATE;
    }
}
