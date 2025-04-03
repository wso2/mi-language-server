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
import java.util.Collections;
import java.util.List;

public class ExpressionCompletionContext {

    private ExpressionCompletionContext parent;
    private List<String> segment;
    private Range range;
    private ExpressionCompletionType type;

    // Whether object traversal is needed for the current or next level.
    private boolean needNext;

    public ExpressionCompletionContext(ExpressionCompletionContext parent) {

        this.parent = parent.deepCopy();
        segment = new ArrayList<>();
    }

    public ExpressionCompletionContext(ExpressionCompletionContext parent, ExpressionCompletionType type) {

        this.parent = parent;
        this.type = type;
        segment = new ArrayList<>();
    }

    public ExpressionCompletionContext() {

        segment = new ArrayList<>();
        type = ExpressionCompletionType.ROOT_LEVEL;
    }

    public ExpressionCompletionContext(ExpressionCompletionContext parent, List<String> segment, Range range) {

        this.parent = parent.deepCopy();
        this.segment = new ArrayList<>(segment);
        this.range = range;
    }

    public void addSegment(String segment) {

        this.segment.add(segment);
    }

    public void setRange(Range range) {

        this.range = range;
    }

    public ExpressionCompletionContext getParent() {

        if (parent == null) {
            return null;
        }
        return parent.deepCopy();
    }

    public List<String> getSegment() {

        return Collections.unmodifiableList(segment);
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

    public ExpressionCompletionContext deepCopy() {

        ExpressionCompletionContext clone = new ExpressionCompletionContext();
        clone.segment = new ArrayList<>(segment);
        clone.range = range;
        clone.type = type;
        clone.needNext = needNext;
        if (parent != null) {
            clone.parent = parent.deepCopy();
        }
        return clone;
    }
}
