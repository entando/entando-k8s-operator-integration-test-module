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

package org.entando.kubernetes.model.debundle;

import io.fabric8.kubernetes.api.builder.Function;
import java.util.Optional;
import org.entando.kubernetes.model.AbstractServerStatus;
import org.entando.kubernetes.model.DoneableEntandoCustomResource;
import org.entando.kubernetes.model.EntandoCustomResourceStatus;
import org.entando.kubernetes.model.EntandoDeploymentPhase;

public class DoneableEntandoDeBundle extends EntandoDeBundleFluent<DoneableEntandoDeBundle> implements
        DoneableEntandoCustomResource<DoneableEntandoDeBundle, EntandoDeBundle> {

    private final EntandoCustomResourceStatus status;
    private final Function<EntandoDeBundle, EntandoDeBundle> function;

    public DoneableEntandoDeBundle(Function<EntandoDeBundle, EntandoDeBundle> function) {
        this.function = function;
        this.status = new EntandoCustomResourceStatus();
    }

    public DoneableEntandoDeBundle(EntandoDeBundle resource, Function<EntandoDeBundle, EntandoDeBundle> function) {
        super(resource.getSpec(), resource.getMetadata());
        this.status = Optional.ofNullable(resource.getStatus()).orElse(new EntandoCustomResourceStatus());
        this.function = function;
    }

    @Override
    public DoneableEntandoDeBundle withStatus(AbstractServerStatus serverStatus) {
        this.status.putServerStatus(serverStatus);
        return this;
    }

    @Override
    public DoneableEntandoDeBundle withPhase(EntandoDeploymentPhase phase) {
        status.updateDeploymentPhase(phase, metadata.getGeneration());
        return this;
    }

    @Override
    public EntandoDeBundle done() {
        return function.apply(new EntandoDeBundle(metadata.build(), spec.build(), status));
    }
}
