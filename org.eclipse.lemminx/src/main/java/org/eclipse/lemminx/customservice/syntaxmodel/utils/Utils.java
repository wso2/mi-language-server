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

package org.eclipse.lemminx.customservice.syntaxmodel.utils;

import org.apache.commons.lang3.StringEscapeUtils;
import org.eclipse.lemminx.commons.TextDocument;
import org.eclipse.lemminx.customservice.syntaxmodel.factory.AbstractFactory;
import org.eclipse.lemminx.customservice.syntaxmodel.factory.endpoint.EndpointFactory;
import org.eclipse.lemminx.customservice.syntaxmodel.factory.mediators.MediatorFactoryFinder;
import org.eclipse.lemminx.customservice.syntaxmodel.factory.misc.SequenceFactory;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.endpoint.NamedEndpoint;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.mediator.Mediator;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.mediator.core.MediatorProperty;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.misc.common.Parameter;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.misc.common.Sequence;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.misc.evaluators.And;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.misc.evaluators.Equal;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.misc.evaluators.EvaluatorList;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.misc.evaluators.Not;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.misc.evaluators.Or;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.misc.targets.Target;
import org.eclipse.lemminx.dom.DOMAttr;
import org.eclipse.lemminx.dom.DOMDocument;
import org.eclipse.lemminx.dom.DOMElement;
import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.dom.DOMParser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Utils {

    private static MediatorFactoryFinder mediatorFactory = MediatorFactoryFinder.getInstance();

    public static Sequence createSequence(DOMNode node) {

        AbstractFactory sequenceFactory = new SequenceFactory();
        Sequence sequence = (Sequence) sequenceFactory.create((DOMElement) node);
        return sequence;
    }

    public static List<Mediator> createMediators(List<DOMNode> children) {

        List<Mediator> mediatorsList = new ArrayList<>();
        for (DOMNode node : children) {
            Mediator mediator = createMediator(node);
            if (mediator != null) {
                mediatorsList.add(mediator);
            }
        }
        return mediatorsList;
    }

    public static Mediator createMediator(DOMNode node) {

        Mediator mediators = mediatorFactory.getMediator(node);
        return mediators;
    }

    public static Target createTarget(DOMNode node) {

        Target target = new Target();
        target.elementNode((DOMElement) node);
        populateTargetAttributes(target, node);
        List<DOMNode> children = node.getChildren();
        if (children != null && !children.isEmpty()) {
            for (DOMNode child : children) {
                String name = child.getNodeName();
                if (name.equals(Constant.SEQUENCE)) {
                    Sequence sequence = createSequence(child);
                    target.setSequence(sequence);
                } else if (name.equals(Constant.ENDPOINT)) {
                    EndpointFactory endpointFactory = new EndpointFactory();
                    NamedEndpoint namedEndpoint = (NamedEndpoint) endpointFactory.create((DOMElement) child);
                    target.setEndpoint(namedEndpoint);
                }
            }
        }
        return target;
    }

    private static void populateTargetAttributes(Target target, DOMNode node) {

        String sequence = node.getAttribute(Constant.SEQUENCE);
        if (sequence != null && !sequence.isEmpty()) {
            target.setSequenceAttribute(sequence);
        }
        String endpoint = node.getAttribute(Constant.ENDPOINT);
        if (endpoint != null && !endpoint.isEmpty()) {
            target.setEndpointAttribute(endpoint);
        }
        String to = node.getAttribute(Constant.TO);
        if (to != null && !to.isEmpty()) {
            target.setTo(to);
        }
        String soapAction = node.getAttribute(Constant.SOAP_ACTION);
        if (soapAction != null && !soapAction.isEmpty()) {
            target.setSoapAction(soapAction);
        }
    }

    public static MediatorProperty createMediatorProperty(DOMNode element) {

        MediatorProperty property = new MediatorProperty();
        property.elementNode((DOMElement) element);
        String name = element.getAttribute(Constant.NAME);
        if (name != null && !name.isEmpty()) {
            property.setName(name);
        }
        String value = element.getAttribute(Constant.VALUE);
        if (value != null && !value.isEmpty()) {
            property.setValue(value);
        }
        String expression = element.getAttribute(Constant.EXPRESSION);
        if (expression != null && !expression.isEmpty()) {
            property.setExpression(expression);
        }
        List<DOMNode> children = element.getChildren();
        List<String> contents = new ArrayList<>();
        if (children != null && !children.isEmpty()) {
            for (DOMNode child : children) {
                String content = getInlineString(child);
                contents.add(content);
            }
            property.setContent(contents.toArray(new String[contents.size()]));
        }
        return property;
    }

    public static Parameter createParameter(DOMNode child) {

        Parameter parameter = new Parameter();
        parameter.elementNode((DOMElement) child);
        String name = child.getAttribute(Constant.NAME);
        if (name != null && !name.isEmpty()) {
            parameter.setName(name);
        }
        String key = child.getAttribute(Constant.KEY);
        if (key != null && !key.isEmpty()) {
            parameter.setKey(key);
        }
        String locked = child.getAttribute(Constant.LOCKED);
        if (locked != null && !locked.isEmpty()) {
            parameter.setLocked(Boolean.parseBoolean(locked));
        }
        return parameter;
    }

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

    public static Not createNot(DOMNode conditionChild) {

        Not not = new Not();
        not.elementNode((DOMElement) conditionChild);
        List<DOMNode> notChildren = conditionChild.getChildren();
        if (notChildren != null && !notChildren.isEmpty()) {
            for (DOMNode notChild : notChildren) {
                String name = notChild.getNodeName();
                if (name.equalsIgnoreCase(Constant.AND)) {
                    And and = createAnd(notChild);
                    not.setAnd(and);
                } else if (name.equalsIgnoreCase(Constant.OR)) {
                    Or or = createOr(notChild);
                    not.setOr(or);
                } else if (name.equalsIgnoreCase(Constant.EQUAL)) {
                    Equal equal = createEqual(notChild);
                    not.setEqual(equal);
                } else if (name.equalsIgnoreCase(Constant.NOT)) {
                    Not notNested = createNot(notChild);
                    not.setNot(notNested);
                }
            }
        }
        return not;
    }

    public static And createAnd(DOMNode conditionChild) {

        And and = new And();
        and.elementNode((DOMElement) conditionChild);
        List<DOMNode> andChildren = conditionChild.getChildren();
        if (andChildren != null && !andChildren.isEmpty()) {
            for (DOMNode andChild : andChildren) {
                String name = andChild.getNodeName();
                if (name.equalsIgnoreCase(Constant.AND)) {
                    And andNested = createAnd(andChild);
                    and.setAnd(andNested);
                } else if (name.equalsIgnoreCase(Constant.OR)) {
                    Or or = createOr(andChild);
                    and.setOr(or);
                } else if (name.equalsIgnoreCase(Constant.EQUAL)) {
                    Equal equal = createEqual(andChild);
                    and.setEqual(equal);
                } else if (name.equalsIgnoreCase(Constant.NOT)) {
                    Not not = createNot(andChild);
                    and.setNot(not);
                }
            }
        }
        return and;
    }

    public static Or createOr(DOMNode conditionChild) {

        Or or = new Or();
        or.elementNode((DOMElement) conditionChild);
        List<DOMNode> orChildren = conditionChild.getChildren();
        if (orChildren != null && !orChildren.isEmpty()) {
            List<EvaluatorList> evaluatorLists = new ArrayList<>();
            for (DOMNode orChild : orChildren) {
                String name = orChild.getNodeName();
                EvaluatorList evaluatorList = new EvaluatorList();
                if (name.equalsIgnoreCase(Constant.AND)) {
                    And and = createAnd(orChild);
                    evaluatorList.setAnd(Optional.of(and));
                    evaluatorLists.add(evaluatorList);
                } else if (name.equalsIgnoreCase(Constant.OR)) {
                    Or orNested = createOr(orChild);
                    evaluatorList.setOr(Optional.of(orNested));
                    evaluatorLists.add(evaluatorList);
                } else if (name.equalsIgnoreCase(Constant.EQUAL)) {
                    Equal equal = createEqual(orChild);
                    evaluatorList.setEqual(Optional.of(equal));
                    evaluatorLists.add(evaluatorList);
                } else if (name.equalsIgnoreCase(Constant.NOT)) {
                    Not not = createNot(orChild);
                    evaluatorList.setNot(Optional.of(not));
                }
            }
            or.setEvaluatorList(evaluatorLists.toArray(new EvaluatorList[evaluatorLists.size()]));
        }
        return or;
    }

    public static Equal createEqual(DOMNode conditionChild) {

        Equal equal = new Equal();
        equal.elementNode((DOMElement) conditionChild);
        String type = conditionChild.getAttribute(Constant.TYPE);
        if (type != null && !type.isEmpty()) {
            equal.setType(type);
        }
        String value = conditionChild.getAttribute(Constant.VALUE);
        if (value != null && !value.isEmpty()) {
            equal.setValue(value);
        }
        String source = conditionChild.getAttribute(Constant.SOURCE);
        if (source != null && !source.isEmpty()) {
            equal.setSource(source);
        }
        return equal;
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

    public static DOMElement getRootElementFromConfigXml(DOMDocument document) {

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

    public static String findRootPath(String currentPath) throws IOException {

        if (currentPath.contains(Constant.FILE_PREFIX)) {
            currentPath = currentPath.substring(7);
        }
        String prevFolderPath = currentPath.substring(0, currentPath.lastIndexOf(File.separator));
        String dotProjectPath = currentPath + Constant.FILE_SEPARATOR + Constant.DOT_PROJECT;
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
                        if (Constant.MAVEN_MULTI_MODULE_PROJECT.equalsIgnoreCase(nature)) {
                            return currentPath;
                        }
                    }
                    return findRootPath(prevFolderPath);
                }
            }
        } else {
            return findRootPath(prevFolderPath);
        }
        return null;
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
}
