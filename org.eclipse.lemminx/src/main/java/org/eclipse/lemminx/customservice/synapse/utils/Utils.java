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

import org.apache.commons.lang3.StringEscapeUtils;
import org.eclipse.lemminx.commons.TextDocument;
import org.eclipse.lemminx.customservice.synapse.directoryTree.legacyBuilder.utils.ProjectType;
import org.eclipse.lemminx.dom.DOMAttr;
import org.eclipse.lemminx.dom.DOMDocument;
import org.eclipse.lemminx.dom.DOMElement;
import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.dom.DOMParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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

        int value = 0;
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

    public static boolean isLegacyProject(DOMDocument document) {

        String documentPath = document.getDocumentURI();
        return isLegacyProject(documentPath);
    }

    public static boolean isLegacyProject(String path) {
        String projectPath = null;
        try {
            projectPath = findLegacyProjectRootPath(path);
        } catch (IOException e) {
        }

        if(projectPath != null) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public static String findProjectRootPath(String currentPath,boolean isLegacyProject) throws IOException {
        if(isLegacyProject) {
            return findLegacyProjectRootPath(currentPath);
        } else {
            return findProjectRootPath(currentPath);
        }
    }

    public static String findLegacyProjectRootPath(String currentPath) throws IOException {

        if (currentPath.contains(Constant.FILE_PREFIX)) {
            currentPath = currentPath.substring(7);
        }
        if (currentPath == null || currentPath.isEmpty()) {
            return null;
        }
        String prevFolderPath = currentPath.substring(0, currentPath.lastIndexOf(File.separator));
        String dotProjectPath = currentPath + File.separator + Constant.DOT_PROJECT;
        File dotProjectFile = new File(dotProjectPath);
        if (dotProjectFile != null && dotProjectFile.exists()) {
            DOMDocument projectDOM = Utils.getDOMDocument(dotProjectFile);
            DOMNode descriptionNode = findDescriptionNode(projectDOM);
            if (descriptionNode != null) {
                DOMNode naturesNode = findNaturesNode(descriptionNode);
                if (naturesNode != null) {
                    List<DOMNode> children = naturesNode.getChildren();
                    for (DOMNode child : children) {
                        String nature = Utils.getInlineString(child.getFirstChild());
                        if (ProjectType.ROOT_PROJECT.value.equalsIgnoreCase(nature)) {
                            return currentPath;
                        }
                    }
                    return findLegacyProjectRootPath(prevFolderPath);
                }
            }
        } else {
            return findLegacyProjectRootPath(prevFolderPath);
        }
        return null;
    }

    public static String findProjectRootPath(String currentPath) {

        if (currentPath.contains(Constant.FILE_PREFIX)) {
            currentPath = currentPath.substring(7);
        }
        if (currentPath == null || currentPath.isEmpty()) {
            return null;
        }
        String prevFolderPath = currentPath.substring(0, currentPath.lastIndexOf(File.separator));
        String pomPath = currentPath + File.separator + Constant.POM;
        File pomFile = new File(pomPath);
        if (pomFile != null && pomFile.exists()) {
            return currentPath;
        } else {
            return findProjectRootPath(prevFolderPath);
        }
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
}
