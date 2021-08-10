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

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.entando.kubernetes.model.common.DbmsVendor;
import org.entando.kubernetes.model.common.ResourceReference;

@JsonSerialize
@JsonDeserialize
@JsonInclude(Include.NON_NULL)
@JsonAutoDetect(
        fieldVisibility = Visibility.ANY,
        isGetterVisibility = Visibility.NONE,
        getterVisibility = Visibility.NONE,
        setterVisibility = Visibility.NONE
)
@JsonIgnoreProperties(
        ignoreUnknown = true
)

public class CapabilityRequirement {

    public static final String PREFERRED_DBMS = "preferredDbms";
    public static final String PREFERRED_INGRESS_HOST_NAME = "preferredIngressHostName";
    public static final String PREFERRED_TLS_SECRET_NAME = "preferredTlsSecretName";

    private StandardCapability capability;
    private StandardCapabilityImplementation implementation;
    private List<CapabilityScope> resolutionScopePreference;
    private CapabilityProvisioningStrategy provisioningStrategy;
    private Map<String, String> selector;
    private Map<String, String> capabilityParameters;
    private ResourceReference specifiedCapability;
    private ExternallyProvidedService externallyProvidedService;

    public CapabilityRequirement() {
    }

    @JsonCreator
    public CapabilityRequirement(@JsonProperty("capability") StandardCapability capability,
            @JsonProperty("implementation") StandardCapabilityImplementation implementation,
            @JsonProperty("resolutionScopePreference") List<CapabilityScope> resolutionScopePreference,
            @JsonProperty("provisioningStrategy") CapabilityProvisioningStrategy provisioningStrategy,
            @JsonProperty("selector") Map<String, String> selector,
            @JsonProperty("capabilityParameters") Map<String, String> capabilityParameters,
            @JsonProperty("specifiedCapability") ResourceReference specifiedCapability,
            @JsonProperty("externallyProvisionedService") ExternallyProvidedService externallyProvidedService
    ) {
        this.capability = capability;
        this.implementation = implementation;
        this.resolutionScopePreference = resolutionScopePreference;
        this.provisioningStrategy = provisioningStrategy;
        this.selector = selector;
        this.capabilityParameters = capabilityParameters;
        this.specifiedCapability = specifiedCapability;
        this.externallyProvidedService = externallyProvidedService;
    }

    public StandardCapability getCapability() {
        return capability;
    }

    public Optional<StandardCapabilityImplementation> getImplementation() {
        return Optional.ofNullable(implementation);
    }

    public Optional<String> getPreferredIngressHostName() {
        return Optional.ofNullable(getCapabilityParameters().get(PREFERRED_INGRESS_HOST_NAME));
    }

    public Optional<DbmsVendor> getPreferredDbms() {
        return Optional.ofNullable(getCapabilityParameters().get(PREFERRED_DBMS)).map(DbmsVendor::fromValue);
    }

    public Optional<String> getPreferredTlsSecretName() {
        return Optional.ofNullable(getCapabilityParameters().get(PREFERRED_TLS_SECRET_NAME));
    }

    public List<CapabilityScope> getResolutionScopePreference() {
        return resolutionScopePreference;
    }

    public Map<String, String> getSelector() {
        selector = Objects.requireNonNullElseGet(selector, ConcurrentHashMap::new);
        return selector;
    }

    public Map<String, String> getCapabilityParameters() {
        capabilityParameters = Objects.requireNonNullElseGet(capabilityParameters, ConcurrentHashMap::new);
        return capabilityParameters;
    }

    public Optional<ResourceReference> getSpecifiedCapability() {
        return Optional.ofNullable(specifiedCapability);
    }

    public Optional<CapabilityProvisioningStrategy> getProvisioningStrategy() {
        return Optional.ofNullable(provisioningStrategy);
    }

    public Optional<ExternallyProvidedService> getExternallyProvisionedService() {
        return Optional.ofNullable(externallyProvidedService);
    }

}
