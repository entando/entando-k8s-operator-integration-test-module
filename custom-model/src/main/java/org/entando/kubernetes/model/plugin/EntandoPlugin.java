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

package org.entando.kubernetes.model.plugin;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import java.util.Optional;
import org.entando.kubernetes.model.EntandoCustomResourceStatus;
import org.entando.kubernetes.model.HasIngress;
import org.entando.kubernetes.model.RequiresKeycloak;
import org.entando.kubernetes.model.app.EntandoBaseCustomResource;

@JsonSerialize
@JsonDeserialize
@JsonInclude(Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = Visibility.ANY, isGetterVisibility = Visibility.NONE, getterVisibility = Visibility.NONE,
        setterVisibility = Visibility.NONE)
public class EntandoPlugin extends EntandoBaseCustomResource implements RequiresKeycloak, HasIngress {

    public static final String CRD_NAME = "entandoplugins.entando.org";

    private EntandoPluginSpec spec;

    public EntandoPlugin() {
        this(null);
    }

    public EntandoPlugin(EntandoPluginSpec spec) {
        this(new ObjectMeta(), spec);
    }

    public EntandoPlugin(ObjectMeta metadata, EntandoPluginSpec spec) {
        this(metadata, spec, null);
    }

    public EntandoPlugin(ObjectMeta metaData, EntandoPluginSpec spec, EntandoCustomResourceStatus status) {
        super(status);
        super.setMetadata(metaData);
        this.spec = spec;
    }

    public EntandoPluginSpec getSpec() {
        return spec;
    }

    public void setSpec(EntandoPluginSpec spec) {
        this.spec = spec;
    }

    @Override
    public Optional<String> getKeycloakSecretToUse() {
        return spec.getKeycloakSecretToUse();
    }

    @Override
    public Optional<String> getIngressHostName() {
        return spec.getIngressHostName();
    }

    @Override
    public Optional<String> getTlsSecretName() {
        return spec.getTlsSecretName();
    }
}
