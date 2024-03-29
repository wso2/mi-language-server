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

package org.eclipse.lemminx.customservice.synapse.api.generator;

// Source: https://github.com/wso2/carbon-mediation/blob/master/components/mediation-admin/org.wso2.carbon.rest.api/src/main/java/org/wso2/carbon/rest/api/APIException.java

/**
 * Class <code>APIException</code> creates a custom exception for
 * Rest API admin
 */
public class APIException extends Exception {

    private static final long serialVersionUID = -6272463911272868928L;

    public APIException() {

    }

    public APIException(String message) {

        super(message);
    }

    public APIException(Throwable cause) {

        super(cause);
    }

    public APIException(String message, Throwable cause) {

        super(message, cause);
    }

}
