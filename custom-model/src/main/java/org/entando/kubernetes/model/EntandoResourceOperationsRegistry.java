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

package org.entando.kubernetes.model;

import io.fabric8.kubernetes.client.CustomResourceList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.internal.CustomResourceOperationsImpl;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.entando.kubernetes.model.app.EntandoApp;
import org.entando.kubernetes.model.app.EntandoAppOperationFactory;
import org.entando.kubernetes.model.app.EntandoBaseCustomResource;
import org.entando.kubernetes.model.externaldatabase.EntandoDatabaseService;
import org.entando.kubernetes.model.externaldatabase.EntandoDatabaseServiceOperationFactory;
import org.entando.kubernetes.model.infrastructure.EntandoClusterInfrastructure;
import org.entando.kubernetes.model.infrastructure.EntandoClusterInfrastructureOperationFactory;
import org.entando.kubernetes.model.keycloakserver.EntandoKeycloakServer;
import org.entando.kubernetes.model.keycloakserver.EntandoKeycloakServerOperationFactory;
import org.entando.kubernetes.model.link.EntandoAppPluginLink;
import org.entando.kubernetes.model.link.EntandoAppPluginLinkOperationFactory;
import org.entando.kubernetes.model.plugin.EntandoPlugin;
import org.entando.kubernetes.model.plugin.EntandoPluginOperationFactory;

public class EntandoResourceOperationsRegistry {

    private static final Map<Class<? extends EntandoBaseCustomResource>, OperationsSupplier> OPERATION_SUPPLIERS = getOperationSuppliers();
    private final KubernetesClient client;
    @SuppressWarnings("unchecked")
    private final Map<Class, CustomResourceOperationsImpl> customResourceOperations =
            new ConcurrentHashMap<>();

    public EntandoResourceOperationsRegistry(KubernetesClient client) {
        this.client = client;
    }

    private static Map<Class<? extends EntandoBaseCustomResource>, OperationsSupplier> getOperationSuppliers() {
        Map<Class<? extends EntandoBaseCustomResource>, OperationsSupplier> operationSuppliers = new ConcurrentHashMap<>();
        operationSuppliers.put(EntandoKeycloakServer.class, EntandoKeycloakServerOperationFactory::produceAllEntandoKeycloakServers);
        operationSuppliers.put(EntandoClusterInfrastructure.class,
                EntandoClusterInfrastructureOperationFactory::produceAllEntandoClusterInfrastructures);
        operationSuppliers.put(EntandoApp.class, EntandoAppOperationFactory::produceAllEntandoApps);
        operationSuppliers.put(EntandoPlugin.class, EntandoPluginOperationFactory::produceAllEntandoPlugins);
        operationSuppliers.put(EntandoAppPluginLink.class, EntandoAppPluginLinkOperationFactory::produceAllEntandoAppPluginLinks);
        operationSuppliers.put(EntandoDatabaseService.class, EntandoDatabaseServiceOperationFactory::produceAllEntandoDatabaseServices);
        return Collections.unmodifiableMap(operationSuppliers);
    }

    @SuppressWarnings("unchecked")
    public <T extends EntandoCustomResource, D extends DoneableEntandoCustomResource<D, T>> CustomResourceOperationsImpl<T,
            CustomResourceList<T>, D> getOperations(Class<T> c) {
        return this.customResourceOperations.computeIfAbsent(c, aClass -> OPERATION_SUPPLIERS.get(aClass).get(client));
    }

    private interface OperationsSupplier {

        CustomResourceOperationsImpl get(KubernetesClient client);
    }

}
