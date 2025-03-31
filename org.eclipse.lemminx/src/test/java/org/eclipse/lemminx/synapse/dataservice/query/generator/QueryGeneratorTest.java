/*
 * Copyright (c) 2025, WSO2 LLC. (http://www.wso2.com).
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

package org.eclipse.lemminx.synapse.dataservice.query.generator;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.lemminx.customservice.synapse.dataService.CheckDBDriverResponseParams;
import org.eclipse.lemminx.customservice.synapse.dataService.QueryGenRequestParams;
import org.eclipse.lemminx.customservice.synapse.dataService.QueryGenerator;
import org.eclipse.lemminx.customservice.synapse.db.DBConnectionTester;
import org.eclipse.lemminx.synapse.connector.downloader.ConnectorDownloadManagerTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class QueryGeneratorTest {

    private QueryGenRequestParams requestParams;
    private Connection connection;
    private DatabaseMetaData metadata;
    private ResultSet resultSet;
    private MockedStatic<DBConnectionTester>  dbConnectionTester;
    private Path tempAddSQLDriverFilePath;
    private Path tempUpdateSQLDriverFilePath;
    private static final String ADD_DB_DRIVER = "synapse/query.generator/mysql-connector-j-8.2.0.jar";
    private static final String UPDATE_DB_DRIVER = "synapse/query.generator/mysql-connector-j-8.3.0.jar";

    @BeforeAll
    public void setup(@TempDir Path tempAddSQLDriverDir, @TempDir Path tempUpdateSQLDriverDir) throws Exception {
        requestParams = new QueryGenRequestParams();
        requestParams.setUrl("jdbc:test");
        requestParams.setUsername("testUser");
        requestParams.setPassword("testPassword");
        requestParams.setClassName("com.mysql.jdbc.Driver");
        requestParams.setDatasourceName("TestDS");
        requestParams.setTableData("{\"student\":\"GET,POST\",\"teacher\":\"PUT,DELETE\"}");
        tempAddSQLDriverFilePath = tempAddSQLDriverDir.resolve("project").resolve("deployment").resolve("libs");
        Files.createDirectories(tempAddSQLDriverFilePath);
        tempAddSQLDriverFilePath = tempAddSQLDriverFilePath.resolve("mysql-connector-j-8.2.0.jar");
        tempUpdateSQLDriverFilePath = tempUpdateSQLDriverDir.resolve("project").resolve("deployment").resolve("libs");
        Files.createDirectories(tempUpdateSQLDriverFilePath);
        tempUpdateSQLDriverFilePath = tempUpdateSQLDriverFilePath.resolve("mysql-connector-j-8.3.0.jar");
        copyResourceToTemp(tempAddSQLDriverFilePath, ADD_DB_DRIVER);
        copyResourceToTemp(tempUpdateSQLDriverFilePath, UPDATE_DB_DRIVER);
    }

    @Test
    @Order(1)
    void addDriverToClassPathTest() {
        String path = QueryGeneratorTest.class.getResource("/synapse/query.generator/mysql-connector-j-8.2.0.jar").getPath();
        String jarPath = new File(path).getAbsolutePath();
        boolean result = QueryGenerator.addDriverToClassPath(jarPath, "com.mysql.cj.jdbc.Driver");
        assertTrue(result);
    }

    @Test
    @Order(2)
    void isDriverAvailableTest() {
        String projectPath = tempAddSQLDriverFilePath.toAbsolutePath().toString().split("/deployment")[0];
        CheckDBDriverResponseParams result = QueryGenerator.isDriverAvailableInClassPath("com.mysql.cj.jdbc.Driver", projectPath);
        assertTrue(result.isDriverAvailable());
        assertEquals("8.2.0", result.getDriverVersion());
    }

    @Test
    @Order(3)
    void modifyDriverInClassPathTest() {
        String path = QueryGeneratorTest.class.getResource("/synapse/query.generator/mysql-connector-j-8.2.0.jar").getPath();
        String removeJarPath = new File(path).getAbsolutePath();
        path = QueryGeneratorTest.class.getResource("/synapse/query.generator/mysql-connector-j-8.3.0.jar").getPath();
        String addJarPath = new File(path).getAbsolutePath();
        boolean result = QueryGenerator.modifyDriverInClassPath(addJarPath, removeJarPath, "com.mysql.cj.jdbc.Driver");
        assertTrue(result);
    }

    @Test
    @Order(4)
    void isDriverAvailableTestAfterUpdate() {
        String projectPath = tempUpdateSQLDriverFilePath.toAbsolutePath().toString().split("/deployment")[0];
        CheckDBDriverResponseParams result = QueryGenerator.isDriverAvailableInClassPath("com.mysql.cj.jdbc.Driver", projectPath);
        assertTrue(result.isDriverAvailable());
        assertEquals("8.3.0", result.getDriverVersion());
    }

    @Test
    @Order(5)
    void addDriverToClassPathWithMismatchedClassNameTest() {
        String path = QueryGeneratorTest.class.getResource("/synapse/query.generator/mysql-connector-j-8.2.0.jar").getPath();
        String jarPath = new File(path).getAbsolutePath();
        boolean result = QueryGenerator.addDriverToClassPath(jarPath, "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        assertFalse(result);
    }

    @Test
    @Order(6)
    void isDriverUnavailableTest() {
        String projectPath = tempAddSQLDriverFilePath.toAbsolutePath().toString().split("/deployment")[0];
        CheckDBDriverResponseParams result = QueryGenerator.isDriverAvailableInClassPath("com.microsoft.sqlserver.jdbc.SQLServerDriver", projectPath);
        assertFalse(result.isDriverAvailable());
    }

    @Test
    @Order(7)
    void modifyDriverInClassPathWithMismatchedClassNameErrorTest() {
        String path = QueryGeneratorTest.class.getResource("/synapse/query.generator/mysql-connector-j-8.2.0.jar").getPath();
        String removeJarPath = new File(path).getAbsolutePath();
        path = QueryGeneratorTest.class.getResource("/synapse/query.generator/mysql-connector-j-8.3.0.jar").getPath();
        String addJarPath = new File(path).getAbsolutePath();
        boolean result = QueryGenerator.modifyDriverInClassPath(addJarPath, removeJarPath, "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        assertFalse(result);
    }

    @Test
    @Order(8)
    void removeNonExistingDriverFromClassPathTest() {
        String jarPath = "/error-path";
        boolean result = QueryGenerator.removeDriverFromClassPath(jarPath);
        assertFalse(result);
    }

    @Test
    @Order(9)
    void removeExistingDriverFromClassPathTest() {
        String jarPath = tempUpdateSQLDriverFilePath.toAbsolutePath().toString().split("/deployment")[0];
        boolean result = QueryGenerator.removeDriverFromClassPath(jarPath);
        assertTrue(result);
    }

    @Test
    @Order(10)
    public void testGenerateDSSQueriesWithValidParams() throws Exception {
        connection= mock(Connection.class);
        metadata = mock(DatabaseMetaData.class);
        resultSet = mock(ResultSet.class);
        dbConnectionTester = mockStatic(DBConnectionTester.class);
        dbConnectionTester.when(() -> DBConnectionTester.getConnection(any(), any(), any(), any())).thenReturn(connection);
        when(connection.getMetaData()).thenReturn(metadata);

        List<Map<String, Object>> columnData = new ArrayList<>();
        Map<String, Object> column1 = new HashMap<>();
        column1.put("COLUMN_NAME", "id");
        column1.put("DATA_TYPE", "INTEGER");
        columnData.add(column1);
        Map<String, Object> column2 = new HashMap<>();
        column2.put("COLUMN_NAME", "name");
        column2.put("DATA_TYPE", "VARCHAR");
        columnData.add(column2);
        List<Map<String, Object>> primaryKeyData = new ArrayList<>();
        Map<String, Object> primaryKey = new HashMap<>();
        primaryKey.put("COLUMN_NAME", "id");
        primaryKey.put("DATA_TYPE", "INTEGER");
        primaryKeyData.add(primaryKey);
        ResultSet studentColumns = new CustomResultSet(columnData);
        ResultSet teacherColumns = new CustomResultSet(columnData);
        ResultSet studentPrimary = new CustomResultSet(primaryKeyData);
        ResultSet teacherPrimary = new CustomResultSet(primaryKeyData);

        when(metadata.getColumns(any(), any(), any(), any())).thenReturn(studentColumns).thenReturn(teacherColumns);
        when(metadata.getPrimaryKeys(any(), any(), any())).thenReturn(studentPrimary).thenReturn(teacherPrimary);
        String path = ConnectorDownloadManagerTest.class.getResource("/synapse/query.generator/generated-configuration.txt").getPath();
        String expectedResultPath = new File(path).getAbsolutePath();
        String expectedResult = Files.readString(Paths.get(expectedResultPath));
        String result = QueryGenerator.generateDSSQueries(requestParams);
        dbConnectionTester.close();

        assertEquals(expectedResult, result);
    }

    @Test
    @Order(11)
    public void testGenerateDSSQueriesWithInvalidParams() {
        connection= mock(Connection.class);
        metadata = mock(DatabaseMetaData.class);
        resultSet = mock(ResultSet.class);
        dbConnectionTester = mockStatic(DBConnectionTester.class);
        dbConnectionTester.when(() -> DBConnectionTester.getConnection(any(), any(), any(), any())).thenReturn(null);
        String result = QueryGenerator.generateDSSQueries(requestParams);
        dbConnectionTester.close();

        assertEquals(StringUtils.EMPTY, result);
    }

    @Test
    @Order(12)
    void getTableListWithValidParams() throws SQLException {

        connection= mock(Connection.class);
        metadata = mock(DatabaseMetaData.class);
        dbConnectionTester = mockStatic(DBConnectionTester.class);
        dbConnectionTester.when(() -> DBConnectionTester.getConnection(any(), any(), any(), any())).thenReturn(connection);
        when(connection.getMetaData()).thenReturn(metadata);

        List<Map<String, Object>> tableData = new ArrayList<>();
        Map<String, Object> studentTable = new HashMap<>();
        studentTable.put("TABLE_NAME", "student");
        tableData.add(studentTable);
        Map<String, Object> teacherTable = new HashMap<>();
        teacherTable.put("TABLE_NAME", "teacher");
        tableData.add(teacherTable);
        List<Map<String, Object>> columnData = new ArrayList<>();
        Map<String, Object> column = new HashMap<>();
        column.put("COLUMN_NAME", "id");
        column.put("DATA_TYPE", "INTEGER");
        columnData.add(column);
        ResultSet tables = new CustomResultSet(tableData);
        ResultSet primary = new CustomResultSet(columnData);

        when(metadata.getTables(any(), any(), any(), any())).thenReturn(tables);
        when(metadata.isReadOnly()).thenReturn(false);
        when(metadata.getPrimaryKeys(any(), any(), any())).thenReturn(primary);
        Map<String, List<Boolean>> result = QueryGenerator.getTableList(requestParams);
        dbConnectionTester.close();

        assertEquals(2, result.size());
        assertTrue(result.keySet().contains("student"));
        assertTrue(result.keySet().contains("teacher"));
    }

    @Test
    @Order(13)
    void getTableListWithInvalidParams() {

        connection= mock(Connection.class);
        metadata = mock(DatabaseMetaData.class);
        dbConnectionTester = mockStatic(DBConnectionTester.class);
        dbConnectionTester.when(() -> DBConnectionTester.getConnection(any(), any(), any(), any())).thenReturn(null);
        Map<String, List<Boolean>> result = QueryGenerator.getTableList(requestParams);
        dbConnectionTester.close();

        assertEquals(0, result.size());
    }

    private void copyResourceToTemp(Path destinationPath, String resource) throws IOException {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resource)) {
            if (inputStream == null) {
                throw new IOException("Resource file not found: " + resource);
            }
            Files.copy(inputStream, destinationPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
