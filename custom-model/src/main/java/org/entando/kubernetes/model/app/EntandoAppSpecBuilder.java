package org.entando.kubernetes.model.app;

import org.entando.kubernetes.model.DbmsImageVendor;
import org.entando.kubernetes.model.JeeServer;

public class EntandoAppSpecBuilder<N extends EntandoAppSpecBuilder> {

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
