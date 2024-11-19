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

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.eclipse.lemminx.customservice.synapse.mediator.TryOutConstants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DebugEventClient extends Thread {

    private static final Logger LOGGER = Logger.getLogger(DebugEventClient.class.getName());
    private static final String HOST = "localhost";
    private int port;
    private Socket socket;
    private final BreakpointEventProcessor breakpointEventProcessor;
    private boolean isDebuggerActive = false;

    public DebugEventClient(int port, BreakpointEventProcessor breakpointEventProcessor) {

        this.port = port;
        this.breakpointEventProcessor = breakpointEventProcessor;
    }

    public void connect() {

        try {
            socket = new Socket(HOST, port);
            isDebuggerActive = true;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, String.format("Failed to connect to the server using port: %d", port), e);
        }
    }

    @Override
    public void run() {

        listenForEvent();
    }

    private void listenForEvent() {

        while (isDebuggerActive) {
            String event = listen();
            Gson gson = new Gson();
            JsonObject eventJson = gson.fromJson(event, JsonObject.class);
            if (eventJson.has(TryOutConstants.EVENT) && eventJson.get(TryOutConstants.EVENT) != null &&
                    TryOutConstants.BREAKPOINT.equals(eventJson.get(TryOutConstants.EVENT).getAsString())) {
                breakpointEventProcessor.process(eventJson);
            }
        }
        try {
            socket.close();
        } catch (IOException e) {
            LOGGER.info("Failed to close the socket: " + e.getMessage());
        }
    }

    public String listen() {

        try {
            var inputStream =
                    new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            String event;

            if ((event = inputStream.readLine()) != null) {
                return event;
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to listen for events", e);
        }
        return null;
    }

    public boolean isConnected() {

        if (socket == null) {
            return false;
        }
        return socket.isConnected();
    }

    public void close() {

        isDebuggerActive = false;
    }
}
