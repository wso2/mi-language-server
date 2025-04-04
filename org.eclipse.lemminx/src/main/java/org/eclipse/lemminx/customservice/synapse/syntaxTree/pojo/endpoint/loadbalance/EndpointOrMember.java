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

package org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.endpoint.loadbalance;

import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.STNode;

import java.util.Optional;

public class EndpointOrMember extends STNode {

    Optional<EndpointLoadbalanceEndpoint> endpoint;
    Optional<EndpointLoadbalanceMember> member;

    public Optional<EndpointLoadbalanceEndpoint> getEndpoint() {

        return endpoint;
    }

    public void setEndpoint(Optional<EndpointLoadbalanceEndpoint> endpoint) {

        this.endpoint = endpoint;
    }

    public Optional<EndpointLoadbalanceMember> getMember() {

        return member;
    }

    public void setMember(Optional<EndpointLoadbalanceMember> member) {

        this.member = member;
    }

    public boolean isEndpoint() {

        if (endpoint != null) {
            return endpoint.isPresent();
        }
        return false;
    }

    public boolean isMember() {

        if (member != null) {
            return member.isPresent();
        }
        return false;
    }
}
