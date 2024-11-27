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
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import org.apache.commons.lang3.StringEscapeUtils;
import org.eclipse.lemminx.commons.TextDocument;
import org.eclipse.lemminx.customservice.synapse.directoryTree.legacyBuilder.utils.ProjectType;
import org.eclipse.lemminx.dom.DOMAttr;
import org.eclipse.lemminx.dom.DOMDocument;
import org.eclipse.lemminx.dom.DOMElement;
import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.dom.DOMParser;
import org.eclipse.lsp4j.InitializeParams;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Utils {

    private static final Logger logger = Logger.getLogger(Utils.class.getName());
    private static FileSystem fileSystem;

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

        String xmlString = " ";
        List<DOMAttr> children = node.getAttributeNodes();
        if (children != null && !children.isEmpty()) {
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
        String text = "";
        text = Files.readString(path);
        TextDocument document = new TextDocument(text, file.getName());
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
        while (zipEntry != null) {
            File newFile = newFile(extractTo, zipEntry);
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

    public static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {

        File destFile = new File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
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
        String fileName = path.getFileName().toString().replace(".json", "");
        try (InputStreamReader reader = new InputStreamReader(Files.newInputStream(path))) {
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
            jsonMap.put(fileName, jsonObject);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to load UI schema: " + fileName, e);
        }
    }

    public static JsonObject getMediatorList(String version) {
        InputStream inputStream = JsonLoader.class
                .getResourceAsStream("/org/eclipse/lemminx/mediators/mediators_"
                        + version.replace(".", "") + ".json");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        return JsonParser.parseReader(reader).getAsJsonObject();
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
        String templateName = path.getFileName().toString().replace(".mustache", "");
        MustacheFactory mustacheFactory = new DefaultMustacheFactory();
        try (InputStreamReader reader = new InputStreamReader(
                Utils.class.getClassLoader().getResourceAsStream(resourceFolder + "/" + path.getFileName()))) {
            Mustache template = mustacheFactory.compile(reader, templateName);
            templateMap.put(templateName, template);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to load template: " + templateName, e);
        }
    }

    public static Path copyXSDFiles(String projectUri) throws IOException {

        String version = getServerVersion(projectUri, Constant.DEFAULT_MI_VERSION);
        String versionFolder = version.replace(".", "");
        String catalogPath = "org/eclipse/lemminx/schemas/" + versionFolder;
        File tempFolder = Files.createTempDirectory("synapse").toFile();
        tempFolder.deleteOnExit();
        extractJarFolder(catalogPath, tempFolder.toPath());
        String catalogFilePath = tempFolder.getAbsolutePath() + File.separator + "catalog.xml";
        return Paths.get(catalogFilePath);
    }

    public static void extractJarFolder(String resourceFolder, Path targetDirectory) throws IOException {

        Files.createDirectories(targetDirectory);
        ClassLoader classLoader = Utils.class.getClassLoader();
        URL resourceURL = classLoader.getResource(resourceFolder);

        if (resourceURL == null) {
            throw new IOException("Folder " + resourceFolder + " not found!");
        }

        if (resourceURL.getProtocol().equals("jar")) {
            // Resource is inside a JAR
            String jarPath = resourceURL.getPath().substring(5, resourceURL.getPath().indexOf("!"));
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

    public static Path updateSynapseCatalogSettings(InitializeParams params) throws IOException, URISyntaxException {

        String projectUri = params.getRootPath();
        Object initParams = params.getInitializationOptions();
        Gson gson = new Gson();
        JsonElement jsonElement = gson.toJsonTree(initParams);
        JsonObject settings = jsonElement.getAsJsonObject().getAsJsonObject("settings");
        Path catalogPath = copyXSDFiles(projectUri);
        JsonElement updatedParams = updateSynapseCatalogSettings(settings, catalogPath);
        params.setInitializationOptions(updatedParams);
        return catalogPath;
    }

    public static JsonElement updateSynapseCatalogSettings(JsonObject settings, Path catalogPath)
            throws IOException, URISyntaxException {

        JsonArray catalogsArray = new JsonArray();
        catalogsArray.add(new JsonPrimitive(catalogPath.toString()));
        settings.getAsJsonObject("xml").add("catalogs", catalogsArray);
        return settings;
    }
}
