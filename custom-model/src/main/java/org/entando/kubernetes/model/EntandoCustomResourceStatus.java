package org.entando.kubernetes.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@JsonSerialize
@JsonDeserialize
public class EntandoCustomResourceStatus {

    @JsonProperty
    private List<DbServerStatus> dbServerStatus = new ArrayList<>();
    @JsonProperty
    private List<WebServerStatus> webServerStatuses = new ArrayList<>();
    @JsonProperty(defaultValue = "requested")
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
        return dbServerStatus.stream().anyMatch(s -> s.hasFailed()) || webServerStatuses.stream()
                .anyMatch(s -> s.hasFailed());
    }

    public void addJeeServerStatus(WebServerStatus status) {
        webServerStatuses.add(status);
    }

    public void addDbServerStatus(DbServerStatus status) {
        dbServerStatus.add(status);
    }

    public List<DbServerStatus> getDbServerStatus() {
        return dbServerStatus;
    }

    public void setDbServerStatus(List<DbServerStatus> dbServerStatus) {
        this.dbServerStatus = dbServerStatus;
    }

    public List<WebServerStatus> getWebServerStatuses() {
        return webServerStatuses;
    }

    public void setWebServerStatuses(
            List<WebServerStatus> webServerStatuses) {
        this.webServerStatuses = webServerStatuses;
    }

    public Optional<DbServerStatus> forDbQualifiedBy(String qualifier) {
        return getDbServerStatus().stream().filter(s -> s.getQualifier().equals(qualifier)).findFirst();
    }

    public Optional<WebServerStatus> forServerQualifiedBy(String qualifier) {
        return getWebServerStatuses().stream().filter(s -> s.getQualifier().equals(qualifier)).findFirst();
    }

    public EntandoDeploymentPhase calculateFinalPhase() {
        return hasFailed() ? EntandoDeploymentPhase.FAILED : EntandoDeploymentPhase.SUCCESSFUL;
    }

}
