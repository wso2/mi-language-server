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

package org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.mediator.data;

import org.apache.axiom.om.OMElement;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.Mediator;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.advanced.DataServiceCall.DataServiceCall;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.advanced.DataServiceCall.DataServiceCallOperations;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.advanced.DataServiceCall.DataServiceCallOperationsOperation;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.advanced.DataServiceCall.DataServiceCallOperationsOperationParam;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.advanced.DataServiceCall.DataServiceCallSource;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.advanced.DataServiceCall.DataServiceCallTarget;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.mediator.AbstractMediatorSerializer;

public class DataServiceCallMediatorSerializer extends AbstractMediatorSerializer {

    @Override
    protected OMElement serializeSpecificMediator(Mediator m) {

        DataServiceCall dataServiceCall = (DataServiceCall) m;
        OMElement dataServiceCallElt = fac.createOMElement("dataServiceCall", synNS);

        serializeAttributes(dataServiceCall, dataServiceCallElt);
        serializeChildren(dataServiceCall, dataServiceCallElt);

        return dataServiceCallElt;
    }

    private void serializeAttributes(DataServiceCall dataServiceCall, OMElement dataServiceCallElt) {

        if (dataServiceCall.getServiceName() != null) {
            dataServiceCallElt.addAttribute("serviceName", dataServiceCall.getServiceName(), null);
        }
        if (dataServiceCall.getDescription() != null) {
            dataServiceCallElt.addAttribute("description", dataServiceCall.getDescription(), null);
        }
    }

    private void serializeChildren(DataServiceCall dataServiceCall, OMElement dataServiceCallElt) {

        serializeSource(dataServiceCall.getSource(), dataServiceCallElt);
        serializeOperations(dataServiceCall.getOperations(), dataServiceCallElt);
        serializeTarget(dataServiceCall.getTarget(), dataServiceCallElt);
    }

    private void serializeSource(DataServiceCallSource source, OMElement dataServiceCallElt) {

        if (source != null) {
            OMElement sourceElt = fac.createOMElement("source", synNS);
            if (source.getType() != null) {
                sourceElt.addAttribute("type", source.getType().name(), nullNS);
            }
            dataServiceCallElt.addChild(sourceElt);
        }
    }

    private void serializeOperations(DataServiceCallOperations operations, OMElement dataServiceCallElt) {

        if (operations != null) {
            OMElement operationsElt = fac.createOMElement("operations", synNS);
            if (operations.getType() != null) {
                operationsElt.addAttribute("type", operations.getType().getValue(), nullNS);
            }
            if (operations.getOperation() != null) {
                for (DataServiceCallOperationsOperation operation : operations.getOperation()) {
                    OMElement operationElt = serializeOperation(operation);
                    operationsElt.addChild(operationElt);
                }
            }
            dataServiceCallElt.addChild(operationsElt);
        }
    }

    private OMElement serializeOperation(DataServiceCallOperationsOperation operation) {

        OMElement operationElt = fac.createOMElement("operation", synNS);
        if (operation.getName() != null) {
            operationElt.addAttribute("name", operation.getName(), nullNS);
        }
        if (operation.getParam() != null) {
            for (DataServiceCallOperationsOperationParam param : operation.getParam()) {
                OMElement paramElt = serializeParam(param);
                operationElt.addChild(paramElt);
            }
        }
        return operationElt;
    }

    private OMElement serializeParam(DataServiceCallOperationsOperationParam param) {

        OMElement paramElt = fac.createOMElement("param", synNS);
        if (param.getName() != null) {
            paramElt.addAttribute("name", param.getName(), nullNS);
        }
        if (param.getValue() != null) {
            paramElt.addAttribute("value", param.getValue(), nullNS);
        } else if (param.getExpression() != null) {
            paramElt.addAttribute("expression", param.getExpression(), nullNS);
            paramElt.addAttribute("evaluator", param.getEvaluator(), nullNS);
        }
        return paramElt;
    }

    private void serializeTarget(DataServiceCallTarget target, OMElement dataServiceCallElt) {

        if (target != null) {
            OMElement targetElt = fac.createOMElement("target", synNS);
            if (target.getType() != null) {
                targetElt.addAttribute("type", target.getType().name(), nullNS);
            }
            if (target.getName() != null) {
                targetElt.addAttribute("name", target.getName(), nullNS);
            }
            dataServiceCallElt.addChild(targetElt);
        }
    }

    @Override
    public String getMediatorClassName() {

        return DataServiceCall.class.getName();
    }
}
