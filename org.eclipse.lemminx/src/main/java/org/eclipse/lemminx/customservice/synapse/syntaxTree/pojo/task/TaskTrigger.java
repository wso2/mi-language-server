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

public class TaskTrigger extends STNode {

    String interval;
    String count;
    Object once;
    String cron;

    public String getInterval() {

        return interval;
    }

    public void setInterval(String interval) {

        this.interval = interval;
    }

    public String getCount() {

        return count;
    }

    public void setCount(String count) {

        this.count = count;
    }

    public Object getOnce() {

        return once;
    }

    public void setOnce(Object once) {

        this.once = once;
    }

    public String getCron() {

        return cron;
    }

    public void setCron(String cron) {

        this.cron = cron;
    }
}
