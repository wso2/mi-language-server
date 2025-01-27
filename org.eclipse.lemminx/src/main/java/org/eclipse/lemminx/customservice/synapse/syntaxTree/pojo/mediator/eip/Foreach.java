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
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.misc.common.Sequence;

public class Foreach extends Mediator {

    Sequence sequence;
    String expression;
    String sequenceAttribute;
    String id;
    String description;
    // V2 attributes
    boolean executeParallel;
    String resultTarget;
    String resultType;
    String collection;
    String counterVariableName;
    boolean continueWithoutAggregation;

    public Foreach() {
        setDisplayName("Foreach");
    }

    public Sequence getSequence() {

        return sequence;
    }

    public void setSequence(Sequence sequence) {

        this.sequence = sequence;
    }

    public String getExpression() {

        return expression;
    }

    public void setExpression(String expression) {

        this.expression = expression;
    }

    public String getSequenceAttribute() {

        return sequenceAttribute;
    }

    public void setSequenceAttribute(String sequenceAttribute) {

        this.sequenceAttribute = sequenceAttribute;
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

    public boolean isExecuteParallel() {

        return executeParallel;
    }

    public void setExecuteParallel(boolean executeParallel) {

        this.executeParallel = executeParallel;
    }

    public String getResultTarget() {

        return resultTarget;
    }

    public void setResultTarget(String resultTarget) {

        this.resultTarget = resultTarget;
    }

    public String getResultType() {

        return resultType;
    }

    public void setResultType(String resultType) {

        this.resultType = resultType;
    }

    public String getCollection() {

        return collection;
    }

    public void setCollection(String collection) {

        this.collection = collection;
    }

    public String getCounterVariableName() {

        return counterVariableName;
    }

    public void setCounterVariableName(String counterVariableName) {

        this.counterVariableName = counterVariableName;
    }

    public boolean isContinueWithoutAggregation() {

        return continueWithoutAggregation;
    }

    public void setContinueWithoutAggregation(boolean continueWithoutAggregation) {

        this.continueWithoutAggregation = continueWithoutAggregation;
    }
}
