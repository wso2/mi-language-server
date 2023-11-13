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

package org.eclipse.lemminx.customservice.syntaxmodel.pojo.endpoint.common;

import org.eclipse.lemminx.customservice.syntaxmodel.pojo.STNode;

public class EndpointSuspendOnFailure extends STNode {

    STNode errorCodes;
    STNode initialDuration;
    STNode progressionFactor;
    STNode maximumDuration;

    public STNode getErrorCodes() {

        return errorCodes;
    }

    public void setErrorCodes(STNode errorCodes) {

        this.errorCodes = errorCodes;
    }

    public STNode getInitialDuration() {

        return initialDuration;
    }

    public void setInitialDuration(STNode initialDuration) {

        this.initialDuration = initialDuration;
    }

    public STNode getProgressionFactor() {

        return progressionFactor;
    }

    public void setProgressionFactor(STNode progressionFactor) {

        this.progressionFactor = progressionFactor;
    }

    public STNode getMaximumDuration() {

        return maximumDuration;
    }

    public void setMaximumDuration(STNode maximumDuration) {

        this.maximumDuration = maximumDuration;
    }
}
