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

package org.eclipse.lemminx.customservice.synapse.dependency.tree.visitor;

import org.eclipse.lemminx.customservice.synapse.dependency.tree.ArtifactType;
import org.eclipse.lemminx.customservice.synapse.dependency.tree.DependencyLookUp;
import org.eclipse.lemminx.customservice.synapse.dependency.tree.DependencyVisitorUtils;
import org.eclipse.lemminx.customservice.synapse.dependency.tree.pojo.Dependency;
import org.eclipse.lemminx.customservice.synapse.dependency.tree.pojo.DependencyTree;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.STNode;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.endpoint.ChildEndpoint;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.endpoint.DefaultEndpoint;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.endpoint.EndpointAddress;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.endpoint.EndpointType;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.endpoint.NamedEndpoint;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.endpoint.common.EndpointEnableSec;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.endpoint.failover.EndpointFailover;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.endpoint.failover.EndpointFailoverEndpoint;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.endpoint.http.EnableSecAndEnableRMAndEnableAddressing;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.endpoint.http.EndpointHttp;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.endpoint.loadbalance.EndpointLoadbalance;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.endpoint.loadbalance.EndpointOrMember;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.endpoint.recipientList.EndpointRecipientlist;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.endpoint.recipientList.EndpointRecipientlistEndpoint;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.endpoint.wsdl.WSDLEndpoint;

public class EndpointVisitor extends AbstractDependencyVisitor {

    public EndpointVisitor(String projectPath, DependencyLookUp dependencyLookUp) {

        super(new DependencyTree(), projectPath, dependencyLookUp);
    }

    public EndpointVisitor(DependencyTree dependencyTree, String projectPath) {

        super(dependencyTree, projectPath, new DependencyLookUp());
    }

    @Override
    public void visit(STNode node) {

        NamedEndpoint endpoint = (NamedEndpoint) node;
        switch (endpoint.getType()) {
            case HTTP_ENDPOINT:
                EndpointHttp httpEndpoint = endpoint.getHttp();
                if (httpEndpoint != null) {
                    EnableSecAndEnableRMAndEnableAddressing configs =
                            httpEndpoint.getEnableSecAndEnableRMAndEnableAddressing();
                    if (configs != null) {
                        if (configs.getEnableSec() != null && configs.getEnableSec().isPresent()) {
                            EndpointEnableSec enableSec = configs.getEnableSec().get();
                            visitEnableSec(enableSec);
                        }
                    }
                }
                break;
            case ADDRESS_ENDPOINT:
                EndpointAddress addressEndpoint = endpoint.getAddress();
                if (addressEndpoint != null) {
                    EndpointEnableSec enableSec = addressEndpoint.getEnableSec();
                    if (enableSec != null) {
                        visitEnableSec(enableSec);
                    }
                }
                break;
            case DEFAULT_ENDPOINT:
                DefaultEndpoint defaultEndpoint = endpoint.get_default();
                if (defaultEndpoint != null) {
                    EndpointEnableSec enableSec = defaultEndpoint.getEnableSec();
                    if (enableSec != null) {
                        visitEnableSec(enableSec);
                    }
                }
                break;
            case FAIL_OVER_ENDPOINT:
                EndpointFailover failOver = endpoint.getFailover();
                if (failOver != null) {
                    EndpointFailoverEndpoint[] endpoints = failOver.getEndpoint();
                    if (endpoints != null) {
                        for (EndpointFailoverEndpoint childEndpoint : endpoints) {
                            visitChildEndpoint(childEndpoint);
                        }
                    }
                }
                break;
            case LOAD_BALANCE_ENDPOINT:
                EndpointLoadbalance loadBalance = endpoint.getLoadbalance();
                if (loadBalance != null) {
                    EndpointOrMember[] endpointOrMembers = loadBalance.getEndpointOrMember();
                    if (endpointOrMembers != null) {
                        for (EndpointOrMember endpointOrMember : endpointOrMembers) {
                            if (endpointOrMember.isEndpoint()) {
                                visitChildEndpoint(endpointOrMember.getEndpoint().get());
                            }
                        }
                    }
                }
                break;
            case RECIPIENT_LIST_ENDPOINT:
                EndpointRecipientlist recipientListEp = endpoint.getRecipientlist();
                if (recipientListEp != null) {
                    EndpointRecipientlistEndpoint[] childEndpoints = recipientListEp.getEndpoint();
                    if (childEndpoints != null) {
                        for (EndpointRecipientlistEndpoint childEndpoint : childEndpoints) {
                            visitChildEndpoint(childEndpoint);
                        }
                    }
                }
                break;
            case TEMPLATE_ENDPOINT:
                String template = endpoint.getTemplate();
                if (template != null) {
                    addDependency(DependencyVisitorUtils.visitTemplate(projectPath, template, dependencyLookUp));
                }
                break;
            case WSDL_ENDPOINT:
                WSDLEndpoint wsdlEndpoint = endpoint.getWsdl();
                if (wsdlEndpoint != null) {
                    EndpointEnableSec enableSec = wsdlEndpoint.getEnableSec();
                    if (enableSec != null) {
                        visitEnableSec(enableSec);
                    }
                }
                break;
        }
    }

    private void visitChildEndpoint(ChildEndpoint childEndpoint) {

        NamedEndpoint endpoint = new NamedEndpoint();
        if (endpoint.get_default() != null) {
            endpoint.set_default(childEndpoint.get_default());
            endpoint.setType(EndpointType.DEFAULT_ENDPOINT);
        } else if (childEndpoint.getHttp() != null) {
            endpoint.setHttp(childEndpoint.getHttp());
            endpoint.setType(EndpointType.HTTP_ENDPOINT);
        } else if (childEndpoint.getAddress() != null) {
            endpoint.setAddress(childEndpoint.getAddress());
            endpoint.setType(EndpointType.ADDRESS_ENDPOINT);
        } else if (childEndpoint.getWsdl() != null) {
            endpoint.setWsdl(childEndpoint.getWsdl());
            endpoint.setType(EndpointType.WSDL_ENDPOINT);
        } else if (childEndpoint.getLoadbalance() != null) {
            endpoint.setLoadbalance(childEndpoint.getLoadbalance());
            endpoint.setType(EndpointType.LOAD_BALANCE_ENDPOINT);
        } else if (childEndpoint.getFailover() != null) {
            endpoint.setFailover(childEndpoint.getFailover());
            endpoint.setType(EndpointType.FAIL_OVER_ENDPOINT);
        }
        visit(endpoint);
    }

    private void visitEnableSec(EndpointEnableSec enableSec) {

        String policy = enableSec.getPolicy();
        if (policy != null) {
            addPolicyDependency(policy);
        }
        String inboundPolicy = enableSec.getInboundPolicy();
        if (inboundPolicy != null) {
            addPolicyDependency(inboundPolicy);
        }
        String outboundPolicy = enableSec.getOutboundPolicy();
        if (outboundPolicy != null) {
            addPolicyDependency(outboundPolicy);
        }
    }

    private void addPolicyDependency(String policyKey) {

        String policyPath = DependencyVisitorUtils.getDependencyPath(policyKey, ArtifactType.POLICY.name(),
                projectPath);
        addDependency(new Dependency(policyKey, ArtifactType.POLICY, policyPath));
    }
}
