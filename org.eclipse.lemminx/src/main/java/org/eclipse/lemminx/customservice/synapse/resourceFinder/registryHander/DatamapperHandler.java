/*
 *   Copyright (c) 2024, WSO2 LLC. (http://www.wso2.com).
 *
 *   WSO2 LLC. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 */

package org.eclipse.lemminx.customservice.synapse.resourceFinder.registryHander;

import org.eclipse.lemminx.customservice.synapse.resourceFinder.pojo.Resource;
import org.eclipse.lemminx.customservice.synapse.utils.Constant;

import java.io.File;
import java.util.List;

public class DatamapperHandler extends NonXMLRegistryHandler {

    private static final List<String> datamapperPaths = List.of(
            Constant.RESOURCE_RELATIVE_PATH.resolve(Constant.DATA_MAPPER).toString(),
            Constant.RESOURCE_RELATIVE_PATH.resolve(Constant.REGISTRY).resolve(Constant.GOV)
                    .resolve(Constant.DATA_MAPPER).toString());

    public DatamapperHandler(List<Resource> resources) {

        super(resources);
    }

    @Override
    protected boolean canHandle(File file) {

        if (file.getAbsolutePath().endsWith(".ts")) {
            return isDatamapperFile(file.getAbsolutePath());
        }
        return Boolean.FALSE;
    }

    @Override
    protected String formatKey(String key) {
        return key != null ? key.replaceAll("/[^/]*.ts$", "") : null;
    }

    private boolean isDatamapperFile(String path) {

        if (path == null) {
            return Boolean.FALSE;
        }
        for (String datamapperPath : datamapperPaths) {
            if (path.contains(datamapperPath) && !isDMUtilsFile(path)) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    private static boolean isDMUtilsFile(String path) {
        String regex = ".*/datamapper/.*?/dm-utils\\.ts$";
        return path.matches(regex);
    }
}
