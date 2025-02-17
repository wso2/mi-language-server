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

package org.eclipse.lemminx.customservice.synapse.definition;

import org.eclipse.lemminx.commons.BadLocationException;
import org.eclipse.lemminx.customservice.synapse.utils.ConfigFinder;
import org.eclipse.lemminx.customservice.synapse.utils.LegacyConfigFinder;
import org.eclipse.lemminx.customservice.synapse.utils.Constant;
import org.eclipse.lemminx.customservice.synapse.utils.Utils;
import org.eclipse.lemminx.dom.DOMAttr;
import org.eclipse.lemminx.dom.DOMDocument;
import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.jsonrpc.CancelChecker;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SynapseDefinitionProvider {

    private static final Logger LOGGER = Logger.getLogger(SynapseDefinitionProvider.class.getName());

    private static List<String> sequenceAttributes = List.of("inSequence", "outSequence", "faultSequence", "sequence"
            , "onAccept", "onReject", "obligation", "advice", "onError");
    private static List<String> endpointAttributes = List.of("endpointKey", "targetEndpoint", "endpoint");

    public static Location definition(DOMDocument document, Position position, String projectPath,
                                      CancelChecker cancelChecker) {

        cancelChecker.checkCanceled();
        int offset;
        try {
            offset = document.offsetAt(position);
        } catch (BadLocationException e) {
            LOGGER.log(Level.WARNING, "Error while reading file content", e);
            return null;
        }

        KeyAndTypeHolder keyAndType = getKeyAndType(document, offset);
        if (!keyAndType.isNull()) {
            Boolean isLegacyProject = Utils.isLegacyProject(projectPath);

            String path = null;
            try {
                if (isLegacyProject) {
                    path = LegacyConfigFinder.findEsbComponentPath(keyAndType.getKey(), keyAndType.getType(),
                            projectPath);
                } else {
                    path = ConfigFinder.findEsbComponentPath(keyAndType.getKey(), keyAndType.getType(),
                            projectPath);
                }
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Error while reading file content", e);
            }
            if (path == null) {
                return null;
            }
            Range range = getDefinitionRange(path);
            Location location = new Location(path, range);
            return location;
        }
        return null;
    }

    private static KeyAndTypeHolder getKeyAndType(DOMDocument document, int offset) {

        DOMAttr clickedAttr = document.findAttrAt(offset);
        if (clickedAttr != null && clickedAttr.getNodeAttrName().getEnd() <= offset) {
            DOMNode node = document.findNodeAt(offset);
            String type = null;
            String key = null;
            if (Constant.ENDPOINT.equalsIgnoreCase(node.getNodeName()) || Constant.SEQUENCE.equalsIgnoreCase(node.getNodeName())) {
                type = node.getNodeName().toLowerCase() + "s";
                key = node.getAttribute(Constant.KEY);
            } else if (Constant.CALL_TEMPLATE.equalsIgnoreCase(node.getNodeName())) {
                type = "templates";
                key = node.getAttribute(Constant.TARGET);
            } else if (Constant.STORE.equalsIgnoreCase(node.getNodeName())) {
                type = "message-stores";
                key = node.getAttribute(Constant.MESSAGE_STORE);
            } else {
                if (sequenceAttributes.contains(clickedAttr.getNodeName())) {
                    type = "sequences";
                    key = clickedAttr.getNodeValue();
                } else if (endpointAttributes.contains(clickedAttr.getNodeName())) {
                    type = "endpoints";
                    key = clickedAttr.getNodeValue();
                } else if ("configKey".equals(clickedAttr.getNodeName())) {
                    key = clickedAttr.getNodeValue();
                    type = "local-entries";
                } else {
                    String attKey = clickedAttr.getNodeValue();
                    if (attKey != null) {
                        if (attKey.contains(Constant.GOV_REGISTRY_PREFIX) || attKey.contains(Constant.CONF_REGISTRY_PREFIX)) {
                            key = attKey;
                        }
                    }
                }
            }
            KeyAndTypeHolder keyAndTypeHolder = new KeyAndTypeHolder(key, type);
            return keyAndTypeHolder;
        }
        return new KeyAndTypeHolder(null, null);
    }

    private static Range getDefinitionRange(String path) {

        File file = new File(path);
        Range range;
        try {
            DOMDocument document = Utils.getDOMDocument(file);
            DOMNode node = Utils.getRootElementFromConfigXml(document);
            if (node == null) {
                return new Range(new Position(0, 0), new Position(0, 1));
            }
            Position start = document.positionAt(node.getStart());
            Position end = document.positionAt(node.getEnd());
            range = new Range(start, end);
        } catch (IOException | BadLocationException e) {
            range = new Range(new Position(0, 0), new Position(0, 1));
        }
        return range;
    }

    private static class KeyAndTypeHolder {

        private String key;
        private String type;

        public KeyAndTypeHolder(String key, String type) {

            this.key = key;
            this.type = type;
        }

        public String getKey() {

            return key;
        }

        public String getType() {

            return type;
        }

        public boolean isNull() {

            return key == null && type == null;
        }
    }
}
