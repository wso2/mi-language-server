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

package org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo;

public class Range {

    TagRange startTagRange;
    TagRange endTagRange;
    TagRange startTagLeadingSpaces;
    TagRange startTagTrailingSpaces;
    TagRange endTagLeadingSpaces;
    TagRange endTagTrailingSpaces;

    public Range(TagRange startTagRange, TagRange endTagRange) {

        this.startTagRange = startTagRange;
        this.endTagRange = endTagRange;
    }

    public TagRange getStartTagRange() {

        return startTagRange;
    }

    public void setStartTagRange(TagRange startTagRange) {

        this.startTagRange = startTagRange;
    }

    public TagRange getEndTagRange() {

        return endTagRange;
    }

    public void setEndTagRange(TagRange endTagRange) {

        this.endTagRange = endTagRange;
    }

    public TagRange getStartTagLeadingSpaces() {

        return startTagLeadingSpaces;
    }

    public void setStartTagLeadingSpaces(TagRange startTagLeadingSpaces) {

        this.startTagLeadingSpaces = startTagLeadingSpaces;
    }

    public TagRange getStartTagTrailingSpaces() {

        return startTagTrailingSpaces;
    }

    public void setStartTagTrailingSpaces(TagRange startTagTrailingSpaces) {

        this.startTagTrailingSpaces = startTagTrailingSpaces;
    }

    public TagRange getEndTagLeadingSpaces() {

        return endTagLeadingSpaces;
    }

    public void setEndTagLeadingSpaces(TagRange endTagLeadingSpaces) {

        this.endTagLeadingSpaces = endTagLeadingSpaces;
    }

    public TagRange getEndTagTrailingSpaces() {

        return endTagTrailingSpaces;
    }

    public void setEndTagTrailingSpaces(TagRange endTagTrailingSpaces) {

        this.endTagTrailingSpaces = endTagTrailingSpaces;
    }
}
