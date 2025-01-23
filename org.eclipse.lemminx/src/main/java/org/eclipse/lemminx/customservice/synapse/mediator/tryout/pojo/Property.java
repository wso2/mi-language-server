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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Property {

    private String key;
    private String value;
    private String description;
    private List<Property> properties;

    public Property(String key, String value) {

        this.key = key;
        this.value = value;
    }

    public Property(String key, String value, String description) {

        this.key = key;
        this.value = value;
        this.description = description;
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

    public void addProperty(Property property) {

        if (properties == null) {
            properties = new ArrayList<>();
        }
        properties.add(property);
    }

    public List<Property> getProperties() {

        if (properties == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(properties);
    }

    public String getDescription() {

        return description;
    }

    public void setDescription(String description) {

        this.description = description;
    }

    public Property deepCopy() {

        Property property = new Property(key, value, description);
        if (properties != null) {
            property.setProperties(new ArrayList<>(properties));
        }
        return property;
    }

    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof Property)) {
            return false;
        }
        Property property = (Property) obj;
        return key.equals(property.getKey()) && (value != null && value.equals(property.getValue()));
    }

    @Override
    public int hashCode() {

        int result = key != null ? key.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    public boolean deleteProperty(String key) {
        if (properties == null || properties.isEmpty()) {
            return false;
        }
        return properties.removeIf(property -> property.getKey().equals(key));
    }

    @Override
    public String toString() {

        return "Property{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                ", description='" + description + '\'' +
                ", properties=" + properties +
                '}';
    }
}
