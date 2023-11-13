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

import org.eclipse.lemminx.customservice.syntaxmodel.pojo.mediator.Mediator;

public class Rule extends Mediator {

    RuleSource source;
    RuleTarget target;
    RuleRuleset ruleSet;
    RuleInput input;
    RuleOutput output;
    RuleChildMediators childMediators;
    String description;

    public RuleSource getSource() {

        return source;
    }

    public void setSource(RuleSource source) {

        this.source = source;
    }

    public RuleTarget getTarget() {

        return target;
    }

    public void setTarget(RuleTarget target) {

        this.target = target;
    }

    public RuleRuleset getRuleSet() {

        return ruleSet;
    }

    public void setRuleSet(RuleRuleset ruleSet) {

        this.ruleSet = ruleSet;
    }

    public RuleInput getInput() {

        return input;
    }

    public void setInput(RuleInput input) {

        this.input = input;
    }

    public RuleOutput getOutput() {

        return output;
    }

    public void setOutput(RuleOutput output) {

        this.output = output;
    }

    public RuleChildMediators getChildMediators() {

        return childMediators;
    }

    public void setChildMediators(RuleChildMediators childMediators) {

        this.childMediators = childMediators;
    }

    public String getDescription() {

        return description;
    }

    public void setDescription(String description) {

        this.description = description;
    }
}
