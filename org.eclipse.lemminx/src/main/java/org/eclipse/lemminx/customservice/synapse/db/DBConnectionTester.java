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

package org.eclipse.lemminx.customservice.synapse.db;

import org.eclipse.lemminx.customservice.synapse.utils.Constant;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

// Source: wso2/integration-studio
public class DBConnectionTester {

    private static final Logger LOGGER = Logger.getLogger(DBConnectionTester.class.getName());

    /**
     * Test the database connection with the given parameters.
     *
     * @param dbType         Database type
     * @param version        Database version
     * @param username       Username
     * @param password       Password
     * @param host           Host
     * @param port           Port
     * @param dbName         Database name
     * @param dbDriverFolder Database driver folder
     * @return True if the connection is successful, false otherwise
     */
    public boolean testDBConnection(String dbType, String version, String username, String password, String host,
                                    String port, String dbName, String dbDriverFolder) {

        Connection connection = getConnection(dbType, version, username, password, host, port, dbName, dbDriverFolder);

        if (connection != null) {
            return true;
        } else {
            return false;
        }
    }

    private Connection getConnection(String dbType, String version, String username, String password, String host,
                                     String port, String dbName, String dbDriverFolder) {

        String connUriStr = generateConnectionUrl(dbType, host, port, dbName);
        Connection connection = null;

        try {
            List<URL> dbDriverUrl = getDBDriverUrl(dbType, dbDriverFolder, version);
            if (dbDriverUrl == null) {
                return null;
            }
            URLClassLoader urlClassLoader = new URLClassLoader(dbDriverUrl.toArray(new URL[0]));

            Driver driver = null;
            switch (dbType) {
                case DBConstant.DBTypes.DB_TYPE_MYSQL:
                    driver = (Driver) Class.forName(DBConstant.DBDrivers.MYSQL_DRIVER, true, urlClassLoader)
                            .newInstance();
                    break;
                case DBConstant.DBTypes.DB_TYPE_MSSQL:
                    driver = (Driver) Class.forName(DBConstant.DBDrivers.MS_SQL_DRIVER, true, urlClassLoader)
                            .newInstance();
                    break;
                case DBConstant.DBTypes.DB_TYPE_POSTGRESSQL:
                    driver = (Driver) Class.forName(DBConstant.DBDrivers.POSTGRESQL_DRIVER, true, urlClassLoader)
                            .newInstance();
                    break;
                case DBConstant.DBTypes.DB_TYPE_DERBY:
                    driver = (Driver) Class
                            .forName(DBConstant.DBDrivers.DERBY_CLIENT_DRIVER, true, urlClassLoader)
                            .newInstance();
                    break;
                case DBConstant.DBTypes.DB_TYPE_H2:
                    driver = (Driver) Class.forName(DBConstant.DBDrivers.H2_DRIVER, true, urlClassLoader)
                            .newInstance();
                    break;
                case DBConstant.DBTypes.DB_TYPE_ORACLE:
                    driver = (Driver) Class.forName(DBConstant.DBDrivers.ORACLE_DRIVER, true, urlClassLoader)
                            .newInstance();
                    break;
            }

            DriverManager.registerDriver(new DriverShim(driver));
            // Check username and password are empty due to Derby db can connect without username and password
            if (dbType.equals(DBConstant.DBTypes.DB_TYPE_DERBY)
                    && username.equals(Constant.EMPTY_STRING)
                    && password.equals(Constant.EMPTY_STRING)) {
                connection = DriverManager.getConnection(connUriStr);
            } else {
                connection = DriverManager.getConnection(connUriStr, username, password);
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Could not establish database connection.", e);
        }

        return connection;
    }

    private List<URL> getDBDriverUrl(String dbType, String dbDriverFolder, String version) {

        // TODO: Improve this logic to get exact driver jar file
        List<URL> driverUrls = new ArrayList<>();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(dbDriverFolder), "*.jar")) {
            for (Path path : directoryStream) {
                if (path.getFileName().toString().contains(version)) {
                    driverUrls.add(path.toUri().toURL());
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error while reading driver files", e);
        }
        return driverUrls;
    }

    private String generateConnectionUrl(String dbType, String host,
                                         String port, String dbName) {

        String connectionUrl = DBConstant.DBUrlParams.DB_URL_JDBC_BASE;

        switch (dbType) {
            case DBConstant.DBTypes.DB_TYPE_MYSQL:
                // Template: jdbc:mysql://HOST:PORT/DBname
                connectionUrl += DBConstant.DBTypes.DB_TYPE_MYSQL + "://" + host + ":" + port + "/"
                        + dbName;
                break;
            case DBConstant.DBTypes.DB_TYPE_MSSQL:
                // Template: jdbc:sqlserver://[HOST]:[PORT1433];databaseName=[DB]
                connectionUrl += DBConstant.DBTypes.DB_TYPE_MSSQL_CONN + "://" + host + ":" + port
                        + ";databaseName=" + dbName;
                break;
            case DBConstant.DBTypes.DB_URL_JDBC_BASE:
                // Template: jdbc:mysql://HOST:PORT/DBname
                break;
            case DBConstant.DBTypes.DB_TYPE_POSTGRESSQL:
                // Template: jdbc:postgresql://HOST:PORT/DBname
                connectionUrl += DBConstant.DBTypes.DB_TYPE_POSTGRESSQL + "://" + host + ":" + port + "/"
                        + dbName;
                break;
            case DBConstant.DBTypes.DB_TYPE_DERBY:
                // Template: jdbc:derby://HOST:PORT/DBname
                connectionUrl += DBConstant.DBTypes.DB_TYPE_DERBY_CONN + "://" + host + ":" + port + "/"
                        + dbName;
                break;
            case DBConstant.DBTypes.DB_TYPE_H2:
                // Template: jdbc:h2:tcp://HOST/~/DBname
                connectionUrl += DBConstant.DBTypes.DB_TYPE_H2 + ":tcp://" + host + "/~/" + dbName;
                break;

            case DBConstant.DBTypes.DB_TYPE_ORACLE:
                // Template: jdbc:oracle:thin:@//<hostName>:<portNumber>/serviceName
                connectionUrl += DBConstant.DBTypes.DB_TYPE_ORACLE_CONN + ":@//" + host + ":" + port + "/"
                        + dbName;
                break;
        }
        return connectionUrl;
    }

}
