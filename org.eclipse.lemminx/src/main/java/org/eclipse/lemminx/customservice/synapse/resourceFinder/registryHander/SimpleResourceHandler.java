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

package org.eclipse.lemminx.customservice.synapse.resourceFinder.registryHander;

import org.eclipse.lemminx.customservice.synapse.resourceFinder.pojo.RequestedResource;
import org.eclipse.lemminx.customservice.synapse.resourceFinder.pojo.Resource;
import org.eclipse.lemminx.customservice.synapse.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleResourceHandler extends NonXMLRegistryHandler {

    private List<String> requestedResources;

    private static final Map<String, String> nonXmlTypeToExtensionMap = new HashMap<>();

    static {
        nonXmlTypeToExtensionMap.put("dataMapper", "dmc");
        nonXmlTypeToExtensionMap.put("js", "js");
        nonXmlTypeToExtensionMap.put("json", "json");
        nonXmlTypeToExtensionMap.put("wsdl", "wsdl");
        nonXmlTypeToExtensionMap.put("xsd", "xsd");
        nonXmlTypeToExtensionMap.put("xsl", "xsl");
        nonXmlTypeToExtensionMap.put("xslt", "xslt");
        nonXmlTypeToExtensionMap.put("yaml", "yaml");
    }

    public SimpleResourceHandler(List<RequestedResource> requestedResources, List<Resource> resources) {

        super(resources);
        this.requestedResources = new ArrayList<>();
        for (RequestedResource requestedResource : requestedResources) {
            this.requestedResources.add(nonXmlTypeToExtensionMap.get(requestedResource.getType()));
        }
    }

    @Override
    protected boolean canHandle(File file) {

        return requestedResources.contains(Utils.getFileExtension(file));
    }
}
