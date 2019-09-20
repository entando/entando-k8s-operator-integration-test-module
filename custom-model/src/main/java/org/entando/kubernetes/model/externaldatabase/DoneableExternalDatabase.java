package org.entando.kubernetes.model.externaldatabase;

import io.fabric8.kubernetes.api.builder.Function;
import io.fabric8.kubernetes.client.CustomResourceDoneable;
import org.entando.kubernetes.model.AbstractServerStatus;
import org.entando.kubernetes.model.DbServerStatus;
import org.entando.kubernetes.model.DoneableEntandoCustomResource;
import org.entando.kubernetes.model.EntandoDeploymentPhase;
import org.entando.kubernetes.model.WebServerStatus;

public class DoneableExternalDatabase extends CustomResourceDoneable<ExternalDatabase> implements
        DoneableEntandoCustomResource<DoneableExternalDatabase, ExternalDatabase> {

    private final ExternalDatabase resource;

    public DoneableExternalDatabase(ExternalDatabase resource, Function function) {
        super(resource, function);
        this.resource = resource;
    }

    @Override
    public DoneableExternalDatabase withStatus(AbstractServerStatus status) {
        if (status instanceof DbServerStatus) {
            this.resource.getStatus().addDbServerStatus((DbServerStatus) status);
        } else {
            this.resource.getStatus().addJeeServerStatus((WebServerStatus) status);
        }
        return this;
    }

    @Override
    public DoneableExternalDatabase withPhase(EntandoDeploymentPhase phase) {
        resource.getStatus().setEntandoDeploymentPhase(phase);
        return this;
    }
}
