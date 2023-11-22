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

package org.eclipse.lemminx.customservice.syntaxmodel.factory.mediators.advanced;

import org.eclipse.lemminx.customservice.syntaxmodel.factory.mediators.AbstractMediatorFactory;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.STNode;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.mediator.Mediator;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.mediator.advanced.DbMediator.DbMediator;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.mediator.advanced.DbMediator.DbMediatorConnection;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.mediator.advanced.DbMediator.DbMediatorConnectionPool;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.mediator.advanced.DbMediator.DbMediatorConnectionPoolProperty;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.mediator.advanced.DbMediator.DbMediatorStatement;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.mediator.advanced.DbMediator.DbMediatorStatementParameter;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.mediator.advanced.DbMediator.DbMediatorStatementResult;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.mediator.advanced.KeyAttribute;
import org.eclipse.lemminx.customservice.syntaxmodel.utils.Constant;
import org.eclipse.lemminx.customservice.syntaxmodel.utils.Utils;
import org.eclipse.lemminx.dom.DOMElement;
import org.eclipse.lemminx.dom.DOMNode;

import java.util.ArrayList;
import java.util.List;

public abstract class DBMediatorFactory extends AbstractMediatorFactory {

    @Override
    public Mediator createSpecificMediator(DOMElement element) {

        DbMediator dbMediator = new DbMediator();
        dbMediator.elementNode(element);
        populateAttributes(dbMediator, element);
        List<DOMNode> children = element.getChildren();
        List<DbMediatorStatement> dbMediatorStatementList = new ArrayList<>();
        for (DOMNode node : children) {
            if (node.getNodeName().equalsIgnoreCase(Constant.CONNECTION)) {
                DbMediatorConnection dbMediatorConnection = createDbMediatorConnection((DOMElement) node);
                dbMediator.setConnection(dbMediatorConnection);
            } else if (node.getNodeName().equalsIgnoreCase(Constant.STATEMENT)) {
                DbMediatorStatement dbMediatorStatement = createDbMediatorStatement((DOMElement) node);
                dbMediatorStatementList.add(dbMediatorStatement);
            } else {
                //invalid configuration
            }
        }
        dbMediator.setStatement(dbMediatorStatementList.toArray(
                new DbMediatorStatement[dbMediatorStatementList.size()]));

        return dbMediator;
    }

    @Override
    public void populateAttributes(STNode node, DOMElement element) {

        DbMediator dbMediator = (DbMediator) node;
        String useTransaction = element.getAttribute(Constant.USE_TRANSACTION);
        if (useTransaction != null) {
            dbMediator.setUseTransaction(Boolean.parseBoolean(useTransaction));
        }
        String description = element.getAttribute(Constant.DESCRIPTION);
        if (description != null) {
            dbMediator.setDescription(description);
        }
    }

    private DbMediatorConnection createDbMediatorConnection(DOMElement element) {

        DbMediatorConnection dbMediatorConnection = new DbMediatorConnection();
        dbMediatorConnection.elementNode(element);
        List<DOMNode> children = element.getChildren();
        for (DOMNode node : children) {
            if (node.getNodeName().equalsIgnoreCase(Constant.POOL)) {
                DbMediatorConnectionPool dbMediatorPool = createDbMediatorPool((DOMElement) node);
                dbMediatorConnection.setPool(dbMediatorPool);
            } else {
                //invalid configuration
            }
        }
        return dbMediatorConnection;
    }

    private DbMediatorConnectionPool createDbMediatorPool(DOMElement element) {

        DbMediatorConnectionPool dbMediatorPool = new DbMediatorConnectionPool();
        dbMediatorPool.elementNode(element);
        List<DOMNode> children = element.getChildren();
        List<DbMediatorConnectionPoolProperty> dbMediatorConnectionPoolPropertyList = new ArrayList<>();
        for (DOMNode node : children) {
            if (node.getNodeName().equalsIgnoreCase(Constant.DS_NAME)) {
                KeyAttribute dbMediatorDsName = createKeyAttribute((DOMElement) node);
                dbMediatorPool.setDsName(dbMediatorDsName);
            } else if (node.getNodeName().equalsIgnoreCase(Constant.IC_CLASS)) {
                KeyAttribute dbMediatorIcClass = createKeyAttribute((DOMElement) node);
                dbMediatorPool.setIcClass(dbMediatorIcClass);
            } else if (node.getNodeName().equalsIgnoreCase(Constant.DRIVER)) {
                KeyAttribute dbMediatorDriver = createKeyAttribute((DOMElement) node);
                dbMediatorPool.setDriver(dbMediatorDriver);
            } else if (node.getNodeName().equalsIgnoreCase(Constant.URL)) {
                KeyAttribute dbMediatorUrl = createKeyAttribute((DOMElement) node);
                dbMediatorPool.setUrl(dbMediatorUrl);
            } else if (node.getNodeName().equalsIgnoreCase(Constant.USER)) {
                KeyAttribute dbMediatorUser = createKeyAttribute((DOMElement) node);
                dbMediatorPool.setUser(dbMediatorUser);
            } else if (node.getNodeName().equalsIgnoreCase(Constant.PASSWORD)) {
                KeyAttribute dbMediatorPassword = createKeyAttribute((DOMElement) node);
                dbMediatorPool.setPassword(dbMediatorPassword);
            } else if (node.getNodeName().equalsIgnoreCase(Constant.PROPERTY)) {
                DbMediatorConnectionPoolProperty dbMediatorProperty = createDbMediatorProperty((DOMElement) node);
                dbMediatorConnectionPoolPropertyList.add(dbMediatorProperty);
            } else {
                //invalid configuration
            }
            dbMediatorPool.setProperty(dbMediatorConnectionPoolPropertyList.toArray(
                    new DbMediatorConnectionPoolProperty[dbMediatorConnectionPoolPropertyList.size()]));
        }
        return dbMediatorPool;
    }

    private DbMediatorConnectionPoolProperty createDbMediatorProperty(DOMElement node) {

        DbMediatorConnectionPoolProperty dbMediatorProperty = new DbMediatorConnectionPoolProperty();
        dbMediatorProperty.elementNode(node);
        String name = node.getAttribute(Constant.NAME);
        if (name != null) {
            dbMediatorProperty.setName(name);
        }
        String value = node.getAttribute(Constant.VALUE);
        if (value != null) {
            dbMediatorProperty.setValue(value);
        }
        return dbMediatorProperty;
    }

    private KeyAttribute createKeyAttribute(DOMElement node) {

        KeyAttribute keyAttribute = new KeyAttribute();
        keyAttribute.elementNode(node);
        String key = node.getAttribute(Constant.KEY);
        if (key != null) {
            keyAttribute.setKey(key);
        }
        String value = node.getNodeValue();
        keyAttribute.setValue(value);
        return keyAttribute;
    }

    private DbMediatorStatement createDbMediatorStatement(DOMElement element) {

        DbMediatorStatement dbMediatorStatement = new DbMediatorStatement();
        dbMediatorStatement.elementNode(element);
        List<DOMNode> children = element.getChildren();
        List<DbMediatorStatementParameter> dbMediatorStatementParameterList = new ArrayList<>();
        List<DbMediatorStatementResult> dbMediatorStatementResultList = new ArrayList<>();
        for (DOMNode node : children) {
            if (node.getNodeName().equalsIgnoreCase(Constant.SQL)) {
                String sql = Utils.getInlineString(node.getFirstChild());
                dbMediatorStatement.setSql(sql);
            } else if (node.getNodeName().equalsIgnoreCase(Constant.PARAMETER)) {
                DbMediatorStatementParameter dbMediatorStatementParameter = createDbMediatorStatementParameter((DOMElement) node);
                dbMediatorStatementParameterList.add(dbMediatorStatementParameter);
            } else if (node.getNodeName().equalsIgnoreCase(Constant.RESULT)) {
                DbMediatorStatementResult dbMediatorStatementResult = createDbMediatorStatementResult((DOMElement) node);
                dbMediatorStatementResultList.add(dbMediatorStatementResult);
            } else {
                //invalid configuration
            }
        }
        return dbMediatorStatement;
    }

    private DbMediatorStatementParameter createDbMediatorStatementParameter(DOMElement node) {

        DbMediatorStatementParameter dbMediatorStatementParameter = new DbMediatorStatementParameter();
        dbMediatorStatementParameter.elementNode(node);
        String type = node.getAttribute(Constant.TYPE);
        if (type != null) {
            dbMediatorStatementParameter.setType(type);
        }
        String value = node.getAttribute(Constant.VALUE);
        if (value != null) {
            dbMediatorStatementParameter.setValue(value);
        }
        String expression = node.getAttribute(Constant.EXPRESSION);
        if (expression != null) {
            dbMediatorStatementParameter.setExpression(expression);
        }
        return dbMediatorStatementParameter;
    }

    private DbMediatorStatementResult createDbMediatorStatementResult(DOMElement node) {

        DbMediatorStatementResult dbMediatorStatementResult = new DbMediatorStatementResult();
        dbMediatorStatementResult.elementNode(node);
        String name = node.getAttribute(Constant.NAME);
        if (name != null) {
            dbMediatorStatementResult.setName(name);
        }
        String column = node.getAttribute(Constant.COLUMN);
        if (column != null) {
            dbMediatorStatementResult.setColumn(column);
        }
        return dbMediatorStatementResult;
    }
}
