/*
 *   Copyright (c) 2024, WSO2 LLC. (http://www.wso2.com).
 *
 *   WSO2 LLC. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 */

package org.eclipse.lemminx.customservice.synapse.mediator.tryout;

import org.eclipse.lemminx.customservice.synapse.connectors.ConnectionTester;
import org.eclipse.lemminx.customservice.synapse.connectors.ConnectorHolder;
import org.eclipse.lemminx.customservice.synapse.connectors.entity.TestConnectionRequest;
import org.eclipse.lemminx.customservice.synapse.connectors.entity.TestConnectionResponse;
import org.eclipse.lemminx.customservice.synapse.mediator.schema.generate.ServerLessTryoutHandler;
import org.eclipse.lemminx.customservice.synapse.mediator.tryout.pojo.MediatorTryoutInfo;
import org.eclipse.lemminx.customservice.synapse.mediator.tryout.pojo.MediatorTryoutRequest;

public class TryOutManager {

    private TryOutHandler tryOutHandler;
    private IsolatedTryOutHandler isolatedTryOutHandler;
    private ServerLessTryoutHandler serverLessTryoutHandler;
    private ConnectionTester connectionTester;

    public TryOutManager(String projectRoot, String miServerPath, ConnectorHolder connectorHolder) {

        tryOutHandler = new TryOutHandler(projectRoot, miServerPath, connectorHolder);
        isolatedTryOutHandler = new IsolatedTryOutHandler(tryOutHandler, projectRoot, connectorHolder);
        serverLessTryoutHandler = new ServerLessTryoutHandler(projectRoot);
        connectionTester = new ConnectionTester(projectRoot, tryOutHandler, connectorHolder);
    }

    public final MediatorTryoutInfo tryout(MediatorTryoutRequest request) {

        if (request.isIsolatedTryout()) {
            return isolatedTryOutHandler.tryOut(request);
        } else {
            return tryOutHandler.handle(request);
        }
    }

    public final MediatorTryoutInfo getInputOutputSchema(MediatorTryoutRequest request) {

        return serverLessTryoutHandler.handle(request);
    }

    public final TestConnectionResponse testConnectorConnection(TestConnectionRequest request) {

        return connectionTester.testConnection(request);
    }

    public boolean shutdown() {

        tryOutHandler.reset();
        return tryOutHandler.shutDown();
    }
}
