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

package org.entando.kubernetes.model.plugin;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.internal.CustomResourceOperationsImpl;
import org.entando.kubernetes.model.EntandoCustomResourceResolver;

public final class EntandoPluginOperationFactory {

    private static EntandoCustomResourceResolver<EntandoPlugin, EntandoPluginList, DoneableEntandoPlugin> resolver =
            new EntandoCustomResourceResolver<>(EntandoPlugin.class, EntandoPluginList.class, DoneableEntandoPlugin.class);

    private EntandoPluginOperationFactory() {
    }

    public static CustomResourceOperationsImpl<EntandoPlugin, EntandoPluginList, DoneableEntandoPlugin> produceAllEntandoPlugins(
            KubernetesClient client) {
        return resolver.resolveOperation(client);
    }
}
