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

package org.eclipse.lemminx.customservice.synapse.directoryTree.node;

public class Node {

    String type;
    String subType;
    String name;
    String path;
    Boolean isFaulty = false;

    public Node(String type, String name, String path) {

        this.type = type;
        this.name = name;
        this.path = path;
    }

    public String getType() {

        return type;
    }

    public void setType(String type) {

        this.type = type;
    }

    public String getSubType() {

        return subType;
    }

    public void setSubType(String subType) {

        this.subType = subType;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public String getPath() {

        return path;
    }

    public void setPath(String path) {

        this.path = path;
    }

    public Boolean getFaulty() {

        return isFaulty;
    }

    public void setFaulty(Boolean faulty) {

        isFaulty = faulty;
    }

    protected Boolean equals(Node component) {

        return this.type.equals(component.type) && this.name.equals(component.name) && this.path.equals(component.path);
    }
}
