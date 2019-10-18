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
import io.fabric8.kubernetes.api.model.ObjectMeta;
import java.util.Optional;
import org.entando.kubernetes.model.EntandoCustomResourceStatus;
import org.entando.kubernetes.model.HasIngress;
import org.entando.kubernetes.model.app.EntandoBaseCustomResource;

@JsonSerialize
@JsonDeserialize
@JsonInclude(Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = Visibility.ANY, isGetterVisibility = Visibility.NONE, getterVisibility = Visibility.NONE,
        setterVisibility = Visibility.NONE)
public class KeycloakServer extends EntandoBaseCustomResource implements HasIngress {

    public static final String CRD_NAME = "entandokeycloakservers.entando.org";

    private KeycloakServerSpec spec;

    public KeycloakServer() {
        this(null);
    }

    public KeycloakServer(KeycloakServerSpec spec) {
        this(new ObjectMeta(), spec);
    }

    public KeycloakServer(ObjectMeta metadata, KeycloakServerSpec spec) {
        this(metadata, spec, null);
    }

    public KeycloakServer(ObjectMeta metadata, KeycloakServerSpec spec, EntandoCustomResourceStatus status) {
        super(status);
        setKind("EntandoKeycloakServer");
        this.spec = spec;
        super.setMetadata(metadata);
    }

    public KeycloakServerSpec getSpec() {
        return spec;
    }

    public void setSpec(KeycloakServerSpec spec) {
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
}
