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

import io.fabric8.kubernetes.api.builder.Function;
import io.fabric8.kubernetes.api.model.Doneable;
import java.util.Optional;
import org.entando.kubernetes.model.AbstractServerStatus;
import org.entando.kubernetes.model.DoneableEntandoCustomResource;
import org.entando.kubernetes.model.EntandoCustomResourceStatus;
import org.entando.kubernetes.model.EntandoDeploymentPhase;

public class DoneableExternalDatabase extends ExternalDatabaseFluent<DoneableExternalDatabase> implements Doneable<ExternalDatabase>,
        DoneableEntandoCustomResource<DoneableExternalDatabase, ExternalDatabase> {

    private final Function<ExternalDatabase, ExternalDatabase> function;
    private final EntandoCustomResourceStatus status;

    public DoneableExternalDatabase(Function<ExternalDatabase, ExternalDatabase> function) {
        this.status = new EntandoCustomResourceStatus();
        this.function = function;
    }

    public DoneableExternalDatabase(ExternalDatabase resource, Function<ExternalDatabase, ExternalDatabase> function) {
        super(resource.getSpec(), resource.getMetadata());
        this.status = Optional.ofNullable(resource.getStatus()).orElse(new EntandoCustomResourceStatus());
        this.function = function;
    }

    @Override
    public ExternalDatabase done() {
        return function.apply(build());
    }

    @Override
    public DoneableExternalDatabase withStatus(AbstractServerStatus status) {
        this.status.putServerStatus(status);
        return this;
    }

    @Override
    public DoneableExternalDatabase withPhase(EntandoDeploymentPhase phase) {
        status.setEntandoDeploymentPhase(phase);
        return this;
    }

    private ExternalDatabase build() {
        return new ExternalDatabase(super.metadata.build(), super.spec.build(), status);
    }
}
