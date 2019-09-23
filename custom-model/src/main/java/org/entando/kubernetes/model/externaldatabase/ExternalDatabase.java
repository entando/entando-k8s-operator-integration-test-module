package org.entando.kubernetes.model.externaldatabase;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.client.CustomResource;
import org.entando.kubernetes.model.EntandoCustomResource;
import org.entando.kubernetes.model.EntandoCustomResourceStatus;

@JsonSerialize
@JsonDeserialize
public class ExternalDatabase extends CustomResource implements EntandoCustomResource {

    public static final String CRD_NAME = "externaldatabases.entando.org";
    @JsonProperty
    private ExternalDatabaseSpec spec;
    @JsonProperty
    private EntandoCustomResourceStatus entandoStatus;

    public ExternalDatabase() {
        super();
        setApiVersion("entando.org/v1alpha1");
    }

    public ExternalDatabase(ExternalDatabaseSpec spec) {
        this();
        this.spec = spec;
    }

    public ExternalDatabase(ObjectMeta metadata, ExternalDatabaseSpec spec) {
        this(spec);
        super.setMetadata(metadata);
    }

    public ExternalDatabase(ObjectMeta metadata, ExternalDatabaseSpec spec, EntandoCustomResourceStatus status) {
        this(metadata, spec);
        entandoStatus = status;
    }

    @JsonIgnore
    public ExternalDatabaseSpec getSpec() {
        return spec;
    }

    public void setSpec(ExternalDatabaseSpec spec) {
        this.spec = spec;
    }

    @JsonIgnore
    @Override
    public EntandoCustomResourceStatus getStatus() {
        return this.entandoStatus == null ? this.entandoStatus = new EntandoCustomResourceStatus() : this.entandoStatus;
    }

    @Override
    public void setStatus(EntandoCustomResourceStatus status) {
        this.entandoStatus = status;
    }

}
