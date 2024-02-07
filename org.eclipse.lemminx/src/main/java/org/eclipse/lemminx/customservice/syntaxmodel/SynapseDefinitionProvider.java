/*
 * Copyright (c) 2024, WSO2 LLC. (http://www.wso2.com).
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.eclipse.lemminx.customservice.syntaxmodel;

import org.eclipse.lemminx.commons.BadLocationException;
import org.eclipse.lemminx.customservice.syntaxmodel.utils.ConfigFinder;
import org.eclipse.lemminx.customservice.syntaxmodel.utils.Constant;
import org.eclipse.lemminx.customservice.syntaxmodel.utils.Utils;
import org.eclipse.lemminx.dom.DOMDocument;
import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.jsonrpc.CancelChecker;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SynapseDefinitionProvider {

    private static final Logger LOGGER = Logger.getLogger(SynapseDefinitionProvider.class.getName());

    public static Location definition(DOMDocument document, Position position, CancelChecker cancelChecker) {

        cancelChecker.checkCanceled();
        int offset;
        try {
            offset = document.offsetAt(position);
        } catch (BadLocationException e) {
            LOGGER.log(Level.WARNING, "Error while reading file content", e);
            return null;
        }
        DOMNode node = document.findNodeAt(offset);
        String key = null;
        if (node != null) {
            key = node.getAttribute(Constant.KEY);
        }
        if (key != null) {
            try {
                String projectPath = Utils.findRootPath(document.getDocumentURI());
                String path = null;
                try {
                    path = ConfigFinder.findEsbComponentPath(key, node.getNodeName().toLowerCase() + "s", projectPath);
                } catch (IOException e) {
                    LOGGER.log(Level.WARNING, "Error while reading file content", e);
                }
                String filePath = Constant.FILE_PREFIX + path;
                Range range = getDefinitionRange(path);
                Location location = new Location(filePath, range);
                return location;
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Error while reading file content", e);
            }
        }
        return null;
    }

    private static Range getDefinitionRange(String path) {

        File file = new File(path);
        Range range;
        try {
            DOMDocument document = Utils.getDOMDocument(file);
            DOMNode node = Utils.getRootElementFromConfigXml(document);
            Position start = document.positionAt(node.getStart());
            Position end = document.positionAt(node.getEnd());
            range = new Range(start, end);
        } catch (IOException | BadLocationException e) {
            range = new Range(new Position(0, 0), new Position(0, 1));
        }
        return range;
    }
}
