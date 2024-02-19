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

package org.eclipse.lemminx.customservice.synapse.directoryTree.component;

import java.util.ArrayList;
import java.util.List;

public class AdvancedComponent extends SimpleComponent {

    List<SimpleComponent> sequences;
    List<SimpleComponent> endpoints;

    public AdvancedComponent(String type, String name, String path) {

        super(type, name, path);
        this.sequences = new ArrayList<>();
        this.endpoints = new ArrayList<>();
    }

    public AdvancedComponent(SimpleComponent component) {

        super(component.getType(), component.getName(), component.getPath());
        this.sequences = new ArrayList<>();
        this.endpoints = new ArrayList<>();
    }

    public void addEndpoint(SimpleComponent endpoint) {

        if (!alreadyExists(endpoints, endpoint)) {
            endpoints.add(endpoint);
        }
    }

    public void addSequence(SimpleComponent sequence) {

        if (!alreadyExists(sequences, sequence)) {
            sequences.add(sequence);
        }
    }

    private Boolean alreadyExists(List<SimpleComponent> components, SimpleComponent component) {

        for (SimpleComponent c : components) {
            if (c.getName().equals(component.getName())) {
                return true;
            }
        }
        return false;
    }
}
