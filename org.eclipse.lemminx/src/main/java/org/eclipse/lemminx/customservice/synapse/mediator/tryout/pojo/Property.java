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

package org.eclipse.lemminx.customservice.synapse.mediator.tryout.pojo;

import java.util.List;

public class Property {

    private String key;
    private String value;
    private List<Property> properties;

    public Property(String key, String value) {

        this.key = key;
        this.value = value;
    }

    public Property(String key, List<Property> properties) {

        this.key = key;
        this.properties = properties;
    }

    public String getKey() {

        return key;
    }

    public void setKey(String key) {

        this.key = key;
    }

    public String getValue() {

        return value;
    }

    public void setValue(String value) {

        this.value = value;
    }

    public void setProperties(List<Property> properties) {

        this.properties = properties;
    }

    @Override
    public String toString() {

        return "Property{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                ", properties=" + properties +
                '}';
    }
}
