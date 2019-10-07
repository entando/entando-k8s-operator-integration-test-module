package org.entando.kubernetes.model.infrastructure;

import io.fabric8.kubernetes.api.builder.Builder;

public class EntandoClusterInfrastructureBuilder extends EntandoClusterInfrastructureFluent<EntandoClusterInfrastructureBuilder>
        implements Builder<EntandoClusterInfrastructure> {

    @Override
    public EntandoClusterInfrastructure build() {
        return new EntandoClusterInfrastructure(super.metadata.build(), super.spec.build());
    }
}
