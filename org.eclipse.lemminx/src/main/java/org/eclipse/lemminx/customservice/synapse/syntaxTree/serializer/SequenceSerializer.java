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
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.NamedSequence;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.Mediator;
import org.eclipse.lemminx.customservice.synapse.utils.Constant;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SequenceSerializer {

    private static Logger log = Logger.getLogger(SequenceSerializer.class.getName());
    private static final OMFactory fac = OMAbstractFactory.getOMFactory();

    public static String serializeSequence(NamedSequence sequence) {

        OMElement sequenceElt = serializeMediators(sequence.getMediatorList());
        if (sequenceElt == null) {
            sequenceElt = fac.createOMElement("sequence", Constant.SYNAPSE_OMNAMESPACE);
        }

        if (sequence.getName() != null) {
            sequenceElt.addAttribute("name", sequence.getName(), null);
        } else {
            handleException("Sequence name is required");
        }
        if (sequence.getOnError() != null) {
            sequenceElt.addAttribute("onError", sequence.getOnError(), null);
        }
        if (sequence.getStatistics() != null) {
            sequenceElt.addAttribute("statistics", sequence.getStatistics().name(), null);
        }
        if (sequence.getTrace() != null) {
            sequenceElt.addAttribute("trace", sequence.getTrace().name(), null);
        }
        if (sequence.getDescription() != null) {
            sequenceElt.addAttribute("description", sequence.getDescription(), null);
        }
        return sequenceElt.toString();

    }

    private static OMElement serializeMediators(List<Mediator> mediators) {

        if (mediators != null) {
            OMElement sequenceElt = AnonymousSequenceSerializer.serializeAnonymousSequence(mediators);
            return sequenceElt;
        }
        return null;
    }

    private static void handleException(String s) {

        log.log(Level.SEVERE, s);
        throw new InvalidConfigurationException(s);
    }
}
