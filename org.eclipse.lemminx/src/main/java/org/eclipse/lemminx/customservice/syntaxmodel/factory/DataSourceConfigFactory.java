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

import org.eclipse.lemminx.customservice.syntaxmodel.pojo.STNode;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.datasource.ConfigurationType;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.datasource.DatasourceType;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.datasource.DefinitionType;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.datasource.JndiConfigType;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.datasource.JndiEnvType;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.datasource.PropertyType;
import org.eclipse.lemminx.customservice.syntaxmodel.utils.Constant;
import org.eclipse.lemminx.customservice.syntaxmodel.utils.Utils;
import org.eclipse.lemminx.dom.DOMElement;
import org.eclipse.lemminx.dom.DOMNode;

import java.util.ArrayList;
import java.util.List;

public class DataSourceConfigFactory extends AbstractFactory {

    @Override
    public STNode create(DOMElement element) {

        DatasourceType datasource = new DatasourceType();
        datasource.elementNode(element);
        List<DOMNode> childNodes = element.getChildren();
        if (childNodes != null && !childNodes.isEmpty()) {
            for (DOMNode child : childNodes) {
                String childName = child.getNodeName();
                if (Constant.NAME.equalsIgnoreCase(childName)) {
                    STNode name = new STNode();
                    name.elementNode((DOMElement) child);
                    datasource.setName(name);
                } else if (Constant.DESCRIPTION.equalsIgnoreCase(childName)) {
                    STNode description = new STNode();
                    description.elementNode((DOMElement) child);
                    datasource.setDescription(description);
                } else if (Constant.JNDI_CONFIG.equalsIgnoreCase(childName)) {
                    JndiConfigType jindiConfig = createJndiConfig(child);
                    datasource.setJndiConfig(jindiConfig);
                } else if (Constant.DEFINITION.equalsIgnoreCase(childName)) {
                    DefinitionType definition = createDefinition(child);
                    datasource.setDefinition(definition);
                }
            }
        }
        return datasource;
    }

    private JndiConfigType createJndiConfig(DOMNode element) {

        JndiConfigType jndiConfig = new JndiConfigType();
        jndiConfig.elementNode((DOMElement) element);
        String useDataSourceFactory = element.getAttribute(Constant.USE_DATA_SOURCE_FACTORY);
        if (useDataSourceFactory != null && !useDataSourceFactory.isEmpty()) {
            jndiConfig.setUseDataSourceFactory(Boolean.parseBoolean(useDataSourceFactory));
        }
        List<DOMNode> childNodes = element.getChildren();
        if (childNodes != null && !childNodes.isEmpty()) {
            for (DOMNode childNode : childNodes) {
                String childName = childNode.getNodeName();
                if (Constant.NAME.equalsIgnoreCase(childName)) {
                    STNode name = new STNode();
                    name.elementNode((DOMElement) childNode);
                    jndiConfig.setName(name);
                } else if (Constant.ENVIRONMENT.equalsIgnoreCase(childName)) {
                    JndiEnvType environment = createEnvironment(childNode);
                    jndiConfig.setEnvironment(environment);
                }
            }
        }
        return jndiConfig;
    }

    private JndiEnvType createEnvironment(DOMNode element) {

        JndiEnvType environment = new JndiEnvType();
        environment.elementNode((DOMElement) element);
        List<DOMNode> childNodes = element.getChildren();
        if (childNodes != null && !childNodes.isEmpty()) {
            List<PropertyType> properties = new ArrayList<>();
            for (DOMNode child : childNodes) {
                PropertyType property = createProperty(child);
                properties.add(property);
            }
            environment.setProperty(properties.toArray(new PropertyType[properties.size()]));
        }
        return environment;
    }

    private PropertyType createProperty(DOMNode element) {

        PropertyType property = new PropertyType();
        property.elementNode((DOMElement) element);
        String name = element.getAttribute(Constant.NAME);
        if (name != null && !name.isEmpty()) {
            property.setName(name);
        }

        DOMNode firstChild = element.getFirstChild();
        String value = Utils.getInlineString(firstChild);
        if (value != null && !value.isEmpty()) {
            property.setValue(value);
        }

        return property;

    }

    private DefinitionType createDefinition(DOMNode element) {

        DefinitionType definition = new DefinitionType();
        definition.elementNode((DOMElement) element);
        String type = element.getAttribute(Constant.TYPE);
        if (type != null && !type.isEmpty()) {
            definition.setType(type);
        }
        List<DOMNode> childNodes = element.getChildren();
        if (childNodes != null && !childNodes.isEmpty()) {
            for (DOMNode child : childNodes) {
                String name = child.getNodeName();
                if (Constant.CONFIGURATION.equalsIgnoreCase(name)) {
                    ConfigurationType configuration = createDefinitionConfiguration(child);
                    definition.setConfiguration(configuration);
                }
            }
        }
        return definition;
    }

    private ConfigurationType createDefinitionConfiguration(DOMNode element) {

        ConfigurationType configuration = new ConfigurationType();
        configuration.elementNode((DOMElement) element);

        List<DOMNode> childNodes = element.getChildren();
        if (childNodes != null && !childNodes.isEmpty()) {
            List<STNode> contents = new ArrayList<>();
            for (DOMNode child : childNodes) {
                STNode content = new STNode();
                content.elementNode((DOMElement) child);
                contents.add(content);
            }
            configuration.setContent(contents.toArray(new STNode[contents.size()]));
        }
        return configuration;
    }

    @Override
    public void populateAttributes(STNode node, DOMElement element) {

    }
}
