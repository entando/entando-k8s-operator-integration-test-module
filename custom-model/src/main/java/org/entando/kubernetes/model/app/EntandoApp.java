package org.entando.kubernetes.model.app;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.fabric8.kubernetes.client.CustomResource;
import java.util.Optional;
import org.entando.kubernetes.model.EntandoCustomResourceStatus;
import org.entando.kubernetes.model.HasIngress;
import org.entando.kubernetes.model.RequiresKeycloak;

@JsonSerialize
@JsonDeserialize
public class EntandoApp extends CustomResource implements HasIngress, RequiresKeycloak {

    @JsonProperty
    private EntandoAppSpec spec;
    @JsonProperty
    private EntandoCustomResourceStatus entandoStatus;

    public EntandoApp() {
        super();
    }

    public EntandoApp(EntandoAppSpec spec) {
        super();
        this.spec = spec;
    }

    @JsonIgnore
    public EntandoAppSpec getSpec() {
        return spec;
    }

    public void setSpec(EntandoAppSpec spec) {
        this.spec = spec;
    }

    @JsonIgnore
    @Override
    public EntandoCustomResourceStatus getStatus() {
        return this.entandoStatus == null ? this.entandoStatus = new EntandoCustomResourceStatus() : entandoStatus;
    }

    @Override
    public void setStatus(EntandoCustomResourceStatus status) {
        this.entandoStatus = status;
    }

    @Override
    @JsonIgnore
    public Optional<String> getIngressHostName() {
        return getSpec().getIngressHostName();
    }

    @Override
    @JsonIgnore
    public Optional<Boolean> getTlsEnabled() {
        return getSpec().getTlsEnabled();
    }

    @Override
    @JsonIgnore
    public String getKeycloakServerNamespace() {
        return getSpec().getKeycloakServerNamespace();
    }

    @Override
    @JsonIgnore

    public String getKeycloakServerName() {
        return getSpec().getKeycloakServerName();
    }
}
