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

import org.entando.kubernetes.model.EntandoDeploymentSpecBuilder;

public class EntandoClusterInfrastructureSpecFluent<N extends EntandoClusterInfrastructureSpecFluent> extends
        EntandoDeploymentSpecBuilder<N> {

    private String entandoImageVersion;
    private String keycloakSecretToUse;
    private boolean isDefault;

    public EntandoClusterInfrastructureSpecFluent(EntandoClusterInfrastructureSpec spec) {
        super(spec);
        this.keycloakSecretToUse = spec.getKeycloakSecretToUse().orElse(null);
        this.entandoImageVersion = spec.getEntandoImageVersion().orElse(null);
        this.isDefault = spec.isDefault();
    }

    public EntandoClusterInfrastructureSpecFluent() {

    }

    @SuppressWarnings("unchecked")
    public N withKeycloakSecretToUse(String keycloakSecretToUse) {
        this.keycloakSecretToUse = keycloakSecretToUse;
        return (N) this;
    }

    @SuppressWarnings("unchecked")
    public N withDefault(boolean isDefault) {
        this.isDefault = isDefault;
        return (N) this;
    }

    @SuppressWarnings("unchecked")
    public N withEntandoImageVersion(String entandoImageVersion) {
        this.entandoImageVersion = entandoImageVersion;
        return (N) this;
    }

    public EntandoClusterInfrastructureSpec build() {
        return new EntandoClusterInfrastructureSpec(dbms, ingressHostName, entandoImageVersion, tlsSecretName, replicas,
                keycloakSecretToUse, isDefault, this.serviceAccountToUse, parameters);
    }
}
