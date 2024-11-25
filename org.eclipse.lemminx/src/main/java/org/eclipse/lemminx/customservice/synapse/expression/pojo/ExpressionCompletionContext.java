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

package org.eclipse.lemminx.customservice.synapse.expression.pojo;

import org.eclipse.lsp4j.Range;

import java.util.ArrayList;
import java.util.List;

public class ExpressionCompletionContext {

    private ExpressionCompletionContext parent;
    private List<String> segment;
    private Range range;
    private ExpressionCompletionType type;

    // Whether object traversal is needed for the current or next level.
    private boolean needNext;

    public ExpressionCompletionContext(ExpressionCompletionContext parent) {

        this.parent = parent;
        segment = new ArrayList<>();
    }

    public ExpressionCompletionContext() {

        segment = new ArrayList<>();
        type = ExpressionCompletionType.ROOT_LEVEL;
    }

    public ExpressionCompletionContext(ExpressionCompletionContext parent, List<String> segment, Range range) {

        this.parent = parent;
        this.segment = segment;
        this.range = range;
    }

    public void addSegment(String segment) {

        this.segment.add(segment);
    }

    public void setRange(Range range) {

        this.range = range;
    }

    public ExpressionCompletionContext getParent() {

        return parent;
    }

    public List<String> getSegment() {

        return segment;
    }

    public Range getRange() {

        return range;
    }

    public ExpressionCompletionType getType() {

        return type;
    }

    public void setType(ExpressionCompletionType type) {

        this.type = type;
    }

    public boolean isNeedNext() {

        return needNext;
    }

    public void setNeedNext(boolean needNext) {

        this.needNext = needNext;
    }

    public void pop() {

        segment.remove(segment.size() - 1);
    }
}
