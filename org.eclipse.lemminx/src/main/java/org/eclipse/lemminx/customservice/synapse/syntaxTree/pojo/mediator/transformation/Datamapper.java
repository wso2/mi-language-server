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

package org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.transformation;

import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.Mediator;

public class Datamapper extends Mediator {

    String config;
    String inputSchema;
    String outputSchema;
    SchemaType inputType;
    SchemaType outputType;
    String xsltStyleSheet;
    String description;

    public Datamapper() {
        setDisplayName("Data Mapper");
    }

    public String getConfig() {

        return config;
    }

    public void setConfig(String config) {

        this.config = config;
    }

    public String getInputSchema() {

        return inputSchema;
    }

    public void setInputSchema(String inputSchema) {

        this.inputSchema = inputSchema;
    }

    public String getOutputSchema() {

        return outputSchema;
    }

    public void setOutputSchema(String outputSchema) {

        this.outputSchema = outputSchema;
    }

    public SchemaType getInputType() {

        return inputType;
    }

    public void setInputType(SchemaType inputType) {

        this.inputType = inputType;
    }

    public SchemaType getOutputType() {

        return outputType;
    }

    public void setOutputType(SchemaType outputType) {

        this.outputType = outputType;
    }

    public String getXsltStyleSheet() {

        return xsltStyleSheet;
    }

    public void setXsltStyleSheet(String xsltStyleSheet) {

        this.xsltStyleSheet = xsltStyleSheet;
    }

    public String getDescription() {

        return description;
    }

    public void setDescription(String description) {

        this.description = description;
    }
}
