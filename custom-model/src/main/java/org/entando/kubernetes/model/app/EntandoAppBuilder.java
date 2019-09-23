package org.entando.kubernetes.model.app;

import io.fabric8.kubernetes.api.builder.Builder;

public class EntandoAppBuilder extends EntandoAppFluent<EntandoAppBuilder> implements Builder<EntandoApp> {

    @Override
    public EntandoApp build() {
        return new EntandoApp(super.metadata.build(), super.spec.build());
    }
}
