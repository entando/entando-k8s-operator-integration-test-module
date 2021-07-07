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

package org.entando.kubernetes.model.capability;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.entando.kubernetes.model.common.DbmsVendor;
import org.entando.kubernetes.model.common.ResourceReference;

public class CapabilityRequirementFluent<N extends CapabilityRequirementFluent<N>> {

    private StandardCapability capability;
    private StandardCapabilityImplementation implementation;
    private List<CapabilityScope> resolutionScopePreference;
    private CapabilityProvisioningStrategy provisioningStrategy;
    private Map<String, String> selector;
    private Map<String, String> capabilityParameters;
    private ResourceReference specifiedCapability;
    private ExternallyProvidedService externallyProvidedService;

    public CapabilityRequirementFluent() {

    }

    public CapabilityRequirementFluent(CapabilityRequirement spec) {
        this.capability = spec.getCapability();
        this.implementation = spec.getImplementation().orElse(null);
        this.resolutionScopePreference = spec.getResolutionScopePreference();
        this.provisioningStrategy = spec.getProvisioningStrategy().orElse(null);
        this.selector = new HashMap<>(spec.getSelector());
        this.capabilityParameters = new HashMap<>(spec.getCapabilityParameters());
        this.specifiedCapability = spec.getSpecifiedCapability().orElse(null);
        this.externallyProvidedService = spec.getExternallyProvisionedService().orElse(null);

    }

    public N withCapability(StandardCapability capability) {
        this.capability = capability;
        return thisAsN();
    }

    public N withImplementation(StandardCapabilityImplementation implementation) {
        this.implementation = implementation;
        return thisAsN();
    }

    public N withResolutionScopePreference(CapabilityScope... resolutionScopePreference) {
        this.resolutionScopePreference = Arrays.asList(resolutionScopePreference);
        return thisAsN();
    }

    public N withSelector(Map<String, String> selector) {
        this.selector = selector;
        return thisAsN();
    }

    public N addAllToCapabilityParameters(Map<String, String> capabilityParameters) {
        getCapabilityParameters().putAll(capabilityParameters);
        return thisAsN();
    }

    public N withSpecifiedCapability(ResourceReference specifiedCapability) {
        this.specifiedCapability = specifiedCapability;
        return thisAsN();
    }

    public N withProvisioningStrategy(CapabilityProvisioningStrategy provisioningStrategy) {
        this.provisioningStrategy = provisioningStrategy;
        return thisAsN();
    }

    public N withExternallyProvidedService(ExternallyProvidedService externallyProvisionedService) {
        this.externallyProvidedService = externallyProvisionedService;
        return thisAsN();
    }

    protected Map<String, String> getCapabilityParameters() {
        this.capabilityParameters = Objects.requireNonNullElseGet(this.capabilityParameters, HashMap::new);
        return this.capabilityParameters;

    }

    public N withPreferredTlsSecretName(String preferredTlsSecretName) {
        getCapabilityParameters().put(CapabilityRequirement.PREFERRED_TLS_SECRET_NAME, preferredTlsSecretName);
        return thisAsN();
    }

    public N withPreferredIngressHostName(String preferredHostName) {
        getCapabilityParameters().put(CapabilityRequirement.PREFERRED_INGRESS_HOST_NAME, preferredHostName);
        return thisAsN();
    }

    public N withPreferredDbms(DbmsVendor dbms) {
        getCapabilityParameters().put(CapabilityRequirement.PREFERRED_DBMS, dbms.toValue());
        return thisAsN();
    }

    @SuppressWarnings("unchecked")
    protected N thisAsN() {
        return (N) this;
    }

    public CapabilityRequirement build() {
        return new CapabilityRequirement(this.capability, this.implementation, this.resolutionScopePreference, this.provisioningStrategy,
                this.selector, this.capabilityParameters, this.specifiedCapability, this.externallyProvidedService);
    }

    public ExternallyProvidedServiceNested withNewExternallyProvidedService() {
        return new ExternallyProvidedServiceNested(thisAsN());
    }

    public ExternallyProvidedServiceNested editExternallyProvidedService() {
        return new ExternallyProvidedServiceNested(thisAsN(), externallyProvidedService);
    }

    public class ExternallyProvidedServiceNested extends ExternallyProvidedServiceFluent<ExternallyProvidedServiceNested> {

        private final N parentBuilder;

        public ExternallyProvidedServiceNested(N parentBuilder, ExternallyProvidedService keycloakToUse) {
            super(keycloakToUse);
            this.parentBuilder = parentBuilder;
        }

        public ExternallyProvidedServiceNested(N parentBuilder) {
            this.parentBuilder = parentBuilder;
        }

        public N endExternallyProvidedService() {
            return parentBuilder.withExternallyProvidedService(super.build());
        }
    }

}
