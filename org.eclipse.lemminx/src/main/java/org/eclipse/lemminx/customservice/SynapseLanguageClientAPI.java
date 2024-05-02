/**
 * Copyright (c) 2020 Angelo ZERR.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 * <p>
 * Contributors:
 * Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package org.eclipse.lemminx.customservice;

import org.eclipse.lemminx.customservice.synapse.ConnectorStatusNotification;
import org.eclipse.lsp4j.jsonrpc.services.JsonNotification;
import org.eclipse.lsp4j.jsonrpc.services.JsonSegment;

/**
 * Synapse language client API.
 *
 */
@JsonSegment("synapse")
public interface SynapseLanguageClientAPI extends XMLLanguageClientAPI {

    /**
     * Notification to be sent to the client when a connector is added
     *
     * @param message the connection status notification
     */
    @JsonNotification("addConnectorStatus")
    void addConnectorStatus(ConnectorStatusNotification message);

    /**
     * Notification to be sent to the client when a connector is removed
     *
     * @param message the connection status notification
     */
    @JsonNotification("removeConnectorStatus")
    void removeConnectorStatus(ConnectorStatusNotification message);

}
