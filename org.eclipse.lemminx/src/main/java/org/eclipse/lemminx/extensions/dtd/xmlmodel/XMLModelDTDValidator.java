/*******************************************************************************
* Copyright (c) 2020 Red Hat Inc. and others.
* All rights reserved. This program and the accompanying materials
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v20.html
*
* SPDX-License-Identifier: EPL-2.0
*
* Contributors:
*     Red Hat Inc. - initial API and implementation
*******************************************************************************/
package org.eclipse.lemminx.extensions.dtd.xmlmodel;

import org.eclipse.lemminx.extensions.xerces.ExternalXMLDTDValidator;
import org.eclipse.lemminx.extensions.xerces.xmlmodel.XMLModelValidator;
import org.xml.sax.XMLReader;

/**
 * XML model validator which process validation with DTD:
 * 
 * <pre>
 * 	&lt;?xml-model href="http://java.sun.com/dtd/web-app_2_3.dtd"?&gt;
 * </pre>
 *
 */
public class XMLModelDTDValidator extends ExternalXMLDTDValidator implements XMLModelValidator {

	public XMLModelDTDValidator(String href) {
		super.setExternalDoctype(href);
	}
	
	@Override
	public void setExternalDoctype(String externalDoctype) {

	}

	@Override
	public void setXMLReader(XMLReader documentHandler) {

	}
}
