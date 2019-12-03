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

package org.entando.kubernetes.model.app;

import static java.util.Optional.ofNullable;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.util.Optional;
import org.entando.kubernetes.model.DbmsImageVendor;
import org.entando.kubernetes.model.EntandoDeploymentSpec;
import org.entando.kubernetes.model.JeeServer;
import org.entando.kubernetes.model.RequiresKeycloak;

@JsonSerialize
@JsonDeserialize()
@JsonInclude(Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = Visibility.ANY, isGetterVisibility = Visibility.NONE, getterVisibility = Visibility.NONE,
        setterVisibility = Visibility.NONE)
@RegisterForReflection
public class EntandoAppSpec extends EntandoDeploymentSpec implements RequiresKeycloak {

    private JeeServer standardServerImage;
    private String customServerImage;
    private String entandoImageVersion;
    private String keycloakSecretToUse;
    private String clusterInfrastructureToUse;

    public EntandoAppSpec() {
        super();
    }

    /**
     * Only for use from the builder.
     */
    @JsonCreator
    public EntandoAppSpec(@JsonProperty("standardServerImage") JeeServer standardServerImage,
            @JsonProperty("customServerImage") String customServerImage,
            @JsonProperty("dbms") DbmsImageVendor dbms,
            @JsonProperty("ingressHostName") String ingressHostName,
            @JsonProperty("replicas") int replicas,
            @JsonProperty("entandoImageVersion") String entandoImageVersion,
            @JsonProperty("tlsSecretName") String tlsSecretName,
            @JsonProperty("keycloakSecretToUse") String keycloakSecretToUse,
            @JsonProperty("clusterInfrastructureToUse") String clusterInfrastructureToUse) {
        super(ingressHostName, tlsSecretName, replicas, dbms);
        this.standardServerImage = standardServerImage;
        this.customServerImage = customServerImage;
        this.entandoImageVersion = entandoImageVersion;
        this.keycloakSecretToUse = keycloakSecretToUse;
        this.clusterInfrastructureToUse = clusterInfrastructureToUse;
    }

    @Override
    public Optional<String> getKeycloakSecretToUse() {
        return ofNullable(keycloakSecretToUse);
    }

    public Optional<String> getClusterInfrastructureTouse() {
        return ofNullable(clusterInfrastructureToUse);
    }

    public Optional<JeeServer> getStandardServerImage() {
        return ofNullable(standardServerImage);
    }

    public Optional<String> getCustomServerImage() {
        return ofNullable(customServerImage);
    }

    public Optional<String> getEntandoImageVersion() {
        return ofNullable(entandoImageVersion);
    }

}
