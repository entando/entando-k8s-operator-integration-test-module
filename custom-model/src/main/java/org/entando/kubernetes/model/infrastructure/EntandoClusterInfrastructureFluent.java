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

import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import org.entando.kubernetes.model.EntandoFluent;
import org.entando.kubernetes.model.EntandoIngressingDeploymentBaseFluent;

public class EntandoClusterInfrastructureFluent<F extends EntandoClusterInfrastructureFluent<F>>
        extends EntandoFluent<F>
        implements EntandoIngressingDeploymentBaseFluent<F, NestedEntandClusterInfrastructureSpecFluent<F>> {

    protected EntandoClusterInfrastructureSpecBuilder spec;

    protected EntandoClusterInfrastructureFluent() {
        this(new ObjectMetaBuilder(), new EntandoClusterInfrastructureSpecBuilder());
    }

    protected EntandoClusterInfrastructureFluent(EntandoClusterInfrastructureSpec spec, ObjectMeta objectMeta) {
        this(new ObjectMetaBuilder(objectMeta), new EntandoClusterInfrastructureSpecBuilder(spec));
    }

    private EntandoClusterInfrastructureFluent(ObjectMetaBuilder metadata, EntandoClusterInfrastructureSpecBuilder spec) {
        super(metadata);
        this.spec = spec;
    }

    @Override
    public NestedEntandClusterInfrastructureSpecFluent<F> editSpec() {
        return new NestedEntandClusterInfrastructureSpecFluent<>(thisAsF(), this.spec.build());
    }

    public NestedEntandClusterInfrastructureSpecFluent<F> withNewSpec() {
        return new NestedEntandClusterInfrastructureSpecFluent<>(thisAsF());
    }

    public F withSpec(EntandoClusterInfrastructureSpec spec) {
        this.spec = new EntandoClusterInfrastructureSpecBuilder(spec);
        return thisAsF();
    }

}
