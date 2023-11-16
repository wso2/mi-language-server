/*******************************************************************************
* Copyright (c) 2019 Red Hat Inc. and others.
* All rights reserved. This program and the accompanying materials
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v20.html
*
* SPDX-License-Identifier: EPL-2.0
*
* Contributors:
*     Red Hat Inc. - initial API and implementation
*******************************************************************************/
package org.eclipse.lemminx.extensions.xsd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

/**
 * XSD Data type tests
 * 
 * @author Angelo ZERR
 *
 */
public class DataTypeTest {

	@Test
	public void xsString() {
		DataType string = DataType.getDataType("string");
		assertNotNull(string);
		assertEquals("string", string.getName());
	}
}
