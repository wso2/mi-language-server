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

package org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.eip.aggregate;

import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.STNode;

import java.util.Optional;

public class CorrelateOnOrCompleteConditionOrOnComplete extends STNode {

    Optional<AggregateCorrelateOn> correlateOn;
    Optional<AggregateCompleteCondition> completeCondition;
    Optional<AggregateOnComplete> onComplete;

    public CorrelateOnOrCompleteConditionOrOnComplete() {

        correlateOn = Optional.empty();
        completeCondition = Optional.empty();
        onComplete = Optional.empty();
    }

    public Optional<AggregateCorrelateOn> getCorrelateOn() {

        return correlateOn;
    }

    public void setCorrelateOn(Optional<AggregateCorrelateOn> correlateOn) {

        this.correlateOn = correlateOn;
    }

    public Optional<AggregateCompleteCondition> getCompleteCondition() {

        return completeCondition;
    }

    public void setCompleteCondition(Optional<AggregateCompleteCondition> completeCondition) {

        this.completeCondition = completeCondition;
    }

    public Optional<AggregateOnComplete> getOnComplete() {

        return onComplete;
    }

    public void setOnComplete(Optional<AggregateOnComplete> onComplete) {

        this.onComplete = onComplete;
    }
}
