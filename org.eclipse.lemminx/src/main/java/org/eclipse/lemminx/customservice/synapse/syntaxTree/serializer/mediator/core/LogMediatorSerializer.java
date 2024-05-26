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

package org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.mediator.core;

import org.apache.axiom.om.OMElement;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.Mediator;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.Log;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.LogCategory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.LogLevel;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.mediator.AbstractMediatorSerializer;

public class LogMediatorSerializer extends AbstractMediatorSerializer {

    @Override
    protected OMElement serializeSpecificMediator(Mediator m) {

        Log log = (Log) m;
        OMElement logElt = fac.createOMElement("log", synNS);
        if (log.getLevel() != null && log.getLevel() != LogLevel.simple) {
            logElt.addAttribute("level", log.getLevel().name(), nullNS);
        }

        if (log.getCategory() != null && log.getCategory() != LogCategory.INFO) {
            logElt.addAttribute("category", log.getCategory().name(), nullNS);
        }

        if (log.getSeparator() != null) {
            logElt.addAttribute("separator", log.getSeparator(), nullNS);
        }

        if (log.getDescription() != null) {
            logElt.addAttribute("description", log.getDescription(), nullNS);
        }

        serializeMediatorProperties(logElt, log.getProperty());

        return logElt;
    }

    @Override
    public String getMediatorClassName() {

        return Log.class.getName();
    }
}
