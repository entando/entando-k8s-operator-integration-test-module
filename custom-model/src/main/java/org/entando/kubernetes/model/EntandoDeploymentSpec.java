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

import static java.util.Optional.ofNullable;
import static org.entando.kubernetes.model.Coalescence.coalesce;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.io.Serializable;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@JsonInclude(Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = Visibility.ANY, isGetterVisibility = Visibility.NONE, getterVisibility = Visibility.NONE,
        setterVisibility = Visibility.NONE)
@RegisterForReflection
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class EntandoDeploymentSpec implements HasIngress, Serializable {

    private Integer replicas = 1;
    private DbmsVendor dbms;
    private String ingressHostName;
    private String tlsSecretName;
    private String serviceAccountToUse;
    private Map<String, String> parameters = new ConcurrentHashMap<>();
    private EntandoResourceRequirements resourceRequirements;
    protected EntandoDeploymentSpec() {
    }

    @SuppressWarnings("unchecked")
    protected EntandoDeploymentSpec(String ingressHostName, String tlsSecretName, Integer replicas, DbmsVendor dbms,
            String serviceAccountToUse, Map<String, String> parameters,  EntandoResourceRequirements resourceRequirements) {
        this.ingressHostName = ingressHostName;
        this.tlsSecretName = tlsSecretName;
        this.replicas = replicas;
        this.dbms = dbms;
        this.serviceAccountToUse = serviceAccountToUse;
        this.parameters = coalesce(parameters, this.parameters);
        this.resourceRequirements = resourceRequirements;
    }

    @Override
    public Optional<String> getIngressHostName() {
        return Optional.ofNullable(ingressHostName);
    }

    @Override
    public Optional<String> getTlsSecretName() {
        return Optional.ofNullable(tlsSecretName);
    }

    public Optional<Integer> getReplicas() {
        return ofNullable(replicas);
    }

    public Optional<DbmsVendor> getDbms() {
        return ofNullable(dbms);
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public Optional<String> getServiceAccountToUse() {
        return ofNullable(serviceAccountToUse);
    }
    public Optional<EntandoResourceRequirements> getResourceRequirements(){
        return Optional.ofNullable(resourceRequirements);
    }
}
