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

import org.entando.kubernetes.model.EntandoDeploymentSpecBuilder;
import org.entando.kubernetes.model.JeeServer;

public class EntandoAppSpecFluent<N extends EntandoAppSpecFluent> extends EntandoDeploymentSpecBuilder<N> {

    protected JeeServer standardServerImage;
    protected String entandoImageVersion;
    protected String keycloakSecretToUse;
    protected String clusterInfrastructureToUse;
    protected String customServerImage;
    protected String ingressPath;
    protected String backupGitRepo;
    protected String backupGitSecretName;

    public EntandoAppSpecFluent(EntandoAppSpec spec) {
        super(spec);
        this.entandoImageVersion = spec.getEntandoImageVersion().orElse(null);
        this.standardServerImage = spec.getStandardServerImage().orElse(null);
        this.customServerImage = spec.getCustomServerImage().orElse(null);
        this.keycloakSecretToUse = spec.getKeycloakSecretToUse().orElse(null);
        this.clusterInfrastructureToUse = spec.getClusterInfrastructureTouse().orElse(null);
        this.ingressPath = spec.getIngressPath().orElse(null);
        this.backupGitRepo = spec.getBackupGitRepo().orElse(null);
        this.backupGitSecretName = spec.getBackupGitSecretName().orElse(null);

    }

    public EntandoAppSpecFluent() {

    }

    public N withStandardServerImage(JeeServer jeeServer) {
        this.standardServerImage = jeeServer;
        this.customServerImage = jeeServer == null ? this.customServerImage : null;
        return thisAsN();
    }

    public N withBackupGitRepo(String backupGitRepo) {
        this.backupGitRepo = backupGitRepo;
        return thisAsN();
    }

    public N withBackupGitSecretName(String backupGitSecretName) {
        this.backupGitSecretName = backupGitSecretName;
        return thisAsN();
    }

    public N withIngressPath(String ingressPath) {
        this.ingressPath = ingressPath;
        return thisAsN();
    }

    public N withCustomServerImage(String customServerImage) {
        this.customServerImage = customServerImage;
        this.standardServerImage = customServerImage == null ? standardServerImage : null;
        return thisAsN();
    }

    public N withEntandoImageVersion(String entandoImageVersion) {
        this.entandoImageVersion = entandoImageVersion;
        return thisAsN();
    }

    public N withKeycloakSecretToUse(String name) {
        this.keycloakSecretToUse = name;
        return thisAsN();
    }

    public N withClusterInfrastructureToUse(String name) {
        this.clusterInfrastructureToUse = name;
        return thisAsN();
    }

    public EntandoAppSpec build() {
        return new EntandoAppSpec(this.standardServerImage, this.customServerImage, this.dbms, this.ingressHostName, this.ingressPath,
                this.replicas, this.entandoImageVersion, this.tlsSecretName, this.keycloakSecretToUse, this.clusterInfrastructureToUse,
                this.backupGitRepo, this.backupGitSecretName,
                this.parameters);
    }

}
