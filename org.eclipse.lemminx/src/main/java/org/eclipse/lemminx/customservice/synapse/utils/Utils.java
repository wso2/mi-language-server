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

package org.eclipse.lemminx.customservice.synapse.utils;

import com.github.fge.jackson.JsonLoader;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.lemminx.commons.TextDocument;
import org.eclipse.lemminx.customservice.synapse.connectors.ConnectorHolder;
import org.eclipse.lemminx.customservice.synapse.connectors.entity.Connector;
import org.eclipse.lemminx.customservice.synapse.connectors.entity.ConnectorAction;
import org.eclipse.lemminx.customservice.synapse.directoryTree.legacyBuilder.utils.ProjectType;
import org.eclipse.lemminx.customservice.synapse.parser.OverviewPageDetailsResponse;
import org.eclipse.lemminx.customservice.synapse.parser.pom.PomParser;
import org.eclipse.lemminx.dom.DOMAttr;
import org.eclipse.lemminx.dom.DOMDocument;
import org.eclipse.lemminx.dom.DOMElement;
import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.dom.DOMParser;
import org.eclipse.lsp4j.InitializeParams;
import org.w3c.dom.Node;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class Utils {

    private static final Logger logger = Logger.getLogger(Utils.class.getName());
    private static FileSystem fileSystem;
    private static final MustacheFactory mustacheFactory = new SynapseMustacheFactory();

    /**
     * Get the inline string of the given node
     *
     * @param node the node
     * @return the inline string of the given node
     */
    public static String getInlineString(DOMNode node) {

        return getInlineString(node, Boolean.TRUE);
    }

    /**
     * Get the inline string of the given node
     *
     * @param node                  the node
     * @param requiresXmlUnescaping whether to unescape the xml content
     * @return the inline string of the given node
     */
    public static String getInlineString(DOMNode node, boolean requiresXmlUnescaping) {

        String inline = "";
        if (node != null) {
            if (node.isCDATA()) {
                inline = "<![CDATA[" + node.getTextContent() + "]]>";
            } else if (node.isText()) {
                inline = requiresXmlUnescaping ? unescapeXml(node.getTextContent()) : node.getTextContent();
            } else if (node instanceof DOMElement) {
                if (((DOMElement) node).isSelfClosed()) {
                    inline = "<" + node.getNodeName().concat(getAttributeXmlString(node)) + "/>";
                } else if (!((DOMElement) node).isOrphanEndTag()) {
                    inline = "<" + node.getNodeName().concat(getAttributeXmlString(node)) + ">";
                    List<DOMNode> children = node.getChildren();
                    if (children != null && !children.isEmpty()) {
                        if (children.get(0) instanceof DOMElement) {
                            inline += "\n";
                        }
                        for (DOMNode child : children) {
                            inline += getInlineString(child);
                            if (child instanceof DOMElement) {
                                inline += "\n";
                            }
                        }
                    }
                    inline += "</" + StringEscapeUtils.escapeXml(node.getNodeName()) + ">";
                }
            }
        }
        return inline;
    }

    private static String getAttributeXmlString(DOMNode node) {

        String xmlString = "";
        List<DOMAttr> children = node.getAttributeNodes();
        if (children != null && !children.isEmpty()) {
            // Add a space before the attribute
            xmlString = " ";
            for (DOMAttr child : children) {
                xmlString += child.getName() + "=\"" + unescapeXml(child.getValue()) + "\" ";
            }
        }
        return xmlString;
    }

    public static int parseInt(String number) {

        int value = -1;
        try {
            value = Integer.parseInt(number);
        } catch (NumberFormatException e) {
            //ignore
        }
        return value;
    }

    public static DOMDocument getDOMDocument(File file) throws IOException {

        Path path = file.toPath();
        String text = Files.readString(path);
        TextDocument document = new TextDocument(text, path.toUri().toString());
        DOMDocument domDocument = DOMParser.getInstance().parse(document, null);
        return domDocument;
    }

    /**
     * Get the DOM document from the given xml content
     *
     * @param content the xml content
     * @return the DOM document for the given xml content
     */
    public static DOMDocument getDOMDocument(String content) {

        TextDocument document = new TextDocument(content, "temp");
        DOMDocument domDocument = DOMParser.getInstance().parse(document, null);
        return domDocument;
    }

    public static DOMElement getRootElementFromConfigXml(DOMNode document) {

        DOMElement rootElement = null;
        for (int i = 0; i < document.getChildren().size(); i++) {
            String elementName = document.getChild(i).getNodeName();
            if (containsIgnoreCase(Constant.SYNAPSE_CONFIG_ELEMENTS, elementName)) {
                rootElement = (DOMElement) document.getChild(i);
                break;
            }
        }
        return rootElement;
    }

    public static boolean containsIgnoreCase(List<String> list, String elementName) {

        return list.stream().anyMatch(s -> s.equalsIgnoreCase(elementName));
    }

    public static boolean isLegacyProject(String path) {

        String dotProjectPath = path + File.separator + Constant.DOT_PROJECT;
        File dotProjectFile = new File(dotProjectPath);
        if (dotProjectFile != null && dotProjectFile.exists()) {
            try {
                DOMDocument projectDOM = Utils.getDOMDocument(dotProjectFile);
                DOMNode descriptionNode = findDescriptionNode(projectDOM);
                if (descriptionNode != null) {
                    DOMNode naturesNode = findNaturesNode(descriptionNode);
                    if (naturesNode != null) {
                        List<DOMNode> children = naturesNode.getChildren();
                        for (DOMNode child : children) {
                            String nature = Utils.getInlineString(child.getFirstChild());
                            if (ProjectType.ROOT_PROJECT.value.equalsIgnoreCase(nature)) {
                                return Boolean.TRUE;
                            }
                        }
                    }
                }
            } catch (IOException e) {
            }
        }
        return Boolean.FALSE;
    }

    public static DOMNode findDescriptionNode(DOMDocument projectDOM) {

        DOMNode descriptionNode = null;
        for (int i = 0; i < projectDOM.getChildren().size(); i++) {
            String elementName = projectDOM.getChild(i).getNodeName();
            if (Constant.PROJECT_DESCRIPTION.equalsIgnoreCase(elementName)) {
                descriptionNode = projectDOM.getChild(i);
                break;
            }
        }
        return descriptionNode;
    }

    public static DOMNode findNaturesNode(DOMNode descriptionNode) {

        DOMNode naturesNode = null;
        for (int i = 0; i < descriptionNode.getChildren().size(); i++) {
            String elementName = descriptionNode.getChild(i).getNodeName();
            if (Constant.NATURES.equalsIgnoreCase(elementName)) {
                naturesNode = descriptionNode.getChild(i);
                break;
            }
        }
        return naturesNode;
    }

    public static boolean isFileExists(String filePath) {

        if (filePath.contains(Constant.FILE_PREFIX)) {
            filePath = filePath.substring(7);
        }
        File file = new File(filePath);
        return file.exists();
    }

    public static boolean isFileInRegistry(File file) {

        return file.getAbsolutePath().contains(Constant.REGISTRY + File.separator + Constant.GOV) || file.getAbsolutePath().contains(Constant.REGISTRY + File.separator + Constant.CONF);
    }

    public static String getRegistryKey(File file) {

        String pattern = "(.*)(\\b(gov|conf)\\b)(.*)";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(file.getAbsolutePath());

        if (m.find()) {
            String type = m.group(3);
            String path = m.group(4).replaceAll("\\\\", "/");
            path = path.replaceAll("^/+", "");
            return type + ":" + path;
        } else {
            return null;
        }
    }

    public static String getResourceKey(File file) {

        String pattern = "(.*)(\\b(resources)\\b)(.*)";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(file.getAbsolutePath());

        if (m.find()) {
            String path = m.group(4).replaceAll("\\\\", "/");
            path = path.replaceAll("^/+", "");
            return Constant.RESOURCES + ":" + path;
        } else {
            return null;
        }
    }

    public static boolean isZipFile(File file) {

        String fileName = file.getName();
        return fileName.endsWith(".zip");
    }

    public static boolean isXml(File file) {

        String filePath = file.getName();
        int dotIndex = filePath.lastIndexOf(Constant.DOT);
        if (dotIndex != -1 && dotIndex < filePath.length() - 1) {
            String extension = filePath.substring(dotIndex + 1);
            return Constant.XML.equalsIgnoreCase(extension);
        }
        return false;
    }

    public static void extractZip(File zip, File extractTo) throws IOException {

        waitForDownload(zip);
        byte[] buffer = new byte[1024];
        ZipInputStream zis = new ZipInputStream(new FileInputStream(zip));
        ZipEntry zipEntry = zis.getNextEntry();
        String zipName = zip.getName().replace(".zip", "");
        boolean removeUpperFolder = false;
        while (zipEntry != null) {
            String entryName = zipEntry.getName();
            if (removeUpperFolder) {
                if (entryName.startsWith(zipName + "/")) {
                    entryName = entryName.substring(zipName.length() + 1);
                    if (entryName.isEmpty()) {
                        zipEntry = zis.getNextEntry();
                        continue;
                    }
                }
            } else if (entryName.equals(zipName + "/") && zipEntry.isDirectory()) {
                removeUpperFolder = true;
                zipEntry = zis.getNextEntry();
                continue;
            }
            File newFile = newFile(extractTo, entryName);
            if (zipEntry.isDirectory()) {
                if (!newFile.isDirectory() && !newFile.mkdirs()) {
                    throw new IOException("Failed to create directory " + newFile);
                }
            } else {
                File parent = newFile.getParentFile();
                if (!parent.isDirectory() && !parent.mkdirs()) {
                    throw new IOException("Failed to create directory " + parent);
                }

                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
            }
            zipEntry = zis.getNextEntry();
        }

        zis.closeEntry();
        zis.close();

    }

    private static void waitForDownload(File file) {

        boolean isDownloaded = false;
        long fileSize = getFileSize(file);
        int waiting = 0;
        int oldTime = 1000000;
        boolean isOldFile = isOldFile(file, oldTime);
        while (!isDownloaded && !isOldFile) {
            try {
                waiting++;
                Thread.sleep(1000);
                long newFileSize = getFileSize(file);
                // If the file size is not changing, then the download is complete.
                // Or if the waiting time is more than 100 seconds, then the waiting is forced to stop.
                if (newFileSize == fileSize || waiting > oldTime / 1000) {
                    isDownloaded = true;
                } else {
                    fileSize = newFileSize;
                }
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    private static boolean isOldFile(File file, int oldTime) {

        long lastModified = file.lastModified();
        long currentTime = System.currentTimeMillis();
        return currentTime - lastModified > oldTime;
    }

    private static long getFileSize(File file) {

        try {
            return Files.size(file.toPath());
        } catch (IOException e) {
            return -1;
        }
    }

    public static File newFile(File destinationDir, String zipName) throws IOException {

        File destFile = new File(destinationDir, zipName.indexOf("Extracted") != -1 ?
                zipName.substring(0, zipName.indexOf("Extracted") + "Extracted".length()) : zipName);

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipName);
        }

        return destFile;
    }

    public static String removeHyphen(String name) {

        String[] parts = name.split("-");
        StringBuilder result = new StringBuilder(parts[0]);

        for (int i = 1; i < parts.length; i++) {
            result.append(Character.toUpperCase(parts[i].charAt(0)))
                    .append(parts[i].substring(1));
        }
        return result.toString();
    }

    public static String pluralToSingular(String name) {

        if (name.endsWith("ies")) {
            return name.substring(0, name.length() - 3) + "y";
        } else if (name.endsWith("s")) {
            return name.substring(0, name.length() - 1);
        }
        return name;
    }

    public static DOMNode getChildNodeByName(DOMNode node, String name) {

        DOMNode foundNode = null;
        for (int i = 0; i < node.getChildren().size(); i++) {
            String elementName = node.getChild(i).getNodeName();
            if (name.equalsIgnoreCase(elementName)) {
                foundNode = node.getChild(i);
                break;
            }
        }
        return foundNode;
    }

    public static String addUnderscoreBetweenWords(String input) {

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char currentChar = input.charAt(i);
            if (Character.isUpperCase(currentChar) && i > 0) {
                result.append("_");
            }
            result.append(currentChar);
        }
        return result.toString();
    }

    public static String getHash(String input) {

        MessageDigest md = null;
        String hash = null;
        try {
            md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            hash = convertToHex(messageDigest);
        } catch (NoSuchAlgorithmException e) {
        }
        return hash;
    }

    private static String convertToHex(final byte[] messageDigest) {

        BigInteger bigint = new BigInteger(1, messageDigest);
        String hexText = bigint.toString(16);
        while (hexText.length() < 32) {
            hexText = "0".concat(hexText);
        }
        return hexText;
    }

    public static DOMElement getFirstElement(DOMElement element) {

        List<DOMNode> children = element.getChildren();
        if (children != null) {
            for (DOMNode child : children) {
                if (child instanceof DOMElement) {
                    return (DOMElement) child;
                }
            }
        }
        return null;
    }

    /**
     * Get the root element of the given document
     *
     * @param document the document
     * @return the root element of the given document
     */
    public static DOMElement getRootElement(DOMDocument document) {

        List<DOMNode> children = document.getChildren();
        if (children != null) {
            for (DOMNode child : children) {
                if (child instanceof DOMElement) {
                    return (DOMElement) child;
                }
            }
        }
        return null;
    }

    /**
     * Read the content of the given file
     *
     * @param file the file
     * @return the content of the given file
     * @throws IOException if an error occurs while reading the file
     */
    public static String readFile(File file) throws IOException {

        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line).append("\n");
        }
        bufferedReader.close();
        return stringBuilder.toString();
    }

    /**
     * Get the JSON object from the given content
     *
     * @param content the content
     * @return the JSON object from the given content
     */
    public static JsonObject getJsonObject(String content) {

        JsonElement jsonElement = JsonParser.parseString(content);
        if (jsonElement.isJsonObject()) {
            return jsonElement.getAsJsonObject();
        }
        return null;
    }

    /**
     * Get the JSON array from the given content
     *
     * @param content the content
     * @return the JSON object from the given content
     */
    public static JsonArray getJsonArray(String content) {

        JsonElement jsonElement = JsonParser.parseString(content);
        if (jsonElement.isJsonArray()) {
            return jsonElement.getAsJsonArray();
        }
        return null;
    }

    /**
     * Get the JSON element from the given content
     *
     * @param content the content
     * @return the JSON element from the given content
     */
    public static JsonElement getJsonElement(String content) {

        try {
            return JsonParser.parseString(content);
        } catch (JsonSyntaxException e) {
            return null;
        }
    }

    /**
     * Check whether the given content is a JSON
     *
     * @param content
     * @return
     */
    public static boolean isJson(String content) {

        return getJsonElement(content) != null;
    }

    public static <T extends Enum<T>> T getEnumFromValue(String value, Class<T> enumClass) {

        if (value != null) {
            try {
                return Enum.valueOf(enumClass, value);
            } catch (IllegalArgumentException e) {
                try {
                    Method method = enumClass.getDeclaredMethod("getValue");
                    for (T enumValue : enumClass.getEnumConstants()) {
                        String valueOfEnum = (String) method.invoke(enumValue);
                        if (valueOfEnum.equals(value)) {
                            return enumValue;
                        }
                    }
                } catch (Exception ex) {
                }
            }
        }
        return null;
    }

    /**
     * Get the extension of the given file
     *
     * @param file
     * @return
     */
    public static String getFileExtension(File file) {

        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex != -1 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex + 1);
        }
        return "";
    }

    /**
     * Get the file name without the extension
     *
     * @param file
     * @return
     */
    public static String getFileName(File file) {

        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex != -1) {
            return fileName.substring(0, dotIndex);
        }
        return fileName;
    }

    /**
     * Unescape the given XML text
     *
     * @param text
     * @return
     */
    public static String unescapeXml(String text) {

        if (text == null) {
            return null;
        }
        return text
                .replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&quot;", "\"");
    }

    public static String escapeXML(String text) {

        if (text == null) {
            return null;
        }
        return text
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }

    public static String removeFilePrefix(String artifactPath) {

        if (artifactPath.contains(Constant.FILE_PREFIX)) {
            artifactPath = artifactPath.substring(7);
        }
        return artifactPath;
    }

    public static boolean isRegistryPropertiesFile(File file) {
        Pattern registryPropertiesFilePattern = Pattern.compile("(.*)\\.properties$");
        Matcher registryPropertiesFileMatcher = registryPropertiesFilePattern.matcher(file.getAbsolutePath());
        if (registryPropertiesFileMatcher.matches()) {
            String fileNameWithoutExtension = registryPropertiesFileMatcher.group(1);
            File otherFile = new File(fileNameWithoutExtension);
            return otherFile.exists();
        }
        return false;
    }

    public static Map<String, JsonObject> getUISchemaMap(String resourceFolderName) {
        Map<String, JsonObject> jsonMap = new HashMap<>();
        try {
            URI resourceURI = Utils.class.getClassLoader().getResource(resourceFolderName).toURI();
            fileSystem = FileSystems.newFileSystem(resourceURI, Map.of());
            Path resourcePath = fileSystem.getPath(resourceFolderName);
            Stream<Path> paths = Files.walk(resourcePath, 1);
            paths.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".json"))
                    .forEach(path -> processJsonFile(path, jsonMap));
            fileSystem.close();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to mediator UI schemas from resources.", e);;
        }
        return jsonMap;
    }

    private static void processJsonFile(Path path, Map<String, JsonObject> jsonMap) {
        Gson gson = new Gson();
        String fileName = path.getFileName().toString().replace(".json", "").replace("_", ":");
        try (InputStreamReader reader = new InputStreamReader(Files.newInputStream(path))) {
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
            jsonMap.put(fileName, jsonObject);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to load UI schema: " + fileName, e);
        }
    }

    public static JsonObject getMediatorList(String version, ConnectorHolder connectorHolder) throws IOException {

        InputStream inputStream = JsonLoader.class
                .getResourceAsStream("/org/eclipse/lemminx/mediators/mediators_"
                        + version.replace(".", "") + ".json");
        if (inputStream == null) {
            throw new IOException("Mediator list not found for the given version: " + version);
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        JsonObject mediatorList = JsonParser.parseReader(reader).getAsJsonObject();
        processMediatorList(mediatorList);
        addConnectorsToMediatorList(mediatorList, connectorHolder);
        return mediatorList;
    }

    private static void processMediatorList(JsonObject mediatorList) {

        List<String> favouritesMediators = mediatorList.getAsJsonArray(Constant.FAVOURITES).asList().stream()
                .map(ele -> ele.getAsJsonObject().get(Constant.TITLE).getAsString()).collect(Collectors.toList());
        for (Map.Entry<String, JsonElement> entry : mediatorList.entrySet()) {
            String key = entry.getKey();
            JsonElement value = entry.getValue();
            if (!Constant.FAVOURITES.equals(key)) {
                removeFavouritesMediators(value, favouritesMediators);
            }
            JsonObject itemObject = new JsonObject();
            itemObject.add(Constant.ITEMS, value);
            mediatorList.add(key, itemObject);
        }
    }

    private static void removeFavouritesMediators(JsonElement mediators, List<String> favouritesMediators) {

        if (mediators.isJsonArray()) {
            Iterator<JsonElement> iterator = mediators.getAsJsonArray().iterator();
            while (iterator.hasNext()) {
                JsonObject mediator = iterator.next().getAsJsonObject();
                if (favouritesMediators.contains(mediator.get(Constant.TITLE).getAsString())) {
                    iterator.remove();
                }
            }
        }
    }

    private static void addConnectorsToMediatorList(JsonObject mediatorList, ConnectorHolder connectorHolder) {

        List<Connector> connectors = connectorHolder.getConnectors();
        JsonElement otherCategoryMediators = mediatorList.remove(Constant.OTHER);
        for (Connector connector : connectors) {
            JsonArray operationsArray = new JsonArray();
            List<ConnectorAction> operations = connector.getActions();
            for (ConnectorAction operation : operations) {
                if (!operation.getHidden()) {
                    JsonObject operationObject = new JsonObject();
                    String operationName = StringUtils.isEmpty(operation.getDisplayName()) ? operation.getName() :
                            operation.getDisplayName();
                    operationObject.addProperty(Constant.TITLE, operationName);
                    operationObject.addProperty(Constant.OPERATION_NAME, operation.getName());
                    operationObject.addProperty(Constant.TAG, operation.getTag());
                    operationObject.addProperty(Constant.TOOLTIP, operation.getDescription());
                    operationObject.addProperty(Constant.ICON_PATH, connector.getIconPath());
                    operationsArray.add(operationObject);
                }
            }
            JsonObject connectorObject = new JsonObject();
            connectorObject.add(Constant.ITEMS, operationsArray);
            connectorObject.addProperty(Constant.IS_CONNECTOR, true);
            connectorObject.addProperty(Constant.ARTIFACT_ID, connector.getArtifactId());
            connectorObject.addProperty(Constant.VERSION, connector.getVersion());
            connectorObject.addProperty(Constant.CONNECTOR_PATH, connector.getConnectorZipPath());
            mediatorList.add(
                    StringUtils.isEmpty(connector.getDisplayName()) ? connector.getName() : connector.getDisplayName(),
                    connectorObject);
        }
        if (otherCategoryMediators != null) {
            mediatorList.add(Constant.OTHER, otherCategoryMediators);
        }
    }

    public static String getServerVersion(String projectPath, String defaultVersion) {
        try {
            Path pomPath = Path.of(projectPath, "pom.xml");
            File pomFile = pomPath.toFile();
            DOMDocument document = getDOMDocument(pomFile);

            DOMNode propertiesList = getChildNodeByName(document.getDocumentElement(), "properties");
            if (propertiesList != null) {
                DOMNode runtimeVersionList = getChildNodeByName(propertiesList, "project.runtime.version");
                if (runtimeVersionList != null) {
                    String version = getInlineString(runtimeVersionList.getFirstChild());
                    Pattern pattern = Pattern.compile("^\\d+\\.\\d+\\.\\d+$");
                    Matcher matcher = pattern.matcher(version);
                    if (matcher.matches()) {
                        if (Integer.parseInt(version.replace(".", "")) <
                                Integer.parseInt(Constant.MI_430_VERSION.replace(".", ""))) {
                            return Constant.MI_430_VERSION;
                        }
                        return version;
                    }
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error occurred while extracting server runtime version.", e);
        }
        return defaultVersion;
    }

    public static Map<String, Mustache> getTemplateMap(String resourceFolderName) {
        Map<String, Mustache> templateMap = new HashMap<>();
        try {
            URI resourceURI = Utils.class.getClassLoader().getResource(resourceFolderName).toURI();
            fileSystem = FileSystems.newFileSystem(resourceURI, Map.of());
            Path templatesPath = fileSystem.getPath(resourceFolderName);
            Stream<Path> paths = Files.walk(templatesPath, 1);
            paths.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".mustache"))
                    .forEach(path -> loadTemplate(path, resourceFolderName, templateMap));
            fileSystem.close();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to load mustache templates from resources.", e);;
        }
        return templateMap;
    }

    private static void loadTemplate(Path path, String resourceFolder, Map<String, Mustache> templateMap) {
        String templateName = path.getFileName().toString().replace(".mustache", "").replace("_", ":");
        try (InputStreamReader reader = new InputStreamReader(
                Utils.class.getClassLoader().getResourceAsStream(resourceFolder + "/" + path.getFileName()))) {
            Mustache template = mustacheFactory.compile(reader, templateName);
            templateMap.put(templateName, template);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to load template: " + templateName, e);
        }
    }

    public static Path copyXSDFiles(String projectUri) throws IOException, URISyntaxException {

        String version = getServerVersion(projectUri, Constant.DEFAULT_MI_VERSION);
        String versionFolder = version.replace(".", "");
        String schemasPath = "org/eclipse/lemminx/schemas/" + versionFolder;
        File tempFolder = Files.createTempDirectory("synapse").toFile();
        tempFolder.deleteOnExit();
        extractJarFolder(schemasPath, tempFolder.toPath());
        return tempFolder.toPath();
    }

    public static void extractJarFolder(String resourceFolder, Path targetDirectory)
            throws IOException, URISyntaxException {

        Files.createDirectories(targetDirectory);
        ClassLoader classLoader = Utils.class.getClassLoader();
        URL resourceURL = classLoader.getResource(resourceFolder);

        if (resourceURL == null) {
            throw new IOException("Folder " + resourceFolder + " not found!");
        }

        if (resourceURL.getProtocol().equals(Constant.JAR)) {
            // Resource is inside a JAR
            String jarPath = extractJarPath(resourceURL);
            try (JarFile jarFile = new JarFile(Paths.get(jarPath).toFile())) {
                Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String entryName = entry.getName();
                    if (entryName.startsWith(resourceFolder) && !entry.isDirectory()) {
                        // Extract each file
                        try (InputStream inputStream = jarFile.getInputStream(entry)) {
                            Path targetFile = targetDirectory.resolve(entryName.substring(resourceFolder.length() + 1));
                            Files.createDirectories(targetFile.getParent());
                            Files.copy(inputStream, targetFile, StandardCopyOption.REPLACE_EXISTING);
                        }
                    }
                }
            }
        }
    }

    private static String extractJarPath(URL resourceURL) throws URISyntaxException {

        URI uri = new URI(resourceURL.getPath().substring(0, resourceURL.getPath().indexOf("!")));
        return new File(uri).getAbsolutePath();
    }

    public static Path updateSynapseCatalogSettings(InitializeParams params) throws IOException, URISyntaxException {

        String projectUri = params.getRootPath();
        Object initParams = params.getInitializationOptions();
        Gson gson = new Gson();
        JsonElement jsonElement = gson.toJsonTree(initParams);
        if (jsonElement != null && jsonElement.isJsonObject() && jsonElement.getAsJsonObject().has(Constant.SETTINGS)) {
            JsonObject settings = jsonElement.getAsJsonObject().getAsJsonObject(Constant.SETTINGS);
            Path schemaPath = copyXSDFiles(projectUri);
            JsonElement updatedParams = updateSynapseCatalogSettings(settings, schemaPath);
            JsonObject updatedSettings = new JsonObject();
            updatedSettings.add(Constant.SETTINGS, updatedParams);
            params.setInitializationOptions(updatedSettings);
            return schemaPath;

        }
        return null;
    }

    public static JsonElement updateSynapseCatalogSettings(JsonObject settings, Path schemaPath)
            throws IOException, URISyntaxException {

        if (schemaPath != null) {
            Path catalogPath = schemaPath.resolve("catalog.xml");
            JsonArray catalogsArray = new JsonArray();
            catalogsArray.add(new JsonPrimitive(catalogPath.toString()));
            if (settings != null && settings.isJsonObject() && settings.has(Constant.XML)) {
                settings.getAsJsonObject(Constant.XML).add(Constant.CATALOGS, catalogsArray);
            }
        }
        return settings;
    }

    public static String deriveResourceKeyFromFilePath(String filePath) {

        String govIdentifier = "registry" + File.separator + "gov" + File.separator;
        String confIdentifier = "registry" + File.separator + "conf" + File.separator;
        String resourcesIdentifier = "resources" + File.separator;
        if (filePath.contains(govIdentifier)) {
            String derivedResourceKey = "gov:" + filePath.substring(filePath.indexOf(govIdentifier) + govIdentifier.length());
            return derivedResourceKey.replace(File.separator, "/");
        } else if (filePath.contains(confIdentifier)) {
            String derivedResourceKey =
                    "conf:" + filePath.substring(filePath.indexOf(confIdentifier) + confIdentifier.length());
            return derivedResourceKey.replace(File.separator, "/");
        } else if (filePath.contains(resourcesIdentifier)) {
            String derivedResourceKey = "resources:" +
                    filePath.substring(filePath.indexOf(resourcesIdentifier) + resourcesIdentifier.length());
            return derivedResourceKey.replace(File.separator, "/");
        } else {
            return filePath;
        }
    }

    /**
     * Copy the content of the source folder to the target folder
     *
     * @param source      the source folder
     * @param target      the target folder
     * @param copiedFiles the list of copied files
     * @throws IOException if an error occurs while copying the content
     */
    public static void copyFolder(Path source, Path target, List<String> copiedFiles) throws IOException {

        if (Files.notExists(source)) {
            return;
        }
        // Create target directory if it doesn't exist
        if (Files.notExists(target)) {
            Files.createDirectories(target);
        }

        // Walk the source tree and copy files to the target location
        Files.walkFileTree(source, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

                Path targetFile = target.resolve(source.relativize(file));
                if (".DS_Store".equals(file.getFileName().toString())) {
                    return FileVisitResult.CONTINUE;
                }
                Files.copy(file, targetFile, StandardCopyOption.REPLACE_EXISTING);
                if (copiedFiles != null) {
                    copiedFiles.add(targetFile.toString());
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {

                Path targetDir = target.resolve(source.relativize(dir));
                if (Files.notExists(targetDir)) {
                    Files.createDirectories(targetDir);
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * Delete the given directory
     *
     * @param path the directory path
     * @throws IOException if an error occurs while deleting the directory
     */
    public static void deleteDirectory(Path path) throws IOException {

        if (Files.exists(path)) {
            Files.walkFileTree(path, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {

                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        }
    }

    /**
     * Write the given content to the file
     *
     * @param path    the file path
     * @param content the content
     * @throws IOException if an error occurs while writing the content to the file
     */
    public static void writeToFile(String path, String content) throws IOException {

        Path path1 = Paths.get(path);
        if (Files.notExists(path1.getParent())) {
            Files.createDirectories(path1.getParent());
        }
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(path))) {
            bos.write(content.getBytes(StandardCharsets.UTF_8));
        }
    }

    /**
     * Check whether the given content is a JSON object
     *
     * @param content the content
     * @return whether the given content is a JSON object
     */
    public static boolean isJSONObject(String content) {

        try {
            return JsonParser.parseString(content).isJsonObject();
        } catch (Exception e) {
            return Boolean.FALSE;
        }
    }

    /**
     * Convert the given string to camel case
     *
     * @param input the input string
     * @return the camel case string
     */
    public static String toCamelCase(String input) {

        if (input == null) {
            return null;
        }
        Pattern pattern = Pattern.compile("_(.)");
        Matcher matcher = pattern.matcher(input);
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(result, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(result);
        return result.toString();
    }

    /**
     * Copy the file from the source path to the target path
     *
     * @param sourcePath
     * @param targetPath
     * @throws IOException
     */
    public static void copyFile(String sourcePath, String targetPath) throws IOException {

        copyFile(sourcePath, targetPath, null);
    }

    /**
     * Copy the file from the source path to the target path with the given target file name
     *
     * @param sourcePath
     * @param targetPath
     * @param targetFileName
     * @throws IOException
     */
    public static void copyFile(String sourcePath, String targetPath, String targetFileName) throws IOException {

        Path source = Paths.get(sourcePath);
        Path targetFolder = Paths.get(targetPath);
        Path target;
        if (targetFileName != null) {
            target = targetFolder.resolve(targetFileName);
        } else {
            target = targetFolder.resolve(source.getFileName());
        }
        if (Files.notExists(target)) {
            Files.createDirectories(target.getParent());
        }
        Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
    }

    public static boolean isExpression(String value) {

        if (value == null) {
            return false;
        }
        return value.startsWith("${") && value.endsWith("}");
    }

    /**
     * Copies selected folders and files from source to destination based on include paths.
     *
     * @param sourceFolder Source directory path
     * @param includePaths List of relative paths to include in the copy
     * @throws IOException If an I/O error occurs
     */
    public static void copySelectedContent(Path sourceFolder, Path targetFolder, List<String> includePaths)
            throws IOException {

        // Normalize all include paths (convert to system-specific format)
        List<Path> normalizedIncludePaths = includePaths.stream()
                .map(path -> Paths.get(path).normalize())
                .collect(Collectors.toList());

        // Create the target root directory if it doesn't exist
        Files.createDirectories(targetFolder);

        // Walk through the source directory
        try (Stream<Path> paths = Files.walk(sourceFolder)) {
            paths.forEach(sourcePath -> {
                try {
                    // Get the relative path from the source root
                    Path relativePath = sourceFolder.relativize(sourcePath);

                    // Check if this path should be included
                    if (shouldIncludePath(relativePath, normalizedIncludePaths)) {
                        Path targetPath = targetFolder.resolve(relativePath);

                        // Create parent directories if needed
                        Files.createDirectories(targetPath.getParent());

                        // Copy the file or directory
                        if (Files.isDirectory(sourcePath)) {
                            Files.createDirectories(targetPath);
                        } else {
                            Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException("Failed to copy path: " + sourcePath, e);
                }
            });
        }
    }

    /**
     * Determines if a given path should be included based on the include paths list.
     *
     * @param relativePath The path to check
     * @param includePaths List of paths to include
     * @return true if the path should be included
     */
    private static boolean shouldIncludePath(Path relativePath, List<Path> includePaths) {

        for (Path includePath : includePaths) {
            if (relativePath.startsWith(includePath) || // Path is under an include path
                    includePath.startsWith(relativePath) || // Path is a parent of an include path
                    relativePath.toString().isEmpty()) { // Root directory
                return true;
            }
        }
        return false;
    }

    /**
     * Transform the org.w3c.dom.Node to a string
     *
     * @param node
     * @return
     */
    public static String nodeToString(Node node) {

        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            DOMSource source = new DOMSource(node);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            transformer.transform(source, result);
            return writer.toString();
        } catch (Exception e) {
            return StringUtils.EMPTY;
        }
    }

    /**
     * Check whether the given project is using an older version of CAR plugin
     *
     * @param projectPath
     * @return
     */
    public static boolean isOlderCARPlugin(String projectPath) {

        OverviewPageDetailsResponse overviewPageDetailsResponse = new OverviewPageDetailsResponse();
        PomParser.getPomDetails(projectPath, overviewPageDetailsResponse);
        String currentVersion = overviewPageDetailsResponse.getBuildDetails().getAdvanceDetails().getPluginDetails()
                .getProjectBuildPluginVersion().getValue();
        if (currentVersion.contains("-SNAPSHOT")) {
            currentVersion = currentVersion.replace("-SNAPSHOT", "");
        }
        return compareVersions(currentVersion, Constant.CAR_PLUGIN_CHECK_VERSION) < 0;
    }

    /**
     * Compares two version strings and determines their relative order.
     *
     * @param version1 The first version string to compare.
     * @param version2 The second version string to compare.
     * @return a negative integer if {@code version1} is less than {@code version2},
     * a positive integer if {@code version1} is greater than {@code version2},
     * or 0 if they are equal
     */
    public static int compareVersions(String version1, String version2) {

        String[] version1Parts = version1.split("\\.");
        String[] version2Parts = version2.split("\\.");
        int length = Math.max(version1Parts.length, version2Parts.length);

        for (int i = 0; i < length; i++) {
            int v1 = i < version1Parts.length ? Integer.parseInt(version1Parts[i]) : 0;
            int v2 = i < version2Parts.length ? Integer.parseInt(version2Parts[i]) : 0;

            if (v1 < v2) return -1;
            if (v1 > v2) return 1;
        }
        return 0;
    }
}
