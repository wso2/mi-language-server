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

import org.eclipse.lemminx.customservice.synapse.directoryTree.node.FileNode;
import org.eclipse.lemminx.customservice.synapse.directoryTree.node.FolderNode;

import java.util.ArrayList;
import java.util.List;

//Directory tree class for docker or kubernetes projects
public class DistributionDirectoryTree implements Tree {

    private String projectPath;
    private String projectType;
    private List<FileNode> files;
    private List<FolderNode> folders;

    public DistributionDirectoryTree(String projectPath, String projectType) {

        this.files = new ArrayList<>();
        this.folders = new ArrayList<>();

        this.projectPath = projectPath;
        this.projectType = projectType;
    }

    public void setFiles(List<FileNode> files) {

        this.files = files;
    }

    public void setFolders(List<FolderNode> folders) {

        this.folders = folders;
    }

    public void setProject(FolderNode project) {

    }
}
