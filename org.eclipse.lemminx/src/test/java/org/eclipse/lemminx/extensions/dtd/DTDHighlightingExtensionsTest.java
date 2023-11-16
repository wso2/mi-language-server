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
package org.eclipse.lemminx.extensions.dtd;

import static org.eclipse.lemminx.XMLAssert.hl;
import static org.eclipse.lemminx.XMLAssert.r;
import static org.eclipse.lemminx.XMLAssert.testHighlightsFor;
import static org.eclipse.lsp4j.DocumentHighlightKind.Read;
import static org.eclipse.lsp4j.DocumentHighlightKind.Write;

import org.eclipse.lemminx.AbstractCacheBasedTest;
import org.eclipse.lemminx.commons.BadLocationException;
import org.junit.jupiter.api.Test;

/**
 * DTD Highlighting tests.
 *
 * @author Angelo ZERR
 */
public class DTDHighlightingExtensionsTest extends AbstractCacheBasedTest {

	@Test
	public void noHighlighting() throws BadLocationException {
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\r\n" + //
				"<!DOCTYPE note [\r\n" + //
				"	<!ELEMENT note (to,from,heading,body, note?|)>\r\n" + // <--after ?, no highlight
				"	<!ATTLIST note version CDATA #REQUIRED>\r\n" + //
				"]>";
		testHighlightsFor(xml);
	}

	@Test
	public void noHighlighting2() throws BadLocationException {
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\r\n" + //
				"<!DOCTYPE note [\r\n" + //
				"	<!ELEMENT note (to,from,heading,body, note?)>\r\n" +
				"	<!ATTLIST note version CD|ATA #REQUIRED>\r\n" + // // <-- in CDATA, no highlight
				"]>";
		testHighlightsFor(xml);
	}

	@Test
	public void highlightingOnDTDElementName() throws BadLocationException {
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\r\n" + //
				"<!DOCTYPE note [\r\n" + //
				"	<!ELEMENT no|te (to,from,heading,body, note?)>\r\n" + // <-- highlight <!ELEMENT note
				"	<!ATTLIST note version CDATA #REQUIRED>\r\n" + //
				"]>";
		testHighlightsFor(xml, hl(r(2, 11, 2, 15), Write), hl(r(2, 39, 2, 43), Read), hl(r(3, 11, 3, 15), Read));
	}

	@Test
	public void highlightingOnDTDAttListName() throws BadLocationException {
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\r\n" + //
				"<!DOCTYPE note [\r\n" + //
				"	<!ELEMENT note (to,from,heading,body, note?)>\r\n" + //
				"	<!ATTLIST no|te version CDATA #REQUIRED>\r\n" + // <-- highlight <!ATTLIST note
				"]>";
		testHighlightsFor(xml, hl(r(3, 11, 3, 15), Read), hl(r(2, 11, 2, 15), Write));
	}

	@Test
	public void highlightingOnDTDElementChildName() throws BadLocationException {
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\r\n" + //
				"<!DOCTYPE note [\r\n" + //
				"	<!ELEMENT note (to,from,heading,body, no|te?)>\r\n" + // <-- highlight (... no|te?
				"	<!ATTLIST note version CDATA #REQUIRED>\r\n" + //
				"]>";
		testHighlightsFor(xml, hl(r(2, 39, 2, 43), Read), hl(r(2, 11, 2, 15), Write));
	}
}
