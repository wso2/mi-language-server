/*
 * Copyright (c) 2025, WSO2 LLC. (http://www.wso2.com).
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

package org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.connector.ai;

import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.STNode;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.Mediator;

public class AgentTool extends STNode {

    private String name;
    private String template;
    private String description;
    private Mediator mediator;
    private String templatePath;

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public String getTemplate() {

        return template;
    }

    public void setTemplate(String template) {

        this.template = template;
    }

    public String getDescription() {

        return description;
    }

    public void setDescription(String description) {

        this.description = description;
    }

    public Mediator getMediator() {

        return mediator;
    }

    public void setMediator(Mediator mediator) {

        this.mediator = mediator;
    }

    public String getTemplatePath() {

        return templatePath;
    }

    public void setTemplatePath(String templatePath) {

        this.templatePath = templatePath;
    }
}
