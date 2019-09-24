package org.entando.kubernetes.model.plugin;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.client.CustomResource;
import org.entando.kubernetes.model.EntandoCustomResource;
import org.entando.kubernetes.model.EntandoCustomResourceStatus;
import org.entando.kubernetes.model.RequiresKeycloak;

@JsonSerialize
@JsonDeserialize
@JsonInclude(Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = Visibility.ANY, isGetterVisibility = Visibility.NONE, getterVisibility = Visibility.NONE,
        setterVisibility = Visibility.NONE)
public class EntandoPlugin extends CustomResource implements EntandoCustomResource, RequiresKeycloak {

    public static final String CRD_NAME = "entandoplugins.entando.org";

    private EntandoPluginSpec spec;
    private EntandoCustomResourceStatus entandoStatus;

    public EntandoPlugin() {
        super();
        setApiVersion("entando.org/v1alpha1");
    }

    public EntandoPlugin(EntandoPluginSpec spec) {
        this();
        this.spec = spec;
    }

    public EntandoPlugin(ObjectMeta metadata, EntandoPluginSpec spec) {
        this(spec);
        super.setMetadata(metadata);
    }

    public EntandoPlugin(EntandoPluginSpec spec, ObjectMeta metaData, EntandoCustomResourceStatus status) {
        this(metaData, spec);
        this.entandoStatus = status;
    }

    public EntandoPluginSpec getSpec() {
        return spec;
    }

    public void setSpec(EntandoPluginSpec spec) {
        this.spec = spec;
    }

    @Override
    public EntandoCustomResourceStatus getStatus() {
        return entandoStatus == null ? entandoStatus = new EntandoCustomResourceStatus() : entandoStatus;
    }

    @Override
    public void setStatus(EntandoCustomResourceStatus status) {
        this.entandoStatus = status;
    }

    @Override
    public String getKeycloakServerNamespace() {
        return spec.getKeycloakServerNamespace();
    }

    @Override
    public String getKeycloakServerName() {
        return spec.getKeycloakServerName();
    }
}
