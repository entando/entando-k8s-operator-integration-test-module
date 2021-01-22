/*
 *
 * Copyright 2015-Present Entando Inc. (http://www.entando.com) All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 *  This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 */

package org.entando.kubernetes.controller.coordinator;

import io.fabric8.kubernetes.client.CustomResourceList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.Watcher.Action;
import io.fabric8.kubernetes.client.dsl.internal.CustomResourceOperationsImpl;
import io.quarkus.runtime.StartupEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import org.entando.kubernetes.controller.EntandoOperatorConfig;
import org.entando.kubernetes.controller.KubeUtils;
import org.entando.kubernetes.controller.common.ControllerExecutor;
import org.entando.kubernetes.model.DoneableEntandoCustomResource;
import org.entando.kubernetes.model.EntandoBaseCustomResource;
import org.entando.kubernetes.model.EntandoResourceOperationsRegistry;
import org.entando.kubernetes.model.compositeapp.EntandoCompositeApp;
import org.entando.kubernetes.model.infrastructure.EntandoClusterInfrastructure;
import org.entando.kubernetes.model.keycloakserver.EntandoKeycloakServer;
import org.entando.kubernetes.model.plugin.EntandoPlugin;

public class EntandoControllerCoordinator {

    private final KubernetesClient client;
    private final Map<Class<? extends EntandoBaseCustomResource<?>>, List<?>> observers =
            new ConcurrentHashMap<>();
    private final EntandoResourceOperationsRegistry entandoResourceOperationsRegistry;

    @Inject
    public EntandoControllerCoordinator(KubernetesClient client) {
        this.entandoResourceOperationsRegistry = new EntandoResourceOperationsRegistry(client);
        this.client = client;
    }

    public void onStartup(@Observes StartupEvent event) {
        //TODO extract TLS and CA certs and write them to the standard secret names

        addObservers(EntandoKeycloakServer.class, this::startImage);
        addObservers(EntandoClusterInfrastructure.class, this::startImage);
        //        addObservers(EntandoApp.class, this::startImage);
        addObservers(EntandoPlugin.class, this::startImage);
        //        addObservers(EntandoAppPluginLink.class, this::startImage);
        addObservers(EntandoCompositeApp.class, this::startImage);
        //        addObservers(EntandoDatabaseService.class, this::startImage);
        KubeUtils.ready(EntandoControllerCoordinator.class.getSimpleName());
    }

    @SuppressWarnings("unchecked")
    public <S extends Serializable,
            R extends EntandoBaseCustomResource<S>,
            L extends CustomResourceList<R>,
            D extends DoneableEntandoCustomResource<R, D>> List<EntandoResourceObserver<S, R, L, D>> getObserver(Class<R> clss) {
        return (List<EntandoResourceObserver<S, R, L, D>>) observers.get(clss);
    }

    @SuppressWarnings("unchecked")
    private <S extends Serializable,
            R extends EntandoBaseCustomResource<S>,
            L extends CustomResourceList<R>,
            D extends DoneableEntandoCustomResource<R, D>> void addObservers(Class<R> type, BiConsumer<Action, R> consumer) {
        CustomResourceOperationsImpl<R, L, D> operations = (CustomResourceOperationsImpl<R, L, D>) this.entandoResourceOperationsRegistry
                .getOperations(type);
        List<EntandoResourceObserver<S, R, L, D>> observersForType = new ArrayList<>();
        if (EntandoOperatorConfig.isClusterScopedDeployment()) {
            //This code is essentially impossible to test in a shared cluster
            observersForType.add(new EntandoResourceObserver<>(
                    (CustomResourceOperationsImpl<R, L, D>) operations.inAnyNamespace(), consumer));
        } else {
            List<String> namespaces = EntandoOperatorConfig.getNamespacesToObserve();
            if (namespaces.isEmpty()) {
                namespaces.add(client.getNamespace());
            }
            for (String namespace : namespaces) {
                CustomResourceOperationsImpl<R, L, D> namespacedOperations = (CustomResourceOperationsImpl<R, L, D>) operations
                        .inNamespace(namespace);
                observersForType.add(new EntandoResourceObserver<>(namespacedOperations, consumer));
            }
        }
        observers.put(type, observersForType);
    }

    private <S extends Serializable, T extends EntandoBaseCustomResource<S>> void startImage(Action action, T resource) {
        ControllerExecutor executor = new ControllerExecutor(client.getNamespace(), client);
        executor.startControllerFor(action, resource, null);
    }

}
