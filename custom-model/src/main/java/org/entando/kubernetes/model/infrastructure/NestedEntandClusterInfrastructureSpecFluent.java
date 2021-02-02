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

package org.entando.kubernetes.model.infrastructure;

import org.entando.kubernetes.model.NestedIngressingDeploymentSpecFluent;

public class NestedEntandClusterInfrastructureSpecFluent<F extends EntandoClusterInfrastructureFluent<F>>
        extends EntandoClusterInfrastructureSpecFluent<NestedEntandClusterInfrastructureSpecFluent<F>>
        implements NestedIngressingDeploymentSpecFluent<F, NestedEntandClusterInfrastructureSpecFluent<F>> {

    private final F parentBuilder;

    NestedEntandClusterInfrastructureSpecFluent(F parentBuilder, EntandoClusterInfrastructureSpec item) {
        super(item);
        this.parentBuilder = parentBuilder;
    }

    public NestedEntandClusterInfrastructureSpecFluent(F parentBuilder) {
        super();
        this.parentBuilder = parentBuilder;
    }

    @Override
    public F and() {
        return parentBuilder.withSpec(this.build());
    }

    public F endSpec() {
        return this.and();
    }
}
