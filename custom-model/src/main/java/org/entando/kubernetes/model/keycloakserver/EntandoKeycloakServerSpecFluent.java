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

import org.entando.kubernetes.model.capability.CapabilityProvisioningStrategy;
import org.entando.kubernetes.model.capability.CapabilityScope;
import org.entando.kubernetes.model.common.EntandoIngressingDeploymentSpecFluent;

public class EntandoKeycloakServerSpecFluent<N extends EntandoKeycloakServerSpecFluent<N>>
        extends EntandoIngressingDeploymentSpecFluent<N> {

    protected String customImage;
    protected boolean isDefault;
    private StandardKeycloakImage standardImage;
    private String frontEndUrl;
    private CapabilityProvisioningStrategy provisioningStrategy;
    private String adminSecretName;
    private CapabilityScope providedCapabilityScope;
    private String defaultRealm;

    public EntandoKeycloakServerSpecFluent(EntandoKeycloakServerSpec spec) {
        super(spec);
        this.isDefault = spec.isDefault();
        this.customImage = spec.getCustomImage().orElse(null);
        this.standardImage = spec.getStandardImage().orElse(null);
        this.frontEndUrl = spec.getFrontEndUrl().orElse(null);
        this.adminSecretName = spec.getAdminSecretName().orElse(null);
        this.provisioningStrategy = spec.getProvisioningStrategy().orElse(null);
        this.providedCapabilityScope = spec.getProvidedCapabilityScope().orElse(null);
        this.defaultRealm = spec.getDefaultRealm().orElse(null);
    }

    public EntandoKeycloakServerSpecFluent() {

    }

    public N withDefault(boolean isDefault) {
        this.isDefault = isDefault;
        return thisAsF();
    }

    public N withCustomImage(String customImage) {
        this.customImage = customImage;
        return thisAsF();
    }

    public N withAdminSecretName(String adminSecretName) {
        this.adminSecretName = adminSecretName;
        return thisAsF();
    }

    public N withProvisioningStrategy(CapabilityProvisioningStrategy provisioningStrategy) {
        this.provisioningStrategy = provisioningStrategy;
        return thisAsF();
    }

    public N withFrontEndUrl(String frontEndUrl) {
        this.frontEndUrl = frontEndUrl;
        return thisAsF();
    }

    public N withProvidedCapabilityScope(CapabilityScope providedCapabilityScope) {
        this.providedCapabilityScope = providedCapabilityScope;
        return thisAsF();
    }

    public N withStandardImage(StandardKeycloakImage standardImage) {
        this.standardImage = standardImage;
        return thisAsF();
    }

    public EntandoKeycloakServerSpec build() {
        return new EntandoKeycloakServerSpec(customImage, standardImage, frontEndUrl, provisioningStrategy, adminSecretName, dbms,
                ingressHostName, tlsSecretName, replicas, isDefault,
                serviceAccountToUse, environmentVariables, resourceRequirements, storageClass, providedCapabilityScope, defaultRealm);
    }

    public N withDefaultRealm(String defaultRealm) {
        this.defaultRealm = defaultRealm;
        return thisAsF();
    }
}
