/*
 *   Copyright (c) 2025, WSO2 LLC. (http://www.wso2.com).
 *
 *   WSO2 LLC. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 */

package org.eclipse.lemminx.customservice.synapse.schemagen.xsd;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSParticle;

/**
 * The TypeProcessor interface defines methods for processing XML Schema types and converting them into JSON Schema nodes.
 */
public interface TypeProcessor {

    /**
     * Processes an XML Schema element declaration and updates the provided JSON Schema object node.
     *
     * @param element          the XML Schema element declaration
     * @param elementStructure the XML Schema particle structure
     * @param node             the JSON Schema object node to update
     * @param id               the identifier for the JSON Schema node
     * @param addTitle         flag indicating if the title should be added
     */
    void processType(XSElementDeclaration element, XSParticle elementStructure, ObjectNode node, String id,
                     boolean addTitle);

    /**
     * Processes the root XML Schema element declaration and updates the provided JSON Schema object node.
     *
     * @param element  the XML Schema element declaration
     * @param node     the JSON Schema object node to update
     * @param id       the identifier for the JSON Schema node
     * @param addTitle flag indicating if the title should be added
     */
    void processRootType(XSElementDeclaration element, ObjectNode node, String id, boolean addTitle);
}
