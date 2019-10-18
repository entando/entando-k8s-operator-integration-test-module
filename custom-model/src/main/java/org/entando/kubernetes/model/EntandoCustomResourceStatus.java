/*
 *
 * Copyright 2015-Present Entando Inc. (http://www.entando.com) All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 *  This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 */

package org.entando.kubernetes.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.io.Serializable;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@JsonSerialize
@JsonDeserialize
@JsonInclude(Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = Visibility.ANY, isGetterVisibility = Visibility.NONE, getterVisibility = Visibility.NONE,
        setterVisibility = Visibility.NONE)
public class EntandoCustomResourceStatus implements Serializable {

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
