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

package org.eclipse.lemminx.customservice.syntaxmodel.factory.mediators.filter;

import org.eclipse.lemminx.customservice.syntaxmodel.factory.mediators.AbstractMediatorFactory;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.STNode;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.mediator.Mediator;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.mediator.filter.condRouter.ConditionalRouter;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.mediator.filter.condRouter.ConditionalRouterConditionalRoute;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.mediator.filter.condRouter.ConditionalRouterConditionalRouteCondition;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.misc.evaluators.Equal;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.misc.targets.Target;
import org.eclipse.lemminx.customservice.syntaxmodel.utils.Constant;
import org.eclipse.lemminx.customservice.syntaxmodel.utils.Utils;
import org.eclipse.lemminx.dom.DOMElement;
import org.eclipse.lemminx.dom.DOMNode;

import java.util.ArrayList;
import java.util.List;

public class ConditionalRouterFactory extends AbstractMediatorFactory {

    private static final String CONDITIONAL_ROUTER = "conditionalRouter";

    @Override
    public Mediator createSpecificMediator(DOMElement element) {

        ConditionalRouter conditionalRouter = new ConditionalRouter();
        conditionalRouter.elementNode(element);
        populateAttributes(conditionalRouter, element);
        List<DOMNode> children = element.getChildren();
        List<ConditionalRouterConditionalRoute> routes = new ArrayList<>();
        if (children != null && !children.isEmpty()) {
            for (DOMNode child : children) {
                if (child.getNodeName().equalsIgnoreCase(Constant.CONDITIONAL_ROUTE)) {
                    ConditionalRouterConditionalRoute route = createRoute(child);
                    routes.add(route);
                }
            }
            conditionalRouter.setConditionalRoute(routes.toArray(new ConditionalRouterConditionalRoute[routes.size()]));
        }
        return conditionalRouter;
    }

    @Override
    public void populateAttributes(STNode node, DOMElement element) {

        String continueAfter = element.getAttribute(Constant.CONTINUE_AFTER);
        if (continueAfter != null && !continueAfter.isEmpty()) {
            ((ConditionalRouter) node).setContinueAfter(Boolean.parseBoolean(continueAfter));
        }
        String description = element.getAttribute(Constant.DESCRIPTION);
        if (description != null && !description.isEmpty()) {
            ((ConditionalRouter) node).setDescription(description);
        }
    }

    private ConditionalRouterConditionalRoute createRoute(DOMNode child) {

        ConditionalRouterConditionalRoute route = new ConditionalRouterConditionalRoute();
        route.elementNode((DOMElement) child);
        String breakRoute = child.getAttribute(Constant.BREAK_ROUTE);
        if (breakRoute != null && !breakRoute.isEmpty()) {
            route.setBreakRoute(Boolean.parseBoolean(breakRoute));
        }
        List<DOMNode> children = child.getChildren();
        if (children != null && !children.isEmpty()) {
            for (DOMNode childNode : children) {
                if (childNode.getNodeName().equalsIgnoreCase(Constant.CONDITION)) {
                    ConditionalRouterConditionalRouteCondition condition =
                            createConditionalRouterConditionalRouteCondition(childNode);
                    route.setCondition(condition);
                } else if (childNode.getNodeName().equalsIgnoreCase(Constant.TARGET)) {
                    Target target = Utils.createTarget(childNode);
                    route.setTarget(target);
                }
            }
        }
        return route;
    }

    private ConditionalRouterConditionalRouteCondition createConditionalRouterConditionalRouteCondition(DOMNode element) {

        ConditionalRouterConditionalRouteCondition condition = new ConditionalRouterConditionalRouteCondition();
        condition.elementNode((DOMElement) element);
        DOMNode child = element.getFirstChild();
        if (child != null) {
            if (child.getNodeName().equalsIgnoreCase(Constant.EQUAL)) {
                Equal equal = Utils.createEqual(child);
                condition.setEqual(equal);
            }
        }
        return condition;
    }

    @Override
    public String getTagName() {

        return CONDITIONAL_ROUTER;
    }
}
