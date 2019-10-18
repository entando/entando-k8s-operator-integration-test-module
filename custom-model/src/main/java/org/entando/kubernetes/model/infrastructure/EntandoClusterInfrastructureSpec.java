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

package org.entando.kubernetes.model.infrastructure;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.Optional;
import org.entando.kubernetes.model.DbmsImageVendor;
import org.entando.kubernetes.model.EntandoDeploymentSpec;
import org.entando.kubernetes.model.RequiresKeycloak;

@JsonInclude(Include.NON_NULL)
@JsonSerialize
@JsonDeserialize
@JsonAutoDetect(fieldVisibility = Visibility.ANY, isGetterVisibility = Visibility.NONE, getterVisibility = Visibility.NONE,
        setterVisibility = Visibility.NONE)
public class EntandoClusterInfrastructureSpec extends EntandoDeploymentSpec implements RequiresKeycloak {

    private String entandoImageVersion;
    private String keycloakSecretToUse;
    @SuppressWarnings("PMD.AvoidFieldNameMatchingMethodName")
    //because 'default' is a reserved word
    private boolean isDefault;

    public EntandoClusterInfrastructureSpec() {
        super();
    }

    public EntandoClusterInfrastructureSpec(DbmsImageVendor dbms, String ingressHostName, String entandoImageVersion,
            String tlsSecretName, Integer replicas, String keycloakSecretToUse, boolean isDefault) {
        super(ingressHostName, tlsSecretName, replicas, dbms);
        this.entandoImageVersion = entandoImageVersion;
        this.keycloakSecretToUse = keycloakSecretToUse;
        this.isDefault = isDefault;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public Optional<String> getEntandoImageVersion() {
        return Optional.ofNullable(entandoImageVersion);
    }

    @Override
    public Optional<String> getKeycloakSecretToUse() {
        return Optional.ofNullable(keycloakSecretToUse);
    }
}
