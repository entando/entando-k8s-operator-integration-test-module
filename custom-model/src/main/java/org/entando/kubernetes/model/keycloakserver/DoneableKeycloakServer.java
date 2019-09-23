package org.entando.kubernetes.model.keycloakserver;

import io.fabric8.kubernetes.api.builder.Function;
import java.util.Optional;
import org.entando.kubernetes.model.AbstractServerStatus;
import org.entando.kubernetes.model.DbServerStatus;
import org.entando.kubernetes.model.DoneableEntandoCustomResource;
import org.entando.kubernetes.model.EntandoCustomResourceStatus;
import org.entando.kubernetes.model.EntandoDeploymentPhase;
import org.entando.kubernetes.model.WebServerStatus;

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
        if (status instanceof DbServerStatus) {
            this.status.addDbServerStatus((DbServerStatus) status);
        } else {
            this.status.addWebServerStatus((WebServerStatus) status);
        }
        return this;
    }

    @Override
    public DoneableKeycloakServer withPhase(EntandoDeploymentPhase phase) {
        status.setEntandoDeploymentPhase(phase);
        return this;
    }

    @Override
    public KeycloakServer done() {
        return function.apply(new KeycloakServer(spec.build(), metadata.build(), status));
    }
}
