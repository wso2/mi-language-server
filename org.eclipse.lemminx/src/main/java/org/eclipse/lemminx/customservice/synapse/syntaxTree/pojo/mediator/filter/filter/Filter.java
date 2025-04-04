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

package org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.filter.filter;

import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.Mediator;

public class Filter extends Mediator {

    FilterThen then;
    FilterElse else_;
    String source;
    String regex;
    String xpath;
    String description;

    public Filter() {
        setDisplayName("If Else");
    }

    public FilterThen getThen() {

        return then;
    }

    public void setThen(FilterThen then) {

        this.then = then;
    }

    public FilterElse getElse_() {

        return else_;
    }

    public void setElse_(FilterElse else_) {

        this.else_ = else_;
    }

    public String getSource() {

        return source;
    }

    public void setSource(String source) {

        this.source = source;
    }

    public String getRegex() {

        return regex;
    }

    public void setRegex(String regex) {

        this.regex = regex;
    }

    public String getXpath() {

        return xpath;
    }

    public void setXpath(String xpath) {

        this.xpath = xpath;
    }

    public String getDescription() {

        return description;
    }

    public void setDescription(String description) {

        this.description = description;
    }
}