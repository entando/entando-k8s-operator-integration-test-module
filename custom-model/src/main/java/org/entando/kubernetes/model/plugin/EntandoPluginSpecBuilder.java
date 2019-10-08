package org.entando.kubernetes.model.plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.entando.kubernetes.model.DbmsImageVendor;

@SuppressWarnings({"PMD.TooManyMethods"})
//TODO remove deprecated methods
public class EntandoPluginSpecBuilder<N extends EntandoPluginSpecBuilder> {

    private final List<String> connectionConfigNames;
    private final List<ExpectedRole> roles;
    private final List<Permission> permissions;
    private final Map<String, Object> parameters;
    private String image;
    private int replicas = 1;
    private DbmsImageVendor dbms;
    private String ingressPath;
    private String keycloakSecretToUse;
    private String clusterInfrastructureToUse;
    private String healthCheckPath;
    private PluginSecurityLevel securityLevel;
    private String tlsSecretName;
    private String ingressHostName;

    public EntandoPluginSpecBuilder() {
        //default constructor required
        connectionConfigNames = new ArrayList<>();
        roles = new ArrayList<>();
        permissions = new ArrayList<>();
        parameters = new ConcurrentHashMap<>();
    }

    public EntandoPluginSpecBuilder(EntandoPluginSpec spec) {
        this.connectionConfigNames = new ArrayList<>(spec.getConnectionConfigNames());
        this.roles = new ArrayList<>(spec.getRoles());
        this.permissions = new ArrayList<>(spec.getPermissions());
        this.parameters = new ConcurrentHashMap<>(spec.getParameters());
        this.image = spec.getImage();
        this.replicas = spec.getReplicas();
        this.dbms = spec.getDbms().orElse(null);
        this.ingressPath = spec.getIngressPath();
        this.keycloakSecretToUse = spec.getKeycloakSecretToUse().orElse(null);
        this.healthCheckPath = spec.getHealthCheckPath();
        this.securityLevel = spec.getSecurityLevel().orElse(null);
        this.clusterInfrastructureToUse = spec.getClusterInfrastructureTouse().orElse(null);
        this.ingressHostName = spec.getIngressHostName().orElse(null);
        this.tlsSecretName = spec.getTlsSecretName().orElse(null);
    }

    public N withClusterInfrastructureToUse(String name) {
        this.clusterInfrastructureToUse = name;
        return (N) this;
    }

    public N withDbms(DbmsImageVendor dbms) {
        this.dbms = dbms;
        return (N) this;
    }

    public N withIngressPath(String ingressPath) {
        this.ingressPath = ingressPath;
        return (N) this;
    }

    public N addNewConnectionConfigName(String name) {
        connectionConfigNames.add(name);
        return (N) this;
    }

    public N withTlsSecretName(String tlsSecretName) {
        this.tlsSecretName = tlsSecretName;
        return (N) this;
    }

    public N withIngressHostName(String ingressHostName) {
        this.ingressHostName = ingressHostName;
        return (N) this;
    }

    public N withImage(String image) {
        this.image = image;
        return (N) this;
    }

    public N withSecurityLevel(PluginSecurityLevel level) {
        this.securityLevel = level;
        return (N) this;
    }

    public N withKeycloakSecretToUse(String name) {
        this.keycloakSecretToUse = name;
        return (N) this;
    }

    public N withReplicas(Integer replicas) {
        this.replicas = replicas;
        return (N) this;
    }

    @Deprecated
    public N withRole(String code, String name) {
        return addNewRole(code, name);
    }

    public N addNewRole(String code, String name) {
        roles.add(new ExpectedRole(code, name));
        return (N) this;
    }

    @Deprecated
    public N withPermission(String clientId, String role) {
        return addNewPermission(clientId, role);
    }

    public N addNewPermission(String clientId, String role) {
        permissions.add(new Permission(clientId, role));
        return (N) this;
    }

    public N addNewParameter(String name, Object value) {
        this.parameters.put(name, value);
        return (N) this;
    }

    public EntandoPluginSpec build() {
        return new EntandoPluginSpec(image, dbms, replicas, ingressPath, keycloakSecretToUse,
                healthCheckPath, securityLevel, tlsSecretName, ingressHostName, roles, permissions, parameters, connectionConfigNames,
                clusterInfrastructureToUse);
    }

    public N withHealthCheckPath(String healthCheckPath) {
        this.healthCheckPath = healthCheckPath;
        return (N) this;
    }

    public N withConnectionConfigNames(List<String> strings) {
        this.connectionConfigNames.clear();
        this.connectionConfigNames.addAll(strings);
        return (N) this;
    }

    public N withRoles(List<ExpectedRole> roles) {
        this.roles.clear();
        this.roles.addAll(roles);
        return (N) this;
    }

    public N withPermissions(List<Permission> permissions) {
        this.permissions.clear();
        this.permissions.addAll(permissions);
        return (N) this;
    }

    public N withParameters(Map<String, Object> parameters) {
        this.parameters.clear();
        this.parameters.putAll(parameters);
        return (N) this;
    }
}
