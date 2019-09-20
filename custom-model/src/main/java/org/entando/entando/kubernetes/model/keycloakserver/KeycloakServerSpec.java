package org.entando.entando.kubernetes.model.keycloakserver;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Optional;
import org.entando.entando.kubernetes.model.DbmsImageVendor;

public class KeycloakServerSpec {

    @JsonProperty
    private String imageName;
    @JsonProperty
    private String dbms;
    @JsonProperty
    private String ingressHostName;
    @JsonProperty
    private String entandoImageVersion;
    @JsonProperty
    private Boolean tlsEnabled;
    @JsonProperty
    private int replicas = 1;

    public KeycloakServerSpec() {
        super();
    }

    public KeycloakServerSpec(String imageName, DbmsImageVendor dbms, String ingressHostName,
            String entandoImageVersion, boolean tnsEnabled) {
        this.imageName = imageName;
        this.dbms = dbms.toValue();
        this.ingressHostName = ingressHostName;
        this.entandoImageVersion = entandoImageVersion;
        this.tlsEnabled = tnsEnabled;
    }

    @JsonIgnore
    public Optional<String> getImageName() {
        return Optional.ofNullable(imageName);
    }

    @JsonIgnore
    public Optional<DbmsImageVendor> getDbms() {
        return Optional.ofNullable(DbmsImageVendor.forValue(dbms));
    }

    @JsonIgnore
    public Optional<String> getIngressHostName() {
        return Optional.ofNullable(ingressHostName);
    }

    @JsonIgnore
    public Optional<Boolean> getTlsEnabled() {
        return Optional.ofNullable(tlsEnabled);
    }

    @JsonIgnore
    public Optional<String> getEntandoImageVersion() {
        return Optional.ofNullable(entandoImageVersion);
    }

    @JsonIgnore
    public int getReplicas() {
        return replicas;
    }

    public void setReplicas(int replicas) {
        this.replicas = replicas;
    }
}
