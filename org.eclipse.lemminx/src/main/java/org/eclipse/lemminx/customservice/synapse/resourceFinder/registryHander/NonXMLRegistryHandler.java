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

import org.eclipse.lemminx.customservice.synapse.resourceFinder.pojo.RegistryResource;
import org.eclipse.lemminx.customservice.synapse.resourceFinder.pojo.Resource;
import org.eclipse.lemminx.customservice.synapse.utils.Constant;
import org.eclipse.lemminx.customservice.synapse.utils.Utils;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class NonXMLRegistryHandler {

    private static final Logger LOGGER = Logger.getLogger(NonXMLRegistryHandler.class.getName());
    private NonXMLRegistryHandler nextHandler;
    List<Resource> resources;

    public NonXMLRegistryHandler(List<Resource> resources) {

        this.resources = resources;
    }

    public void handleFile(File file) {

        if (canHandle(file)) {
            Resource resource = createNonXmlResource(file);
            resources.add(resource);
        } else {
            if (nextHandler != null) {
                nextHandler.handleFile(file);
            }
        }
    }

    protected abstract boolean canHandle(File file);

    protected String formatKey(String key){
        return key;
    }
    private Resource createNonXmlResource(File file) {

        RegistryResource resource = new RegistryResource();
        resource.setName(file.getName());
        resource.setType(Utils.getFileExtension(file));
        resource.setRegistryPath(file.getAbsolutePath());
        if (Utils.isFileInRegistry(file)) {
            resource.setFrom(Constant.REGISTRY);
            resource.setRegistryKey(formatKey(Utils.getRegistryKey(file)));
        } else {
            resource.setFrom(Constant.RESOURCES);
            resource.setRegistryKey(formatKey(Utils.getResourceKey(file)));
        }
        return resource;
    }

    public NonXMLRegistryHandler getNextHandler() {

        return nextHandler;
    }

    public void setNextHandler(NonXMLRegistryHandler nextHandler) {

        this.nextHandler = nextHandler;
    }

    public List<Resource> getResources() {

        return resources;
    }

    public void setResources(List<Resource> resources) {

        this.resources = resources;
    }
}
