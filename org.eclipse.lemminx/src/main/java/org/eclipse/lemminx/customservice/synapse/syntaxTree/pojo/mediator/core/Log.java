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

package org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core;

import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.Mediator;

public class Log extends Mediator {

    MediatorProperty[] property;
    LogLevel level;
    String separator;
    LogCategory category;
    String description;
    String message;
    boolean logFullPayload;
    boolean logMessageID;

    public Log() {
        setDisplayName("Log");
    }

    public MediatorProperty[] getProperty() {

        return property;
    }

    public void setProperty(MediatorProperty[] property) {

        this.property = property;
    }

    public LogLevel getLevel() {

        return level;
    }

    public void setLevel(LogLevel level) {

        this.level = level;
    }

    public String getSeparator() {

        return separator;
    }

    public void setSeparator(String separator) {

        this.separator = separator;
    }

    public LogCategory getCategory() {

        return category;
    }

    public void setCategory(LogCategory category) {

        this.category = category;
    }

    public String getDescription() {

        return description;
    }

    public void setDescription(String description) {

        this.description = description;
    }

    public String getMessage() {

        return message;
    }

    public void setMessage(String message) {

        this.message = message;
    }

    public boolean isLogFullPayload() {

        return logFullPayload;
    }

    public void setLogFullPayload(boolean logFullPayload) {

        this.logFullPayload = logFullPayload;
    }

    public boolean isLogMessageID() {

        return logMessageID;
    }

    public void setLogMessageID(boolean logMessageID) {

        this.logMessageID = logMessageID;
    }
}
