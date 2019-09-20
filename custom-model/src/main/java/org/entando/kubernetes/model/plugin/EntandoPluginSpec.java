package org.entando.kubernetes.model.plugin;

import static java.util.Optional.ofNullable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.fabric8.kubernetes.api.model.KubernetesResource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.entando.kubernetes.model.DbmsImageVendor;

@JsonDeserialize
public class EntandoPluginSpec implements KubernetesResource {

    @JsonProperty
    private String entandoAppName;
    @JsonProperty
    private String entandoAppNamespace;
    @JsonProperty
    private String image;
    @JsonProperty
    private int replicas = 1;
    @JsonProperty
    private String dbms;
    @JsonProperty
    private String securityLevel;
    @JsonProperty
    private List<String> connectionConfigNames = new ArrayList<>();
    @JsonProperty
    private List<ExpectedRole> roles = new ArrayList<>();
    @JsonProperty
    private List<Permission> permissions = new ArrayList<>();
    @JsonProperty
    private Map<String, Object> parameters = new ConcurrentHashMap<>();
    @JsonProperty
    private String ingressPath;
    @JsonProperty
    private String keycloakServerNamespace;
    @JsonProperty
    private String keycloakServerName;
    @JsonProperty
    private String healthCheckPath;

    public EntandoPluginSpec() {
        //Needed for JSON Deserialization
    }

    @SuppressWarnings("PMD.ExcessiveParameterList")
    //Because the typical builder pattern requires a full constructor
    public EntandoPluginSpec(String entandoAppNamespace, String entandoAppName, String image, DbmsImageVendor dbms,
            int replicas, String ingressPath, String keycloakServerNamespace, String keycloakServerName,
            String healthCheckPath, PluginSecurityLevel securityLevel, List<ExpectedRole> roles, List<Permission> permissions,
            Map<String, Object> parameters, List<String> connectionConfigNames) {
        this();
        this.entandoAppNamespace = entandoAppNamespace;
        this.entandoAppName = entandoAppName;
        this.image = image;
        this.dbms = dbms.toValue();
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

    @JsonIgnore
    public Map<String, Object> getParameters() {
        return parameters;
    }

    @JsonIgnore
    public Optional<PluginSecurityLevel> getSecurityLevel() {
        return ofNullable(PluginSecurityLevel.forName(securityLevel));
    }

    @JsonIgnore
    public String getEntandoAppNamespace() {
        return entandoAppNamespace;
    }

    @JsonIgnore
    public String getImage() {
        return image;
    }

    @JsonIgnore
    public int getReplicas() {
        return replicas;
    }

    @JsonIgnore
    public String getEntandoAppName() {
        return entandoAppName;
    }

    @JsonIgnore
    public Optional<DbmsImageVendor> getDbms() {
        return ofNullable(DbmsImageVendor.forValue(dbms));
    }

    @JsonIgnore
    public List<ExpectedRole> getRoles() {
        return roles;
    }

    @JsonIgnore
    public List<Permission> getPermissions() {
        return permissions;
    }

    @JsonIgnore
    public String getIngressPath() {
        return ingressPath;
    }

    @JsonIgnore
    public String getHealthCheckPath() {
        return healthCheckPath;
    }

    @JsonIgnore
    public String getKeycloakServerNamespace() {
        return keycloakServerNamespace;
    }

    @JsonIgnore
    public String getKeycloakServerName() {
        return keycloakServerName;
    }

    @JsonIgnore
    public List<String> getConnectionConfigNames() {
        return this.connectionConfigNames;
    }

    public static class EntandoPluginSpecBuilder {

        private final List<String> connectionConfigNames = new ArrayList<>();
        private final List<ExpectedRole> roles = new ArrayList<>();
        private final List<Permission> permissions = new ArrayList<>();
        private final Map<String, Object> parameters = new ConcurrentHashMap<>();
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

        public EntandoPluginSpecBuilder withDbms(DbmsImageVendor dbms) {
            this.dbms = dbms;
            return this;
        }

        public EntandoPluginSpecBuilder withIngressPath(String ingressPath) {
            this.ingressPath = ingressPath;
            return this;
        }

        public EntandoPluginSpecBuilder withConnectionConfigNamed(String name) {
            connectionConfigNames.add(name);
            return this;
        }

        public EntandoPluginSpecBuilder withImage(String image) {
            this.image = image;
            return this;
        }

        public EntandoPluginSpecBuilder withSecurityLevel(PluginSecurityLevel level) {
            this.securityLevel = level;
            return this;
        }

        public EntandoPluginSpecBuilder withKeycloakServer(String namespace, String name) {
            this.keycloakServerName = name;
            this.keycloakServerNamespace = namespace;
            return this;
        }

        public EntandoPluginSpecBuilder withEntandoApp(String namespace, String name) {
            this.entandoAppName = name;
            this.entandoAppNamespace = namespace;
            return this;
        }

        public EntandoPluginSpecBuilder withReplicas(Integer replicas) {
            this.replicas = replicas;
            return this;
        }

        public EntandoPluginSpecBuilder withRole(String code, String name) {
            roles.add(new ExpectedRole(code, name));
            return this;
        }

        public EntandoPluginSpecBuilder withPermission(String clientId, String role) {
            permissions.add(new Permission(clientId, role));
            return this;
        }

        public EntandoPluginSpec build() {
            return new EntandoPluginSpec(entandoAppNamespace, entandoAppName, image, dbms, replicas, ingressPath,
                    keycloakServerNamespace, keycloakServerName, healthCheckPath, securityLevel, roles, permissions, parameters,
                    connectionConfigNames);
        }

        public EntandoPluginSpecBuilder withHealthCheckPath(String healthCheckPath) {
            this.healthCheckPath = healthCheckPath;
            return this;
        }
    }
}
