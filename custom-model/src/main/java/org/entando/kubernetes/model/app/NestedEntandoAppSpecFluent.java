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

package org.entando.kubernetes.model.app;

import org.entando.kubernetes.model.NestedIngressingDeploymentSpecFluent;

public class NestedEntandoAppSpecFluent<N extends EntandoAppFluent<N>>
        extends EntandoAppSpecFluent<NestedEntandoAppSpecFluent<N>>
        implements NestedIngressingDeploymentSpecFluent<N, NestedEntandoAppSpecFluent<N>> {

    private final N parentBuilder;

    NestedEntandoAppSpecFluent(N parentBuilder, EntandoAppSpec item) {
        super(item);
        this.parentBuilder = parentBuilder;
    }

    public NestedEntandoAppSpecFluent(N parentBuilder) {
        super();
        this.parentBuilder = parentBuilder;
    }

    @Override
    public N and() {
        return (N) parentBuilder.withSpec(this.build());
    }

    public N endSpec() {
        return this.and();
    }
}
