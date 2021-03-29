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

import org.entando.kubernetes.model.ClusterInfrastructureAwareSpecFluent;
import org.entando.kubernetes.model.JeeServer;
import org.entando.kubernetes.model.gitspec.GitSpec;
import org.entando.kubernetes.model.gitspec.GitSpecFluent;

public class EntandoAppSpecFluent<N extends EntandoAppSpecFluent<N>> extends ClusterInfrastructureAwareSpecFluent<N> {

    protected JeeServer standardServerImage;
    protected String customServerImage;
    protected String ingressPath;
    private GitSpec backupGitSpec;
    private String ecrGitSshSecretName;

    public EntandoAppSpecFluent(EntandoAppSpec spec) {
        super(spec);
        this.standardServerImage = spec.getStandardServerImage().orElse(null);
        this.customServerImage = spec.getCustomServerImage().orElse(null);
        this.ingressPath = spec.getIngressPath().orElse(null);
        this.backupGitSpec = spec.getBackupGitSpec().orElse(null);
        this.ecrGitSshSecretName = spec.getEcrGitSshSecretName().orElse(null);
    }

    public EntandoAppSpecFluent() {

    }

    public N withStandardServerImage(JeeServer jeeServer) {
        this.standardServerImage = jeeServer;
        this.customServerImage = jeeServer == null ? this.customServerImage : null;
        return thisAsF();
    }

    public N withIngressPath(String ingressPath) {
        this.ingressPath = ingressPath;
        return thisAsF();
    }

    public N withCustomServerImage(String customServerImage) {
        this.customServerImage = customServerImage;
        this.standardServerImage = customServerImage == null ? standardServerImage : null;
        return thisAsF();
    }

    public N withEcrGitSshSecretname(String ecrGitSshSecretName) {
        this.ecrGitSshSecretName = ecrGitSshSecretName;
        return thisAsF();
    }

    public GitSpecBuilderNested<N> editBackupGitSpec() {
        return new GitSpecBuilderNested<>(thisAsF(), this.backupGitSpec);
    }

    public GitSpecBuilderNested<N> withNewBackupGitSpec() {
        return new GitSpecBuilderNested<>(thisAsF());
    }

    public N withBackupGitSpec(GitSpec gitSpec) {
        this.backupGitSpec = gitSpec;
        return thisAsF();
    }

    public EntandoAppSpec build() {
        return new EntandoAppSpec(this.standardServerImage, this.customServerImage, this.dbms, this.ingressHostName, this.ingressPath,
                this.replicas, this.tlsSecretName, this.keycloakToUse,
                this.clusterInfrastructureToUse,
                this.backupGitSpec, this.serviceAccountToUse, this.environmentVariables, this.resourceRequirements,
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
