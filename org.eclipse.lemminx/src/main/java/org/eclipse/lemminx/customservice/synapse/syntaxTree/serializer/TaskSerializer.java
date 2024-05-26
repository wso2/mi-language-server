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

package org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.task.Task;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.task.TaskTrigger;
import org.eclipse.lemminx.customservice.synapse.utils.Constant;

public class TaskSerializer {

    private static final OMFactory fac = OMAbstractFactory.getOMFactory();

    public static String serializeTask(Task task) {

        OMElement taskElt = fac.createOMElement("task", Constant.SYNAPSE_OMNAMESPACE);

        serializeAttributes(task, taskElt);
        serializeChildren(task, taskElt);

        return taskElt.toString();
    }

    private static void serializeAttributes(Task task, OMElement taskElt) {

        if (task.getClazz() != null) {
            taskElt.addAttribute("class", task.getClazz(), null);
        }
        if (task.getName() != null) {
            taskElt.addAttribute("name", task.getName(), null);
        }
        if (task.getGroup() != null) {
            taskElt.addAttribute("group", task.getGroup(), null);
        }
        if (task.getPinnedServers() != null) {
            taskElt.addAttribute("pinnedServers", task.getPinnedServers(), null);
        }
    }

    private static void serializeChildren(Task task, OMElement taskElt) {

        if (task.getTrigger() != null) {
            serializeTrigger(task.getTrigger(), taskElt);
        }
        if (task.getProperty() != null) {
            SerializerUtils.serializeMediatorProperties(taskElt, task.getProperty());
        }
    }

    private static void serializeTrigger(TaskTrigger trigger, OMElement taskElt) {

        OMElement triggerElt = fac.createOMElement("trigger", Constant.SYNAPSE_OMNAMESPACE);

        if (trigger.getCron() != null) {
            triggerElt.addAttribute("cron", trigger.getCron(), null);
        } else {
            if (trigger.getOnce() || ("1".equals(trigger.getInterval()) && "1".equals(trigger.getCount()))) {
                triggerElt.addAttribute("once", "true", null);
            } else {
                if (trigger.getInterval() != null) {
                    triggerElt.addAttribute("interval", trigger.getInterval(), null);
                }
                if (trigger.getCount() != null) {
                    triggerElt.addAttribute("count", trigger.getCount(), null);
                }
            }
        }
        taskElt.addChild(triggerElt);
    }
}
