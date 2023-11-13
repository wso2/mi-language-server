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

package org.eclipse.lemminx.customservice.syntaxmodel.pojo.endpoint.http;

import org.eclipse.lemminx.customservice.syntaxmodel.pojo.STNode;

public class EndpointHttpAuthenticationOauthClientCredentials extends STNode {

    STNode clientId;
    STNode clientSecret;
    STNode refreshToken;
    STNode tokenUrl;
    EndpointHttpAuthenticationOauthRequestParameters requestParameters;
    STNode authMode;

    public STNode getClientId() {

        return clientId;
    }

    public void setClientId(STNode clientId) {

        this.clientId = clientId;
    }

    public STNode getClientSecret() {

        return clientSecret;
    }

    public void setClientSecret(STNode clientSecret) {

        this.clientSecret = clientSecret;
    }

    public STNode getRefreshToken() {

        return refreshToken;
    }

    public void setRefreshToken(STNode refreshToken) {

        this.refreshToken = refreshToken;
    }

    public STNode getTokenUrl() {

        return tokenUrl;
    }

    public void setTokenUrl(STNode tokenUrl) {

        this.tokenUrl = tokenUrl;
    }

    public EndpointHttpAuthenticationOauthRequestParameters getRequestParameters() {

        return requestParameters;
    }

    public void setRequestParameters(EndpointHttpAuthenticationOauthRequestParameters requestParameters) {

        this.requestParameters = requestParameters;
    }

    public STNode getAuthMode() {

        return authMode;
    }

    public void setAuthMode(STNode authMode) {

        this.authMode = authMode;
    }
}
