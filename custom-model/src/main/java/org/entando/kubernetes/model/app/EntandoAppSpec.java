package org.entando.kubernetes.model.app;

import static java.util.Optional.ofNullable;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.Optional;
import org.entando.kubernetes.model.DbmsImageVendor;
import org.entando.kubernetes.model.JeeServer;

@JsonSerialize
@JsonDeserialize
@JsonInclude(Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = Visibility.ANY, isGetterVisibility = Visibility.NONE, getterVisibility = Visibility.NONE,
        setterVisibility = Visibility.NONE)
public class EntandoAppSpec {

    private JeeServer jeeServer;
    private DbmsImageVendor dbms;
    private String ingressHostName;
    private Boolean tlsEnabled;
    private String entandoImageVersion;
    private Integer replicas; // TODO distinguish between dbReplicas and jeeServer replicas
    private String keycloakServerNamespace;
    private String keycloakServerName;

    public EntandoAppSpec() {
        super();
    }

    /**
     * Only for use from the builder.
     */
    EntandoAppSpec(JeeServer jeeServer, DbmsImageVendor dbms, String ingressHostName,
            int replicas, String entandoImageVersion, Boolean tlsEnabled, String keycloakServerNamespace,
            String keycloakServerName) {
        this.jeeServer = jeeServer;
        this.dbms = dbms;
        this.ingressHostName = ingressHostName;
        this.replicas = replicas;
        this.entandoImageVersion = entandoImageVersion;
        this.tlsEnabled = tlsEnabled;
        this.keycloakServerNamespace = keycloakServerNamespace;
        this.keycloakServerName = keycloakServerName;
    }

    public String getKeycloakServerNamespace() {
        return keycloakServerNamespace;
    }

    public String getKeycloakServerName() {
        return keycloakServerName;
    }

    public Optional<JeeServer> getJeeServer() {
        return ofNullable(jeeServer);
    }

    public Optional<String> getEntandoImageVersion() {
        return ofNullable(entandoImageVersion);
    }

    public Optional<DbmsImageVendor> getDbms() {
        return ofNullable(dbms);
    }

    public Optional<String> getIngressHostName() {
        return ofNullable(ingressHostName);
    }

    public Optional<Boolean> getTlsEnabled() {
        return ofNullable(tlsEnabled);
    }

    public Optional<Integer> getReplicas() {
        return ofNullable(replicas);
    }

}
