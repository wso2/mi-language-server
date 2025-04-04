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

package org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.transformation;

import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.AbstractMediatorFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.STNode;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.Mediator;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.transformation.Datamapper;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.transformation.SchemaType;
import org.eclipse.lemminx.customservice.synapse.utils.Constant;
import org.eclipse.lemminx.customservice.synapse.utils.Utils;
import org.eclipse.lemminx.dom.DOMElement;

public class DataMapperFactory extends AbstractMediatorFactory {

    private static final String DATA_MAPPER = "datamapper";

    @Override
    protected Mediator createSpecificMediator(DOMElement element) {

        Datamapper datamapper = new Datamapper();
        datamapper.elementNode(element);
        populateAttributes(datamapper, element);
        return datamapper;
    }

    @Override
    public void populateAttributes(STNode node, DOMElement element) {

        String config = element.getAttribute(Constant.CONFIG);
        if (config != null) {
            ((Datamapper) node).setConfig(config);
        }
        String inputSchema = element.getAttribute(Constant.INPUT_SCHEMA);
        if (inputSchema != null) {
            ((Datamapper) node).setInputSchema(inputSchema);
        }
        String outputSchema = element.getAttribute(Constant.OUTPUT_SCHEMA);
        if (outputSchema != null) {
            ((Datamapper) node).setOutputSchema(outputSchema);
        }
        String inputType = element.getAttribute(Constant.INPUT_TYPE);
        SchemaType inputTypeEnum = Utils.getEnumFromValue(inputType, SchemaType.class);
        if (inputTypeEnum != null) {
            ((Datamapper) node).setInputType(inputTypeEnum);
        }
        String outputType = element.getAttribute(Constant.OUTPUT_TYPE);
        SchemaType outputTypeEnum = Utils.getEnumFromValue(outputType, SchemaType.class);
        if (outputTypeEnum != null) {
            ((Datamapper) node).setOutputType(outputTypeEnum);
        }
        String xsltStyleSheet = element.getAttribute(Constant.XSLT_STYLE_SHEET);
        if (xsltStyleSheet != null) {
            ((Datamapper) node).setXsltStyleSheet(xsltStyleSheet);
        }
        String description = element.getAttribute(Constant.DESCRIPTION);
        if (description != null) {
            ((Datamapper) node).setDescription(description);
        }
    }

    @Override
    public String getTagName() {

        return DATA_MAPPER;
    }
}
