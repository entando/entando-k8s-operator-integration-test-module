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

    /**
     * Only for use from the builder
     */

    @SuppressWarnings("PMD.ExcessiveParameterList")
    //Because the typical builder pattern requires a full constructor
    EntandoPluginSpec(String entandoAppNamespace, String entandoAppName, String image, DbmsImageVendor dbms,
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

}
