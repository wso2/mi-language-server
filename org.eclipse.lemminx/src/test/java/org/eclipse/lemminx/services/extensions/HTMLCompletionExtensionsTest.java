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
package org.eclipse.lemminx.services.extensions;

import static org.eclipse.lemminx.XMLAssert.c;
import static org.eclipse.lemminx.XMLAssert.r;

import org.eclipse.lemminx.AbstractCacheBasedTest;
import org.eclipse.lemminx.XMLAssert;
import org.eclipse.lemminx.commons.BadLocationException;
import org.eclipse.lemminx.services.XMLLanguageService;
import org.eclipse.lemminx.services.extensions.completion.CompletionParticipantAdapter;
import org.eclipse.lemminx.services.extensions.completion.ICompletionParticipant;
import org.eclipse.lemminx.services.extensions.completion.ICompletionRequest;
import org.eclipse.lemminx.services.extensions.completion.ICompletionResponse;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionItemKind;
import org.eclipse.lsp4j.InsertTextFormat;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.TextEdit;
import org.eclipse.lsp4j.jsonrpc.CancelChecker;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.junit.jupiter.api.Test;

/**
 * XML completion tests which uses the {@link ICompletionParticipant} to emulate
 * HTML language.
 *
 */
public class HTMLCompletionExtensionsTest extends AbstractCacheBasedTest {

	@Test
	public void testHTMLElementCompletion() throws BadLocationException {

		testCompletionFor("<|", c("iframe", "<iframe/>", "<iframe"), //
				c("h1", "<h1/>", "<h1"), //
				c("div", "<div/>", "<div"));

		testCompletionFor("< |", c("iframe", "<iframe/>", "<iframe"), //
				c("h1", "<h1/>", "<h1"), //
				c("div", "<div/>", "<div"));

		testCompletionFor("<h|", c("html", "<html/>", "<html"), //
				c("h1", "<h1/>", "<h1"), //
				c("header", "<header/>", "<header"));

		testCompletionFor("<input|", c("input", "<input/>", "<input"));

		testCompletionFor("<inp|ut", c("input", "<input/>", "<input"));

		testCompletionFor("<|inp", c("input", "<input/>", "<input"));
	}

	@Test
	public void testHTMLElementCompletionWithoutOpenStart() throws BadLocationException {

		/*
		 * testCompletionFor("|", r("iframe", "<iframe"), // r("h1", "<h1"), // r("div",
		 * "<div"));
		 */
		testCompletionFor(" |", c("iframe", "<iframe/>", "iframe"), //
				c("h1", "<h1/>", "h1"), //
				c("div", "<div/>", "div"));

		testCompletionFor("h|", c("html", "<html/>", "html"), //
				c("h1", "<h1/>", "h1"), //
				c("header", "<header/>", "header"));

		testCompletionFor("input|", c("input", "<input/>", "input"));

		testCompletionFor("inp|ut", c("input", "<input/>", "input"));

		testCompletionFor("|inp", c("input", "<input/>", "input"));
	}

	@Test
	public void testHTMLAttributeNameCompletion() throws BadLocationException {

		testCompletionFor("<input |", c("type", "type=\"$1\""), //
				c("style", "style=\"$1\""), //
				c("onmousemove", "onmousemove=\"$1\""));

		testCompletionFor("<input t|", c("type", "type=\"$1\""), //
				c("style", "style=\"$1\""));

		testCompletionFor("<input t|ype", c("type", "type=\"$1\""), //
				c("style", "style=\"$1\""));

		// TODO: fix me!
		/*
		 * testCompletionFor("<input t|ype=\"text\"", r("type", "type=\"text\""), //
		 * r("style", "style=\"text\""));
		 *
		 * testCompletionFor("<input type=\"text\" |", r("style",
		 * "<input type=\"text\" style=\"$1\""), // r("type",
		 * "<input type=\"text\" style=\"$1\""), // r("size",
		 * "<input type=\"text\" size=\"$1\""));
		 *
		 * testCompletionFor("<input type=\"text\" s|", r("type",
		 * "<input type=\"text\""), // r("src", "<input type=\"text\" src=\"$1\""), //
		 * r("size", "<input type=\"text\" size=\"$1\""));
		 *
		 * testCompletionFor("<input di| type=\"text\"", r("disabled",
		 * "<input disabled=\"$1\" type=\"text\""), // r("dir",
		 * "<input dir=\"$1\" type=\"text\""));
		 *
		 * testCompletionFor("<input disabled | type=\"text\"", r("dir",
		 * "<input disabled dir=\"$1\" type=\"text\""), // r("style",
		 * "<input disabled style=\"$1\" type=\"text\""));
		 */

	}

	@Test
	public void testHTMLAttributeValueCompletion() throws BadLocationException {
		testCompletionFor("<input type=|", c("text", "\"text\""/* "<input type=\"text\"" */), //
				c("checkbox", "\"checkbox\"" /* "<input type=\"checkbox\"" */));
	}

	@Test
	public void testHTMLOnXMLContentCompletion() throws BadLocationException {
		testCompletionFor("<input>|</input>", //
				c("Test replace range", "replacement text", r(0, 7, 0, 7), null));
		testCompletionFor("<input> |</input>", //
				c("Test replace range", "replacement text", r(0, 7, 0, 8), null));
		testCompletionFor("<input>| </input>", //
				c("Test replace range", "replacement text", r(0, 7, 0, 8), null));
		testCompletionFor("<input>some extisti|ng text</input>", //
				c("Test replace range", "replacement text", r(0, 7, 0, 26), null));
		testCompletionFor("<input>some extisti|ng <br/>text</input>", //
				c("Test replace range", "replacement text", r(0, 7, 0, 22), null));
	}

	public static void testCompletionFor(String value, CompletionItem... expectedItems) throws BadLocationException {
		XMLAssert.testCompletionFor(new HTMLLanguageService(), value, (String) null, null, null, null, true,
				expectedItems);
	}

	private static class HTMLLanguageService extends XMLLanguageService {

		public HTMLLanguageService() {
			super.registerCompletionParticipant(new HTMLCompletionParticipant());
		}

		class HTMLCompletionParticipant extends CompletionParticipantAdapter {

			@Override
			public void onTagOpen(ICompletionRequest completionRequest, ICompletionResponse completionResponse, CancelChecker cancelChecker)
					throws Exception {
				Range range = completionRequest.getReplaceRange();
				HTMLTag.HTML_TAGS.forEach(t -> {
					String tag = t.getTag();
					String label = t.getLabel();
					CompletionItem item = new CompletionItem();
					item.setLabel(tag);
					item.setFilterText(completionRequest.getFilterForStartTagName(tag));
					item.setKind(CompletionItemKind.Property);
					item.setDocumentation(Either.forLeft(label));
					item.setTextEdit(Either.forLeft(new TextEdit(range, "<" + tag + "/>")));
					item.setInsertTextFormat(InsertTextFormat.PlainText);
					completionResponse.addCompletionItem(item);
				});
			}

			@Override
			public void onAttributeName(boolean generateValue, ICompletionRequest completionRequest,
					ICompletionResponse completionResponse, CancelChecker cancelChecker) {
				String tag = completionRequest.getCurrentTag();
				HTMLTag htmlTag = HTMLTag.getHTMLTag(tag);
				if (htmlTag != null) {
					String[] attributes = htmlTag.getAttributes();
					if (attributes != null) {
						Range replaceRange = completionRequest.getReplaceRange();
						for (String attribute : attributes) {
							int index = attribute.indexOf(":");
							if (index != -1) {
								attribute = attribute.substring(0, index);
							}
							if (!completionResponse.hasAttribute(attribute)) {
								CompletionItem item = new CompletionItem();
								item.setLabel(attribute);
								item.setKind(CompletionItemKind.Value);
								String value = generateValue ? "=\"$1\"" : "";
								item.setTextEdit(Either.forLeft(new TextEdit(replaceRange, attribute + value)));
								item.setInsertTextFormat(InsertTextFormat.Snippet);
								completionResponse.addCompletionAttribute(item);
							}
						}
					}
				}
			}

			@Override
			public void onAttributeValue(String valuePrefix, ICompletionRequest completionRequest,
					ICompletionResponse completionResponse, CancelChecker cancelChecker) {
				String tag = completionRequest.getCurrentTag();
				String attributeName = completionRequest.getCurrentAttributeName();
				HTMLTag htmlTag = HTMLTag.getHTMLTag(tag);
				if (htmlTag != null) {
					String[] attributes = htmlTag.getAttributes();
					if (attributes != null) {
						for (String attribute : attributes) {
							String attrName = attribute;
							String attrType = null;
							int index = attribute.indexOf(":");
							if (index != -1) {
								attrName = attribute.substring(0, index);
								attrType = attribute.substring(index + 1, attribute.length());
							}
							if (attrType != null && attributeName.equals(attrName)) {
								Range fullRange = completionRequest.getReplaceRange();
								String[] values = HTMLTag.getAttributeValues(attrType);
								for (String value : values) {
									String insertText = completionRequest.getInsertAttrValue(value);

									CompletionItem item = new CompletionItem();
									item.setLabel(value);
									item.setFilterText(insertText);
									item.setKind(CompletionItemKind.Unit);
									item.setTextEdit(Either.forLeft(new TextEdit(fullRange, insertText)));
									item.setInsertTextFormat(InsertTextFormat.PlainText);
									completionResponse.addCompletionAttribute(item);
								}
								break;
							}
						}
					}
				}
			}

			@Override
			public void onXMLContent(ICompletionRequest request, ICompletionResponse response, CancelChecker cancelChecker) {
				CompletionItem completion = new CompletionItem("Test replace range");
				TextEdit edit = new TextEdit();
				edit.setNewText("replacement text");
				edit.setRange(request.getReplaceRange());
				completion.setTextEdit(Either.forLeft(edit));
				response.addCompletionItem(completion);
			}
		}
	}
}
