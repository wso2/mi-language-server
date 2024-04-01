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

// Source: https://github.com/wso2/carbon-mediation/blob/master/components/mediation-commons/src/main/java/org/wso2/carbon/mediation/commons/rest/api/swagger/APIGenException.java

/**
 * Custom exception for API generation
 */
public class APIGenException extends Exception {

    public APIGenException() {

    }

    public APIGenException(String message) {

        super(message);
    }

    public APIGenException(String message, Throwable cause) {

        super(message, cause);
    }

    public APIGenException(Throwable cause) {

        super(cause);
    }
}
