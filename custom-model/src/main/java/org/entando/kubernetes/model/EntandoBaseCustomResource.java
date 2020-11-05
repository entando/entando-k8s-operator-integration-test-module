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
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.client.CustomResource;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.io.Serializable;
import org.entando.kubernetes.model.app.EntandoApp;
import org.entando.kubernetes.model.compositeapp.EntandoCustomResourceReference;
import org.entando.kubernetes.model.externaldatabase.EntandoDatabaseService;
import org.entando.kubernetes.model.infrastructure.EntandoClusterInfrastructure;
import org.entando.kubernetes.model.keycloakserver.EntandoKeycloakServer;
import org.entando.kubernetes.model.link.EntandoAppPluginLink;
import org.entando.kubernetes.model.plugin.EntandoPlugin;

@JsonSerialize
@JsonDeserialize
@JsonInclude(Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = Visibility.ANY, isGetterVisibility = Visibility.NONE, getterVisibility = Visibility.NONE,
        setterVisibility = Visibility.NONE)
@RegisterForReflection
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(
        use = Id.NAME,
        include = As.EXISTING_PROPERTY,
        property = "kind"

)
@JsonSubTypes({
        @Type(value = EntandoKeycloakServer.class, name = "EntandoKeycloakServer"),
        @Type(value = EntandoClusterInfrastructure.class, name = "EntandoClusterInfrastructure"),
        @Type(value = EntandoApp.class, name = "EntandoApp"),
        @Type(value = EntandoPlugin.class, name = "EntandoPlugin"),
        @Type(value = EntandoAppPluginLink.class, name = "EntandoAppPluginLink"),
        @Type(value = EntandoDatabaseService.class, name = "EntandoDatabaseService"),
        @Type(value = EntandoCustomResourceReference.class, name = "EntandoCustomResourceReference")
})

public abstract class EntandoBaseCustomResource<S extends Serializable> extends CustomResource implements EntandoCustomResource {

    private S spec;
    private EntandoCustomResourceStatus entandoStatus;

    protected EntandoBaseCustomResource() {
        super();
    }

    protected EntandoBaseCustomResource(ObjectMeta objectMeta, S spec, EntandoCustomResourceStatus entandoStatus) {
        super();
        super.setMetadata(objectMeta);
        this.spec = spec;
        this.entandoStatus = entandoStatus;
    }

    public S getSpec() {
        return spec;
    }

    public void setSpec(S spec) {
        this.spec = spec;
    }

    @Override
    public EntandoCustomResourceStatus getStatus() {
        if (entandoStatus == null) {
            setStatus(new EntandoCustomResourceStatus());
        }
        return this.entandoStatus;
    }

    @Override
    public void setStatus(EntandoCustomResourceStatus status) {
        this.entandoStatus = status;
    }
}
