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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.StringEscapeUtils;
import org.eclipse.lemminx.commons.TextDocument;
import org.eclipse.lemminx.customservice.synapse.directoryTree.legacyBuilder.utils.ProjectType;
import org.eclipse.lemminx.dom.DOMAttr;
import org.eclipse.lemminx.dom.DOMDocument;
import org.eclipse.lemminx.dom.DOMElement;
import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.dom.DOMParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Utils {

    public static String getInlineString(DOMNode node) {

        String inline = null;
        if (node != null) {
            if (node.isCDATA()) {
                inline = "<![CDATA[" + node.getTextContent() + "]]>";
            } else if (node.isText()) {
                inline = node.getTextContent();
            } else if (node instanceof DOMElement) {
                if (((DOMElement) node).isSelfClosed()) {
                    inline = "<" + node.getNodeName().concat(getAttributeXmlString(node)) + "/>";
                } else {
                    inline = "<" + node.getNodeName().concat(getAttributeXmlString(node)) + ">";
                    List<DOMNode> children = node.getChildren();
                    if (children != null && !children.isEmpty()) {
                        for (DOMNode child : children) {
                            inline += getInlineString(child);
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
                xmlString += child.getName() + "=\"" + child.getValue() + "\" ";
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

    public static DOMDocument getDOMDocument(String file) {

        TextDocument document = new TextDocument(file, "temp");
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
}
