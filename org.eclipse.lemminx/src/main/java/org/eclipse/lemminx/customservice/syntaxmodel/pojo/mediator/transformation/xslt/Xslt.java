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

package org.eclipse.lemminx.customservice.syntaxmodel.pojo.mediator.transformation.xslt;

import org.eclipse.lemminx.customservice.syntaxmodel.pojo.mediator.Mediator;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.mediator.core.MediatorProperty;

public class Xslt extends Mediator {

    MediatorProperty[] property;
    XsltFeature[] feature;
    XsltResource[] resource;
    String key;
    String source;
    String description;

    public MediatorProperty[] getProperty() {

        return property;
    }

    public void setProperty(MediatorProperty[] property) {

        this.property = property;
    }

    public XsltFeature[] getFeature() {

        return feature;
    }

    public void setFeature(XsltFeature[] feature) {

        this.feature = feature;
    }

    public XsltResource[] getResource() {

        return resource;
    }

    public void setResource(XsltResource[] resource) {

        this.resource = resource;
    }

    public String getKey() {

        return key;
    }

    public void setKey(String key) {

        this.key = key;
    }

    public String getSource() {

        return source;
    }

    public void setSource(String source) {

        this.source = source;
    }

    public String getDescription() {

        return description;
    }

    public void setDescription(String description) {

        this.description = description;
    }
}