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

package org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.extension.ejb;

import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.Mediator;

public class Ejb extends Mediator {

    EjbArgs args;
    String beanstalk;
    String clazz;
    String sessionId;
    boolean remove;
    String method;
    String target;
    String jndiName;
    String id;
    boolean stateful;
    String description;

    public EjbArgs getArgs() {

        return args;
    }

    public void setArgs(EjbArgs args) {

        this.args = args;
    }

    public String getBeanstalk() {

        return beanstalk;
    }

    public void setBeanstalk(String beanstalk) {

        this.beanstalk = beanstalk;
    }

    public String getClazz() {

        return clazz;
    }

    public void setClazz(String clazz) {

        this.clazz = clazz;
    }

    public String getSessionId() {

        return sessionId;
    }

    public void setSessionId(String sessionId) {

        this.sessionId = sessionId;
    }

    public boolean isRemove() {

        return remove;
    }

    public void setRemove(boolean remove) {

        this.remove = remove;
    }

    public String getMethod() {

        return method;
    }

    public void setMethod(String method) {

        this.method = method;
    }

    public String getTarget() {

        return target;
    }

    public void setTarget(String target) {

        this.target = target;
    }

    public String getJndiName() {

        return jndiName;
    }

    public void setJndiName(String jndiName) {

        this.jndiName = jndiName;
    }

    public String getId() {

        return id;
    }

    public void setId(String id) {

        this.id = id;
    }

    public boolean isStateful() {

        return stateful;
    }

    public void setStateful(boolean stateful) {

        this.stateful = stateful;
    }

    public String getDescription() {

        return description;
    }

    public void setDescription(String description) {

        this.description = description;
    }
}