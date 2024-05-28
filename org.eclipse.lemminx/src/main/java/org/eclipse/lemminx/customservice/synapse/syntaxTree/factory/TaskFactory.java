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

package org.eclipse.lemminx.customservice.synapse.syntaxTree.factory;

import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.STNode;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.MediatorProperty;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.task.Task;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.task.TaskTrigger;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.utils.SyntaxTreeUtils;
import org.eclipse.lemminx.customservice.synapse.utils.Constant;
import org.eclipse.lemminx.dom.DOMElement;
import org.eclipse.lemminx.dom.DOMNode;

import java.util.ArrayList;
import java.util.List;

public class TaskFactory extends AbstractFactory {

    @Override
    public STNode create(DOMElement element) {

        Task task = new Task();
        task.elementNode(element);
        populateAttributes(task, element);
        List<DOMNode> children = element.getChildren();
        List<MediatorProperty> mediatorPropertyList = new ArrayList<>();
        if (children != null && !children.isEmpty()) {
            for (DOMNode node : children) {
                String name = node.getNodeName();
                if (name.equals(Constant.TRIGGER)) {
                    TaskTrigger taskTrigger = createTaskTrigger(node);
                    task.setTrigger(taskTrigger);
                } else if (name.equals(Constant.PROPERTY)) {
                    MediatorProperty mediatorProperty = SyntaxTreeUtils.createMediatorProperty(node);
                    mediatorPropertyList.add(mediatorProperty);
                }
            }
            task.setProperty(mediatorPropertyList.toArray(new MediatorProperty[mediatorPropertyList.size()]));
        }
        return task;
    }

    @Override
    public void populateAttributes(STNode node, DOMElement element) {

        String name = element.getAttribute(Constant.NAME);
        if (name != null) {
            ((Task) node).setName(name);
        }
        String clazz = element.getAttribute(Constant.CLASS);
        if (clazz != null) {
            ((Task) node).setClazz(clazz);
        }
        String group = element.getAttribute(Constant.GROUP);
        if (group != null) {
            ((Task) node).setGroup(group);
        }
        String pinnedServers = element.getAttribute(Constant.PINNED_SERVERS);
        if (pinnedServers != null) {
            ((Task) node).setPinnedServers(pinnedServers);
        }
    }

    private TaskTrigger createTaskTrigger(DOMNode node) {

        TaskTrigger taskTrigger = new TaskTrigger();
        taskTrigger.elementNode((DOMElement) node);
        String cron = node.getAttribute(Constant.CRON);
        if (cron != null) {
            taskTrigger.setCron(cron);
        }
        String interval = node.getAttribute(Constant.INTERVAL);
        if (interval != null) {
            taskTrigger.setInterval(interval);
        }
        String count = node.getAttribute(Constant.COUNT);
        if (count != null) {
            taskTrigger.setCount(count);
        }
        String once = node.getAttribute(Constant.ONCE);
        if (once != null) {
            taskTrigger.setOnce(Boolean.parseBoolean(once));
        }
        return taskTrigger;
    }
}
