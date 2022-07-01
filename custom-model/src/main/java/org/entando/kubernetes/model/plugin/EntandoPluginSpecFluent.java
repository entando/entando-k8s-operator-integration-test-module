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

import java.util.ArrayList;
import java.util.List;
import org.entando.kubernetes.model.common.ExpectedRole;
import org.entando.kubernetes.model.common.KeycloakAwareSpecFluent;
import org.entando.kubernetes.model.common.Permission;

public class EntandoPluginSpecFluent<N extends EntandoPluginSpecFluent<N>> extends KeycloakAwareSpecFluent<N> {

    protected final List<String> connectionConfigNames;
    protected final List<ExpectedRole> roles;
    protected final List<Permission> permissions;
    private final List<String> companionContainers;
    protected String image;
    protected String ingressPath;
    protected String customIngressPath;
    protected String healthCheckPath;
    protected PluginSecurityLevel securityLevel;

    public EntandoPluginSpecFluent(EntandoPluginSpec spec) {
        super(spec);
        this.ingressPath = spec.getIngressPath();
        this.customIngressPath = spec.getCustomIngressPath();
        this.healthCheckPath = spec.getHealthCheckPath();
        this.securityLevel = spec.getSecurityLevel().orElse(null);
        this.image = spec.getImage();
        this.permissions = new ArrayList<>(spec.getPermissions());
        this.connectionConfigNames = new ArrayList<>(spec.getConnectionConfigNames());
        this.roles = new ArrayList<>(spec.getRoles());
        this.companionContainers = new ArrayList<>(spec.getCompanionContainers());
    }

    public EntandoPluginSpecFluent() {
        //default constructor required
        connectionConfigNames = new ArrayList<>();
        roles = new ArrayList<>();
        permissions = new ArrayList<>();
        this.companionContainers = new ArrayList<>();
    }

    public N withIngressPath(String ingressPath) {
        this.ingressPath = ingressPath;
        return thisAsF();
    }

    public N withCustomIngressPath(String customIngressPath) {
        this.customIngressPath = customIngressPath;
        return thisAsF();
    }

    public N addNewConnectionConfigName(String name) {
        connectionConfigNames.add(name);
        return thisAsF();
    }

    public N withImage(String image) {
        this.image = image;
        return thisAsF();
    }

    public N withSecurityLevel(PluginSecurityLevel level) {
        this.securityLevel = level;
        return thisAsF();
    }

    public N addNewRole(String code, String name) {
        roles.add(new ExpectedRole(code, name));
        return thisAsF();
    }

    public N addNewCompanionContainer(String name) {
        this.companionContainers.add(name);
        return thisAsF();
    }

    public N addNewPermission(String clientId, String role) {
        permissions.add(new Permission(clientId, role));
        return thisAsF();
    }

    public N withHealthCheckPath(String healthCheckPath) {
        this.healthCheckPath = healthCheckPath;
        return thisAsF();
    }

    public N withConnectionConfigNames(List<String> strings) {
        this.connectionConfigNames.clear();
        this.connectionConfigNames.addAll(strings);
        return thisAsF();
    }

    public N withCompanionContainers(List<String> strings) {
        this.companionContainers.clear();
        this.companionContainers.addAll(strings);
        return thisAsF();
    }

    public N withRoles(List<ExpectedRole> roles) {
        this.roles.clear();
        this.roles.addAll(roles);
        return thisAsF();
    }

    public N withPermissions(List<Permission> permissions) {
        this.permissions.clear();
        this.permissions.addAll(permissions);
        return thisAsF();
    }

    public EntandoPluginSpec build() {
        return new EntandoPluginSpec(image, dbms, replicas, ingressPath, customIngressPath, keycloakToUse,
                healthCheckPath, securityLevel, tlsSecretName, ingressHostName, roles, permissions,
                serviceAccountToUse, environmentVariables, connectionConfigNames,
                companionContainers, resourceRequirements, storageClass);
    }

}
