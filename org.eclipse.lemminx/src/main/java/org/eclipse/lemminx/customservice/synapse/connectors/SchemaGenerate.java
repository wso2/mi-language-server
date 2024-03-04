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

package org.eclipse.lemminx.customservice.synapse.connectors;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SchemaGenerate {

    private static final Logger LOGGER = Logger.getLogger(SchemaGenerate.class.getName());

    public static void generate(ConnectorHolder holder, String schemaPath) {

        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n" +
                "<!--\n" +
                "  ~  Licensed to the Apache Software Foundation (ASF) under one\n" +
                "  ~  or more contributor license agreements.  See the NOTICE file\n" +
                "  ~  distributed with this work for additional information\n" +
                "  ~  regarding copyright ownership.  The ASF licenses this file\n" +
                "  ~  to you under the Apache License, Version 2.0 (the\n" +
                "  ~  \"License\"); you may not use this file except in compliance\n" +
                "  ~  with the License.  You may obtain a copy of the License at\n" +
                "  ~\n" +
                "  ~   http://www.apache.org/licenses/LICENSE-2.0\n" +
                "  ~\n" +
                "  ~  Unless required by applicable law or agreed to in writing,\n" +
                "  ~  software distributed under the License is distributed on an\n" +
                "  ~   * \"AS IS\" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY\n" +
                "  ~  KIND, either express or implied.  See the License for the\n" +
                "  ~  specific language governing permissions and limitations\n" +
                "  ~  under the License.\n" +
                "  -->\n" +
                "\n" +
                "<xs:schema \n" +
                "        xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" \n" +
                "        elementFormDefault=\"qualified\"\n" +
                "        targetNamespace=\"http://ws.apache.org/ns/synapse\"\n" +
                "        xmlns=\"http://ws.apache.org/ns/synapse\">\n" +
                "\n" +
                "    <xs:group name=\"connectors\">\n" +
                "        <xs:annotation>\n" +
                "            <xs:documentation source=\"description\">\n" +
                "                This group denotes all the connectors elements that are added to the integration " +
                "studio\n" +
                "            </xs:documentation>\n" +
                "        </xs:annotation>\n" +
                "        <xs:choice>\n");

        sb.append(getConnectorSchema(holder));

        sb.append("        </xs:choice>\n" +
                "    </xs:group>\n" +
                "</xs:schema>");

        writeToFile(sb.toString(), schemaPath);
    }

    private static void writeToFile(String string, String schemaPath) {

        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(schemaPath))) {
            bos.write(string.getBytes());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Could not update connector schemas. Connectors will not be validated.", e);
        }
    }

    private static String getConnectorSchema(ConnectorHolder holder) {

        List<Connector> connectors = holder.getConnectors();
        StringBuilder sb = new StringBuilder();
        for (Connector conn : connectors) {
            List<ConnectorAction> actions = conn.getActions();
            for (ConnectorAction action : actions) {
                if (!action.getHidden()) {
                    sb.append("            <xs:element name=\"" + action.getTag() + "\">\n");
                    sb.append("                <xs:complexType>\n" +
                            "                    <xs:all>\n");
                    for (String parameter : action.getParameters()) {
                        sb.append("                        <xs:element name=\"" + parameter + "\" type=\"xs:string\" " +
                                "minOccurs=\"0\" maxOccurs=\"1\" />\n");
                    }
                    sb.append("                    </xs:all>\n" +
                            "                </xs:complexType>\n" +
                            "            </xs:element>\n");
                }
            }
        }
        return sb.toString();
    }
}
