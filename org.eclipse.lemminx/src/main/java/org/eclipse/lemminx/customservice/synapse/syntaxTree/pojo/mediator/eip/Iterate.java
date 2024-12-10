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

package org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.eip;

import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.Mediator;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.misc.targets.Target;

public class Iterate extends Mediator {

    Target target;
    boolean sequential;
    boolean continueParent;
    String expression;
    boolean preservePayload;
    String attachPath;
    String id;
    String description;

    public Iterate() {
        setDisplayName("Iterate");
    }

    public Target getTarget() {

        return target;
    }

    public void setTarget(Target target) {

        this.target = target;
    }

    public boolean isSequential() {

        return sequential;
    }

    public void setSequential(boolean sequential) {

        this.sequential = sequential;
    }

    public boolean isContinueParent() {

        return continueParent;
    }

    public void setContinueParent(boolean continueParent) {

        this.continueParent = continueParent;
    }

    public String getExpression() {

        return expression;
    }

    public void setExpression(String expression) {

        this.expression = expression;
    }

    public boolean isPreservePayload() {

        return preservePayload;
    }

    public void setPreservePayload(boolean preservePayload) {

        this.preservePayload = preservePayload;
    }

    public String getAttachPath() {

        return attachPath;
    }

    public void setAttachPath(String attachPath) {

        this.attachPath = attachPath;
    }

    public String getId() {

        return id;
    }

    public void setId(String id) {

        this.id = id;
    }

    public String getDescription() {

        return description;
    }

    public void setDescription(String description) {

        this.description = description;
    }
}
