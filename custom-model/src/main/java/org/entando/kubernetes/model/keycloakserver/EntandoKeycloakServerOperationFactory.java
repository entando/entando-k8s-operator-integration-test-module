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

package org.entando.kubernetes.model.keycloakserver;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.internal.CustomResourceOperationsImpl;
import io.fabric8.kubernetes.internal.KubernetesDeserializer;
import org.entando.kubernetes.model.EntandoCustomResourceResolver;

public final class EntandoKeycloakServerOperationFactory {

    private static EntandoCustomResourceResolver<EntandoKeycloakServer, EntandoKeycloakServerList, DoneableEntandoKeycloakServer> resolver =
            new EntandoCustomResourceResolver<>(EntandoKeycloakServer.class, EntandoKeycloakServerList.class, DoneableEntandoKeycloakServer.class);

    private EntandoKeycloakServerOperationFactory() {
    }

    public static CustomResourceOperationsImpl<EntandoKeycloakServer, EntandoKeycloakServerList, DoneableEntandoKeycloakServer> produceAllEntandoKeycloakServers(
            KubernetesClient client) {
        KubernetesDeserializer.registerCustomKind("entando.org/v1alpha1#EntandoKeycloakServer", EntandoKeycloakServer.class);
        return resolver.resolveOperation(client);
    }
}
