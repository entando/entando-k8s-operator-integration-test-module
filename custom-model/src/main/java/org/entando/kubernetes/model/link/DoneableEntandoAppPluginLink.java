package org.entando.kubernetes.model.link;

import io.fabric8.kubernetes.api.builder.Function;
import java.util.Optional;
import org.entando.kubernetes.model.AbstractServerStatus;
import org.entando.kubernetes.model.DoneableEntandoCustomResource;
import org.entando.kubernetes.model.EntandoCustomResourceStatus;
import org.entando.kubernetes.model.EntandoDeploymentPhase;

public class DoneableEntandoAppPluginLink extends EntandoAppPluginLinkFluent<DoneableEntandoAppPluginLink> implements
        DoneableEntandoCustomResource<DoneableEntandoAppPluginLink, EntandoAppPluginLink> {

    private final EntandoCustomResourceStatus entandoStatus;
    private final Function<EntandoAppPluginLink, EntandoAppPluginLink> function;

    public DoneableEntandoAppPluginLink(EntandoAppPluginLink resource, Function<EntandoAppPluginLink, EntandoAppPluginLink> function) {
        super(resource.getSpec(), resource.getMetadata());
        this.function = function;
        this.entandoStatus = Optional.ofNullable(resource.getStatus()).orElse(new EntandoCustomResourceStatus());
    }

    @Override
    public DoneableEntandoAppPluginLink withStatus(AbstractServerStatus status) {
        this.entandoStatus.putServerStatus(status);
        return this;
    }

    @Override
    public DoneableEntandoAppPluginLink withPhase(EntandoDeploymentPhase phase) {
        entandoStatus.setEntandoDeploymentPhase(phase);
        return this;
    }

    @Override
    public EntandoAppPluginLink done() {
        return function.apply(new EntandoAppPluginLink(metadata.build(), spec.build(), entandoStatus));
    }

}
