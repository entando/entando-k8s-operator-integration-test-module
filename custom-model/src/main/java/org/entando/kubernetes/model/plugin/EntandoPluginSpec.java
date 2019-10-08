package org.entando.kubernetes.model.plugin;

import static java.util.Optional.ofNullable;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.entando.kubernetes.model.DbmsImageVendor;
import org.entando.kubernetes.model.RequiresKeycloak;

@JsonSerialize
@JsonDeserialize
@JsonInclude(Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = Visibility.ANY, isGetterVisibility = Visibility.NONE, getterVisibility = Visibility.NONE,
        setterVisibility = Visibility.NONE)
public class EntandoPluginSpec implements RequiresKeycloak {

    private String image;
    private Integer replicas = 1;
    private DbmsImageVendor dbms;
    private PluginSecurityLevel securityLevel;
    private List<String> connectionConfigNames = new ArrayList<>();
    private String tlsSecretName;
    private String ingressHostName;
    private List<ExpectedRole> roles = new ArrayList<>();
    private List<Permission> permissions = new ArrayList<>();
    private Map<String, Object> parameters = new ConcurrentHashMap<>();
    private String ingressPath;
    private String keycloakSecretToUse;
    private String healthCheckPath;
    private String clusterInfrastructureToUse;

    public EntandoPluginSpec() {
        //Needed for JSON Deserialization
    }

    /**
     * Only for use from the builder.
     */

    @SuppressWarnings("PMD.ExcessiveParameterList")
    //Because the typical builder pattern requires a full constructor
    EntandoPluginSpec(String image, DbmsImageVendor dbms,
            Integer replicas, String ingressPath, String keycloakSecretToUse,
            String healthCheckPath, PluginSecurityLevel securityLevel, String tlsSecretName,
            String ingressHostName, List<ExpectedRole> roles, List<Permission> permissions,
            Map<String, Object> parameters, List<String> connectionConfigNames, String clusterInfrastructureToUse) {
        this();
        this.image = image;
        this.dbms = dbms;
        this.replicas = replicas;
        this.ingressPath = ingressPath;
        this.keycloakSecretToUse = keycloakSecretToUse;
        this.healthCheckPath = healthCheckPath;
        this.tlsSecretName = tlsSecretName;
        this.ingressHostName = ingressHostName;
        this.roles = roles;
        this.permissions = permissions;
        this.parameters = parameters;
        this.connectionConfigNames = connectionConfigNames;
        this.securityLevel = securityLevel;
        this.clusterInfrastructureToUse = clusterInfrastructureToUse;

    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public Optional<PluginSecurityLevel> getSecurityLevel() {
        return ofNullable(securityLevel);
    }

    public String getImage() {
        return image;
    }

    public Integer getReplicas() {
        return replicas;
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

    public Optional<String> getTlsSecretName() {
        return ofNullable(tlsSecretName);
    }

    public Optional<String> getIngressHostName() {
        return ofNullable(ingressHostName);
    }
}
