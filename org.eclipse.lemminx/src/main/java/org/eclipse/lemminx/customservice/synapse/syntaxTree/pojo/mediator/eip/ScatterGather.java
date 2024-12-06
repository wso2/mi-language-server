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

package org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.eip;

import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.Mediator;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.advanced.Clone.CloneTarget;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.eip.aggregate.ScatterGatherAggregation;

public class ScatterGather extends Mediator {

    CloneTarget[] targets;
    boolean executeParallel;
    String resultTarget;
    String contentType;
    String rootElement;
    String description;
    ScatterGatherAggregation scatterGatherAggregation;

    public ScatterGatherAggregation getScatterGatherAggregation() {

        return scatterGatherAggregation;
    }

    public void setScatterGatherAggregation(ScatterGatherAggregation scatterGatherAggregation) {

        this.scatterGatherAggregation = scatterGatherAggregation;
    }

    public CloneTarget[] getTargets() {

        return targets;
    }

    public void addTarget(CloneTarget[] targets) {

        this.targets = targets;
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

    public String getContentType() {

        return contentType;
    }

    public void setContentType(String contentType) {

        this.contentType = contentType;
    }

    public void setTargets(CloneTarget[] targets) {

        this.targets = targets;
    }

    public String getRootElement() {

        return rootElement;
    }

    public void setRootElement(String rootElement) {

        this.rootElement = rootElement;
    }
}
