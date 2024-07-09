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

package org.eclipse.lemminx.customservice.synapse.dependency.tree;

import org.eclipse.lemminx.customservice.synapse.debugger.visitor.AbstractMediatorVisitor;
import org.eclipse.lemminx.customservice.synapse.dependency.tree.pojo.Dependency;
import org.eclipse.lemminx.customservice.synapse.dependency.tree.visitor.EndpointVisitor;
import org.eclipse.lemminx.customservice.synapse.dependency.tree.visitor.MediatorDependencyVisitor;
import org.eclipse.lemminx.customservice.synapse.dependency.tree.visitor.MessageStoreVisitor;
import org.eclipse.lemminx.customservice.synapse.dependency.tree.visitor.SequenceVisitor;
import org.eclipse.lemminx.customservice.synapse.dependency.tree.visitor.TemplateVisitor;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.endpoint.NamedEndpoint;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.Mediator;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.misc.common.Sequence;
import org.eclipse.lemminx.customservice.synapse.utils.ConfigFinder;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DependencyVisitorUtils {

    private static final Logger LOGGER = Logger.getLogger(DependencyVisitorUtils.class.getName());

    /**
     * Visit the sequence and return the dependencies.
     *
     * @param projectPath      The project path.
     * @param sequenceName     The sequence name to visit.
     * @param dependencyLookUp
     * @return The list of dependencies.
     */
    public static Dependency visitSequence(String projectPath, String sequenceName, DependencyLookUp dependencyLookUp) {

        String inSequencePath = DependencyVisitorUtils.getDependencyPath(sequenceName, "sequences", projectPath);
        if (inSequencePath != null) {
            Dependency dependency = dependencyLookUp.getDependency(sequenceName);
            if (dependency != null) {
                return dependency;
            }
            SequenceVisitor sequenceVisitor = new SequenceVisitor(projectPath, dependencyLookUp);
            sequenceVisitor.visit(inSequencePath);
            dependency = new Dependency(sequenceName, ArtifactType.SEQUENCE, inSequencePath,
                    sequenceVisitor.getDependencyTree().getDependencyList());
            return dependency;
        }
        return null;
    }

    /**
     * Visit the anonymous sequence and return the dependencies.
     *
     * @param sequence         The anonymous sequence to visit.
     * @param projectPath
     * @param dependencyLookUp
     * @return The list of dependencies.
     */
    public static List<Dependency> visitAnonymousSequence(Sequence sequence, String projectPath,
                                                          DependencyLookUp dependencyLookUp) {

        if (sequence != null) {
            return visitMediators(sequence.getMediatorList(), projectPath, dependencyLookUp);
        }
        return Collections.emptyList();
    }

    /**
     * Visit the mediators in the list and return the dependencies.
     *
     * @param mediators        The list of mediators to visit.
     * @param dependencyLookUp
     * @return The list of dependencies.
     */
    public static List<Dependency> visitMediators(List<Mediator> mediators, String projectPath,
                                                  DependencyLookUp dependencyLookUp) {

        MediatorDependencyVisitor visitor = new MediatorDependencyVisitor(projectPath, dependencyLookUp);
        for (Mediator mediator : mediators) {
            visitMediator(mediator, visitor);
        }
        return visitor.getDependencies();
    }

    /**
     * Visit the mediator node.
     *
     * @param node    The mediator node to visit.
     * @param visitor
     */
    public static void visitMediator(Mediator node, MediatorDependencyVisitor visitor) {

        String tag = node.getTag();
        tag = sanitizeTag(tag);
        String visitFn;
        visitFn = "visit" + tag.substring(0, 1).toUpperCase() + tag.substring(1);
        try {
            Method method = AbstractMediatorVisitor.class.getDeclaredMethod(visitFn, node.getClass());
            method.setAccessible(true);
            method.invoke(visitor, node);
        } catch (NoSuchMethodException e) {
            LOGGER.log(Level.SEVERE, "No visit method found for mediator: " + tag, e);
        } catch (InvocationTargetException e) {
            LOGGER.log(Level.SEVERE, "Error while invoking visit method for mediator: " + tag, e);
        } catch (IllegalAccessException e) {
            LOGGER.log(Level.SEVERE, "Error while accessing visit method for mediator: " + tag, e);
        }
    }

    private static String sanitizeTag(String tag) {

        String sanitizedTag = tag;
        if (tag.contains("-")) {
            String[] split = tag.split("-");
            sanitizedTag = split[0] + split[1].substring(0, 1).toUpperCase() + split[1].substring(1);
        } else if (tag.contains(":")) {
            String[] split = tag.split(":");
            sanitizedTag = split[1];
        } else if (tag.contains(".")) {
            sanitizedTag = "connector";
        }
        return sanitizedTag;
    }

    /**
     * Visit the endpoint and return the dependencies.
     *
     * @param endpoint         The endpoint to visit.
     * @param projectPath      The project path.
     * @param dependencyLookUp
     * @return The list of dependencies.
     */
    public static Dependency visitEndpoint(NamedEndpoint endpoint, String projectPath,
                                           DependencyLookUp dependencyLookUp) {

        String endpointKey = endpoint.getKey();
        EndpointVisitor endpointVisitor = new EndpointVisitor(projectPath, dependencyLookUp);
        if (endpointKey != null) {
            String endpointPath = DependencyVisitorUtils.getDependencyPath(endpointKey, "endpoints", projectPath);
            if (endpointPath != null) {
                Dependency dependency = dependencyLookUp.getDependency(endpointPath);
                if (dependency != null) {
                    return dependency;
                }
                endpointVisitor.visit(endpointPath);
                dependency = new Dependency(endpointKey, ArtifactType.ENDPOINT, endpointPath,
                        endpointVisitor.getDependencyTree().getDependencyList());
                return dependency;
            }
        } else {
            endpointVisitor.visit(endpoint);
            return new Dependency(null, ArtifactType.ENDPOINT, null,
                    endpointVisitor.getDependencyTree().getDependencyList());
        }
        return null;
    }

    /**
     * Visit the endpoint and return the dependencies.
     *
     * @param endpoint         The endpoint to visit.
     * @param projectPath      The project path.
     * @param dependencyLookUp
     * @return The list of dependencies.
     */
    public static Dependency visitEndpoint(String endpoint, String projectPath, DependencyLookUp dependencyLookUp) {

        String endpointPath = DependencyVisitorUtils.getDependencyPath(endpoint, "endpoints", projectPath);
        if (endpointPath != null) {
            Dependency dependency = dependencyLookUp.getDependency(endpointPath);
            if (dependency != null) {
                return dependency;
            }
            EndpointVisitor endpointVisitor = new EndpointVisitor(projectPath, dependencyLookUp);
            endpointVisitor.visit(endpointPath);
            dependency = new Dependency(endpoint, ArtifactType.ENDPOINT, endpointPath,
                    endpointVisitor.getDependencyTree().getDependencyList());
            return dependency;
        }
        return null;
    }

    /**
     * Visit the template and return the dependencies.
     *
     * @param template         The template to visit.
     * @param projectPath      The project path.
     * @param dependencyLookUp
     * @return The list of dependencies.
     */
    public static Dependency visitTemplate(String template, String projectPath, DependencyLookUp dependencyLookUp) {

        String templatePath = DependencyVisitorUtils.getDependencyPath(template, "templates", projectPath);
        if (templatePath != null) {
            Dependency dependency = dependencyLookUp.getDependency(templatePath);
            if (dependency != null) {
                return dependency;
            }
            TemplateVisitor templateVisitor = new TemplateVisitor(projectPath, dependencyLookUp);
            templateVisitor.visit(templatePath);
            dependency = new Dependency(template, ArtifactType.TEMPLATE, templatePath,
                    templateVisitor.getDependencyTree().getDependencyList());
            return dependency;
        }
        return null;
    }

    /**
     * Get the path of the given artifact.
     *
     * @param key         The key of the artifact.
     * @param type        The type of the artifact.
     * @param projectPath The project path.
     * @return The path of the artifact.
     */
    public static String getDependencyPath(String key, String type, String projectPath) {

        try {
            return ConfigFinder.findEsbComponentPath(key, type, projectPath);
        } catch (IOException e) {
        }
        return null;
    }

    /**
     * Visit the message store and return the dependencies.
     *
     * @param messageStore     The message store to visit.
     * @param projectPath      The project path.
     * @param dependencyLookUp
     * @return The list of dependencies.
     */
    public static Dependency visitMessageStore(String messageStore, String projectPath,
                                               DependencyLookUp dependencyLookUp) {

        String path = getDependencyPath(messageStore, "message-stores", projectPath);
        if (path != null) {
            Dependency dependency = dependencyLookUp.getDependency(path);
            if (dependency != null) {
                return dependency;
            }
            MessageStoreVisitor messageStoreVisitor = new MessageStoreVisitor(projectPath, dependencyLookUp);
            messageStoreVisitor.visit(path);
            dependency = new Dependency(messageStore, ArtifactType.MESSAGE_STORE, path,
                    messageStoreVisitor.getDependencyTree().getDependencyList());
            return dependency;
        }
        return null;
    }
}
