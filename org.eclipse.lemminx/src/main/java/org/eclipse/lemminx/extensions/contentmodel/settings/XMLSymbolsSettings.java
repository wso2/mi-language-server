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
package org.eclipse.lemminx.extensions.contentmodel.settings;

/**
 * XML symbol settings for content model.
 * 
 */
public class XMLSymbolsSettings {

	private boolean showReferencedGrammars;

	/**
	 * Returns true if the referenced grammars must be inserted in the symbols and
	 * false otherwise.
	 * 
	 * @return true if the referenced grammars must be inserted in the symbols and
	 *         false otherwise.
	 */
	public boolean isShowReferencedGrammars() {
		return showReferencedGrammars;
	}

	public void setShowReferencedGrammars(boolean showReferencedGrammars) {
		this.showReferencedGrammars = showReferencedGrammars;
	}
}
