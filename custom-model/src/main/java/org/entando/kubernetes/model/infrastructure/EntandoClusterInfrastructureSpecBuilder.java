package org.entando.kubernetes.model.infrastructure;

import org.entando.kubernetes.model.DbmsImageVendor;

public class EntandoClusterInfrastructureSpecBuilder<N extends EntandoClusterInfrastructureSpecBuilder> {

    private DbmsImageVendor dbms;
    private String ingressHostName;
    private String entandoImageVersion;
    private String tlsSecretName;
    private int replicas = 1;
    private String keycloakSecretToUse;
    private boolean isDefault;

    public EntandoClusterInfrastructureSpecBuilder(EntandoClusterInfrastructureSpec spec) {
        this.dbms = spec.getDbms().orElse(null);
        this.ingressHostName = spec.getIngressHostName().orElse(null);
        this.entandoImageVersion = spec.getEntandoImageVersion().orElse(null);
        this.tlsSecretName = spec.getTlsSecretName().orElse(null);
        this.replicas = spec.getReplicas();
        this.keycloakSecretToUse = spec.getKeycloakSecretToUse().orElse(null);
        this.isDefault = spec.isDefault();
    }

    public EntandoClusterInfrastructureSpecBuilder() {
        //required default constructor
    }

    public N withKeycloakSecretToUse(String keycloakSecretToUse) {
        this.keycloakSecretToUse = keycloakSecretToUse;
        return (N) this;
    }

    public N withDefault(boolean isDefault) {
        this.isDefault = isDefault;
        return (N) this;
    }

    public N withDbms(DbmsImageVendor dbms) {
        this.dbms = dbms;
        return (N) this;
    }

    public N withEntandoImageVersion(String entandoImageVersion) {
        this.entandoImageVersion = entandoImageVersion;
        return (N) this;
    }

    public N withTlsSecretName(String tlsSecretName) {
        this.tlsSecretName = tlsSecretName;
        return (N) this;
    }

    public N withReplicas(int replicas) {
        this.replicas = replicas;
        return (N) this;
    }

    public N withIngressHostName(String ingressHostName) {
        this.ingressHostName = ingressHostName;
        return (N) this;
    }

    public EntandoClusterInfrastructureSpec build() {
        return new EntandoClusterInfrastructureSpec(dbms, ingressHostName, entandoImageVersion, tlsSecretName, replicas,
                keycloakSecretToUse, isDefault);
    }

}
