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

package org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.transformation.payload;

import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.Mediator;

public class PayloadFactory extends Mediator {

    PayloadFactoryFormat format;
    PayloadFactoryArgs args;
    MediaType mediaType;
    TemplateType templateType;
    String description;

    public PayloadFactoryFormat getFormat() {

        return format;
    }

    public void setFormat(PayloadFactoryFormat format) {

        this.format = format;
    }

    public PayloadFactoryArgs getArgs() {

        return args;
    }

    public void setArgs(PayloadFactoryArgs args) {

        this.args = args;
    }

    public MediaType getMediaType() {

        return mediaType;
    }

    public void setMediaType(MediaType mediaType) {

        this.mediaType = mediaType;
    }

    public TemplateType getTemplateType() {

        return templateType;
    }

    public void setTemplateType(TemplateType templateType) {

        this.templateType = templateType;
    }

    public String getDescription() {

        return description;
    }

    public void setDescription(String description) {

        this.description = description;
    }
}
