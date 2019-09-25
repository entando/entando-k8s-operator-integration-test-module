package org.entando.kubernetes.model.plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.entando.kubernetes.model.DbmsImageVendor;

@SuppressWarnings("PMD.TooManyMethods")
//TODO remove deprecated methods
public class EntandoPluginSpecBuilder<N extends EntandoPluginSpecBuilder> {

    private final List<String> connectionConfigNames;
    private final List<ExpectedRole> roles;
    private final List<Permission> permissions;
    private final Map<String, Object> parameters;
    private String entandoAppName;
    private String entandoAppNamespace;
    private String image;
    private int replicas = 1;
    private DbmsImageVendor dbms;
    private String ingressPath;
    private String keycloakServerNamespace;
    private String keycloakServerName;
    private String healthCheckPath;
    private PluginSecurityLevel securityLevel;

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
        this.entandoAppName = spec.getEntandoAppName();
        this.entandoAppNamespace = spec.getEntandoAppNamespace();
        this.image = spec.getImage();
        this.replicas = spec.getReplicas();
        this.dbms = spec.getDbms().orElse(null);
        this.ingressPath = spec.getIngressPath();
        this.keycloakServerName = spec.getKeycloakServerName();
        this.keycloakServerNamespace = spec.getKeycloakServerNamespace();
        this.healthCheckPath = spec.getHealthCheckPath();
        this.securityLevel = spec.getSecurityLevel().orElse(null);
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

    public N withImage(String image) {
        this.image = image;
        return (N) this;
    }

    public N withSecurityLevel(PluginSecurityLevel level) {
        this.securityLevel = level;
        return (N) this;
    }

    public N withKeycloakServer(String namespace, String name) {
        this.keycloakServerName = name;
        this.keycloakServerNamespace = namespace;
        return (N) this;
    }

    public N withEntandoApp(String namespace, String name) {
        this.entandoAppName = name;
        this.entandoAppNamespace = namespace;
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
        return new EntandoPluginSpec(entandoAppNamespace, entandoAppName, image, dbms, replicas, ingressPath,
                keycloakServerNamespace, keycloakServerName, healthCheckPath, securityLevel, roles, permissions, parameters,
                connectionConfigNames);
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
