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

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.sql.Types;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class QueryGenerateUtils {

    private static final Logger LOGGER = Logger.getLogger(QueryGenerateUtils.class.getName());

    private static Map<Integer, String> definedTypeMap = new HashMap<Integer, String>();
    private static Map<Integer, String> qnameTypeMap = new HashMap<Integer, String>();

    static {
        updateDataTypes();
        updateQueryParamTypes();
    }

    /**
     * Generate resource element content
     *
     * @param doc DOM document with resources and queries
     * @param method Type of resource
     * @param path Resource path
     * @param callQueryElement Call query element content
     *
     * @return Resource element content
     */
    public static Element generateResourceElement(Document doc, String method, String path, Element callQueryElement) {
        Element resourceEle = doc.createElement("resource");
        resourceEle.setAttribute("method", method);
        resourceEle.setAttribute("path", path);
        resourceEle.appendChild(callQueryElement);
        return resourceEle;
    }

    /**
     * Generate param element content
     *
     * @param doc DOM document with resources and queries
     * @param column DB column name and type
     * @param ordinal Ordinal value
     *
     * @return Param element content
     */
    public static Element generateParamElement(Document doc, Map.Entry<String, String> column, int ordinal) {
        Element paramEle = doc.createElement("param");
        paramEle.setAttribute("name", column.getKey());
        paramEle.setAttribute("ordinal", "" + ordinal);
        paramEle.setAttribute("paramType", "SCALAR");
        paramEle.setAttribute("sqlType", column.getValue());
        paramEle.setAttribute("type", "IN");
        return paramEle;
    }

    /**
     * Generate query element content
     *
     * @param doc DOM document with resources and queries
     * @param id Query ID
     * @param query SQL query
     * @param datasource Name of the datasource to be used
     *
     * @return Query element content
     */
    public static Element generateQueryElement(Document doc, String id, String query, String datasource) {
        Element queryEle = doc.createElement("query");
        queryEle.setAttribute("id", id);
        queryEle.setAttribute("useConfig", datasource);
        Element sqlEle = doc.createElement("sql");
        sqlEle.setTextContent(query);
        queryEle.appendChild(sqlEle);
        return queryEle;
    }

    /**
     * Generate result element content
     *
     * @param doc DOM document with resources and queries
     * @param table DB table name
     * @param columnsList Existing columns in the DB
     *
     * @return Result element content
     */
    public static Element generateResultElement(Document doc, String table, Map<String, String> columnsList) {
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
        return resultEle;
    }

    /**
     * Generate with-param element content
     *
     * @param doc DOM document with resources and queries
     * @param column Considered DB column details
     *
     * @return With-param element content
     */
    public static Element generateWithParamElement(Document doc, Map.Entry<String, String> column) {
        Element paramEle = doc.createElement("with-param");
        paramEle.setAttribute("name", column.getKey());
        paramEle.setAttribute("query-param", column.getKey());
        return paramEle;
    }

    /**
     * Generate call-query element content
     *
     * @param doc DOM document with resources and queries
     * @param href Referred query name
     *
     * @return Call-query element content
     */
    public static Element generateCallQueryElement(Document doc, String href) {
        Element callQueryEle = doc.createElement("call-query");
        callQueryEle.setAttribute("href", href);
        return callQueryEle;
    }

    public static Map<Integer, String> getDefinedTypes() {
        return definedTypeMap;
    }

    private static void updateDataTypes() {
        definedTypeMap.put(Types.CHAR, "STRING");
        definedTypeMap.put(Types.NUMERIC, "NUMERIC");
        definedTypeMap.put(Types.DECIMAL, "DOUBLE");
        definedTypeMap.put(Types.INTEGER, "INTEGER");
        definedTypeMap.put(Types.SMALLINT, "SMALLINT");
        definedTypeMap.put(Types.FLOAT, "DOUBLE");
        definedTypeMap.put(Types.REAL, "REAL");
        definedTypeMap.put(Types.DOUBLE, "DOUBLE");
        definedTypeMap.put(Types.VARCHAR, "STRING");
        definedTypeMap.put(Types.NVARCHAR, "STRING");
        definedTypeMap.put(Types.CLOB, "STRING");
        definedTypeMap.put(Types.BOOLEAN, "BOOLEAN");
        definedTypeMap.put(Types.TIMESTAMP, "TIMESTAMP");
        definedTypeMap.put(Types.BIT, "BIT");
        definedTypeMap.put(Types.TIME, "TIME");
        definedTypeMap.put(Types.TINYINT, "TINYINT");
        definedTypeMap.put(Types.BIGINT, "BIGINT");
        definedTypeMap.put(Types.LONGVARBINARY, "BINARY");
        definedTypeMap.put(Types.VARBINARY, "BINARY");
        definedTypeMap.put(Types.BINARY, "BINARY");
        definedTypeMap.put(Types.BLOB, "BINARY");
        definedTypeMap.put(Types.DATE, "DATE");
        definedTypeMap.put(Types.TIMESTAMP, "TIMESTAMP");
    }

    private static void updateQueryParamTypes() {
        qnameTypeMap.put(Types.CHAR, "string");
        qnameTypeMap.put(Types.NUMERIC, "integer");
        qnameTypeMap.put(Types.DECIMAL, "decimal");
        qnameTypeMap.put(Types.INTEGER, "integer");
        qnameTypeMap.put(Types.SMALLINT, "integer");
        qnameTypeMap.put(Types.FLOAT, "float");
        qnameTypeMap.put(Types.REAL, "double");
        qnameTypeMap.put(Types.DOUBLE, "double");
        qnameTypeMap.put(Types.VARCHAR, "string");
        qnameTypeMap.put(Types.NVARCHAR, "string");
        qnameTypeMap.put(Types.CLOB, "string");
        qnameTypeMap.put(Types.BOOLEAN, "boolean");
        qnameTypeMap.put(Types.TIMESTAMP, "dateTime");
        qnameTypeMap.put(Types.BIT, "integer");
        qnameTypeMap.put(Types.TIME, "time");
        qnameTypeMap.put(Types.TINYINT, "integer");
        qnameTypeMap.put(Types.BIGINT, "long");
        qnameTypeMap.put(Types.LONGVARBINARY, "base64Binary");
        qnameTypeMap.put(Types.VARBINARY, "base64Binary");
        qnameTypeMap.put(Types.BINARY, "base64Binary");
        qnameTypeMap.put(Types.BLOB, "base64Binary");
        qnameTypeMap.put(Types.DATE, "date");
    }

}
