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

import java.util.HashMap;
import org.entando.kubernetes.model.EntandoDeploymentSpecBuilder;
import org.entando.kubernetes.model.JeeServer;
import org.entando.kubernetes.model.gitspec.GitSpec;
import org.entando.kubernetes.model.gitspec.GitSpecBuilder;
import org.entando.kubernetes.model.gitspec.GitSpecFluent;

public class EntandoAppSpecFluent<N extends EntandoAppSpecFluent> extends EntandoDeploymentSpecBuilder<N> {

    protected JeeServer standardServerImage;
    protected String keycloakSecretToUse;
    protected String clusterInfrastructureSecretToUse;
    protected String customServerImage;
    protected String ingressPath;
    private GitSpecBuilder backupGitSpec;
    private String ecrGitSshSecretName;

    public EntandoAppSpecFluent(EntandoAppSpec spec) {
        super(spec);
        this.standardServerImage = spec.getStandardServerImage().orElse(null);
        this.customServerImage = spec.getCustomServerImage().orElse(null);
        this.keycloakSecretToUse = spec.getKeycloakSecretToUse().orElse(null);
        this.clusterInfrastructureSecretToUse = spec.getClusterInfrastructureSecretToUse().orElse(null);
        this.ingressPath = spec.getIngressPath().orElse(null);
        this.backupGitSpec = spec.getBackupGitSpec().map(GitSpecBuilder::new).orElse(new GitSpecBuilder());
        this.ecrGitSshSecretName = spec.getEcrGitSshSecretName().orElse(null);
    }

    public EntandoAppSpecFluent() {
        this.backupGitSpec = new GitSpecBuilder();
    }

    public N withStandardServerImage(JeeServer jeeServer) {
        this.standardServerImage = jeeServer;
        this.customServerImage = jeeServer == null ? this.customServerImage : null;
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

    public N withKeycloakSecretToUse(String name) {
        this.keycloakSecretToUse = name;
        return thisAsN();
    }

    public N withEcrGitSshSecretname(String ecrGitSshSecretName) {
        this.ecrGitSshSecretName = ecrGitSshSecretName;
        return thisAsN();
    }

    public N withClusterInfrastructureSecretToUse(String name) {
        this.clusterInfrastructureSecretToUse = name;
        return thisAsN();
    }

    public GitSpecBuilderNested<N> editBackupGitSpec() {
        return new GitSpecBuilderNested<>(thisAsN(), this.backupGitSpec.build());
    }

    public GitSpecBuilderNested<N> withNewBackupGitSpec() {
        return new GitSpecBuilderNested<>(thisAsN());
    }

    public N withBackupGitSpec(GitSpec gitSpec) {
        this.backupGitSpec = new GitSpecBuilder(gitSpec);
        return thisAsN();
    }

    public EntandoAppSpec build() {
        return new EntandoAppSpec(this.standardServerImage, this.customServerImage, this.dbms, this.ingressHostName, this.ingressPath,
                this.replicas, this.tlsSecretName, this.keycloakSecretToUse,
                this.clusterInfrastructureSecretToUse,
                this.backupGitSpec.build(), this.serviceAccountToUse, new HashMap<>(), this.environmentVariables, this.resourceRequirements,
                this.ecrGitSshSecretName);
    }

    public static class GitSpecBuilderNested<P extends EntandoAppSpecFluent> extends GitSpecFluent<GitSpecBuilderNested<P>> {

        private P parentBuilder;

        public GitSpecBuilderNested(P parentBuilder, GitSpec gitSpec) {
            super(gitSpec);
            this.parentBuilder = parentBuilder;
        }

        public GitSpecBuilderNested(P parentBuilder) {
            this.parentBuilder = parentBuilder;
        }

        @SuppressWarnings("unchecked")
        public P endBackupGitSpec() {
            return (P) parentBuilder.withBackupGitSpec(super.build());
        }
    }

}
