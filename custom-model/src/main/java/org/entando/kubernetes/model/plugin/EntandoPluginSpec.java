package org.entando.kubernetes.model.plugin;

import static java.util.Optional.ofNullable;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.fabric8.kubernetes.api.model.KubernetesResource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.entando.kubernetes.model.DbmsImageVendor;

@JsonSerialize
@JsonDeserialize
@JsonInclude(Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = Visibility.ANY, isGetterVisibility = Visibility.NONE, getterVisibility = Visibility.NONE,
        setterVisibility = Visibility.NONE)
public class EntandoPluginSpec implements KubernetesResource {

    private String entandoAppName;
    private String entandoAppNamespace;
    private String image;
    private Integer replicas = 1;
    private DbmsImageVendor dbms;
    private String securityLevel;
    private List<String> connectionConfigNames = new ArrayList<>();
    private List<ExpectedRole> roles = new ArrayList<>();
    private List<Permission> permissions = new ArrayList<>();
    private Map<String, Object> parameters = new ConcurrentHashMap<>();
    private String ingressPath;
    private String keycloakServerNamespace;
    private String keycloakServerName;
    private String healthCheckPath;

    public EntandoPluginSpec() {
        //Needed for JSON Deserialization
    }

    @SuppressWarnings("PMD.ExcessiveParameterList")
    //Because the typical builder pattern requires a full constructor
    public EntandoPluginSpec(String entandoAppNamespace, String entandoAppName, String image, DbmsImageVendor dbms,
            Integer replicas, String ingressPath, String keycloakServerNamespace, String keycloakServerName,
            String healthCheckPath, PluginSecurityLevel securityLevel, List<ExpectedRole> roles, List<Permission> permissions,
            Map<String, Object> parameters, List<String> connectionConfigNames) {
        this();
        this.entandoAppNamespace = entandoAppNamespace;
        this.entandoAppName = entandoAppName;
        this.image = image;
        this.dbms = dbms;
        this.replicas = replicas;
        this.ingressPath = ingressPath;
        this.keycloakServerNamespace = keycloakServerNamespace;
        this.keycloakServerName = keycloakServerName;
        this.healthCheckPath = healthCheckPath;
        this.roles = roles;
        this.permissions = permissions;
        this.parameters = parameters;
        this.connectionConfigNames = connectionConfigNames;
        this.securityLevel = ofNullable(securityLevel).map(PluginSecurityLevel::toName).orElse(null);
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public Optional<PluginSecurityLevel> getSecurityLevel() {
        return ofNullable(PluginSecurityLevel.forName(securityLevel));
    }

    public String getEntandoAppNamespace() {
        return entandoAppNamespace;
    }

    public String getImage() {
        return image;
    }

    public Integer getReplicas() {
        return replicas;
    }

    public String getEntandoAppName() {
        return entandoAppName;
    }

    public Optional<DbmsImageVendor> getDbms() {
        return ofNullable(dbms);
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

    public String getKeycloakServerNamespace() {
        return keycloakServerNamespace;
    }

    public String getKeycloakServerName() {
        return keycloakServerName;
    }

    public List<String> getConnectionConfigNames() {
        return connectionConfigNames;
    }

    @SuppressWarnings("PMD.TooManyMethods")
    //TODO remove deprecated methods
    public static class EntandoPluginSpecBuilder<N extends EntandoPluginSpecBuilder> {

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
}
