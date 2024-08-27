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

package org.eclipse.lemminx.customservice.synapse.dataService;

import java.io.File;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.lemminx.customservice.synapse.db.DBConnectionTester;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class QueryGenerator {

    private static final Logger LOGGER = Logger.getLogger(QueryGenerator.class.getName());

    /**
     * Generate resources and queries for data service based on a datasource
     *
     * @param requestParams datasource parameters object
     *
     * @return dbs file content with resources and queries
     */
    public String generateDSSQueries(QueryGenRequestParams requestParams) {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            Document doc = docBuilder.newDocument();
            Element dataElement = doc.createElement("data");

            DBConnectionTester dbConnectionTester = new DBConnectionTester();
            Connection connection = null;
            connection = dbConnectionTester.getConnection(requestParams.url, requestParams.username,
                    requestParams.password, requestParams.className);
            DatabaseMetaData metadata = connection.getMetaData();

            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> tableData = mapper.readValue(requestParams.tableData, Map.class);

            for (Map.Entry<String, String> entry : tableData.entrySet()) {
                Map<String, String> columnsList = new HashMap<>();
                Map<String, String> primaryKeys = new HashMap<String, String>();
                Map<String, String> autoIncrementFields = new HashMap<String, String>();
                String columnNamesCombined = "";
                int i = 0;
                String table = entry.getKey();
                try (ResultSet rs = metadata.getColumns(null, null, table, null)) {
                    while (rs.next()) {
                        String name = rs.getString(DataServiceConstants.COLUMN_NAME);
                        int type = rs.getInt(DataServiceConstants.DATA_TYPE);
                        String sqlType = getSQLType(type);
                        if (this.isAutoIncrementField(rs)) {
                            autoIncrementFields.put(name, sqlType);
                            continue;
                        }
                        columnsList.put(name, sqlType);
                        if (i == 0) {
                            columnNamesCombined = " " + name;
                        } else {
                            columnNamesCombined = columnNamesCombined + ", " + name;
                        }
                        i++;
                    }
                }
                try (ResultSet rs = metadata.getPrimaryKeys(null, null, table)) {
                    while (rs.next()) {
                        String name = rs.getString(DataServiceConstants.COLUMN_NAME);
                        String sqlType = columnsList.get(name);
                        if (sqlType == null) {
                            sqlType = autoIncrementFields.get(name);
                        }
                        primaryKeys.put(name, sqlType);
                    }
                }
                String methods = entry.getValue();
                if (methods.contains("GET")) {
                    generateSelectAllDefinition(doc, dataElement, table, columnNamesCombined, columnsList);
                }
                if (methods.contains("POST")) {
                    generateInsertDefinition(doc, dataElement, table, columnsList);
                }
                if (methods.contains("PUT")) {
                    generateUpdateDefinition(doc, dataElement, table, columnsList, primaryKeys);
                }
                if (methods.contains("DELETE")) {
                    generateDeleteDefinition(doc, dataElement, table, primaryKeys);
                }
            }

            doc.appendChild(dataElement);
            return generateServiceFromDoc(doc);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error occurred while generating the DBS file content.", e);
            return "";
        }
    }

    /**
     * Extract the tables in a datasource
     *
     * @param requestParams datasource parameters object
     *
     * @return List of tables that exist in the database
     */
    public Map<String, List<Boolean>> getTableList(QueryGenRequestParams requestParams) {
        try {
            Map<String, List<Boolean>> tablesMap = new HashMap<String, List<Boolean>>();
            DatabaseMetaData mObject = null;
            DBConnectionTester dbConnectionTester = new DBConnectionTester();
            Connection connection = dbConnectionTester.getConnection(requestParams.url, requestParams.username,
                    requestParams.password, requestParams.className);
            if (connection != null) {
                mObject = connection.getMetaData();
                ResultSet tableNamesList = null;
                String schema = null;
                try {
                    String dbType = mObject.getDatabaseProductName();
                    if ("Oracle".equalsIgnoreCase(dbType)) {
                        schema = connection.getSchema();
                    } else if ("PostgreSQL".equalsIgnoreCase(dbType)) {
                        ResultSet schemas = mObject.getSchemas();
                        while (schemas.next()) {
                            schema = schemas.getString("TABLE_SCHEM");
                        }
                    }
                    tableNamesList = mObject.getTables(connection.getCatalog(), schema, "%", new String[]{"TABLE"});
                    while (tableNamesList.next()) {
                        String tableName = tableNamesList.getString("TABLE_NAME");
                        ResultSet rs = mObject.getPrimaryKeys(null, null, tableName);
                        tablesMap.put(tableName, Arrays.asList(mObject.isReadOnly(), rs.next()));
                    }
                } finally {
                    if (tableNamesList != null) {
                        tableNamesList.close();
                    }
                }
            }
            return tablesMap;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Could not establish database connection.", e);
            return null;
        }
    }

    /**
     * Check whether a driver is available in the class path
     *
     * @param className DB connector class name in the driver
     *
     * @return Whether the DB driver is available in the class path
     */
    public static boolean isDriverAvailableInClassPath(String className) {

        try {
            URLClassLoader urlClassLoader = DynamicClassLoader.getClassLoader();
            Driver driver = (Driver) Class.forName(className, true, urlClassLoader).newInstance();
            return true;
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Error occurred while loading the DB driver class", e);
            return false;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error occurred while accessing the DB driver class", e);
            return false;
        }
    }

    /**
     * Add a DB driver to the class loader
     *
     * @param driverPath folder path of the DB driver
     * @param className DB connector class name in the driver
     *
     * @return Whether the DB driver was successfully added to the class path
     */
    public static boolean addDriverToClassPath(String driverPath, String className) {

        try {
            Path jarPath = Paths.get(driverPath);
            ArrayList<URL> driverUrls = new ArrayList<>(Arrays.asList(jarPath.toUri().toURL()));
            URLClassLoader urlClassLoader = new URLClassLoader(driverUrls.toArray(new URL[0]));
            Driver driver = (Driver) Class.forName(className, true, urlClassLoader).newInstance();
            DynamicClassLoader.addJarToClassLoader(new File(driverPath));
            return true;
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Error occurred while loading the DB driver class", e);
            return false;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error occurred while loading the DB driver class", e);
            return false;
        }
    }

    /**
     * Generate resource and query for select all operation
     *
     * @param doc DOM document with resources and queries
     * @param dataElement DOM element with resources and queries
     * @param table name of the database table
     * @param columnNamesCombined columns in the databased table combined to a single string
     * @param columnsList columns in the database table
     */
    private void generateSelectAllDefinition(Document doc, Element dataElement, String table, String columnNamesCombined,
                                             Map<String, String> columnsList) {
        Element queryEle = doc.createElement("query");
        queryEle.setAttribute("id", "select_all_" + table + "_query");
        queryEle.setAttribute("useConfig", "default");
        String query = QueryGenerateUtils.getSelectAll(table, "", columnNamesCombined);
        Element sqlEle = doc.createElement("sql");
        sqlEle.setTextContent(query);
        queryEle.appendChild(sqlEle);

        Element resultEle = doc.createElement("result");
        resultEle.setAttribute("element", table + "Collection");
        resultEle.setAttribute("rowName", table);

        for (Map.Entry<String, String> column : columnsList.entrySet()) {
            Element columnEle = doc.createElement("element");
            columnEle.setAttribute("column", column.getKey());
            columnEle.setAttribute("name", column.getKey());
            columnEle.setAttribute("xsdType", "xs:" + column.getValue().toLowerCase());
            resultEle.appendChild(columnEle);
        }
        queryEle.appendChild(resultEle);

        Element resourceEle = doc.createElement("resource");
        resourceEle.setAttribute("method", "GET");
        resourceEle.setAttribute("path", table);
        Element callQueryEle = doc.createElement("call-query");
        callQueryEle.setAttribute("href", "select_all_" + table + "_query");
        resourceEle.appendChild(callQueryEle);

        dataElement.appendChild(queryEle);
        dataElement.appendChild(resourceEle);
    }

    /**
     * Generate resource and query for insert operation
     *
     * @param doc DOM document with resources and queries
     * @param dataElement DOM element with resources and queries
     * @param table name of the database table
     * @param columnsList columns in the database table
     */
    private void generateInsertDefinition(Document doc, Element dataElement, String table,
                                          Map<String, String> columnsList) {
        Element queryEle = doc.createElement("query");
        queryEle.setAttribute("id", "insert_" + table + "_query");
        queryEle.setAttribute("useConfig", "default");
        String query = QueryGenerateUtils.getInsertStatement(table, "",
                columnsList.keySet().stream().collect(Collectors.toList()));
        Element sqlEle = doc.createElement("sql");
        sqlEle.setTextContent(query);
        queryEle.appendChild(sqlEle);

        Element callQueryEle = doc.createElement("call-query");
        callQueryEle.setAttribute("href", "insert_" + table + "_query");

        int i = 1;
        for (Map.Entry<String, String> column : columnsList.entrySet()) {
            Element paramEle = doc.createElement("param");
            paramEle.setAttribute("name", column.getKey());
            paramEle.setAttribute("ordinal", "" + i);
            paramEle.setAttribute("paramType", "SCALAR");
            paramEle.setAttribute("sqlType", column.getValue());
            paramEle.setAttribute("type", "IN");
            queryEle.appendChild(paramEle);

            Element withParamEle = doc.createElement("with-param");
            withParamEle.setAttribute("name", column.getKey());
            withParamEle.setAttribute("query-param", column.getKey());
            callQueryEle.appendChild(withParamEle);
            i++;
        }
        Element resourceEle = doc.createElement("resource");
        resourceEle.setAttribute("method", "POST");
        resourceEle.setAttribute("path", table);
        resourceEle.appendChild(callQueryEle);

        dataElement.appendChild(queryEle);
        dataElement.appendChild(resourceEle);
    }

    /**
     * Generate resource and query for update operation
     *
     * @param doc DOM document with resources and queries
     * @param dataElement DOM element with resources and queries
     * @param table name of the database table
     * @param columnsList columns in the database table
     * @param primaryKeys primary keys in the database table
     */
    private void generateUpdateDefinition(Document doc, Element dataElement, String table,
                                          Map<String, String> columnsList, Map<String, String> primaryKeys) {
        Element queryEle = doc.createElement("query");
        queryEle.setAttribute("id", "update_" + table + "_query");
        queryEle.setAttribute("useConfig", "default");
        String query = QueryGenerateUtils.getUpdateStatement(table, "",
                columnsList.keySet().stream().collect(Collectors.toList()),
                primaryKeys.keySet().stream().collect(Collectors.toList()));
        Element sqlEle = doc.createElement("sql");
        sqlEle.setTextContent(query);
        queryEle.appendChild(sqlEle);

        Element callQueryEle = doc.createElement("call-query");
        callQueryEle.setAttribute("href", "update_" + table + "_query");

        int i = 1;
        for (Map.Entry<String, String> column : columnsList.entrySet()) {
            if (primaryKeys.containsKey(column.getKey())) {
                continue;
            }
            Element paramEle = doc.createElement("param");
            paramEle.setAttribute("name", column.getKey());
            paramEle.setAttribute("ordinal", "" + i);
            paramEle.setAttribute("paramType", "SCALAR");
            paramEle.setAttribute("sqlType", column.getValue());
            paramEle.setAttribute("type", "IN");
            queryEle.appendChild(paramEle);

            Element withParamEle = doc.createElement("with-param");
            withParamEle.setAttribute("name", column.getKey());
            withParamEle.setAttribute("query-param", column.getKey());
            callQueryEle.appendChild(withParamEle);
            i++;
        }
        for (Map.Entry<String, String> primaryKey : primaryKeys.entrySet()) {
            Element paramEle = doc.createElement("param");
            paramEle.setAttribute("name", primaryKey.getKey());
            paramEle.setAttribute("ordinal", "" + i);
            paramEle.setAttribute("paramType", "SCALAR");
            paramEle.setAttribute("sqlType", primaryKey.getValue());
            paramEle.setAttribute("type", "IN");
            queryEle.appendChild(paramEle);

            Element withParamEle = doc.createElement("with-param");
            withParamEle.setAttribute("name", primaryKey.getKey());
            withParamEle.setAttribute("query-param", primaryKey.getKey());
            callQueryEle.appendChild(withParamEle);
            i++;
        }

        Element resourceEle = doc.createElement("resource");
        resourceEle.setAttribute("method", "PUT");
        resourceEle.setAttribute("path", table);
        resourceEle.appendChild(callQueryEle);

        dataElement.appendChild(queryEle);
        dataElement.appendChild(resourceEle);

    }

    /**
     * Generate resource and query for delete operation
     *
     * @param doc DOM document with resources and queries
     * @param dataElement DOM element with resources and queries
     * @param table name of the database table
     * @param primaryKeys primary keys in the database table
     */
    private void generateDeleteDefinition(Document doc, Element dataElement, String table,
                                          Map<String, String> primaryKeys) {
        Element queryEle = doc.createElement("query");
        queryEle.setAttribute("id", "delete_" + table + "_query");
        queryEle.setAttribute("useConfig", "default");
        String query = QueryGenerateUtils.getDeleteStatement(table, "",
                primaryKeys.keySet().stream().collect(Collectors.toList()));
        Element sqlEle = doc.createElement("sql");
        sqlEle.setTextContent(query);
        queryEle.appendChild(sqlEle);

        Element callQueryEle = doc.createElement("call-query");
        callQueryEle.setAttribute("href", "delete_" + table + "_query");

        int i = 1;
        for (Map.Entry<String, String> column : primaryKeys.entrySet()) {
            Element paramEle = doc.createElement("param");
            paramEle.setAttribute("name", column.getKey());
            paramEle.setAttribute("ordinal", "" + i);
            paramEle.setAttribute("paramType", "SCALAR");
            paramEle.setAttribute("sqlType", column.getValue());
            paramEle.setAttribute("type", "IN");
            queryEle.appendChild(paramEle);

            Element withParamEle = doc.createElement("with-param");
            withParamEle.setAttribute("name", column.getKey());
            withParamEle.setAttribute("query-param", column.getKey());
            callQueryEle.appendChild(withParamEle);
            i++;
        }

        Element resourceEle = doc.createElement("resource");
        resourceEle.setAttribute("method", "DELETE");
        resourceEle.setAttribute("path", table);
        resourceEle.appendChild(callQueryEle);

        dataElement.appendChild(queryEle);
        dataElement.appendChild(resourceEle);

    }

    /**
     * Generate the XML content with the given DOM document
     *
     * @param doc DOM document with resources and queries
     *
     * @return Formatted dbs file content
     */
    private String generateServiceFromDoc(Document doc) {
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer trans = tf.newTransformer();
            StringWriter sw = new StringWriter();
            trans.setOutputProperty(OutputKeys.INDENT, "yes");
            trans.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            trans.transform(new DOMSource(doc), new StreamResult(sw));

            String templateContent = sw.toString();
            templateContent = templateContent.replace("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>", "");
            templateContent = templateContent.replaceAll("(?s)<data>(.*)</data>", "$1").trim();
            return templateContent;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error occurred while generating the DBS file content", e);
            return "";
        }
    }

    private boolean isAutoIncrementField(ResultSet columnNames) {
        try {
            String autoIncrString = columnNames.getString(DataServiceConstants.AUTOINCREMENT_COLUMN);
            if (DataServiceConstants.IS_AUTOINCREMENT.equalsIgnoreCase(autoIncrString)) {
                return true;
            }
            Boolean identity = columnNames.getBoolean(DataServiceConstants.IDENTITY_COLUMN);
            if (identity != null) {
                return identity;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error occurred while retrieving DB column details", e);
        }
        return false;
    }

    private String getSQLType(int type) {
        if ((-1 == type) || (-16 == type) || (-15 == type)
                || (2009 == type) || (1111 == type)) {
            type = 1;
        }
        return QueryGenerateUtils.getDefinedTypes().get(type);
    }
}
