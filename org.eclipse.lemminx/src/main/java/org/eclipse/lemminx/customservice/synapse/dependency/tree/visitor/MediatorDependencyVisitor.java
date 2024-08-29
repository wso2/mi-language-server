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

import org.eclipse.lemminx.customservice.synapse.debugger.visitor.AbstractMediatorVisitor;
import org.eclipse.lemminx.customservice.synapse.dependency.tree.ArtifactType;
import org.eclipse.lemminx.customservice.synapse.dependency.tree.DependencyLookUp;
import org.eclipse.lemminx.customservice.synapse.dependency.tree.DependencyVisitorUtils;
import org.eclipse.lemminx.customservice.synapse.dependency.tree.pojo.Dependency;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.connector.Connector;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.endpoint.NamedEndpoint;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.Mediator;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.SequenceMediator;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.advanced.Clone.Clone;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.advanced.Clone.CloneTarget;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.advanced.DataServiceCall.DataServiceCall;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.advanced.DbMediator.DbMediator;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.advanced.Enqueue;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.advanced.Event;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.advanced.Transaction;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.advanced.cache.Cache;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.CallTemplate;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.Drop;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.Header;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.Log;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.Loopback;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.Property;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.PropertyGroup;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.Respond;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.Send;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.Store;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.call.Call;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.callout.Callout;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.callout.CalloutEnableSec;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.validate.Validate;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.validate.ValidateResource;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.validate.ValidateSchema;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.eip.Foreach;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.eip.Iterate;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.eip.aggregate.Aggregate;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.eip.aggregate.AggregateOnComplete;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.extension.Bean;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.extension.Class;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.extension.Script;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.extension.Spring;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.extension.command.PojoCommand;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.extension.ejb.Ejb;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.filter.condRouter.ConditionalRouter;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.filter.condRouter.ConditionalRouterConditionalRoute;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.filter.filter.Filter;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.filter.switchMediator.Switch;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.filter.switchMediator.SwitchCase;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.filter.throttle.Throttle;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.other.OauthService;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.other.bam.Bam;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.other.builder.Builder;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.other.entitlement.EntitlementService;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.other.ntlm.Ntlm;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.other.publishEvent.PublishEvent;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.other.rule.Rule;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.transformation.Datamapper;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.transformation.FastXSLT;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.transformation.Jsontransform;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.transformation.enrich.Enrich;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.transformation.fault.Makefault;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.transformation.payload.PayloadFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.transformation.rewrite.Rewrite;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.transformation.smooks.Smooks;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.transformation.xquery.Xquery;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.transformation.xquery.XqueryVariable;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.transformation.xslt.Xslt;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.transformation.xslt.XsltResource;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.misc.common.Sequence;

import java.util.ArrayList;
import java.util.List;

public class MediatorDependencyVisitor extends AbstractMediatorVisitor {

    String projectPath;
    List<Dependency> dependencies;
    DependencyLookUp dependencyLookUp;

    public MediatorDependencyVisitor(String projectPath, DependencyLookUp dependencyLookUp) {

        this.projectPath = projectPath;
        this.dependencyLookUp = dependencyLookUp;
        this.dependencies = new ArrayList<>();
    }

    private void addSimpleDependency(String name, String from, ArtifactType type) {

        String path = DependencyVisitorUtils.getDependencyPath(name, from, projectPath);
        Dependency dependency = new Dependency(name, type, path);
        dependencies.add(dependency);
    }

    private void addSequenceDependency(String name) {

        Dependency dependency = DependencyVisitorUtils.visitSequence(projectPath, name, dependencyLookUp);
        if (dependency != null) {
            dependencies.add(dependency);
        }
    }

    private void addEndpointDependency(NamedEndpoint endpoint) {

        Dependency dependency = DependencyVisitorUtils.visitEndpoint(endpoint, projectPath, dependencyLookUp);
        if (dependency != null) {
            dependencies.add(dependency);
        }
    }

    private void addEndpointDependency(String endpoint) {

        NamedEndpoint endpointObj = new NamedEndpoint();
        endpointObj.setName(endpoint);
        Dependency dependency = DependencyVisitorUtils.visitEndpoint(endpointObj, projectPath, dependencyLookUp);
        if (dependency != null) {
            dependencies.add(dependency);
        }
    }

    public List<Dependency> getDependencies() {

        return dependencies;
    }

    @Override
    protected void visitConnector(Connector node) {

    }

    @Override
    protected void visitPropertyGroup(PropertyGroup node) {

        // DO NOTHING
    }

    @Override
    protected void visitJsontransform(Jsontransform node) {

        if (node.getSchema() != null) {
            addSimpleDependency(node.getSchema(), ArtifactType.JSON_SCHEMA.name(), ArtifactType.JSON_SCHEMA);
        }
    }

    @Override
    protected void visitMakefault(Makefault node) {

        // DO NOTHING

    }

    @Override
    protected void visitSmooks(Smooks node) {

        if (node.getConfigKey() != null) {
            addSimpleDependency(node.getConfigKey(), ArtifactType.SMOOKS_CONFIG.name(), ArtifactType.SMOOKS_CONFIG);
        }
    }

    @Override
    protected void visitHeader(Header node) {

        // DO NOTHING
    }

    @Override
    protected void visitDataServiceCall(DataServiceCall node) {

        if (node.getServiceName() != null) {
            addSimpleDependency(node.getServiceName(), "data-services", ArtifactType.DATA_SERVICE);
        }
    }

    @Override
    protected void visitTransaction(Transaction node) {

        // DO NOTHING
    }

    @Override
    protected void visitDatamapper(Datamapper node) {

        if (node.getInputSchema() != null) {
            addSimpleDependency(node.getInputSchema(), ArtifactType.DATA_MAPPER_INPUT_SCHEMA.name(),
                    ArtifactType.DATA_MAPPER_INPUT_SCHEMA);
        }
        if (node.getOutputSchema() != null) {
            addSimpleDependency(node.getOutputSchema(), ArtifactType.DATA_MAPPER_OUTPUT_SCHEMA.name(),
                    ArtifactType.DATA_MAPPER_OUTPUT_SCHEMA);
        }
        if (node.getConfig() != null) {
            addSimpleDependency(node.getConfig(), ArtifactType.DATA_MAPPER_CONFIG.name(),
                    ArtifactType.DATA_MAPPER_CONFIG);
        }
        if (node.getXsltStyleSheet() != null) {
            addSimpleDependency(node.getXsltStyleSheet(), ArtifactType.XSLT.name(), ArtifactType.XSLT);
        }
    }

    @Override
    protected void visitPojoCommand(PojoCommand node) {

        // DO NOTHING
    }

    @Override
    protected void visitPayloadFactory(PayloadFactory node) {

        // DO NOTHING
    }

    @Override
    protected void visitEntitlementService(EntitlementService node) {

        addAnonymousOrAttributeSequence(node.getOnAccept(), node.getOnAcceptAttribute());
        addAnonymousOrAttributeSequence(node.getOnReject(), node.getOnRejectAttribute());
        addAnonymousOrAttributeSequence(node.getAdvice(), node.getAdviceAttribute());
        addAnonymousOrAttributeSequence(node.getObligations(), node.getObligationsAttribute());
    }

    private void addAnonymousOrAttributeEndpoint(NamedEndpoint endpoint, String attribute) {

        if (endpoint != null) {
            Dependency dependency = DependencyVisitorUtils.visitEndpoint(endpoint, projectPath, dependencyLookUp);
            if (dependency != null) {
                dependencies.add(dependency);
            }
        } else if (attribute != null) {
            addEndpointDependency(attribute);
        }
    }

    private void addAnonymousOrAttributeSequence(Sequence sequence, String attribute) {

        if (sequence != null) {
            List<Dependency> dependencyList = DependencyVisitorUtils.visitAnonymousSequence(sequence, projectPath,
                    dependencyLookUp);
            dependencies.addAll(dependencyList);
        } else if (attribute != null) {
            addSequenceDependency(attribute);
        }
    }

    private void addAnonymousOrAttributeSequence(List<Mediator> sequence, String attribute) {

        if (sequence != null) {
            List<Dependency> dependencyList = DependencyVisitorUtils.visitMediators(sequence, projectPath,
                    dependencyLookUp);
            dependencies.addAll(dependencyList);
        } else if (attribute != null) {
            addSequenceDependency(attribute);
        }
    }

    @Override
    protected void visitThrottle(Throttle node) {

        addAnonymousOrAttributeSequence(node.getOnAccept(), node.getOnAcceptAttribute());
        addAnonymousOrAttributeSequence(node.getOnReject(), node.getOnRejectAttribute());
        if (node.getPolicy() != null && node.getPolicy().getKey() != null) {
            addSimpleDependency(node.getPolicy().getKey(), ArtifactType.THROTTLE_POLICY.name(),
                    ArtifactType.THROTTLE_POLICY);
        }
    }

    @Override
    protected void visitCache(Cache node) {

        addAnonymousOrAttributeSequence(node.getOnCacheHit().getMediatorList(), node.getOnCacheHit().getSequence());
    }

    @Override
    protected void visitEnqueue(Enqueue node) {

        if (node.getSequence() != null) {
            addSequenceDependency(node.getSequence());
        }
    }

    @Override
    protected void visitDbreport(DbMediator node) {

        if (node.getConnection() != null && node.getConnection().getPool() != null &&
                node.getConnection().getPool().getDsName() != null &&
                node.getConnection().getPool().getDsName().getValue() != null) {
            addSimpleDependency(node.getConnection().getPool().getDsName().getValue(), ArtifactType.DATASOURCE.name()
                    , ArtifactType.DATASOURCE);
        }
    }

    @Override
    protected void visitDblookup(DbMediator node) {

        if (node.getConnection() != null && node.getConnection().getPool() != null &&
                node.getConnection().getPool().getDsName() != null &&
                node.getConnection().getPool().getDsName().getValue() != null) {
            addSimpleDependency(node.getConnection().getPool().getDsName().getValue(), ArtifactType.DATASOURCE.name()
                    , ArtifactType.DATASOURCE);
        }
    }

    @Override
    protected void visitEvent(Event node) {

        // DO NOTHING
    }

    @Override
    protected void visitRespond(Respond node) {

        // DO NOTHING
    }

    @Override
    protected void visitPublishEvent(PublishEvent node) {

        // DO NOTHING
    }

    @Override
    protected void visitSwitch(Switch node) {

        if (node.get_default() != null) {
            addAnonymousOrAttributeSequence(node.get_default().getMediatorList(), null);
        }
        SwitchCase[] cases = node.get_case();
        if (cases != null) {
            for (SwitchCase _case : cases) {
                addAnonymousOrAttributeSequence(_case.getMediatorList(), null);
            }
        }
    }

    @Override
    protected void visitSpring(Spring node) {
        // DO NOTHING
    }

    @Override
    protected void visitRule(Rule node) {

        // DO NOTHING
    }

    @Override
    protected void visitConditionalRouter(ConditionalRouter node) {

        ConditionalRouterConditionalRoute[] routes = node.getConditionalRoute();
        if (routes != null) {
            for (ConditionalRouterConditionalRoute route : routes) {
                if (route.getTarget() != null && route.getTarget().getSequenceAttribute() != null) {
                    addSequenceDependency(route.getTarget().getSequenceAttribute());
                }
            }
        }
    }

    @Override
    protected void visitLoopback(Loopback node) {

        // DO NOTHING
    }

    @Override
    protected void visitStore(Store node) {

        if (node.getMessageStore() != null) {
            Dependency dependency = DependencyVisitorUtils.visitMessageStore(node.getMessageStore(), projectPath,
                    dependencyLookUp);
            if (dependency != null) {
                dependencies.add(dependency);
            }
        }
        if (node.getSequence() != null) {
            addSequenceDependency(node.getSequence());
        }
    }

    @Override
    protected void visitValidate(Validate node) {

        ValidateResource[] resources = node.getResource();
        if (resources != null) {
            for (ValidateResource resource : resources) {
                if (resource.getKey() != null) {
                    addSimpleDependency(resource.getKey(), ArtifactType.VALIDATE_RESOURCE.name(),
                            ArtifactType.VALIDATE_RESOURCE);
                }
            }
        }

        ValidateSchema[] schemas = node.getSchema();
        if (schemas != null) {
            for (ValidateSchema schema : schemas) {
                if (schema.getKey() != null) {
                    addSimpleDependency(schema.getKey(), ArtifactType.VALIDATE_SCHEMA.name(),
                            ArtifactType.VALIDATE_SCHEMA);
                }
            }
        }

        if (node.getOnFail() != null && node.getOnFail().getMediatorList() != null) {
            addAnonymousOrAttributeSequence(node.getOnFail().getMediatorList(), null);
        }
    }

    @Override
    protected void visitFilter(Filter node) {

        addAnonymousOrAttributeSequence(node.getThen().getMediatorList(), node.getThen().getSequence());
        addAnonymousOrAttributeSequence(node.getElse_().getMediatorList(), node.getElse_().getSequence());
    }

    @Override
    protected void visitSend(Send node) {

        if (node.getEndpoint() != null) {
            addEndpointDependency(node.getEndpoint());
        }
        if (node.getReceive() != null) {
            addSequenceDependency(node.getReceive());
        }
    }

    @Override
    protected void visitClone(Clone node) {

        CloneTarget[] targets = node.getTarget();
        if (targets != null) {
            for (CloneTarget target : targets) {
                if (target.getSequence() != null) {
                    addAnonymousOrAttributeSequence(target.getSequence(), target.getSequenceAttribute());
                }
                if (target.getEndpoint() != null) {
                    addAnonymousOrAttributeEndpoint(target.getEndpoint(), target.getEndpointAttribute());
                }
            }
        }
    }

    @Override
    protected void visitClass(Class node) {

        // DO NOTHING
    }

    @Override
    protected void visitAggregate(Aggregate node) {

        if (node.getCorrelateOnOrCompleteConditionOrOnComplete() != null) {
            AggregateOnComplete onComplete = node.getCorrelateOnOrCompleteConditionOrOnComplete().getOnComplete().get();
            if (onComplete != null) {
                addAnonymousOrAttributeSequence(onComplete.getMediatorList(), onComplete.getSequenceAttribute());
            }
        }
    }

    @Override
    protected void visitLog(Log node) {

        // DO NOTHING
    }

    @Override
    protected void visitEjb(Ejb node) {

        // DO NOTHING
    }

    @Override
    protected void visitNTLM(Ntlm node) {

        // DO NOTHING
    }

    @Override
    protected void visitRewrite(Rewrite node) {

        // DO NOTHING
    }

    @Override
    protected void visitCallTemplate(CallTemplate node) {

        if (node.getTarget() != null) {
            addSimpleDependency(node.getTarget(), "templates", ArtifactType.TEMPLATE);
        }
        if (node.getOnError() != null) {
            addSequenceDependency(node.getOnError());
        }
    }

    @Override
    protected void visitCall(Call node) {

        if (node.getEndpoint() != null) {
            addEndpointDependency(node.getEndpoint());
        }
    }

    @Override
    protected void visitIterate(Iterate node) {

        if (node.getTarget() != null) {
            addAnonymousOrAttributeSequence(node.getTarget().getSequence(), node.getTarget().getSequenceAttribute());
        }
    }

    @Override
    protected void visitProperty(Property node) {

        // DO NOTHING
    }

    @Override
    protected void visitForeach(Foreach node) {

        addAnonymousOrAttributeSequence(node.getSequence(), node.getSequenceAttribute());
    }

    @Override
    protected void visitEnrich(Enrich node) {

        // DO NOTHING
    }

    @Override
    protected void visitScript(Script node) {

        if (node.getKey() != null) {
            addSimpleDependency(node.getKey(), ArtifactType.SCRIPT.name(), ArtifactType.SCRIPT);
        }
    }

    @Override
    protected void visitBean(Bean node) {

        // DO NOTHING
    }

    @Override
    protected void visitXquery(Xquery node) {

        if (node.getKey() != null) {
            addSimpleDependency(node.getKey(), ArtifactType.XQUERY.name(), ArtifactType.XQUERY);
        }

        XqueryVariable[] variables = node.getVariable();
        if (variables != null) {
            for (XqueryVariable variable : variables) {
                if (variable.getKey() != null) {
                    addSimpleDependency(variable.getKey(), ArtifactType.XQUERY_VARIABLE.name(),
                            ArtifactType.XQUERY_VARIABLE);
                }
            }
        }
    }

    @Override
    protected void visitBuilder(Builder node) {

        // DO NOTHING
    }

    @Override
    protected void visitDrop(Drop node) {

        // DO NOTHING
    }

    @Override
    protected void visitXslt(Xslt node) {

        if (node.getKey() != null) {
            addSimpleDependency(node.getKey(), ArtifactType.XSLT.name(), ArtifactType.XSLT);
        }

        XsltResource[] resources = node.getResource();
        if (resources != null) {
            for (XsltResource resource : resources) {
                if (resource.getKey() != null) {
                    addSimpleDependency(resource.getKey(), ArtifactType.XSLT_RESOURCE.name(),
                            ArtifactType.XSLT_RESOURCE);
                }
            }
        }
    }

    @Override
    protected void visitBam(Bam node) {

        // DO NOTHING
    }

    @Override
    protected void visitFastXSLT(FastXSLT node) {

        if (node.getKey() != null) {
            addSimpleDependency(node.getKey(), ArtifactType.XSLT.name(), ArtifactType.XSLT);
        }
    }

    @Override
    protected void visitOauthService(OauthService node) {

        // DO NOTHING
    }

    @Override
    protected void visitCallout(Callout node) {

        if (node.getEndpointKey() != null) {
            addEndpointDependency(node.getEndpointKey());
        }

        CalloutEnableSec enableSec = node.getSourceOrTargetOrConfiguration().getEnableSec().get();
        if (enableSec != null) {
            if (enableSec.getPolicy() != null) {
                addSimpleDependency(enableSec.getPolicy(), ArtifactType.POLICY.name(), ArtifactType.POLICY);
            } else {
                if (enableSec.getInboundPolicy() != null) {
                    addSimpleDependency(enableSec.getInboundPolicy(), ArtifactType.POLICY.name(), ArtifactType.POLICY);
                }
                if (enableSec.getOutboundPolicy() != null) {
                    addSimpleDependency(enableSec.getOutboundPolicy(), ArtifactType.POLICY.name(), ArtifactType.POLICY);
                }
            }
        }
    }

    @Override
    protected void visitSequence(SequenceMediator node) {

        if (node.getKey() != null) {
            addSequenceDependency(node.getKey());
        }
    }
}
