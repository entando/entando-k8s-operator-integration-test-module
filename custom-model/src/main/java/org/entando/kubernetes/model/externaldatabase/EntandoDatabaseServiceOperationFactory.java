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

package org.entando.kubernetes.model.externaldatabase;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.internal.CustomResourceOperationsImpl;
import io.fabric8.kubernetes.internal.KubernetesDeserializer;
import org.entando.kubernetes.model.EntandoCustomResourceResolver;

public final class EntandoDatabaseServiceOperationFactory {

    private static EntandoCustomResourceResolver
            <EntandoDatabaseService, EntandoDatabaseServiceList, DoneableEntandoDatabaseService> resolver =
            new EntandoCustomResourceResolver<>(
                    EntandoDatabaseService.class, EntandoDatabaseServiceList.class, DoneableEntandoDatabaseService.class);

    private EntandoDatabaseServiceOperationFactory() {
    }

    public static CustomResourceOperationsImpl<EntandoDatabaseService, EntandoDatabaseServiceList,
            DoneableEntandoDatabaseService> produceAllEntandoDatabaseServices(KubernetesClient client) {
        KubernetesDeserializer.registerCustomKind("entando.org/v1#EntandoDatabaseService", EntandoDatabaseService.class);
        return resolver.resolveOperation(client);
    }
}
