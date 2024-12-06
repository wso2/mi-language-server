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

import org.eclipse.lemminx.customservice.synapse.mediator.TryOutConstants;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DebugCommandClient {

    private static final Logger LOGGER = Logger.getLogger(DebugCommandClient.class.getName());

    private static final String HOST = TryOutConstants.LOCALHOST;
    private int port;
    private Socket socket;

    public DebugCommandClient(int port) {

        this.port = port;
    }

    public void connect() {

        try {
            socket = new Socket(HOST, port);
            socket.setSoTimeout(10000); // Set a timeout for reading from the socket
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, String.format("Failed to connect to the server using port: %d", port), e);
        }
    }

    public String sendCommand(String message) {

        try {
            var outputStream = new BufferedOutputStream(socket.getOutputStream());

            message += "\n";

            outputStream.write(message.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();

            var inputStream =
                    new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            return inputStream.readLine();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to send command", e);
        }
        return null;
    }

    public void sendResumeCommand() {

        sendCommand(TryOutConstants.RESUME_COMMAND);
    }

    public boolean isConnected() {

        if (socket == null) {
            return false;
        }
        return socket.isConnected();
    }

    public void close() throws IOException {

        socket.close();
    }
}
