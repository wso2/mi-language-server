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

public class DBConstant {

    public static class DBTypes {

        public static final String DB_TYPE_MYSQL = "mysql";
        public static final String DB_TYPE_ORACLE = "oracle";
        public static final String DB_TYPE_ORACLE_CONN = "oracle:thin";
        public static final String DB_TYPE_MSSQL = "mssql";
        public static final String DB_TYPE_POSTGRESSQL = "postgresql";
        public static final String DB_TYPE_DERBY = "apachederby";
        public static final String DB_TYPE_DERBY_CONN = "derby";
        public static final String DB_TYPE_MSSQL_CONN = "sqlserver";
        public static final String DB_URL_JDBC_BASE = "jdbc:";
        public static final String DB_TYPE_H2 = "h2";
    }

    public static class DBUrlParams {

        public static final String DB_URL_JDBC_BASE = "jdbc:";
        public static final String DB_DRIVER_URL_BASE = "jar:file:";
        public static final String DB_DRIVER_JAR_BASE = "/dbdrivers/";
        public static final String DB_URL_JDBC_SUFFIX = "!/";
    }

    public static class DBDrivers {

        public static final String MYSQL_DRIVER = "com.mysql.cj.jdbc.Driver";
        public static final String DERBY_CLIENT_DRIVER = "org.apache.derby.jdbc.ClientDriver";
        public static final String MS_SQL_DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        public static final String ORACLE_DRIVER = "oracle.jdbc.driver.OracleDriver";
        public static final String DB2_DRIVER = "com.ibm.db2.jcc.DB2Driver";
        public static final String HSQL_DRIVER = "org.hsqldb.jdbcDriver";
        public static final String INFORMIX_DRIVER = "com.informix.jdbc.IfxDriver";
        public static final String POSTGRESQL_DRIVER = "org.postgresql.Driver";
        public static final String H2_DRIVER = "org.h2.Driver";
    }
}
