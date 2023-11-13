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

package org.eclipse.lemminx.customservice.syntaxmodel.pojo.mediator.transformation.payload;

import org.eclipse.lemminx.customservice.syntaxmodel.pojo.STNode;

public class PayloadFactoryArgsArg extends STNode {

    String value;
    String evaluator;
    String expression;
    boolean literal;

    public String getValue() {

        return value;
    }

    public void setValue(String value) {

        this.value = value;
    }

    public String getEvaluator() {

        return evaluator;
    }

    public void setEvaluator(String evaluator) {

        this.evaluator = evaluator;
    }

    public String getExpression() {

        return expression;
    }

    public void setExpression(String expression) {

        this.expression = expression;
    }

    public boolean isLiteral() {

        return literal;
    }

    public void setLiteral(boolean literal) {

        this.literal = literal;
    }
}
