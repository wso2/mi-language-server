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

package org.eclipse.lemminx.customservice.syntaxmodel.pojo.misc.evaluators;

import org.eclipse.lemminx.customservice.syntaxmodel.pojo.STNode;

import java.util.Optional;

public class EvaluatorList extends STNode {

    Optional<And> and;
    Optional<Or> or;
    Optional<Equal> equal;
    Optional<Not> not;

    public Optional<And> getAnd() {

        return and;
    }

    public void setAnd(Optional<And> and) {

        this.and = and;
    }

    public Optional<Or> getOr() {

        return or;
    }

    public void setOr(Optional<Or> or) {

        this.or = or;
    }

    public Optional<Equal> getEqual() {

        return equal;
    }

    public void setEqual(Optional<Equal> equal) {

        this.equal = equal;
    }

    public Optional<Not> getNot() {

        return not;
    }

    public void setNot(Optional<Not> not) {

        this.not = not;
    }
}