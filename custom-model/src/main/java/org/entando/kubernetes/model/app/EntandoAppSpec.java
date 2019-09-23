package org.entando.kubernetes.model.app;

import static java.util.Optional.ofNullable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Optional;
import org.entando.kubernetes.model.DbmsImageVendor;
import org.entando.kubernetes.model.JeeServer;

public class EntandoAppSpec {

    @JsonProperty
    private String jeeServer;
    @JsonProperty
    private String dbms;
    @JsonProperty
    private String ingressHostName;
    @JsonProperty
    private Boolean tlsEnabled;
    @JsonProperty
    private String entandoImageVersion;
    @JsonProperty
    private Integer replicas; // TODO distinguish between dbReplicas and jeeServer replicas
    @JsonProperty
    private String keycloakServerNamespace;
    @JsonProperty
    private String keycloakServerName;

    public EntandoAppSpec() {
        super();
    }

    public EntandoAppSpec(JeeServer jeeServer, DbmsImageVendor dbms, String ingressHostName,
            int replicas, String entandoImageVersion, Boolean tlsEnabled, String keycloakServerNamespace,
            String keycloakServerName) {
        this.jeeServer = ofNullable(jeeServer).map(JeeServer::toValue).orElse(null);
        this.dbms = ofNullable(dbms).map(DbmsImageVendor::toValue).orElse(null);
        this.ingressHostName = ingressHostName;
        this.replicas = replicas;
        this.entandoImageVersion = entandoImageVersion;
        this.tlsEnabled = tlsEnabled;
        this.keycloakServerNamespace = keycloakServerNamespace;
        this.keycloakServerName = keycloakServerName;
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
    public Optional<JeeServer> getJeeServer() {
        return ofNullable(JeeServer.forValue(jeeServer));
    }

    @JsonIgnore
    public Optional<String> getEntandoImageVersion() {
        return ofNullable(entandoImageVersion);
    }

    @JsonIgnore
    public Optional<DbmsImageVendor> getDbms() {
        return ofNullable(DbmsImageVendor.forValue(dbms));
    }

    @JsonIgnore
    public Optional<String> getIngressHostName() {
        return ofNullable(ingressHostName);
    }

    @JsonIgnore
    public Optional<Boolean> getTlsEnabled() {
        return ofNullable(tlsEnabled);
    }

    @JsonIgnore
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
