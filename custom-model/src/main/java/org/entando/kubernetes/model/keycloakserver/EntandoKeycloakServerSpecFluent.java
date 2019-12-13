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

package org.entando.kubernetes.model.keycloakserver;

import org.entando.kubernetes.model.EntandoDeploymentSpecBuilder;

public class EntandoKeycloakServerSpecFluent<N extends EntandoKeycloakServerSpecFluent> extends EntandoDeploymentSpecBuilder<N> {

    protected String imageName;
    protected String entandoImageVersion;
    protected boolean isDefault;

    public EntandoKeycloakServerSpecFluent(EntandoKeycloakServerSpec spec) {
        super(spec);
        this.entandoImageVersion = spec.getEntandoImageVersion().orElse(null);
        this.isDefault = spec.isDefault();
        this.imageName = spec.getImageName().orElse(null);
    }

    public EntandoKeycloakServerSpecFluent() {

    }

    @SuppressWarnings("unchecked")
    public N withDefault(boolean isDefault) {
        this.isDefault = isDefault;
        return (N) this;
    }

    @SuppressWarnings("unchecked")
    public N withImageName(String imageName) {
        this.imageName = imageName;
        return (N) this;
    }

    @SuppressWarnings("unchecked")
    public N withEntandoImageVersion(String entandoImageVersion) {
        this.entandoImageVersion = entandoImageVersion;
        return (N) this;
    }

    public EntandoKeycloakServerSpec build() {
        return new EntandoKeycloakServerSpec(imageName, dbms, ingressHostName, entandoImageVersion, tlsSecretName, replicas, isDefault);
    }
}
