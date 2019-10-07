package org.entando.kubernetes.model.link;

public class EntandoAppPluginLinkBuilder extends EntandoAppPluginLinkFluent<EntandoAppPluginLinkBuilder> {

    public EntandoAppPluginLink build() {
        return new EntandoAppPluginLink(super.spec.build(), super.metadata.build());
    }

}
