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

package org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.eip.aggregate;

import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.STNode;

public class ScatterGatherAggregation extends STNode {

    String expression;
    String condition;
    String completeTimeout;
    String minMessages;
    String maxMessages;

    public String getExpression() {

        return expression;
    }

    public void setExpression(String expression) {

        this.expression = expression;
    }

    public String getCondition() {

        return condition;
    }

    public void setCondition(String condition) {

        this.condition = condition;
    }

    public String getCompleteTimeout() {

        return completeTimeout;
    }

    public void setCompleteTimeout(String completeTimeout) {

        this.completeTimeout = completeTimeout;
    }

    public String getMinMessages() {

        return minMessages;
    }

    public void setMinMessages(String minMessages) {

        this.minMessages = minMessages;
    }

    public String getMaxMessages() {

        return maxMessages;
    }

    public void setMaxMessages(String maxMessages) {

        this.maxMessages = maxMessages;
    }
}
