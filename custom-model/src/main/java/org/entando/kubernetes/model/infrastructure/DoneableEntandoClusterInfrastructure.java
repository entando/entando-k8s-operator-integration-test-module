package org.entando.kubernetes.model.infrastructure;

import io.fabric8.kubernetes.api.builder.Function;
import java.util.Optional;
import org.entando.kubernetes.model.AbstractServerStatus;
import org.entando.kubernetes.model.DoneableEntandoCustomResource;
import org.entando.kubernetes.model.EntandoCustomResourceStatus;
import org.entando.kubernetes.model.EntandoDeploymentPhase;

public class DoneableEntandoClusterInfrastructure extends
        EntandoClusterInfrastructureFluent<DoneableEntandoClusterInfrastructure> implements
        DoneableEntandoCustomResource<DoneableEntandoClusterInfrastructure, EntandoClusterInfrastructure> {

    private final Function<EntandoClusterInfrastructure, EntandoClusterInfrastructure> function;
    private final EntandoCustomResourceStatus status;

    public DoneableEntandoClusterInfrastructure(EntandoClusterInfrastructure resource,
            Function<EntandoClusterInfrastructure, EntandoClusterInfrastructure> function) {
        super(resource.getSpec(), resource.getMetadata());
        this.status = Optional.ofNullable(resource.getStatus()).orElse(new EntandoCustomResourceStatus());
        this.function = function;
    }

    @Override
    public DoneableEntandoClusterInfrastructure withStatus(AbstractServerStatus status) {
        this.status.putServerStatus(status);
        return this;
    }

    @Override
    public DoneableEntandoClusterInfrastructure withPhase(EntandoDeploymentPhase phase) {
        status.setEntandoDeploymentPhase(phase);
        return this;
    }

    @Override
    public EntandoClusterInfrastructure done() {
        EntandoClusterInfrastructure entandoClusterInfrastructure = new EntandoClusterInfrastructure(spec.build(), metadata.build(),
                status);
        return function.apply(entandoClusterInfrastructure);
    }
}
