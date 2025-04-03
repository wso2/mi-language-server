/*
 *   Copyright (c) 2025, WSO2 LLC. (http://www.wso2.com).
 *
 *   WSO2 LLC. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 */

package org.eclipse.lemminx.customservice.synapse.mediator.schema.generate.visitor;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.lemminx.customservice.synapse.mediator.tryout.pojo.MediatorTryoutInfo;
import org.eclipse.lemminx.customservice.synapse.mediator.tryout.pojo.MediatorTryoutRequest;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.STNode;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.inbound.InboundEndpoint;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InboundEndpointVisitor implements SchemaVisitor {

    private static final Logger LOGGER = Logger.getLogger(InboundEndpointVisitor.class.getName());
    private String projectPath;

    public InboundEndpointVisitor(String projectPath) {

        this.projectPath = projectPath;
    }

    @Override
    public void visit(STNode node, MediatorTryoutInfo info, MediatorTryoutRequest request) {

        String sequence = ((InboundEndpoint) node).getSequence();
        if (StringUtils.isEmpty(sequence)) {
            return;
        }
        try {
            Utils.visitSequenceByKey(sequence, projectPath, info, request);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, String.format("Error occurred while visiting the sequence: %s", sequence), e);
        }
    }
}
