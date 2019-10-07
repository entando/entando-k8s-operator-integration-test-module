package org.entando.kubernetes.model.plugin;

import io.fabric8.kubernetes.api.builder.Function;
import java.util.Optional;
import org.entando.kubernetes.model.AbstractServerStatus;
import org.entando.kubernetes.model.DoneableEntandoCustomResource;
import org.entando.kubernetes.model.EntandoCustomResourceStatus;
import org.entando.kubernetes.model.EntandoDeploymentPhase;

public class DoneableEntandoPlugin extends EntandoPluginFluent<DoneableEntandoPlugin> implements
        DoneableEntandoCustomResource<DoneableEntandoPlugin, EntandoPlugin> {

    private final EntandoCustomResourceStatus entandoStatus;
    private final Function<EntandoPlugin, EntandoPlugin> function;

    public DoneableEntandoPlugin(EntandoPlugin resource, Function<EntandoPlugin, EntandoPlugin> function) {
        super(resource.getSpec(), resource.getMetadata());
        this.function = function;
        this.entandoStatus = Optional.ofNullable(resource.getStatus()).orElse(new EntandoCustomResourceStatus());
    }

    @Override
    public DoneableEntandoPlugin withStatus(AbstractServerStatus status) {
        this.entandoStatus.putServerStatus(status);
        return this;
    }

    @Override
    public DoneableEntandoPlugin withPhase(EntandoDeploymentPhase phase) {
        entandoStatus.setEntandoDeploymentPhase(phase);
        return this;
    }

    @Override
    public EntandoPlugin done() {
        return function.apply(new EntandoPlugin(metadata.build(), spec.build(), entandoStatus));
    }
}
