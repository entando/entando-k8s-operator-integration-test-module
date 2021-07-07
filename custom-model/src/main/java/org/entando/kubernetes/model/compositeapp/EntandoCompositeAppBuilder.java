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

package org.entando.kubernetes.model.compositeapp;

import io.fabric8.kubernetes.api.builder.Builder;

public class EntandoCompositeAppBuilder extends EntandoCompositeAppFluent<EntandoCompositeAppBuilder> implements
        Builder<EntandoCompositeApp> {

    public EntandoCompositeAppBuilder(EntandoCompositeApp entandoCompositeApp) {
        super(entandoCompositeApp.getSpec(), entandoCompositeApp.getMetadata());
    }

    public EntandoCompositeAppBuilder() {

    }

    @Override
    public EntandoCompositeApp build() {
        return new EntandoCompositeApp(super.metadata.build(), super.spec.build());
    }
}
