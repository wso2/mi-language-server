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

import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xs.XSElementDeclaration;

/**
 * The TypeProcessorFactory class provides a method to obtain the appropriate TypeProcessor
 * based on the type of the XML Schema element declaration.
 */
public class TypeProcessorFactory {

    /**
     * Returns the appropriate TypeProcessor for the given XML Schema element declaration.
     *
     * @param element the XML Schema element declaration
     * @return the TypeProcessor for the given element
     */
    public static TypeProcessor getTypeProcessor(XSElementDeclaration element) {

        if (element.getTypeDefinition() instanceof XSComplexTypeDefinition) {
            return new ComplexTypeProcessor();
        } else {
            return new SimpleTypeProcessor();
        }
    }
}
