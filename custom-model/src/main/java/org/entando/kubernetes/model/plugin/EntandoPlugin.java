package org.entando.kubernetes.model.plugin;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.client.CustomResource;
import org.entando.kubernetes.model.EntandoCustomResource;
import org.entando.kubernetes.model.EntandoCustomResourceStatus;
import org.entando.kubernetes.model.RequiresKeycloak;

@JsonSerialize
@JsonDeserialize()
public class EntandoPlugin extends CustomResource implements EntandoCustomResource, RequiresKeycloak {

    public static final String CRD_NAME = "entandoplugins.entando.org";
    @JsonProperty
    private EntandoPluginSpec spec;
    @JsonProperty
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

    @JsonIgnore
    public EntandoPluginSpec getSpec() {
        return spec;
    }

    public void setSpec(EntandoPluginSpec spec) {
        this.spec = spec;
    }

    @JsonIgnore
    @Override
    public EntandoCustomResourceStatus getStatus() {
        return entandoStatus == null ? entandoStatus = new EntandoCustomResourceStatus() : entandoStatus;
    }

    @Override
    public void setStatus(EntandoCustomResourceStatus status) {
        this.entandoStatus = status;
    }

    @Override
    @JsonIgnore
    public String getKeycloakServerNamespace() {
        return spec.getKeycloakServerNamespace();
    }

    @Override
    @JsonIgnore
    public String getKeycloakServerName() {
        return spec.getKeycloakServerName();
    }
}
