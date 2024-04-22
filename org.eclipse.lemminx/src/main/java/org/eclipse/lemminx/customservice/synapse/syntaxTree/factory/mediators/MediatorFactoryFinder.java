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

package org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators;

import org.eclipse.lemminx.SynapseLanguageService;
import org.eclipse.lemminx.customservice.synapse.connectors.ConnectorHolder;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.advanced.CacheFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.advanced.CloneFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.advanced.DBLookupFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.advanced.DBReportFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.advanced.DataServiceFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.advanced.EnqueueFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.advanced.EventFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.advanced.TransactionFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.core.CallFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.core.CallOutFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.core.CallTemplateFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.core.DropFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.core.HeaderFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.core.LogFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.core.LoopbackFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.core.PropertyFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.core.PropertyGroupFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.core.RespondFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.core.SendFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.core.StoreFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.core.ValidateFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.eip.AggregateFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.eip.ForeachFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.eip.IterateFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.extension.BeanFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.extension.ClassFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.extension.EjbFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.extension.PojoCommandFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.extension.ScriptFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.extension.SpringFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.filter.ConditionalRouterFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.filter.FilterFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.filter.SwitchFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.filter.ThrottleFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.other.BamFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.other.BuilderFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.other.EntitlementFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.other.NtlmFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.other.OauthServiceFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.other.PublishEventFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.other.RuleFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.transformation.DataMapperFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.transformation.EnrichFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.transformation.FastXSLTFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.transformation.FaultFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.transformation.JsonTransformFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.transformation.PayloadFactoryFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.transformation.RewriteFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.transformation.SmooksFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.transformation.XqueryFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.factory.mediators.transformation.XsltFactory;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.Mediator;
import org.eclipse.lemminx.customservice.synapse.utils.Constant;
import org.eclipse.lemminx.dom.DOMElement;
import org.eclipse.lemminx.dom.DOMNode;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

//adapted from org.apache.synapse.config.xml.MediatorFactoryFinder
public class MediatorFactoryFinder {

    private static final Logger log = Logger.getLogger(MediatorFactoryFinder.class.getName());
    private static final Class[] mediatorFactories = {
            CacheFactory.class,
            CloneFactory.class,
            DataServiceFactory.class,
            DBLookupFactory.class,
            DBReportFactory.class,
            EnqueueFactory.class,
            EventFactory.class,
            TransactionFactory.class,
            CallTemplateFactory.class,
            CallFactory.class,
            CallOutFactory.class,
            DropFactory.class,
            HeaderFactory.class,
            LogFactory.class,
            LoopbackFactory.class,
            PropertyFactory.class,
            PropertyGroupFactory.class,
            RespondFactory.class,
            SendFactory.class,
            StoreFactory.class,
            ValidateFactory.class,
            AggregateFactory.class,
            ForeachFactory.class,
            IterateFactory.class,
            BeanFactory.class,
            ClassFactory.class,
            PojoCommandFactory.class,
            EjbFactory.class,
            ScriptFactory.class,
            SpringFactory.class,
            ConditionalRouterFactory.class,
            FilterFactory.class,
            SwitchFactory.class,
            ThrottleFactory.class,
            BamFactory.class,
            BuilderFactory.class,
            OauthServiceFactory.class,
            EntitlementFactory.class,
            PublishEventFactory.class,
            RuleFactory.class,
            DataMapperFactory.class,
            EnrichFactory.class,
            FastXSLTFactory.class,
            FaultFactory.class,
            JsonTransformFactory.class,
            PayloadFactoryFactory.class,
            SmooksFactory.class,
            XqueryFactory.class,
            XsltFactory.class,
            NtlmFactory.class,
            RewriteFactory.class,
            FilterSequenceFactory.class,
            ConnectorFactory.class
    };

    private final static MediatorFactoryFinder instance = new MediatorFactoryFinder();
    private static Map<String, AbstractMediatorFactory> factoryMap = new HashMap<>();
    private static boolean initialized = false;

    public static synchronized MediatorFactoryFinder getInstance() {

        if (!initialized) {
            loadMediatorFactories();
        }
        return instance;
    }

    private MediatorFactoryFinder() {

    }

    private static void loadMediatorFactories() {

        for (Class c : mediatorFactories) {
            try {
                AbstractMediatorFactory fac = (AbstractMediatorFactory) c.newInstance();
                factoryMap.put(fac.getTagName().toLowerCase(), fac);
            } catch (Exception e) {
                log.log(Level.SEVERE, "Error instantiating " + c.getName(), e);
            }
        }
        initialized = true;
    }

    public Mediator getMediator(DOMNode node) {

        if (!node.getNodeName().equalsIgnoreCase(Constant.COMMENT)) {
            String mediatorName = node.getNodeName().toLowerCase();
            ConnectorHolder connectorHolder = getConnectorHolder();
            if (mediatorName.contains(Constant.DOT) && connectorHolder.isValidConnector(mediatorName)) {
                mediatorName = Constant.CONNECTOR;
            }
            AbstractMediatorFactory factory = factoryMap.get(mediatorName);
            if (factory != null) {
                Mediator mediator = (Mediator) factory.create((DOMElement) node);
                mediator.elementNode((DOMElement) node);
                return mediator;
            }
        }
        return null;
    }

    public ConnectorHolder getConnectorHolder() {

        return SynapseLanguageService.getConnectorHolder();
    }
}
