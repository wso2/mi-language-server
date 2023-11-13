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

package org.eclipse.lemminx.customservice.syntaxmodel.pojo.mediator.core.callout;

import org.eclipse.lemminx.customservice.syntaxmodel.pojo.STNode;

import java.util.Optional;

public class SourceOrTargetOrConfiguration extends STNode {

    Optional<CalloutSource> source;
    Optional<CalloutTarget> target;
    Optional<CalloutConfiguration> configuration;
    Optional<CalloutEnableSec> enableSec;

    public Optional<CalloutSource> getSource() {

        return source;
    }

    public void setSource(Optional<CalloutSource> source) {

        this.source = source;
    }

    public Optional<CalloutTarget> getTarget() {

        return target;
    }

    public void setTarget(Optional<CalloutTarget> target) {

        this.target = target;
    }

    public Optional<CalloutConfiguration> getConfiguration() {

        return configuration;
    }

    public void setConfiguration(Optional<CalloutConfiguration> configuration) {

        this.configuration = configuration;
    }

    public Optional<CalloutEnableSec> getEnableSec() {

        return enableSec;
    }

    public void setEnableSec(Optional<CalloutEnableSec> enableSec) {

        this.enableSec = enableSec;
    }
}