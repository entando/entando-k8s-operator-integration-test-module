package org.entando.kubernetes.model.app;

import io.fabric8.kubernetes.api.builder.Function;
import java.util.Optional;
import org.entando.kubernetes.model.AbstractServerStatus;
import org.entando.kubernetes.model.DbServerStatus;
import org.entando.kubernetes.model.DoneableEntandoCustomResource;
import org.entando.kubernetes.model.EntandoCustomResourceStatus;
import org.entando.kubernetes.model.EntandoDeploymentPhase;
import org.entando.kubernetes.model.WebServerStatus;

public class DoneableEntandoApp extends EntandoAppFluent<DoneableEntandoApp> implements
        DoneableEntandoCustomResource<DoneableEntandoApp, EntandoApp> {

    private final EntandoCustomResourceStatus status;
    private final Function<EntandoApp, EntandoApp> function;

    public DoneableEntandoApp(EntandoApp resource, Function<EntandoApp, EntandoApp> function) {
        super(resource.getSpec(), resource.getMetadata());
        this.status = Optional.ofNullable(resource.getStatus()).orElse(new EntandoCustomResourceStatus());
        this.function = function;
    }

    @Override
    public DoneableEntandoApp withStatus(AbstractServerStatus serverStatus) {
        if (serverStatus instanceof DbServerStatus) {
            this.status.addDbServerStatus((DbServerStatus) serverStatus);
        } else {
            this.status.addWebServerStatus((WebServerStatus) serverStatus);
        }
        return this;
    }

    @Override
    public DoneableEntandoApp withPhase(EntandoDeploymentPhase phase) {
        status.setEntandoDeploymentPhase(phase);
        return this;
    }

    @Override
    public EntandoApp done() {
        return function.apply(new EntandoApp(metadata.build(), spec.build(), status));
    }
}
