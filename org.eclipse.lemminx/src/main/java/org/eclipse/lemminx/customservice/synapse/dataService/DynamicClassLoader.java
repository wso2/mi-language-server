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

package org.eclipse.lemminx.customservice.synapse.dataService;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public class DynamicClassLoader {

    private static final Logger LOGGER = Logger.getLogger(DynamicClassLoader.class.getName());

    private static URLClassLoader classLoader;
    private static final Object lock = new Object();
    private static Set<URL> currentUrls = new HashSet<>();

    /**
     * Add DB drivers in a folder to the class loader
     *
     * @param jarDirectory path of the DB driver jars folder
     */
    public static void updateClassLoader(File jarDirectory) throws Exception {
        synchronized (lock) {
            File[] jarFiles = jarDirectory.listFiles((dir1, name) -> name.endsWith(".jar"));

            if (jarFiles == null || jarFiles.length == 0) {
                return;
            }

            Set<URL> newUrls = new HashSet<>();
            for (File jarFile : jarFiles) {
                URL jarUrl = jarFile.toURI().toURL();
                newUrls.add(jarUrl);
            }

            Set<URL> urlsToAdd = new HashSet<>(newUrls);
            urlsToAdd.removeAll(currentUrls);

            if (!urlsToAdd.isEmpty()) {
                URL[] updatedUrls = new URL[newUrls.size()];
                newUrls.toArray(updatedUrls);
                URLClassLoader newClassLoader = new URLClassLoader(updatedUrls,
                        Thread.currentThread().getContextClassLoader());
                classLoader = newClassLoader;
                currentUrls = new HashSet<>(newUrls);
            }
        }
    }

    /**
     * Add a DB driver to the class loader
     *
     * @param jarFile path of the DB driver jar
     */
    public static void addJarToClassLoader(File jarFile) throws Exception {
        synchronized (lock) {
            URL jarUrl = jarFile.toURI().toURL();
            if (!currentUrls.contains(jarUrl)) {
                Set<URL> updatedUrls = new HashSet<>(currentUrls);
                updatedUrls.add(jarUrl);

                URLClassLoader newClassLoader = new URLClassLoader(updatedUrls.toArray(new URL[0]),
                        Thread.currentThread().getContextClassLoader());
                classLoader = newClassLoader;
                currentUrls = updatedUrls;
            }
        }
    }

    public static URLClassLoader getClassLoader() {
        return classLoader;
    }
}
