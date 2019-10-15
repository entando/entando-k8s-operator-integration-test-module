/*
 *
 *  * Copyright 2015-Present Entando Inc. (http://www.entando.com) All rights reserved.
 *  *
 *  * This library is free software; you can redistribute it and/or modify it under
 *  * the terms of the GNU Lesser General Public License as published by the Free
 *  * Software Foundation; either version 2.1 of the License, or (at your option)
 *  * any later version.
 *  *
 *  * This library is distributed in the hope that it will be useful, but WITHOUT
 *  * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 *  * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 *  * details.
 *
 */

package org.entando.kubernetes.model.keycloakserver;

import io.fabric8.kubernetes.api.builder.Function;
import java.util.Optional;
import org.entando.kubernetes.model.AbstractServerStatus;
import org.entando.kubernetes.model.DoneableEntandoCustomResource;
import org.entando.kubernetes.model.EntandoCustomResourceStatus;
import org.entando.kubernetes.model.EntandoDeploymentPhase;

public class DoneableKeycloakServer extends KeycloakServerFluent<DoneableKeycloakServer> implements
        DoneableEntandoCustomResource<DoneableKeycloakServer, KeycloakServer> {

    private final Function<KeycloakServer, KeycloakServer> function;
    private final EntandoCustomResourceStatus status;

    public DoneableKeycloakServer(KeycloakServer resource, Function<KeycloakServer, KeycloakServer> function) {
        super(resource.getSpec(), resource.getMetadata());
        this.status = Optional.ofNullable(resource.getStatus()).orElse(new EntandoCustomResourceStatus());
        this.function = function;
    }

    @Override
    public DoneableKeycloakServer withStatus(AbstractServerStatus status) {
        this.status.putServerStatus(status);
        return this;
    }

    @Override
    public DoneableKeycloakServer withPhase(EntandoDeploymentPhase phase) {
        status.setEntandoDeploymentPhase(phase);
        return this;
    }

    @Override
    public KeycloakServer done() {
        KeycloakServer keycloakServer = new KeycloakServer(metadata.build(), spec.build(), status);
        return function.apply(keycloakServer);
    }
}
