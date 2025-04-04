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

package org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core;

import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.Mediator;

public class Property extends Mediator {

    Object any;
    PropertyScope scope;
    PropertyMediatorType type;
    String pattern;
    String group;
    String description;
    String name;
    String action;
    String value;
    String expression;

    public Property() {
        setDisplayName("Property");
    }
    public Object getAny() {

        return any;
    }

    public void setAny(Object any) {

        this.any = any;
    }

    public PropertyScope getScope() {

        return scope;
    }

    public void setScope(PropertyScope scope) {

        this.scope = scope;
    }

    public PropertyMediatorType getType() {

        return type;
    }

    public void setType(PropertyMediatorType type) {

        this.type = type;
    }

    public String getPattern() {

        return pattern;
    }

    public void setPattern(String pattern) {

        this.pattern = pattern;
    }

    public String getGroup() {

        return group;
    }

    public void setGroup(String group) {

        this.group = group;
    }

    public String getDescription() {

        return description;
    }

    public void setDescription(String description) {

        this.description = description;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public String getAction() {

        return action;
    }

    public void setAction(String action) {

        this.action = action;
    }

    public String getValue() {

        return value;
    }

    public void setValue(String value) {

        this.value = value;
    }

    public String getExpression() {

        return expression;
    }

    public void setExpression(String expression) {

        this.expression = expression;
    }
}