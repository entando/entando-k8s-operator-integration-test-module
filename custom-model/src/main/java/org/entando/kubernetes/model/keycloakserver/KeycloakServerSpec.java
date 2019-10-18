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

package org.entando.kubernetes.model.keycloakserver;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.io.Serializable;
import java.util.Optional;
import org.entando.kubernetes.model.DbmsImageVendor;
import org.entando.kubernetes.model.HasIngress;

@JsonInclude(Include.NON_NULL)
@JsonSerialize
@JsonDeserialize
@JsonAutoDetect(fieldVisibility = Visibility.ANY, isGetterVisibility = Visibility.NONE, getterVisibility = Visibility.NONE,
        setterVisibility = Visibility.NONE)
public class KeycloakServerSpec implements HasIngress, Serializable {

    private String imageName;
    private DbmsImageVendor dbms;
    private String ingressHostName;
    private String entandoImageVersion;
    private String tlsSecretName;
    private Integer replicas = 1;
    @SuppressWarnings("PMD.AvoidFieldNameMatchingMethodName")
    //because 'default' is a reserved word
    private boolean isDefault;

    public KeycloakServerSpec() {
        super();
    }

    public KeycloakServerSpec(String imageName, DbmsImageVendor dbms, String ingressHostName,
            String entandoImageVersion, String tlsSecretName) {
        this();
        this.imageName = imageName;
        this.dbms = dbms;
        this.ingressHostName = ingressHostName;
        this.entandoImageVersion = entandoImageVersion;
        this.tlsSecretName = tlsSecretName;
    }

    public KeycloakServerSpec(String imageName, DbmsImageVendor dbms, String ingressHostName, String entandoImageVersion,
            String tlsSecretName, int replicas, boolean isDefault) {
        this(imageName, dbms, ingressHostName, entandoImageVersion, tlsSecretName);
        this.replicas = replicas;
        this.isDefault = isDefault;
    }

    public Optional<String> getImageName() {
        return Optional.ofNullable(imageName);
    }

    public Optional<DbmsImageVendor> getDbms() {
        return Optional.ofNullable(dbms);
    }

    @Override
    public Optional<String> getIngressHostName() {
        return Optional.ofNullable(ingressHostName);
    }

    @Override
    public Optional<String> getTlsSecretName() {
        return Optional.ofNullable(tlsSecretName);
    }

    public Optional<String> getEntandoImageVersion() {
        return Optional.ofNullable(entandoImageVersion);
    }

    public Integer getReplicas() {
        return replicas;
    }

    public void setReplicas(Integer replicas) {
        this.replicas = replicas;
    }

    public boolean isDefault() {
        return isDefault;
    }
}
