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
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.client.CustomResource;
import java.util.Optional;
import org.entando.kubernetes.model.EntandoCustomResource;
import org.entando.kubernetes.model.EntandoCustomResourceStatus;
import org.entando.kubernetes.model.HasIngress;
import org.entando.kubernetes.model.RequiresKeycloak;

@JsonSerialize
@JsonDeserialize
@JsonInclude(Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = Visibility.ANY, isGetterVisibility = Visibility.NONE, getterVisibility = Visibility.NONE,
        setterVisibility = Visibility.NONE)
public class EntandoClusterInfrastructure extends CustomResource implements HasIngress, EntandoCustomResource, RequiresKeycloak {

    public static final String CRD_NAME = "entandoclusterinfrastructures.entando.org";

    private EntandoClusterInfrastructureSpec spec;
    private EntandoCustomResourceStatus entandoStatus;

    public EntandoClusterInfrastructure() {
        super();
        setApiVersion("entando.org/v1alpha1");
    }

    public EntandoClusterInfrastructure(EntandoClusterInfrastructureSpec spec) {
        this();
        this.spec = spec;
    }

    public EntandoClusterInfrastructure(ObjectMeta metadata, EntandoClusterInfrastructureSpec spec,
            EntandoCustomResourceStatus status) {
        this(metadata, spec);
        this.entandoStatus = status;
    }

    public EntandoClusterInfrastructure(ObjectMeta metadata, EntandoClusterInfrastructureSpec spec) {
        this(spec);
        super.setMetadata(metadata);
    }

    public EntandoClusterInfrastructureSpec getSpec() {
        return spec;
    }

    public void setSpec(EntandoClusterInfrastructureSpec spec) {
        this.spec = spec;
    }

    @Override
    public EntandoCustomResourceStatus getStatus() {
        if (this.entandoStatus == null) {
            this.entandoStatus = new EntandoCustomResourceStatus();
        }

        return this.entandoStatus;
    }

    @Override
    public void setStatus(EntandoCustomResourceStatus status) {
        this.entandoStatus = status;
    }

    @Override
    public Optional<String> getIngressHostName() {
        return getSpec().getIngressHostName();
    }

    @Override
    public Optional<String> getTlsSecretName() {
        return getSpec().getTlsSecretName();
    }

    @Override
    public Optional<String> getKeycloakSecretToUse() {
        return spec.getKeycloakSecretToUse();
    }
}
