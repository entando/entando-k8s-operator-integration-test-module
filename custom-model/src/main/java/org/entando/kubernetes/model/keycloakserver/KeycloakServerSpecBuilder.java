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

public class KeycloakServerSpecBuilder<N extends KeycloakServerSpecBuilder> extends EntandoDeploymentSpecBuilder<N> {

    private String imageName;
    private String entandoImageVersion;
    private boolean isDefault;

    public KeycloakServerSpecBuilder(KeycloakServerSpec spec) {
        super(spec);
        this.imageName = spec.getImageName().orElse(null);
        this.entandoImageVersion = spec.getEntandoImageVersion().orElse(null);
        this.isDefault = spec.isDefault();
    }

    public KeycloakServerSpecBuilder() {
        //required default constructor
    }

    public N withDefault(boolean isDefault) {
        this.isDefault = isDefault;
        return (N) this;
    }

    public N withImageName(String imageName) {
        this.imageName = imageName;
        return (N) this;
    }

    public N withEntandoImageVersion(String entandoImageVersion) {
        this.entandoImageVersion = entandoImageVersion;
        return (N) this;
    }

    public KeycloakServerSpec build() {
        return new KeycloakServerSpec(imageName, dbms, ingressHostName, entandoImageVersion, tlsSecretName, replicas, isDefault);
    }

}
