package org.entando.kubernetes.model.link;

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

@JsonSerialize
@JsonDeserialize
@JsonInclude(Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = Visibility.ANY, isGetterVisibility = Visibility.NONE, getterVisibility = Visibility.NONE,
        setterVisibility = Visibility.NONE)
public class EntandoAppPluginLink extends CustomResource implements EntandoCustomResource {

    public static final String CRD_NAME = "entandoapppluginlinks.entando.org";
    private EntandoCustomResourceStatus entandoStatus;
    private EntandoAppPluginLinkSpec spec;

    public EntandoAppPluginLink() {
        super();
        setApiVersion("entando.org/v1alpha1");
    }

    public EntandoAppPluginLink(EntandoAppPluginLinkSpec spec) {
        this();
        this.spec = spec;
    }

    public EntandoAppPluginLink(EntandoAppPluginLinkSpec spec, ObjectMeta meta, EntandoCustomResourceStatus entandoStatus) {
        this(spec, meta);
        this.entandoStatus = entandoStatus;
    }

    public EntandoAppPluginLink(EntandoAppPluginLinkSpec spec, ObjectMeta meta) {
        this(spec);
        super.setMetadata(meta);
    }

    public EntandoAppPluginLinkSpec getSpec() {
        return spec;
    }

    @Override
    public EntandoCustomResourceStatus getStatus() {
        return entandoStatus;
    }

    @Override
    public void setStatus(EntandoCustomResourceStatus status) {
        this.entandoStatus = status;
    }
}