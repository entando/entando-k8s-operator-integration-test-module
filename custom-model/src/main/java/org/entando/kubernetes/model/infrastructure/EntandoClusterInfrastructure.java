package org.entando.kubernetes.model.infrastructure;

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
import org.entando.kubernetes.model.RequiresKeycloak;

@JsonSerialize
@JsonDeserialize
@JsonInclude(Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = Visibility.ANY, isGetterVisibility = Visibility.NONE, getterVisibility = Visibility.NONE,
        setterVisibility = Visibility.NONE)
public class EntandoClusterInfrastructure extends CustomResource implements HasIngress, EntandoCustomResource, RequiresKeycloak {

    public static final String CRD_NAME = "entandoclusterinfrastructures.entando.org";

    private EntandoClusterInfrastructureSpec spec;
    private EntandoCustomResourceStatus entandoStatus;

    public EntandoClusterInfrastructure() {
        super();
        setApiVersion("entando.org/v1alpha1");
    }

    public EntandoClusterInfrastructure(EntandoClusterInfrastructureSpec spec) {
        this();
        this.spec = spec;
    }

    public EntandoClusterInfrastructure(EntandoClusterInfrastructureSpec spec, ObjectMeta metadata, EntandoCustomResourceStatus status) {
        this(metadata, spec);
        this.entandoStatus = status;
    }

    public EntandoClusterInfrastructure(ObjectMeta metadata, EntandoClusterInfrastructureSpec spec) {
        this(spec);
        super.setMetadata(metadata);
    }

    public EntandoClusterInfrastructureSpec getSpec() {
        return spec;
    }

    public void setSpec(EntandoClusterInfrastructureSpec spec) {
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

    @Override
    public Optional<String> getKeycloakSecretToUse() {
        return spec.getKeycloakSecretToUse();
    }
}
