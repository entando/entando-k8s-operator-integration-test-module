package org.entando.kubernetes.model.keycloakserver;

import io.fabric8.kubernetes.api.builder.Builder;

public class KeycloakServerBuilder extends KeycloakServerFluent<KeycloakServerBuilder> implements Builder<KeycloakServer> {

    @Override
    public KeycloakServer build() {
        return new KeycloakServer(super.metadata.build(), super.spec.build());
    }
}
