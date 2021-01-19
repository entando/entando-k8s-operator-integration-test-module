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

package org.entando.kubernetes.model.link;

import io.fabric8.kubernetes.api.builder.Function;
import java.util.Optional;
import org.entando.kubernetes.model.AbstractServerStatus;
import org.entando.kubernetes.model.DoneableEntandoCustomResource;
import org.entando.kubernetes.model.EntandoCustomResourceStatus;
import org.entando.kubernetes.model.EntandoDeploymentPhase;

public class DoneableEntandoAppPluginLink extends EntandoAppPluginLinkFluent<DoneableEntandoAppPluginLink> implements
        DoneableEntandoCustomResource<DoneableEntandoAppPluginLink, EntandoAppPluginLink> {

    private final EntandoCustomResourceStatus status;
    private final Function<EntandoAppPluginLink, EntandoAppPluginLink> function;

    public DoneableEntandoAppPluginLink(Function<EntandoAppPluginLink, EntandoAppPluginLink> function) {
        this.status = new EntandoCustomResourceStatus();
        this.function = function;
    }

    public DoneableEntandoAppPluginLink(EntandoAppPluginLink resource, Function<EntandoAppPluginLink, EntandoAppPluginLink> function) {
        super(resource.getSpec(), resource.getMetadata());
        this.function = function;
        this.status = Optional.ofNullable(resource.getStatus()).orElse(new EntandoCustomResourceStatus());
    }

    @Override
    public DoneableEntandoAppPluginLink withStatus(AbstractServerStatus status) {
        this.status.putServerStatus(status);
        return this;
    }

    @Override
    public DoneableEntandoAppPluginLink withPhase(EntandoDeploymentPhase phase) {
        status.updateDeploymentPhase(phase, metadata.getGeneration());
        return this;
    }

    @Override
    public EntandoAppPluginLink done() {
        return function.apply(new EntandoAppPluginLink(metadata.build(), spec.build(), status));
    }

}
