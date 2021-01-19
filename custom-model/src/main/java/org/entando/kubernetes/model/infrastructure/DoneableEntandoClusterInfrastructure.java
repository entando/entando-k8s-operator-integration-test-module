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

import io.fabric8.kubernetes.api.builder.Function;
import java.util.Optional;
import org.entando.kubernetes.model.AbstractServerStatus;
import org.entando.kubernetes.model.DoneableEntandoCustomResource;
import org.entando.kubernetes.model.EntandoCustomResourceStatus;
import org.entando.kubernetes.model.EntandoDeploymentPhase;

public class DoneableEntandoClusterInfrastructure extends
        EntandoClusterInfrastructureFluent<DoneableEntandoClusterInfrastructure> implements
        DoneableEntandoCustomResource<DoneableEntandoClusterInfrastructure, EntandoClusterInfrastructure> {

    private final Function<EntandoClusterInfrastructure, EntandoClusterInfrastructure> function;
    private final EntandoCustomResourceStatus status;

    public DoneableEntandoClusterInfrastructure(Function<EntandoClusterInfrastructure, EntandoClusterInfrastructure> function) {
        this.status = new EntandoCustomResourceStatus();
        this.function = function;
    }

    public DoneableEntandoClusterInfrastructure(EntandoClusterInfrastructure resource,
            Function<EntandoClusterInfrastructure, EntandoClusterInfrastructure> function) {
        super(resource.getSpec(), resource.getMetadata());
        this.status = Optional.ofNullable(resource.getStatus()).orElse(new EntandoCustomResourceStatus());
        this.function = function;
    }

    @Override
    public DoneableEntandoClusterInfrastructure withStatus(AbstractServerStatus status) {
        this.status.putServerStatus(status);
        return this;
    }

    @Override
    public DoneableEntandoClusterInfrastructure withPhase(EntandoDeploymentPhase phase) {
        status.updateDeploymentPhase(phase, metadata.getGeneration());
        return this;
    }

    @Override
    public EntandoClusterInfrastructure done() {
        EntandoClusterInfrastructure entandoClusterInfrastructure = new EntandoClusterInfrastructure(metadata.build(), spec.build(),
                status);
        return function.apply(entandoClusterInfrastructure);
    }
}
