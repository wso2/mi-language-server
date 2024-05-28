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

package org.eclipse.lemminx.customservice.synapse.api.generator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.APIFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.api.API;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.api.APIResource;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.endpoint.EndpointType;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.endpoint.NamedEndpoint;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.endpoint.wsdl.WSDLEndpoint;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.Header;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.HeaderScope;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.Property;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.PropertyMediatorType;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.PropertyScope;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.Respond;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core.call.Call;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.transformation.payload.MediaType;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.transformation.payload.PayloadFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.transformation.payload.PayloadFactoryFormat;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.transformation.payload.TemplateType;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.misc.common.Sequence;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.api.APISerializer;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.serializer.endpoint.EndpointSerializer;
import org.eclipse.lemminx.customservice.synapse.utils.Utils;
import org.wso2.soaptorest.SOAPToRESTConverter;
import org.wso2.soaptorest.exceptions.SOAPToRESTException;
import org.wso2.soaptorest.models.SOAPRequestElement;
import org.wso2.soaptorest.models.SOAPtoRESTConversionData;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import static org.wso2.soaptorest.utils.SOAPToRESTConstants.IF_PLACEHOLDER;

// Source: https://github.com/wso2/integration-studio/blob/v8.2.0/components/esb-tools/plugins/org.wso2.integrationstudio.artifact.synapse.api/src/org/wso2/integrationstudio/artifact/synapse/api/ui/wizard/SynapseAPICreationWizard.java

public class RestApiAdmin {

    private static final Logger LOGGER = Logger.getLogger(RestApiAdmin.class.getName());
    private static final String CREATE_FROM_SWAGGER = "create.api.from.swagger";
    private static final String CREATE_FROM_WSDL = "create.api.from.wsdl";
    private static final String TITLE = "title";
    private static final String FREEMARKER_IF = "#if";
    private static final String QUESTION_MARK_PLACEHOLDER = "questionPlaceholder";
    private static final String ATTRIBUTE_REGEX = "attributePlaceholder=\"(.*?)\"";
    private static final String SOAP_BODY_PREFIX = "<soapenv:Envelope xmlns:soapenv"
            + "=\"http://www.w3.org/2003/05/soap-envelope\">\r\n<soapenv:Header/>\r\n<soapenv:Body>\r\n";
    private static final String SOAP_BODY_POSTFIX = "</soapenv:Body>\r\n</soapenv:Envelope>\r\n";

    public GenerateAPIResponse createAPI(GenerateAPIParam param) {

        String apiName = param.apiName;
        String sourcePath = param.swaggerOrWsdlPath;
        String endpoint = param.wsdlEndpointName;
        boolean isPublishSwagger = param.isPublishSwagger;
        String mode = param.mode;

        return createAPI(apiName, sourcePath, endpoint, isPublishSwagger, mode);
    }

    public GenerateAPIResponse createAPI(String apiName, String sourcePath, String endpoint, boolean isPublishSwagger
            , String mode) {

        if (CREATE_FROM_SWAGGER.equalsIgnoreCase(mode)) {
            try {
                return createAPIFromSwagger(apiName, sourcePath);
            } catch (JsonProcessingException e) {
                LOGGER.log(Level.SEVERE, "Exception occurred while creating API from Swagger", e);
                return null;
            }
        } else if (CREATE_FROM_WSDL.equalsIgnoreCase(mode)) {
            try {
                return createAPIFromWSDL(apiName, endpoint, sourcePath);
            } catch (SOAPToRESTException e) {
                LOGGER.log(Level.SEVERE, "Exception occurred while converting SOAP to REST", e);
            } catch (MalformedURLException e) {
                LOGGER.log(Level.SEVERE, "Invalid WSDL URL", e);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Exception occurred while creating API from WSDL", e);
            }
        }
        return null;
    }

    private GenerateAPIResponse createAPIFromSwagger(String apiName, String swaggerPath) throws JsonProcessingException {

        File swaggerFile = new File(swaggerPath);
        String swaggerYaml = getSwaggerFileAsYAML(swaggerFile, apiName);

        String api = getSynapseAPIFromSwagger(swaggerYaml);
        return new GenerateAPIResponse(api);
    }

    private String getSynapseAPIFromSwagger(String swaggerYaml) throws JsonProcessingException {

        String swaggerString = GenericApiObjectDefinition.convertYamlToJson(swaggerYaml);
        JsonParser jsonParser = new JsonParser();
        JsonElement swaggerJson = jsonParser.parse(swaggerString);
        APIGenerator apiGenerator = new APIGenerator(swaggerJson.getAsJsonObject());
        String apiXml = apiGenerator.generateSynapseAPIXml();
        return apiXml;
    }

    private GenerateAPIResponse createAPIFromWSDL(String apiName, String endpoint, String sourcePath) throws SOAPToRESTException, MalformedURLException, JsonProcessingException, TransformerException {

        URL url = new URL(sourcePath);
        SOAPtoRESTConversionData soaPtoRESTConversionData = SOAPToRESTConverter.getSOAPtoRESTConversionData(url,
                apiName,
                "1.0.0");
        String synapseWSDLEndpointName = apiName + "_SOAP_ENDPOINT";

        NamedEndpoint wsdlEndpoint = new NamedEndpoint();
        wsdlEndpoint.setType(EndpointType.WSDL_ENDPOINT);
        wsdlEndpoint.setName(synapseWSDLEndpointName);

        WSDLEndpoint wsdlEndpointData = new WSDLEndpoint();
        if (endpoint == null) {
            endpoint = "http://yoursoapep.com?wsdl";
        }
        if (!endpoint.endsWith("wsdl")) {
            endpoint = endpoint.concat("?wsdl");
        }
        wsdlEndpointData.setUri(endpoint);
        wsdlEndpointData.setService(soaPtoRESTConversionData.getSoapService());
        wsdlEndpointData.setPort(soaPtoRESTConversionData.getSoapPort());
        wsdlEndpoint.setWsdl(wsdlEndpointData);

        String swaggerYaml = soaPtoRESTConversionData.getOASString();
        String apiXml = getSynapseAPIFromSwagger(swaggerYaml);
        APIFactory apiFactory = new APIFactory();
        API api = (API) apiFactory.create(Utils.getDOMDocument(apiXml).getDocumentElement());

        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

        Set<Map.Entry<String, SOAPRequestElement>> soapElementEntry =
                soaPtoRESTConversionData.getAllSOAPRequestBodies();
        for (Map.Entry<String, SOAPRequestElement> requestElementEntry : soapElementEntry) {
            SOAPRequestElement soapRequestElement = requestElementEntry.getValue();

            APIResource[] resources = api.getResource();
            for (APIResource resource : resources) {
                String resourcePath = resource.getResourcePath();
                if (resourcePath.toLowerCase().contains(requestElementEntry.getKey().toLowerCase())) {
                    StringWriter writer = new StringWriter();
                    transformer.transform(new DOMSource(soapRequestElement.getSoapRequestBody()),
                            new StreamResult(writer));
                    String soapPayload = writer.getBuffer().toString();
                    soapPayload = soapPayload.replaceAll(IF_PLACEHOLDER, FREEMARKER_IF)
                            .replaceAll(QUESTION_MARK_PLACEHOLDER, "?");
                    Pattern pattern = Pattern.compile(ATTRIBUTE_REGEX);
                    Matcher matcher = pattern.matcher(soapPayload);
                    soapPayload = matcher.replaceAll("$1");
                    Sequence sequence = new Sequence();

                    if (StringUtils.isNotBlank(soapRequestElement.getSoapAction())) {
                        Header headerMediator = new Header();
                        headerMediator.setName("SOAPAction");
                        headerMediator.setScope(HeaderScope.TRANSPORT);
                        headerMediator.setValue(soapRequestElement.getSoapAction());
                        headerMediator.setAction("set");
                        sequence.addToMediatorList(headerMediator);
                    }
                    Property propertyMediator1 = new Property();
                    propertyMediator1.setName("REST_URL_POSTFIX");
                    propertyMediator1.setScope(PropertyScope.AXIS2);
                    propertyMediator1.setAction("remove");
                    sequence.addToMediatorList(propertyMediator1);

                    PayloadFactory payloadFactoryMediator = new PayloadFactory();
                    payloadFactoryMediator.setTemplateType(TemplateType.FREE_MARKER);
                    payloadFactoryMediator.setMediaType(MediaType.xml);
                    PayloadFactoryFormat payloadFactoryFormat = new PayloadFactoryFormat();
                    payloadFactoryFormat.setContent(SOAP_BODY_PREFIX + soapPayload + SOAP_BODY_POSTFIX);
                    payloadFactoryMediator.setFormat(payloadFactoryFormat);
                    sequence.addToMediatorList(payloadFactoryMediator);

                    Property propertyMediator2 = new Property();
                    propertyMediator2.setName("messageType");
                    propertyMediator2.setScope(PropertyScope.AXIS2);
                    propertyMediator2.setValue("application/soap+xml");
                    propertyMediator2.setType(PropertyMediatorType.STRING);
                    sequence.addToMediatorList(propertyMediator2);

                    NamedEndpoint indirectEndpoint = new NamedEndpoint();
                    indirectEndpoint.setKey(synapseWSDLEndpointName);

                    Call callMediator = new Call();
                    callMediator.setEndpoint(indirectEndpoint);
                    sequence.addToMediatorList(callMediator);

                    Property propertyMediator3 = new Property();
                    propertyMediator3.setName("messageType");
                    propertyMediator3.setScope(PropertyScope.AXIS2);
                    propertyMediator3.setValue("application/json");
                    propertyMediator3.setType(PropertyMediatorType.STRING);
                    sequence.addToMediatorList(propertyMediator3);

                    Respond respondMediator = new Respond();
                    sequence.addToMediatorList(respondMediator);
                    resource.setInSequence(sequence);

                    resource.setOutSequence(new Sequence());
                }
            }
        }

        String genApi = APISerializer.serializeAPI(api);
        String genEndpoint = EndpointSerializer.serializeEndpoint(wsdlEndpoint).toString();
        return new GenerateAPIResponse(genApi, genEndpoint);
    }

    private String getSwaggerFileAsYAML(File swaggerFile, String apiName) {

        String swaggerContent = "";
        try {
            swaggerContent = new String(Files.readAllBytes(Paths.get(swaggerFile.getAbsolutePath())));
            if (FilenameUtils.getExtension(swaggerFile.getAbsolutePath()).equals("json")) {
                swaggerContent = convertJSONtoYaml(swaggerContent);
            }
            swaggerContent = updateNameInSwagger(apiName, swaggerContent);

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Exception occurred while reading swagger file", e);
        } catch (APIException e) {
            LOGGER.log(Level.SEVERE, "Exception occurred while updating swagger name", e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Exception occurred while converting swagger JSON to YAML", e);
        }
        return swaggerContent;
    }

    public String updateNameInSwagger(String newName, String swagger) throws APIException {

        JsonParser parser = new JsonParser();
        JsonElement jsonElement;
        try {
            jsonElement = parser.parse(swagger);
            Boolean openApi = jsonElement.getAsJsonObject().has("openapi");
            if (!openApi) {
                LOGGER.log(Level.SEVERE, "Provided swagger is not OpenApi 3.0");
            } else {
                JsonObject infoObject = jsonElement.getAsJsonObject().get("info").getAsJsonObject();
                infoObject.remove(TITLE);
                infoObject.add(TITLE, new JsonPrimitive(newName));
                return jsonElement.toString();
            }
        } catch (JsonSyntaxException ex) {
            // neglect the error - treat as YAML
            Yaml yaml = new Yaml();
            Map<String, Object> obj = yaml.load(swagger);
            Map<String, Object> infoMap = (Map<String, Object>) obj.get("info");
            infoMap.remove(TITLE);
            infoMap.put(TITLE, newName);

            DumperOptions options = new DumperOptions();
            options.setIndent(2);
            options.setPrettyFlow(true);
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            Yaml output = new Yaml(options);
            return output.dump(obj);
        }
        return null;
    }

    public static String convertJSONtoYaml(String jsonSource) throws Exception {

        try {
            Yaml yaml = new Yaml();
            Map<String, Object> obj = yaml.load(jsonSource);
            DumperOptions options = new DumperOptions();
            options.setIndent(2);
            options.setPrettyFlow(true);
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            Yaml output = new Yaml(options);
            return output.dump(obj);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Exception while converting json to yaml", e);
            throw new Exception(e);
        }
    }
}
