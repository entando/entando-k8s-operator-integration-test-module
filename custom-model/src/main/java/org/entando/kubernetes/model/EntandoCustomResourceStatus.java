package org.entando.kubernetes.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@JsonSerialize
@JsonDeserialize
@JsonInclude(Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = Visibility.ANY, isGetterVisibility = Visibility.NONE, getterVisibility = Visibility.NONE,
        setterVisibility = Visibility.NONE)
public class EntandoCustomResourceStatus {

    private final Map<String, AbstractServerStatus> serverStatuses = new ConcurrentHashMap<>();

    private EntandoDeploymentPhase entandoDeploymentPhase;

    public EntandoCustomResourceStatus() {
        entandoDeploymentPhase = EntandoDeploymentPhase.REQUESTED;
    }

    public EntandoDeploymentPhase getEntandoDeploymentPhase() {
        return entandoDeploymentPhase;
    }

    public void setEntandoDeploymentPhase(EntandoDeploymentPhase entandoDeploymentPhase) {
        this.entandoDeploymentPhase = entandoDeploymentPhase;
    }

    public boolean hasFailed() {
        return serverStatuses.values().stream().anyMatch(AbstractServerStatus::hasFailed);
    }

    public void putServerStatus(AbstractServerStatus status) {
        serverStatuses.put(status.getQualifier(), status);
    }

    public Optional<DbServerStatus> forDbQualifiedBy(String qualifier) {
        return Optional.ofNullable((DbServerStatus) serverStatuses.get(qualifier));
    }

    public Optional<WebServerStatus> forServerQualifiedBy(String qualifier) {
        return Optional.ofNullable((WebServerStatus) serverStatuses.get(qualifier));
    }

    public EntandoDeploymentPhase calculateFinalPhase() {
        return hasFailed() ? EntandoDeploymentPhase.FAILED : EntandoDeploymentPhase.SUCCESSFUL;
    }

}
