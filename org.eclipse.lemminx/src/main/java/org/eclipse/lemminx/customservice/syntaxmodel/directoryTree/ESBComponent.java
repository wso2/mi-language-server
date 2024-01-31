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

package org.eclipse.lemminx.customservice.syntaxmodel.directoryTree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ESBComponent extends SimpleComponent {

    HashMap<String, List<SimpleComponent>> esbConfigs;

    public ESBComponent(String type, String name, String path) {

        super(type, name, path);
        this.esbConfigs = new HashMap<>();
        List<String> esbKeys = new ArrayList<>(Arrays.asList(
                "api", "endpoints", "inbound-endpoints", "message-processors",
                "local-entries", "message-stores", "proxy-services", "sequences",
                "tasks", "templates"
        ));
        for (String key : esbKeys) {
            esbConfigs.put(key, new ArrayList<>());
        }
    }

    public void addEsbConfig(String type, SimpleComponent component) {

        esbConfigs.get(type).add(component);
    }
}
