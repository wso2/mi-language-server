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

package org.eclipse.lemminx.customservice.syntaxmodel.pojo.mediator.filter.condRouter;

import org.eclipse.lemminx.customservice.syntaxmodel.pojo.STNode;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.misc.targets.Target;

public class ConditionalRouterConditionalRoute extends STNode {

    ConditionalRouterConditionalRouteCondition condition;
    Target target;
    boolean asynchronous;
    boolean breakRoute;

    public ConditionalRouterConditionalRouteCondition getCondition() {

        return condition;
    }

    public void setCondition(ConditionalRouterConditionalRouteCondition condition) {

        this.condition = condition;
    }

    public Target getTarget() {

        return target;
    }

    public void setTarget(Target target) {

        this.target = target;
    }

    public boolean isAsynchronous() {

        return asynchronous;
    }

    public void setAsynchronous(boolean asynchronous) {

        this.asynchronous = asynchronous;
    }

    public boolean isBreakRoute() {

        return breakRoute;
    }

    public void setBreakRoute(boolean breakRoute) {

        this.breakRoute = breakRoute;
    }
}
