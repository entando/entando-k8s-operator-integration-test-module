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

package org.entando.kubernetes.model.plugin;

import static java.util.Optional.ofNullable;
import static org.entando.kubernetes.model.Coalescence.coalesce;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.entando.kubernetes.model.DbmsImageVendor;
import org.entando.kubernetes.model.EntandoDeploymentSpec;
import org.entando.kubernetes.model.RequiresKeycloak;

@JsonSerialize
@JsonDeserialize
@JsonInclude(Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = Visibility.ANY, isGetterVisibility = Visibility.NONE, getterVisibility = Visibility.NONE,
        setterVisibility = Visibility.NONE)
@RegisterForReflection
@JsonIgnoreProperties(ignoreUnknown = true)
public class EntandoPluginSpec extends EntandoDeploymentSpec implements RequiresKeycloak {

    private String image;
    private PluginSecurityLevel securityLevel;
    private List<String> connectionConfigNames = new ArrayList<>();
    private List<ExpectedRole> roles = new ArrayList<>();
    private List<Permission> permissions = new ArrayList<>();
    private String ingressPath;
    private String keycloakSecretToUse;
    private String healthCheckPath;
    private String clusterInfrastructureToUse;
    private List<String> companionContainers = new ArrayList<>();

    public EntandoPluginSpec() {
        //Needed for JSON Deserialization
    }

    /**
     * Only for use from the builder.
     */

    @SuppressWarnings("unchecked")
    @JsonCreator()
    public EntandoPluginSpec(@JsonProperty("image") String image,
            @JsonProperty("dbms") DbmsImageVendor dbms,
            @JsonProperty("replicas") Integer replicas,
            @JsonProperty("ingressPath") String ingressPath,
            @JsonProperty("keycloakSecretToUse") String keycloakSecretToUse,
            @JsonProperty("healthCheckPath") String healthCheckPath,
            @JsonProperty("securityLevel") PluginSecurityLevel securityLevel,
            @JsonProperty("tlsSecretName") String tlsSecretName,
            @JsonProperty("ingressHostName") String ingressHostName,
            @JsonProperty("roles") List<ExpectedRole> roles,
            @JsonProperty("permissions") List<Permission> permissions,
            @JsonProperty("parameters") Map<String, String> parameters,
            @JsonProperty("connectionConfigNames") List<String> connectionConfigNames,
            @JsonProperty("clusterInfrastructureToUse") String clusterInfrastructureToUse,
            @JsonProperty("companionContainers") List<String> companionContainers
    ) {
        super(ingressHostName, tlsSecretName, replicas, dbms, parameters);
        this.image = image;
        this.ingressPath = ingressPath;
        this.keycloakSecretToUse = keycloakSecretToUse;
        this.healthCheckPath = healthCheckPath;
        this.roles = coalesce(roles, this.roles);
        this.permissions = coalesce(permissions, this.permissions);
        this.connectionConfigNames = coalesce(connectionConfigNames, this.connectionConfigNames);
        this.securityLevel = securityLevel;
        this.clusterInfrastructureToUse = clusterInfrastructureToUse;
        this.companionContainers = coalesce(companionContainers, this.companionContainers);
    }

    public Optional<PluginSecurityLevel> getSecurityLevel() {
        return ofNullable(securityLevel);
    }

    public String getImage() {
        return image;
    }

    public List<ExpectedRole> getRoles() {
        return roles;
    }

    public List<Permission> getPermissions() {
        return permissions;
    }

    public String getIngressPath() {
        return ingressPath;
    }

    public String getHealthCheckPath() {
        return healthCheckPath;
    }

    @Override
    public Optional<String> getKeycloakSecretToUse() {
        return ofNullable(keycloakSecretToUse);
    }

    public Optional<String> getClusterInfrastructureTouse() {
        return ofNullable(clusterInfrastructureToUse);
    }

    public List<String> getConnectionConfigNames() {
        return connectionConfigNames;
    }

    public List<String> getCompanionContainers() {
        return this.companionContainers;
    }
}
