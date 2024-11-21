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

package org.eclipse.lemminx.customservice.synapse.mediator.schema.generate.visitor;

import com.google.gson.JsonPrimitive;
import org.eclipse.lemminx.customservice.synapse.debugger.visitor.AbstractMediatorVisitor;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.connector.Connector;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.SequenceMediator;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.advanced.Clone.Clone;
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
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.PropertyScope;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.Respond;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.Send;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.Store;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.call.Call;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.callout.Callout;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.validate.Validate;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.eip.Foreach;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.eip.Iterate;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.eip.aggregate.Aggregate;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.extension.Bean;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.extension.Class;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.extension.Script;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.extension.Spring;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.extension.command.PojoCommand;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.extension.ejb.Ejb;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.filter.condRouter.ConditionalRouter;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.filter.filter.Filter;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.filter.switchMediator.Switch;
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
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.transformation.xslt.Xslt;
import org.eclipse.lemminx.customservice.synapse.mediator.tryout.pojo.MediatorTryoutInfo;
import org.eclipse.lsp4j.Position;

public class MediatorSchemaGenerateVisitor extends AbstractMediatorVisitor {

    MediatorTryoutInfo info;
    Position position;

    public MediatorSchemaGenerateVisitor(MediatorTryoutInfo info, Position position) {

        this.info = info;
        this.position = position;
    }

    //TODO: Finish all the mediators
    @Override
    protected void visitConnector(Connector node) {

        // TODO: get the i/o schema from the connector and set it to the info
    }

    @Override
    protected void visitPropertyGroup(PropertyGroup node) {

        Property[] properties = node.getProperty();
        if (properties != null) {
            for (Property property : properties) {
                visitProperty(property);
            }
        }
    }

    @Override
    protected void visitJsontransform(Jsontransform node) {

        // TODO: replace with the json schema
    }

    @Override
    protected void visitMakefault(Makefault node) {

    }

    @Override
    protected void visitSmooks(Smooks node) {

    }

    @Override
    protected void visitHeader(Header node) {

    }

    @Override
    protected void visitDataServiceCall(DataServiceCall node) {

    }

    @Override
    protected void visitTransaction(Transaction node) {

    }

    @Override
    protected void visitDatamapper(Datamapper node) {

    }

    @Override
    protected void visitPojoCommand(PojoCommand node) {

    }

    @Override
    protected void visitPayloadFactory(PayloadFactory node) {

        String content = (String) node.getFormat().getContent();
        info.setInputPayload(new JsonPrimitive(content));
    }

    @Override
    protected void visitEntitlementService(EntitlementService node) {

    }

    @Override
    protected void visitThrottle(Throttle node) {

    }

    @Override
    protected void visitCache(Cache node) {

    }

    @Override
    protected void visitEnqueue(Enqueue node) {

    }

    @Override
    protected void visitDbreport(DbMediator node) {

    }

    @Override
    protected void visitDblookup(DbMediator node) {

    }

    @Override
    protected void visitEvent(Event node) {

    }

    @Override
    protected void visitRespond(Respond node) {

    }

    @Override
    protected void visitPublishEvent(PublishEvent node) {

    }

    @Override
    protected void visitSwitch(Switch node) {

    }

    @Override
    protected void visitSpring(Spring node) {

    }

    @Override
    protected void visitRule(Rule node) {

    }

    @Override
    protected void visitConditionalRouter(ConditionalRouter node) {

    }

    @Override
    protected void visitLoopback(Loopback node) {

    }

    @Override
    protected void visitStore(Store node) {

    }

    @Override
    protected void visitValidate(Validate node) {

    }

    @Override
    protected void visitFilter(Filter node) {

    }

    @Override
    protected void visitSend(Send node) {

    }

    @Override
    protected void visitClone(Clone node) {

    }

    @Override
    protected void visitClass(Class node) {

    }

    @Override
    protected void visitAggregate(Aggregate node) {

    }

    @Override
    protected void visitLog(Log node) {

    }

    @Override
    protected void visitEjb(Ejb node) {

    }

    @Override
    protected void visitNTLM(Ntlm node) {

    }

    @Override
    protected void visitRewrite(Rewrite node) {

    }

    @Override
    protected void visitCallTemplate(CallTemplate node) {

    }

    @Override
    protected void visitCall(Call node) {

    }

    @Override
    protected void visitIterate(Iterate node) {

    }

    @Override
    protected void visitProperty(Property node) {

        String propertyName = node.getName();
        PropertyScope scope = node.getScope();
        org.eclipse.lemminx.customservice.synapse.mediator.tryout.pojo.Property property =
                new org.eclipse.lemminx.customservice.synapse.mediator.tryout.pojo.Property(propertyName, node.getValue()!=null ? node.getValue() : node.getExpression());
        if (scope != null) {
            switch (scope) {
                case AXIS2:
                    info.addOutputAxis2Properties(property);
                    break;
                case AXIS2_CLIENT:
                    info.addOutputAxis2ClientProperties(property);
                    break;
                case OPERATION:
                    info.addOutputAxis2OperationProperties(property);
                    break;
                case TRANSPORT:
                    info.addOutputAxis2TransportProperties(property);
                    break;
                default:
                    info.addOutputSynapseProperties(property);
            }
        } else {
            info.addOutputSynapseProperties(property);
        }
    }

    @Override
    protected void visitForeach(Foreach node) {
        // Do nothing
    }

    @Override
    protected void visitEnrich(Enrich node) {

    }

    @Override
    protected void visitScript(Script node) {

    }

    @Override
    protected void visitBean(Bean node) {

    }

    @Override
    protected void visitXquery(Xquery node) {

    }

    @Override
    protected void visitBuilder(Builder node) {

    }

    @Override
    protected void visitDrop(Drop node) {

        // Do nothing
    }

    @Override
    protected void visitXslt(Xslt node) {

    }

    @Override
    protected void visitBam(Bam node) {

    }

    @Override
    protected void visitFastXSLT(FastXSLT node) {

    }

    @Override
    protected void visitOauthService(OauthService node) {

    }

    @Override
    protected void visitCallout(Callout node) {

    }

    @Override
    protected void visitSequence(SequenceMediator node) {

    }
}
