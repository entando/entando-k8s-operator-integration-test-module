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

package org.entando.kubernetes.model.app;

import org.entando.kubernetes.model.DbmsImageVendor;
import org.entando.kubernetes.model.EntandoDeploymentSpecBuilder;
import org.entando.kubernetes.model.JeeServer;

public class EntandoAppSpecBuilder<N extends EntandoAppSpecBuilder> extends EntandoDeploymentSpecBuilder<N> {

    private JeeServer standardServerImage;
    private String entandoImageVersion;
    private String keycloakSecretToUse;
    private String clusterInfrastructureToUse;
    private String customServerImage;

    public EntandoAppSpecBuilder() {
        //Needed for JSON Deserialization

    }

    public EntandoAppSpecBuilder(EntandoAppSpec spec) {
        super(spec);
        this.keycloakSecretToUse = spec.getKeycloakSecretToUse().orElse(null);
        this.entandoImageVersion = spec.getEntandoImageVersion().orElse(null);
        this.standardServerImage = spec.getStandardServerImage().orElse(null);
        this.clusterInfrastructureToUse = spec.getClusterInfrastructureTouse().orElse(null);
        this.customServerImage = spec.getCustomServerImage().orElse(null);
    }

    public N withStandardServerImage(JeeServer jeeServer) {
        this.standardServerImage = jeeServer;
        this.customServerImage = jeeServer == null ? this.customServerImage : null;
        return (N) this;
    }

    public N withCustomServerImage(String customServerImage) {
        this.customServerImage = customServerImage;
        this.standardServerImage = customServerImage == null ? standardServerImage : null;
        return (N) this;
    }

    public N withEntandoImageVersion(String entandoImageVersion) {
        this.entandoImageVersion = entandoImageVersion;
        return (N) this;
    }

    public N withKeycloakSecretToUse(String name) {
        this.keycloakSecretToUse = name;
        return (N) this;
    }

    public N withClusterInfrastructureToUse(String name) {
        this.clusterInfrastructureToUse = name;
        return (N) this;
    }

    public N withReplicas(Integer replicas) {
        this.replicas = replicas;
        return (N) this;
    }

    public EntandoAppSpec build() {
        return new EntandoAppSpec(this.standardServerImage, this.customServerImage, this.dbms, this.ingressHostName, this.replicas,
                this.entandoImageVersion, this.tlsSecretName, this.keycloakSecretToUse, this.clusterInfrastructureToUse);
    }
}
