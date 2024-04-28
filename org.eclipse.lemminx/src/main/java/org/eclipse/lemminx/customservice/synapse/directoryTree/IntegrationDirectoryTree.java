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

package org.eclipse.lemminx.customservice.synapse.directoryTree;

import org.eclipse.lemminx.customservice.synapse.directoryTree.node.APINode;
import org.eclipse.lemminx.customservice.synapse.directoryTree.node.FolderNode;
import org.eclipse.lemminx.customservice.synapse.directoryTree.node.Resource;
import org.eclipse.lemminx.customservice.synapse.directoryTree.node.Node;
import org.eclipse.lemminx.customservice.synapse.directoryTree.node.TestFolder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

// Directory tree class for integration projects
public class IntegrationDirectoryTree implements Tree {

    String projectPath;
    String projectType;
    List<APINode> apis;
    List<Node> endpoints;
    List<Node> inboundEndpoints;
    List<Node> localEntries;
    List<Node> messageProcessors;
    List<Node> messageStores;
    List<Node> proxyServices;
    List<Node> sequences;
    List<Node> tasks;
    List<Node> templates;
    List<Node> dataServices;
    List<Node> dataSources;
    Resource resources;
    FolderNode java;
    TestFolder tests;

    public IntegrationDirectoryTree(String projectPath, String projectType) {

        this.apis = new ArrayList<>();
        this.endpoints = new ArrayList<>();
        this.inboundEndpoints = new ArrayList<>();
        this.localEntries = new ArrayList<>();
        this.messageProcessors = new ArrayList<>();
        this.messageStores = new ArrayList<>();
        this.proxyServices = new ArrayList<>();
        this.sequences = new ArrayList<>();
        this.tasks = new ArrayList<>();
        this.templates = new ArrayList<>();
        this.dataServices = new ArrayList<>();
        this.dataSources = new ArrayList<>();
        this.resources = new Resource();

        this.projectPath = projectPath;
        this.projectType = projectType;
    }

    public void addApi(Node api) {

        apis.add((APINode) api);
    }

    public void addEndpoint(Node endpoint) {

        endpoints.add(endpoint);
    }

    public void addInboundEndpoint(Node inboundEndpoint) {

        inboundEndpoints.add(inboundEndpoint);
    }

    public void addLocalEntry(Node localEntry) {

        localEntries.add(localEntry);
    }

    public void addMessageProcessor(Node messageProcessor) {

        messageProcessors.add(messageProcessor);
    }

    public void addMessageStore(Node messageStore) {

        messageStores.add(messageStore);
    }

    public void addProxyService(Node proxyService) {

        proxyServices.add(proxyService);
    }

    public void addSequence(Node sequence) {

        sequences.add(sequence);
    }

    public void addTask(Node task) {

        tasks.add(task);
    }

    public void addTemplate(Node template) {

        templates.add(template);
    }

    public void addDataService(Node dataService) {

        dataServices.add(dataService);
    }

    public void addDataSource(Node dataSource) {

        dataSources.add(dataSource);
    }

    public void setResources(Resource resources) {

        this.resources = resources;
    }

    public Resource getResources() {

        return resources;
    }

    public void setJava(FolderNode java) {

        this.java = java;
    }

    public void setTests(TestFolder tests) {

        this.tests = tests;
    }

    public void sort() {

        apis.sort(Comparator.comparing(Node::getName));
        endpoints.sort(Comparator.comparing(Node::getName));
        inboundEndpoints.sort(Comparator.comparing(Node::getName));
        localEntries.sort(Comparator.comparing(Node::getName));
        messageProcessors.sort(Comparator.comparing(Node::getName));
        messageStores.sort(Comparator.comparing(Node::getName));
        proxyServices.sort(Comparator.comparing(Node::getName));
        sequences.sort(Comparator.comparing(Node::getName));
        tasks.sort(Comparator.comparing(Node::getName));
        templates.sort(Comparator.comparing(Node::getName));
        dataServices.sort(Comparator.comparing(Node::getName));
        dataSources.sort(Comparator.comparing(Node::getName));
    }
}
