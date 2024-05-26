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

package org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.mediator.transformation;

import org.apache.axiom.om.OMElement;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.Mediator;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.transformation.Datamapper;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.mediator.AbstractMediatorSerializer;

public class DatamapperMediatorSerializer extends AbstractMediatorSerializer {

    @Override
    protected OMElement serializeSpecificMediator(Mediator m) {

        Datamapper datamapper = (Datamapper) m;
        OMElement datamapperElt = fac.createOMElement("datamapper", synNS);

        if (datamapper.getInputType() != null) {
            datamapperElt.addAttribute("inputType", datamapper.getInputType().name(), null);
        }
        if (datamapper.getInputSchema() != null) {
            datamapperElt.addAttribute("inputSchema", datamapper.getInputSchema(), null);
        }
        if (datamapper.getOutputType() != null) {
            datamapperElt.addAttribute("outputType", datamapper.getOutputType().name(), null);
        }
        if (datamapper.getOutputSchema() != null) {
            datamapperElt.addAttribute("outputSchema", datamapper.getOutputSchema(), null);
        }
        if (datamapper.getConfig() != null) {
            datamapperElt.addAttribute("config", datamapper.getConfig(), null);
        }
        if (datamapper.getXsltStyleSheet() != null) {
            datamapperElt.addAttribute("xsltStyleSheet", datamapper.getXsltStyleSheet(), null);
        }
        if (datamapper.getDescription() != null) {
            datamapperElt.addAttribute("description", datamapper.getDescription(), null);
        }
        return datamapperElt;
    }

    @Override
    public String getMediatorClassName() {

        return Datamapper.class.getName();
    }
}
