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

package org.eclipse.lemminx.customservice.synapse.debugger.visitor;

import org.eclipse.lemminx.customservice.synapse.debugger.debuginfo.DebugInfo;
import org.eclipse.lemminx.customservice.synapse.debugger.debuginfo.IDebugInfo;
import org.eclipse.lemminx.customservice.synapse.debugger.entity.Breakpoint;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.STNode;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.connector.Connector;
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
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.transformation.Xquery;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.transformation.enrich.Enrich;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.transformation.fault.Makefault;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.transformation.payload.PayloadFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.transformation.rewrite.Rewrite;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.transformation.smooks.Smooks;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.transformation.xslt.Xslt;

public class MediatorVisitor extends AbstractMediatorVisitor {

    Breakpoint breakpoint;
    IDebugInfo debugInfo;
    String mediatorPosition;
    int mediatorCount;
    boolean done; //remove it later

    public MediatorVisitor(Breakpoint breakpoint, IDebugInfo debugInfo) {

        this.breakpoint = breakpoint;
        this.debugInfo = debugInfo;
        this.mediatorCount = 0;
        this.done = false;
    }

    public MediatorVisitor(Breakpoint breakpoint) {

        this.breakpoint = breakpoint;
        this.debugInfo = new DebugInfo();
        this.mediatorCount = 0;
    }

    public boolean isDone() {

        return done;
    }

    void visitSimpleMediator(STNode node) {

        if (VisitorUtils.checkNodeInRange(node, breakpoint)) {
            if (VisitorUtils.checkValidBreakpoint(node, breakpoint)) {
                mediatorPosition = Integer.toString(mediatorCount);
                debugInfo.setMediatorPosition(mediatorPosition);
                this.done = true;
            } else {
                mediatorPosition = null;
                debugInfo.setValid(false);
                debugInfo.setError("Breakpoint is not in the starting tag of the mediator:" + node.getTag());
            }
        } else {
            mediatorCount += 1;
        }
    }

    @Override
    void visitConnector(Connector node) {

        visitSimpleMediator(node);
    }

    @Override
    void visitPropertyGroup(PropertyGroup node) {

        visitSimpleMediator(node);
    }

    @Override
    void visitJsontransform(Jsontransform node) {

        visitSimpleMediator(node);
    }

    @Override
    void visitMakefault(Makefault node) {

        visitSimpleMediator(node);
    }

    @Override
    void visitSmooks(Smooks node) {

        visitSimpleMediator(node);
    }

    @Override
    void visitHeader(Header node) {

        visitSimpleMediator(node);
    }

    @Override
    void visitDataServiceCall(DataServiceCall node) {

        visitSimpleMediator(node);
    }

    @Override
    void visitTransaction(Transaction node) {

        visitSimpleMediator(node);
    }

    @Override
    void visitDatamapper(Datamapper node) {

        visitSimpleMediator(node);
    }

    @Override
    void visitPojoCommand(PojoCommand node) {

        visitSimpleMediator(node);
    }

    @Override
    void visitPayloadFactory(PayloadFactory node) {

        visitSimpleMediator(node);
    }

    @Override
    void visitEntitlementService(EntitlementService node) {

        if (VisitorUtils.checkNodeInRange(node, breakpoint)) {
            mediatorPosition = Integer.toString(mediatorCount);
            this.done = true;
            if (VisitorUtils.checkValidBreakpoint(node, breakpoint)) {
                debugInfo.setMediatorPosition(mediatorPosition);
            } else {
                MediatorVisitor visitor = new MediatorVisitor(breakpoint);
                if (VisitorUtils.checkNodeInRange(node.getOnAccept(), breakpoint)) {
                    mediatorPosition += " " + "0";
                    VisitorUtils.visitMediators(node.getOnAccept().getMediatorList(), visitor);
                } else if (VisitorUtils.checkNodeInRange(node.getOnReject(), breakpoint)) {
                    mediatorPosition += " " + "1";
                    VisitorUtils.visitMediators(node.getOnReject().getMediatorList(), visitor);
                } else if (VisitorUtils.checkNodeInRange(node.getAdvice(), breakpoint)) {
                    mediatorPosition += " " + "2";
                    VisitorUtils.visitMediators(node.getAdvice().getMediatorList(), visitor);
                } else if (VisitorUtils.checkNodeInRange(node.getObligations(), breakpoint)) {
                    mediatorPosition += " " + "3";
                    VisitorUtils.visitMediators(node.getObligations().getMediatorList(), visitor);
                }
                if (visitor.mediatorPosition != null) {
                    mediatorPosition += " " + visitor.mediatorPosition;
                    debugInfo.setMediatorPosition(mediatorPosition);
                } else {
                    mediatorPosition = null;
                    debugInfo.setMediatorPosition(null);
                    debugInfo.setValid(false);
                    debugInfo.setError("Invalid breakpoint in Entitlement Service");
                }
            }
        } else {
            mediatorCount += 1;
        }
    }

    @Override
    void visitThrottle(Throttle node) {

        if (VisitorUtils.checkNodeInRange(node, breakpoint)) {
            mediatorPosition = Integer.toString(mediatorCount);
            this.done = true;
            if (VisitorUtils.checkValidBreakpoint(node, breakpoint)) {
                debugInfo.setMediatorPosition(mediatorPosition);
            } else {
                MediatorVisitor visitor = new MediatorVisitor(breakpoint);
                if (VisitorUtils.checkNodeInRange(node.getOnAccept(), breakpoint)) {
                    mediatorPosition += " " + "0";
                    VisitorUtils.visitMediators(node.getOnAccept().getMediatorList(), visitor);
                } else if (VisitorUtils.checkNodeInRange(node.getOnReject(), breakpoint)) {
                    mediatorPosition += " " + "1";
                    VisitorUtils.visitMediators(node.getOnReject().getMediatorList(), visitor);
                }
                if (visitor.mediatorPosition != null) {
                    mediatorPosition += " " + visitor.mediatorPosition;
                    debugInfo.setMediatorPosition(mediatorPosition);
                } else {
                    mediatorPosition = null;
                    debugInfo.setMediatorPosition(null);
                    debugInfo.setValid(false);
                    debugInfo.setError("Invalid breakpoint in Throttle Mediator");
                }
            }
        } else {
            mediatorCount += 1;
        }
    }

    @Override
    void visitCache(Cache node) {

        if (VisitorUtils.checkNodeInRange(node, breakpoint)) {
            mediatorPosition = Integer.toString(mediatorCount);
            this.done = true;
            if (VisitorUtils.checkValidBreakpoint(node, breakpoint)) {
                debugInfo.setMediatorPosition(mediatorPosition);
            } else {
                MediatorVisitor visitor = new MediatorVisitor(breakpoint);
                if (VisitorUtils.checkNodeInRange(node.getOnCacheHit(), breakpoint)) {
                    mediatorPosition += " " + "0";
                    VisitorUtils.visitMediators(node.getOnCacheHit().getMediatorList(), visitor);
                }
                if (visitor.mediatorPosition != null) {
                    mediatorPosition += " " + visitor.mediatorPosition;
                    debugInfo.setMediatorPosition(mediatorPosition);
                } else {
                    mediatorPosition = null;
                    debugInfo.setMediatorPosition(null);
                    debugInfo.setValid(false);
                    debugInfo.setError("Invalid breakpoint in Cache Mediator");
                }
            }
        } else {
            mediatorCount += 1;
        }
    }

    @Override
    void visitEnqueue(Enqueue node) {

        visitSimpleMediator(node);
    }

    @Override
    void visitDbreport(DbMediator node) {

        visitSimpleMediator(node);
    }

    @Override
    void visitDblookup(DbMediator node) {

        visitSimpleMediator(node);
    }

    @Override
    void visitEvent(Event node) {

        visitSimpleMediator(node);
    }

    @Override
    void visitRespond(Respond node) {

        visitSimpleMediator(node);
    }

    @Override
    void visitPublishEvent(PublishEvent node) {

        visitSimpleMediator(node);
    }

    @Override
    void visitSwitch(Switch node) {

        if (VisitorUtils.checkNodeInRange(node, breakpoint)) {
            mediatorPosition = Integer.toString(mediatorCount);
            this.done = true;
            if (VisitorUtils.checkValidBreakpoint(node, breakpoint)) {
                debugInfo.setMediatorPosition(mediatorPosition);
            } else {
                MediatorVisitor visitor = new MediatorVisitor(breakpoint);
                if (VisitorUtils.checkNodeInRange(node.get_default(), breakpoint)) {
                    mediatorPosition += " " + "0";
                    VisitorUtils.visitMediators(node.get_default().getMediatorList(), visitor);
                } else {
                    SwitchCase[] cases = node.get_case();
                    if (cases != null) {
                        for (int i = 0; i < cases.length; i++) {
                            if (VisitorUtils.checkNodeInRange(cases[i], breakpoint)) {
                                mediatorPosition += " " + (i + 1);
                                VisitorUtils.visitMediators(cases[i].getMediatorList(), visitor);
                                break;
                            }
                        }
                    }
                }
                if (visitor.mediatorPosition != null) {
                    mediatorPosition += " " + visitor.mediatorPosition;
                    debugInfo.setMediatorPosition(mediatorPosition);
                } else {
                    mediatorPosition = null;
                    debugInfo.setMediatorPosition(null);
                    debugInfo.setValid(false);
                    debugInfo.setError("Invalid breakpoint in Switch Mediator");
                }
            }

        } else {
            mediatorCount += 1;
        }
    }

    @Override
    void visitSpring(Spring node) {

        visitSimpleMediator(node);
    }

    @Override
    void visitRule(Rule node) {

        if (VisitorUtils.checkNodeInRange(node, breakpoint)) {
            this.done = true;
            mediatorPosition = Integer.toString(mediatorCount);
            if (VisitorUtils.checkValidBreakpoint(node, breakpoint)) {
                debugInfo.setMediatorPosition(mediatorPosition);
            } else {
                MediatorVisitor visitor = new MediatorVisitor(breakpoint);
                if (VisitorUtils.checkNodeInRange(node.getChildMediators(), breakpoint)) {
                    mediatorPosition += " " + "0";
                    VisitorUtils.visitMediators(node.getChildMediators().getMediatorList(), visitor);
                }
                if (visitor.mediatorPosition != null) {
                    mediatorPosition += " " + visitor.mediatorPosition;
                    debugInfo.setMediatorPosition(mediatorPosition);
                } else {
                    mediatorPosition = null;
                    debugInfo.setMediatorPosition(null);
                    debugInfo.setValid(false);
                    debugInfo.setError("Invalid breakpoint in Rule Mediator");
                }
            }
        } else {
            mediatorCount += 1;
        }
    }

    @Override
    void visitConditionalRouter(ConditionalRouter node) {

        visitSimpleMediator(node);
    }

    @Override
    void visitLoopback(Loopback node) {

        visitSimpleMediator(node);
    }

    @Override
    void visitStore(Store node) {

        visitSimpleMediator(node);
    }

    @Override
    void visitValidate(Validate node) {

        if (VisitorUtils.checkNodeInRange(node, breakpoint)) {
            this.done = true;
            mediatorPosition = Integer.toString(mediatorCount);
            if (VisitorUtils.checkValidBreakpoint(node, breakpoint)) {
                debugInfo.setMediatorPosition(mediatorPosition);
            } else {
                MediatorVisitor visitor = new MediatorVisitor(breakpoint);
                if (VisitorUtils.checkNodeInRange(node.getOnFail(), breakpoint)) {
                    mediatorPosition += " " + "0";
                    VisitorUtils.visitMediators(node.getOnFail().getMediatorList(), visitor);
                }
                if (visitor.mediatorPosition != null) {
                    mediatorPosition += " " + visitor.mediatorPosition;
                    debugInfo.setMediatorPosition(mediatorPosition);
                } else {
                    mediatorPosition = null;
                    debugInfo.setMediatorPosition(null);
                    debugInfo.setValid(false);
                    debugInfo.setError("Invalid breakpoint in Validate Mediator");
                }
            }
        } else {
            mediatorCount += 1;
        }
    }

    @Override
    void visitFilter(Filter node) {

        if (VisitorUtils.checkNodeInRange(node, breakpoint)) {
            this.done = true;
            mediatorPosition = Integer.toString(mediatorCount);
            if (VisitorUtils.checkValidBreakpoint(node, breakpoint)) {
                debugInfo.setMediatorPosition(mediatorPosition);
            } else {
                MediatorVisitor visitor = new MediatorVisitor(breakpoint);
                if (VisitorUtils.checkNodeInRange(node.getThen(), breakpoint)) {
                    mediatorPosition += " " + "0";
                    VisitorUtils.visitMediators(node.getThen().getMediatorList(), visitor);
                } else if (VisitorUtils.checkNodeInRange(node.getElse_(), breakpoint)) {
                    mediatorPosition += " " + "1";
                    VisitorUtils.visitMediators(node.getElse_().getMediatorList(), visitor);
                }
                if (visitor.mediatorPosition != null) {
                    mediatorPosition += " " + visitor.mediatorPosition;
                    debugInfo.setMediatorPosition(mediatorPosition);
                } else {
                    mediatorPosition = null;
                    debugInfo.setMediatorPosition(null);
                    debugInfo.setValid(false);
                    debugInfo.setError("Invalid breakpoint in Filter Mediator");
                }
            }
        } else {
            mediatorCount += 1;
        }
    }

    @Override
    void visitSend(Send node) {

        visitSimpleMediator(node);
    }

    @Override
    void visitClone(Clone node) {

        if (VisitorUtils.checkNodeInRange(node, breakpoint)) {
            mediatorPosition = Integer.toString(mediatorCount);
            this.done = true;
            if (VisitorUtils.checkValidBreakpoint(node, breakpoint)) {
                debugInfo.setMediatorPosition(mediatorPosition);
            } else {
                MediatorVisitor visitor = new MediatorVisitor(breakpoint);
                CloneTarget[] targets = node.getTarget();
                if (targets != null) {
                    for (int i = 0; i < targets.length; i++) {
                        if (VisitorUtils.checkNodeInRange(targets[i], breakpoint)) {
                            mediatorPosition += " " + i;
                            VisitorUtils.visitMediators(targets[i].getSequence().getMediatorList(), visitor);
                            break;
                        }
                    }
                }
                if (visitor.mediatorPosition != null) {
                    mediatorPosition += " " + visitor.mediatorPosition;
                    debugInfo.setMediatorPosition(mediatorPosition);
                } else {
                    mediatorPosition = null;
                    debugInfo.setMediatorPosition(null);
                    debugInfo.setValid(false);
                    debugInfo.setError("Invalid breakpoint in Clone Mediator");
                }
            }
        } else {
            mediatorCount += 1;
        }
    }

    @Override
    void visitClass(Class node) {

        visitSimpleMediator(node);
    }

    @Override
    void visitAggregate(Aggregate node) {

        if (VisitorUtils.checkNodeInRange(node, breakpoint)) {
            this.done = true;
            mediatorPosition = Integer.toString(mediatorCount);
            if (VisitorUtils.checkValidBreakpoint(node, breakpoint)) {
                debugInfo.setMediatorPosition(mediatorPosition);
            } else {
                MediatorVisitor visitor = new MediatorVisitor(breakpoint);
                if (VisitorUtils.checkNodeInRange(node.getCorrelateOnOrCompleteConditionOrOnComplete().getOnComplete().get(), breakpoint)) {
                    mediatorPosition += " " + "0";
                    VisitorUtils.visitMediators(node.getCorrelateOnOrCompleteConditionOrOnComplete().getOnComplete().get().getMediatorList(), visitor);
                }
                if (visitor.mediatorPosition != null) {
                    mediatorPosition += " " + visitor.mediatorPosition;
                    debugInfo.setMediatorPosition(mediatorPosition);
                } else {
                    mediatorPosition = null;
                    debugInfo.setMediatorPosition(null);
                    debugInfo.setValid(false);
                    debugInfo.setError("Invalid breakpoint in Aggregate Mediator");
                }
            }
        } else {
            mediatorCount += 1;
        }
    }

    @Override
    void visitLog(Log node) {

        visitSimpleMediator(node);
    }

    @Override
    void visitEjb(Ejb node) {

        visitSimpleMediator(node);
    }

    @Override
    void visitNTLM(Ntlm node) {

        visitSimpleMediator(node);
    }

    @Override
    void visitRewrite(Rewrite node) {

        visitSimpleMediator(node);
    }

    @Override
    void visitCallTemplate(CallTemplate node) {

        visitSimpleMediator(node);
    }

    @Override
    void visitCall(Call node) {

        visitSimpleMediator(node);
    }

    @Override
    void visitIterate(Iterate node) {

        if (VisitorUtils.checkNodeInRange(node, breakpoint)) {
            this.done = true;
            mediatorPosition = Integer.toString(mediatorCount);
            if (VisitorUtils.checkValidBreakpoint(node, breakpoint)) {
                debugInfo.setMediatorPosition(mediatorPosition);
            } else {
                MediatorVisitor visitor = new MediatorVisitor(breakpoint);
                if (VisitorUtils.checkNodeInRange(node.getTarget(), breakpoint)) {
                    mediatorPosition += " " + "0";
                    VisitorUtils.visitMediators(node.getTarget().getSequence().getMediatorList(), visitor);
                }
                if (visitor.mediatorPosition != null) {
                    mediatorPosition += " " + visitor.mediatorPosition;
                    debugInfo.setMediatorPosition(mediatorPosition);
                } else {
                    mediatorPosition = null;
                    debugInfo.setMediatorPosition(null);
                    debugInfo.setValid(false);
                    debugInfo.setError("Invalid breakpoint in Iterate Mediator");
                }
            }
        } else {
            mediatorCount += 1;
        }
    }

    @Override
    void visitProperty(Property node) {

        visitSimpleMediator(node);
    }

    @Override
    void visitForeach(Foreach node) {

        if (VisitorUtils.checkNodeInRange(node, breakpoint)) {
            this.done = true;
            mediatorPosition = Integer.toString(mediatorCount);
            if (VisitorUtils.checkValidBreakpoint(node, breakpoint)) {
                debugInfo.setMediatorPosition(mediatorPosition);
            } else {
                MediatorVisitor visitor = new MediatorVisitor(breakpoint);
                if (VisitorUtils.checkNodeInRange(node.getSequence(), breakpoint)) {
                    mediatorPosition += " " + "0";
                    VisitorUtils.visitMediators(node.getSequence().getMediatorList(), visitor);
                }
                if (visitor.mediatorPosition != null) {
                    mediatorPosition += " " + visitor.mediatorPosition;
                    debugInfo.setMediatorPosition(mediatorPosition);
                } else {
                    mediatorPosition = null;
                    debugInfo.setMediatorPosition(null);
                    debugInfo.setValid(false);
                    debugInfo.setError("Invalid breakpoint in Foreach Mediator");
                }
            }
        } else {
            mediatorCount += 1;
        }
    }

    @Override
    void visitEnrich(Enrich node) {

        visitSimpleMediator(node);
    }

    @Override
    void visitScript(Script node) {

        visitSimpleMediator(node);
    }

    @Override
    void visitBean(Bean node) {

        visitSimpleMediator(node);
    }

    @Override
    void visitXquery(Xquery node) {

        visitSimpleMediator(node);
    }

    @Override
    void visitBuilder(Builder node) {

        visitSimpleMediator(node);
    }

    @Override
    void visitDrop(Drop node) {

        visitSimpleMediator(node);
    }

    @Override
    void visitXslt(Xslt node) {

        visitSimpleMediator(node);
    }

    @Override
    void visitBam(Bam node) {

        visitSimpleMediator(node);
    }

    @Override
    void visitFastXSLT(FastXSLT node) {

        visitSimpleMediator(node);
    }

    @Override
    void visitOauthService(OauthService node) {

        visitSimpleMediator(node);
    }

    @Override
    void visitCallout(Callout node) {

        visitSimpleMediator(node);
    }
}
