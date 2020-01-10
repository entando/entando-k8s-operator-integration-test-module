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

package org.entando.kubernetes.model.compositeapp;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.quarkus.runtime.annotations.RegisterForReflection;
import org.entando.kubernetes.model.EntandoBaseCustomResource;
import org.entando.kubernetes.model.EntandoCustomResourceStatus;

@JsonSerialize
@JsonDeserialize
@JsonInclude(Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = Visibility.ANY, isGetterVisibility = Visibility.NONE, getterVisibility = Visibility.NONE,
        setterVisibility = Visibility.NONE)
@RegisterForReflection
@JsonIgnoreProperties(ignoreUnknown = true)
public class EntandoCompositeApp extends EntandoBaseCustomResource {

    public static final String CRD_NAME = "entandocompositeapps.entando.org";

    private EntandoCompositeAppSpec spec;

    public EntandoCompositeApp() {
        this(null);
    }

    public EntandoCompositeApp(EntandoCompositeAppSpec spec) {
        this(new ObjectMeta(), spec);
    }

    public EntandoCompositeApp(ObjectMeta metadata, EntandoCompositeAppSpec spec) {
        this(metadata, spec, null);
    }

    public EntandoCompositeApp(ObjectMeta metadata, EntandoCompositeAppSpec spec, EntandoCustomResourceStatus status) {
        super(status);
        super.setMetadata(metadata);
        this.setSpec(spec);
    }

    @Override
    public String getDefinitionName() {
        return CRD_NAME;
    }

    public EntandoCompositeAppSpec getSpec() {
        return spec;
    }

    public void setSpec(EntandoCompositeAppSpec spec) {
        this.spec = spec;
    }

}
