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

package org.eclipse.lemminx.customservice.synapse.syntaxTree;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.APIFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.AbstractFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.DataServiceConfigFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.DataSourceConfigFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.InboundEndpointFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.LocalEntryFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.MessageProcessorFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.MessageStoreFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.NamedSequenceFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.ProxyFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.TaskFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.TemplateFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.endpoint.EndpointFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.misc.Wsdl11Factory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.misc.Wsdl20Factory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.STNode;
import org.eclipse.lemminx.customservice.synapse.utils.Constant;
import org.eclipse.lemminx.customservice.synapse.utils.OptionalTypeAdapter;
import org.eclipse.lemminx.customservice.synapse.utils.Utils;
import org.eclipse.lemminx.dom.DOMDocument;
import org.eclipse.lemminx.dom.DOMElement;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SyntaxTreeGenerator {

    private static final Logger LOGGER = Logger.getLogger(SyntaxTreeGenerator.class.getName());

    private static String projectPath;
    private static List<String> componentNames = Arrays.asList(Constant.API, Constant.ENDPOINT, Constant.INBOUND_ENDPOINT,
            Constant.MESSAGE_PROCESSOR, Constant.LOCAL_ENTRY, Constant.MESSAGE_STORE, Constant.PROXY, Constant.SEQUENCE,
            Constant.TASK, Constant.TEMPLATE, Constant.WSDL_DEFINITIONS, Constant.WSDL_DESCRIPTION, Constant.DATA,
            Constant.DATA_SOURCE);

    public SyntaxTreeResponse getSyntaxTree(DOMDocument document) {

        setProjectPath(document.getDocumentURI());
        SyntaxTreeResponse response = new SyntaxTreeResponse(null, document.getDocumentURI());
        DOMElement rootElement = getRootElement(document);
        STNode tree = buildTree(rootElement);
        if (tree != null) {
            String rootTag = tree.getTag();
            Gson gson = new GsonBuilder()
                    .registerTypeHierarchyAdapter(Optional.class, new OptionalTypeAdapter())
                    .serializeNulls()
                    .disableHtmlEscaping()
                    .create();
            JsonElement nextNode = gson.toJsonTree(tree);
            JsonObject root = new JsonObject();
            root.add(rootTag, nextNode);
            response.setSyntaxTree(root);
        }
        return response;
    }

    private void setProjectPath(String documentURI) {

        if (documentURI != null) {
            String tempUri = documentURI.replace(Constant.FILE_PREFIX, Constant.EMPTY_STRING);
            Boolean isLegacy = Utils.isLegacyProject(tempUri);
            try {
                projectPath = Utils.findProjectRootPath(tempUri, isLegacy);
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error occurred while finding the project root path.", e);
            }
        }
    }

    private DOMElement getRootElement(DOMDocument document) {

        DOMElement rootElement = null;
        for (int i = 0; i < document.getChildren().size(); i++) {
            String elementName = document.getChild(i).getNodeName();
            if (Utils.containsIgnoreCase(componentNames, elementName)) {
                rootElement = (DOMElement) document.getChild(i);
                break;
            }
        }
        return rootElement;
    }

    private static STNode buildTree(DOMElement xmlNode) {

        AbstractFactory factory = null;
        STNode root = null;
        if (xmlNode != null) {
            if (Constant.API.equalsIgnoreCase(xmlNode.getNodeName())) {
                factory = new APIFactory();
            } else if (Constant.ENDPOINT.equalsIgnoreCase(xmlNode.getNodeName())) {
                factory = new EndpointFactory();
            } else if (Constant.INBOUND_ENDPOINT.equalsIgnoreCase(xmlNode.getNodeName())) {
                factory = new InboundEndpointFactory();
            } else if (Constant.MESSAGE_PROCESSOR.equalsIgnoreCase(xmlNode.getNodeName())) {
                factory = new MessageProcessorFactory();
            } else if (Constant.LOCAL_ENTRY.equalsIgnoreCase(xmlNode.getNodeName())) {
                factory = new LocalEntryFactory();
            } else if (Constant.MESSAGE_STORE.equalsIgnoreCase(xmlNode.getNodeName())) {
                factory = new MessageStoreFactory();
            } else if (Constant.PROXY.equalsIgnoreCase(xmlNode.getNodeName())) {
                factory = new ProxyFactory();
            } else if (Constant.SEQUENCE.equalsIgnoreCase(xmlNode.getNodeName())) {
                factory = new NamedSequenceFactory();
            } else if (Constant.TASK.equalsIgnoreCase(xmlNode.getNodeName())) {
                factory = new TaskFactory();
            } else if (Constant.TEMPLATE.equalsIgnoreCase(xmlNode.getNodeName())) {
                factory = new TemplateFactory();
            } else if (Constant.WSDL_DEFINITIONS.equalsIgnoreCase(xmlNode.getNodeName())) {
                factory = new Wsdl11Factory();
            } else if (Constant.WSDL_DESCRIPTION.equalsIgnoreCase(xmlNode.getNodeName())) {
                factory = new Wsdl20Factory();
            } else if (Constant.DATA.equalsIgnoreCase(xmlNode.getNodeName())) {
                factory = new DataServiceConfigFactory();
            } else if (Constant.DATA_SOURCE.equalsIgnoreCase(xmlNode.getNodeName())) {
                factory = new DataSourceConfigFactory();
            }
        }

        if (factory != null) {
            root = factory.create(xmlNode);
        }
        return root;
    }

    public static String getProjectPath() {

        return projectPath;
    }
}
