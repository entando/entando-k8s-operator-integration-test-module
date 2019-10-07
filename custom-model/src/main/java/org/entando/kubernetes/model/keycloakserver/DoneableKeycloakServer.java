package org.entando.kubernetes.model.keycloakserver;

import io.fabric8.kubernetes.api.builder.Function;
import java.util.Optional;
import org.entando.kubernetes.model.AbstractServerStatus;
import org.entando.kubernetes.model.DoneableEntandoCustomResource;
import org.entando.kubernetes.model.EntandoCustomResourceStatus;
import org.entando.kubernetes.model.EntandoDeploymentPhase;

public class DoneableKeycloakServer extends KeycloakServerFluent<DoneableKeycloakServer> implements
        DoneableEntandoCustomResource<DoneableKeycloakServer, KeycloakServer> {

    private final Function<KeycloakServer, KeycloakServer> function;
    private final EntandoCustomResourceStatus status;

    public DoneableKeycloakServer(KeycloakServer resource, Function<KeycloakServer, KeycloakServer> function) {
        super(resource.getSpec(), resource.getMetadata());
        this.status = Optional.ofNullable(resource.getStatus()).orElse(new EntandoCustomResourceStatus());
        this.function = function;
    }

    @Override
    public DoneableKeycloakServer withStatus(AbstractServerStatus status) {
        this.status.putServerStatus(status);
        return this;
    }

    @Override
    public DoneableKeycloakServer withPhase(EntandoDeploymentPhase phase) {
        status.setEntandoDeploymentPhase(phase);
        return this;
    }

    @Override
    public KeycloakServer done() {
        KeycloakServer keycloakServer = new KeycloakServer(metadata.build(), spec.build(), status);
        return function.apply(keycloakServer);
    }
}
