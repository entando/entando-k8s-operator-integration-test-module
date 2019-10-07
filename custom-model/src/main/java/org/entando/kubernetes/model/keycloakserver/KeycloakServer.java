package org.entando.kubernetes.model.keycloakserver;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.client.CustomResource;
import java.util.Optional;
import org.entando.kubernetes.model.EntandoCustomResource;
import org.entando.kubernetes.model.EntandoCustomResourceStatus;
import org.entando.kubernetes.model.HasIngress;

@JsonSerialize
@JsonDeserialize
@JsonInclude(Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = Visibility.ANY, isGetterVisibility = Visibility.NONE, getterVisibility = Visibility.NONE,
        setterVisibility = Visibility.NONE)
public class KeycloakServer extends CustomResource implements HasIngress, EntandoCustomResource {

    public static final String CRD_NAME = "entandokeycloakservers.entando.org";

    private KeycloakServerSpec spec;
    private EntandoCustomResourceStatus entandoStatus;

    public KeycloakServer() {
        super("EntandoKeycloakServer");
        setApiVersion("entando.org/v1alpha1");
    }

    public KeycloakServer(KeycloakServerSpec spec) {
        this();
        this.spec = spec;
    }

    public KeycloakServer(ObjectMeta metadata, KeycloakServerSpec spec, EntandoCustomResourceStatus status) {
        this(metadata, spec);
        this.entandoStatus = status;
    }

    public KeycloakServer(ObjectMeta metadata, KeycloakServerSpec spec) {
        this(spec);
        super.setMetadata(metadata);
    }

    public KeycloakServerSpec getSpec() {
        return spec;
    }

    public void setSpec(KeycloakServerSpec spec) {
        this.spec = spec;
    }

    @Override
    public EntandoCustomResourceStatus getStatus() {
        return this.entandoStatus == null ? this.entandoStatus = new EntandoCustomResourceStatus() : this.entandoStatus;
    }

    @Override
    public void setStatus(EntandoCustomResourceStatus status) {
        this.entandoStatus = status;
    }

    @Override
    public Optional<String> getIngressHostName() {
        return getSpec().getIngressHostName();
    }

    @Override
    public Optional<String> getTlsSecretName() {
        return getSpec().getTlsSecretName();
    }
}
