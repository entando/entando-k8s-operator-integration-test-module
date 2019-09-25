package org.entando.kubernetes.model.plugin;

import io.fabric8.kubernetes.api.builder.Builder;

public class EntandoPluginBuilder extends EntandoPluginFluent<EntandoPluginBuilder> implements Builder<EntandoPlugin> {

    @Override
    public EntandoPlugin build() {
        return new EntandoPlugin(super.metadata.build(), super.spec.build());
    }
}
