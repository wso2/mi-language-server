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

package org.eclipse.lemminx.customservice.syntaxmodel.factory;

import org.eclipse.lemminx.customservice.syntaxmodel.pojo.STNode;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.dataservice.AuthorizationProvider;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.dataservice.AuthorizationProviderProperty;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.dataservice.CallQuery;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.dataservice.CallQueryWithParam;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.dataservice.Config;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.dataservice.Data;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.dataservice.DataPolicy;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.dataservice.EventTrigger;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.dataservice.EventTriggerSubscriptions;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.dataservice.Expression;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.dataservice.Operation;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.dataservice.OperationElements;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.dataservice.Param;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.dataservice.ParamElements;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.dataservice.ParamValidateCustom;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.dataservice.ParamValidateDoubleRange;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.dataservice.ParamValidateLength;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.dataservice.ParamValidateLongRange;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.dataservice.ParamValidatePattern;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.dataservice.Property;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.dataservice.PropertyConfiguration;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.dataservice.PropertyConfigurationEntry;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.dataservice.PropertyProperty;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.dataservice.Query;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.dataservice.QueryElements;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.dataservice.QueryProperties;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.dataservice.Resource;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.dataservice.Sparql;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.dataservice.Sql;
import org.eclipse.lemminx.customservice.syntaxmodel.utils.Constant;
import org.eclipse.lemminx.customservice.syntaxmodel.utils.Utils;
import org.eclipse.lemminx.dom.DOMElement;
import org.eclipse.lemminx.dom.DOMNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DataServiceConfigFactory extends AbstractFactory {

    @Override
    public STNode create(DOMElement element) {

        Data dataService = new Data();
        dataService.elementNode(element);
        populateAttributes(dataService, element);

        List<DOMNode> children = element.getChildren();
        if (children != null && !children.isEmpty()) {
            List<STNode> elementList = new ArrayList<>();
            for (DOMNode child : children) {
                String childName = child.getNodeName();
                if (Constant.DESCRIPTION.equalsIgnoreCase(childName)) {
                    STNode description = new STNode();
                    description.elementNode((DOMElement) child);
                    elementList.add(description);
                } else if (Constant.CONFIG.equalsIgnoreCase(childName)) {
                    Config config = createConfig(child);
                    elementList.add(config);
                } else if (Constant.QUERY.equalsIgnoreCase(childName)) {
                    Query query = createQuery(child);
                    elementList.add(query);
                } else if (Constant.OPERATION.equalsIgnoreCase(childName)) {
                    Operation operation = createOperation(child);
                    elementList.add(operation);
                } else if (Constant.RESOURCE.equalsIgnoreCase(childName)) {
                    Resource resource = createResource(child);
                    elementList.add(resource);
                } else if (Constant.POLICY.equalsIgnoreCase(childName)) {
                    DataPolicy policy = createPolicy(child);
                    elementList.add(policy);
                } else if (Constant.EVENT_TRIGGER.equalsIgnoreCase(childName)) {
                    EventTrigger eventTrigger = createEventTrigger(child);
                    elementList.add(eventTrigger);
                } else if (Constant.ENABLE_SEC.equalsIgnoreCase(childName)) {
                    STNode enableSec = new STNode();
                    enableSec.elementNode((DOMElement) child);
                    elementList.add(enableSec);
                } else if (Constant.AUTHORIZATION_PROVIDER.equalsIgnoreCase(childName)) {
                    AuthorizationProvider authorizationProvider = createAuthorizationProvider(child);
                    elementList.add(authorizationProvider);
                }
            }
            dataService.setDescriptionOrConfigOrQuery(elementList);
        }
        return dataService;
    }

    private Config createConfig(DOMNode element) {

        Config config = new Config();
        config.elementNode((DOMElement) element);
        String id = element.getAttribute(Constant.ID);
        if (id != null && !id.isEmpty()) {
            config.setId(id);
        }
        String enableOData = element.getAttribute(Constant.ENABLE_ODATA);
        if (enableOData != null && !enableOData.isEmpty()) {
            config.setEnableOData(Boolean.parseBoolean(enableOData));
        }
        List<DOMNode> children = element.getChildren();
        if (children != null && !children.isEmpty()) {
            List<Property> propertyList = new ArrayList<>();
            for (DOMNode child : children) {
                String childName = child.getNodeName();
                if (Constant.PROPERTY.equalsIgnoreCase(childName)) {
                    Property property = createProperty(child);
                    propertyList.add(property);
                }
            }
            config.setProperty(propertyList.toArray(new Property[propertyList.size()]));
        }
        return config;
    }

    private Query createQuery(DOMNode element) {

        Query query = new Query();
        query.elementNode((DOMElement) element);
        String id = element.getAttribute(Constant.ID);
        if (id != null && !id.isEmpty()) {
            query.setId(id);
        }
        String useConfig = element.getAttribute(Constant.USE_CONFIG);
        if (useConfig != null && !useConfig.isEmpty()) {
            query.setUseConfig(useConfig);
        }
        String returnGeneratedKeys = element.getAttribute(Constant.RETURN_GENERATED_KEYS);
        if (returnGeneratedKeys != null && !returnGeneratedKeys.isEmpty()) {
            query.setReturnGeneratedKeys(Boolean.parseBoolean(returnGeneratedKeys));
        }
        String inputEventTrigger = element.getAttribute(Constant.INPUT_EVENT_TRIGGER);
        if (inputEventTrigger != null && !inputEventTrigger.isEmpty()) {
            query.setInputEventTrigger(inputEventTrigger);
        }
        String keyColumns = element.getAttribute(Constant.KEY_COLUMNS);
        if (keyColumns != null && !keyColumns.isEmpty()) {
            query.setKeyColumns(keyColumns);
        }
        String returnUpdatedRowCount = element.getAttribute(Constant.RETURN_UPDATED_ROW_COUNT);
        if (returnUpdatedRowCount != null && !returnUpdatedRowCount.isEmpty()) {
            query.setReturnUpdatedRowCount(returnUpdatedRowCount);
        }

        List<DOMNode> children = element.getChildren();
        if (children != null && !children.isEmpty()) {
            List<QueryElements> elementList = new ArrayList<>();
            for (DOMNode child : children) {
                String childName = child.getNodeName();
                QueryElements queryElements = new QueryElements();
                if (Constant.SQL.equalsIgnoreCase(childName)) {
                    Sql sql = createSql(child);
                    queryElements.setSql(Optional.ofNullable(sql));
                } else if (Constant.EXPRESSION.equalsIgnoreCase(childName)) {
                    Expression expression = createExpression(child);
                    queryElements.setExpression(Optional.ofNullable(expression));
                } else if (Constant.SPARQL.equalsIgnoreCase(childName)) {
                    Sparql sparql = createSparql(child);
                    queryElements.setSparql(Optional.ofNullable(sparql));
                } else if (Constant.PROPERTIES.equalsIgnoreCase(childName)) {
                    QueryProperties properties = createQueryProperties(child);
                    queryElements.setProperties(Optional.ofNullable(properties));
                } else if (Constant.RESULT.equalsIgnoreCase(childName)) {
                    STNode result = new STNode();
                    result.elementNode((DOMElement) child);
                    queryElements.setResult(Optional.ofNullable(result));
                } else if (Constant.PARAM.equalsIgnoreCase(childName)) {
                    Param param = createParam(child);
                    queryElements.setParam(Optional.ofNullable(param));
                }
                elementList.add(queryElements);
            }
            query.setQueryElements(elementList.toArray(new QueryElements[elementList.size()]));
        }
        return query;
    }

    private Sql createSql(DOMNode element) {

        Sql sql = new Sql();
        sql.elementNode((DOMElement) element);
        String dialect = element.getAttribute(Constant.DIALECT);
        if (dialect != null && !dialect.isEmpty()) {
            sql.setDialect(dialect);
        }
        DOMNode child = element.getFirstChild();
        String value = Utils.getInlineString(child);
        if (value != null) {
            sql.setValue(value);
        }
        return sql;
    }

    private Expression createExpression(DOMNode element) {

        Expression expression = new Expression();
        expression.elementNode((DOMElement) element);
        String dialect = element.getAttribute(Constant.DIALECT);
        if (dialect != null && !dialect.isEmpty()) {
            expression.setDialect(dialect);
        }
        DOMNode child = element.getFirstChild();
        String value = Utils.getInlineString(child);
        if (value != null) {
            expression.setValue(value);
        }
        return expression;
    }

    private Sparql createSparql(DOMNode element) {

        Sparql sparql = new Sparql();
        sparql.elementNode((DOMElement) element);
        DOMNode child = element.getFirstChild();
        String value = Utils.getInlineString(child);
        if (value != null) {
            sparql.setValue(value);
        }
        return sparql;
    }

    private QueryProperties createQueryProperties(DOMNode element) {

        QueryProperties properties = new QueryProperties();
        properties.elementNode((DOMElement) element);
        List<DOMNode> children = element.getChildren();
        if (children != null && !children.isEmpty()) {
            List<Property> propertyList = new ArrayList<>();
            for (DOMNode child : children) {
                String childName = child.getNodeName();
                if (Constant.PROPERTY.equalsIgnoreCase(childName)) {
                    Property property = createProperty(child);
                    property.elementNode((DOMElement) child);
                    propertyList.add(property);
                }
            }
            properties.setProperty(propertyList.toArray(new Property[propertyList.size()]));
        }
        return properties;
    }

    private Property createProperty(DOMNode element) {

        Property property = new Property();
        property.elementNode((DOMElement) element);
        String name = element.getAttribute(Constant.NAME);
        if (name != null && !name.isEmpty()) {
            property.setName(name);
        }
        List<DOMNode> children = element.getChildren();
        if (children != null && !children.isEmpty()) {
            List<PropertyProperty> propertyList = new ArrayList<>();
            List<PropertyConfiguration> configurationList = new ArrayList<>();
            for (DOMNode child : children) {
                String childName = child.getNodeName();
                if (Constant.CONFIGURATION.equalsIgnoreCase(childName)) {
                    PropertyConfiguration configuration = createPropertyConfiguration(child);
                    configurationList.add(configuration);
                } else if (Constant.PROPERTY.equalsIgnoreCase(childName)) {
                    PropertyProperty propertyProperty = createPropertyProperty(child);
                    propertyList.add(propertyProperty);
                }
            }
            property.setProperty(propertyList.toArray(new PropertyProperty[propertyList.size()]));
            property.setConfiguration(configurationList.toArray(new PropertyConfiguration[configurationList.size()]));
        }
        return property;
    }

    private PropertyConfiguration createPropertyConfiguration(DOMNode element) {

        PropertyConfiguration configuration = new PropertyConfiguration();
        configuration.elementNode((DOMElement) element);

        List<DOMNode> children = element.getChildren();
        if (children != null && !children.isEmpty()) {
            List<PropertyConfigurationEntry> entryList = new ArrayList<>();
            for (DOMNode child : children) {
                String childName = child.getNodeName();
                if (Constant.ENTRY.equalsIgnoreCase(childName)) {
                    PropertyConfigurationEntry entry = createPropertyConfigurationEntry(child);
                    entryList.add(entry);
                }
            }
            configuration.setEntry(entryList.toArray(new PropertyConfigurationEntry[entryList.size()]));
        }
        return configuration;
    }

    private PropertyConfigurationEntry createPropertyConfigurationEntry(DOMNode element) {

        PropertyConfigurationEntry entry = new PropertyConfigurationEntry();
        entry.elementNode((DOMElement) element);
        String request = element.getAttribute(Constant.REQUEST);
        if (request != null && !request.isEmpty()) {
            entry.setRequest(request);
        }
        List<DOMNode> children = element.getChildren();
        if (children != null && !children.isEmpty()) {
            for (DOMNode child : children) {
                String childName = child.getNodeName();
                if (Constant.USERNAME.equalsIgnoreCase(childName)) {
                    STNode username = new STNode();
                    username.elementNode((DOMElement) child);
                    entry.setUsername(username);
                } else if (Constant.PASSWORD.equalsIgnoreCase(childName)) {
                    STNode password = new STNode();
                    password.elementNode((DOMElement) child);
                    entry.setPassword(password);
                }
            }
        }
        return entry;
    }

    private PropertyProperty createPropertyProperty(DOMNode element) {

        PropertyProperty propertyProperty = new PropertyProperty();
        propertyProperty.elementNode((DOMElement) element);
        String name = element.getAttribute(Constant.NAME);
        if (name != null && !name.isEmpty()) {
            propertyProperty.setName(name);
        }
        DOMNode child = element.getFirstChild();
        String value = Utils.getInlineString(child);
        if (value != null && !value.isEmpty()) {
            propertyProperty.setValue(value);
        }
        return propertyProperty;
    }

    private Param createParam(DOMNode element) {

        Param param = new Param();
        param.elementNode((DOMElement) element);
        String name = element.getAttribute(Constant.NAME);
        if (name != null && !name.isEmpty()) {
            param.setName(name);
        }
        String sqlType = element.getAttribute(Constant.SQL_TYPE);
        if (sqlType != null && !sqlType.isEmpty()) {
            param.setSqlType(sqlType);
        }
        String paramType = element.getAttribute(Constant.PARAM_TYPE);
        if (paramType != null && !paramType.isEmpty()) {
            param.setParamType(paramType);
        }
        String type = element.getAttribute(Constant.TYPE);
        if (type != null && !type.isEmpty()) {
            param.setType(type);
        }
        String ordinal = element.getAttribute(Constant.ORDINAL);
        if (ordinal != null && !ordinal.isEmpty()) {
            param.setOrdinal(Utils.parseInt(ordinal));
        }
        String defaultValue = element.getAttribute(Constant.DEFAULT_VALUE);
        if (defaultValue != null && !defaultValue.isEmpty()) {
            param.setDefaultValue(defaultValue);
        }
        List<DOMNode> children = element.getChildren();
        if (children != null && !children.isEmpty()) {
            List<ParamElements> elementList = new ArrayList<>();
            for (DOMNode child : children) {
                String childName = child.getNodeName();
                ParamElements paramElements = new ParamElements();
                if (Constant.VALIDATE_CUSTOM.equalsIgnoreCase(childName)) {
                    ParamValidateCustom validateCustom = createParamValidateCustom(child);
                    paramElements.setValidateCustom(Optional.ofNullable(validateCustom));
                } else if (Constant.VALIDATE_LENGTH.equalsIgnoreCase(childName)) {
                    ParamValidateLength validateLength = createParamValidateLength(child);
                    paramElements.setValidateLength(Optional.ofNullable(validateLength));
                } else if (Constant.VALIDATE_PATTERN.equalsIgnoreCase(childName)) {
                    ParamValidatePattern validatePattern = createParamValidatePattern(child);
                    paramElements.setValidatePattern(Optional.ofNullable(validatePattern));
                } else if (Constant.VALIDATE_LONG_RANGE.equalsIgnoreCase(childName)) {
                    ParamValidateLongRange validateLongRange = createParamValidateLongRange(child);
                    paramElements.setValidateLongRange(Optional.ofNullable(validateLongRange));
                } else if (Constant.VALIDATE_DOUBLE_RANGE.equalsIgnoreCase(childName)) {
                    ParamValidateDoubleRange validateDoubleRange = createParamValidateDoubleRange(child);
                    paramElements.setValidateDoubleRange(Optional.ofNullable(validateDoubleRange));
                }
            }
            param.setParamElements(elementList.toArray(new ParamElements[elementList.size()]));
        }
        return param;
    }

    private ParamValidateCustom createParamValidateCustom(DOMNode element) {

        ParamValidateCustom validateCustom = new ParamValidateCustom();
        validateCustom.elementNode((DOMElement) element);
        String className = element.getAttribute(Constant.CLASS);
        if (className != null && !className.isEmpty()) {
            validateCustom.setClazz(className);
        }
        return validateCustom;
    }

    private ParamValidateLength createParamValidateLength(DOMNode element) {

        ParamValidateLength validateLength = new ParamValidateLength();
        validateLength.elementNode((DOMElement) element);
        String minimum = element.getAttribute(Constant.MINIMUM);
        if (minimum != null && !minimum.isEmpty()) {
            validateLength.setMinimum(Utils.parseInt(minimum));
        }
        String maximum = element.getAttribute(Constant.MAXIMUM);
        if (maximum != null && !maximum.isEmpty()) {
            validateLength.setMaximum(Utils.parseInt(maximum));
        }
        return validateLength;
    }

    private ParamValidatePattern createParamValidatePattern(DOMNode element) {

        ParamValidatePattern validatePattern = new ParamValidatePattern();
        validatePattern.elementNode((DOMElement) element);
        String pattern = element.getAttribute(Constant.PATTERN);
        if (pattern != null && !pattern.isEmpty()) {
            validatePattern.setPattern(pattern);
        }
        return validatePattern;
    }

    private ParamValidateLongRange createParamValidateLongRange(DOMNode element) {

        ParamValidateLongRange validateLongRange = new ParamValidateLongRange();
        validateLongRange.elementNode((DOMElement) element);
        String minimum = element.getAttribute(Constant.MINIMUM);
        if (minimum != null && !minimum.isEmpty()) {
            validateLongRange.setMinimum(Utils.parseInt(minimum));
        }
        String maximum = element.getAttribute(Constant.MAXIMUM);
        if (maximum != null && !maximum.isEmpty()) {
            validateLongRange.setMaximum(Utils.parseInt(maximum));
        }
        return validateLongRange;
    }

    private ParamValidateDoubleRange createParamValidateDoubleRange(DOMNode element) {

        ParamValidateDoubleRange validateDoubleRange = new ParamValidateDoubleRange();
        validateDoubleRange.elementNode((DOMElement) element);
        String minimum = element.getAttribute(Constant.MINIMUM);
        if (minimum != null && !minimum.isEmpty()) {
            validateDoubleRange.setMinimum(Utils.parseInt(minimum));
        }
        String maximum = element.getAttribute(Constant.MAXIMUM);
        if (maximum != null && !maximum.isEmpty()) {
            validateDoubleRange.setMaximum(Utils.parseInt(maximum));
        }
        return validateDoubleRange;
    }

    private Operation createOperation(DOMNode element) {

        Operation operation = new Operation();
        operation.elementNode((DOMElement) element);
        String name = element.getAttribute(Constant.NAME);
        if (name != null && !name.isEmpty()) {
            operation.setName(name);
        }
        String disableStreaming = element.getAttribute(Constant.DISABLE_STREAMING);
        if (disableStreaming != null && !disableStreaming.isEmpty()) {
            operation.setDisableStreaming(Boolean.parseBoolean(disableStreaming));
        }
        String returnRequestStatus = element.getAttribute(Constant.RETURN_REQUEST_STATUS);
        if (returnRequestStatus != null && !returnRequestStatus.isEmpty()) {
            operation.setReturnRequestStatus(Boolean.parseBoolean(returnRequestStatus));
        }
        List<DOMNode> children = element.getChildren();
        if (children != null && !children.isEmpty()) {
            List<OperationElements> elementList = new ArrayList<>();
            for (DOMNode child : children) {
                String childName = child.getNodeName();
                OperationElements operationElements = new OperationElements();
                if (Constant.DESCRIPTION.equalsIgnoreCase(childName)) {
                    STNode description = new STNode();
                    description.elementNode((DOMElement) child);
                    operationElements.setDescription(Optional.ofNullable(description));
                } else if (Constant.CALL_QUERY.equalsIgnoreCase(childName)) {
                    CallQuery callQuery = createCallQuery(child);
                    operationElements.setCall_query(Optional.ofNullable(callQuery));
                }
                elementList.add(operationElements);
            }
            operation.setOperationElements(elementList.toArray(new OperationElements[elementList.size()]));
        }
        return operation;
    }

    private Resource createResource(DOMNode element) {

        Resource resource = new Resource();
        resource.elementNode((DOMElement) element);
        String path = element.getAttribute(Constant.PATH);
        if (path != null && !path.isEmpty()) {
            resource.setPath(path);
        }
        String method = element.getAttribute(Constant.METHOD);
        if (method != null && !method.isEmpty()) {
            resource.setMethod(method);
        }
        String disableStreaming = element.getAttribute(Constant.DISABLE_STREAMING);
        if (disableStreaming != null && !disableStreaming.isEmpty()) {
            resource.setDisableStreaming(Boolean.parseBoolean(disableStreaming));
        }
        String returnRequestStatus = element.getAttribute(Constant.RETURN_REQUEST_STATUS);
        if (returnRequestStatus != null && !returnRequestStatus.isEmpty()) {
            resource.setReturnRequestStatus(Boolean.parseBoolean(returnRequestStatus));
        }
        List<DOMNode> children = element.getChildren();
        if (children != null && !children.isEmpty()) {
            for (DOMNode child : children) {
                String childName = child.getNodeName();
                if (Constant.DESCRIPTION.equalsIgnoreCase(childName)) {
                    STNode description = new STNode();
                    description.elementNode((DOMElement) child);
                    resource.setDescription(description);
                } else if (Constant.CALL_QUERY.equalsIgnoreCase(childName)) {
                    CallQuery callQuery = createCallQuery(child);
                    resource.setCallQuery(callQuery);
                }
            }
        }
        return resource;
    }

    private CallQuery createCallQuery(DOMNode element) {

        CallQuery callQuery = new CallQuery();
        callQuery.elementNode((DOMElement) element);
        String href = element.getAttribute(Constant.HREF);
        if (href != null && !href.isEmpty()) {
            callQuery.setHref(href);
        }
        String requiredRoles = element.getAttribute(Constant.REQUIRED_ROLES);
        if (requiredRoles != null && !requiredRoles.isEmpty()) {
            callQuery.setRequiredRoles(requiredRoles);
        }
        List<DOMNode> children = element.getChildren();
        if (children != null && !children.isEmpty()) {
            List<STNode> withParamList = new ArrayList<>();
            for (DOMNode child : children) {
                String childName = child.getNodeName();
                if (Constant.WITH_PARAM.equalsIgnoreCase(childName)) {
                    CallQueryWithParam withParam = createCallQueryWithParam(child);
                    withParamList.add(withParam);
                }
            }
            callQuery.setWithParam(withParamList.toArray(new CallQueryWithParam[withParamList.size()]));
        }
        return callQuery;
    }

    private CallQueryWithParam createCallQueryWithParam(DOMNode element) {

        CallQueryWithParam withParam = new CallQueryWithParam();
        withParam.elementNode((DOMElement) element);
        String name = element.getAttribute(Constant.NAME);
        if (name != null && !name.isEmpty()) {
            withParam.setName(name);
        }
        String query_param = element.getAttribute(Constant.QUERY_PARAM);
        if (query_param != null && !query_param.isEmpty()) {
            withParam.setQueryParam(query_param);
        }
        String column = element.getAttribute(Constant.COLUMN);
        if (column != null && !column.isEmpty()) {
            withParam.setColumn(column);
        }
        String param = element.getAttribute(Constant.PARAM);
        if (param != null && !param.isEmpty()) {
            withParam.setParam(param);
        }
        return withParam;
    }

    private DataPolicy createPolicy(DOMNode element) {

        DataPolicy policy = new DataPolicy();
        policy.elementNode((DOMElement) element);
        String key = element.getAttribute(Constant.KEY);
        if (key != null && !key.isEmpty()) {
            policy.setKey(key);
        }
        return policy;
    }

    private EventTrigger createEventTrigger(DOMNode element) {

        EventTrigger eventTrigger = new EventTrigger();
        eventTrigger.elementNode((DOMElement) element);
        String id = element.getAttribute(Constant.ID);
        if (id != null && !id.isEmpty()) {
            eventTrigger.setId(id);
        }
        String language = element.getAttribute(Constant.LANGUAGE);
        if (language != null && !language.isEmpty()) {
            eventTrigger.setLanguage(language);
        }
        List<DOMNode> children = element.getChildren();
        if (children != null && !children.isEmpty()) {
            List<STNode> eventTriggerElements = new ArrayList<>();
            for (DOMNode child : children) {
                String childName = child.getNodeName();
                if (Constant.EXPRESSION.equalsIgnoreCase(childName)) {
                    STNode expression = new STNode();
                    expression.elementNode((DOMElement) child);
                    eventTriggerElements.add(expression);
                } else if (Constant.TARGET_TOPIC.equalsIgnoreCase(childName)) {
                    STNode targetTopic = new STNode();
                    targetTopic.elementNode((DOMElement) child);
                    eventTriggerElements.add(targetTopic);
                } else if (Constant.SUBSCRIPTIONS.equalsIgnoreCase(childName)) {
                    EventTriggerSubscriptions subscriptions = createEventTriggerSubscriptions(child);
                    eventTriggerElements.add(subscriptions);
                }
            }
            eventTrigger.setEventTriggerElements(eventTriggerElements);
        }
        return eventTrigger;
    }

    private EventTriggerSubscriptions createEventTriggerSubscriptions(DOMNode element) {

        EventTriggerSubscriptions subscriptions = new EventTriggerSubscriptions();
        subscriptions.elementNode((DOMElement) element);
        DOMNode child = element.getFirstChild();
        if (child != null && child instanceof DOMElement) {
            STNode subscription = new STNode();
            subscription.elementNode((DOMElement) child);
            subscriptions.setSubscription(subscription);
        }
        return subscriptions;
    }

    private AuthorizationProvider createAuthorizationProvider(DOMNode element) {

        AuthorizationProvider authorizationProvider = new AuthorizationProvider();
        authorizationProvider.elementNode((DOMElement) element);
        String clazz = element.getAttribute(Constant.CLASS);
        if (clazz != null && !clazz.isEmpty()) {
            authorizationProvider.setClazz(clazz);
        }
        List<DOMNode> children = element.getChildren();
        if (children != null && !children.isEmpty()) {
            List<STNode> properties = new ArrayList<>();
            for (DOMNode child : children) {
                String childName = child.getNodeName();
                if (Constant.PROPERTY.equalsIgnoreCase(childName)) {
                    AuthorizationProviderProperty property = createAuthorizationProviderProperty(child);
                    properties.add(property);
                }
            }
            authorizationProvider.setProperty(properties.toArray(new AuthorizationProviderProperty[properties.size()]));
        }
        return authorizationProvider;
    }

    private AuthorizationProviderProperty createAuthorizationProviderProperty(DOMNode element) {

        AuthorizationProviderProperty property = new AuthorizationProviderProperty();
        property.elementNode((DOMElement) element);
        String name = element.getAttribute(Constant.NAME);
        if (name != null && !name.isEmpty()) {
            property.setName(name);
        }
        DOMNode child = element.getFirstChild();
        String value = Utils.getInlineString(child);
        if (value != null && !value.isEmpty()) {
            property.setValue(value);
        }
        return property;
    }

    @Override
    public void populateAttributes(STNode node, DOMElement element) {

        String baseURI = element.getAttribute(Constant.BASE_URI);
        if (baseURI != null && !baseURI.isEmpty()) {
            ((Data) node).setBaseURI(baseURI);
        }
        String name = element.getAttribute(Constant.NAME);
        if (name != null && !name.isEmpty()) {
            ((Data) node).setName(name);
        }
        String enableBatchRequests = element.getAttribute(Constant.ENABLE_BATCH_REQUESTS);
        if (enableBatchRequests != null && !enableBatchRequests.isEmpty()) {
            ((Data) node).setEnableBatchRequests(Boolean.parseBoolean(enableBatchRequests));
        }
        String enableBoxcarring = element.getAttribute(Constant.ENABLE_BOXCARRING);
        if (enableBoxcarring != null && !enableBoxcarring.isEmpty()) {
            ((Data) node).setEnableBoxcarring(Boolean.parseBoolean(enableBoxcarring));
        }
        String disableLegacyBoxcarringMode = element.getAttribute(Constant.DISABLE_LEGACY_BOXCARRING_MODE);
        if (disableLegacyBoxcarringMode != null && !disableLegacyBoxcarringMode.isEmpty()) {
            ((Data) node).setDisableLegacyBoxcarringMode(Boolean.parseBoolean(disableLegacyBoxcarringMode));
        }
        String disableStreaming = element.getAttribute(Constant.DISABLE_STREAMING);
        if (disableStreaming != null && !disableStreaming.isEmpty()) {
            ((Data) node).setDisableStreaming(Boolean.parseBoolean(disableStreaming));
        }
        String txManagerJNDIName = element.getAttribute(Constant.TX_MANAGER_JNDI_NAME);
        if (txManagerJNDIName != null && !txManagerJNDIName.isEmpty()) {
            ((Data) node).setTxManagerJNDIName(txManagerJNDIName);
        }
        String serviceNamespace = element.getAttribute(Constant.SERVICE_NAMESPACE);
        if (serviceNamespace != null && !serviceNamespace.isEmpty()) {
            ((Data) node).setServiceNamespace(serviceNamespace);
        }
        String serviceGroup = element.getAttribute(Constant.SERVICE_GROUP);
        if (serviceGroup != null && !serviceGroup.isEmpty()) {
            ((Data) node).setServiceGroup(serviceGroup);
        }
        String publishSwagger = element.getAttribute(Constant.PUBLISH_SWAGGER);
        if (publishSwagger != null && !publishSwagger.isEmpty()) {
            ((Data) node).setPublishSwagger(publishSwagger);
        }
        String transports = element.getAttribute(Constant.TRANSPORTS);
        if (transports != null && !transports.isEmpty()) {
            ((Data) node).setTransports(transports);
        }
        String serviceStatus = element.getAttribute(Constant.SERVICE_STATUS);
        if (serviceStatus != null && !serviceStatus.isEmpty()) {
            ((Data) node).setServiceStatus(serviceStatus);
        }
    }
}
