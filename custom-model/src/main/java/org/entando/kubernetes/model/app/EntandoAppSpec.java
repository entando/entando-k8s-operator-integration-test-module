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

    public EntandoAppSpec(JeeServer jeeServer, DbmsImageVendor dbms, String ingressHostName,
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

    //TODO move to top level
    public static class EntandoAppSpecBuilder<N extends EntandoAppSpecBuilder> {

        private JeeServer jeeServer;
        private DbmsImageVendor dbms;
        private String ingressHostName;
        private Boolean tlsEnabled;
        private String entandoImageVersion;
        private Integer replicas;
        private String keycloakServerNamespace;
        private String keycloakServerName;

        public EntandoAppSpecBuilder() {
            //Needed for JSON Deserialization

        }

        @Deprecated
        @SuppressWarnings("PMD")
        //Because it is deprecated
        public EntandoAppSpecBuilder(JeeServer jeeServer, DbmsImageVendor dbms) {
            withJeeServer(jeeServer);
            withDbms(dbms);
        }

        public EntandoAppSpecBuilder(EntandoAppSpec spec) {
            this.keycloakServerNamespace = spec.getKeycloakServerNamespace();
            this.keycloakServerName = spec.getKeycloakServerName();
            this.replicas = spec.getReplicas().orElse(null);
            this.entandoImageVersion = spec.getEntandoImageVersion().orElse(null);
            this.tlsEnabled = spec.getTlsEnabled().orElse(null);
            this.dbms = spec.getDbms().orElse(null);
            this.jeeServer = spec.getJeeServer().orElse(null);
            this.ingressHostName = spec.getIngressHostName().orElse(null);
        }

        public N withJeeServer(JeeServer jeeServer) {
            this.jeeServer = jeeServer;
            return (N) this;
        }

        public N withDbms(DbmsImageVendor dbms) {
            this.dbms = dbms;
            return (N) this;
        }

        public N withIngressHostName(String ingressHostName) {
            this.ingressHostName = ingressHostName;
            return (N) this;
        }

        public N withTlsEnabled(Boolean tlsEnabled) {
            this.tlsEnabled = tlsEnabled;
            return (N) this;
        }

        public N withEntandoImageVersion(String entandoImageVersion) {
            this.entandoImageVersion = entandoImageVersion;
            return (N) this;
        }

        public N withKeycloakServer(String namespace, String name) {
            this.keycloakServerName = name;
            this.keycloakServerNamespace = namespace;
            return (N) this;
        }

        public N withReplicas(Integer replicas) {
            this.replicas = replicas;
            return (N) this;
        }

        public EntandoAppSpec build() {
            return new EntandoAppSpec(this.jeeServer, this.dbms, this.ingressHostName, this.replicas,
                    this.entandoImageVersion, this.tlsEnabled, keycloakServerNamespace, keycloakServerName);
        }
    }
}
