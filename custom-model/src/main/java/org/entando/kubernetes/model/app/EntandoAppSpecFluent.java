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

import static java.util.Optional.ofNullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.entando.kubernetes.model.common.JeeServer;
import org.entando.kubernetes.model.common.KeycloakAwareSpecFluent;

public class EntandoAppSpecFluent<N extends EntandoAppSpecFluent<N>> extends KeycloakAwareSpecFluent<N> {

    protected JeeServer standardServerImage;
    protected String customServerImage;
    protected String ingressPath;
    private String ecrGitSshSecretName;
    private String entandoAppVersion;
    private List<String> componentRepositoryNamespaces;

    public EntandoAppSpecFluent(EntandoAppSpec spec) {
        super(spec);
        this.standardServerImage = spec.getStandardServerImage().orElse(null);
        this.customServerImage = spec.getCustomServerImage().orElse(null);
        this.ingressPath = spec.getIngressPath().orElse(null);
        this.ecrGitSshSecretName = spec.getEcrGitSshSecretName().orElse(null);
        this.entandoAppVersion = spec.getEntandoAppVersion().orElse(null);
        this.componentRepositoryNamespaces = spec.getComponentRepositoryNamespaces();
    }

    public EntandoAppSpecFluent() {

    }

    public N withToComponentRepositoryNamespaces(List<String> componentRepositoryNamespaces) {
        this.componentRepositoryNamespaces = null;
        return addToComponentRepositoryNamespaces(componentRepositoryNamespaces);
    }

    public N addToComponentRepositoryNamespaces(List<String> componentRepositoryNamespaces) {
        ofNullable(componentRepositoryNamespaces).ifPresent(s -> s.forEach(this::addToComponentRepositoryNamespaces));
        return thisAsF();
    }

    public N addToComponentRepositoryNamespaces(String componentRepositoryNamespaces) {
        this.componentRepositoryNamespaces = Objects.requireNonNullElseGet(this.componentRepositoryNamespaces, ArrayList::new);
        ofNullable(componentRepositoryNamespaces).ifPresent(this.componentRepositoryNamespaces::add);
        return thisAsF();
    }

    public N withEntandoAppVersion(String entandoAppVersion) {
        this.entandoAppVersion = entandoAppVersion;
        return thisAsF();
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

    public EntandoAppSpec build() {
        return new EntandoAppSpec(this.standardServerImage, this.customServerImage, this.dbms, this.ingressHostName, this.ingressPath,
                this.replicas, this.tlsSecretName, this.keycloakToUse,
                this.serviceAccountToUse, this.environmentVariables, this.resourceRequirements,
                this.ecrGitSshSecretName, this.storageClass, this.entandoAppVersion, this.componentRepositoryNamespaces);
    }

}
