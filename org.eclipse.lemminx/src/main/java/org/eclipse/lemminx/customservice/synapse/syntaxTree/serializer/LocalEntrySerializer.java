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

package org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.LocalEntry;
import org.eclipse.lemminx.customservice.synapse.utils.Constant;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LocalEntrySerializer {

    private static Logger log = Logger.getLogger(LocalEntrySerializer.class.getName());
    private static final OMFactory fac = OMAbstractFactory.getOMFactory();

    public static String serializeLocalEntry(LocalEntry localEntry) {

        OMElement localEntryElt = fac.createOMElement("localEntry", Constant.SYNAPSE_OMNAMESPACE);

        if (localEntry.getKey() != null) {
            localEntryElt.addAttribute("key", localEntry.getKey(), null);
        } else {
            handleException("Local Entry key is required");
        }
        if (localEntry.getSrc() != null) {
            localEntryElt.addAttribute("src", localEntry.getSrc(), null);
        } else if (localEntry.getContent() != null) {
            OMElement contentElt = SerializerUtils.stringToOM(localEntry.getContent().toString());
            localEntryElt.addChild(contentElt);
        }
        return localEntryElt.toString();
    }

    private static void handleException(String s) {

        log.log(Level.SEVERE, s);
        throw new InvalidConfigurationException(s);
    }
}
