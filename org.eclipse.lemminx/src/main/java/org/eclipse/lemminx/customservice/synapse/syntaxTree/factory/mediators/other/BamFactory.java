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

package org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.other;

import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.AbstractMediatorFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.STNode;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.Mediator;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.other.bam.Bam;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.other.bam.BamServerProfile;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.other.bam.BamServerProfileStreamConfig;
import org.eclipse.lemminx.customservice.synapse.utils.Constant;
import org.eclipse.lemminx.dom.DOMElement;
import org.eclipse.lemminx.dom.DOMNode;

import java.util.List;

public class BamFactory extends AbstractMediatorFactory {

    private static final String BAM = "bam";

    @Override
    public Mediator createSpecificMediator(DOMElement element) {

        Bam bam = new Bam();
        bam.elementNode(element);
        populateAttributes(bam, element);
        List<DOMNode> children = element.getChildren();
        if (children != null && !children.isEmpty()) {
            for (DOMNode child : children) {
                if (child.getNodeName().equalsIgnoreCase(Constant.SERVER_PROFILE)) {
                    BamServerProfile serverProfile = createBamServerProfile(child);
                    bam.setServerProfile(serverProfile);
                }
            }
        }
        return bam;
    }

    @Override
    public void populateAttributes(STNode node, DOMElement element) {

        String description = element.getAttribute(Constant.DESCRIPTION);
        if (description != null && !description.isEmpty()) {
            ((Bam) node).setDescription(description);
        }
    }

    private BamServerProfile createBamServerProfile(DOMNode node) {

        BamServerProfile bamServerProfile = new BamServerProfile();
        bamServerProfile.elementNode((DOMElement) node);
        String name = node.getAttribute(Constant.NAME);
        if (name != null && !name.isEmpty()) {
            bamServerProfile.setName(name);
        }
        List<DOMNode> children = node.getChildren();
        if (children != null && !children.isEmpty()) {
            for (DOMNode child : children) {
                if (child.getNodeName().equalsIgnoreCase(Constant.STREAM_CONFIG)) {
                    BamServerProfileStreamConfig streamConfig = createBamServerProfileStreamConfig(child);
                    bamServerProfile.setStreamConfig(streamConfig);
                }
            }
        }
        return bamServerProfile;
    }

    private BamServerProfileStreamConfig createBamServerProfileStreamConfig(DOMNode node) {

        BamServerProfileStreamConfig bamServerProfileStreamConfig = new BamServerProfileStreamConfig();
        bamServerProfileStreamConfig.elementNode((DOMElement) node);
        String name = node.getAttribute(Constant.NAME);
        if (name != null && !name.isEmpty()) {
            bamServerProfileStreamConfig.setName(name);
        }
        String version = node.getAttribute(Constant.VERSION);
        if (version != null && !version.isEmpty()) {
            bamServerProfileStreamConfig.setVersion(version);
        }
        return bamServerProfileStreamConfig;
    }

    @Override
    public String getTagName() {

        return BAM;
    }
}
