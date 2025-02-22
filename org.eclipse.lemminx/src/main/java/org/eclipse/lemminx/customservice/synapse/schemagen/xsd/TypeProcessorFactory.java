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
