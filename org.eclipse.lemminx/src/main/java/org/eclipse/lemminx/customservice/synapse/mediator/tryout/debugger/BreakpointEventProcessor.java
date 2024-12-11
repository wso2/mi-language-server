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

package org.eclipse.lemminx.customservice.synapse.mediator.tryout.debugger;

import com.google.gson.JsonObject;
import org.eclipse.lemminx.customservice.synapse.debugger.entity.debuginfo.IDebugInfo;
import org.eclipse.lemminx.customservice.synapse.mediator.TryOutConstants;
import org.eclipse.lemminx.customservice.synapse.mediator.TryOutUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public class BreakpointEventProcessor {

    private static final Logger LOGGER = Logger.getLogger(BreakpointEventProcessor.class.getName());
    private static List<String>
            PROPERTY_CONTEXTS = List.of("synapse", "axis2", "axis2-client", "transport", "operation", "variable");
    private final DebugCommandClient commandClient;
    private final Object lock;
    private boolean isListeningForStepOver = false;
    private boolean isInputFetched = false;
    private boolean isDone = false;
    private boolean isFault = false;
    private List<String> inputResponse;
    private List<String> outputResponse;
    private IDebugInfo faultSequenceBreakpoint;

    public BreakpointEventProcessor(DebugCommandClient commandClient, Object lock) {

        this.commandClient = commandClient;

        this.lock = lock;
    }

    public void process(JsonObject eventData) {

        List<String> properties = getProperties();
        if (isDone) {
            return;
        }
        if (TryOutUtils.isExpectedBreakpoint(eventData, faultSequenceBreakpoint)) {
            isFault = true;
            isDone = true;
        }
        if (!isListeningForStepOver) {
            inputResponse = Collections.unmodifiableList(properties);
            isListeningForStepOver = true;
            isInputFetched = true;
            synchronized (lock) {
                lock.notifyAll();
            }
        } else {
            outputResponse = Collections.unmodifiableList(properties);
            isDone = true;
            isListeningForStepOver = false;
            synchronized (lock) {
                lock.notifyAll();
            }
            commandClient.sendResumeCommand();
        }
    }

    private List<String> getProperties() {

        List<String> properties = new ArrayList<>();
        for (String context : PROPERTY_CONTEXTS) {
            String property = getProperty(context);
            properties.add(property);
        }
        return properties;
    }

    private String getProperty(String context) {

        JsonObject property = new JsonObject();
        property.addProperty(TryOutConstants.COMMAND, TryOutConstants.GET);
        property.addProperty(TryOutConstants.COMMAND_ARGUMENT,
                TryOutConstants.VARIABLE.equals(context) ? TryOutConstants.VARIABLES : TryOutConstants.PROPERTIES);
        property.addProperty(TryOutConstants.CONTEXT, context);

        return commandClient.sendCommand(property.toString());
    }

    public boolean isDone() {

        return isDone;
    }

    public List<String> getInputResponse() {

        if (inputResponse == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(inputResponse);
    }

    public List<String> getOutputResponse() {

        if (outputResponse == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(outputResponse);
    }

    public void setFaultSequenceBreakpoint(IDebugInfo faultSequenceBreakpoint) {

        this.faultSequenceBreakpoint = faultSequenceBreakpoint;
    }

    public boolean isFault() {

        return isFault;
    }

    public boolean isInputFetched() {

        return isInputFetched;
    }

    public void reset() {

        isDone = false;
        isInputFetched = false;
        isListeningForStepOver = false;
        inputResponse = null;
        outputResponse = null;
        isFault = false;
    }
}
