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

package org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.task;

import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.STNode;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.MediatorProperty;

public class Task extends STNode {

    TaskTrigger trigger;
    MediatorProperty[] property;
    String clazz;
    String name;
    String group;
    String pinnedServers;

    public TaskTrigger getTrigger() {

        return trigger;
    }

    public void setTrigger(TaskTrigger trigger) {

        this.trigger = trigger;
    }

    public MediatorProperty[] getProperty() {

        return property;
    }

    public void setProperty(MediatorProperty[] property) {

        this.property = property;
    }

    public String getClazz() {

        return clazz;
    }

    public void setClazz(String clazz) {

        this.clazz = clazz;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public String getGroup() {

        return group;
    }

    public void setGroup(String group) {

        this.group = group;
    }

    public String getPinnedServers() {

        return pinnedServers;
    }

    public void setPinnedServers(String pinnedServers) {

        this.pinnedServers = pinnedServers;
    }
}