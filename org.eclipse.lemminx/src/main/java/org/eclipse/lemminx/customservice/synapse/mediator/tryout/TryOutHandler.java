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

package org.eclipse.lemminx.customservice.synapse.mediator.tryout;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.eclipse.lemminx.commons.BadLocationException;
import org.eclipse.lemminx.customservice.synapse.debugger.DebuggerHelper;
import org.eclipse.lemminx.customservice.synapse.debugger.entity.Breakpoint;
import org.eclipse.lemminx.customservice.synapse.debugger.entity.StepOverInfo;
import org.eclipse.lemminx.customservice.synapse.debugger.entity.debuginfo.IDebugInfo;
import org.eclipse.lemminx.customservice.synapse.mediator.TryOutConstants;
import org.eclipse.lemminx.customservice.synapse.mediator.TryOutUtils;
import org.eclipse.lemminx.customservice.synapse.mediator.tryout.debugger.BreakpointEventProcessor;
import org.eclipse.lemminx.customservice.synapse.mediator.tryout.debugger.DebugCommandClient;
import org.eclipse.lemminx.customservice.synapse.mediator.tryout.debugger.DebugEventClient;
import org.eclipse.lemminx.customservice.synapse.mediator.tryout.pojo.ArtifactDeploymentException;
import org.eclipse.lemminx.customservice.synapse.mediator.tryout.pojo.Edit;
import org.eclipse.lemminx.customservice.synapse.mediator.tryout.pojo.InvalidConfigurationException;
import org.eclipse.lemminx.customservice.synapse.mediator.tryout.pojo.MediatorInfo;
import org.eclipse.lemminx.customservice.synapse.mediator.tryout.pojo.MediatorTryoutRequest;
import org.eclipse.lemminx.customservice.synapse.mediator.tryout.pojo.MediatorTryoutInfo;
import org.eclipse.lemminx.customservice.synapse.mediator.tryout.pojo.NoBreakpointHitException;
import org.eclipse.lemminx.customservice.synapse.mediator.tryout.pojo.Property;
import org.eclipse.lemminx.customservice.synapse.mediator.tryout.server.MIServer;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.SyntaxTreeGenerator;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.NamedSequence;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.STNode;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.api.API;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.api.APIResource;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.inbound.InboundEndpoint;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.Mediator;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.SequenceMediator;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.misc.common.Sequence;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.api.APISerializer;
import org.eclipse.lemminx.customservice.synapse.utils.Constant;
import org.eclipse.lemminx.customservice.synapse.utils.Utils;
import org.eclipse.lemminx.dom.DOMDocument;
import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TryOutHandler {

    private static final Logger LOGGER = Logger.getLogger(TryOutHandler.class.getName());
    private static final Path TEMP_FOLDER_PATH =
            Path.of(System.getProperty("user.home"), ".wso2-mi", "tryout");
    private static final Path DEFAULT_FAULT_SEQUENCE_PATH = Path.of("repository", "deployment", "server",
            "synapse-configs", "default", "sequences", "fault.xml");
    private static final String MI_HOST = "localhost";
    private static final Executor executor = Executors.newSingleThreadExecutor();
    private final String projectUri;
    private MIServer server;
    private DebugCommandClient commandClient;
    private DebugEventClient eventClient;
    private List<JsonObject> activeBreakpoints = new ArrayList<>();
    private boolean isServerStarted = false;
    private BreakpointEventProcessor breakpointEventProcessor;
    private boolean isFault = false;

    public TryOutHandler(String projectUri) {

        this.projectUri = projectUri;
    }

    public void initAsync(String serverPath) {

        executor.execute(() -> {
            try {
                init(serverPath);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error while initializing the tryout handler", e);
            }
        });
    }

    public synchronized void init(String serverPath) {

        //TODO: copy the MI in the project and use that
        server = new MIServer(Path.of(serverPath));
        server.startServer();
        commandClient = new DebugCommandClient(server.getDebuggerCommandPort());
        breakpointEventProcessor = new BreakpointEventProcessor(commandClient, this);
        eventClient =
                new DebugEventClient(server.getDebuggerEventPort(),
                        breakpointEventProcessor);
        commandClient.connect();
        eventClient.connect();
        eventClient.start();
        server.waitForServerStartup();
        synchronized (this) {
            isServerStarted = server.isStarted();
            isFault = !eventClient.isConnected() || !commandClient.isConnected() || !isServerStarted;
            this.notifyAll();
        }
    }

    public synchronized MediatorTryoutInfo handle(MediatorTryoutRequest request) throws InterruptedException {

        while (!isServerStarted) {
            wait();
        }
        if (isFault) {
            return new MediatorTryoutInfo("Try-Out feature not activated.");
        }
        try {
            Path editFilePath = TryOutUtils.cloneAndPreprocessProject(projectUri, request.getFile(), request.getEdits(),
                    TEMP_FOLDER_PATH);
            boolean needStepOver = checkNeedStepOver(request, editFilePath);

            // Create an API to invoke if the given file is not an API.
            String serviceUrl = createApiForSequenceInvocation(request);
            String serviceMethod = TryOutConstants.POST;

            server.deployProject(TEMP_FOLDER_PATH.toString(), request.getFile());

            // Get the mediator info
            registerBreakpoints(request, editFilePath);
            registerFaultSequenceBreakpoint(server.getServerPath().resolve(DEFAULT_FAULT_SEQUENCE_PATH));
            if (serviceUrl == null) {
                serviceUrl = getServiceUrl();
                serviceMethod = getServiceMethod();
            }
            sendRequest(serviceUrl, serviceMethod, request.getInputPayload());
            waitForMediatorInfo(needStepOver);
            if (breakpointEventProcessor.isFault()) {
                return createFaultTryOutInfo();
            }
            return getMediatorTryoutInfo(needStepOver);
        } catch (IOException | InvalidConfigurationException | ArtifactDeploymentException e) {
            LOGGER.log(Level.SEVERE, "Error while handling the tryout", e);
            return new MediatorTryoutInfo(e.getMessage());
        } catch (NoBreakpointHitException e) {
            LOGGER.log(Level.INFO,
                    "Breakpoint not hit by the mediator. Consider adjusting the payload or retrying.");
            return new MediatorTryoutInfo(e.getMessage());
        } finally {
            breakpointEventProcessor.reset();
            executor.execute(() -> {
                try {
                    Utils.deleteDirectory(TEMP_FOLDER_PATH);
                    server.deleteDeployedFiles();
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, "Error while deleting the temp folder", e);
                }
            });
        }
    }

    private MediatorTryoutInfo createFaultTryOutInfo() {

        List<String> inputProperties = breakpointEventProcessor.getInputResponse();
        List<String> outputProperties = breakpointEventProcessor.getOutputResponse();
        MediatorInfo inputInfo = createMediatorInfo(inputProperties);
        if (outputProperties != null) {
            String errorMsg = getErrorMessage(createMediatorInfo(outputProperties));
            return new MediatorTryoutInfo(inputInfo, errorMsg);
        }
        return new MediatorTryoutInfo(getErrorMessage(inputInfo));
    }

    private boolean checkNeedStepOver(MediatorTryoutRequest request, Path editFilePath) throws InvalidConfigurationException {

        try {
            DOMDocument document = Utils.getDOMDocument(editFilePath.toFile());
            List<String> lastMediatorList = List.of("send", "respond", "drop", "loopback");
            int line = request.getLine();
            int column = request.getColumn();
            int offset = document.offsetAt(new Position(line, column + 1));
            DOMNode currentNode = document.findNodeAt(offset);
            if (currentNode != null) {
                DOMNode parentNode = currentNode.getParentNode();
                DOMNode lastNode = parentNode.getChildren().get(parentNode.getChildren().size() - 1);
                if (lastNode == currentNode) {
                    if (lastMediatorList.contains(currentNode.getNodeName())) {
                        return false;
                    }

                    // Add a log mediator if the trying out mediator is the last mediator
                    addLastMediator(document, lastNode, editFilePath);
                }
                return true;
            }
            return false;
        } catch (IOException | BadLocationException e) {
            throw new InvalidConfigurationException("Invalid synapse configuration");
        }
    }

    private void registerBreakpoints(MediatorTryoutRequest request, Path editFilePath)
            throws InterruptedException, InvalidConfigurationException {

        DebuggerHelper debuggerHelper = new DebuggerHelper(editFilePath.toString());
        Breakpoint breakpoint = new Breakpoint(request.getLine(), request.getColumn());
        List<Breakpoint> breakpoints = new ArrayList<>(List.of(breakpoint));
        StepOverInfo stepOverInfo = debuggerHelper.getStepOverBreakpoints(breakpoint);
        if (stepOverInfo != null) {
            breakpoints.addAll(stepOverInfo.getStepOverBreakpoints());
        }
        List<IDebugInfo> debugInfo = debuggerHelper.generateDebugInfo(breakpoints);
        registerBreakpoints(debugInfo);
        breakpointEventProcessor.setTryoutMediatorBreakpoint(debugInfo.get(0));

        while (activeBreakpoints.size() < breakpoints.size()) {
            synchronized (this) {
                wait(1300);
            }
        }
    }

    private void registerFaultSequenceBreakpoint(Path path) throws InvalidConfigurationException, IOException {

        DOMDocument document = Utils.getDOMDocument(path.toFile());
        NamedSequence node = (NamedSequence) SyntaxTreeGenerator.buildTree(document.getDocumentElement());
        if (node.getMediatorList() != null) {
            Mediator firstMediator = node.getMediatorList().get(0);
            Breakpoint breakpoint = new Breakpoint(firstMediator.getRange().getStartTagRange().getStart().getLine(),
                    firstMediator.getRange().getStartTagRange().getStart().getCharacter());
            DebuggerHelper debuggerHelper = new DebuggerHelper(path.toString());
            List<Breakpoint> breakpoints = new ArrayList<>(List.of(breakpoint));
            List<IDebugInfo> debugInfo = debuggerHelper.generateDebugInfo(breakpoints);
            breakpointEventProcessor.setFaultSequenceBreakpoint(debugInfo.get(0));
            registerBreakpoints(debugInfo);
        }
    }

    private void registerBreakpoints(List<IDebugInfo> debugInfo) throws InvalidConfigurationException {

        for (IDebugInfo info : debugInfo) {
            if (info != null) {
                JsonObject command = constructCommand(info, "set");
                String result = sendCommand(command);
                if (result != null && result.contains("successful")) {
                    activeBreakpoints.add(command);
                } else {
                    throw new InvalidConfigurationException("Failed to register breakpoint");
                }
            }
        }
    }

    private String getErrorMessage(MediatorInfo info) {

        List<Property> synapseProperties = info.getSynapse();
        for (Property property : synapseProperties) {
            if (property.getKey().equals("ERROR_MESSAGE")) {
                return property.getValue();
            }
        }
        return "Error occurred while trying out the mediator";
    }

    private void addLastMediator(DOMDocument document, DOMNode lastNode, Path editFilePath)
            throws BadLocationException, IOException {

        String xml = "<log category=\"INFO\" level=\"simple\"/>";
        int insertOffset = lastNode.getEnd() + 1;
        Edit edit = new Edit(xml, new Range(document.positionAt(insertOffset), document.positionAt(insertOffset)));
        TryOutUtils.doEdit(edit, editFilePath);
    }

    private synchronized void waitForMediatorInfo(boolean needStepOver) throws NoBreakpointHitException {

        if (!needStepOver) {
            cleanUp();
            return;
        }
        waitForBreakpointHit();
    }

    private synchronized void waitForBreakpointHit() throws NoBreakpointHitException {

        int count = 0;
        while (!breakpointEventProcessor.isDone()) {
            count++;
            if (count > 5) {
                cleanUp();
                throw new NoBreakpointHitException("The given payload did not hit the breakpoint");
            }
            try {
                wait(1500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new NoBreakpointHitException("The given payload did not hit the breakpoint", e);
            }
        }
    }

    private void cleanUp() {

        commandClient.sendResumeCommand();
        clearBreakpoints();
        server.deleteDeployedFiles();
    }

    private String getServiceUrl() {

        JsonObject apiObj =
                activeBreakpoints.get(0).get(Constant.SEQUENCE).getAsJsonObject().get(Constant.API).getAsJsonObject();
        String apiKey = apiObj.get(TryOutConstants.API_KEY).getAsString();
        JsonObject resourceObj = apiObj.get(Constant.RESOURCE).getAsJsonObject();
        JsonElement urlMapping = resourceObj.get(Constant.URL_MAPPING);
        JsonElement uriMapping = resourceObj.get(TryOutConstants.URI_MAPPING);
        StringBuilder url = new StringBuilder();
        url.append(TryOutConstants.HTTP_PREFIX).append(MI_HOST).append(":").append(server.getServerPort())
                .append(TryOutConstants.SLASH).append(apiKey);
        if (urlMapping != null && !urlMapping.isJsonNull()) {
            url.append(urlMapping.getAsString());
        } else if (uriMapping != null && !uriMapping.isJsonNull()) {
            url.append(uriMapping.getAsString());
        } else {
            url.append(TryOutConstants.SLASH);
        }
        return url.toString();
    }

    private String getServiceMethod() {

        return activeBreakpoints.get(0).get(Constant.SEQUENCE).getAsJsonObject().get(Constant.API).getAsJsonObject()
                .get(Constant.RESOURCE).getAsJsonObject().get(Constant.METHOD).getAsString();
    }

    private String createApiForSequenceInvocation(MediatorTryoutRequest request) throws InvalidConfigurationException {

        if (isApi(request.getFile())) {
            return null;
        }
        try {
            DOMDocument document = Utils.getDOMDocument(new File(request.getFile()));
            STNode node = SyntaxTreeGenerator.buildTree(document.getDocumentElement());
            if (node != null) {
                String apiName = node.getTag() + "_" + UUID.randomUUID();
                API api = new API();
                api.setName(apiName);
                api.setContext(TryOutConstants.SLASH + apiName);
                APIResource resource = new APIResource();
                resource.setMethods(new String[]{"POST"});
                resource.setUrlMapping(TryOutConstants.SLASH);
                api.setResource(new APIResource[]{resource});
                Sequence sequence = new Sequence();
                resource.setInSequence(sequence);
                SequenceMediator sequenceMediator = new SequenceMediator();
                sequence.addToMediatorList(sequenceMediator);
                switch (node.getTag()) {
                    case Constant.SEQUENCE:
                        sequenceMediator.setKey(((NamedSequence) node).getName());
                        break;
                    case Constant.INBOUND_ENDPOINT:
                        sequenceMediator.setKey(((InboundEndpoint) node).getSequence());
                        break;
                    default:
                        return null;
                }
                String apiContent = APISerializer.serializeAPI(api);
                Path apiPath = Path.of(TEMP_FOLDER_PATH.toString(), "src", "main", "wso2mi", "artifacts", "apis",
                        apiName + ".xml");
                Utils.writeToFile(apiPath.toString(), apiContent);
                return TryOutConstants.HTTP_PREFIX + TryOutConstants.LOCALHOST + ":" + server.getServerPort() + "/" +
                        apiName;
            }
        } catch (IOException e) {
            throw new InvalidConfigurationException("Error while creating the API for the sequence", e);
        }
        return null;
    }

    private boolean isApi(String file) {

        Path filePath = Path.of(file);
        Path relativePath = Path.of(projectUri).relativize(filePath);
        Path apiRelativePath = Path.of("src", "main", "wso2mi", "artifacts", "apis");
        return relativePath.startsWith(apiRelativePath);
    }

    private MediatorTryoutInfo getMediatorTryoutInfo(boolean needStepOver) {

        MediatorInfo inputInfo = createMediatorInfo(breakpointEventProcessor.getInputResponse());
        if (!needStepOver) {
            return new MediatorTryoutInfo(inputInfo, inputInfo);
        }
        return new MediatorTryoutInfo(inputInfo, createMediatorInfo(breakpointEventProcessor.getOutputResponse()));
    }

    private MediatorInfo createMediatorInfo(List<String> properties) {

        List<JsonObject> parsedProperties = parseProperties(properties);

        MediatorInfo mediatorInfo = new MediatorInfo();
        for (JsonObject property : parsedProperties) {
            if (property.has(TryOutConstants.SYNAPSE_PROPERTIES)) {
                mediatorInfo.addSynapseProperties(
                        parseProperties(property.getAsJsonObject(TryOutConstants.SYNAPSE_PROPERTIES)));
            } else if (property.has(TryOutConstants.AXIS2_PROPERTIES)) {
                mediatorInfo.addAxis2Properties(
                        parseProperties(property.getAsJsonObject(TryOutConstants.AXIS2_PROPERTIES)));
                mediatorInfo.setPayload(
                        property.getAsJsonObject(TryOutConstants.AXIS2_PROPERTIES).get(TryOutConstants.ENVELOPE)
                                .getAsJsonPrimitive());
            } else if (property.has(TryOutConstants.AXIS2_CLIENT_PROPERTIES)) {
                mediatorInfo.addAxis2ClientProperties(
                        parseProperties(property.getAsJsonObject(TryOutConstants.AXIS2_CLIENT_PROPERTIES)));
            } else if (property.has(TryOutConstants.AXIS2_TRANSPORT_PROPERTIES)) {
                mediatorInfo.addAxis2TransportProperties(
                        parseProperties(property.getAsJsonObject(TryOutConstants.AXIS2_TRANSPORT_PROPERTIES)));
            } else if (property.has(TryOutConstants.AXIS2_OPERATION_PROPERTIES)) {
                mediatorInfo.addAxis2OperationProperties(
                        parseProperties(property.getAsJsonObject(TryOutConstants.AXIS2_OPERATION_PROPERTIES)));
            }
        }
        return mediatorInfo;
    }

    private List<Property> parseProperties(JsonObject propertiesJson) {

        List<Property> properties = new ArrayList<>();
        for (String key : propertiesJson.keySet()) {
            Property property;
            if (propertiesJson.get(key).isJsonPrimitive()) {
                property = new Property(key, propertiesJson.get(key).getAsString());
            } else {
                property = new Property(key, parseProperties(propertiesJson.getAsJsonObject(key)));
            }
            properties.add(property);
        }
        return properties;
    }

    private List<JsonObject> parseProperties(List<String> properties) {

        List<JsonObject> parsedProperties = new ArrayList<>();
        for (String response : properties) {
            Gson gson = new Gson();
            JsonObject eventJson = gson.fromJson(response, JsonObject.class);
            parsedProperties.add(eventJson);
        }
        return parsedProperties;
    }

    public void clearBreakpoints() {

        Iterator<JsonObject> iterator = activeBreakpoints.iterator();
        while (iterator.hasNext()) {
            JsonObject command = iterator.next();
            command.addProperty("command", "clear");
            sendCommand(command);
            iterator.remove();
        }
    }

    private JsonObject constructCommand(IDebugInfo info, String action) {

        JsonObject command = info.toJson().getAsJsonObject();
        command.addProperty("command", action);
        command.addProperty("command-argument", "breakpoint");
        return command;
    }

    public String sendCommand(JsonObject command) {

        return commandClient.sendCommand(command.toString());

    }

    public void sendRequest(String url, String methodType, String inputPayload) throws InvalidConfigurationException {

        try (Socket socket = new Socket(MI_HOST, server.getServerPort())) {

            OutputStream outputStream = socket.getOutputStream();
            // TODO: support all contentTypes
            String contentType = "application/json";
            StringBuilder request = new StringBuilder(methodType + " " + url + " HTTP/1.1\r\n" +
                    "Host: " + MI_HOST + "\r\n" +
                    "Connection: close\r\n");

            if (TryOutConstants.POST.equalsIgnoreCase(methodType)) {
                if (inputPayload != null && !inputPayload.isEmpty()) {
                    request.append("Content-Type: ").append(contentType).append("\r\n")
                            .append("Content-Length: ").append(inputPayload.getBytes(StandardCharsets.UTF_8).length)
                            .append("\r\n")
                            .append("\r\n")
                            .append(inputPayload);
                } else {
                    // Empty body for POST requests
                    request.append("Content-Length: 0\r\n\r\n");
                }
            } else {
                request.append("\r\n");
            }

            outputStream.write(request.toString().getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (IOException e) {
            throw new InvalidConfigurationException("Error while executing the mediator");
        }
    }

    public void close() {

        try {
            commandClient.close();
            eventClient.close();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error while closing the clients", e);
        }
    }
}
