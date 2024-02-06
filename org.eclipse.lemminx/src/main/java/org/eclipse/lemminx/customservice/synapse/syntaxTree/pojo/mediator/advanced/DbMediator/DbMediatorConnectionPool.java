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

package org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.advanced.DbMediator;

import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.STNode;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.advanced.KeyAttribute;

public class DbMediatorConnectionPool extends STNode {

    KeyAttribute dsName;
    KeyAttribute icClass;
    KeyAttribute driver;
    KeyAttribute url;
    KeyAttribute user;
    KeyAttribute password;
    DbMediatorConnectionPoolProperty[] property;

    public KeyAttribute getDsName() {

        return dsName;
    }

    public void setDsName(KeyAttribute dsName) {

        this.dsName = dsName;
    }

    public KeyAttribute getIcClass() {

        return icClass;
    }

    public void setIcClass(KeyAttribute icClass) {

        this.icClass = icClass;
    }

    public KeyAttribute getDriver() {

        return driver;
    }

    public void setDriver(KeyAttribute driver) {

        this.driver = driver;
    }

    public KeyAttribute getUrl() {

        return url;
    }

    public void setUrl(KeyAttribute url) {

        this.url = url;
    }

    public KeyAttribute getUser() {

        return user;
    }

    public void setUser(KeyAttribute user) {

        this.user = user;
    }

    public KeyAttribute getPassword() {

        return password;
    }

    public void setPassword(KeyAttribute password) {

        this.password = password;
    }

    public DbMediatorConnectionPoolProperty[] getProperty() {

        return property;
    }

    public void setProperty(DbMediatorConnectionPoolProperty[] property) {

        this.property = property;
    }
}