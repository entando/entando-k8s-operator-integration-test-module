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

@JsonSerialize
@JsonDeserialize
@JsonInclude(Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = Visibility.ANY, isGetterVisibility = Visibility.NONE, getterVisibility = Visibility.NONE,
        setterVisibility = Visibility.NONE)
public class EntandoApp extends EntandoBaseCustomResource implements HasIngress, RequiresKeycloak {

    public static final String CRD_NAME = "entandoapps.entando.org";

    private EntandoAppSpec spec;

    public EntandoApp() {
        this(null);
    }

    public EntandoApp(EntandoAppSpec spec) {
        this(new ObjectMeta(), spec);
    }

    public EntandoApp(ObjectMeta metadata, EntandoAppSpec spec) {
        this(metadata, spec, null);
    }

    public EntandoApp(ObjectMeta metadata, EntandoAppSpec spec, EntandoCustomResourceStatus status) {
        super(status);
        super.setMetadata(metadata);
        this.setSpec(spec);
    }

    @Override
    public String getDefinitionName() {
        return CRD_NAME;
    }

    public EntandoAppSpec getSpec() {
        return spec;
    }

    public void setSpec(EntandoAppSpec spec) {
        this.spec = spec;
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
        return getSpec().getKeycloakSecretToUse();
    }
}
