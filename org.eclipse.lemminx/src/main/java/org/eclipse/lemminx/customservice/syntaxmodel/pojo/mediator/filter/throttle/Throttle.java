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

package org.eclipse.lemminx.customservice.syntaxmodel.pojo.mediator.filter.throttle;

import org.eclipse.lemminx.customservice.syntaxmodel.pojo.mediator.Mediator;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.misc.common.Sequence;

public class Throttle extends Mediator {

    ThrottlePolicy policy;
    Sequence onAccept;
    Sequence onReject;
    String id;
    String onAcceptAttribute;
    String onRejectAttribute;
    String description;

    public ThrottlePolicy getPolicy() {

        return policy;
    }

    public void setPolicy(ThrottlePolicy policy) {

        this.policy = policy;
    }

    public Sequence getOnAccept() {

        return onAccept;
    }

    public void setOnAccept(Sequence onAccept) {

        this.onAccept = onAccept;
    }

    public Sequence getOnReject() {

        return onReject;
    }

    public void setOnReject(Sequence onReject) {

        this.onReject = onReject;
    }

    public String getId() {

        return id;
    }

    public void setId(String id) {

        this.id = id;
    }

    public String getOnAcceptAttribute() {

        return onAcceptAttribute;
    }

    public void setOnAcceptAttribute(String onAcceptAttribute) {

        this.onAcceptAttribute = onAcceptAttribute;
    }

    public String getOnRejectAttribute() {

        return onRejectAttribute;
    }

    public void setOnRejectAttribute(String onRejectAttribute) {

        this.onRejectAttribute = onRejectAttribute;
    }

    public String getDescription() {

        return description;
    }

    public void setDescription(String description) {

        this.description = description;
    }
}
