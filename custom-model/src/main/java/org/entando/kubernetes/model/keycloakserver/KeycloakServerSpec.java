package org.entando.kubernetes.model.keycloakserver;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.Optional;
import org.entando.kubernetes.model.DbmsImageVendor;

@JsonInclude(Include.NON_NULL)
@JsonSerialize
@JsonDeserialize
@JsonAutoDetect(fieldVisibility = Visibility.ANY, isGetterVisibility = Visibility.NONE, getterVisibility = Visibility.NONE,
        setterVisibility = Visibility.NONE)
public class KeycloakServerSpec {

    private String imageName;
    //Seems K8S doesn't support enums: https://github.com/kubernetes/kubernetes/issues/62325
    private DbmsImageVendor dbms;
    private String ingressHostName;
    private String entandoImageVersion;
    private Boolean tlsEnabled;
    private Integer replicas = 1;

    public KeycloakServerSpec() {
        super();
    }

    public KeycloakServerSpec(String imageName, DbmsImageVendor dbms, String ingressHostName,
            String entandoImageVersion, Boolean tnsEnabled) {
        this.imageName = imageName;
        this.dbms = dbms;
        this.ingressHostName = ingressHostName;
        this.entandoImageVersion = entandoImageVersion;
        this.tlsEnabled = tnsEnabled;
    }

    public KeycloakServerSpec(String imageName, DbmsImageVendor dbms, String ingressHostName, String entandoImageVersion,
            Boolean tlsEnabled, int replicas) {
        this(imageName, dbms, ingressHostName, entandoImageVersion, tlsEnabled);
        this.replicas = replicas;
    }

    public Optional<String> getImageName() {
        return Optional.ofNullable(imageName);
    }

    public Optional<DbmsImageVendor> getDbms() {
        return Optional.ofNullable(dbms);
    }

    public Optional<String> getIngressHostName() {
        return Optional.ofNullable(ingressHostName);
    }

    public Optional<Boolean> getTlsEnabled() {
        return Optional.ofNullable(tlsEnabled);
    }

    public Optional<String> getEntandoImageVersion() {
        return Optional.ofNullable(entandoImageVersion);
    }

    public Integer getReplicas() {
        return replicas;
    }

    public void setReplicas(Integer replicas) {
        this.replicas = replicas;
    }
}
