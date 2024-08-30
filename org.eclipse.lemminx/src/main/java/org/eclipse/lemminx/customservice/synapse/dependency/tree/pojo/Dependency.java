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

package org.eclipse.lemminx.customservice.synapse.dependency.tree.pojo;

import org.eclipse.lemminx.customservice.synapse.dependency.tree.ArtifactType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Dependency {

    private final String name;
    private final ArtifactType type;
    private final String path;
    private final List<Dependency> dependencyList;

    public Dependency(String name, ArtifactType type, String path) {

        this(name, type, path, Collections.emptyList());
    }

    public Dependency(String name, ArtifactType type, String path, List<Dependency> dependencyList) {

        this.name = name;
        this.type = type;
        this.path = path;
        this.dependencyList = new ArrayList<>(dependencyList);
    }

    public String getName() {

        return name;
    }

    public ArtifactType getType() {

        return type;
    }

    public String getPath() {

        return path;
    }

    public List<Dependency> getDependencyList() {

        return Collections.unmodifiableList(dependencyList);
    }

    public void addDependency(Dependency dependency) {

        this.dependencyList.add(dependency);
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Dependency that = (Dependency) obj;
        return name.equals(that.name) && type == that.type && path.equals(that.path);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name, type, path);
    }

    @Override
    public String toString() {

        return "Dependency{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", path='" + path + '\'' +
                ", dependencyList=" + dependencyList +
                '}';
    }
}
