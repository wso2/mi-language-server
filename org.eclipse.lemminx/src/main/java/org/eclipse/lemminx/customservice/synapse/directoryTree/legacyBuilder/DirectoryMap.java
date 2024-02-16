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

package org.eclipse.lemminx.customservice.synapse.directoryTree.legacyBuilder;

import org.eclipse.lemminx.customservice.synapse.directoryTree.node.ESBNode;
import org.eclipse.lemminx.customservice.synapse.directoryTree.node.Node;

import java.util.ArrayList;
import java.util.List;

public class DirectoryMap {

    List<ESBNode> esbConfigs;
    List<Node> dataServiceConfigs;
    List<Node> dataSourceConfigs;
    List<Node> mediatorProjects;
    List<Node> registryResources;
    List<Node> javaLibraryProjects;
    List<Node> compositeExporters;
    List<Node> connectorExporters;
    List<Node> dockerExporters;
    List<Node> kubernetesExporters;

    public DirectoryMap() {

        this.esbConfigs = new ArrayList<>();
        this.dataServiceConfigs = new ArrayList<>();
        this.dataSourceConfigs = new ArrayList<>();
        this.mediatorProjects = new ArrayList<>();
        this.registryResources = new ArrayList<>();
        this.javaLibraryProjects = new ArrayList<>();
        this.compositeExporters = new ArrayList<>();
        this.connectorExporters = new ArrayList<>();
        this.dockerExporters = new ArrayList<>();
        this.kubernetesExporters = new ArrayList<>();
    }

    public void addEsbComponent(ESBNode esbNode) {

        esbConfigs.add(esbNode);
    }

    public void addDataServiceConfig(String type, String name, String path) {

        Node component = new Node(type, name, path);
        dataServiceConfigs.add(component);
    }

    public void addDataSourceConfig(String type, String name, String path) {

        Node component = new Node(type, name, path);
        dataSourceConfigs.add(component);
    }

    public void addMediatorProject(String type, String name, String path) {

        Node component = new Node(type, name, path);
        mediatorProjects.add(component);
    }

    public void addRegistryResource(String type, String name, String path) {

        Node component = new Node(type, name, path);
        registryResources.add(component);
    }

    public void addJavaLibraryProject(String type, String name, String path) {

        Node component = new Node(type, name, path);
        javaLibraryProjects.add(component);
    }

    public void addCompositeExporter(String type, String name, String path) {

        Node component = new Node(type, name, path);
        compositeExporters.add(component);
    }

    public void addConnectorExporter(String type, String name, String path) {

        Node component = new Node(type, name, path);
        connectorExporters.add(component);
    }

    public void addDockerExporter(String type, String name, String path) {

        Node component = new Node(type, name, path);
        dockerExporters.add(component);
    }

    public void addKubernetesExporter(String type, String name, String path) {

        Node component = new Node(type, name, path);
        kubernetesExporters.add(component);
    }

    public List<ESBNode> getEsbConfigs() {

        return esbConfigs;
    }

    public List<Node> getDataServiceConfigs() {

        return dataServiceConfigs;
    }

    public List<Node> getDataSourceConfigs() {

        return dataSourceConfigs;
    }

    public List<Node> getMediatorProjects() {

        return mediatorProjects;
    }

    public List<Node> getRegistryResources() {

        return registryResources;
    }

    public List<Node> getJavaLibraryProjects() {

        return javaLibraryProjects;
    }

    public List<Node> getCompositeExporters() {

        return compositeExporters;
    }

    public List<Node> getConnectorExporters() {

        return connectorExporters;
    }

    public List<Node> getDockerExporters() {

        return dockerExporters;
    }

    public List<Node> getKubernetesExporters() {

        return kubernetesExporters;
    }
}
