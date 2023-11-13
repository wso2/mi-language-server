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

package org.eclipse.lemminx.customservice.syntaxmodel.pojo.mediator.other.rule;

import org.eclipse.lemminx.customservice.syntaxmodel.pojo.STNode;

public class RuleOutput extends STNode {

    RuleOutputFact[] fact;
    String namespace;
    String wrapperElementName;

    public RuleOutputFact[] getFact() {

        return fact;
    }

    public void setFact(RuleOutputFact[] fact) {

        this.fact = fact;
    }

    public String getNamespace() {

        return namespace;
    }

    public void setNamespace(String namespace) {

        this.namespace = namespace;
    }

    public String getWrapperElementName() {

        return wrapperElementName;
    }

    public void setWrapperElementName(String wrapperElementName) {

        this.wrapperElementName = wrapperElementName;
    }
}