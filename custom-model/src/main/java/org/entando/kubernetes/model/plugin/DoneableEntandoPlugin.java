package org.entando.kubernetes.model.plugin;

import io.fabric8.kubernetes.api.builder.Function;
import java.util.Optional;
import org.entando.kubernetes.model.AbstractServerStatus;
import org.entando.kubernetes.model.DbServerStatus;
import org.entando.kubernetes.model.DoneableEntandoCustomResource;
import org.entando.kubernetes.model.EntandoCustomResourceStatus;
import org.entando.kubernetes.model.EntandoDeploymentPhase;
import org.entando.kubernetes.model.WebServerStatus;

public class DoneableEntandoPlugin extends EntandoPluginFluent<DoneableEntandoPlugin> implements
        DoneableEntandoCustomResource<DoneableEntandoPlugin, EntandoPlugin> {

    private final EntandoCustomResourceStatus entandoStatus;
    private final Function<EntandoPlugin, EntandoPlugin> function;

    public DoneableEntandoPlugin(EntandoPlugin resource, Function<EntandoPlugin, EntandoPlugin> function) {
        super(resource.getSpec(), resource.getMetadata());
        this.function = function;
        this.entandoStatus = Optional.ofNullable(resource.getStatus()).orElse(new EntandoCustomResourceStatus());
    }

    public DoneableEntandoPlugin addNewConnectionConfigName(String name) {
        spec.addNewConnectionConfigName(name);
        return this;
    }

    @Override
    public DoneableEntandoPlugin withStatus(AbstractServerStatus status) {
        if (status instanceof DbServerStatus) {
            entandoStatus.addDbServerStatus((DbServerStatus) status);
        } else {
            entandoStatus.addWebServerStatus((WebServerStatus) status);
        }
        return this;
    }

    @Override
    public DoneableEntandoPlugin withPhase(EntandoDeploymentPhase phase) {
        entandoStatus.setEntandoDeploymentPhase(phase);
        return this;
    }

    @Override
    public EntandoPlugin done() {
        return function.apply(new EntandoPlugin(spec.build(), metadata.build(), entandoStatus));
    }
}
