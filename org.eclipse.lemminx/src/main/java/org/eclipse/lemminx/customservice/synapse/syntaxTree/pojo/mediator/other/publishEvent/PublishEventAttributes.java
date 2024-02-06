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

package org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.other.publishEvent;

import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.STNode;

public class PublishEventAttributes extends STNode {

    PublishEventAttributesMeta meta;
    PublishEventAttributesCorrelation correlation;
    PublishEventAttributesPayload payload;
    PublishEventAttributesArbitrary arbitrary;

    public PublishEventAttributesMeta getMeta() {

        return meta;
    }

    public void setMeta(PublishEventAttributesMeta meta) {

        this.meta = meta;
    }

    public PublishEventAttributesCorrelation getCorrelation() {

        return correlation;
    }

    public void setCorrelation(PublishEventAttributesCorrelation correlation) {

        this.correlation = correlation;
    }

    public PublishEventAttributesPayload getPayload() {

        return payload;
    }

    public void setPayload(PublishEventAttributesPayload payload) {

        this.payload = payload;
    }

    public PublishEventAttributesArbitrary getArbitrary() {

        return arbitrary;
    }

    public void setArbitrary(PublishEventAttributesArbitrary arbitrary) {

        this.arbitrary = arbitrary;
    }
}
