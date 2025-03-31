/*
 * Copyright (c) 2025, WSO2 LLC. (http://www.wso2.com).
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

package org.eclipse.lemminx.customservice.synapse.dataService;

public class CheckDBDriverResponseParams {

    private boolean isDriverAvailable;
    private String driverVersion;
    private String driverPath;

    public CheckDBDriverResponseParams(boolean isDriverAvailable, String driverVersion, String driverPath) {
        this.isDriverAvailable = isDriverAvailable;
        this.driverVersion = driverVersion;
        this.driverPath = driverPath;
    }

    public boolean isDriverAvailable() {
        return isDriverAvailable;
    }

    public String getDriverVersion() {
        return driverVersion;
    }
}
