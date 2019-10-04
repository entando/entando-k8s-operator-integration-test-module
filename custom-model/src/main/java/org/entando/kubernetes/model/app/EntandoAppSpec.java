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
import org.entando.kubernetes.model.HasIngress;
import org.entando.kubernetes.model.JeeServer;
import org.entando.kubernetes.model.RequiresKeycloak;

@JsonSerialize
@JsonDeserialize
@JsonInclude(Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = Visibility.ANY, isGetterVisibility = Visibility.NONE, getterVisibility = Visibility.NONE,
        setterVisibility = Visibility.NONE)
public class EntandoAppSpec implements RequiresKeycloak, HasIngress {

    private JeeServer standardServerImage;
    private String customServerImage;
    private DbmsImageVendor dbms;
    private String ingressHostName;
    private String tlsSecretName;
    private String entandoImageVersion;
    private Integer replicas; // TODO distinguish between dbReplicas and standardServerImage replicas
    private String keycloakSecretToUse;
    private String clusterInfrastructureToUse;

    public EntandoAppSpec() {
        super();
    }

    /**
     * Only for use from the builder.
     */
    EntandoAppSpec(JeeServer standardServerImage, String customServerImage, DbmsImageVendor dbms, String ingressHostName, int replicas,
            String entandoImageVersion,
            String tlsSecretName, String keycloakSecretToUse, String clusterInfrastructureToUse) {
        this.standardServerImage = standardServerImage;
        this.customServerImage = customServerImage;
        this.dbms = dbms;
        this.ingressHostName = ingressHostName;
        this.replicas = replicas;
        this.entandoImageVersion = entandoImageVersion;
        this.tlsSecretName = tlsSecretName;
        this.keycloakSecretToUse = keycloakSecretToUse;
        this.clusterInfrastructureToUse = clusterInfrastructureToUse;
    }

    @Override
    public Optional<String> getKeycloakSecretToUse() {
        return ofNullable(keycloakSecretToUse);
    }

    public Optional<String> getClusterInfrastructureTouse() {
        return ofNullable(clusterInfrastructureToUse);
    }

    public Optional<JeeServer> getStandardServerImage() {
        return ofNullable(standardServerImage);
    }

    public Optional<String> getCustomServerImage() {
        return ofNullable(customServerImage);
    }

    public Optional<String> getEntandoImageVersion() {
        return ofNullable(entandoImageVersion);
    }

    public Optional<DbmsImageVendor> getDbms() {
        return ofNullable(dbms);
    }

    @Override
    public Optional<String> getIngressHostName() {
        return ofNullable(ingressHostName);
    }

    @Override
    public Optional<String> getTlsSecretName() {
        return ofNullable(tlsSecretName);
    }

    public Optional<Integer> getReplicas() {
        return ofNullable(replicas);
    }

}
