package org.entando.kubernetes.model.app;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.client.CustomResource;
import java.util.Optional;
import org.entando.kubernetes.model.EntandoCustomResourceStatus;
import org.entando.kubernetes.model.HasIngress;
import org.entando.kubernetes.model.RequiresKeycloak;

@JsonSerialize
@JsonDeserialize
@JsonInclude(Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = Visibility.ANY, isGetterVisibility = Visibility.NONE, getterVisibility = Visibility.NONE,
        setterVisibility = Visibility.NONE)
public class EntandoApp extends CustomResource implements HasIngress, RequiresKeycloak {

    public static final String CRD_NAME = "entandoapps.entando.org";

    private EntandoAppSpec spec;
    private EntandoCustomResourceStatus entandoStatus;

    public EntandoApp() {
        super();
    }

    public EntandoApp(EntandoAppSpec spec) {
        super();
        this.spec = spec;
    }

    public EntandoApp(ObjectMeta metadata, EntandoAppSpec spec) {
        this(spec);
        super.setMetadata(metadata);
    }

    public EntandoApp(ObjectMeta metadata, EntandoAppSpec spec, EntandoCustomResourceStatus status) {
        this(metadata, spec);
        this.entandoStatus = status;
    }

    public EntandoAppSpec getSpec() {
        return spec;
    }

    public void setSpec(EntandoAppSpec spec) {
        this.spec = spec;
    }

    @Override
    public EntandoCustomResourceStatus getStatus() {
        return this.entandoStatus == null ? this.entandoStatus = new EntandoCustomResourceStatus() : entandoStatus;
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
    public Optional<Boolean> getTlsEnabled() {
        return getSpec().getTlsEnabled();
    }

    @Override
    public String getKeycloakServerNamespace() {
        return getSpec().getKeycloakServerNamespace();
    }

    @Override
    public String getKeycloakServerName() {
        return getSpec().getKeycloakServerName();
    }
}
