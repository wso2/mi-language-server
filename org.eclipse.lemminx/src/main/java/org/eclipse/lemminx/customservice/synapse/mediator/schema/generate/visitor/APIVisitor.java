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

import org.eclipse.lemminx.customservice.synapse.mediator.tryout.pojo.Params;
import org.eclipse.lemminx.customservice.synapse.mediator.tryout.pojo.Property;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.api.API;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.api.APIResource;
import org.eclipse.lemminx.customservice.synapse.mediator.tryout.pojo.MediatorTryoutRequest;
import org.eclipse.lemminx.customservice.synapse.mediator.tryout.pojo.MediatorTryoutInfo;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.Mediator;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.misc.common.Sequence;
import org.eclipse.lsp4j.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class APIVisitor {

    public void visit(API api, MediatorTryoutInfo info, MediatorTryoutRequest request) {

        int line = request.getLine();
        int column = request.getColumn();
        Position position = new Position(line, column);
        if (Utils.checkNodeInRange(api, position)) {
            APIResource[] resources = api.getResource();
            if (resources != null) {
                for (APIResource resource : resources) {
                    if (Utils.checkNodeInRange(resource, position)) {
                        visitResource(resource, info, position);
                    }
                }
            }
        }
    }

    private void visitResource(APIResource resource, MediatorTryoutInfo info, Position position) {

        updateResourceParams(resource, info);
        if (needToVisit(resource.getInSequence(), position) || needToVisit(resource.getOutSequence(), position)) {
            Utils.visitSequence(resource.getInSequence(), info, position);
            Utils.visitSequence(resource.getOutSequence(), info, position);
        } else if (needToVisit(resource.getFaultSequence(), position)) {
            Utils.visitSequence(resource.getFaultSequence(), info, position);
        }
    }

    private static boolean needToVisit(Sequence sequence, Position position) {

        if (!Utils.checkNodeInRange(sequence, position)) {
            return false;
        }
        List<Mediator> mediatorList = sequence.getMediatorList();
        if (mediatorList.isEmpty()) {
            return false;
        }
        Mediator firstMediator = mediatorList.get(0);
        int mediatorStartLine = firstMediator.getRange().getStartTagRange().getStart().getLine();
        int mediatorStartColumn = firstMediator.getRange().getStartTagRange().getStart().getCharacter();
        return position.getLine() > mediatorStartLine ||
                (position.getLine() == mediatorStartLine && position.getCharacter() >= mediatorStartColumn);
    }

    private void updateResourceParams(APIResource resource, MediatorTryoutInfo info) {

        String uri = resource.getUriTemplate();
        if (uri != null) {
            Params params = new Params();
            params.setUriParams(getPathParams(uri).stream().map(p -> new Property(p, "")).collect(Collectors.toList()));
            params.setQueryParams(
                    getQueryParams(uri).stream().map(p -> new Property(p, "")).collect(Collectors.toList()));
            info.setInputParams(params);
            info.setOutputParams(params.deepCopy());
        }
    }

    private List<String> getPathParams(String uri) {

        List<String> pathParams = new ArrayList<>();
        if (uri.contains("?")) {
            String pathParamPart = uri.split("\\?")[0];
            for (String part : pathParamPart.split("/")) {
                if (part.startsWith("{") && part.endsWith("}")) {
                    pathParams.add(part.substring(1, part.length() - 1));
                }
            }
        }
        return pathParams;
    }

    private List<String> getQueryParams(String uri) {

        List<String> queryParams = new ArrayList<>();
        if (uri.contains("?")) {
            String queryParamPart = uri.split("\\?")[1];
            for (String part : queryParamPart.split("&")) {
                queryParams.add(part.split("=")[0]);
            }
        }
        return queryParams;
    }
}
