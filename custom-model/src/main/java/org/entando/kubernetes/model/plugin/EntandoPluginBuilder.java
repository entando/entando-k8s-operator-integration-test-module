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

import io.fabric8.kubernetes.api.builder.Builder;

public class EntandoPluginBuilder extends EntandoPluginFluent<EntandoPluginBuilder> implements Builder<EntandoPlugin> {

    public EntandoPluginBuilder() {
    }

    public EntandoPluginBuilder(EntandoPlugin plugin) {
        super(plugin.getSpec(), plugin.getMetadata());
    }

    @Override
    public EntandoPlugin build() {
        return new EntandoPlugin(super.metadata.build(), super.spec.build());
    }
}
