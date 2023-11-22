/*
 * Copyright (c) 2023, WSO2 LLC. (http://www.wso2.com).
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

package org.eclipse.lemminx.customservice.syntaxmodel.pojo.proxy;

import org.eclipse.lemminx.customservice.syntaxmodel.pojo.STNode;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.misc.wsdl11.TDefinitions;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.misc.wsdl20.DescriptionType;

public class ProxyPublishWSDL extends STNode {

    TDefinitions definitions;
    DescriptionType description;
    String inlineWsdl;
    Resource[] resource;
    String uri;
    String key;
    String endpoint;
    boolean preservePolicy;

    public String getInlineWsdl() {

        return inlineWsdl;
    }

    public void setInlineWsdl(String inlineWsdl) {

        this.inlineWsdl = inlineWsdl;
    }

    public TDefinitions getDefinitions() {

        return definitions;
    }

    public void setDefinitions(TDefinitions definitions) {

        this.definitions = definitions;
    }

    public DescriptionType getDescription() {

        return description;
    }

    public void setDescription(DescriptionType description) {

        this.description = description;
    }

    public Resource[] getResource() {

        return resource;
    }

    public void setResource(Resource[] resource) {

        this.resource = resource;
    }

    public String getUri() {

        return uri;
    }

    public void setUri(String uri) {

        this.uri = uri;
    }

    public String getKey() {

        return key;
    }

    public void setKey(String key) {

        this.key = key;
    }

    public String getEndpoint() {

        return endpoint;
    }

    public void setEndpoint(String endpoint) {

        this.endpoint = endpoint;
    }

    public boolean isPreservePolicy() {

        return preservePolicy;
    }

    public void setPreservePolicy(boolean preservePolicy) {

        this.preservePolicy = preservePolicy;
    }
}
