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

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DebugEventClient extends Thread {

    private static final Logger LOGGER = Logger.getLogger(DebugEventClient.class.getName());
    private static final String HOST = TryOutConstants.LOCALHOST;
    private int port = TryOutConstants.DEFAULT_DEBUGGER_EVENT_PORT;
    private Socket socket;
    private final BreakpointEventProcessor breakpointEventProcessor;
    private boolean isDebuggerActive = false;

    public DebugEventClient(BreakpointEventProcessor breakpointEventProcessor) {

        this.breakpointEventProcessor = breakpointEventProcessor;
    }

    public void connect() {

        try {
            socket = new Socket(HOST, port);
            socket.setReceiveBufferSize(65536);
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
    }

    public String listen() {

        try {
            InputStream inputStream = socket.getInputStream();
            byte[] tempBuffer = new byte[1024];
            StringBuilder buffer = new StringBuilder();
            int bytesRead;

            while ((bytesRead = inputStream.read(tempBuffer)) != -1) {
                String receivedData = new String(tempBuffer, 0, bytesRead, StandardCharsets.UTF_8);
                buffer.append(receivedData);

                int delimiterIndex;
                while ((delimiterIndex = buffer.indexOf("\n")) != -1) {
                    String event = buffer.substring(0, delimiterIndex).trim();
                    buffer.delete(0, delimiterIndex + 1);
                    return event;
                }
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

    public void close() throws IOException {

        isDebuggerActive = false;
        if (socket != null) {
            socket.close();
        }
    }
}
