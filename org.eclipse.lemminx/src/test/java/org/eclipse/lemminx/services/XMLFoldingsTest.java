/**
 *  Copyright (c) 2018 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v2.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package org.eclipse.lemminx.services;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.lemminx.commons.TextDocument;
import org.eclipse.lemminx.dom.DOMDocument;
import org.eclipse.lemminx.dom.DOMParser;
import org.eclipse.lemminx.settings.XMLFoldingSettings;
import org.eclipse.lsp4j.FoldingRange;
import org.eclipse.lsp4j.FoldingRangeCapabilities;
import org.junit.jupiter.api.Test;

/**
 * XML foldings services tests
 *
 */
public class XMLFoldingsTest {

	private static class ExpectedIndentRange {

		public final int startLine;
		public final int endLine;
		public final String kind;

		public ExpectedIndentRange(int startLine, int endLine, String kind) {
			this.startLine = startLine;
			this.endLine = endLine;
			this.kind = kind;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + endLine;
			result = prime * result + ((kind == null) ? 0 : kind.hashCode());
			result = prime * result + startLine;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			ExpectedIndentRange other = (ExpectedIndentRange) obj;
			if (endLine != other.endLine) {
				return false;
			}
			if (kind == null) {
				if (other.kind != null) {
					return false;
				}
			} else if (!kind.equals(other.kind)) {
				return false;
			}
			if (startLine != other.startLine) {
				return false;
			}
			return true;
		}
	}

	@Test
	public void testFoldOneLevel() {
		String[] input = new String[] {
			/*0*/"<html>",
			/*1*/"Hello",
			/*2*/"</html>"
		};
		assertRanges(input,  new ExpectedIndentRange[] {r(0, 1)});
	}


	@Test
	public void testFoldTwoLevel()  {
		String[] input = new String[] {
			/*0*/"<html>",
			/*1*/"<head>",
			/*2*/"Hello",
			/*3*/"</head>",
			/*4*/"</html>"
		};
		assertRanges(input,  new ExpectedIndentRange[] {r(0, 3), r(1, 2)});
	}

	@Test
	public void testFoldSiblings()  {
		String[] input = new String[] {
			/*0*/"<html>",
			/*1*/"<head>",
			/*2*/"Head",
			/*3*/"</head>",
			/*4*/"<body class=\"f\">",
			/*5*/"Body",
			/*6*/"</body>",
			/*7*/"</html>"
		};
		assertRanges(input,  new ExpectedIndentRange[] {r(0, 6), r(1, 2), r(4, 5)});
	}


	@Test
	public void testFoldComment()  {
		String[] input = new String[] {
			/*0*/"<!--",
			/*1*/" multi line",
			/*2*/"-->",
			/*3*/"<!-- some stuff",
			/*4*/" some more stuff -->",
		};
		assertRanges(input,  new ExpectedIndentRange[] {r(0, 1, "comment")});
	}

	@Test
	public void testFoldRegions()  {
		String[] input = new String[] {
			/*0*/"<!-- #region -->",
			/*1*/"<!-- #region -->",
			/*2*/"<!-- #endregion -->",
			/*3*/"<!-- #endregion -->",
		};
		assertRanges(input,  new ExpectedIndentRange[] {r(0, 2, "region")});
	}

	@Test
	public void testFoldIncomplete()  {
		String[] input = new String[] {
			/*0*/"<body>",
			/*1*/"<div></div>",
			/*2*/"Hello",
			/*3*/"</div>",
			/*4*/"</body>",
		};
		assertRanges(input,  new ExpectedIndentRange[] {r(0, 3)});
	}

	@Test
	public void testFoldIcomplete2()  {
		String[] input = new String[] {
			/*0*/"<be><div>",
			/*1*/"<!-- #endregion -->",
			/*2*/"</div>",
		};
		assertRanges(input,  new ExpectedIndentRange[] {r(0, 1)});
	}

	@Test
	public void testFoldIntersectingRegion()  {
		String[] input = new String[] {
			/*0*/"<body>",
			/*1*/"<!-- #region -->",
			/*2*/"Hello",
			/*3*/"<div></div>",
			/*4*/"</body>",
			/*5*/"<!-- #endregion -->",
		};
		assertRanges(input,  new ExpectedIndentRange[] {r(0, 3)});
	}

	@Test
	public void testFoldIntersectingRegion2()  {
		String[] input = new String[] {
			/*0*/"<!-- #region -->",
			/*1*/"<body>",
			/*2*/"Hello",
			/*3*/"<!-- #endregion -->",
			/*4*/"<div></div>",
			/*5*/"</body>",
		};
		assertRanges(input,  new ExpectedIndentRange[] {r(0, 2, "region")});
	}

	@Test
	public void testLimit()  {
		String[] input = new String[] {
			/* 0*/"<div>",
			/* 1*/" <span>",
			/* 2*/"  <b>",
			/* 3*/"  ",
			/* 4*/"  </b>,",
			/* 5*/"  <b>",
			/* 6*/"   <pre>",
			/* 7*/"  ",
			/* 8*/"   </pre>,",
			/* 9*/"   <pre>",
			/*10*/"  ",
			/*11*/"   </pre>,",
			/*12*/"  </b>,",
			/*13*/"  <b>",
			/*14*/"  ",
			/*15*/"  </b>,",
			/*16*/"  <b>",
			/*17*/"  ",
			/*18*/"  </b>",
			/*19*/" </span>",
			/*20*/"</div>",
		};
		XMLFoldingSettings settings = new XMLFoldingSettings ();
        settings.setIncludeClosingTagInFold(false);
		assertRanges(input, new ExpectedIndentRange[] {r(0, 19), r(1, 18), r(2, 3), r(5, 11), r(6, 7), r(9, 10), r(13, 14), r(16, 17)}, "no limit", null);
		assertRanges(input, new ExpectedIndentRange[] {r(0, 19), r(1, 18), r(2, 3), r(5, 11), r(6, 7), r(9, 10), r(13, 14), r(16, 17)}, "limit 8", 8);
		assertRanges(input, new ExpectedIndentRange[] {r(0, 19), r(1, 18), r(2, 3), r(5, 11), r(6, 7), r(13, 14), r(16, 17)}, "limit 7", 7);
		assertRanges(input, new ExpectedIndentRange[] {r(0, 19), r(1, 18), r(2, 3), r(5, 11), r(13, 14), r(16, 17)}, "limit 6", 6);
		assertRanges(input, new ExpectedIndentRange[] {r(0, 19), r(1, 18), r(2, 3), r(5, 11), r(13, 14)}, "limit 5", 5);
		assertRanges(input, new ExpectedIndentRange[] {r(0, 19), r(1, 18), r(2, 3), r(5, 11)}, "limit 4", 4);
		assertRanges(input, new ExpectedIndentRange[] {r(0, 19), r(1, 18), r(2, 3)}, "limit 3", 3);
		assertRanges(input, new ExpectedIndentRange[] {r(0, 19), r(1, 18)}, "limit 2", 2);
		assertRanges(input, new ExpectedIndentRange[] {r(0, 19)}, "limit 1", 1);
	}

	@Test
	public void testFoldDoctype()  {
		String[] input = new String[] {
			/*0*/"<!DOCTYPE opt [",
			/*1*/"  <!ENTITY size \"short\">",
			/*2*/"  <!ENTITY ResolutionX \"1920\"> 	",
			/*3*/"]>",
		};
		assertRanges(input,  new ExpectedIndentRange[] {r(0, 2)});
	}

	@Test
	public void testFoldOneLevelWithincludeClosingTagInFoldAsTrue () {
		String[] input = new String[] {
			/*0*/"<html>",
			/*1*/"Hello",
			/*2*/"</html>"
		};
		XMLFoldingSettings settings = new XMLFoldingSettings ();
        settings.setIncludeClosingTagInFold(true);
		assertRanges(input,  new ExpectedIndentRange[] {r(0, 2)}, settings);
	}

	@Test
	public void testFoldTwoLevelWithincludeClosingTagInFoldAsTrue()  {
		String[] input = new String[] {
			/*0*/"<html>",
			/*1*/"<head>",
			/*2*/"Hello",
			/*3*/"</head>",
			/*4*/"</html>"
		};
		XMLFoldingSettings settings = new XMLFoldingSettings ();
        settings.setIncludeClosingTagInFold(true);
		assertRanges(input,  new ExpectedIndentRange[] {r(0, 4), r(1, 3)}, settings);
	}

	@Test
	public void testFoldSiblingsWithincludeClosingTagInFoldAsTrue()  {
		String[] input = new String[] {
			/*0*/"<html>",
			/*1*/"<head>",
			/*2*/"Head",
			/*3*/"</head>",
			/*4*/"<body class=\"f\">",
			/*5*/"Body",
			/*6*/"</body>",
			/*7*/"</html>"
		};
		XMLFoldingSettings settings = new XMLFoldingSettings ();
        settings.setIncludeClosingTagInFold(true);
		assertRanges(input,  new ExpectedIndentRange[] {r(0, 7), r(1, 3), r(4, 6)}, settings);
	}


	@Test
	public void testFoldCommentWithincludeClosingTagInFoldAsTrue()  {
		String[] input = new String[] {
			/*0*/"<!--",
			/*1*/" multi line",
			/*2*/"-->",
			/*3*/"<!-- some stuff",
			/*4*/" some more stuff -->",
		};
		XMLFoldingSettings settings = new XMLFoldingSettings ();
        settings.setIncludeClosingTagInFold(true);
		assertRanges(input,  new ExpectedIndentRange[] {r(0, 2, "comment"), r(3, 4, "comment")}, settings);
	}

	@Test
	public void testFoldRegionsWithincludeClosingTagInFoldAsTrue()  {
		String[] input = new String[] {
			/*0*/"<!-- #region -->",
			/*1*/"<!-- #region -->",
			/*2*/"<!-- #endregion -->",
			/*3*/"<!-- #endregion -->",
		};
		XMLFoldingSettings settings = new XMLFoldingSettings ();
        settings.setIncludeClosingTagInFold(true);
		assertRanges(input,  new ExpectedIndentRange[] {r(0, 3, "region"), r(1, 2, "region")}, settings);
	}

	@Test
	public void testFoldIncompleteWithincludeClosingTagInFoldAsTrue()  {
		String[] input = new String[] {
			/*0*/"<body>",
			/*1*/"<div></div>",
			/*2*/"Hello",
			/*3*/"</div>",
			/*4*/"</body>",
		};
		XMLFoldingSettings settings = new XMLFoldingSettings ();
        settings.setIncludeClosingTagInFold(true);
		assertRanges(input,  new ExpectedIndentRange[] {r(0, 4)}, settings);
	}

	@Test
	public void testFoldIcomplete2WithincludeClosingTagInFoldAsTrue()  {
		String[] input = new String[] {
			/*0*/"<be><div>",
			/*1*/"<!-- #endregion -->",
			/*2*/"</div>",
		};
		XMLFoldingSettings settings = new XMLFoldingSettings ();
        settings.setIncludeClosingTagInFold(true);
		assertRanges(input,  new ExpectedIndentRange[] {r(0, 2)}, settings);
	}

	@Test
	public void testFoldIntersectingRegionWithincludeClosingTagInFoldAsTrue()  {
		String[] input = new String[] {
			/*0*/"<body>",
			/*1*/"<!-- #region -->",
			/*2*/"Hello",
			/*3*/"<div></div>",
			/*4*/"</body>",
			/*5*/"<!-- #endregion -->",
		};
		XMLFoldingSettings settings = new XMLFoldingSettings ();
        settings.setIncludeClosingTagInFold(true);
		assertRanges(input,  new ExpectedIndentRange[] {r(0, 4)}, settings);
	}

	@Test
	public void testFoldIntersectingRegion2WithincludeClosingTagInFoldAsTrue()  {
		String[] input = new String[] {
			/*0*/"<!-- #region -->",
			/*1*/"<body>",
			/*2*/"Hello",
			/*3*/"<!-- #endregion -->",
			/*4*/"<div></div>",
			/*5*/"</body>",
		};
		XMLFoldingSettings settings = new XMLFoldingSettings ();
        settings.setIncludeClosingTagInFold(true);
		assertRanges(input,  new ExpectedIndentRange[] {r(0, 3, "region")}, settings);
	}

	@Test
	public void testLimitWithincludeClosingTagInFoldAsTrue()  {
		String[] input = new String[] {
			/* 0*/"<div>",
			/* 1*/" <span>",
			/* 2*/"  <b>",
			/* 3*/"  ",
			/* 4*/"  </b>,",
			/* 5*/"  <b>",
			/* 6*/"   <pre>",
			/* 7*/"  ",
			/* 8*/"   </pre>,",
			/* 9*/"   <pre>",
			/*10*/"  ",
			/*11*/"   </pre>,",
			/*12*/"  </b>,",
			/*13*/"  <b>",
			/*14*/"  ",
			/*15*/"  </b>,",
			/*16*/"  <b>",
			/*17*/"  ",
			/*18*/"  </b>",
			/*19*/" </span>",
			/*20*/"</div>",
		};
		XMLFoldingSettings settings = new XMLFoldingSettings ();
        settings.setIncludeClosingTagInFold(true);
		assertRanges(input, new ExpectedIndentRange[] {r(0, 20), r(1, 19), r(2, 4), r(5, 12), r(6, 8), r(9, 11), r(13, 15), r(16, 18)}, "no limit", null, settings);
		assertRanges(input, new ExpectedIndentRange[] {r(0, 20), r(1, 19), r(2, 4), r(5, 12), r(6, 8), r(9, 11), r(13, 15), r(16, 18)}, "limit 8", 8, settings);
		assertRanges(input, new ExpectedIndentRange[] {r(0, 20), r(1, 19), r(2, 4), r(5, 12), r(6, 8), r(13, 15), r(16, 18)}, "limit 7", 7, settings);
		assertRanges(input, new ExpectedIndentRange[] {r(0, 20), r(1, 19), r(2, 4), r(5, 12), r(13, 15), r(16, 18)}, "limit 6", 6, settings);
		assertRanges(input, new ExpectedIndentRange[] {r(0, 20), r(1, 19), r(2, 4), r(5, 12), r(13, 15)}, "limit 5", 5, settings);
		assertRanges(input, new ExpectedIndentRange[] {r(0, 20), r(1, 19), r(2, 4), r(5, 12)}, "limit 4", 4, settings);
		assertRanges(input, new ExpectedIndentRange[] {r(0, 20), r(1, 19), r(2, 4)}, "limit 3", 3, settings);
		assertRanges(input, new ExpectedIndentRange[] {r(0, 20), r(1, 19)}, "limit 2", 2, settings);
		assertRanges(input, new ExpectedIndentRange[] {r(0, 20)}, "limit 1", 1, settings);
	}

	@Test
	public void testFoldDoctypeWithincludeClosingTagInFoldAsTrue()  {
		String[] input = new String[] {
			/*0*/"<!DOCTYPE opt [",
			/*1*/"  <!ENTITY size \"short\">",
			/*2*/"  <!ENTITY ResolutionX \"1920\"> 	",
			/*3*/"]>",
		};
		XMLFoldingSettings settings = new XMLFoldingSettings ();
        settings.setIncludeClosingTagInFold(true);
		assertRanges(input,  new ExpectedIndentRange[] {r(0, 3)}, settings);
	}

	private static void assertRanges(String[] lines, ExpectedIndentRange[] expected) {
		assertRanges(lines, expected, "", null, new XMLFoldingSettings());
	}

	private static void assertRanges(String[] lines, ExpectedIndentRange[] expected, XMLFoldingSettings settings) {
		assertRanges(lines, expected, "", null, settings);
	}

	private static void assertRanges(String[] lines, ExpectedIndentRange[] expected, String message, Integer nRanges) {
		assertRanges(lines, expected, message, nRanges, new XMLFoldingSettings());
	}

	private static void assertRanges(String[] lines, ExpectedIndentRange[] expected, String message, Integer nRanges, XMLFoldingSettings settings) {
		TextDocument document = new TextDocument(String.join("\n", lines), "test://foo/bar.json");
		DOMDocument xmlDocument = DOMParser.getInstance().parse(document, null);
		XMLLanguageService languageService = new XMLLanguageService();

		FoldingRangeCapabilities context = new FoldingRangeCapabilities();
		context.setRangeLimit(nRanges);
		settings.setCapabilities(context);
		List<FoldingRange> actual = languageService.getFoldingRanges(xmlDocument, settings);
		
		List<ExpectedIndentRange> actualRanges = new ArrayList<>();
		for (FoldingRange f : actual) {
			actualRanges.add(r(f.getStartLine(), f.getEndLine(), f.getKind()));
		}
		Collections.sort(actualRanges, (r1, r2) -> r1.startLine - r2.startLine);
		assertArrayEquals(expected, actualRanges.toArray(), message);
	}

	private static ExpectedIndentRange r(int startLine, int endLine) {
		return r(startLine, endLine, null);
	}

	private static ExpectedIndentRange r(int startLine, int endLine, String kind) {
		return new ExpectedIndentRange(startLine, endLine, kind);
	}
}
