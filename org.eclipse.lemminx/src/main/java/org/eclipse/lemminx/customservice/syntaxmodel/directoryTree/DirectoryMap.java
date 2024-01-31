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

package org.eclipse.lemminx.customservice.syntaxmodel.directoryTree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class DirectoryMap {

    List<ESBComponent> esbConfigs;
    List<SimpleComponent> dataServiceConfigs;
    List<SimpleComponent> dataSourceConfigs;
    List<SimpleComponent> mediatorProjects;
    List<SimpleComponent> registryResources;
    List<SimpleComponent> javaLibraryProjects;
    List<SimpleComponent> compositeExporters;
    List<SimpleComponent> connectorExporters;
    List<SimpleComponent> dockerExporters;
    List<SimpleComponent> kubernetesExporters;

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

    public void addEsbComponent(ESBComponent esbComponent) {

        esbConfigs.add(esbComponent);
    }

    public void addDataServiceConfig(String type, String name, String path) {

        SimpleComponent component = new SimpleComponent(type, name, path);
        dataServiceConfigs.add(component);
    }

    public void addDataSourceConfig(String type, String name, String path) {

        SimpleComponent component = new SimpleComponent(type, name, path);
        dataSourceConfigs.add(component);
    }

    public void addMediatorProject(String type, String name, String path) {

        SimpleComponent component = new SimpleComponent(type, name, path);
        mediatorProjects.add(component);
    }

    public void addRegistryResource(String type, String name, String path) {

        SimpleComponent component = new SimpleComponent(type, name, path);
        registryResources.add(component);
    }

    public void addJavaLibraryProject(String type, String name, String path) {

        SimpleComponent component = new SimpleComponent(type, name, path);
        javaLibraryProjects.add(component);
    }

    public void addCompositeExporter(String type, String name, String path) {

        SimpleComponent component = new SimpleComponent(type, name, path);
        compositeExporters.add(component);
    }

    public void addConnectorExporter(String type, String name, String path) {

        SimpleComponent component = new SimpleComponent(type, name, path);
        connectorExporters.add(component);
    }

    public void addDockerExporter(String type, String name, String path) {

        SimpleComponent component = new SimpleComponent(type, name, path);
        dockerExporters.add(component);
    }

    public void addKubernetesExporter(String type, String name, String path) {

        SimpleComponent component = new SimpleComponent(type, name, path);
        kubernetesExporters.add(component);
    }

    public List<ESBComponent> getEsbConfigs() {

        return esbConfigs;
    }

    public List<SimpleComponent> getDataServiceConfigs() {

        return dataServiceConfigs;
    }

    public List<SimpleComponent> getDataSourceConfigs() {

        return dataSourceConfigs;
    }

    public List<SimpleComponent> getMediatorProjects() {

        return mediatorProjects;
    }

    public List<SimpleComponent> getRegistryResources() {

        return registryResources;
    }

    public List<SimpleComponent> getJavaLibraryProjects() {

        return javaLibraryProjects;
    }

    public List<SimpleComponent> getCompositeExporters() {

        return compositeExporters;
    }

    public List<SimpleComponent> getConnectorExporters() {

        return connectorExporters;
    }

    public List<SimpleComponent> getDockerExporters() {

        return dockerExporters;
    }

    public List<SimpleComponent> getKubernetesExporters() {

        return kubernetesExporters;
    }
}
