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

package org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.test;

import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.STNode;

public class TestRegistryResource extends STNode {

    STNode fileName;
    STNode artifact;
    STNode registryPath;
    STNode mediaType;

    public STNode getFileName() {

        return fileName;
    }

    public void setFileName(STNode fileName) {

        this.fileName = fileName;
    }

    public STNode getArtifact() {

        return artifact;
    }

    public void setArtifact(STNode artifact) {

        this.artifact = artifact;
    }

    public STNode getRegistryPath() {

        return registryPath;
    }

    public void setRegistryPath(STNode registryPath) {

        this.registryPath = registryPath;
    }

    public STNode getMediaType() {

        return mediaType;
    }

    public void setMediaType(STNode mediaType) {

        this.mediaType = mediaType;
    }
}
