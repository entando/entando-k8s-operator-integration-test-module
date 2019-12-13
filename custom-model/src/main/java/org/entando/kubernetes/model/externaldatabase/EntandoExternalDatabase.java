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

package org.entando.kubernetes.model.externaldatabase;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.internal.KubernetesDeserializer;
import io.quarkus.runtime.annotations.RegisterForReflection;
import org.entando.kubernetes.model.EntandoCustomResourceStatus;
import org.entando.kubernetes.model.app.EntandoBaseCustomResource;

@JsonSerialize
@JsonDeserialize
@JsonInclude(Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = Visibility.ANY, isGetterVisibility = Visibility.NONE, getterVisibility = Visibility.NONE,
        setterVisibility = Visibility.NONE)
@RegisterForReflection
public class EntandoExternalDatabase extends EntandoBaseCustomResource {

    public static final String CRD_NAME = "entandoexternaldbs.entando.org";

    private EntandoExternalDatabaseSpec spec;

    public EntandoExternalDatabase() {
        this(null);
    }

    public EntandoExternalDatabase(EntandoExternalDatabaseSpec spec) {
        this(new ObjectMeta(), spec);
    }

    public EntandoExternalDatabase(ObjectMeta metadata, EntandoExternalDatabaseSpec spec) {
        this(metadata, spec, null);
    }

    public EntandoExternalDatabase(ObjectMeta metadata, EntandoExternalDatabaseSpec spec, EntandoCustomResourceStatus status) {
        super(status);
        setKind("EntandoExternalDB");
        KubernetesDeserializer.registerCustomKind("entando.org/v1#EntandoExternalDB", EntandoExternalDatabase.class);
        super.setMetadata(metadata);
        this.spec = spec;
    }

    @Override
    public String getDefinitionName() {
        return CRD_NAME;
    }

    public EntandoExternalDatabaseSpec getSpec() {
        return spec;
    }

    public void setSpec(EntandoExternalDatabaseSpec spec) {
        this.spec = spec;
    }

}
