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

package org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.transformation.rewrite;

import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.STNode;

public class RewriteRewriteruleAction extends STNode {

    String value;
    String xpath;
    String regex;
    RewriteRuleActionType type;
    RewriteRuleActionFragment fragment;

    public String getValue() {

        return value;
    }

    public void setValue(String value) {

        this.value = value;
    }

    public String getXpath() {

        return xpath;
    }

    public void setXpath(String xpath) {

        this.xpath = xpath;
    }

    public String getRegex() {

        return regex;
    }

    public void setRegex(String regex) {

        this.regex = regex;
    }

    public RewriteRuleActionType getType() {

        return type;
    }

    public void setType(RewriteRuleActionType type) {

        this.type = type;
    }

    public RewriteRuleActionFragment getFragment() {

        return fragment;
    }

    public void setFragment(RewriteRuleActionFragment fragment) {

        this.fragment = fragment;
    }
}
