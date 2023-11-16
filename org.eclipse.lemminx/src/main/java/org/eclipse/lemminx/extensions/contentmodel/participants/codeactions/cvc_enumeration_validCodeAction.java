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
package org.eclipse.lemminx.extensions.contentmodel.participants.codeactions;

import java.util.List;

import org.eclipse.lemminx.commons.CodeActionFactory;
import org.eclipse.lemminx.dom.DOMDocument;
import org.eclipse.lemminx.dom.DOMElement;
import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.extensions.contentmodel.model.CMDocument;
import org.eclipse.lemminx.extensions.contentmodel.model.CMElementDeclaration;
import org.eclipse.lemminx.extensions.contentmodel.model.ContentModelManager;
import org.eclipse.lemminx.services.extensions.codeaction.ICodeActionParticipant;
import org.eclipse.lemminx.services.extensions.codeaction.ICodeActionRequest;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.jsonrpc.CancelChecker;

/**
 * Code action to fix cvc_enumeration_valid error only for text, because for
 * attribute cvc-attribute-3 error fix it.
 *
 */
public class cvc_enumeration_validCodeAction implements ICodeActionParticipant {

	@Override
	public void doCodeAction(ICodeActionRequest request, List<CodeAction> codeActions, CancelChecker cancelChecker) {
		Diagnostic diagnostic = request.getDiagnostic();
		DOMDocument document = request.getDocument();
		Range range = request.getRange();
		try {
			int offset = document.offsetAt(range.getStart());
			DOMNode node = document.findNodeBefore(offset);
			if (node != null && node.isElement()) {
				DOMElement element = (DOMElement) node;
				ContentModelManager contentModelManager = request.getComponent(ContentModelManager.class);
				for (CMDocument cmDocument : contentModelManager.findCMDocument(element)) {
					CMElementDeclaration cmElement = cmDocument.findCMElement(element);
					if (cmElement != null) {
						cmElement.getEnumerationValues().forEach(value -> {
							// Replace text content
							CodeAction replaceTextContentAction = CodeActionFactory.replace(
									"Replace with '" + value + "'", range, value, document.getTextDocument(),
									diagnostic);
							codeActions.add(replaceTextContentAction);
						});
					}
				}
			}
		} catch (Exception e) {
			// do nothing
		}
	}

}
